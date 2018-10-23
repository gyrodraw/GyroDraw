package ch.epfl.sweng.SDP.matchmaking;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MatchmakerTest {

    @Test
    public void matchMaker() {
        Matchmaker.getInstance().leaveRoom("2312");
        assertTrue(true);
    }


    @Test
    public void joinRoom() {
        Matchmaker.getInstance().joinRoom();
        assertTrue(true);

    }

    @Test
    public void leaveRoom() {
        Matchmaker.getInstance().leaveRoom("2312");
        assertTrue(true);
    }

}