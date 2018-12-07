package ch.epfl.sweng.SDP;

import static ch.epfl.sweng.SDP.utils.OnlineStatus.OFFLINE;
import static ch.epfl.sweng.SDP.utils.OnlineStatus.ONLINE;
import static ch.epfl.sweng.SDP.utils.OnlineStatus.changeOnlineStatus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Background service responsible of updating Firebase Database with the user online status.
 */
public final class OnlineStatusService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        changeOnlineStatus(this, ONLINE);
        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        changeOnlineStatus(this, OFFLINE);
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }
}
