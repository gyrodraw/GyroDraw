package ch.epfl.sweng.SDP;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Class that simulates an account.
 */
public class Account implements java.io.Serializable {
    private String username;
    private int trophies;
    private int stars;
    private String userId;

    /**
     * empty Builder for Firebase support.
     */
    public Account() {

    }

    /**
     * Builder for account.
     * @param username String defining the preferred username
     */
    public Account(String username) {
        if (username == null) throw new NullPointerException();
        this.username = username;
        this.trophies = 0;
        this.stars = 0;
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * Builder for account
     * @param username String defining the preferred username
     * @param trophies int defining current rating
     * @param stars int defining current currency
     */
    public Account(String username, int trophies, int stars) {
        if (username == null) throw new NullPointerException();
        this.username = username;
        this.trophies = trophies;
        this.stars = stars;
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * Is needed for testing.
     * @param userId random id
     */
    public void setUserId(String userId){
        this.userId = userId;
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
     * method that allows one to change the current username to a new one,
     * if and only if the name is available, and then synchronizes the firebase.
     * @param newName String specifying preferred new name
     * @throws IllegalArgumentException in case the name is already taken
     * @throws DatabaseException in case write to database fails
     */
    public void changeUsername(final String newName) throws NullPointerException, IllegalArgumentException, DatabaseException {
        if (newName == null) {
            throw new NullPointerException();
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
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
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
     * method that allowes one to change rating (Trophies).
     * @param change modifier of trophies
     * @throws DatabaseException in case write to database fails
     */
    public void changeTrophies(int change) throws DatabaseException {
        final int newTrophies = Math.max(0, trophies + change);
        Constants.usersRef.child(userId).child("trophies")
                .setValue(newTrophies, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                checkForDatabaseError(databaseError);
                trophies = newTrophies;
            }
        });
    }

    /**
     * method that allows one to add currency (stars).
     * @param add positive int
     * @throws IllegalArgumentException in case 'add' is negative
     * @throws DatabaseException in case write to database fails
     */
    public void addStars(int add) throws IllegalArgumentException, DatabaseException {
        if (add < 0) {
            throw new IllegalArgumentException();
        }
        final int newStars = add + stars;
        Constants.usersRef.child(userId).child("stars")
                .setValue(newStars, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                checkForDatabaseError(databaseError);
                stars = newStars;
            }
        });
    }

    /**
     * method that allows to spend currency (stars).
     * @param sub positive int
     * @throws IllegalArgumentException in case 'sub' is negative or the balance after the transaction would be negative
     * @throws DatabaseException in case write to database fails
     */
    public void subtractStars(int sub) throws IllegalArgumentException, DatabaseException {
        if (sub < 0 || stars - sub < 0) {
            throw new IllegalArgumentException();
        }
        final int newStars = stars -= sub;
        Constants.usersRef.child(userId).child("stars")
                .setValue(newStars, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                checkForDatabaseError(databaseError);
                stars = newStars;
            }
        });
    }

    /**
     * method that allows one to add friends.
     * @param usernameId String specifying FirebaseUser.UID of friend
     * @throws DatabaseException in case write to database fails
     */
    public void addFriend(final String usernameId) throws NullPointerException, DatabaseException {
        if (usernameId == null) {
            throw new NullPointerException();
        }
        Constants.usersRef.child(userId).child("friends").child(usernameId)
                .setValue(true, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                checkForDatabaseError(databaseError);
            }
        });
    }

    /**
     * method that allows one to remove friends.
     * @param usernameId String specifying FirebaseUser.UID of friend
     * @throws DatabaseException in case write to database fails
     */
    public void removeFriend(final String usernameId) throws NullPointerException, DatabaseException {
        if (usernameId == null) {
            throw new NullPointerException();
        }
        Constants.usersRef.child(userId).child("friends").child(usernameId)
                .removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                checkForDatabaseError(databaseError);
            }
        });
    }

    /**
     * checks if databaseError occurred.
     * @param databaseError potenial databaseError
     * @throws DatabaseException in case databaseError is non-null
     */
    private void checkForDatabaseError(@Nullable DatabaseError databaseError) throws DatabaseException {
        if (databaseError != null) {
            throw databaseError.toException();
        }
    }
}