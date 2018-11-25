package ch.epfl.sweng.SDP.auth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.firebase.Database.DatabaseReferenceBuilder;
import ch.epfl.sweng.SDP.home.League;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForAccount;

import static ch.epfl.sweng.SDP.home.FriendsRequestState.FRIENDS;
import static ch.epfl.sweng.SDP.utils.LayoutUtils.LEAGUES;
import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;
import static java.lang.String.format;

/**
 * Singleton class that represents an account.
 */
public class Account {

    private static Account instance = null;

    private String userId;
    private String username;
    private String email;
    private String currentLeague;
    private int trophies;
    private int stars;
    private int matchesWon;
    private int totalMatches;
    private double averageRating;
    private int maxTrophies;

    private DatabaseReference usersRef;

    private LocalDbHandlerForAccount localDbHandler;

    private Account(Context context, ConstantsWrapper constantsWrapper, String username,
                    String email, String currentLeague,
                    int trophies, int stars, int matchesWon, int totalMatches, double averageRating,
                    int maxTrophies) {
        if (instance != null) {
            throw new IllegalStateException("Already instantiated");
        }
        this.localDbHandler = new LocalDbHandlerForAccount(context, null, 1);
        this.usersRef = constantsWrapper.getReference("users");
        this.userId = constantsWrapper.getFirebaseUserId();
        this.username = username;
        this.email = email;
        this.currentLeague = currentLeague;
        this.trophies = trophies;
        this.stars = stars;
        this.matchesWon = matchesWon;
        this.totalMatches = totalMatches;
        this.averageRating = averageRating;
        this.maxTrophies = maxTrophies;
    }

    /**
     * Create an account instance. Trophies, stars and statistics are initialized to 0.
     *
     * @param context          the context in which the method is called
     * @param constantsWrapper the {@link ConstantsWrapper} instance necessary for building the
     *                         instance
     * @param username         the string defining the preferred username
     * @param email            the string defining the user email
     * @throws IllegalArgumentException if one of the parameters is null
     * @throws IllegalStateException    if the account was already instantiated
     */
    public static void createAccount(Context context, ConstantsWrapper constantsWrapper,
                                     String username, String email) {
        createAccount(context, constantsWrapper, username, email, LEAGUES[0].getName(), 0,
                0, 0, 0, 0.0, 0);
    }

    /**
     * Create an account instance given the specified parameters.
     *
     * @param context          the context in which the method is called
     * @param constantsWrapper the {@link ConstantsWrapper} instance necessary for building the
     *                         instance
     * @param username         the string defining the preferred username
     * @param email            the string defining the user's email
     * @param currentLeague    the string defining the user's current league
     * @param trophies         the string defining the user's trophies
     * @param stars            the string defining the user's stars
     * @param matchesWon       the string defining the user's matches won
     * @param totalMatches     the string defining the user's total matches played
     * @param averageRating    the string defining the user's average rating
     * @param maxTrophies      the string defining the user's max trophies achieved
     * @throws IllegalArgumentException if one of the parameters is null or invalid
     * @throws IllegalStateException    if the account was already instantiated
     */
    public static void createAccount(Context context, ConstantsWrapper constantsWrapper,
                                     String username, String email, String currentLeague,
                                     int trophies, int stars, int matchesWon, int totalMatches,
                                     double averageRating, int maxTrophies) {
        checkPrecondition(context != null, "context is null");
        checkPrecondition(constantsWrapper != null, "constantsWrapper is null");
        checkPrecondition(username != null, "username is null");
        checkPrecondition(email != null, "email is null");
        checkPrecondition(currentLeague != null, "currentLeague is null");
        checkPrecondition(trophies >= 0, "trophies is negative");
        checkPrecondition(stars >= 0, "stars is negative");
        checkPrecondition(matchesWon >= 0, "matchesWon is negative");
        checkPrecondition(totalMatches >= 0, "totalMatches is negative");
        checkPrecondition(averageRating >= 0.0, "averageRating is negative");
        checkPrecondition(maxTrophies >= 0, "maxTrophies is negative");

        instance = new Account(context, constantsWrapper, username, email, currentLeague,
                trophies,
                stars, matchesWon, totalMatches, averageRating, maxTrophies);
    }

    /**
     * Get the account instance.
     *
     * @param context context calling this method
     * @return the account instance
     */
    public static Account getInstance(Context context) {
        if (instance == null) {
            createAccount(context, new ConstantsWrapper(), "", "");
        }

        return instance;
    }

