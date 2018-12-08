package ch.epfl.sweng.SDP.utils;

import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;
import static java.lang.String.format;

import android.content.Context;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;

/**
 * Enum representing whether the user is online or offline.
 */
public enum OnlineStatus {
    OFFLINE, ONLINE;

    /**
     * Builds a {@link OnlineStatus} value from the given integer.
     *
     * @param integer the integer corresponding to the desired enum value
     * @return an enum value
     * @throws IllegalArgumentException if the given integer does not correspond to a status
     */
    public static OnlineStatus fromInteger(int integer) {
        switch (integer) {
            case 0:
                return OFFLINE;
            case 1:
                return ONLINE;
            default:
                throw new IllegalArgumentException(integer + " does not correspond to a status");
        }
    }

    /**
     * Changes the user online status to the given {@link OnlineStatus} value.
     *
     * @param context the context calling the method
     * @param status the desired status for the user
     * @throws IllegalArgumentException if the context is null or the given status is wrong/unknown
     */
    public static void changeOnlineStatus(Context context, OnlineStatus status) {
        checkPrecondition(context != null, "context is null");
        checkPrecondition(status == OFFLINE || status == ONLINE,
                "Wrong status given");

        Database.getReference(format("users.%s.online", Account.getInstance(context).getUserId()))
                .setValue(status.ordinal());
    }
}
