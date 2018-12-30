package ch.epfl.sweng.GyroDraw.utils;

/**
 * Utility methods that helps generating positions and trophies distributions.
 */
public final class RankingUtils {

    private static final int MAX_RANK = 10;
    private static final int MIN_RANK = -10;
    private static final int DELTA_RANK = 5;
    private static final int FIRST_POSITION = 1;
    private static final int LAST_POSITION = 5;

    private RankingUtils() {
    }

    /**
     * Generates an array corresponding to the trophy distribution according
     * to the ranking array given as parameter.
     *
     * @param ranking the sorted ranking array corresponding to the stars that each player
     *                earned
     * @return the trophy array
     */
    public static Integer[] generateTrophiesFromRanking(Integer[] ranking) {
        Integer[] trophies = new Integer[ranking.length];

        trophies[0] = ranking[0] >= 0 ? MAX_RANK : MIN_RANK;
        for (int i = 1; i < trophies.length; ++i) {
            if (ranking[i] >= 0 && ranking[i - 1].intValue() == ranking[i].intValue()) {
                trophies[i] = trophies[i - 1];
            } else if (ranking[i] >= 0) {
                trophies[i] = MAX_RANK - DELTA_RANK * i;
            } else {
                trophies[i] = MIN_RANK;
            }
        }

        return trophies;
    }

    /**
     * Generates an array corresponding to the final position distribution according
     * to the ranking array given as parameter.
     *
     * @param ranking the sorted ranking array corresponding to the stars that each player
     *                earned
     * @return the position array
     */
    public static Integer[] generatePositionsFromRanking(Integer[] ranking) {
        Integer[] positions = new Integer[ranking.length];

        positions[0] = ranking[0] >= 0 ? FIRST_POSITION : LAST_POSITION;
        for (int i = 1; i < positions.length; ++i) {
            if (ranking[i] >= 0 && ranking[i - 1].intValue() == ranking[i].intValue()) {
                positions[i] = positions[i - 1];
            } else if (ranking[i] >= 0) {
                positions[i] = i + 1;
            } else {
                positions[i] = LAST_POSITION;
            }
        }

        return positions;
    }

    /**
     * Returns a string array with the sign of the number in front of this latter. In other words
     * the number 10 becomes +10.
     *
     * @param list List of the numbers
     * @return A list of number with the respective sign
     */
    public static String[] addSignToNumberList(Integer[] list) {
        String[] numbersWithSign = new String[list.length];

        for (int i = 0; i < numbersWithSign.length; ++i) {
            numbersWithSign[i] = addSignToNumber(list[i]);
        }

        return numbersWithSign;
    }

    /**
     * Returns a string with the sign of the number in front of this latter. In other words
     * the number 10 becomes +10.
     *
     * @param num An integer
     * @return An integer with the sign
     */
    public static String addSignToNumber(int num) {
        return num >= 0 ? "+" + String.valueOf(num) : String.valueOf(num);
    }
}
