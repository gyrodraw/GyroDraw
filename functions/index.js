// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');

const maxPlayers = 5;
const mockMaxPlayers = 3;
const maxWords = 6;
const WAITING_TIME_CHOOSE_WORDS = 10;
const WAITING_TIME_DRAWING = 10;
const WAITING_TIME_VOTING = 10;
const parentRoomID = "realRooms/";
var StateEnumMock = Object.freeze({"votingPage":1, "endVotingPage":2});
var StateEnum = Object.freeze({"Idle": 0, "ChoosingWordsCountdown":1, "DrawingPage": 2, "VotingPage": 3, "EndVoting" : 4});
var PlayingEnum = Object.freeze({"Idle": 0, "PlayingButJoinable": 1, "Playing": 2});
var state = 0;

admin.initializeApp();

// Mock to be removed soon
exports.connectedUsers = functions.database.ref('/mockRooms/ABCDE/connectedUsers').onWrite((event) => {
    return admin.database().ref('/mockRooms/ABCDE/connectedUsers').once("value")
    .then(snapshot => {
      checkUsersReady(StateEnumMock.endVotingPage, 'mockRooms/ABCDE/timer/usersEndVoting', snapshot);
      checkUsersReady(StateEnumMock.votingPage, 'mockRooms/ABCDE/timer/startTimer', snapshot);
      return;
    });
});

