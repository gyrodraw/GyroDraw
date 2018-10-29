package ch.epfl.sweng.SDP.matchmaking;

import android.support.annotation.NonNull;
import android.util.Log;

import ch.epfl.sweng.SDP.ConstantsWrapper;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.firebase.Database.DatabaseReferenceBuilder;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;

import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.firebase.Database.DatabaseReferenceBuilder;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton enum responsible for the matchmaking.
 */
public enum Matchmaker implements MatchmakingInterface {
    // Singleton enum

    INSTANCE;

    private DatabaseReference myRef;
    private static ConstantsWrapper constantsWrapper = new ConstantsWrapper();

    /**
     * Matchmaker init.
     */
    Matchmaker() {
        myRef = Database.INSTANCE.getReference("realRooms");

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
     */
    public Task<String> joinRoom() {
        FirebaseFunctions mFunctions;
        mFunctions = FirebaseFunctions.getInstance();

        Map<String, Object> data = new HashMap<>();

        // Pass the ID for the moment
        data.put("username", constantsWrapper.getFirebaseUserId());

        return mFunctions.getHttpsCallable("joinGame2")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });

    }

    /**
     * Leave a room.
     *
     * @param roomId the id of the room.
     */
    public void leaveRoom(String roomId) {
        myRef.child(roomId).child("users")
                .child(constantsWrapper.getFirebaseUserId()).removeValue();
    }
}
