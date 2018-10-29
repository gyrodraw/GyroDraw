package ch.epfl.sweng.SDP.matchmaking;

import android.support.test.InstrumentationRegistry;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.SDP.Account;
import ch.epfl.sweng.SDP.ConstantsWrapper;
import ch.epfl.sweng.SDP.matchmaking.Matchmaker;
import ch.epfl.sweng.SDP.matchmaking.MatchmakingInterface;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;



public class MatchmakerTest {

    ConstantsWrapper mockConstantsWrapper;
    DatabaseReference mockReference;
    Query mockQuery;
    Account account;

    MatchmakingInterface mockedmatchmaker;

    @Before
    public void init() {

        Matchmaker.getInstance().testing = true;

        // Use this for mocking purpose
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
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        Boolean functionReturnedOK200 = Matchmaker.getInstance().joinRoom();
        assertTrue(functionReturnedOK200);
    }

    @Test
    public void leaveRoom() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        Boolean funtionReturned = Matchmaker.getInstance().leaveRoom("Testroom");
        assertTrue(funtionReturned);
    }


}