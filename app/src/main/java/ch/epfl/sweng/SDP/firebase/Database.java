package ch.epfl.sweng.SDP.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;

/**
 * Singleton wrapper enum over {@link FirebaseDatabase}.
 */
public enum Database {

    INSTANCE;

    /**
     * Gets and returns the {@link DatabaseReference} associated to the given path.
     * The path can be a single keyword or multiple nested keywords and has the format
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
     * Returns a new {@link DatabaseReferenceBuilder}.
     *
     * @return a DatabaseReferenceBuilder
     */
    public static DatabaseReferenceBuilder constructBuilder() {
        return new DatabaseReferenceBuilder();
    }

    /**
     * Returns a new {@link DatabaseReferenceBuilder} starting from the given reference, used as
     * root.
     *
     * @param initialRef the reference used to start building
     * @return a DatabaseReferenceBuilder
     */
    public static DatabaseReferenceBuilder constructBuilder(DatabaseReference initialRef) {
        return new DatabaseReferenceBuilder(initialRef);
    }

    /**
     * Utility builder for {@link DatabaseReference}.
     */
    public static class DatabaseReferenceBuilder {

        private DatabaseReference ref;

        /**
         * Constructs a builder.
         */
        public DatabaseReferenceBuilder() {
            ref = null;
        }

        /**
         * Constructs a builder starting from the given reference, which will be used as the root.
         *
         * @param initialRef the reference used to start building
         * @throws IllegalArgumentException if the given reference is null
         */
        public DatabaseReferenceBuilder(DatabaseReference initialRef) {
            checkPrecondition(initialRef != null, "initialRef is null");

            ref = initialRef;
        }

        /**
         * Adds a child to the reference under construction.
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
         * Adds multiple children to the reference under construction.
         *
         * @param path the sequence of keys, separated by dots, corresponding to the desired nesting
         *             of children
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

        /**
         * Builds and returns the reference.
         *
         * @return the constructed reference
         */
        public DatabaseReference build() {
            return ref;
        }
    }
}
