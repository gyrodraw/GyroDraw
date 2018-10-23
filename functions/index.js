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
              name: "fredrik"
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
    o[userId] =  { name : "fredrik"};
    return  admin.database().ref('rooms').push().set({
      playing: false,
      users: o
    }).then( () => {
      return res.status(200).end();
    });
  });

});
