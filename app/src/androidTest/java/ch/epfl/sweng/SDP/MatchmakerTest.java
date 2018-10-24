package ch.epfl.sweng.SDP;

import org.junit.Test;

import ch.epfl.sweng.SDP.Matchmaking.*;

import static org.junit.Assert.assertTrue;

public class MatchmakerTest {

    @Test
    public void matchMaker() {
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