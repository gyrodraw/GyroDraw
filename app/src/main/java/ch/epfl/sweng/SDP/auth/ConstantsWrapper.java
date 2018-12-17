package ch.epfl.sweng.SDP.auth;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Wrapper class useful for testing.
 */
public class ConstantsWrapper {

    /**
     * Defines wrapper for testing interaction with FbDatabase.
     */
    public ConstantsWrapper() {
        // This constructor does nothing.
    }

    /**
     * Returns the current user id.
     *
     * @return the user id
     */
    public String getFirebaseUserId() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return "no_user";
    }
}
