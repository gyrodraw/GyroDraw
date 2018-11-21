package ch.epfl.sweng.SDP.game.drawing;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.game.drawing.items.BumpingItem;
import ch.epfl.sweng.SDP.game.drawing.items.Item;
import ch.epfl.sweng.SDP.game.drawing.items.RandomItemGenerator;

public class DrawingOfflineItems extends DrawingOffline {

    private static final int INTERVAL = 10000;

    protected RelativeLayout paintViewHolder;
    protected PaintView paintView;
    private Map<Item, ImageView> displayedItems;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paintViewHolder = findViewById(R.id.paintViewHolder);
        paintView = super.paintView;
        displayedItems = new HashMap<>();
        generateItems();
    }

    protected HashMap<Item, ImageView> getDisplayedItems() {
        return new HashMap<>(displayedItems);
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
        paintView.updateCoordinates(coordinateX, coordinateY);

        Item collidingItem = findCollidingElement();

        if (collidingItem != null) {
            collidingItem.activate(paintView);
            paintViewHolder.removeView(displayedItems.get(collidingItem));
            paintViewHolder.addView(itemTextFeedback(collidingItem));
            displayedItems.remove(collidingItem);
        }
    }

    /**
     * Checks if the paintViews' circle collided with an item.
     *
     * @return item that collided, or null.
     */
    private Item findCollidingElement() {
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
    private void generateItems() {
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

    private void convertAndAddItemToLayout(Item item) {
        ImageView imageView = itemToImageView(item);
        paintViewHolder.addView(imageView);
        displayedItems.put(item, imageView);
    }

    /**
     * Converts an item into an ImageView to be displayed on the Activity.
     *
     * @param item to be converted
     * @return ImageView of the item
     */
    private ImageView itemToImageView(Item item) {
        ImageView view = new ImageView(this);
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
     * Creates a text feedback to inform the player which item
     * has been picked up.
     *
     * @param item that was activated
     * @return feedback text
     */
    private TextView itemTextFeedback(Item item) {
        final FeedbackTextView feedback = new FeedbackTextView(this);
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

    /**
     * Helper class that defines the style of the text feedback.
     */
    private class FeedbackTextView extends android.support.v7.widget.AppCompatTextView {

        protected FeedbackTextView(Context context) {
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
