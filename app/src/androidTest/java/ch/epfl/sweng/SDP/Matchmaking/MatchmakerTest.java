package ch.epfl.sweng.SDP.matchmaking;

import android.support.test.InstrumentationRegistry;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.SDP.Account;
import ch.epfl.sweng.SDP.ConstantsWrapper;
import ch.epfl.sweng.SDP.matchmaking.Matchmaker;
import ch.epfl.sweng.SDP.matchmaking.MatchmakingInterface;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;



public class MatchmakerTest {

    ConstantsWrapper mockConstantsWrapper;
    DatabaseReference mockReference;
    Task mockTask;

    @Before
    public void init(){
        mockConstantsWrapper = mock(ConstantsWrapper.class);
        mockReference = mock(DatabaseReference.class);
        mockTask = mock(Task.class);
        when(mockConstantsWrapper.getFirebaseUserId()).thenReturn("123456789");
        when(mockConstantsWrapper.getReference(isA(String.class))).thenReturn(mockReference);
        doNothing().when(mockReference).addListenerForSingleValueEvent(isA(ValueEventListener.class));
        when(mockReference.child(isA(String.class))).thenReturn(mockReference);
        when(mockReference.removeValue()).thenReturn(mockTask);
    }

    @Test
    public void joinRoom() {
        Boolean functionReturnedOK200 = Matchmaker.getInstance(mockConstantsWrapper).joinRoom();
        assertTrue(functionReturnedOK200);
    }

    @Test
    public void leaveRoom() {
        Boolean functionReturned = Matchmaker.getInstance(mockConstantsWrapper).leaveRoom("Testroom");
        assertTrue(functionReturned);
    }


}