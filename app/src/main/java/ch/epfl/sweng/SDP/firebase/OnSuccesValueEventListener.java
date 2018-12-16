package ch.epfl.sweng.SDP.firebase;

import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static ch.epfl.sweng.SDP.firebase.FbDatabase.checkForDatabaseError;

public abstract class OnSuccesValueEventListener implements ValueEventListener {

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        checkForDatabaseError(databaseError);
    }
}
