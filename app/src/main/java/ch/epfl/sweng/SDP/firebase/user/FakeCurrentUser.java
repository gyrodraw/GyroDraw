package ch.epfl.sweng.SDP.firebase.user;

public class FakeCurrentUser extends CurrentUser {

    public static CurrentUser getInstance() {
        return CurrentUser.getInstance(new FakeCurrentUser());
    }

    @Override
    public String getCurrentUserId() {
        return "123456789";
    }
}
