package ch.epfl.sweng.SDP;

import android.util.Log;

import com.google.firebase.database.*;
import com.google.firebase.auth.*;

import java.security.PublicKey;
import java.util.HashMap;

public class Matchmaker {


    private static Matchmaker single_instance = null;
    // static method to create instance of Singleton class
    public static Matchmaker getInstance()
    {
        if (single_instance == null)
            single_instance = new Matchmaker();

        return single_instance;
    }


    public Matchmaker() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("rooms");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();

                // Update room

                System.out.println(map);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

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
