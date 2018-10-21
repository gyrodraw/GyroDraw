package ch.epfl.sweng.SDP.home;

/**
 * Class representing a league.
 */
public class League {

    private final String name;
    private final int minTrophies;
    private final int maxTrophies;

    private League(String name, int minTrophies, int maxTrophies) {
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
     * @throws IllegalArgumentException if name is null, if minTrophies or maxTrophies are negative
     * or if minTrophies is greater than maxTrophies
     */
    public static League createLeague(String name, int minTrophies, int maxTrophies) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        } else if (minTrophies < 0) {
            throw new IllegalArgumentException("minTrophies is negative");
        } else if (maxTrophies < 0) {
            throw new IllegalArgumentException("maxTrophies is negative");
        } else if (minTrophies > maxTrophies) {
            throw new IllegalArgumentException("minTrophies is greater than maxTrophies");
        }
        return new League(name, minTrophies, maxTrophies);
    }

    /**
     * Check if the given number of trophies is inside the league's boundaries.
     *
     * @param trophies the number of trophies to check
     * @return true if the league contains the given number of trophies, false otherwise
     */
    public boolean contains(int trophies) {
        if (trophies < 0) {
            throw new IllegalArgumentException("trophies is negative");
        }
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
