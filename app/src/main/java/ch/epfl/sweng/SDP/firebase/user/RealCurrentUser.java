package ch.epfl.sweng.SDP.firebase.user;

import com.google.firebase.auth.FirebaseAuth;

public class RealCurrentUser extends CurrentUser {

    public static CurrentUser getInstance() {
        return CurrentUser.getInstance(new RealCurrentUser());
    }

    @Override
    public String getCurrentUserId() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            throw new NullPointerException("No connection to Firebase");
        }
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
