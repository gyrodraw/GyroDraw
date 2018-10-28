package ch.epfl.sweng.SDP.home;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Matchers;
import org.mockito.Mockito;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class UserTest {

    DatabaseReference mockedDatabaseReference;

    @Before
    public void before() {

        mockedDatabaseReference = Mockito.mock(DatabaseReference.class);

        FirebaseDatabase mockedFirebaseDatabase = Mockito.mock(FirebaseDatabase.class);

        when(mockedFirebaseDatabase.getReference()).thenReturn(mockedDatabaseReference);

    }

    @Test
    public void downloadUser() {

      //  when(mockedDatabaseReference.child(anyString())).thenReturn(mockedDatabaseReference);
      //  when(mockedDatabaseReference.addListenerForSingleValueEvent(Matchers.<Class<A>>any())).thenReturn(true);

        assertTrue(true);
    }

    @Test
    public void uploadUser() {
        assertTrue(true);
    }

}