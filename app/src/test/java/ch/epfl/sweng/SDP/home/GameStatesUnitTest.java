package ch.epfl.sweng.SDP.home;

import org.junit.Test;

import ch.epfl.sweng.SDP.matchmaking.GameStates;

import static junit.framework.TestCase.assertEquals;

public class GameStatesUnitTest {

    private GameStates gameStates;

    @Test
    public void testConvertIntoEnum() {
        gameStates = GameStates.convertValueIntoState(0);
        assertEquals(gameStates, GameStates.HOMESTATE);

        gameStates = GameStates.convertValueIntoState(1);
        assertEquals(gameStates, GameStates.CHOOSE_WORDS_TIMER_START);

        gameStates = GameStates.convertValueIntoState(2);
        assertEquals(gameStates, GameStates.START_DRAWING_ACTIVITY);

        gameStates = GameStates.convertValueIntoState(3);
        assertEquals(gameStates, GameStates.START_VOTING_ACTIVITY);

        gameStates = GameStates.convertValueIntoState(4);
        assertEquals(gameStates, GameStates.END_VOTING_ACTIVITY);
    }
}
