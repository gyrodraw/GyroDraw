package ch.epfl.sweng.SDP.game.drawing.items;

import android.os.CountDownTimer;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.game.drawing.PaintView;

/**
 * Class representing an item which slows down the player's cursor.
 */
public class SlowdownItem extends Item {

    private static final double SLOWDOWN_FACTOR = 0.5;

    private SlowdownItem(int x, int y, int radius) {
        super(x, y, radius);
    }

    /**
     * Creates a {@link SlowdownItem}.
     *
     * @param x      x position
     * @param y      y position
     * @param radius radius of the item
     * @return the desired item
     */
    public static SlowdownItem createSlowdownItem(int x, int y, int radius) {
        return new SlowdownItem(x, y, radius);
    }

    @Override
    public void activate(final PaintView paintView) {
        paintView.multSpeed(SLOWDOWN_FACTOR);
        new CountDownTimer(ITEM_DURATION, ITEM_DURATION) {

            public void onTick(long millisUntilFinished) {
                // Is never called
            }

            public void onFinish() {
                deactivate(paintView);
            }
        }.start();
    }

    private void deactivate(PaintView paintView) {
        paintView.multSpeed(1 / SLOWDOWN_FACTOR);
    }

    @Override
    public String getTextFeedback() {
        return "SLOWDOWN ! ";
    }

    @Override
    public int getColorId() {
        return R.color.colorGreen;
    }
}
