package ch.epfl.sweng.SDP.matchmaking;

import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.util.HashMap;

import ch.epfl.sweng.SDP.ConstantsWrapper;
import ch.epfl.sweng.SDP.firebase.CheckConnection;

public class Matchmaker implements MatchmakingInterface {

    private ConstantsWrapper constantsWrapper;
    private static Matchmaker singleInstance = null;
    // static method to create instance of Singleton class
    private DatabaseReference reference;

    /**
     *  Create a singleton Instance.
     * @return returns a singleton instance.
     */
    public static Matchmaker getInstance(ConstantsWrapper constantsWrapper)
    {
        if (singleInstance == null) {
            singleInstance = new Matchmaker(constantsWrapper);
        }

        return singleInstance;
    }


    /**
     *  Matchmaker init.
     */
    private Matchmaker(ConstantsWrapper constantsWrapper) {

        this.constantsWrapper = constantsWrapper;
        this.reference = constantsWrapper.getReference("rooms");

        // Read from the database
        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated;
                // Update room

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
    public Boolean joinRoom() {

            Boolean successful = false;
        HttpURLConnection connection = null;

        try {
                //Create connection
                String urlParameters = "userId=" + URLEncoder.encode(constantsWrapper.getFirebaseUserId(), "UTF-8");
                URL url = new URL("https://us-central1-gyrodraw.cloudfunctions.net/joinGame?" + urlParameters);
                connection = createConnection(url);

                //Send request
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.close();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
                String line;

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // OK
                    successful = true;
                    // otherwise, if any other status code is returned, or no status
                    // code is returned, do stuff in the else block
                }

                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
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
     * @param roomId the id of the room.
     */
    public Boolean leaveRoom(String roomId) {
        reference.child(roomId).child("users").child(constantsWrapper.getFirebaseUserId()).removeValue();
        return true;
    }


}
