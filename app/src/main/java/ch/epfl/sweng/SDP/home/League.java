package ch.epfl.sweng.SDP.home;

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
        assert maxTrophies >= minTrophies : "minTrophies greater than maxTrophies";

        this.name = name;
        this.minTrophies = minTrophies;
        this.maxTrophies = maxTrophies;
    }

    /**
     * Create a {@link League}.
     *
     * @param name the name of the league
     * @param minTrophies the minimum number of trophies needed to enter the league
     * @param maxTrophies the maximum number of trophies before passing to the next league
     * @return the desired League
     */
    public static League createLeague(String name, int minTrophies, int maxTrophies) {
        return new League(name, minTrophies, maxTrophies);
    }

    /**
     * Check if the given number of trophies is inside the league's boundaries.
     *
     * @param trophies the number of trophies to check
     * @return true if the league contains the given number of trophies, false otherwise
     */
    public boolean contains(int trophies) {
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
