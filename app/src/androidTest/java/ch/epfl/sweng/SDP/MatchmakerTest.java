package ch.epfl.sweng.SDP;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MatchmakerTest {

    @Test
    public void getInstance() {
        Matchmaker.getInstance();
        assertTrue (true);
    }

    @Test
    public void matchMaker() {
        Matchmaker.getInstance();
        assertTrue (true);
    }


    @Test
    public void joinRoom() {
        Matchmaker.getInstance().joinRoom("2312");
        assertTrue (true);

    }

    @Test
    public void leaveRoom() {
        Matchmaker.getInstance().leaveRoom("2312");
        assertTrue (true);
    }

}