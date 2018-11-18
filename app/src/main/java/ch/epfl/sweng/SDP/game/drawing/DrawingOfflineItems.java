package ch.epfl.sweng.SDP.game.drawing;

import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import ch.epfl.sweng.SDP.R;

public class DrawingOfflineItems extends DrawingOffline {

    private static final int INTERVAL = 10000;

    protected RelativeLayout paintViewHolder;
    protected PaintView paintView;
    protected RandomItemGenerator randomItemGenerator;
    private Map<Item, ImageView> displayedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paintViewHolder = findViewById(R.id.paintViewHolder);
        paintView = super.paintView;
        randomItemGenerator = new RandomItemGenerator(paintView);
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
     * @param coordinateX new X coordinate for paintView
     * @param coordinateY new Y coordinate for paintView
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void updateValues(float coordinateX, float coordinateY) {
        paintView.updateCoordinates(coordinateX, coordinateY);

        Optional<Item> collidingItemOptional = hasCollision();

        if(collidingItemOptional.isPresent()) {
            Item collidingItem = collidingItemOptional.get();
            collidingItem.activate(paintView);
            paintViewHolder.removeView(displayedItems.get(collidingItem));
            paintViewHolder.addView(itemTextFeedback(collidingItem));
            displayedItems.remove(collidingItem);
        }
    }

    /**
     * Checks if the paintViews' circle collided with an item.
     * @return Optional of the item that collided, or empty.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Optional<Item> hasCollision() {
        for(Item item : displayedItems.keySet()) {
            if(item.collision(paintView.circleX, paintView.circleY, paintView.getCircleRadius())) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    /**
     * Generates a random item every INTERVAL seconds.
     */
    private void generateItems() {
        new CountDownTimer(INTERVAL, 1000) {

            public void onTick(long millisUntilFinished) {
                // nüt
            }

            public void onFinish() {
                Item newItem = randomItemGenerator.generateItem();
                ImageView imageView = itemToImageView(newItem);
                paintViewHolder.addView(imageView);
                displayedItems.put(newItem, imageView);
                generateItems();
            }
        }.start();
    }

    /**
     * Converts an item into an ImageView to be displayed on the Activity.
     * @param item to be converted
     * @return ImageView of the item
     */
    private ImageView itemToImageView(Item item) {
        ImageView view =  new ImageView(this);
        view.setX(item.x-item.radius);
        view.setY(item.y-item.radius);
        view.setLayoutParams(new RelativeLayout.LayoutParams(2*item.radius, 2*item.radius));
        view.setBackgroundResource(R.drawable.mystery_box);
        return view;
    }

    /**
     * Creates a text feedback to inform the player which item
     * has been picked up.
     * @param item that was activated
     * @return feedback text
     */
    private TextView itemTextFeedback(Item item) {
        final FeedbackTextView feedback = new FeedbackTextView(this);
        feedback.setText(item.textFeedback());

        new CountDownTimer(800, 40) {

            public void onTick(long millisUntilFinished) {
                feedback.setTextSize(60-millisUntilFinished/15);
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
