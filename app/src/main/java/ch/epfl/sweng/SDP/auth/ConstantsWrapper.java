package ch.epfl.sweng.SDP.auth;

import ch.epfl.sweng.SDP.firebase.Database;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class ConstantsWrapper {

    /**
     * Defines wrapper for testing interaction with Database.
     */
    public ConstantsWrapper() {
        // This constructor does nothing.
    }

    /**
     * Return the {@link DatabaseReference} specified by the given path.
     * @param path the path corresponding to the desired reference
     * @return the desired reference
     */
    public DatabaseReference getReference(String path) {
        return Database.INSTANCE.getReference(path);
    }

    /**
     * Return the current user id.
     * @return the user id
     */
    public String getFirebaseUserId() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return "no_user";
    }
}
