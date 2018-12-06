package ch.epfl.sweng.SDP.utils;

public class TestUsers {

    private static final String[] allTestUsers = new String[]{
            "123456789", "no_user", "userA", "userAA"
    };

    private TestUsers() {
        // This constructor must not be visible.
    }

    /**
     * Checks if the given id belongs to a user created only for testing purposes.
     *
     * @param userId    to check
     * @return          true if id belongs to a test user, else false
     * @throws          IllegalArgumentException if userId is null or empty
     */
    public static boolean isTestUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("UserId must not be null or empty");
        }

        for (int i = 0; i < allTestUsers.length; ++i) {
            if (allTestUsers[i].equals(userId)) {
                return true;
            }
        }
        return false;
    }
}
