package ch.epfl.sweng.SDP;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import ch.epfl.sweng.SDP.firebase.Database;

public class ConstantsWrapper {
    private FirebaseUser firebaseUser;

    /**
     * Defines wrapper for testing interaction with Database.
     */
    public ConstantsWrapper() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public DatabaseReference getReference(String path) {
        return Database.INSTANCE.getReference(path);
    }


    /**
     * Get the ID of the connected user.
     * @return Returns the ID of the user or Undefined if its null
     */
    public String getFirebaseUserId(){
        if(firebaseUser != null) {
            return firebaseUser.getUid();
        }
        else {
            // Needed for testing for now
            return "Undefined";
        }
    }
}
