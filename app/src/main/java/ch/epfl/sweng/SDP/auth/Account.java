package ch.epfl.sweng.SDP.auth;

import static ch.epfl.sweng.SDP.home.League.createLeague;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ch.epfl.sweng.SDP.firebase.Database.DatabaseReferenceBuilder;
import ch.epfl.sweng.SDP.home.League;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


/**
 * Singleton class that simulates an account.
 */
public class Account {

    private static Account instance = null;

    private static final League[] LEAGUES = new League[]{
            createLeague("league1", 0, 99),
            createLeague("league2", 100, 199),
            createLeague("league3", 200, 299)
    };

    private String userId;
    private String username;
    private String currentLeague;
    private int trophies;
    private int stars;
    private DatabaseReference usersRef;

    private Account(ConstantsWrapper constantsWrapper, String username) {
        if (instance != null) {
            throw new IllegalStateException("Already instantiated");
        }

        this.usersRef = constantsWrapper.getUsersRef();
        this.userId = constantsWrapper.getFirebaseUserId();
        this.username = username;
        this.currentLeague = LEAGUES[0].getName();
        this.trophies = 0;
        this.stars = 0;
    }

    /**
     * Create an account instance. Trophies and stars are initialized at 0.
     *
     * @param username string defining the preferred username
     * @throws IllegalArgumentException if username is null
     * @throws IllegalStateException if the account was already instantiated
     */
    public static void createAccount(ConstantsWrapper constantsWrapper, String username) {
        if (username == null) {
            throw new IllegalArgumentException("username is null");
        }

        instance = new Account(constantsWrapper, username);
    }

    public static Account getInstance() {
        if (instance == null) {
            createAccount(new ConstantsWrapper(), "");
        }

        return instance;
    }

//    /**
//     * Refresh the account, updating uninitialized fields. It has to be called before calling other
//     * methods.
//     */
//    public void refreshAccount() {
//        if (usersRef == null) {
//            usersRef = Database.INSTANCE.getReference("users");
//        }
//        if (userId == null) {
//            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        }
//
//        changeTrophies(0);
//        addStars(0);
//    }

    public void setUsersRef(DatabaseReference usersRef) {
        this.usersRef = usersRef;
    }

    public String getUsername() {
        if (username == null) {
            return "testUsername";
        }
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
        if (userId == null) {
            return "testUserId";
        }
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
     * Registers this account in Firebase.
     */
    public void registerAccount() throws DatabaseException {
        usersRef.child(userId).setValue(this, createCompletionListener());
    }

    /**
     * Checks in Firebase if username already exists.
     *
     * @param newName username to compare
     * @throws IllegalArgumentException If username is null or already taken
     * @throws DatabaseException If something went wrong with database.
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
     * Updates username to newName.
     *
     * @param newName new username
     * @throws IllegalArgumentException if username not available anymore
     * @throws DatabaseException if problem with firebase
     */
    public void updateUsername(final String newName)
            throws IllegalArgumentException, DatabaseException {
        checkIfAccountNameIsFree(newName);
        DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder(usersRef);
        builder.addChildren(userId + ".username").build()
                .setValue(newName, createCompletionListener());
        username = newName;
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Method that allows one to add currency (stars).
     *
     * @param amount positive int
     * @throws IllegalArgumentException in case 'add' is negative
     * @throws DatabaseException in case write to database fails
     */
    public void addStars(final int amount)
            throws IllegalArgumentException, DatabaseException {
        DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder(usersRef);
        builder.addChildren(userId + ".stars").build().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Long value = dataSnapshot.getValue(Long.class);
                        if (value != null) {
                            final int newStars = amount + value.intValue();
                            if (newStars < 0) {
                                throw new IllegalArgumentException("Negative Balance");
                            }

                            DatabaseReferenceBuilder starsBuilder = new DatabaseReferenceBuilder(
                                    usersRef);
                            starsBuilder.addChildren(userId + ".stars").build()
                                    .setValue(newStars, createCompletionListener());
                            stars = newStars;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
}