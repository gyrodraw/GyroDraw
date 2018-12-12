package ch.epfl.sweng.SDP.firebase;

import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;
import static java.lang.String.format;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Utility wrapper class over {@link FirebaseDatabase}.
 */
public final class Database {

    private static final DatabaseReference USERS_REFERENCE = Database.getReference("users");

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

    public static void getUsers(ValueEventListener valueEventListener) {
        USERS_REFERENCE.addListenerForSingleValueEvent(valueEventListener);
    }

    public static void getUserById(String id, ValueEventListener valueEventListener) {
        USERS_REFERENCE.child(id).addListenerForSingleValueEvent(valueEventListener);
    }

    public static void getUserByUsername(String username, ValueEventListener valueEventListener) {
        USERS_REFERENCE.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(valueEventListener);
    }

    public static void getUserByEmail(String email, ValueEventListener valueEventListener) {
        USERS_REFERENCE.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(valueEventListener);
    }

    public static void getAllFriends(String userId, ValueEventListener valueEventListener) {
        Database.getReference(format("users.%s.friends", userId))
                .addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Gets data if users are friends, else null. Then applies listener.
     *
     * @param valueEventListener how to handle response
     */
    public static void getFriend(String userId, String friendId,
            ValueEventListener valueEventListener) {
        Database.getReference(format("users.%s.friends.%s", userId, friendId))
                .addListenerForSingleValueEvent(valueEventListener);
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
