package ch.epfl.sweng.SDP.utils.network;

/**
 * Interface modelling a {@link NetworkStateReceiver} listener.
 */
public interface NetworkStateReceiverListener {

    /**
     * TODO
     */
    void networkAvailable();

    /**
     * Displays the disconnected popup dialog.
     */
    void networkUnavailable();
}
