package ch.epfl.sweng.SDP.game.drawing.items;

import android.os.CountDownTimer;

import ch.epfl.sweng.SDP.game.drawing.PaintView;

/**
 * Class representing an item which swaps the axis while drawing.
 */
public class SwapAxisItem extends Item {

    private SwapAxisItem(int x, int y, int radius) {
        super(x, y, radius);
    }

    /**
     * Creates a {@link SwapAxisItem}.
     *
     * @param x      x position
     * @param y      y position
     * @param radius radius of the item
     * @return the desired item
     */
    public static SwapAxisItem createSwapAxisItem(int x, int y, int radius) {
        return new SwapAxisItem(x, y, radius);
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
