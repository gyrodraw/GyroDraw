package ch.epfl.sweng.SDP.home;

import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;

/**
 * Class representing a league.
 */
public class League {

    private final String name;
    private final int minTrophies;
    private final int maxTrophies;

    private League(String name, int minTrophies, int maxTrophies) {
        assert name != null : "name is null";
        assert minTrophies >= 0 : "minTrophies is negative";
        assert maxTrophies >= 0 : "maxTrophies is negative";
        assert minTrophies <= maxTrophies : "minTrophies is greater than maxTrophies";

        this.name = name;
        this.minTrophies = minTrophies;
        this.maxTrophies = maxTrophies;
    }

    /**
     * Create League 1.
     *
     * @return the desired league.
     */
    public static League createLeague1() {
        return new League("league1", 0, 99);
    }

    /**
     * Create League 2.
     *
     * @return the desired league.
     */
    public static League createLeague2() {
        return new League("league2", 100, 199);
    }

    /**
     * Create League 3.
     *
     * @return the desired league.
     */
    public static League createLeague3() {
        return new League("league3", 200, Integer.MAX_VALUE);
    }

    /**
     * Check if the given number of trophies is inside the league's boundaries.
     *
     * @param trophies the number of trophies to check
     * @return true if the league contains the given number of trophies, false otherwise
     */
    public boolean contains(int trophies) {
        checkPrecondition(trophies >= 0, "trophies is negative");

        return minTrophies <= trophies && trophies <= maxTrophies;
    }

    /**
     * Get the league's name.
     *
     * @return the league's name
     */
    public String getName() {
        return name;
    }
}
