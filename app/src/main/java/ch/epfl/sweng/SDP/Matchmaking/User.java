package ch.epfl.sweng.SDP.Matchmaking;

import android.annotation.TargetApi;
import android.os.Build;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.toIntExact;

public class User implements Serializable {

    private static User singleUser = null;

    public static User getInstance()
    {
        if (singleUser == null) {
            singleUser = new User();
        }

        return singleUser;
    }


    private String name;
    private String username;
    private String id;

    private int stars;
    private int matchesWon;
    private int matchesLost;
    private int matchesPlayed;
    private double averageRating;
    private int trophies;

    private ArrayList<String> friends;

    private DatabaseReference mDatabase;

    public User(String id) {
        this.id = id;
    }

    public User() {
        this.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void downloadUser() {

        // Read from the database
        mDatabase.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {


            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                // Update room

                name = (String) map.get("name");
                name = (String) map.get("username");
                id = (String) map.get("id");

                stars = toIntExact( (Long) map.get("stars"));
                trophies = toIntExact((Long) map.get("trophies"));
                matchesWon = toIntExact( (Long) map.get("matchesWon"));
                matchesLost = toIntExact((Long) map.get("matchesLost"));
                matchesPlayed = toIntExact((Long) map.get("matchesPlayed"));
                averageRating = toIntExact((Long) map.get("averageRating"));

                Log.d("1",map.toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

    }

    public void uploadUser() {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(id).setValue(this);

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTrophies() {
        return trophies;
    }

    public void setTrophies(int trophies) {
        this.trophies = trophies;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public int getStars() {
        return stars;
    }
    public void setStars(int stars) {
        this.stars = stars;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }
    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public int getMatchesWon() {

        return matchesWon;
    }

    public void setMatchesWon(int matchesWon) {

        this.matchesWon = matchesWon;
    }

    public int getMatchesLost() {

        return matchesLost;
    }

    public void setMatchesLost(int matchesLost) {

        this.matchesLost = matchesLost;
    }

    public int getMatchesPlayed() {

        return matchesPlayed;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public double getAverageRating() {

        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

}