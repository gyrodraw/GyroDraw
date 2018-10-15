// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');

const maxPlayers = 5;
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
