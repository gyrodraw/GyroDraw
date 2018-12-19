package ch.epfl.sweng.SDP.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;

/**
 * This class contains the IDs of all users on Firebase that were created for testing purposes. It
 * implements a method for checking if a given user is a real or a test user.
 */
public final class TestUsers {

    private static final List<String> allTestUsers = Collections.unmodifiableList(Arrays.asList(
            "123456789", "1234567891", "FriendId123ForTesting", "no_user",
            "userA", "userAA", "EPFLien"
    ));

    private TestUsers() {
        // This constructor must not be visible.
    }

    /**
     * Checks if the given id belongs to a user created only for testing purposes.
     *
     * @param userId to check
     * @return true if id belongs to a test user, else false
     * @throws IllegalArgumentException if userId is null or empty
     */
    public static boolean isTestUser(String userId) {
        checkPrecondition(userId != null, "UserId must not be null");
        checkPrecondition(!userId.isEmpty(), "UserId must not be empty");

        return allTestUsers.contains(userId);
    }
}
