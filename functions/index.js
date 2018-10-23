// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');

const maxPlayers = 5;
const maxWords = 6;
const WAITING_TIME = 10;
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

function checkUsersReady2(state, path, snapshot) {
  let ready = true;
  snapshot.forEach((child) => {
    if(child.val() !== state) {
      ready = false;
    }
  });

  if(ready && snapshot.numChildren() >= 2) {
    admin.database().ref(path + "/state").set(state + 1);
    /*admin.database().ref(path).child("users").on('value', (snapUsers) => {
      return snapUsers.forEach((child) => {
        child.ref.set(state + 1);
      });
    });*/
    console.log("Ready");
  } else {
    console.log("Not ready");
  }
  return;
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

exports.joinGame = functions.https.onRequest((req, res) => {
  // Grab the text parameter.
  const original = req.query.text;
  // Grab the id of the player
  // Check if room is already available and join available room
  return admin.database().ref('rooms').once("value").then(x => {
    var rooms = x.val();
    console.log(rooms);
    for (var room in rooms) {
      try{
        if(typeof rooms[room].users !== 'undefined') {
          console.log(Object.keys(rooms[room].users).length);
          if (Object.keys(rooms[room].users).length < 5 && rooms[room].playing === false) {
            if (Object.keys(rooms[room].users).length ===  4) {
              // set playing to true
              admin.database().ref('rooms').child(room).child('playing').set(true)
            }
            return  admin.database().ref('rooms').child(room).child('users').push().set({
              name: "fredrik"
            }).then(() => {
              return res.status(200).end();
            });
          }
        }
      }catch(e){
        console.log('rooms[room].users is undefined');
      }
    }
    return  admin.database().ref('rooms').push().set({
      playing: false,
      users: { "42343243" : { name : "fredrik"} }
    }).then( () => {
      return res.status(200).end();
    });
  });
});

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
            admin.database().ref(path).child("users").update({[username]:0});
          } else {
            admin.database().ref(path).update({"users":{[username]:0}});
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

exports.chooseWordsGeneration = functions.database.ref("realRooms/{roomID}/users").onWrite((change, context) => {
  const roomID = context.params.roomID;
  return admin.database().ref("realRooms/" + roomID).once('value', (snapshot) => {
    if(snapshot.child("users").numChildren() === 1) {
      admin.database().ref("realRooms/" + roomID + "/state").set(0);
    }

    if(snapshot.hasChild("words") && !snapshot.hasChild("users")) {
      // Remove the words because the room is empty
      admin.database().ref("realRooms/" + roomID + "/words").remove();
    }
    else if(snapshot.hasChild("users") && !snapshot.hasChild("words")) {
      // Generate the words
      addWordsToDatabase(roomID);
    }

    return checkUsersReady2(0, "realRooms/" + roomID, snapshot.child("users"));
  });
});

exports.onStateUpdate = functions.database.ref("realRooms/{roomID}/state").onWrite((change, context) => {
  const roomID = context.params.roomID;
  const state = change.after.val();
  switch(state) {
    case 0:
        break;
    case 1:
      return functionTimer(WAITING_TIME, elapsedTime => {
              return admin.database().ref("realRooms/" + roomID + "/timer/observableTime").set(elapsedTime);
          })
          .then(totalTime => {
              return console.log('Timer of ' + totalTime + ' has finished.');
          })
          .then(() => new Promise(resolve => setTimeout(resolve, 1000)))
          .then(() => admin.database().ref("realRooms/" + roomID + "/timer/endTime").set(1))
          .then(() => admin.database().ref("realRooms/" + roomID + "/state").set(2))
          //.then(() => event.data.ref.remove())
          .catch(error => console.error(error));
    case 2:
      return functionTimer(15, elapsedTime => {
              return admin.database().ref("realRooms/" + roomID + "/timer/observableTime").set(elapsedTime);
          })
          .then(totalTime => {
              return console.log('Timer of ' + totalTime + ' has finished.');
          })
          .then(() => new Promise(resolve => setTimeout(resolve, 1000)))
          .then(() => admin.database().ref("realRooms/" + roomID + "/timer/endTime").set(1))
          .then(() => admin.database().ref("realRooms/" + roomID + "/state").set(3))
          //.then(() => event.data.ref.remove())
          .catch(error => console.error(error));
    case 3:
      return functionTimer(15, elapsedTime => {
              return admin.database().ref("realRooms/" + roomID + "/timer/observableTime").set(elapsedTime);
          })
          .then(totalTime => {
              return console.log('Timer of ' + totalTime + ' has finished.');
          })
          .then(() => new Promise(resolve => setTimeout(resolve, 1000)))
          .then(() => admin.database().ref("realRooms/" + roomID + "/timer/endTime").set(1))
          .then(() => admin.database().ref("realRooms/" + roomID + "/state").set(4))
          //.then(() => event.data.ref.remove())
          .catch(error => console.error(error));
    default:
      break;
  }
  return 0;
});
