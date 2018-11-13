package ch.epfl.sweng.SDP.game.drawing;

import android.util.Log;
import android.view.View;

public class DrawingOffline extends GyroDrawingActivity {

    public void exitClick(View view) {
        Log.d(TAG, "Exiting drawing view");
        this.finish();
    }

}
