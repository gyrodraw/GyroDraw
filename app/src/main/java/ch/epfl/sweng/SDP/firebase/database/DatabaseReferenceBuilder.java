package ch.epfl.sweng.SDP.firebase.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Utility builder for {@link DatabaseReference}.
 */
public class DatabaseReferenceBuilder {

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
        if (initialRef == null) {
            throw new IllegalArgumentException("initialRef is null");
        }
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
        if (childKey == null) {
            throw new IllegalArgumentException("childKey is null");
        }

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
     *             of children
     * @return the builder
     * @throws IllegalArgumentException if the given path is null
     */
    public DatabaseReferenceBuilder addChildren(String path) {
        if (path == null) {
            throw new IllegalArgumentException("path is null");
        }

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