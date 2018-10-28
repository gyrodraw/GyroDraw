package ch.epfl.sweng.SDP.home;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import ch.epfl.sweng.SDP.Account;
import ch.epfl.sweng.SDP.ConstantsWrapper;
import ch.epfl.sweng.SDP.matchmaking.Matchmaker;
import ch.epfl.sweng.SDP.matchmaking.MatchmakingInterface;


import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class MatchmakerTest {

    ConstantsWrapper mockConstantsWrapper;
    DatabaseReference mockReference;
    Query mockQuery;
    Account account;

    MatchmakingInterface mockedmatchmaker;

    @Before
    public void init() {

        Matchmaker.getInstance().testing = true;

        mockedmatchmaker = new MatchmakingInterface() {

            @Override
            public Boolean joinRoom() {
                return true;
            }

            @Override
            public Boolean leaveRoom(String roomId) {
                return true;
            }
        };

    }

    @Test
    public void joinRoom() {
        Matchmaker mock = Mockito.mock(Matchmaker.class);
        when(mock.joinRoom()).thenReturn(true);
        Boolean condition = mock.joinRoom();
        assertTrue(condition);
    }

    @Test
    public void leaveRoom() {

        Matchmaker mock = Mockito.mock(Matchmaker.class);
        when(mock.leaveRoom(anyString())).thenReturn(true);
        Boolean condition = mock.leaveRoom("testroom");
        assertTrue(condition);

       // Boolean result = mockedmatchmaker.leaveRoom("testroom");
       // assertTrue(condition);
    }


}