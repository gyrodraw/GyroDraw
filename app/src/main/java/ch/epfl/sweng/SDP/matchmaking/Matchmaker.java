package ch.epfl.sweng.SDP.matchmaking;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;

import static java.lang.String.format;

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
        this.roomsRef = Database.getReference("realRooms");
        this.account = account;
    }

    /**
     * Join a room by calling a FirebaseFunction that will handle
     * which particular room a player should join.
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
        Database.constructBuilder(roomsRef)
                .addChildren(format("%s.users.%s", roomId, account.getUserId())).build()
                .removeValue();

        if (!account.getUsername().isEmpty()) {
            Database.constructBuilder(roomsRef)
                    .addChildren(format("%s.ranking.%s", roomId, account.getUsername())).build()
                    .removeValue();
            Database.constructBuilder(roomsRef)
                    .addChildren(format("%s.finished.%s", roomId, account.getUsername())).build()
                    .removeValue();
            Database.constructBuilder(roomsRef)
                    .addChildren(format("%s.uploadDrawing.%s", roomId, account.getUsername()))
                    .build()
                    .removeValue();
        }
    }
}
