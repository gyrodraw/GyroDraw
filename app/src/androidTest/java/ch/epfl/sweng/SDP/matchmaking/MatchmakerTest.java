package ch.epfl.sweng.SDP.matchmaking;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.sweng.SDP.firebase.user.FakeCurrentUser;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MatchmakerTest {

    private static final String USER_ID = "123456789";
    private DatabaseReference mockReference;
    private Task mockTask;

    /**
     * Setup up all the mocks.
     */
    @Before
    public void init() {
        FakeCurrentUser.getInstance();
        mockReference = mock(DatabaseReference.class);
        mockTask = mock(Task.class);
        when(mockReference.child(isA(String.class))).thenReturn(mockReference);
        when(mockReference.removeValue()).thenReturn(mockTask);
        doNothing().when(mockReference)
                .addListenerForSingleValueEvent(isA(ValueEventListener.class));
    }

    @Test
    public void testJoinRoom() {
        Boolean functionReturnedOK200 = Matchmaker.getInstance()
                .joinRoomOther();
        assertTrue(functionReturnedOK200);
    }

    @Test
    public void testLeaveRoom() {
        Matchmaker.getInstance().leaveRoom("Testroom");
    }

    @Test
    public void testLeaveRoomOther() {
        Matchmaker.getInstance().leaveRoomOther("Testroom");
    }

    @Test
    public void testJoinRoomWithExceptionThrown() {
        Matchmaker.getInstance().joinRoom();
    }
}