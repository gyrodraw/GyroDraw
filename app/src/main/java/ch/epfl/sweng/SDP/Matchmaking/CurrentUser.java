package ch.epfl.sweng.SDP.Matchmaking;

import com.google.firebase.auth.FirebaseAuth;

public class CurrentUser extends User {

    CurrentUser() {
        super(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

}
