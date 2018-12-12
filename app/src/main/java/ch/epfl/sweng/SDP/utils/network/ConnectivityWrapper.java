package ch.epfl.sweng.SDP.utils.network;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.VisibleForTesting;

/**
 * Connectivity wrapper that registers the the network receiver and unregisters it
 */
public final class ConnectivityWrapper {

    private static NetworkStateReceiver networkStateReceiver = null;

    private ConnectivityWrapper() {}

    private static void getInstanceNetwork() {
        if (networkStateReceiver == null) {
            networkStateReceiver = new NetworkStateReceiver();
        }
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

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public static void callOnReceiveNetwork(final Context context, final Intent intent) {
        for(NetworkStateReceiverListener listener : networkStateReceiver.getListeners()) {
            networkStateReceiver.removeListener(listener);
        }

        networkStateReceiver.addListener(new NetworkStatusHandler(context));
        networkStateReceiver.onReceive(context, intent);
    }
}
