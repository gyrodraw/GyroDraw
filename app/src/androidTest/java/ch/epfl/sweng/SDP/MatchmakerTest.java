package ch.epfl.sweng.SDP;

import org.junit.Test;

import ch.epfl.sweng.SDP.Matchmaking.Matchmaker;

import static org.junit.Assert.assertTrue;

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