package ch.epfl.sweng.SDP;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class Account implements java.io.Serializable {
    public String username;
    public int trophies;
    public int stars;

    public Account() {

    }

    public Account(String username) {
        this.username = username;
        this.trophies = 1200;
        this.stars = 0;
    }

    public void changeUsername(final String newName) throws IllegalArgumentException, DatabaseException {
        Constants.usersRef.orderByChild("username").equalTo(newName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    throw new IllegalArgumentException("Username already taken.");
                }
                else {
                    Constants.databaseRef.child("users").child(getCurrentUserUID()).child("username").setValue(newName, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                throw databaseError.toException();
                            }
                            else {
                                username = newName;
                            }
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
        this.username = newName;
    }

    public void changeTrophies(int a) throws DatabaseException {
        final int newTrophies = Math.max(0, trophies + a);
        Constants.usersRef.child(getCurrentUserUID()).child("trophies").setValue(newTrophies, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    throw databaseError.toException();
                }
                else {
                    trophies = newTrophies;
                }
            }
        });
    }

    public void addStars(int a) throws IllegalArgumentException, DatabaseException {
        if (a < 0) {
            throw new IllegalArgumentException();
        }
        final int newStars = stars += a;
        Constants.usersRef.child(getCurrentUserUID()).child("stars").setValue(newStars, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    throw databaseError.toException();
                }
                else {
                    stars = newStars;
                }
            }
        });
    }

    public void subtractStars(int a) throws IllegalArgumentException, DatabaseException {
        if (a < 0 || stars - a < 0) {
            throw new IllegalArgumentException();
        }
        final int newStars = stars -= a;
        Constants.usersRef.child(getCurrentUserUID()).child("stars").setValue(newStars, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    throw databaseError.toException();
                }
                else {
                    stars = newStars;
                }
            }
        });
    }

    public void addFriend(final String usernameID) throws DatabaseException {
        Constants.usersRef.child(getCurrentUserUID()).child("friends").child(usernameID).setValue(true, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    throw databaseError.toException();
                }
            }
        });
    }

    public void removeFriend(final String usernameID) throws DatabaseException {
        Constants.usersRef.child(getCurrentUserUID()).child("friends").child(usernameID).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    throw databaseError.toException();
                }
            }
        });
    }

    private String getCurrentUserUID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}