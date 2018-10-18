package ch.epfl.sweng.SDP;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constants {
    public FirebaseDatabase database;
    public DatabaseReference databaseRef;
    private DatabaseReference usersRef;

    public Constants() {
        this.database = FirebaseDatabase.getInstance("https://gyrodraw.firebaseio.com/");
        this.databaseRef = database.getReference();
        usersRef = databaseRef.child("users");
    }

    public DatabaseReference getUsersRef() {
        return usersRef;
    }
}
