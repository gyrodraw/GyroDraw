package ch.epfl.sweng.SDP.game.drawing.withoutItems;

import android.util.Log;
import android.view.View;

import ch.epfl.sweng.SDP.game.drawing.GyroDrawingActivity;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForImages;

public class DrawingOffline extends GyroDrawingActivity {

    /**
     * Function to leave the activity.
     * @param view clicked button
     */
    public void exitClick(View view) {
        LocalDbHandlerForImages localDbHandlerForImages =
                new LocalDbHandlerForImages(this, null, 1);
        paintView.saveCanvasInDb(localDbHandlerForImages);
        Log.d(TAG, "Exiting drawing view");
        finish();
    }
}
