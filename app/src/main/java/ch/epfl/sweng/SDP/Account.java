package ch.epfl.sweng.SDP;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


/**
 * Class that simulates an account.
 */
public class Account implements java.io.Serializable {
    private String userId;
    private String username;
    private int trophies;
    private int stars;
    private DatabaseReference usersRef;

    /**
     * Builder for account.
     * @param username String defining the preferred username
     */
    public Account(Constants constants, String username) {
        this(constants, username, 0, 0);
    }
  
    /**
     * Builder for account.
     * @param username String defining the preferred username
     * @param trophies int defining current rating
     * @param stars int defining current currency
     */
    public Account(Constants constants, String username, int trophies, int stars) {
        this.usersRef = constants.getUsersRef();
        this.userId = constants.getFirebaseUserId();
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
     * @param context to set hasAccount to true in.
     */
    public void registerAccount(final Context context){
        usersRef.child(userId).setValue(this,
                new DatabaseReference.CompletionListener() {

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError,
                                           @NonNull DatabaseReference databaseReference)
                                            throws DatabaseException{
                        checkForDatabaseError(databaseError);
                        getDefaultSharedPreferences(context).edit()
                                .putBoolean("hasAccount", true).apply();
                    }
                });
    }

    /**
     * Checks in firebase if username already exists.
     * @param newName username to compare
     * @throws IllegalArgumentException If username is null or already taken
     * @throws DatabaseException If something went wrong with database.
     */
    public void checkIfAccountNameIsFree(final String newName)
            throws IllegalArgumentException, DatabaseException{
        if (newName == null) {
            throw new IllegalArgumentException("Username must not be null");
        }
        usersRef.orderByChild("username").equalTo(newName)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.exists()) {
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
     * @param newName new username
     * @throws IllegalArgumentException if username not available anymore
     * @throws DatabaseException if problem with firebase
     */
    public void updateUsername(final String newName)
            throws IllegalArgumentException, DatabaseException{
        checkIfAccountNameIsFree(newName);
        usersRef.child("users").child(userId).child("username")
                .setValue(newName, new DatabaseReference.CompletionListener() {

                    @Override
                    public void onComplete(DatabaseError databaseError,
                                           DatabaseReference databaseReference) {
                        checkForDatabaseError(databaseError);
                    }
                });
        username = newName;
    }

    /**
     * Method that allowes one to change rating (Trophies).
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
     * @param amount positive int
     * @throws IllegalArgumentException in case 'add' is negative
     * @throws DatabaseException in case write to database fails
     */
    public void addStars(int amount) throws IllegalArgumentException, DatabaseException {
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
     * @param databaseError potenial databaseError
     * @throws DatabaseException in case databaseError is non-null
     */
    private void checkForDatabaseError(@Nullable DatabaseError databaseError)
            throws DatabaseException {
        if (databaseError != null) {
            throw databaseError.toException();
        }
    }

    private DatabaseReference.CompletionListener createCompletionListener(){
        return new DatabaseReference.CompletionListener(){
            @Override
            public void onComplete(@Nullable DatabaseError databaseError,
                                   @NonNull DatabaseReference databaseReference) {
                checkForDatabaseError(databaseError);
            }
        };
    }
}