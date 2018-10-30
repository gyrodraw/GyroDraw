package ch.epfl.sweng.SDP;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static java.lang.Math.toIntExact;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that simulates an account.
 */
public class Account implements java.io.Serializable {

    private String userId;
    private String username;
    private int trophies;
    private int stars;
    private transient DatabaseReference usersRef;

    private int matchesWon;
    private int matchesLost;
    private double averageRating;

    /**
     * Builder for account.
     *
     * @param username String defining the preferred username
     */
    public Account(ConstantsWrapper constantsWrapper, String username) {
        this(constantsWrapper, username, 0, 0);
    }

    /**
     * Builder for account.
     *
     * @param username String defining the preferred username
     * @param trophies int defining current rating
     * @param stars    int defining current currency
     */
    public Account(ConstantsWrapper constantsWrapper, String username, int trophies, int stars) {
        this.usersRef = constantsWrapper.getReference("users");
        this.userId = constantsWrapper.getFirebaseUserId();
        this.username = username;
        this.trophies = trophies;
        this.stars = stars;
    }

    public String getUsername() {
        return username;
    }

    public int getTrophies() {
        return trophies;
    }

    public int getStars() {
        return stars;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Registers this account in Firebase.
     */
    public void registerAccount() throws DatabaseException {
        usersRef.child(userId).setValue(this, createCompletionListener());
    }

    /**
     * Checks in firebase if username already exists.
     *
     * @param newName username to compare
     * @throws IllegalArgumentException If username is null or already taken
     * @throws DatabaseException        If something went wrong with database.
     */
    public void checkIfAccountNameIsFree(final String newName)
            throws IllegalArgumentException, DatabaseException {
        if (newName == null) {
            throw new IllegalArgumentException("Username must not be null");
        }
        usersRef.orderByChild("username").equalTo(newName)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            throw new IllegalArgumentException("Username already taken.");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
    }

    /**
     * Updates Username to newName.
     *
     * @param newName new username
     * @throws IllegalArgumentException if username not available anymore
     * @throws DatabaseException        if problem with firebase
     */
    public void updateUsername(final String newName)
            throws IllegalArgumentException, DatabaseException {
        checkIfAccountNameIsFree(newName);
        // is this code really working?
        usersRef.child(userId).child("username")
                .setValue(newName, createCompletionListener());
        username = newName;
    }

    /**
     * Method that allowes one to change rating (Trophies).
     *
     * @param change modifier of trophies
     * @throws DatabaseException in case write to database fails
     */
    public void changeTrophies(int change) throws DatabaseException {
        final int newTrophies = Math.max(0, trophies + change);
        usersRef.child(userId).child("trophies")
                .setValue(newTrophies, createCompletionListener());
        trophies = newTrophies;
    }

    /**
     * Method that allows one to add currency (stars).
     *
     * @param amount positive int
     * @throws IllegalArgumentException in case 'add' is negative
     * @throws DatabaseException        in case write to database fails
     */
    public void addStars(int amount)
            throws IllegalArgumentException, DatabaseException {
        final int newStars = amount + stars;
        if (newStars < 0) {
            throw new IllegalArgumentException("Negative Balance");
        }
        usersRef.child(userId).child("stars")
                .setValue(newStars, createCompletionListener());
        stars = newStars;
    }

    /**
     * Method that allows one to add friends.
     *
     * @param usernameId String specifying FirebaseUser.UID of friend
     * @throws DatabaseException in case write to database fails
     */
    public void addFriend(final String usernameId)
            throws IllegalArgumentException, DatabaseException {
        if (usernameId == null) {
            throw new IllegalArgumentException();
        }
        usersRef.child(userId).child("friends").child(usernameId)
                .setValue(true, createCompletionListener());
    }

    /**
     * Method that allows one to remove friends.
     *
     * @param usernameId String specifying FirebaseUser.UID of friend
     * @throws DatabaseException in case write to database fails
     */
    public void removeFriend(final String usernameId)
            throws IllegalArgumentException, DatabaseException {
        if (usernameId == null) {
            throw new IllegalArgumentException();
        }
        usersRef.child(userId).child("friends").child(usernameId)
                .removeValue(createCompletionListener());
    }

    /**
     * Checks if databaseError occurred.
     *
     * @param databaseError potenial databaseError
     * @throws DatabaseException in case databaseError is non-null
     */
    private void checkForDatabaseError(@Nullable DatabaseError databaseError)
            throws DatabaseException {
        if (databaseError != null) {
            throw databaseError.toException();
        }
    }

    /**
     * Creates a CompletionListener that checks if there was a DatabaseError.
     *
     * @return CompletionListener
     */
    private DatabaseReference.CompletionListener createCompletionListener() {
        return new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError,
                                   @NonNull DatabaseReference databaseReference) {
                checkForDatabaseError(databaseError);
            }
        };
    }

    /**
     *  Upload all the variables of this object to the database.
     */
    public void uploadUser() {
        this.usersRef.child(userId).setValue(this);

    }

    /**
     * Download an object from the database and set the variables of this object.
     */
    public void downloadUser() {

        // Read from the database
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                setValues(map);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value, print error
                Log.d("Account", error.getDetails());
            }
        });

    }

    /**
     * Sets the values of this object.
     * @param map the dictionary with all the variables-
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setValues(Map<String, Object> map) {
        username = (String) map.get("username");
        userId = (String) map.get("id");

        stars = toIntExact( (Long) map.get("stars"));
        trophies = toIntExact((Long) map.get("trophies"));
        matchesWon = toIntExact( (Long) map.get("matchesWon"));
        matchesLost = toIntExact((Long) map.get("matchesLost"));
        averageRating = toIntExact((Long) map.get("averageRating"));
    }

    public int getMatchesWon() {
        return matchesWon;
    }

    public int getMatchesLost() {
        return matchesLost;
    }

    public double getAverageRating() {
        return averageRating;
    }

}