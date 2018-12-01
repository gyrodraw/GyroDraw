package ch.epfl.sweng.SDP.game.drawing;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForImages;
import ch.epfl.sweng.SDP.utils.LayoutUtils;

public class DrawingOffline extends GyroDrawingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutUtils.setSlideLeftExitListener(findViewById(R.id.exit), this);
    }

    /**
     * Function to leave the activity.
     * @param view clicked button
     */
    public void exitClick(View view) {
        LocalDbHandlerForImages localDbHandlerForImages =
                new LocalDbHandlerForImages(this, null, 1);
        paintView.saveCanvasInDb(localDbHandlerForImages);
        Log.d(TAG, "Exiting drawing view");
        this.finish();
    }
}
