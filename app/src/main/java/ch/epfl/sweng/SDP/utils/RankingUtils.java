package ch.epfl.sweng.SDP.utils;

public class RankingUtils {

    private static final int MAX_RANK = 10;
    private static final int DELTA_RANK = 5;

    private RankingUtils(){}

    /**
     * Generates an array corresponding to the trophy distribution according
     * to the ranking array given as parameter.
     * @param ranking The sorted ranking array corresponding to the stars that each player
     *                earned
     * @return Returns the trophy array
     */
    public static Integer[] generateTrophiesFromRanking(Integer[] ranking) {
        Integer[] trophies = new Integer[ranking.length];
        trophies[0] = MAX_RANK;
        for(int i = 1; i < trophies.length; ++i) {
            if(ranking[i-1].intValue() == ranking[i].intValue()) {
                trophies[i] = trophies[i-1];
            } else {
                trophies[i] = MAX_RANK - DELTA_RANK * i;
            }
        }

        return trophies;
    }

    /**
     * Generates an array corresponding to the final position distribution according
     * to the ranking array given as parameter.
     * @param ranking The sorted ranking array corresponding to the stars that each player
     *                earned
     * @return Returns the position array
     */
    public static Integer[] generatePositionsFromRanking(Integer[] ranking) {
        Integer[] positions = new Integer[ranking.length];
        positions[0] = 1;
        for(int i = 1; i < positions.length; ++i) {
            if(ranking[i-1].intValue() == ranking[i].intValue()) {
                positions[i] = positions[i-1];
            } else {
                positions[i] = i + 1;
            }
        }

        return positions;
    }

    public static String[] addSignToNumber(Integer[] list) {
        String[] numbersWithSign = new String[list.length];

        for(int i = 0; i < numbersWithSign.length; ++i) {
            numbersWithSign[i] = list[i] > 0 ? "+" + String.valueOf(list[i])
                                                    : String.valueOf(list[i]);
        }

        return numbersWithSign;
    }
}
