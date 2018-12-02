package ch.epfl.sweng.SDP.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utility class for checking device's connectivity.
 */
public final class CheckConnection {

    private CheckConnection() {
    }

    /**
     * Checks if the device is connected to the Internet.
     *
     * @param context the activity that calls this method
     * @return true if the device is connected, false otherwise
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
