package ch.epfl.sweng.SDP;

import android.app.Activity;
import android.content.Intent;

public interface Utilities {

    static void startActivity(Activity caller, Class activityToStart) {
        Intent intent = new Intent(caller.getApplicationContext(), activityToStart);
        caller.startActivity(intent);
    }

}
