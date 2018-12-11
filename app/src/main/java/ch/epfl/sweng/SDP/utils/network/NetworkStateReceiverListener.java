package ch.epfl.sweng.SDP.utils.network;

public interface NetworkStateReceiverListener {
    void networkAvailable();
    void networkUnavailable();
}
