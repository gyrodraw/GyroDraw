package ch.epfl.sweng.SDP.utils.network;

/**
 * Interface modelling a {@link NetworkStateReceiver} listener.
 */
public interface NetworkStateReceiverListener {

    /**
     * This method describes the behavior when a user goes online.
     */
    void networkAvailable();

    /**
     * This method describes the behavior when a user goes offline.
     */
    void networkUnavailable();
}
