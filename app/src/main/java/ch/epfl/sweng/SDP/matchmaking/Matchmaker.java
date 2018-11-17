package ch.epfl.sweng.SDP.matchmaking;

import static java.lang.String.format;

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

    private DatabaseReference myRef;
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
        this.myRef = Database.getReference("realRooms");
        this.account = account;
    }

    /**
     * Join a room.
     *
     * @return true if it was successful, false otherwise
     */
    public Boolean joinRoomOther() {

        Boolean successful = false;
        HttpURLConnection connection = null;

        try {
            //Create connection

            String userId = account.getUserId();
            String urlParameters = "userId=" + URLEncoder.encode(userId, "UTF-8");
            URL url = new URL(
                    "https://us-central1-gyrodraw.cloudfunctions.net/joinGame?" + urlParameters);
            connection = createConnection(url);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // OK
                successful = true;
                // otherwise, if any other status code is returned, or no status
                // code is returned, do stuff in the else block
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return successful;
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

    private HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");

        connection.setRequestProperty("Content-Language", "en-US");

        connection.setUseCaches(false);
        connection.setDoOutput(true);
        return connection;
    }

    /**
     * Leave a room.
     *
     * @param roomId the id of the room.
     */
    public void leaveRoom(String roomId) {
        Database.constructBuilder(myRef)
                .addChildren(format("%s.users.%s", roomId, account.getUserId())).build()
                .removeValue();

        if (!account.getUsername().isEmpty()) {
            Database.constructBuilder(myRef)
                    .addChildren(format("%s.ranking.%s", roomId, account.getUsername())).build()
                    .removeValue();
        }
    }
}
