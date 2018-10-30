package ch.epfl.sweng.SDP;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static java.lang.Math.toIntExact;

public class EventListener implements ValueEventListener {

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // This method is called once with the initial value and again
        // whenever data at this location is updated.
        HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
        // Update room
    }

    @Override
    public void onCancelled(DatabaseError error) {
        // Failed to read value
    }

}
