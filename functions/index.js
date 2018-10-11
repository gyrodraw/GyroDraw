const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
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



            // no empty room found, create new game.



  // Create a new room


  // Create the room
  // Push the new message into the Realtime Database using the Firebase Admin SDK.
//  return admin.database().ref('/messages').push({original: original}).then((snapshot) => {
    // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
//  });


});

// check if there is an available room. If so, join game.
/* exports.joinGameListener = functions.database.ref('/rooms/{uid}/users')
    .onWrite(event => {
      var users = event.data.val();

      if (users.length > 4) {
           console.log('Room is full, starting game!');
      }
}); */
