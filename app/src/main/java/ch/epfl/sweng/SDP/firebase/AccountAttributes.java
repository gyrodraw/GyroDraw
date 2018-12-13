package ch.epfl.sweng.SDP.firebase;

public enum AccountAttributes {
    USER_ID, USERNAME, EMAIL, STARS, TROPHIES, LEAGUE, MATCHES_WON, MATCHES_TOTAL,
    AVERAGE_RATING, MAX_TROPHIES, FRIENDS;

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
            default:
                throw new IllegalStateException("Unknown attribute");
        }
    }
}
