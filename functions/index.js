// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');

const maxPlayers = 5;
const maxWords = 6;
const numberRoomsPerLeague = 100;
const minRank = -10;

// Waiting times
const WAITING_TIME_CHOOSE_WORDS = 10;
const WAITING_TIME_DRAWING = 10;
const WAITING_TIME_VOTING = 30;

const parentRoomID = "realRooms/";

var StateEnum = Object.freeze({"Idle": 0, "ChoosingWordsCountdown": 1, "DrawingPage": 2, "WaitingUpload": 3, "VotingPage": 4, "EndVoting": 5, "ShowRanking": 6});
var PlayingEnum = Object.freeze({"Idle": 0, "PlayingButJoinable": 1, "Playing": 2});
var state = 0;

admin.initializeApp();

function checkUsersReady(path, snapshot) {
  if (snapshot.child("users").numChildren() === maxPlayers) {
    if (snapshot.child("state").val() === StateEnum.Idle ||
      snapshot.child("state").val() === StateEnum.EndVoting) {
      admin.database().ref(path + "/state").set(StateEnum.ChoosingWordsCountdown);
    }
    console.log("Ready");
  } else {
    console.log("Not ready");
  }
  return;
}

// Check if all the values childs of a node are true i.e. the values are set to 1
function checkNodeTrue(snapshot) {
  let ready = true;
  snapshot.forEach((child) => {
    if (child.val() !== 1) {
      ready = false;
    }
  });

  return ready;
}

function checkNodeTrueTesting(snapshot, howMany) {
  let count = 0;
  snapshot.forEach((child) => {
    if (child.val() === 1) {
      count++;
    }
  });

  return count >= howMany;
}

function hasEveryoneVoted(snapshot) {
  let sum = 0;
  snapshot.child("words").forEach((child) => {
    sum += child.val();
  });

  if (sum >= maxPlayers) {
    return true;
  }

  return false;
}

