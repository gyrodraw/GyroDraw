package ch.epfl.sweng.SDP.Matchmaking;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UserTest {

    DatabaseReference mockedDatabaseReference;

    @Before
    public void before() {
        mockedDatabaseReference = Mockito.mock(DatabaseReference.class);



        FirebaseDatabase mockedFirebaseDatabase = Mockito.mock(FirebaseDatabase.class);

        when(mockedFirebaseDatabase.getReference()).thenReturn(mockedDatabaseReference);

     //   PowerMockito.mockStatic(FirebaseDatabase.class);
       // when(FirebaseDatabase.getInstance()).thenReturn(mockedFirebaseDatabase);


    }

    @Test
    public void downloadUser() {

        when(mockedDatabaseReference.child(anyString())).thenReturn(mockedDatabaseReference);
        when(mockedDatabaseReference.addListenerForSingleValueEvent(Matchers.<Class<A>>any())).thenReturn()

        assertTrue(true);
    }

    @Test
    public void uploadUser() {
        assertTrue(true);
    }

}