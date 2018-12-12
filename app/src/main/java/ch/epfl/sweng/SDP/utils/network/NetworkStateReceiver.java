package ch.epfl.sweng.SDP.utils.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for checking device's connectivity.
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    private Set<NetworkStateReceiverListener> listeners;
    private Boolean connected;

    public NetworkStateReceiver() {
        listeners = new HashSet<>();
        connected = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getExtras() == null) {
            return;
        }

        ConnectivityManager manager = (ConnectivityManager) context
                                        .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = manager.getActiveNetworkInfo();

        if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        } else if (intent.getBooleanExtra(ConnectivityManager
                                            .EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
            connected = false;
        }

        notifyStateToAll();
    }

    private void notifyStateToAll() {
        for(NetworkStateReceiverListener listener : listeners) {
            notifyState(listener);
        }
    }

    private void notifyState(NetworkStateReceiverListener listener) {
        if (connected == null || listener == null) {
            return;
        }

        if (connected) {
            listener.networkAvailable();
        } else {
            listener.networkUnavailable();
        }
    }

    /**
     * Adds a listener to the set of listeners.
     *
     * @param listener Listener to be added
     */
    public void addListener(NetworkStateReceiverListener listener) {
        listeners.add(listener);
        notifyState(listener);
    }

    /**
     * Remove from the listener sets the given listener.
     *
     * @param listener Listener to be removed
     */
    public void removeListener(NetworkStateReceiverListener listener) {
        listeners.remove(listener);
    }

    public Set<NetworkStateReceiverListener> getListeners() {
        return listeners;
    }
}
