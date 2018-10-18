package ch.epfl.sweng.SDP;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Class that simulates an account.
 */
public class Account implements java.io.Serializable {
    private FirebaseUser firebaseUser;
    private String username;
    private int trophies;
    private int stars;
    private String userId;

    /**
     * Builder for account.
     * @param username String defining the preferred username
     */
    public Account(String username) {
        this(username, 0, 0);
    }
  
    /**
     * Builder for account.
     * @param username String defining the preferred username
     * @param trophies int defining current rating
     * @param stars int defining current currency
     */
    public Account(String username, int trophies, int stars) {
        this.username = username;
        this.trophies = trophies;
        this.stars = stars;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.userId = firebaseUser.getUid();
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

    /**
     * Updates Username to newName.
     * @param newName new username
     * @throws IllegalArgumentException if username not available anymore
     * @throws DatabaseException if problem with firebase
     */
    private void updateUsername(final String newName)
            throws IllegalArgumentException, DatabaseException{
        if (newName == null) {
            throw new IllegalArgumentException();
        }
        Constants.usersRef.orderByChild("username").equalTo(newName)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            throw new IllegalArgumentException("Username already taken.");
                        }
                        else {
                            Constants.databaseRef.child("users").child(userId).child("username")
                                    .setValue(newName, new DatabaseReference.CompletionListener() {

                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError,
                                                               @NonNull DatabaseReference databaseReference) {
                                            checkForDatabaseError(databaseError);
                                            username = newName;
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
    }

    /**
     * Method that allowes one to change rating (Trophies).
     * @param change modifier of trophies
     * @throws DatabaseException in case write to database fails
     */
    public void changeTrophies(int change) throws DatabaseException {
        final int newTrophies = Math.max(0, trophies + change);
        Constants.usersRef.child(userId).child("trophies")
                .setValue(newTrophies, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError,
                                   @NonNull DatabaseReference databaseReference) {
                checkForDatabaseError(databaseError);
                trophies = newTrophies;
            }
        });
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
        Constants.usersRef.child(userId).child("stars")
                .setValue(newStars, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError,
                                   @NonNull DatabaseReference databaseReference) {
                checkForDatabaseError(databaseError);
                stars = newStars;
            }
        });
    }

    /**
     * Method that allows one to add friends.
     * @param usernameId String specifying FirebaseUser.UID of friend
     * @throws DatabaseException in case write to database fails
     */
    public void addFriend(final String usernameId) throws IllegalArgumentException, DatabaseException {
        if (usernameId == null) {
            throw new IllegalArgumentException();
        }
        Constants.usersRef.child(userId).child("friends").child(usernameId)
                .setValue(true, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError,
                                   @NonNull DatabaseReference databaseReference) {
                checkForDatabaseError(databaseError);
            }
        });
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
        Constants.usersRef.child(userId).child("friends").child(usernameId)
                .removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError,
                                   @NonNull DatabaseReference databaseReference) {
                checkForDatabaseError(databaseError);
            }
        });
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
}