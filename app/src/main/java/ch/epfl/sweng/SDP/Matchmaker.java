package ch.epfl.sweng.SDP;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class Matchmaker implements MatchmakingInterface {

    private static Matchmaker singleInstance = null;
    // static method to create instance of Singleton class
    private DatabaseReference myRef;

    private final static String userName = "claudio";
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
     */
    public Task<String> joinRoom() {
        // FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        FirebaseFunctions mFunctions;
        mFunctions = FirebaseFunctions.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("username", userName);

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
     * leave a room.
     * @param roomId the id of the room.
     */
    public void leaveRoom(String roomId) {
        // FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef.child(roomId).child("users").child(userName).removeValue();
    }

}
