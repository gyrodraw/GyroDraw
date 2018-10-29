package ch.epfl.sweng.SDP.matchmaking;

import android.support.annotation.NonNull;

import ch.epfl.sweng.SDP.ConstantsWrapper;

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

import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Matchmaker implements MatchmakingInterface {

    private ConstantsWrapper constantsWrapper;
    private static Matchmaker singleInstance = null;
    // static method to create instance of Singleton class
    private DatabaseReference reference;

    /**
     * Create a singleton Instance.
     *
     * @return returns a singleton instance.
     */
    public static Matchmaker getInstance() {
        if (singleInstance == null) {
            singleInstance = new Matchmaker();
        }

        return singleInstance;
    }


    private DatabaseReference myRef;

    /**
     * Matchmaker init.
     */
    private Matchmaker() {
        this.myRef = Database.INSTANCE.getReference("realRooms");
        this.constantsWrapper = new ConstantsWrapper();
        this.reference = constantsWrapper.getReference("rooms");
    }

    /**
     * join a room.
     */
    public Boolean joinRoomOther() {

        Boolean successful = false;
        HttpURLConnection connection = null;

        try {
            //Create connection

            String userId = constantsWrapper.getFirebaseUserId();
            String urlParameters = "userId=" + URLEncoder.encode(userId, "UTF-8");
            URL url = new URL("https://us-central1-gyrodraw.cloudfunctions.net/joinGame?" + urlParameters);
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
     * Creates a connection.
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
     * Creates a connection.
     * @return set up connection
     */
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
     * leave a room.
     *
     * @param roomId the id of the room.
     */
    public void leaveRoom(String roomId) {
        myRef.child(roomId).child("users")
                .child(constantsWrapper.getFirebaseUserId()).removeValue();
    }

    public Boolean leaveRoomOther(String roomId) {
        reference.child(roomId)
                .child("users")
                .child(constantsWrapper.getFirebaseUserId())
                .removeValue();
        return true;
    }
}
