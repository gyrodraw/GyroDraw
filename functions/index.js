// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');

const maxPlayers = 2;
const numberRoomsPerLeague = 100;
const minRank = -10;

// Waiting times
const WAITING_TIME_CHOOSE_WORDS = 10;
const WAITING_TIME_DRAWING = 180;
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
      return admin.database().ref(path + "/state").set(StateEnum.ChoosingWordsCountdown);
    }
    console.log("Ready");
  } else {
    console.log("Not ready");
  }
  return 0;
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

      if (stop === true) {
        stop = false;
        throw new "Timer stopped";
      }

      if (elapsedSeconds >= seconds) {
        clearInterval(interval);
        call(0);
        resolve(elapsedSeconds);
        return 0;
      }

      call(seconds - elapsedSeconds);
      elapsedSeconds++;

      if (isWaiting === true) {
        return admin.database().ref(parentRoomID + roomID).once('value', (snapshot) => {
          let promises = [];
          // If a user leaves stop the timer and return to idle state.
          if (snapshot.child("users").numChildren() < maxPlayers 
            && snapshot.child("state").val() === StateEnum.ChoosingWordsCountdown) {
            stop = true;
            clearInterval(interval);
            call(0);
            promises.push(admin.database().ref(parentRoomID + roomID + "/state").set(StateEnum.Idle));
            promises.push(admin.database().ref(parentRoomID + roomID + "/playing").set(PlayingEnum.Idle));
          }

          if (hasEveryoneVoted(snapshot) === true 
            && snapshot.child("state").val() === StateEnum.ChoosingWordsCountdown) {
            elapsedSeconds = seconds;
          }

          return Promise.all(promises).then(_ => true);

        });
      }
      return 0;
    }
  }).catch((error) => {
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
  var alreadyJoined = false;
  let roomsList = [];

  return admin.database().ref(parentRoomID).once('value', (snapshot) => {
    return snapshot.forEach((roomID) => {

      promises = [];

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

        alreadyJoined = true;

        // Removes the user and adds it again to trigger the event for the user fields.
        if (roomID.child("users/" + id).exists() && roomID.hasChild("users")) {
          return (admin.database().ref(path).child("users/" + id).remove())
                  .then(() => {
                    return admin.database().ref(path).child("users").update({[id]:username});
                  });
        }

        return admin.database().ref(path).child("users").update({[id]:username});
      }

      return 0;

    });
  }).then(() => {
    if (alreadyJoined === false) {
        _roomID = generateRoomID(league, roomsList).toString();
        joinCreatedRoom(_roomID, username, id, gameMode);
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

function generateTwoRandomNumbers(wordsNumber) {
  const firstNumber = getRandomInt(0, wordsNumber - 1);
  let secondNumber = firstNumber;

  while (firstNumber === secondNumber) {
    secondNumber = getRandomInt(0, wordsNumber - 1);
  }

  return [firstNumber, secondNumber];
}

function addWordsToDatabase(roomID) {
  return admin.database().ref("leagues/league" + findLeagueFromRoomID(roomID) + "/words").once('value', (snapshot) => {
    const numbers = generateTwoRandomNumbers(snapshot.numChildren());
    const word1 = snapshot.child(numbers[0]).val();
    const word2 = snapshot.child(numbers[1]).val();
    return admin.database().ref(parentRoomID + roomID).update({"words": {[word1]:0,[word2]:0}});
  });
}

function generateRoomID(league, roomsList) {
  const minRoomID = league * numberRoomsPerLeague;
  const maxRoomID = (league + 1) * numberRoomsPerLeague - 1
  let roomID;
  do {
    roomID = Math.floor(Math.random() * (maxRoomID - minRoomID + 1) + minRoomID);
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

function joinCreatedRoom(roomID, username, id, gameMode) {
  let promises = [];
  let roomObj = {[roomID]:{gameMode : gameMode, state : 0, playing : 0, timer :{observableTime:WAITING_TIME_CHOOSE_WORDS}}};

  promises.push(admin.database().ref(parentRoomID).update(roomObj));
  promises.push(admin.database().ref(parentRoomID + roomID).update({"users":{[id]:username}}));

  return Promise.all(promises).then(_ => true);
}


function removeRoom(roomID) {
  // Do not remove the testing room
  if (roomID !== "0123457890") {
    return admin.database().ref(parentRoomID + roomID).remove();
  }

  return 0;
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
  .then(() => { 
    return admin.database().ref(parentRoomID + roomID + "/state").set(newState);
  })
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
      return snapshot.ref.set(updates);
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
    return 0;
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
    promises = [];
    snapshot.forEach((child) => {
      console.log(child.val());
      if (child.val() === 0) {
        promises.push(admin.database().ref(parentRoomID + roomID + "/ranking/" + child.key).set(-1));
      }
      return;
    });
    return Promise.all(promises).then(_ => true);
  }).then(() => {return createNode(roomID, "onlineStatus", 0)});
}

/**
* Defines the different tasks that server has to do when state is updated. 
*/
exports.onStateUpdate = functions.runWith({timeoutSeconds:100}).database.ref(parentRoomID + "{roomID}/state").onWrite((change, context) => {
  let promises = [];
  const roomID = context.params.roomID;
  state = change.after.val();
  let playingRef = admin.database().ref(parentRoomID + roomID + "/playing");
  let stateRef = admin.database().ref(parentRoomID + roomID + "/state");

  switch(state) {
    case StateEnum.Idle:
      promises.push(playingRef.set(PlayingEnum.Idle));
      promises.push(admin.database().ref(parentRoomID + roomID + "/timer/observableTime").set(WAITING_TIME_CHOOSE_WORDS));
      return Promise.all(promises).then(_ => true);

    case StateEnum.ChoosingWordsCountdown:
      promises.push(admin.database().ref(parentRoomID + roomID + "/timer/observableTime").set(WAITING_TIME_CHOOSE_WORDS));
      promises.push(playingRef.set(PlayingEnum.PlayingButJoinable));
      promises.push(startTimer(WAITING_TIME_CHOOSE_WORDS, roomID, StateEnum.ChoosingWordsCountdown, StateEnum.DrawingPage, true, true));
      return Promise.all(promises).then(_ => true);

    case StateEnum.DrawingPage:
      promises.push(admin.database().ref(parentRoomID + roomID + "/timer/observableTime").set(WAITING_TIME_DRAWING));
      promises.push(createNode(roomID, "ranking", 0));
      promises.push(createNode(roomID, "finished", 0));
      promises.push(createNode(roomID, "uploadDrawing", 0));
      promises.push(playingRef.set(PlayingEnum.Playing));
      promises.push(startTimer(WAITING_TIME_DRAWING, roomID, StateEnum.DrawingPage, StateEnum.WaitingUpload, false, false));
      return Promise.all(promises).then(_ => true);

    case StateEnum.WaitingUpload:
      // Set timeout to pass to next activity to avoid infinites timeouts.
      return setTimeout(() => {
        return stateRef.set(StateEnum.VotingPage);
      }, 5000);

    case StateEnum.VotingPage:
      promises.push(admin.database().ref(parentRoomID + roomID + "/timer/observableTime").set(WAITING_TIME_VOTING));
      promises.push(startTimer(WAITING_TIME_VOTING, roomID, StateEnum.VotingPage, StateEnum.EndVoting, false, false));
      return Promise.all(promises).then(_ => true);

    case StateEnum.EndVoting:
      // Check if some user disconnected during voting phase
      return setTimeout(() => {
        promises.push(checkOnlineNode(roomID));
        promises.push(stateRef.set(StateEnum.ShowRanking));
        return Promise.all(promises).then(_ => true);
      }, 1500);

    case StateEnum.ShowRanking:
      setTimeout(() => {
        return removeRoom(roomID);
      }, 8000);

      return admin.database().ref(parentRoomID + roomID + "/ranking").once('value', (snapshot) => {
        return updateDisconnectedUsers(snapshot, roomID);
      });

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
      return admin.database().ref(parentRoomID + roomID + "/state").set(StateEnum.VotingPage);
    }
    return 0;
  })
});