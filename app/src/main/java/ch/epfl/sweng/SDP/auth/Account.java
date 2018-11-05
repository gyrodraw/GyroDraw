package ch.epfl.sweng.SDP.auth;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import ch.epfl.sweng.SDP.firebase.database.Database;
import ch.epfl.sweng.SDP.firebase.user.RealCurrentUser;
import ch.epfl.sweng.SDP.firebase.database.RealDatabase;
import ch.epfl.sweng.SDP.home.League;
import static ch.epfl.sweng.SDP.home.League.createLeague1;
import static ch.epfl.sweng.SDP.home.League.createLeague2;
import static ch.epfl.sweng.SDP.home.League.createLeague3;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForAccount;

import com.google.firebase.database.DatabaseException;

import static java.lang.Math.toIntExact;

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

    private Database database;

    private LocalDbHandlerForAccount localDbHandler;

    /**
     * Main account init method.
     * @param context the current context.
     * @param username the user name.
     */
    public Account(Context context, String username) {
        if (instance != null && !testing) {
            throw new IllegalStateException("Already instantiated");
        }

        this.localDbHandler = new LocalDbHandlerForAccount(context, null, 1);
        this.database = RealDatabase.getInstance();
        this.userId = RealCurrentUser.getInstance().getCurrentUserId();
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
    public static void createAccount(Context context,
            String username) {
        if (username == null) {
            throw new IllegalArgumentException("username is null");
        }

        instance = new Account(context, username);
    }

    /**
     * Get the account instance.
     *
     * @param context context calling this method
     * @return the account instance
     */
    public static Account getInstance(Context context) {
        if (instance == null) {
            createAccount(context, "");
        }

        return instance;
    }

    //public void setUsersRef(DatabaseReference usersRef) {
    //    this.usersRef = usersRef;
    //}

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
        database.setValue("users."+userId, this);
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
        database.containsValue("users." + username + "." + newName, new Runnable() {
            @Override
            public void run() {
                throw new IllegalArgumentException("Username already taken.");
            }
        }, new Runnable() {
            @Override
            public void run() {
                database.setValue("users."+userId+".username", newName);
                username = newName;
                localDbHandler.saveAccount(instance);
            }
        });
    }

    /**
     * Method that allows one to change trophies.
     *
     * @param change modifier of trophies
     * @throws DatabaseException in case write to database fails
     */
    public void changeTrophies(int change) throws DatabaseException {
        trophies = Math.max(this.trophies + change, 0);
        database.setValue("users."+userId+".trophies", trophies);
        updateCurrentLeague();
        localDbHandler.saveAccount(instance);
    }

    private void updateCurrentLeague() {
        // Update current league
        for (League league : LEAGUES) {
            if (league.contains(trophies)) {
                currentLeague = league.getName();
            }
        }
        database.setValue("users."+userId+".currentLeague", currentLeague);
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
        stars += amount;
        database.setValue("Users."+userId+".stars", stars);
        localDbHandler.saveAccount(instance);
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
            throw new IllegalArgumentException("Friends name must not be null");
        }
        database.setValue("users."+userId+".friends."+usernameId, true);
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
        database.removeValue("users."+userId+".friends."+usernameId);
    }

    /**
     * Download an object from the database and set the variables of this object.

    public void downloadUser() {

        // Read from the database
        database.
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
     */

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