package ch.epfl.sweng.SDP.game.drawing;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.home.HomeActivity;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForImages;
import ch.epfl.sweng.SDP.utils.LayoutUtils;

public class DrawingOffline extends GyroDrawingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView exitButton = findViewById(R.id.exit);
        final DrawingOffline activity = this;

        exitButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        LayoutUtils.pressButton(view, LayoutUtils.AnimMode.CENTER, activity);
                        break;
                    case MotionEvent.ACTION_UP:
                        LayoutUtils.bounceButton(view, activity);
                        LocalDbHandlerForImages localDbHandlerForImages =
                                new LocalDbHandlerForImages(activity, null, 1);
                        paintView.saveCanvasInDb(localDbHandlerForImages);
                        Log.d(TAG, "Exiting drawing view");
                        activity.launchActivity(HomeActivity.class);
                        activity.overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_right);
                        activity.finish();
                        break;
                    default:
                }
                return true;
            }
        });
    }
}
