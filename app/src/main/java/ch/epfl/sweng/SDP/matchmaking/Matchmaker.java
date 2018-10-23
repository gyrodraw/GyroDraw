package ch.epfl.sweng.SDP.Matchmaking;

import android.support.annotation.NonNull;
import android.util.Log;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.firebase.Database.DatabaseReferenceBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

/**
 * Singleton enum responsible for the matchmaking.
 */
public enum Matchmaker  {
    // Singleton enum

    INSTANCE;

    private DatabaseReference myRef;

    /**
     * Matchmaker init.
     */
    Matchmaker() {
        myRef = Database.INSTANCE.getReference("rooms");

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
        DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder(myRef);
        builder.addChildren(roomId + ".users.123").build().setValue("InRoom");
    }

    /**
     * Leave a room.
     *
     * @param roomId the id of the room.
     */
    public void leaveRoom(String roomId) {
        // FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder(myRef);
        builder.addChildren(roomId + ".users.123").build().removeValue();
    }
}
