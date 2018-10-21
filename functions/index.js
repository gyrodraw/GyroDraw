// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');

const maxPlayers = 5;
const maxWords = 6;
var StateEnum = Object.freeze({"votingPage":1, "endVotingPage":2})

admin.initializeApp();

exports.connectedUsers = functions.database.ref('/mockRooms/ABCDE/connectedUsers').onWrite((event) => {
    return admin.database().ref('/mockRooms/ABCDE/connectedUsers').once("value")
    .then(snapshot => {
      checkUsersReady(StateEnum.endVotingPage, 'mockRooms/ABCDE/timer/usersEndVoting', snapshot);
      checkUsersReady(StateEnum.votingPage, 'mockRooms/ABCDE/timer/startTimer', snapshot);
      return;
    });
});

function checkUsersReady(state, path, snapshot) {
  let ready = true;
  snapshot.forEach( (child) => {
    if(child.val() !== state) {
      ready = false;
    }
  });

  if(ready) {
    admin.database().ref(path).set(1);
    if(state === StateEnum.endVotingPage) {
      admin.database().ref('mockRooms/ABCDE/timer/endTime').set(0);
    }
    console.log("Ready");
  } else {
    admin.database().ref(path).set(0);
    console.log("Not ready");
  }
}

exports.startTimer = functions.database.ref('/mockRooms/ABCDE/timer/startTimer').onWrite((event) => {
  return admin.database().ref('/mockRooms/ABCDE/timer/startTimer').once("value")
  .then(snapshot => {
    if(snapshot.val() === 1) {
      console.log("Timer started");

      // Wait in seconds
      return functionTimer(20,
            elapsedTime => {
                admin.database().ref('/mockRooms/ABCDE/timer/observableTime').set(elapsedTime);
            })
            .then(totalTime => {
                return console.log('Timer of ' + totalTime + ' has finished.');
            })
            .then(() => new Promise(resolve => setTimeout(resolve, 1000)))
            .then(() => admin.database().ref('/mockRooms/ABCDE/timer/endTime').set(1))
            .then(() => event.data.ref.remove())
            .catch(error => console.error(error));
    }
    return;
  })
});

function functionTimer (seconds, call) {
    return new Promise((resolve, reject) => {
        if (seconds > 300) {
            return;
        }
        let interval = setInterval(onInterval, 1000);
        let elapsedSeconds = 0;

        function onInterval () {
            if (elapsedSeconds >= seconds) {
                clearInterval(interval);
                call(0);
                resolve(elapsedSeconds);
                return;
            }
            call(seconds - elapsedSeconds);
            elapsedSeconds++;
        }
    });
}

exports.joinGame2 = functions.https.onCall((data, context) => {
  console.log("Started method");
  // Grab the text parameter.
  const username = data.username;
  let _roomID;
  console.log(username);
  var alreadyJoined = false;

  return admin.database().ref('realRooms').once('value', (snapshot) => {
    return snapshot.forEach((roomID) => {
        console.log(roomID.child("users").numChildren());

        // Check if the room is full or if the user already joined a room
        if(roomID.child("users").numChildren() < 5 && !alreadyJoined) {
          const userCount = "user" +  (roomID.child("users").numChildren() + 1).toString();
          const path = "realRooms/" + roomID.key;
          _roomID = roomID.key;
          if(roomID.hasChild("users")) {
            admin.database().ref(path).child("users").update({[username]:userCount});
          } else {
            admin.database().ref(path).update({"users":{[username]:userCount}});
          }
          alreadyJoined = true;
        }
    });
  }).then(() => {
    return _roomID;
  });
});

function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function generateTwoRandomNumbers() {
  const firstNumber = getRandomInt(0, maxWords);
  let secondNumber = firstNumber;

  while(firstNumber === secondNumber) {
    secondNumber = getRandomInt(0, maxWords);
  }

  return [firstNumber, secondNumber];
}

function addWordsToDatabase(roomID) {
  const numbers = generateTwoRandomNumbers();
  return admin.database().ref("words").once('value', (snapshot) => {
    const word1 = snapshot.child(numbers[0]).val();
    const word2 = snapshot.child(numbers[1]).val();
    admin.database().ref("realRooms/" + roomID).update({"words": {[word1]:0,[word2]:0}});
  });

}

exports.chooseWordsGeneration = functions.database.ref("realRooms/{roomID}").onWrite((change, context) => {
  const roomID = change.before.key;
  return admin.database().ref("realRooms/" + roomID).once('value', (snapshot) => {
    if(snapshot.hasChild("words") && !snapshot.hasChild("users")) {
      // Remove the words because the room is empty
      admin.database().ref("realRooms/" + roomID + "/words").remove();
    }
    else if(snapshot.hasChild("users") && !snapshot.hasChild("words")) {
      // Generate the words
      addWordsToDatabase(roomID);
    }
    return;
  });
});
