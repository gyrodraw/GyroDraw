package ch.epfl.sweng.SDP.firebase;

import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Singleton wrapper enum over {@link FirebaseDatabase}.
 */
public enum Database {

    INSTANCE;

    /**
     * Get and return the {@link DatabaseReference} associated to the given path. The path can be a
     * single keyword or multiple nested keywords and has the format "root.child1.child2...childN".
     *
     * @param path the path to follow inside the database in order to retrieve the reference
     * @return the DatabaseReference associated to the given path
     * @throws IllegalArgumentException if the given string is null
     */
    public DatabaseReference getReference(String path) {
        checkPrecondition(path != null, "path is null");

        DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder();
        return builder.addChildren(path).build();
    }

    /**
     * Utility builder for {@link DatabaseReference}.
     */
    public static class DatabaseReferenceBuilder {

        private DatabaseReference ref;

        /**
         * Construct a builder.
         */
        public DatabaseReferenceBuilder() {
            ref = null;
        }

        /**
         * Construct a builder starting from the given reference, which will be used as the root.
         *
         * @param initialRef the reference used to start building
         * @throws IllegalArgumentException if the given reference is null
         */
        public DatabaseReferenceBuilder(DatabaseReference initialRef) {
            checkPrecondition(initialRef != null, "initialRef is null");

            ref = initialRef;
        }

        /**
         * Add a child to the reference under construction.
         *
         * @param childKey the key corresponding to the child
         * @return the builder
         * @throws IllegalArgumentException if the given key is null
         */
        public DatabaseReferenceBuilder addChild(String childKey) {
            checkPrecondition(childKey != null, "childKey is null");

            if (ref == null) {
                ref = FirebaseDatabase
                        .getInstance("https://gyrodraw.firebaseio.com/").getReference(childKey);
            } else {
                ref = ref.child(childKey);
            }

            return this;
        }

        /**
         * Add multiple children to the reference under construction.
         *
         * @param path the sequence of keys, separated by dots, corresponding to the desired nesting
         * of children
         * @return the builder
         * @throws IllegalArgumentException if the given path is null
         */
        public DatabaseReferenceBuilder addChildren(String path) {
            checkPrecondition(path != null, "path is null");

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

        public DatabaseReference build() {
            return ref;
        }
    }
}
