package ch.epfl.sweng.SDP.game.drawing.withItems;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.annotation.VisibleForTesting;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.game.drawing.PaintView;
import ch.epfl.sweng.SDP.game.drawing.items.BumpingItem;
import ch.epfl.sweng.SDP.game.drawing.items.Item;
import ch.epfl.sweng.SDP.game.drawing.items.RandomItemGenerator;

/**
 * Utility class containing methods related to the special items mode.
 */
final class DrawingItems {

    private static final int INTERVAL = 10000;

    private Context context;
    private RelativeLayout paintViewHolder;
    private PaintView paintView;
    private Map<Item, ImageView> displayedItems;
    private Random random;

    private CountDownTimer offlineModeTimer = new CountDownTimer(INTERVAL, INTERVAL) {

        public void onTick(long millisUntilFinished) {
            // Does nothing
        }

        public void onFinish() {
            convertAndAddItemToLayout(RandomItemGenerator.generateItemForOfflineMode(paintView));
            generateItemsForOfflineMode();
        }
    };

    DrawingItems(Context context, RelativeLayout paintViewHolder, PaintView paintView, Map<Item, ImageView> displayedItems, Random random) {
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

    PaintView getPaintView() {
        return paintView;
    }

    /**
     * Check if the paintViews' circle collided with an item.
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
     * Generate a random item every INTERVAL seconds.
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
     * Generate a random item (add stars excluded) every INTERVAL seconds.
     */
    void generateItemsForOfflineMode() {
        offlineModeTimer.start();
    }

    /**
     * Stop the timer and the item generation.
     */
    void stopOfflineModeItemGeneration() {
        offlineModeTimer.cancel();
        for (ImageView item: displayedItems.values()) {
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
     * Convert an item into an ImageView to be displayed on the Activity.
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

    /**
     * Create a text feedback to inform the player which item
     * has been picked up.
     *
     * @param item that was activated
     * @return feedback text
     */
    TextView itemTextFeedback(Item item) {
        final FeedbackTextView feedback = new FeedbackTextView(context);
        feedback.setText(item.textFeedback());

        new CountDownTimer(800, 40) {

            public void onTick(long millisUntilFinished) {
                feedback.setTextSize(60 - millisUntilFinished / 15);
            }

            public void onFinish() {
                paintViewHolder.removeView(feedback);
            }
        }.start();
        return feedback;
    }

    @VisibleForTesting
    void addRandomItemForOfflineMode() {
        convertAndAddItemToLayout(RandomItemGenerator.generateItemForOfflineMode(paintView));
    }

    @VisibleForTesting
    void addRandomItem() {
        convertAndAddItemToLayout(RandomItemGenerator.generateItem(paintView));
    }

    /**
     * Helper class that defines the style of the text feedback.
     */
    private class FeedbackTextView extends android.support.v7.widget.AppCompatTextView {

        private FeedbackTextView(Context context) {
            super(context);
            setTextColor(context.getResources().getColor(R.color.colorDrawYellow));
            setShadowLayer(10, 0, 0, context.getResources().getColor(R.color.colorGrey));
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            setTextSize(1);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            setLayoutParams(layoutParams);
            Typeface typeMuro = Typeface.createFromAsset(context.getAssets(), "fonts/Muro.otf");
            setTypeface(typeMuro, Typeface.ITALIC);
        }
    }
}
