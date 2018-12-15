package ch.epfl.sweng.SDP.game.drawing;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.game.drawing.items.Item;

import java.util.HashMap;
import java.util.Random;

/**
 * Class representing the drawing phase of an online game in special mode.
 */
public class DrawingOnlineItemsActivity extends DrawingOnlineActivity {

    private DrawingItems drawingItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawingItems = new DrawingItems(this,
                (RelativeLayout) findViewById(R.id.paintViewHolder), super.paintView,
                new HashMap<Item, ImageView>(), new Random());
        drawingItems.generateItems();
    }

    /**
     * Gets called when sensor data changed. Updates the paintViews' circle coordinates
     * and checks if there are collisions with any displayed items.
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
            drawingItems.getPaintViewHolder().removeView(
                    drawingItems.getDisplayedItems().get(collidingItem));
            drawingItems.getPaintViewHolder().addView(
                    FeedbackTextView.itemTextFeedback(collidingItem, paintViewHolder, this));
            drawingItems.getDisplayedItems().remove(collidingItem);
        }
    }

    @VisibleForTesting
    public DrawingItems getDrawingItems() {
        return drawingItems;
    }
}
