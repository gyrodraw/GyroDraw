package ch.epfl.sweng.SDP;

import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.PublicKey;
import java.util.HashMap;

public class Matchmaker implements MatchmakingInterface {


    private static Matchmaker singleInstance = null;
    // static method to create instance of Singleton class
    private DatabaseReference myRef;

    /**
     *  Create a singleton Instance.
     * @return returns a singleton instance.
     */
    public static Matchmaker getInstance()
    {
        if (singleInstance == null) {
            singleInstance = new Matchmaker();
        }

        return singleInstance;
    }


    /**
     *  Matchmaker init.
     */
    public Matchmaker() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("rooms");

        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                // Update room

                Log.d("1",map.toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    /**
     * join a room.
     * @param roomId the id of the room.
     */
    public void joinRoom(String roomId) {
       // FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        myRef.child(roomId).child("users").child("123").setValue("InRoom");
    }

    /**
     * leave a room.
     * @param roomId the id of the room.
     */
    public void leaveRoom(String roomId) {
        // FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        myRef.child(roomId).child("users").child("123").removeValue();
    }


}
