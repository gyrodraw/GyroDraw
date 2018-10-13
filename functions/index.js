// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

exports.connectedUsers = functions.database.ref('/timer/connectedUsers').onWrite((event) => {
    console.log("Function connectedUsers");
    return admin.database().ref('/timer/connectedUsers').once("value")
    .then(snapshot => {
      if(snapshot.val() === 5) {
        admin.database().ref('/timer').update({'startTimer': 1});
      } else {
        admin.database().ref('/timer').update({'startTimer': 0});
      }
      return;
    });
});

exports.startTimer = functions.database.ref('/timer/startTimer').onWrite((event) => {
  return admin.database().ref('/timer/startTimer').once("value")
  .then(snapshot => {
    if(snapshot.val() === 1) {
      console.log("Timer started");

      return functionTimer(20,
            elapsedTime => {
                admin.database().ref('timer/observableTime').set(elapsedTime);
            })
            .then(totalTime => {
                return console.log('Timer of ' + totalTime + ' has finished.');
            })
            .then(() => new Promise(resolve => setTimeout(resolve, 1000)))
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
