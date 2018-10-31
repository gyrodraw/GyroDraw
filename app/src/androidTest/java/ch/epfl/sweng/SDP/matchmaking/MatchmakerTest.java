package ch.epfl.sweng.SDP.matchmaking;

import ch.epfl.sweng.SDP.auth.ConstantsWrapper;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MatchmakerTest {

    private static final String USER_ID = "123456789";
    private ConstantsWrapper mockConstantsWrapper;
    private DatabaseReference mockReference;
    private Task mockTask;

    /**
     * Setup up all the mocks.
     */
    @Before
    public void init() {
        mockConstantsWrapper = mock(ConstantsWrapper.class);
        mockReference = mock(DatabaseReference.class);
        mockTask = mock(Task.class);
        when(mockReference.child(isA(String.class))).thenReturn(mockReference);
        when(mockReference.removeValue()).thenReturn(mockTask);
        when(mockConstantsWrapper.getReference(isA(String.class))).thenReturn(mockReference);
        doNothing().when(mockReference)
                .addListenerForSingleValueEvent(isA(ValueEventListener.class));
    }

    @Test
    public void testJoinRoom() {
        when(mockConstantsWrapper.getFirebaseUserId()).thenReturn(USER_ID);
        Boolean functionReturnedOK200 = Matchmaker.getInstance(mockConstantsWrapper)
                .joinRoomOther();
        assertTrue(functionReturnedOK200);
    }

    @Test
    public void testLeaveRoom() {
        when(mockConstantsWrapper.getFirebaseUserId()).thenReturn(USER_ID);
        Matchmaker.getInstance(mockConstantsWrapper).leaveRoom("Testroom");
    }

    @Test
    public void testLeaveRoomOther() {
        when(mockConstantsWrapper.getFirebaseUserId()).thenReturn(USER_ID);
        Matchmaker.getInstance(mockConstantsWrapper).leaveRoomOther("Testroom");
    }

    @Test
    public void testJoinRoomWithExceptionThrown() {
        doThrow(IllegalArgumentException.class).when(mockConstantsWrapper).getFirebaseUserId();
        Matchmaker.getInstance(mockConstantsWrapper).joinRoom();
    }
}