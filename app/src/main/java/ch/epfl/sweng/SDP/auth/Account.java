package ch.epfl.sweng.SDP.auth;

import static ch.epfl.sweng.SDP.home.League.createLeague1;
import static ch.epfl.sweng.SDP.home.League.createLeague2;
import static ch.epfl.sweng.SDP.home.League.createLeague3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ch.epfl.sweng.SDP.firebase.Database.DatabaseReferenceBuilder;
import ch.epfl.sweng.SDP.home.League;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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
    private int totalMatches;
    private double averageRating;
    private int maxTrophies;

    private DatabaseReference usersRef;

    private LocalDbHandlerForAccount localDbHandler;

    private Account(Context context, ConstantsWrapper constantsWrapper, String username) {
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
        this.matchesWon = 0;
        this.totalMatches = 0;
        this.averageRating = 0.0;
        this.maxTrophies = 0;
    }

    /**
     * Create an account instance. Trophies, stars and statistics are initialized to 0.
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

    public int getMatchesWon() {
        return matchesWon;
    }

    public void setMatchesWon(int matchesWon) {
        this.matchesWon = matchesWon;
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(int totalMatches) {
        this.totalMatches = totalMatches;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getMaxTrophies() {
        return maxTrophies;
    }

    public void setMaxTrophies(int maxTrophies) {
        this.maxTrophies = maxTrophies;
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
    public void updateUsername(final String newName) throws DatabaseException {
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
        DatabaseReferenceBuilder trophiesBuilder = new DatabaseReferenceBuilder(
                usersRef);
        int newTrophies = Math.max(0, trophies + change);
        trophiesBuilder.addChildren(userId + ".trophies").build()
                .setValue(newTrophies, createCompletionListener());
        trophies = newTrophies;

        updateCurrentLeague();

        if (trophies > maxTrophies) {
            DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder(
                    usersRef);
            builder.addChildren(userId + ".maxTrophies").build()
                    .setValue(trophies, createCompletionListener());
            maxTrophies = trophies;
        }

        localDbHandler.saveAccount(instance);
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
    public void changeStars(final int amount) throws DatabaseException {
        int newStars = amount + stars;

        if (newStars < 0) {
            throw new IllegalArgumentException("Negative Balance");
        }

        DatabaseReferenceBuilder starsBuilder = new DatabaseReferenceBuilder(
                usersRef);
        starsBuilder.addChildren(userId + ".stars").build()
                .setValue(newStars, createCompletionListener());
        stars = newStars;

        localDbHandler.saveAccount(instance);
    }

    /**
     * Method that allows one to increase the number of matches won.
     *
     * @throws DatabaseException in case write to database fails
     */
    public void increaseMatchesWon() throws DatabaseException {
        DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder(
                usersRef);
        builder.addChildren(userId + ".matchesWon").build()
                .setValue(++matchesWon, createCompletionListener());

        localDbHandler.saveAccount(instance);
    }

    /**
     * Method that allows one to increase the total number of matches.
     *
     * @throws DatabaseException in case write to database fails
     */
    public void increaseTotalMatches() throws DatabaseException {
        DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder(
                usersRef);
        builder.addChildren(userId + ".totalMatches").build()
                .setValue(++totalMatches, createCompletionListener());

        localDbHandler.saveAccount(instance);
    }

    /**
     * Method that allows one to change the average rating per game given a new rating.
     *
     * The rating passed as parameter should be the average rating obtained after a match.
     *
     * @throws IllegalArgumentException in case a rating <= 0 or > 5 is given
     * @throws DatabaseException in case write to database fails
     */
    public void changeAverageRating(double rating) throws DatabaseException {
        if (!(0 < rating && rating <= 5)) {
            throw new IllegalArgumentException("Wrong rating given");
        }

        double newAverageRating = averageRating == 0 ? rating
                : (averageRating * (totalMatches - 1) + rating) / totalMatches;
        DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder(
                usersRef);
        builder.addChildren(userId + ".averageRating").build()
                .setValue(newAverageRating, createCompletionListener());
        averageRating = newAverageRating;

        localDbHandler.saveAccount(instance);
    }

    /**
     * Method that allows one to add friends.
     *
     * @param usernameId String specifying FirebaseUser.UID of friend
     * @throws IllegalArgumentException in case the given usernameId is null
     * @throws DatabaseException in case write to database fails
     */
    public void addFriend(final String usernameId) throws DatabaseException {
        if (usernameId == null) {
            throw new IllegalArgumentException("Friend's usernameId is null");
        }
        DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder(usersRef);
        builder.addChildren(userId + ".friends." + usernameId).build()
                .setValue(usernameId, createCompletionListener());
        builder = new DatabaseReferenceBuilder(usersRef);
        builder.addChildren(usernameId + ".friends." + userId).build()
                .setValue(userId, createCompletionListener());
    }

    /**
     * Method that allows one to remove friends.
     *
     * @param usernameId String specifying FirebaseUser.UID of friend
     * @throws IllegalArgumentException in case the given usernameId is null
     * @throws DatabaseException in case write to database fails
     */
    public void removeFriend(final String usernameId) throws DatabaseException {
        if (usernameId == null) {
            throw new IllegalArgumentException("Friend's usernameId is null");
        }
        DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder(usersRef);
        builder.addChildren(userId + ".friends." + usernameId).build()
                .removeValue(createCompletionListener());
        builder = new DatabaseReferenceBuilder(usersRef);
        builder.addChildren(usernameId + ".friends." + userId).build()
                .removeValue(createCompletionListener());
    }

    /**
     * Checks if databaseError occurred.
     *
     * @param databaseError potential databaseError
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
     * @return the CompletionListener
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
     * Enable testing.
     */
    public static void enableTesting() {
        testing = true;
    }
}