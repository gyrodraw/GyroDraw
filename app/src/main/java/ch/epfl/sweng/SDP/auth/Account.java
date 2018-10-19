package ch.epfl.sweng.SDP.auth;

import static ch.epfl.sweng.SDP.home.League.createLeague;

import ch.epfl.sweng.SDP.home.League;

public class Account {

    private static final League[] LEAGUES = new League[]{createLeague("league1", 0, 99),
            createLeague("league2", 100, 199), createLeague("league3", 200, 299)};
    private int trophies;

    public Account(int trophies) {
        this.trophies = trophies;
    }

    /**
     * Get the user's current league based on his number of trophies.
     *
     * @return the league name
     */
    public String getCurrentLeague() {
        for (League league : LEAGUES) {
            if (league.contains(trophies)) {
                return league.getName();
            }
        }
        return null;
    }
}
