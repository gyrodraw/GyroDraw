package ch.epfl.sweng.SDP.Matchmaking;

import com.google.firebase.auth.FirebaseAuth;

public class CurrentUser extends User {

    private static User singleUser = null;

    /**
     * Create and returns a singleton instance of this class
     * @return singleton
     */
    public static User getInstance()
    {
        if (singleUser == null) {
            singleUser = new User();
        }

        return singleUser;
    }

}
