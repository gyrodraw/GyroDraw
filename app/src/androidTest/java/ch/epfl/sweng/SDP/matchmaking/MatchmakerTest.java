package ch.epfl.sweng.SDP.matchmaking;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.sweng.SDP.Account;
import ch.epfl.sweng.SDP.ConstantsWrapper;
import ch.epfl.sweng.SDP.matchmaking.MatchmakingInterface;

import static org.junit.Assert.assertTrue;

import android.support.test.InstrumentationRegistry;

public class MatchmakerTest {

    ConstantsWrapper mockConstantsWrapper;
    DatabaseReference mockReference;
    Query mockQuery;
    Account account;



    @Before
    public void init() {

        Matchmaker.getInstance().testing = true;


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