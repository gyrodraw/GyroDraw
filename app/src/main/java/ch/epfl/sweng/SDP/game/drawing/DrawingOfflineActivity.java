package ch.epfl.sweng.SDP.game.drawing;

import static ch.epfl.sweng.SDP.utils.LayoutUtils.isPointInsideView;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.game.drawing.items.Item;
import ch.epfl.sweng.SDP.home.HomeActivity;
import ch.epfl.sweng.SDP.localDatabase.LocalDbForImages;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForImages;
import ch.epfl.sweng.SDP.utils.LayoutUtils;
import java.util.HashMap;
import java.util.Random;

/**
 * Class representing the offline practice mode. The user has the choice of
 * activating or not the mystery mode and the related item generation via a toggle button.
 */
public class DrawingOfflineActivity extends GyroDrawingActivity {

    private static final String TAG = "DrawingOfflineActivity";
    private ImageView mysteryButton;
    private DrawingItems drawingItems;
    private boolean isToggled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawingItems = new DrawingItems(this, (RelativeLayout) findViewById(R.id.paintViewHolder),
                super.paintView, new HashMap<Item, ImageView>(), new Random());
        isToggled = false;
        mysteryButton = findViewById(R.id.mysteryModeButton);
        setExitButtonListener();
        setTypeFace(typeMuro, findViewById(R.id.exit));
    }

    private void setExitButtonListener() {
        TextView exitButton = findViewById(R.id.exit);
        final DrawingOfflineActivity activity = this;

        exitButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        LayoutUtils.pressButton(view, LayoutUtils.AnimMode.CENTER, activity);
                        break;
                    case MotionEvent.ACTION_UP:
                        LayoutUtils.bounceButton(view, activity);
                        saveImageInDb();
                        if (isPointInsideView(event.getRawX(), event.getRawY(), view)) {
                            activity.launchActivity(HomeActivity.class);
                            activity.overridePendingTransition(R.anim.slide_in_left,
                                    R.anim.slide_out_right);
                            activity.finish();
                        }
                        break;
                    default:
                }
                return true;
            }
        });
    }

    private void saveImageInDb() {
        LocalDbForImages localDbHandlerForImages =
                new LocalDbHandlerForImages(this, null, 1);
        paintView.saveCanvasInDb(localDbHandlerForImages);
        Log.d(TAG, "Exiting drawing view");
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
            drawingItems.getPaintViewHolder().removeView(drawingItems.getDisplayedItems()
                    .get(collidingItem));
            drawingItems.getPaintViewHolder().addView(drawingItems.getTextFeedback(collidingItem));
            drawingItems.getDisplayedItems().remove(collidingItem);
        }
    }

    /**
     * Toggles the mystery mode.
     *
     * @param view the toggle button clicked
     */
    public void toggleMysteryMode(View view) {
        isToggled = !isToggled;
        if (isToggled) {
            mysteryButton.setImageResource(R.drawable.mystery_mode_selected);
            drawingItems.generateItemsForOfflineMode();
        } else {
            mysteryButton.setImageResource(R.drawable.mystery_mode);
            drawingItems.stopOfflineModeItemGeneration();
        }
    }

    @VisibleForTesting
    public DrawingItems getDrawingItems() {
        return drawingItems;
    }
}
