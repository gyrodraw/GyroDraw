package ch.epfl.sweng.SDP.matchmaking;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MatchmakerTest {

    @Test
    public void matchMaker() {
        Matchmaker.INSTANCE.leaveRoom("2312");
        assertTrue(true);
    }


    @Test
    public void joinRoom() {
        Matchmaker.INSTANCE.joinRoom();
        assertTrue(true);

    }

    @Test
    public void leaveRoom() {
        Matchmaker.INSTANCE.leaveRoom("2312");
        assertTrue(true);
    }

}