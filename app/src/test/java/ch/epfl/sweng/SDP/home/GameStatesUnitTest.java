package ch.epfl.sweng.SDP.home;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import ch.epfl.sweng.SDP.matchmaking.GameStates;
import org.junit.Test;

public class GameStatesUnitTest {

    private GameStates gameStates;

    @Test
    public void testConvertIntoEnum() {
        gameStates = GameStates.convertValueIntoState(0);
        assertThat(gameStates, is(GameStates.HOMESTATE));

        gameStates = GameStates.convertValueIntoState(1);
        assertThat(gameStates, is(GameStates.CHOOSE_WORDS_TIMER_START));

        gameStates = GameStates.convertValueIntoState(2);
        assertThat(gameStates, is(GameStates.START_DRAWING_ACTIVITY));

        gameStates = GameStates.convertValueIntoState(3);
        assertThat(gameStates, is(GameStates.WAITING_UPLOAD));

        gameStates = GameStates.convertValueIntoState(4);
        assertThat(gameStates, is(GameStates.START_VOTING_ACTIVITY));

        gameStates = GameStates.convertValueIntoState(5);
        assertThat(gameStates, is(GameStates.END_VOTING_ACTIVITY));
    }
}
