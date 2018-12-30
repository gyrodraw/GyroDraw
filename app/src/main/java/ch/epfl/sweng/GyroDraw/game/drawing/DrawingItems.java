package ch.epfl.sweng.GyroDraw.game.drawing;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.CountDownTimer;
import android.support.annotation.VisibleForTesting;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ch.epfl.sweng.GyroDraw.R;
import ch.epfl.sweng.GyroDraw.game.drawing.items.BumpingItem;
import ch.epfl.sweng.GyroDraw.game.drawing.items.Item;
import ch.epfl.sweng.GyroDraw.game.drawing.items.Items;
import ch.epfl.sweng.GyroDraw.game.drawing.items.RandomItemGenerator;

/**
 * Utility class containing methods related to the special items mode.
 */
final class DrawingItems {

    private static final int INTERVAL = 10000;

    private final Context context;
    private RelativeLayout paintViewHolder;
    private PaintView paintView;
    private Map<Item, ImageView> displayedItems;
    private final Random random;

    private CountDownTimer offlineModeTimer = new CountDownTimer(INTERVAL, INTERVAL) {

        public void onTick(long millisUntilFinished) {
            // Does nothing
        }

        public void onFinish() {
            convertAndAddItemToLayout(RandomItemGenerator.generateItemForOfflineMode(paintView));
            generateItemsForOfflineMode();
        }
    };

    DrawingItems(Context context, RelativeLayout paintViewHolder, PaintView paintView,
                 Map<Item, ImageView> displayedItems, Random random) {
        this.context = context;
        this.paintViewHolder = paintViewHolder;
        this.paintView = paintView;
        this.displayedItems = new HashMap<>(displayedItems);
        this.random = random;
    }

    RelativeLayout getPaintViewHolder() {
        return paintViewHolder;
    }

    Map<Item, ImageView> getDisplayedItems() {
        return displayedItems;
    }

    @VisibleForTesting
    PaintView getPaintView() {
        return paintView;
    }

    /**
     * Returns the feedback {@link TextView} of the given item.
     *
     * @param item the given item
     * @return the associated feedback TextView
     */
    TextView getTextFeedback(Item item) {
        return FeedbackTextView.itemTextFeedback(item, paintViewHolder, context);
    }

    /**
     * Checks if the paintViews' circle collided with an item.
     *
     * @return item that collided, or null.
     */
    Item findCollidingElement() {
        for (Item item : displayedItems.keySet()) {
            if (item.collision(paintView)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Generates a random item every INTERVAL seconds.
     */
    void generateItems() {
        new CountDownTimer(INTERVAL, INTERVAL) {

            public void onTick(long millisUntilFinished) {
                // Does nothing
            }

            public void onFinish() {
                convertAndAddItemToLayout(RandomItemGenerator.generateItem(paintView));
                generateItems();
            }
        }.start();
    }

    /**
     * Generates a random item ({@link Items#ADD_STARS} excluded) every INTERVAL seconds.
     */
    void generateItemsForOfflineMode() {
        offlineModeTimer.start();
    }

    /**
     * Stops the timer and the item generation.
     */
    void stopOfflineModeItemGeneration() {
        offlineModeTimer.cancel();
        for (ImageView item : displayedItems.values()) {
            paintViewHolder.removeView(item);
        }
        displayedItems.clear();

    }

    private void convertAndAddItemToLayout(Item item) {
        ImageView imageView = itemToImageView(item);
        paintViewHolder.addView(imageView);
        displayedItems.put(item, imageView);
    }

    /**
     * Converts an item into an {@link ImageView} to be displayed on the activity.
     *
     * @param item to be converted
     * @return ImageView of the item
     */
    private ImageView itemToImageView(Item item) {
        ImageView view = new ImageView(context);
        view.setX(item.getX() - item.getRadius());
        view.setY(item.getY() - item.getRadius());
        view.setLayoutParams(new RelativeLayout.LayoutParams(
                2 * item.getRadius(),
                2 * item.getRadius()));
        view.setImageResource(R.drawable.mystery_box);

        int color = Color.rgb(getRandomByte(), getRandomByte(), getRandomByte());
        view.setColorFilter(new LightingColorFilter(Color.WHITE, color));

        if (item instanceof BumpingItem) {
            ((BumpingItem) item).setImageView(view);
        }
        return view;
    }

    private int getRandomByte() {
        return random.nextInt(155) + 100;
    }

    @VisibleForTesting
    void addRandomItemForOfflineMode() {
        convertAndAddItemToLayout(RandomItemGenerator.generateItemForOfflineMode(paintView));
    }

    @VisibleForTesting
    void addRandomItem() {
        convertAndAddItemToLayout(RandomItemGenerator.generateItem(paintView));
    }
}
