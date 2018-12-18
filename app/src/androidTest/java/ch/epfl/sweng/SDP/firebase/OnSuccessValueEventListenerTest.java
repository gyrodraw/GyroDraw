package ch.epfl.sweng.SDP.firebase;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class OnSuccessValueEventListenerTest {

    @Test(expected = DatabaseException.class)
    public void testOnCancelledThrowsException() {
        DatabaseError mockError = Mockito.mock(DatabaseError.class);
        DatabaseException mockException = Mockito.mock(DatabaseException.class);
        when(mockError.toException()).thenReturn(mockException);

        OnSuccessValueEventListener listener = new OnSuccessValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Empty
            }
        };

        listener.onCancelled(mockError);
    }

}
