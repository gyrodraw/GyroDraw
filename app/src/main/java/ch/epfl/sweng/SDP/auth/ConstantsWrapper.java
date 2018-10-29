package ch.epfl.sweng.SDP.auth;

import ch.epfl.sweng.SDP.firebase.Database;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class ConstantsWrapper {

    /**
     * Defines wrapper for testing interaction with Database.
     */
    public ConstantsWrapper() {
        // This constructor does nothing.
    }

    public DatabaseReference getReference(String path) {
        return Database.INSTANCE.getReference(path);
    }

    public String getFirebaseUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
