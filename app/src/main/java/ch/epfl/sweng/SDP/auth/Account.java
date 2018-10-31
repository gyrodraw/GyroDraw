package ch.epfl.sweng.SDP.auth;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import ch.epfl.sweng.SDP.firebase.Database.DatabaseReferenceBuilder;
import ch.epfl.sweng.SDP.home.League;
import static ch.epfl.sweng.SDP.home.League.createLeague1;
import static ch.epfl.sweng.SDP.home.League.createLeague2;
import static ch.epfl.sweng.SDP.home.League.createLeague3;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForAccount;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static java.lang.Math.toIntExact;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class that represents an account.
 */
public class Account {

    private static Account instance = null;

    private static final League[] LEAGUES = new League[]{
            createLeague1(),
            createLeague2(),
            createLeague3()
    };

    private static boolean testing = false;

    private String userId;
    private String username;
    private String currentLeague;
    private int trophies;
    private int stars;
    private int matchesWon;
    private int matchesLost;
    private double averageRating;

    private DatabaseReference usersRef;

    private LocalDbHandlerForAccount localDbHandler;

    /**
     * Main account init method.
     * @param context the current context.
     * @param constantsWrapper the constant wrapper.
     * @param username the user name.
     */
    public Account(Context context, ConstantsWrapper constantsWrapper, String username) {
        if (instance != null && !testing) {
            throw new IllegalStateException("Already instantiated");
        }

        this.localDbHandler = new LocalDbHandlerForAccount(context, null, 1);
        this.usersRef = constantsWrapper.getReference("users");
        this.userId = constantsWrapper.getFirebaseUserId();
        this.username = username;
        this.currentLeague = LEAGUES[0].getName();
        this.trophies = 0;
        this.stars = 0;
    }

    /**
     * Offline init method for testing.
     * @param trophies number of trophies.
     * @param stars number of stars.
     * @param matchesLost number of matches lost.
     * @param matchesWon number of matches won.
     */
    public Account(int trophies, int stars, int matchesLost, int matchesWon) {
        this.trophies = trophies;
        this.stars = stars;
        this.matchesLost = matchesLost;
        this.matchesWon = matchesWon;
    }

    /**
     * Create an account instance. Trophies and stars are initialized at 0.
     *
     * @param username string defining the preferred username
     * @throws IllegalArgumentException if username is null
     * @throws IllegalStateException if the account was already instantiated
     */
    public static void createAccount(Context context, ConstantsWrapper constantsWrapper,
            String username) {
        if (username == null) {
            throw new IllegalArgumentException("username is null");
        }

        instance = new Account(context, constantsWrapper, username);
    }

    /**
     * Get the account instance.
     *
     * @param context context calling this method
     * @return the account instance
     */
    public static Account getInstance(Context context) {
        if (instance == null) {
            createAccount(context, new ConstantsWrapper(), "");
        }

        return instance;
    }

    public void setUsersRef(DatabaseReference usersRef) {
        this.usersRef = usersRef;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTrophies() {
        return trophies;
    }

    public void setTrophies(int trophies) {
        this.trophies = trophies;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCurrentLeague() {
        return currentLeague;
    }

    public void setCurrentLeague(String currentLeague) {
        this.currentLeague = currentLeague;
    }

    /**
     * Registers this account in Firebase and in the local database.
     */
    void registerAccount() throws DatabaseException {
        usersRef.child(userId).setValue(this, createCompletionListener());
        localDbHandler.saveAccount(this);
    }

    /**
     * Updates username to newName.
     *
     * @param newName new username
     * @throws IllegalArgumentException if username not available anymore
     * @throws DatabaseException if problems with Firebase
     */
    public void updateUsername(final String newName)
            throws IllegalArgumentException, DatabaseException {
        if (newName == null) {
            throw new IllegalArgumentException("Username must not be null");
        }
        usersRef.orderByChild("username").equalTo(newName)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            throw new IllegalArgumentException("Username already taken.");
                        } else {
                            DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder(
                                    usersRef);
                            builder.addChildren(userId + ".username").build()
                                    .setValue(newName, createCompletionListener());
                            username = newName;
                            localDbHandler.saveAccount(instance);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
    }

    /**
     * Method that allowes one to change trophies.
     *
     * @param change modifier of trophies
     * @throws DatabaseException in case write to database fails
     */
    public void changeTrophies(final int change) throws DatabaseException {
        DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder(usersRef);
        builder.addChildren(userId + ".trophies").build().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Long value = dataSnapshot.getValue(Long.class);
                        if (value != null) {
                            final int newTrophies = Math.max(0, value.intValue() + change);
                            DatabaseReferenceBuilder trophiesBuilder = new DatabaseReferenceBuilder(
                                    usersRef);
                            trophiesBuilder.addChildren(userId + ".trophies").build()
                                    .setValue(newTrophies, createCompletionListener());
                            trophies = newTrophies;

                            updateCurrentLeague();

                            localDbHandler.saveAccount(instance);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
    }

    private void updateCurrentLeague() {
        // Update current league
        DatabaseReferenceBuilder leagueBuilder = new DatabaseReferenceBuilder(
                usersRef);
        for (League league : LEAGUES) {
            if (league.contains(trophies)) {
                currentLeague = league.getName();
            }
        }

        leagueBuilder.addChildren(userId + ".currentLeague").build()
                .setValue(currentLeague, createCompletionListener());
    }

    /**
     * Method that allows one to add currency (stars), both positive or negative.
     *
     * @param amount the amount to add
     * @throws IllegalArgumentException in case the balance becomes negative
     * @throws DatabaseException in case write to database fails
     */
    public void changeStars(final int amount)
            throws IllegalArgumentException, DatabaseException {
        if (stars + amount < 0) {
            throw new IllegalArgumentException("Negative Balance");
        }
        DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder(usersRef);
        builder.addChildren(userId + ".stars").build().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Long value = dataSnapshot.getValue(Long.class);
                        if (value != null) {
                            final int newStars = amount + value.intValue();

                            DatabaseReferenceBuilder starsBuilder = new DatabaseReferenceBuilder(
                                    usersRef);
                            starsBuilder.addChildren(userId + ".stars").build()
                                    .setValue(newStars, createCompletionListener());
                            stars = newStars;

                            localDbHandler.saveAccount(instance);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
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
        DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder(usersRef);
        builder.addChildren(userId + ".friends." + usernameId).build()
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
        DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder(usersRef);
        builder.addChildren(userId + ".friends." + usernameId).build()
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

    /**
     * Enable testing.
     */
    public static void enableTesting() {
        testing = true;
    }

}