package ch.epfl.sweng.SDP.matchmaking;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;

/**
 * Singleton class that represents the matchmaker.
 */
public class Matchmaker implements MatchmakingInterface {

    private static Matchmaker instance = null;

    private Account account;

    /**
     * Gets (eventually creates) the instance.
     *
     * @return the unique instance.
     */
    public static Matchmaker getInstance(Account account) {
        if (instance == null) {
            instance = new Matchmaker(account);
        }

        return instance;
    }

    private Matchmaker(Account account) {
        if (instance != null) {
            throw new IllegalStateException("Already instantiated");
        }
        this.account = account;
    }

    /**
     * Joins a room by calling a FirebaseFunction that will handle which particular room a player
     * should join.
     *
     * @return a {@link Task} wrapping the result
     */
    public Task<String> joinRoom(int gameMode) {
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
        data.put("mode", gameMode);

        return mFunctions.getHttpsCallable("joinGame")
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
     * Leaves a room.
     *
     * @param roomId the id of the room.
     */
    public void leaveRoom(String roomId) {
        if (!account.getUsername().isEmpty()) {
            Database.removeUserFromRoom(roomId, account);
        }
    }
}
