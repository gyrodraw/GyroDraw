package ch.epfl.sweng.SDP;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constants {
    public FirebaseDatabase database;
    public DatabaseReference databaseRef;
    private DatabaseReference usersRef;
    private FirebaseUser firebaseUser;

    /**
     * Defines Constants for Firebase.
     */
    public Constants() {
        this.database = FirebaseDatabase.getInstance("https://gyrodraw.firebaseio.com/");
        this.databaseRef = database.getReference();
        usersRef = databaseRef.child("users");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public DatabaseReference getUsersRef() {
        return usersRef;
    }

    public String getFirebaseUserId(){
        return firebaseUser.getUid();
    }
}
