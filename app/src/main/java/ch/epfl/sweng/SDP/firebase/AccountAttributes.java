package ch.epfl.sweng.SDP.firebase;

public enum AccountAttributes {
    USER_ID, USERNAME, EMAIL, STARS, TROPHIES, LEAGUE, MATCHES_WON, MATCHES_TOTAL,
    AVERAGE_RATING, MAX_TROPHIES, FRIENDS, STATUS, BOUGHT_ITEMS;

    static String attributeToPath(AccountAttributes accountAttribute) {
        switch (accountAttribute) {
            case USER_ID:
                return "userId";
            case USERNAME:
                return "userId";
            case EMAIL:
                return "email";
            case STARS:
                return "stars";
            case TROPHIES:
                return "trophies";
            case LEAGUE:
                return "currentLeague";
            case MATCHES_WON:
                return "matchesWon";
            case MATCHES_TOTAL:
                return "totalMatches";
            case AVERAGE_RATING:
                return "averageRating";
            case MAX_TROPHIES:
                return "maxTrophies";
            case FRIENDS:
                return "friends";
            case STATUS:
                return "online";
            case BOUGHT_ITEMS:
                return "boughtItems";
            default:
                throw new IllegalStateException("Unknown attribute");
        }
    }
}
