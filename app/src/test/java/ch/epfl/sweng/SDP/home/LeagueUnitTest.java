package ch.epfl.sweng.SDP.home;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class LeagueUnitTest {
    private static final String NAME = "test";

    private final League league = League.createLeague(NAME, 0, 99);

    @Test(expected = IllegalArgumentException.class)
    public void testCreateLeagueNullName() {
        League.createLeague(null,0,99);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateLeagueNegativeMinTrophies() {
        League.createLeague("test",-42,99);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateLeagueNegativeMaxTrophies() {
        League.createLeague("test",0,-42);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateLeagueMinTrophiesGreaterThanMaxTrophies() {
        League.createLeague("test",100,99);
    }

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
