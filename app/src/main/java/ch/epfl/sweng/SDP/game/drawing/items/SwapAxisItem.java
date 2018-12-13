package ch.epfl.sweng.SDP.game.drawing.items;

import android.os.CountDownTimer;

import ch.epfl.sweng.SDP.game.drawing.PaintView;

/**
 * Immutable class representing an item which swaps the axis while drawing.
 */
public class SwapAxisItem extends Item {

    public SwapAxisItem(int x, int y, int radius) {
        super(x, y, radius);
    }

    @Override
    public void activate(final PaintView paintView) {
        paintView.multSpeed(-1);
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
        paintView.multSpeed(-1);
    }

    @Override
    public String getTextFeedback() {
        return "SWAPPED ! ";
    }
}
