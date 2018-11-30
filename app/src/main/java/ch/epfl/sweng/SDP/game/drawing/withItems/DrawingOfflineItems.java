package ch.epfl.sweng.SDP.game.drawing.withItems;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Random;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.game.drawing.withoutItems.DrawingOffline;
import ch.epfl.sweng.SDP.game.drawing.items.Item;

public class DrawingOfflineItems extends DrawingOffline {

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

    @VisibleForTesting
    public DrawingItems getDrawingItems() {
        return drawingItems;
    }
}
