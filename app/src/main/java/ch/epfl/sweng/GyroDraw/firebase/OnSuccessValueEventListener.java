package ch.epfl.sweng.GyroDraw.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static ch.epfl.sweng.GyroDraw.firebase.FbDatabase.checkForDatabaseError;

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
