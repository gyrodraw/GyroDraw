package ch.epfl.sweng.SDP.matchmaking;

import android.util.Log;

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
    public void joinRoom() {
            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

            HttpURLConnection connection = null;

            try {
                //Create connection
                String urlParameters = "userId=" + URLEncoder.encode(currentFirebaseUser.getUid(), "UTF-8");
                URL url = new URL("https://us-central1-gyrodraw.cloudfunctions.net/joinGame?" + urlParameters);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");


              //  connection.setRequestProperty("Content-Length",
               //         Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches(false);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
               // wr.writeBytes(urlParameters);
                wr.close();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                 System.out.println(response.toString());
            } catch (Exception e) {
                e.printStackTrace();
             //   return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }


    }

    /**
     * leave a room.
     * @param roomId the id of the room.
     */
    public void leaveRoom(String roomId) {
        // FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        myRef.child(roomId).child("users").child("123").removeValue();
    }


}
