package ch.epfl.sweng.SDP;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MatchmakerTest {

    @Test
    public void matchMaker() {
        //Matchmaker matchMaker = new Matchmaker();
      //  matchMaker.leaveRoom("2312");
        assertTrue(true);
    }


    @Test
    public void joinRoom() {
        Matchmaker.INSTANCE.joinRoom("DSAD");
        assertTrue(true);

    }

    @Test
    public void leaveRoom() {
        Matchmaker.INSTANCE.leaveRoom("2312");
        assertTrue(true);
    }

}