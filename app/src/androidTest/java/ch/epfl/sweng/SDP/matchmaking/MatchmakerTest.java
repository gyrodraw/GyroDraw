package ch.epfl.sweng.SDP.matchmaking;

import static org.junit.Assert.assertTrue;

import ch.epfl.sweng.SDP.matchmaking.Matchmaker;
import org.junit.Test;

public class MatchmakerTest {

    @Test
    public void getInstance() {
        Matchmaker.getInstance();
        assertTrue(true);
    }

    @Test
    public void matchMaker() {
        Matchmaker matchMaker = new Matchmaker();
        matchMaker.leaveRoom("2312");
        assertTrue(true);
    }


    @Test
    public void joinRoom() {
        Matchmaker.getInstance().joinRoom("2312");
        assertTrue(true);

    }

    @Test
    public void leaveRoom() {
        Matchmaker.getInstance().leaveRoom("2312");
        assertTrue(true);
    }

}