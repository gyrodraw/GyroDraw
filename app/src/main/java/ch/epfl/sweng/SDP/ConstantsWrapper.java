package ch.epfl.sweng.SDP;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ch.epfl.sweng.SDP.firebase.Database;

public class ConstantsWrapper {
    private DatabaseReference usersRef;
    private FirebaseUser firebaseUser;

    /**
     * Defines wrapper for testing interaction with Database. 
     */
    public ConstantsWrapper() {
        usersRef = Database.INSTANCE.getReference("users");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public DatabaseReference getUsersRef() {
        return usersRef;
    }

    public String getFirebaseUserId(){
        return firebaseUser.getUid();
    }
}
