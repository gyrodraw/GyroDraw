package ch.epfl.sweng.SDP.game.drawing;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Random;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.game.drawing.items.Item;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForImages;

/**
 * Class representing the practice mode, which is offline and where the user has the choice of
 * activating or not the mystery mode and the related item generation.
 */
public class DrawingOffline extends GyroDrawingActivity {

    private DrawingItems drawingItems;
    private boolean isToggled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawingItems = new DrawingItems(this,
                (RelativeLayout) findViewById(R.id.paintViewHolder), super.paintView,
                new HashMap<Item, ImageView>(), new Random());
        isToggled = false;
    }

    /**
     * Get called when sensor data changed. Update the paintViews' circle coordinates
     * and check if there are collisions with any displayed items.
     * If there is, the item gets activated and removed from the displayedItems.
     *
     * @param coordinateX new X coordinate for paintView
     * @param coordinateY new Y coordinate for paintView
     */
    @Override
    public void updateValues(float coordinateX, float coordinateY) {
        super.updateValues(coordinateX, coordinateY);

        Item collidingItem = drawingItems.findCollidingElement();

        if (collidingItem != null) {
            collidingItem.activate(paintView);
            drawingItems.getPaintViewHolder().removeView(drawingItems.getDisplayedItems().get(collidingItem));
            drawingItems.getPaintViewHolder().addView(drawingItems.itemTextFeedback(collidingItem));
            drawingItems.getDisplayedItems().remove(collidingItem);
        }
    }

    /**
     * Toggle the mystery mode.
     *
     * @param view the toggle button clicked
     */
    public void toggleMysteryMode(View view) {
        isToggled = !isToggled;
        if (isToggled) {
            drawingItems.generateItemsForOfflineMode();
        } else {
            drawingItems.stopOfflineModeItemGeneration();
        }
    }

    /**
     * Leave the activity.
     *
     * @param view the button clicked
     */
    public void exitClick(View view) {
        LocalDbHandlerForImages localDbHandlerForImages =
                new LocalDbHandlerForImages(this, null, 1);
        paintView.saveCanvasInDb(localDbHandlerForImages);
        Log.d(TAG, "Exiting drawing view");
        finish();
    }

    @VisibleForTesting
    public DrawingItems getDrawingItems() {
        return drawingItems;
    }
}
