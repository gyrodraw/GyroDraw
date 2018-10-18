package ch.epfl.sweng.SDP.matchmaking;

import android.support.annotation.NonNull;
import android.util.Log;
import ch.epfl.sweng.SDP.firebase.Database;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

public class Matchmaker implements MatchmakingInterface {

    // Singleton class
    private static Matchmaker singleInstance = null;

    private DatabaseReference myRef;

    /**
     * Create a singleton Instance.
     *
     * @return a singleton instance.
     */
    public static Matchmaker getInstance() {
        if (singleInstance == null) {
            singleInstance = new Matchmaker();
        }

        return singleInstance;
    }


    /**
     * Matchmaker init.
     */
    public Matchmaker() {
        Database database = Database.getInstance();
        myRef = database.getReference("rooms");

        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                // Update room

                Log.d("1", map.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });
    }

    /**
     * Join a room.
     *
     * @param roomId the id of the room.
     */
    public void joinRoom(String roomId) {
        // FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        myRef.child(roomId).child("users").child("123").setValue("InRoom");
    }

    /**
     * Leave a room.
     *
     * @param roomId the id of the room.
     */
    public void leaveRoom(String roomId) {
        // FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        myRef.child(roomId).child("users").child("123").removeValue();
    }
}
