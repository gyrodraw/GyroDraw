package ch.epfl.sweng.SDP.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database {

    // Singleton class
    private static Database instance;

    private final FirebaseDatabase database = FirebaseDatabase
            .getInstance("https://gyrodraw.firebaseio.com/");

    // Prevent outside creation
    private Database() {
    }

    /**
     * Factory method to create or retrieve the {@link Database} instance.
     * @return the Database instance
     */
    public static Database getInstance() {
        if (Database.instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     * TODO
     * @param path
     * @return
     */
    public DatabaseReference getReference(String path) {
        if (path == null) {
            throw new IllegalArgumentException("path is null");
        }

        String[] keys = path.split("\\.");
        String root = keys[0];
        if (keys.length == 1) {
            return database.getReference(root);
        } else {
            DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder(root);
            for (int i = 1; i < keys.length; i++) {
                String key = keys[i];
                if (key != null) {
                    builder.addKey(key);
                }
            }
            return builder.build();
        }
    }

    private class DatabaseReferenceBuilder {

        private DatabaseReference ref;

        private DatabaseReferenceBuilder(String root) {
            assert root != null : "root is null";
            ref = database.getReference(root);
        }

        private DatabaseReferenceBuilder addKey(String key) {
            assert key != null : "key is null";
            ref = ref.child(key);
            return this;
        }

        private DatabaseReference build() {
            return ref;
        }
    }
}
