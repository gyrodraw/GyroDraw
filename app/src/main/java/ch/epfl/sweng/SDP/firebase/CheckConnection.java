package ch.epfl.sweng.SDP.firebase;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckConnection {

    private CheckConnection(){}

    /**
     * Method to check if device is connected to the internet.
     * @param context Activity that calls this method
     * @return true if device is connected
     */
    public static boolean isOnline(Context context){
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