// Mock to be removed soon
function checkUsersReady(state, path, snapshot) {
  let ready = true;
  snapshot.forEach( (child) => {
    if(child.val() !== state) {
      ready = false;
    }
  });

  if(ready) {
    admin.database().ref(path).set(1);
    if(state === StateEnumMock.endVotingPage) {
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
  snapshot.child("users").forEach((child) => {
    if(child.val() !== state) {
      ready = false;
    }
  });

  if(ready && snapshot.child("users").numChildren() >= mockMaxPlayers) {
    if(snapshot.child("state").val() === StateEnum.Idle || 
      snapshot.child("state").val() === StateEnum.EndVoting) {
      admin.database().ref(path + "/state").set(StateEnum.ChoosingWordsCountdown);
    }
    console.log("Ready");
  } else {
    console.log("Not ready");
  }
  return;
}

// Mock to be removed soon
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

function functionTimer (seconds, state, roomID, call) {
  return new Promise((resolve, reject) => {
    if (seconds > 300) {
        return;
    }

    let interval = setInterval(onInterval, 1000);
    let elapsedSeconds = 0;
    let stop = false;

    function onInterval () {

      admin.database().ref(parentRoomID + roomID).once('value', (snapshot) => {
        if(snapshot.child("users").numChildren() < mockMaxPlayers && state === StateEnum.ChoosingWordsCountdown) {
          stop = true;
          clearInterval(interval);
          call(0);
          admin.database().ref(parentRoomID + roomID + "/state").set(0);
          admin.database().ref(parentRoomID + roomID + "/playing").set(PlayingEnum.Idle);
        }
      });

      if(stop === true){
        console.log("Timer should be stopped");
        stop = false;
        throw new "Timer stopped";
      }

      if (elapsedSeconds >= seconds) {
        clearInterval(interval);
        call(0);
        resolve(elapsedSeconds);
        return;
      }

      call(seconds - elapsedSeconds);
      elapsedSeconds++;
    }
  }).catch(function(error) {
    // log and rethrow 
    console.log(error);
    throw error;
  });
}

exports.joinGame = functions.https.onRequest((req, res) => {
  // Grab the text parameter.
  var userId = req.query.userId;
  console.log("userid: " + userId);

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
            // Push new user
            return  admin.database().ref('rooms').child(room).child('users').child(userId).set({
              id: userId
            }).then(() => {
              // return http error true
              return res.status(200).end();
            });
          }
        }
      }catch(e){
        console.log('rooms[room].users is undefined');
      }
    }
    var o = {  };
    o[userId] =  { id : userId };
    return  admin.database().ref('rooms').push().set({
      playing: false,
      users: o
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

  return admin.database().ref(parentRoomID).once('value', (snapshot) => {
    return snapshot.forEach((roomID) => {
        console.log(roomID.child("users").numChildren());
        const playingVal = roomID.child("playing").val();
        console.log("Playing value: " + playingVal);

        // Check if the room is full, if the user already joined a room and if 
        // the game is not already playing
        if(roomID.child("users").numChildren() < maxPlayers && alreadyJoined === false
          && playingVal !== PlayingEnum.Playing) {
          const userCount = "user" +  (roomID.child("users").numChildren() + 1).toString();
          const path = parentRoomID + roomID.key;
          _roomID = roomID.key;
          if(roomID.hasChild("users")) {
            if(roomID.child("users/" + username).exists()) {
              admin.database().ref(path).child("users/" + username).remove();
            }
            admin.database().ref(path).child("users").update({[username]:0});
          } else {
            if(roomID.child("users/" + username).exists()) {
              admin.database().ref(path).child("users/" + username).remove();
            }
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
    admin.database().ref(parentRoomID + roomID).update({"words": {[word1]:0,[word2]:0}});
  });

}

exports.chooseWordsGeneration = functions.database.ref(parentRoomID + "{roomID}/users").onWrite((change, context) => {
  const roomID = context.params.roomID;
  return admin.database().ref(parentRoomID + roomID).once('value', (snapshot) => {

    if(snapshot.hasChild("words") && !snapshot.hasChild("users")) {
      // Remove the words because the room is empty
      admin.database().ref(parentRoomID + roomID + "/words").remove();
    }
    else if(snapshot.hasChild("users") && !snapshot.hasChild("words")) {
      // Generate the words
      addWordsToDatabase(roomID);
    }

    return checkUsersReady2(0, parentRoomID + roomID, snapshot);
  });
});

function startTimer(time, roomID, prevState, newState) {
  return functionTimer(time, prevState, roomID, elapsedTime => {
          return admin.database().ref(parentRoomID + roomID + "/timer/observableTime").set(elapsedTime);
      })
      .then(totalTime => {
          return console.log('Timer of ' + totalTime + ' has finished.');
      })
      .then(() => new Promise(resolve => setTimeout(resolve, 1000)))
      .then(() => admin.database().ref(parentRoomID + roomID + "/timer/endTime").set(1))
      .then(() => admin.database().ref(parentRoomID + roomID + "/state").set(newState))
      .catch(error => console.error(error));
}

exports.onStateUpdate = functions.database.ref(parentRoomID + "{roomID}/state").onWrite((change, context) => {
  const roomID = context.params.roomID;
  state = change.after.val();
  let playingRef = admin.database().ref(parentRoomID + roomID + "/playing");
  let stateRef = admin.database().ref(parentRoomID + roomID + "/state");
  let playingState;

  playingRef.on('value', function(snapshot) {
    playingState = snapshot.val();
  });

  switch(state) {
    case StateEnum.Idle:
      playingRef.set(PlayingEnum.Idle);
      break;
    case StateEnum.ChoosingWordsCountdown:
      playingRef.set(PlayingEnum.PlayingButJoinable);
      return startTimer(WAITING_TIME_CHOOSE_WORDS, roomID, StateEnum.ChoosingWordsCountdown, StateEnum.DrawingPage);
    case StateEnum.DrawingPage:
      playingRef.set(PlayingEnum.Playing);
      return startTimer(WAITING_TIME_DRAWING, roomID, StateEnum.DrawingPage, StateEnum.VotingPage);
    case StateEnum.VotingPage:
      return startTimer(WAITING_TIME_VOTING, roomID, StateEnum.VotingPage, StateEnum.EndVoting);
    case StateEnum.EndVoting:
      playingRef.set(PlayingEnum.Idle);
      stateRef.set(StateEnum.Idle);
      break;
    default:
      break;
  }
  return 0;
});
