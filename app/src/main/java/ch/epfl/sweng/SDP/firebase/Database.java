package ch.epfl.sweng.SDP.firebase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import static ch.epfl.sweng.SDP.firebase.AccountAttributes.BOUGHT_ITEMS;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.EMAIL;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.FRIENDS;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.STATUS;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.USERNAME;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.attributeToPath;
import static ch.epfl.sweng.SDP.utils.OnlineStatus.OFFLINE;
import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;
import static java.lang.String.format;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.shop.ShopItem;

/**
 * Utility wrapper class over {@link FirebaseDatabase}.
 */
public final class Database {

    private static final DatabaseReference USERS_REFERENCE = Database.getReference("users");
    private static final String USERS_TAG = "users";

    private Database() {
    }

    /**
     * Gets and returns the {@link DatabaseReference} associated to the given path. The path can be
     * a single keyword or multiple nested keywords and has the format
     * "root.child1.child2...childN".
     *
     * @param path the path to follow inside the database in order to retrieve the reference
     * @return the DatabaseReference associated to the given path
     * @throws IllegalArgumentException if the given string is null
     */
    public static DatabaseReference getReference(String path) {
        checkPrecondition(path != null, "path is null");

        DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder();
        return builder.addChildren(path).build();
    }

    public static void saveAccount(Account account) {
        USERS_REFERENCE.child(account.getUserId()).setValue(account, createCompletionListener());
    }