    public static void deleteAccount() {
        instance = null;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
     * @throws DatabaseException        if problems with Firebase
     */
    public void updateUsername(final String newName) throws DatabaseException {
        checkPrecondition(newName != null, "Username must not be null");

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
     * Method that allows one to change trophies.
     *
     * @param change modifier of trophies
     * @throws DatabaseException in case write to database fails
     */
    public void changeTrophies(final int change) throws DatabaseException {
        int newTrophies = Math.max(0, trophies + change);
        Database.constructBuilder(usersRef).addChildren(userId + ".trophies").build()
                .setValue(newTrophies, createCompletionListener());
        trophies = newTrophies;

        updateCurrentLeague();

        if (trophies > maxTrophies) {
            Database.constructBuilder(usersRef).addChildren(userId + ".maxTrophies").build()
                    .setValue(trophies, createCompletionListener());
            maxTrophies = trophies;
        }

        localDbHandler.saveAccount(instance);
    }

    private void updateCurrentLeague() {
        // Update current league
        for (League league : LEAGUES) {
            if (league.contains(trophies)) {
                currentLeague = league.getName();
            }
        }

        Database.constructBuilder(usersRef).addChildren(userId + ".currentLeague").build()
                .setValue(currentLeague, createCompletionListener());
    }

    /**
     * Method that allows one to add currency (stars), both positive or negative.
     *
     * @param amount the amount to add
     * @throws IllegalArgumentException in case the balance becomes negative
     * @throws DatabaseException        in case write to database fails
     */
    public void changeStars(final int amount) throws DatabaseException {
        int newStars = amount + stars;
        checkPrecondition(newStars >= 0, "Negative Balance");

        Database.constructBuilder(usersRef).addChildren(userId + ".stars").build()
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
        Database.constructBuilder(usersRef).addChildren(userId + ".matchesWon").build()
                .setValue(++matchesWon, createCompletionListener());

        localDbHandler.saveAccount(instance);
    }

    /**
     * Method that allows one to increase the total number of matches.
     *
     * @throws DatabaseException in case write to database fails
     */
    public void increaseTotalMatches() throws DatabaseException {
        Database.constructBuilder(usersRef).addChildren(userId + ".totalMatches").build()
                .setValue(++totalMatches, createCompletionListener());

        localDbHandler.saveAccount(instance);
    }

    /**
     * Method that allows one to change the average rating per game given a new rating.
     * The rating passed as parameter should be the average rating obtained after a match.
     *
     * @throws IllegalArgumentException in case a rating <= 0 or > 5 is given
     * @throws DatabaseException        in case write to database fails
     */
    public void changeAverageRating(double rating) throws DatabaseException {
        checkPrecondition(0 < rating && rating <= 5, "Wrong rating given");

        double newAverageRating = averageRating == 0 ? rating
                : (averageRating * (totalMatches - 1) + rating) / totalMatches;
        Database.constructBuilder(usersRef).addChildren(userId + ".averageRating").build()
                .setValue(newAverageRating, createCompletionListener());
        averageRating = newAverageRating;

        localDbHandler.saveAccount(instance);
    }

    /**
     * Method that allows one to add friends.
     *
     * @param usernameId String specifying FirebaseUser.UID of friend
     * @throws IllegalArgumentException in case the given usernameId is null
     * @throws DatabaseException        in case write to database fails
     */
    public void addFriend(final String usernameId) throws DatabaseException {
        checkPrecondition(usernameId != null, "Friend's usernameId is null");

        // Update the user's friends' list
        Database.getReference(format("users.%s.friends.%s",
                userId, usernameId)).setValue(FRIENDS.ordinal(), createCompletionListener());

        // Update the sender's friends' list
        Database.getReference(format("users.%s.friends.%s",
                usernameId, userId)).setValue(FRIENDS.ordinal(), createCompletionListener());
    }

    /**
     * Method that allows one to remove friends.
     *
     * @param usernameId String specifying FirebaseUser.UID of friend
     * @throws IllegalArgumentException in case the given usernameId is null
     * @throws DatabaseException        in case write to database fails
     */
    public void removeFriend(final String usernameId) throws DatabaseException {
        checkPrecondition(usernameId != null, "Friend's usernameId is null");

        Database.getReference(format("users.%s.friends.%s",
                userId, usernameId)).removeValue(createCompletionListener());

        Database.getReference(format("users.%s.friends.%s",
                usernameId, userId)).removeValue(createCompletionListener());
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
}