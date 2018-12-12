package ch.epfl.sweng.SDP.utils.network;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.VisibleForTesting;

import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.game.WaitingPageActivity;

/**
 * Connectivity wrapper that registers the the network receiver and unregisters it
 */
public final class ConnectivityWrapper {

    private static final String TOP_ROOM_NODE_ID = "realRooms";
    private static NetworkStateReceiver networkStateReceiver = null;

    private ConnectivityWrapper() {}

    private static NetworkStateReceiver getInstanceNetwork() {
        if (networkStateReceiver == null) {
            networkStateReceiver = new NetworkStateReceiver();
        }

        return networkStateReceiver;
    }

    public static void registerNetworkReceiver(Context context) {
        getInstanceNetwork();
        NetworkStateReceiverListener networkStateReceiverListener =
                new NetworkStatusHandler(context);

        networkStateReceiver.addListener(networkStateReceiverListener);
        context.registerReceiver(networkStateReceiver,
                new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public static void unregisterNetworkReceiver(Context context) {
        if(networkStateReceiver != null) {
            context.unregisterReceiver(networkStateReceiver);
            networkStateReceiver = null;
        }
    }

    public static void setOnlineStatusInGame(String roomID, String username) {
        Database.getReference(TOP_ROOM_NODE_ID + "." + roomID + ".onlineStatus."
                + username).setValue(1);
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

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public static void callOnReceiveNetwork(final Context context, final Intent intent) {
        for(NetworkStateReceiverListener listener : networkStateReceiver.getListeners()) {
            networkStateReceiver.removeListener(listener);
        }

        ((WaitingPageActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                networkStateReceiver.addListener(new NetworkStatusHandler(context));
                networkStateReceiver.onReceive(context, intent);
            }
        });
    }
}
