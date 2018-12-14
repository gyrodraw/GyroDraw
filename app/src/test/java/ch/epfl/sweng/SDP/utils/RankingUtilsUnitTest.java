package ch.epfl.sweng.SDP.utils;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RankingUtilsUnitTest {

    @Test
    public void testGenerateTrophiesFromRanking() {
        Integer[] ranking = new Integer[]{5, 5, 4, 2, 0};

        Integer[] trophies = RankingUtils.generateTrophiesFromRanking(ranking);
        Integer[] result = new Integer[]{10, 10, 0, -5, -10};
        assertThat(trophies, is(result));

        ranking = new Integer[]{5, 5, 5, 5, 5};

        trophies = RankingUtils.generateTrophiesFromRanking(ranking);
        result = new Integer[]{10, 10, 10, 10, 10};

        assertThat(trophies, is(result));
    }

    @Test
    public void testGenerateTrophiesFromRankingWithNegatives() {
        Integer[] ranking = new Integer[] {5, -1, -1, -1, -1};

        Integer[] trophies = RankingUtils.generateTrophiesFromRanking(ranking);
        Integer[] result = new Integer[] {10, -10, -10, -10, -10};
        assertThat(trophies, is(result));

        ranking = new Integer[] {-1, -1, -1, -1, -1};

        trophies = RankingUtils.generateTrophiesFromRanking(ranking);
        result = new Integer[] {-10, -10, -10, -10, -10};

        assertThat(trophies, is(result));
    }

    @Test
    public void testGeneratePositionsFromRankingWithNegatives() {
        Integer[] ranking = new Integer[] {5, -1, -1, -1, -1};

        Integer[] trophies = RankingUtils.generatePositionsFromRanking(ranking);
        Integer[] result = new Integer[] {1, 5, 5, 5, 5};
        assertThat(trophies, is(result));
    }


    @Test
    public void testGeneratePositionsFromRanking() {
        Integer[] ranking = new Integer[]{5, 5, 4, 2, 0};

        Integer[] trophies = RankingUtils.generatePositionsFromRanking(ranking);
        Integer[] result = new Integer[]{1, 1, 3, 4, 5};
        assertThat(trophies, is(result));
    }

    @Test
    public void testAddSignToNumbers() {
        Integer[] input = new Integer[]{10, 10, 0, -5, -10};
        String[] expectedOutput = new String[]{"+10", "+10", "0", "-5", "-10"};

        assertThat(RankingUtils.addSignToNumberList(input), is(expectedOutput));
    }
}
