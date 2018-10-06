package ch.epfl.sweng.SDP;

import android.util.Log;

import com.google.firebase.database.*;
import com.google.firebase.auth.*;

import java.security.PublicKey;

public class Matchmaker {

    public static final String TAG = "1";

    public Matchmaker() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("rooms");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);

                // Update room
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public void joinRoom(String roomID) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("rooms");

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        myRef.child(roomID).child("users").child(currentFirebaseUser.getUid()).setValue("InRoom");

    }

    public void leaveRoom(String roomID) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("rooms");

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        myRef.child(roomID).child("users").child(currentFirebaseUser.getUid()).removeValue();

    }


    public void createRoom(String roomname) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("rooms");

        // room owner + roomname
        myRef.push().setValue("New room");

    }


}
