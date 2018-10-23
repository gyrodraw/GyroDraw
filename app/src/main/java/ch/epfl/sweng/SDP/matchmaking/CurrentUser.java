package ch.epfl.sweng.SDP.matchmaking;

import com.google.firebase.auth.FirebaseAuth;

public class CurrentUser extends User {

    CurrentUser() {
        super(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

}