    /**
     * Retrieves a DataSnapshot of all users in the database and applies the given listener.
     *
     * @param valueEventListener action that should be taken after retrieving all users
     */
    public static void getUsers(ValueEventListener valueEventListener) {
        USERS_REFERENCE.addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Retrieves a DataSnapshot of a user with the given id.
     * Applies the listener if the user exists.
     *
     * @param valueEventListener action that should be taken after retrieving the user
     */
    public static void getUserById(String id, ValueEventListener valueEventListener) {
        USERS_REFERENCE.child(id).addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Retrieves a DataSnapshot of a user with the given username.
     * Applies the listener if the user exists.
     *
     * @param username              username of the user to search for
     * @param valueEventListener    action that should be taken after retrieving the user
     */
    public static void getUserByUsername(String username, ValueEventListener valueEventListener) {
        USERS_REFERENCE.orderByChild(attributeToPath(USERNAME)).equalTo(username)
                .addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Retrieves a DataSnapshot of a user with the given email.
     * Applies the listener if the user exists.
     *
     * @param email                 email of the user to search for
     * @param valueEventListener    action that should be taken after retrieving the user
     */
    public static void getUserByEmail(String email, ValueEventListener valueEventListener) {
        USERS_REFERENCE.orderByChild(attributeToPath(EMAIL)).equalTo(email)
                .addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Retrieves a DataSnapshot of all friends from the user with the given id.
     * Applies the listener on the snapshot.
     *
     * @param userId                id of the user whose friends should be retrieved
     * @param valueEventListener    action that should be taken after retrieving the friends
     */
    public static void getAllFriends(String userId, ValueEventListener valueEventListener) {
        Database.getReference(constructUsersPath(userId, attributeToPath(FRIENDS)))
                .addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Gets data if users are friends, else null. Then applies listener.
     *
     * @param valueEventListener how to handle response
     */
    public static void getFriend(String userId, String friendId,
            ValueEventListener valueEventListener) {
        Database.getReference(constructUsersPath(userId, attributeToPath(FRIENDS), friendId))
                .addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Gets an attribute from a given user in the database.
     *
     * @param userId                id of the user to get the attribute from
     * @param attribute             enum to determine which attribute to get
     * @param valueEventListener    listener to handle response
     */
    public static void getAttribute(String userId, AccountAttributes attribute,
                                    ValueEventListener valueEventListener) {
        Database.getReference(constructUsersPath(userId, attributeToPath(attribute)))
                .addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Modifies (or inserts) the value of a given attribute in the database.
     *
     * @param userId                id of the user whose attribute to modify
     * @param attribute             enum to determine which attribute to modify
     * @param newValue              new value to be inserted for attribute
     * @param completionListener    listener to handle response
     */
    public static void setAttribute(String userId, AccountAttributes attribute, Object newValue,
                                    DatabaseReference.CompletionListener completionListener) {
        Database.getReference(constructUsersPath(userId, attributeToPath(attribute)))
                .setValue(newValue, completionListener);
    }

    /**
     * Same method as above, but with a default completionListener.
     *
     * @param userId    id of user whose attribute to modify
     * @param attribute enum to determine which attribute to modify
     * @param newValue  new value to be inserted for attribute
     */
    public static void setAttribute(String userId, AccountAttributes attribute, Object newValue) {
        setAttribute(userId, attribute, newValue, createCompletionListener());
    }

    /**
     * Sets a listener to an attribute of a given user.
     *
     * @param userId                id of user whose attribute will be observed
     * @param attribute             enum to determine which attribute to observe
     * @param valueEventListener    listener to handle response
     */
    public static void setListenerToAttribute(String userId, AccountAttributes attribute,
                                              ValueEventListener valueEventListener) {
        Database.getReference(constructUsersPath(userId, attributeToPath(attribute)))
                .addValueEventListener(valueEventListener);
    }

    /**
     * Updates the friendship status of a friend.
     *
     * @param userId    id of user whose friendship state will be modified
     * @param friendId  id of friend
     * @param newValue  new status of friendship
     */
    public static void setFriendValue(String userId, String friendId, int newValue) {
        Database.getReference(constructUsersPath(userId, attributeToPath(FRIENDS), friendId))
                .setValue(newValue, createCompletionListener());
    }

    /**
     * Removes a friend.
     *
     * @param userId    id of user whose friend will be removed
     * @param friendId  id of friend to be removed
     */
    public static void removeFriend(String userId, String friendId) {
        Database.getReference(constructUsersPath(userId, attributeToPath(FRIENDS), friendId))
                .removeValue(createCompletionListener());
    }

    /**
     * Adds a shopItem to the bought items of a given user.
     *
     * @param userId    id of user that receives the item
     * @param item      item that will be inserted
     */
    public static void setShopItemValue(String userId, ShopItem item) {
        Database.getReference(constructUsersPath(userId, attributeToPath(BOUGHT_ITEMS),
                        item.getColorItem().toString()))
                .setValue(item.getPriceItem(), createCompletionListener());
    }

    /**
     * Sets the online value of a given user to offline upon disconnection.
     *
     * @param userId id of user whose online value will be set to offline upon disconnection
     */
    public static void changeToOfflineOnDisconnect(String userId) {
        Database.getReference(constructUsersPath(userId, attributeToPath(STATUS)))
                .onDisconnect()
                .setValue(OFFLINE.ordinal());
    }

    /**
     * Checks if databaseError occurred.
     *
     * @param databaseError potential databaseError
     * @throws DatabaseException in case databaseError is non-null
     */
    public static void checkForDatabaseError(@Nullable DatabaseError databaseError)
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
    public static DatabaseReference.CompletionListener createCompletionListener() {
        return new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError,
                                   @NonNull DatabaseReference databaseReference) {
                checkForDatabaseError(databaseError);
            }
        };
    }

    private static String constructUsersPath(String... args) {
        StringBuilder builder = new StringBuilder(USERS_TAG);

        for (String arg : args) {
            builder.append("." + arg);
        }

        return builder.toString();
    }

    /**
     * Utility builder for {@link DatabaseReference}.
     */
    private static class DatabaseReferenceBuilder {

        private DatabaseReference ref;

        private DatabaseReferenceBuilder() {
            ref = null;
        }

        /**
         * Adds a child to the reference under construction.
         */
        private DatabaseReferenceBuilder addChild(String childKey) {
            assert childKey != null : "childKey is null";

            if (ref == null) {
                ref = FirebaseDatabase
                        .getInstance("https://gyrodraw.firebaseio.com/").getReference(childKey);
            } else {
                ref = ref.child(childKey);
            }

            return this;
        }

        /**
         * Adds multiple children to the reference under construction.
         */
        private DatabaseReferenceBuilder addChildren(String path) {
            assert path != null : "path is null";

            String[] keys = path.split("\\.");
            String root = keys[0];
            if (keys.length == 1) {
                return addChild(root);
            } else {
                for (String key : keys) {
                    if (key != null) {
                        addChild(key);
                    }
                }
                return this;
            }
        }

        /**
         * Builds and returns the reference.
         */
        private DatabaseReference build() {
            return ref;
        }
    }
}
