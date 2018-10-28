package ch.epfl.sweng.SDP.matchmaking;

public enum GameStates {
    HOMESTATE, CHOOSE_WORDS_TIMER_START, START_DRAWING_ACTIVITY,
    START_VOTING_ACTIVITY, END_VOTING_ACTIVITY;

    public static GameStates convertValueIntoState(int value) {
        GameStates state;
        switch(value) {
            case 0: state = HOMESTATE; break;
            case 1: state = CHOOSE_WORDS_TIMER_START; break;
            case 2: state = START_DRAWING_ACTIVITY; break;
            case 3: state = START_VOTING_ACTIVITY; break;
            case 4: state = END_VOTING_ACTIVITY; break;
            default: state = HOMESTATE ; break;
        }

        return state;
    }
}
