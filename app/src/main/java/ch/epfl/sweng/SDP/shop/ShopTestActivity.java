package ch.epfl.sweng.SDP.shop;

import com.google.firebase.database.FirebaseDatabase;

public class ShopTestActivity extends ShopActivity {

    @Override
    public void initializeReferences() {
        db = FirebaseDatabase.getInstance("https://gyrodraw.firebaseio.com/");
        dbRef = db.getReference();
        usersRef = dbRef.child("testUsers");
        currentUser = usersRef.child("testUserOne");
        shopColorsRef = dbRef.child("testItems").child("colors");
    }
}
