package ch.epfl.sweng.SDP;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constants {
    public static final FirebaseDatabase database = FirebaseDatabase.getInstance("https://gyrodraw.firebaseio.com/");
    public static final DatabaseReference databaseRef = database.getReference();
    public static final DatabaseReference usersRef = databaseRef.child("users");

    private Constants() {
    }
}
