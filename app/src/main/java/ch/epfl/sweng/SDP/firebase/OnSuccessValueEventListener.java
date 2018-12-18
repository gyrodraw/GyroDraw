package ch.epfl.sweng.SDP.firebase;

import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static ch.epfl.sweng.SDP.firebase.FbDatabase.checkForDatabaseError;

/**
 * This class represents a {@link ValueEventListener} with a default implementation of {@code
 * onCancelled}.
 */
public abstract class OnSuccessValueEventListener implements ValueEventListener {

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        checkForDatabaseError(databaseError);
    }
}
