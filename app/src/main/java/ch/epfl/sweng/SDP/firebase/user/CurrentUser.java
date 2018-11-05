package ch.epfl.sweng.SDP.firebase.user;

public abstract class CurrentUser {

    public static CurrentUser instance;

    protected static CurrentUser getInstance(CurrentUser newCurrentUser) {
        if(instance != null) {
            return instance;
        } else {
            instance = newCurrentUser;
            return instance;
        }
    }

    public abstract String getCurrentUserId();
}