function functionTimer (seconds, state, roomID, isWaiting, call) {
  return new Promise((resolve, reject) => {
    // This represents the maximum time allowed for firebase.
    if (seconds > 300) {
        return;
    }

    let elapsedSeconds = 0;
    let stop = false;
    let interval = setInterval(onInterval, 1000);

    function onInterval() {

      if(isWaiting === true){
        admin.database().ref(parentRoomID + roomID).once('value', (snapshot) => {
          // If a user leaves stop the timer and return to idle state.
          if (snapshot.child("users").numChildren() < maxPlayers 
            && snapshot.child("state").val() === StateEnum.ChoosingWordsCountdown) {
            stop = true;
            clearInterval(interval);
            call(0);
            admin.database().ref(parentRoomID + roomID + "/state").set(StateEnum.Idle);
            admin.database().ref(parentRoomID + roomID + "/playing").set(PlayingEnum.Idle);
          }

          if (hasEveryoneVoted(snapshot) === true 
            && snapshot.child("state").val() === StateEnum.ChoosingWordsCountdown) {
            elapsedSeconds = seconds;
          }
        });
      }

      if (stop === true){
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
    // Log and rethrow
    console.log(error);
    throw error;
  });
}

exports.joinGame = functions.https.onCall((data, context) => {
  const id = data.id;
  const username = data.username;
  const league = data.league - 1;
  const gameMode = data.mode;

  let _roomID;
  console.log(username);
  var alreadyJoined = false;
  let roomsList = [];

  return admin.database().ref(parentRoomID).once('value', (snapshot) => {
    return snapshot.forEach((roomID) => {

      // Retrieve the room game mode
      var roomGameMode = roomID.val().gameMode;
      console.log("Game mode:" + roomGameMode);

      const playingVal = roomID.child("playing").val();
      roomsList.push(parseInt(roomID.key, 10));

      // Checks if the room is full, if the user already joined a room and if
      // the game is not already playing
      if (roomID.child("users").numChildren() < maxPlayers && alreadyJoined === false
        && playingVal !== PlayingEnum.Playing && isRoomInLeagueRange(roomID.key, league) === true 
        && roomGameMode === gameMode) {
        const userCount = "user" +  (roomID.child("users").numChildren() + 1).toString();
        const path = parentRoomID + roomID.key;
        _roomID = roomID.key;

        // Removes the user and adds it again to trigger the event for the user fields.
        if (roomID.child("users/" + id).exists() && roomID.hasChild("users")) {
          admin.database().ref(path).child("users/" + id).remove();
        }

        admin.database().ref(path).child("users").update({[id]:username});

        alreadyJoined = true;
      }
    });
  }).then(() => {
    if (alreadyJoined === false) {
        _roomID = createRoomAndJoin(league, roomsList, username, id, gameMode);
    }
    console.log(_roomID);
    return _roomID;
  });
});

function getLeagueFromTrophies(trophies) {
  if (trophies >= 0 && trophies < 100) {
    return "league1";
  } else if (trophies >= 100 && trophies < 200) {
    return "league2"; 
  } else {
    return "league3";
  }
}

function isRoomInLeagueRange(roomID, league) {
  return parseInt(roomID, 10) >= league * numberRoomsPerLeague &&
          parseInt(roomID, 10) < (league + 1) * numberRoomsPerLeague;
}

function findLeagueFromRoomID(roomID) {
  return Math.floor(parseInt(roomID, 10) / numberRoomsPerLeague) + 1;
}

function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function generateTwoRandomNumbers() {
  const firstNumber = getRandomInt(0, maxWords - 1);
  let secondNumber = firstNumber;

  while (firstNumber === secondNumber) {
    secondNumber = getRandomInt(0, maxWords - 1);
  }

  return [firstNumber, secondNumber];
}

function addWordsToDatabase(roomID) {
  const numbers = generateTwoRandomNumbers();
  return admin.database().ref("leagues/league" + findLeagueFromRoomID(roomID) + "/words").once('value', (snapshot) => {
    const word1 = snapshot.child(numbers[0]).val();
    const word2 = snapshot.child(numbers[1]).val();
    admin.database().ref(parentRoomID + roomID).update({"words": {[word1]:0,[word2]:0}});
  });

}

function generateRoomID(league, roomsList) {
  const minRoomID = league * numberRoomsPerLeague;
  const maxRoomID = (league + 1) * numberRoomsPerLeague - 1
  let roomID;
  do {
    roomID = Math.floor(Math.random()*(maxRoomID - minRoomID + 1) + minRoomID);
  } while (roomsList.includes(roomID))

  return roomID;
}

function createRoomAndJoin(league, roomsList, username, id, gameMode) {
  const roomID = generateRoomID(league, roomsList);

  let roomObj = {[roomID]:{gameMode : gameMode, state : 0, playing : 0, timer :{observableTime:WAITING_TIME_CHOOSE_WORDS}}};

  admin.database().ref(parentRoomID).update(roomObj);
  admin.database().ref(parentRoomID + roomID).update({"users":{[id]:username}});

  return roomID.toString();
}

function removeRoomData(roomID) {
  admin.database().ref(parentRoomID + roomID).child("users").remove();
  admin.database().ref(parentRoomID + roomID).child("ranking").remove();
  admin.database().ref(parentRoomID + roomID).child("playing").set(PlayingEnum.Idle);
  admin.database().ref(parentRoomID + roomID).child("state").set(StateEnum.Idle);
  admin.database().ref(parentRoomID + roomID).child("finished").remove();
  admin.database().ref(parentRoomID + roomID).child("uploadDrawing").remove();
}

function removeRoom(roomID) {
  // Do not remove the testing room
  if (roomID !== "0123457890") {
    admin.database().ref(parentRoomID + roomID).remove();
  }
}

exports.onUsersChange = functions.database.ref(parentRoomID + "{roomID}/users").onWrite((change, context) => {
  const roomID = context.params.roomID;
  let isRoomRemoved = false;
  return admin.database().ref(parentRoomID + roomID).once('value', (snapshot) => {

    if (snapshot.hasChild("words") && !snapshot.hasChild("users")) {
      // Removes the words because the room is empty
      admin.database().ref(parentRoomID + roomID + "/words").remove();
      removeRoom(roomID);
      isRoomRemoved = true;
    } else if(snapshot.hasChild("users") && !snapshot.hasChild("words")) {
      // Generates the words
      addWordsToDatabase(roomID);
    }

    if (isRoomRemoved === false) {
      checkUsersReady(parentRoomID + roomID, snapshot);
    }
  });
});

function startTimer(time, roomID, prevState, newState, nodeCreation, isWaiting) {
  return functionTimer(time, prevState, roomID, isWaiting, elapsedTime => {
    return admin.database().ref(parentRoomID + roomID + "/timer/observableTime").set(elapsedTime);
  })
  .then(totalTime => {
      return console.log('Timer of ' + totalTime + ' has finished.');
  })
  .then(() => {

    // A new node needs to be created after the game starts. Otherwise just checks the node.
    if(nodeCreation === true) {
      return createNode(roomID, "onlineStatus", 0);
    } else {
      return checkOnlineNode(roomID);
    }
  })
  .then(() => admin.database().ref(parentRoomID + roomID + "/state").set(newState))
  .catch(error => console.error(error));
}

function createNode(roomID, node, value) {
  updates = {};
  return admin.database().ref(parentRoomID + roomID).child("users").once('value', (snapshot) => {
    snapshot.forEach((child) => {
      updates[child.val()] = value;
    });
  }).then(() => { 
    return admin.database().ref(parentRoomID + roomID).child(node).once('value', function(snapshot) {
      snapshot.ref.set(updates);
    });
  });
}

function updateUser(userID) {
  return admin.database().ref("users/").once('value', (snapshot) => {
    if(snapshot.hasChild(userID)) {
      let newTrophies;
      let totalMatches;

      return admin.database().ref("users/" + userID + "/trophies").transaction((currentValue) => {
        newTrophies = Math.max(currentValue + minRank, 0);
        return newTrophies;
      })
      .then(() => {
        return admin.database().ref("users/" + userID + "/currentLeague").transaction((currentValueInner) => {
          return getLeagueFromTrophies(newTrophies);
        });
      })
      .then(() => {
        return admin.database().ref("users/" + userID + "/totalMatches").transaction((currentValue) => {
          totalMatches = currentValue + 1;
          return totalMatches;
        });
      })
      .then(() => {
        return admin.database().ref("users/" + userID + "/averageRating").transaction((currentValueInner) => {
          return parseFloat(((currentValueInner*(totalMatches-1))/totalMatches).toPrecision(3));
        });
      });
    }
  });
}

function updateDisconnectedUsers(snapshotRanking, roomID) {
  let dict = {};
  return admin.database().ref(parentRoomID + roomID + "/users").once('value', (snapshot) => {
    return snapshot.forEach((child) => {
      dict[child.val()] = child.key;
    });
  }).then(() => {
      return snapshotRanking.forEach((child) => {
        if (child.val() === -1) {
          updateUser(dict[child.key]);
        }
    });
  });
}

function checkOnlineNode(roomID) {
  return admin.database().ref(parentRoomID + roomID + "/onlineStatus").once('value', (snapshot) => {
    snapshot.forEach((child) => {
      console.log(child.val());
      if (child.val() === 0) {
        admin.database().ref(parentRoomID + roomID + "/ranking/" + child.key).set(-1);
      }
    });
  }).then(() => {return createNode(roomID, "onlineStatus", 0)});
}

/**
* Defines the different tasks that server has to do when state is updated. 
*/
exports.onStateUpdate = functions.database.ref(parentRoomID + "{roomID}/state").onWrite((change, context) => {
  const roomID = context.params.roomID;
  state = change.after.val();
  let playingRef = admin.database().ref(parentRoomID + roomID + "/playing");
  let stateRef = admin.database().ref(parentRoomID + roomID + "/state");

  switch(state) {
    case StateEnum.Idle:
      playingRef.set(PlayingEnum.Idle);
      admin.database().ref(parentRoomID + roomID + "/timer/observableTime").set(WAITING_TIME_CHOOSE_WORDS);
      break;

    case StateEnum.ChoosingWordsCountdown:
      admin.database().ref(parentRoomID + roomID + "/timer/observableTime").set(WAITING_TIME_CHOOSE_WORDS);
      playingRef.set(PlayingEnum.PlayingButJoinable);
      return startTimer(WAITING_TIME_CHOOSE_WORDS, roomID, StateEnum.ChoosingWordsCountdown, StateEnum.DrawingPage, true, true);

    case StateEnum.DrawingPage:
      admin.database().ref(parentRoomID + roomID + "/timer/observableTime").set(WAITING_TIME_DRAWING);
      createNode(roomID, "ranking", 0);
      createNode(roomID, "finished", 0);
      createNode(roomID, "uploadDrawing", 0);
      playingRef.set(PlayingEnum.Playing);
      return startTimer(WAITING_TIME_DRAWING, roomID, StateEnum.DrawingPage, StateEnum.WaitingUpload, false, false);

    case StateEnum.WaitingUpload:
      // Set timeout to pass to next activity to avoid infinites timeouts.
      setTimeout(() => {
        admin.database().ref(parentRoomID + roomID + "/state").set(StateEnum.VotingPage);
      } , 5000);
      break;

    case StateEnum.VotingPage:
      admin.database().ref(parentRoomID + roomID + "/timer/observableTime").set(WAITING_TIME_VOTING);
      return startTimer(WAITING_TIME_VOTING, roomID, StateEnum.VotingPage, StateEnum.EndVoting, false, false);

    case StateEnum.EndVoting:

      // Check if some user disconnected during voting phase
      setTimeout(() => {
        checkOnlineNode(roomID);
        admin.database().ref(parentRoomID + roomID + "/state").set(StateEnum.ShowRanking);
      }, 1500);
      break;

    case StateEnum.ShowRanking:
      admin.database().ref(parentRoomID + roomID + "/ranking").once('value', (snapshot) => {
        return updateDisconnectedUsers(snapshot, roomID);
      });

      setTimeout(() => {
        removeRoom(roomID);
      }, 8000);

      break;
    default:
      break;
  }

  return 0;
});

/**
* Checks if every user entered the last state of the game (ranking page). If it is the case remove the room. 
*/
exports.onFinishedUpdate = functions.database.ref(parentRoomID + "{roomID}/finished").onWrite((change, context) => {
  const roomID = context.params.roomID;
  return admin.database().ref(parentRoomID + roomID).once('value', (snapshot) => {
    if (checkNodeTrue(snapshot.child("finished")) === true && roomID !== "0123457890" && snapshot.child("state").val() === StateEnum.EndVoting) {
      removeRoom(roomID);
    }
  });
});

/**
* Checks if every user uploaded their drawings. If it is the case proceed to the next state. 
*/
exports.onUploadDrawingUpdate = functions.database.ref(parentRoomID + "{roomID}/uploadDrawing").onWrite((change, context) => {
  const roomID = context.params.roomID;
  return admin.database().ref(parentRoomID + roomID).once('value', (snapshot) => {
    if (checkNodeTrue(snapshot.child("uploadDrawing")) === true && snapshot.child("state").val() === StateEnum.WaitingUpload) {
      admin.database().ref(parentRoomID + roomID + "/state").set(StateEnum.VotingPage);
    }
  })
});