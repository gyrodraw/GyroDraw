package ch.epfl.sweng.SDP.home;

/**
 * Enum modelling a friends request state.
 */
public enum FriendsRequestState {
    SENT, RECEIVED, FRIENDS;

    /**
     * Builds a {@link FriendsRequestState} value from the given integer.
     *
     * @param integer the integer corresponding to the desired enum value
     * @return an enum value
     */
    public static FriendsRequestState fromInteger(int integer) {
        switch (integer) {
            case 0:
                return SENT;
            case 1:
                return RECEIVED;
            case 2:
                return FRIENDS;
            default:
                return null;
        }
    }
}
