package ch.epfl.sweng.SDP.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import ch.epfl.sweng.SDP.R;
import org.junit.Test;

public class LayoutUtilsUnitTest {

    @Test
    public void testGetLeagueColorId() {
        assertThat(LayoutUtils.getLeagueColorId(LayoutUtils.LEAGUES[0].getName()),
                is(R.color.colorLeague1));
        assertThat(LayoutUtils.getLeagueColorId(LayoutUtils.LEAGUES[1].getName()),
                is(R.color.colorLeague2));
        assertThat(LayoutUtils.getLeagueColorId(LayoutUtils.LEAGUES[2].getName()),
                is(R.color.colorLeague3));
    }

    @Test
    public void testGetLeagueTextId() {
        assertThat(LayoutUtils.getLeagueTextId(LayoutUtils.LEAGUES[0].getName()),
                is(R.string.league_1));
        assertThat(LayoutUtils.getLeagueTextId(LayoutUtils.LEAGUES[1].getName()),
                is(R.string.league_2));
        assertThat(LayoutUtils.getLeagueTextId(LayoutUtils.LEAGUES[2].getName()),
                is(R.string.league_3));
    }
}
