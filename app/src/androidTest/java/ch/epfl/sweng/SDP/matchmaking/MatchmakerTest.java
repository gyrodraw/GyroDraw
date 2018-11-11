package ch.epfl.sweng.SDP.matchmaking;

import ch.epfl.sweng.SDP.auth.Account;
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
    private static final String FAKE_USERNAME = "IAmNotFake";
    private static final String FAKE_ROOM = "Testroom";
    private ConstantsWrapper mockConstantsWrapper;
    private DatabaseReference mockReference;
    private Task mockTask;
    private Account mockAccount;

    /**
     * Setup up all the mocks.
     */
    @Before
    public void init() {
        mockConstantsWrapper = mock(ConstantsWrapper.class);
        mockReference = mock(DatabaseReference.class);
        mockTask = mock(Task.class);
        mockAccount = mock(Account.class);
        when(mockReference.child(isA(String.class))).thenReturn(mockReference);
        when(mockReference.removeValue()).thenReturn(mockTask);
        when(mockConstantsWrapper.getReference(isA(String.class))).thenReturn(mockReference);
        doNothing().when(mockReference)
                .addListenerForSingleValueEvent(isA(ValueEventListener.class));
    }

    @Test
    public void testJoinRoom() {
        when(mockConstantsWrapper.getFirebaseUserId()).thenReturn(USER_ID);
        when(mockAccount.getUserId()).thenReturn(USER_ID);
        when(mockAccount.getUsername()).thenReturn(FAKE_USERNAME);
        Boolean functionReturnedOK200 = Matchmaker.getInstance(mockAccount)
                .joinRoomOther();
        assertTrue(functionReturnedOK200);
    }

    @Test
    public void testLeaveRoom() {
        when(mockConstantsWrapper.getFirebaseUserId()).thenReturn(USER_ID);
        when(mockAccount.getUserId()).thenReturn(USER_ID);
        when(mockAccount.getUsername()).thenReturn(FAKE_USERNAME);
        Matchmaker.getInstance(mockAccount).leaveRoom(FAKE_ROOM);
    }

    @Test
    public void testJoinRoomWithExceptionThrown() {
        doThrow(IllegalArgumentException.class).when(mockConstantsWrapper).getFirebaseUserId();
        when(mockAccount.getUserId()).thenReturn(USER_ID);
        when(mockAccount.getUsername()).thenReturn(FAKE_USERNAME);
        Matchmaker.getInstance(mockAccount).joinRoom();
    }
}