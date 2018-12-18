package ch.epfl.sweng.SDP.firebase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseReference.CompletionListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.shop.ShopItem;

import static ch.epfl.sweng.SDP.firebase.AccountAttributes.BOUGHT_ITEMS;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.EMAIL;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.FRIENDS;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.STATUS;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.USERNAME;
import static ch.epfl.sweng.SDP.firebase.RoomAttributes.FINISHED;
import static ch.epfl.sweng.SDP.firebase.RoomAttributes.RANKING;
import static ch.epfl.sweng.SDP.firebase.RoomAttributes.UPLOAD_DRAWING;
import static ch.epfl.sweng.SDP.firebase.RoomAttributes.USERS;
import static ch.epfl.sweng.SDP.utils.OnlineStatus.OFFLINE;
import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;

/**
 * Utility wrapper class over {@link FirebaseDatabase}.
 */
public final class FbDatabase {

    private static final String USERS_TAG = "users";
    private static final String ROOMS_TAG = "realRooms";

    private static final DatabaseReference USERS_REFERENCE = getReference(USERS_TAG);

    private FbDatabase() {
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

    /**
     * Uploads all attributes of a given account on Firebase Database.
     *
     * @param account the account to be uploaded
     */
    public static void saveAccount(Account account) {
        USERS_REFERENCE.child(account.getUserId()).setValue(account, createCompletionListener());
    }

    /**
     * Retrieves a {@link DataSnapshot} of all users in the database and applies the given
     * listener.
     *
     * @param valueEventListener listener representing the action that should be taken after
     *                           retrieving all users
     */
    public static void getUsers(ValueEventListener valueEventListener) {
        USERS_REFERENCE.addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Gets an attribute from a given user in the database.
     *
     * @param userId             id of the user to get the attribute from
     * @param attribute          which attribute to get
     * @param valueEventListener listener to handle response
     */
    public static void getAccountAttribute(String userId, String attribute,
                                           ValueEventListener valueEventListener) {
        getReference(constructUsersPath(userId, attribute))
                .addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Modifies (or inserts) the value of a given attribute in the database.
     *
     * @param userId             id of the user whose attribute to modify
     * @param attribute          which attribute to modify
     * @param newValue           new value to be inserted for attribute
     * @param completionListener listener to handle response
     */
    public static void setAccountAttribute(
            String userId, String attribute, Object newValue,
            DatabaseReference.CompletionListener completionListener) {
        getReference(constructUsersPath(userId, attribute))
                .setValue(newValue, completionListener);
    }

    /**
     * Same as {@link #setAccountAttribute}, but with a default completionListener.
     *
     * @param userId    id of user whose attribute to modify
     * @param attribute enum to determine which attribute to modify
     * @param newValue  new value to be inserted for attribute
     */
    public static void setAccountAttribute(String userId,
                                           String attribute, Object newValue) {
        setAccountAttribute(userId, attribute, newValue, createCompletionListener());
    }

    /**
     * Removes an attribute from a given user.
     */
    public static void removeAccountAttribute(String userId, String attribute) {
        getReference(constructUsersPath(userId, attribute)).removeValue();
    }

    /**
     * Retrieves a {@link DataSnapshot} of a user with the given id. Applies the listener if the
     * user exists.
     *
     * @param userId             id of user to get
     * @param valueEventListener action that should be taken after retrieving the user
     */
    public static void getUserById(String userId, ValueEventListener valueEventListener) {
        USERS_REFERENCE.child(userId).addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Retrieves a {@link DataSnapshot} of a user with the given username. Applies the listener if
     * the user exists.
     *
     * @param username           username of the user to search for
     * @param valueEventListener action that should be taken after retrieving the user
     */
    public static void getUserByUsername(String username, ValueEventListener valueEventListener) {
        USERS_REFERENCE.orderByChild(USERNAME).equalTo(username)
                .addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Retrieves a {@link DataSnapshot} of a user with the given email. Applies the listener if the
     * user exists.
     *
     * @param email              email of the user to search for
     * @param valueEventListener action that should be taken after retrieving the user
     */
    public static void getUserByEmail(String email, ValueEventListener valueEventListener) {
        USERS_REFERENCE.orderByChild(EMAIL).equalTo(email)
                .addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Retrieves a {@link DataSnapshot} of all friends from the user with the given id. Applies the
     * listener on the snapshot.
     *
     * @param userId             id of the user whose friends should be retrieved
     * @param valueEventListener action that should be taken after retrieving the friends
     */
    public static void getAllFriends(String userId, ValueEventListener valueEventListener) {
        getReference(constructUsersPath(userId, FRIENDS))
                .addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Gets data if users are friends and applies the given listener.
     *
     * @param valueEventListener how to handle response
     */
    public static void getFriend(String userId, String friendId,
                                 ValueEventListener valueEventListener) {
        getReference(constructUsersPath(userId, FRIENDS, friendId))
                .addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Updates the friendship status of a friend.
     *
     * @param userId   id of user whose friendship state will be modified
     * @param friendId id of friend
     * @param newValue new status of friendship
     */
    public static void setFriendValue(String userId, String friendId, int newValue) {
        getReference(constructUsersPath(userId, FRIENDS, friendId))
                .setValue(newValue, createCompletionListener());
    }

    /**
     * Removes a friend.
     *
     * @param userId   id of user whose friend will be removed
     * @param friendId id of friend to be removed
     */
    public static void removeFriend(String userId, String friendId) {
        getReference(constructUsersPath(userId, FRIENDS, friendId))
                .removeValue(createCompletionListener());
    }

    /**
     * Adds a {@link ShopItem} to the bought items of a given user.
     *
     * @param userId id of user that receives the item
     * @param item   item that will be inserted
     */
    public static void setShopItemValue(String userId, ShopItem item) {
        getReference(constructUsersPath(userId, BOUGHT_ITEMS,
                item.getColorItem().toString()))
                .setValue(item.getPriceItem(), createCompletionListener());
    }

    /**
     * Sets a listener to an attribute of a given user.
     *
     * @param userId             id of user whose attribute will be observed
     * @param attribute          enum to determine which attribute to observe
     * @param valueEventListener listener to add
     */
    public static void setListenerToAccountAttribute(String userId, String attribute,
                                                     ValueEventListener valueEventListener) {
        getReference(constructUsersPath(userId, attribute))
                .addValueEventListener(valueEventListener);
    }

    /**
     * Removes the listener to an attribute of a given user.
     *
     * @param userId             id of user whose attribute won't be observed anymore
     * @param attribute          enum to determine which attribute's listener to remove
     * @param valueEventListener listener to remove
     */
    public static void removeListenerFromAccountAttribute(
            String userId, String attribute, ValueEventListener valueEventListener) {
        getReference(constructUsersPath(userId, attribute))
                .removeEventListener(valueEventListener);
    }

    /**
     * Gets an attribute from a given room in the database.
     *
     * @param roomId             id of the room to get the attribute from
     * @param attribute          attribute to get
     * @param valueEventListener listener to run on completion
     */
    public static void getRoomAttribute(String roomId, String attribute,
                                        ValueEventListener valueEventListener) {
        getReference(constructRoomsPath(roomId, attribute))
                .addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Modifies the value associated to a given username in a given attribute of a given room.
     *
     * @param roomId    id of the room where the value will be changed
     * @param username  of the user whose value will change
     * @param attribute of room where to search the user in
     * @param newValue  associated to the user
     */
    public static void setValueToUserInRoomAttribute(String roomId, String username,
                                                     String attribute, Object newValue) {
        getReference(constructRoomsPath(roomId, attribute, username))
                .setValue(newValue);
    }

    /**
     * Removes a user from a given room.
     *
     * @param roomId  id of the room to modify
     * @param account user that should be deleted
     */
    public static void removeUserFromRoom(String roomId, Account account) {
        getReference(constructRoomsPath(roomId, USERS, account.getUserId())).removeValue();
        getReference(constructRoomsPath(roomId, RANKING, account.getUsername())).removeValue();
        getReference(constructRoomsPath(roomId, FINISHED, account.getUsername())).removeValue();
        getReference(constructRoomsPath(roomId, UPLOAD_DRAWING, account.getUsername()))
                .removeValue();
    }

    /**
     * Sets a listener to an attribute of a given room.
     *
     * @param roomId             id of room whose attribute will be observed
     * @param attribute          enum to determine which attribute to observe
     * @param valueEventListener listener to handle response
     */
    public static void setListenerToRoomAttribute(String roomId, String attribute,
                                                  ValueEventListener valueEventListener) {
        getReference(constructRoomsPath(roomId, attribute))
                .addValueEventListener(valueEventListener);
    }

    /**
     * Removes a listener from an attribute of a given room.
     */
    public static void removeListenerFromRoomAttribute(String roomId, String attribute,
                                                       ValueEventListener valueEventListener) {
        getReference(constructRoomsPath(roomId, attribute))
                .removeEventListener(valueEventListener);
    }

    /**
     * Returns the {@link DatabaseReference} of an attribute in a given room.
     */
    public static DatabaseReference getRoomAttributeReference(String roomId, String attribute) {
        return getReference(constructRoomsPath(roomId, attribute));
    }

    /**
     * Sets the online value of a given user to offline upon disconnection.
     *
     * @param userId id of user whose online value will be set to offline upon disconnection
     */
    public static void changeToOfflineOnDisconnect(String userId) {
        FbDatabase.getReference(constructUsersPath(userId, STATUS))
                .onDisconnect()
                .setValue(OFFLINE.ordinal());
    }

    /**
     * Checks if a {@link DatabaseError} occurred.
     *
     * @param databaseError potential databaseError
     */
    public static void checkForDatabaseError(DatabaseError databaseError) {
        throw databaseError.toException();
    }

    /**
     * Creates a {@link CompletionListener} that checks if there was a {@link DatabaseError}.
     *
     * @return the CompletionListener
     */
    public static DatabaseReference.CompletionListener createCompletionListener() {
        return new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError,
                                   @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    checkForDatabaseError(databaseError);
                }
            }
        };
    }

    /**
     * Constructs a path with users at the root.
     */
    private static String constructUsersPath(String... args) {
        return constructPath(USERS_TAG, args);
    }

    /**
     * Constructs a path with rooms at the root.
     */
    private static String constructRoomsPath(String... args) {
        return constructPath(ROOMS_TAG, args);
    }

    /**
     * Constructs a path from a series of string arguments.
     */
    private static String constructPath(String base, String... args) {
        StringBuilder builder = new StringBuilder(base);

        for (String arg : args) {
            builder.append(".").append(arg);
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
