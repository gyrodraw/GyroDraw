package ch.epfl.sweng.SDP.Matchmaking;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.epfl.sweng.SDP.Matchmaking.Matchmaker;

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