package ch.epfl.sweng.SDP.matchmaking;

import android.support.annotation.NonNull;

import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class that represents the matchmaker.
 */
public class Matchmaker implements MatchmakingInterface {

    private static Matchmaker singleInstance = null;

    private DatabaseReference roomsRef;
    private Account account;

    /**
     * Get (eventually create) the instance.
     *
     * @return the unique instance.
     */
    public static Matchmaker getInstance(Account account) {
        if (singleInstance == null) {
            singleInstance = new Matchmaker(account);
        }

        return singleInstance;
    }

    private Matchmaker(Account account) {
        this.roomsRef = Database.INSTANCE.getReference("realRooms");
        this.account = account;
    }

    /**
     * Create a connection.
     *
     * @return a {@link Task} wrapping the result
     */
    public Task<String> joinRoom() {
        FirebaseFunctions mFunctions;
        mFunctions = FirebaseFunctions.getInstance();

        Map<String, Object> data = new HashMap<>();

        // Pass the ID for the moment
        data.put("id", account.getUserId());
        data.put("username", account.getUsername());

        // Use regex to extract the number from the league string
        // TODO define a method in account that extracts directly the number corresponding
        // TODO to the league
        data.put("league", account.getCurrentLeague().replaceAll("\\D+", ""));

        return mFunctions.getHttpsCallable("joinGame2")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        return (String) task.getResult().getData();
                    }
                });
    }

    /**
     * Leave a room.
     *
     * @param roomId the id of the room.
     */
    public void leaveRoom(String roomId) {
        roomsRef.child(roomId).child("users")
                .child(account.getUserId()).removeValue();

        if (!account.getUsername().isEmpty()) {
            roomsRef.child(roomId).child("ranking")
                    .child(account.getUsername()).removeValue();
        }
    }
}
