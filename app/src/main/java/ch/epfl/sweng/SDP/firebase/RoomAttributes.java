package ch.epfl.sweng.SDP.firebase;

public enum RoomAttributes {
    FINISHED, GAME_MODE, ONLINE_STATUS, PLAYING, RANKING, STATE,
    TIMER, UPLOAD_DRAWING, USERS, WORDS;

    static String attributeToPath(RoomAttributes roomAttributes) {
        switch (roomAttributes) {
            case FINISHED:
                return "finished";
            case GAME_MODE:
                return "gameMode";
            case ONLINE_STATUS:
                return "onlineStatus";
            case PLAYING:
                return "playing";
            case RANKING:
                return "ranking";
            case STATE:
                return "state";
            case TIMER:
                return "timer.observableTime";
            case UPLOAD_DRAWING:
                return "uploadDrawing";
            case USERS:
                return "users";
            case WORDS:
                return "words";
            default:
                throw new IllegalArgumentException("Unknown attribute");
        }
    }
}
