package ch.epfl.sweng.SDP.game.drawing.items;

import android.os.CountDownTimer;

import ch.epfl.sweng.SDP.game.drawing.PaintView;

/**
 * Immutable class representing an item which speeds up the player's cursor.
 */
public class SpeedupItem extends Item {

    private static final double SPEEDUP_FACTOR = 2;

    public SpeedupItem(int x, int y, int radius) {
        super(x, y, radius);
    }

    @Override
    public void activate(final PaintView paintView) {
        paintView.multSpeed(SPEEDUP_FACTOR);
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
        paintView.multSpeed(1 / SPEEDUP_FACTOR);
    }

    @Override
    public String getTextFeedback() {
        return "SPEEDUP ! ";
    }
}
