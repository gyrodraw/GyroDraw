package ch.epfl.sweng.SDP.matchmaking;

import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.util.HashMap;

public class Matchmaker {

    public boolean testing = false;

    private static Matchmaker singleInstance = null;
    // static method to create instance of Singleton class
    private DatabaseReference myRef;

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
    private Matchmaker() {

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
        String currentFirebaseUser = currentFirebaseUser = "TEST_USER";
        if (!testing) {
            currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        HttpURLConnection connection = null;
        try {

            //Create connection
            String urlParameters = "userId=" + URLEncoder.encode(currentFirebaseUser, "UTF-8");
            URL url = new URL("https://us-central1-gyrodraw.cloudfunctions.net/joinGame?" + urlParameters);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

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
                return true;
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
        return false;
    }



    /**
     * leave a room.
     * @param roomId the id of the room.
     */
    public Boolean leaveRoom(String roomId) {
        String currentFirebaseUser = currentFirebaseUser = "TEST_USER";
        if (!testing) {
            currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        myRef.child(roomId).child("users").child(currentFirebaseUser).removeValue();
        return true;
    }


}
