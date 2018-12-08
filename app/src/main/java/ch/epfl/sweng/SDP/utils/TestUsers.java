package ch.epfl.sweng.SDP.utils;

import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;

/**
 * Contains the IDs of all users on Firebase that were created for testing purposes.
 * Implements a method for checking ig a given user is a real or a test user.
 */
public final class TestUsers {

    private static final String[] allTestUsers = new String[]{
            "123456789", "no_user", "userA", "userAA", "EPFLien"
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
        checkPrecondition(userId != null, "UserId must not be null");
        checkPrecondition(!userId.isEmpty(), "UserId must not be empty");

        for (String testUserId : allTestUsers) {
            if (testUserId.equals(userId)) {
                return true;
            }
        }
        return false;
    }
}
