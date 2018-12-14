package ch.epfl.sweng.SDP.home.leagues;

import ch.epfl.sweng.SDP.home.leagues.League;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LeagueUnitTest {
    private static final String NAME = "league1";

    private final League league = League.createLeague1();

    @Test
    public void testContainsOnLowerBound() {
        assertThat(league.contains(0), is(true));
    }

    @Test
    public void testContainsOnUpperBound() {
        assertThat(league.contains(99), is(true));
    }

    @Test
    public void testContainsTrue() {
        assertThat(league.contains(42), is(true));
    }

    @Test
    public void testContainsFalse() {
        assertThat(league.contains(100), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testContainsThrowsExceptionOnNegativeTrophies() {
        league.contains(-42);
    }

    @Test
    public void testGetName() {
        assertThat(league.getName(), is(NAME));
    }
}
