package ch.epfl.sweng.SDP.home;

public enum FriendsRequestState {
    SENT, RECEIVED, FRIENDS;

    public static FriendsRequestState fromInteger(int i) {
        switch (i) {
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
