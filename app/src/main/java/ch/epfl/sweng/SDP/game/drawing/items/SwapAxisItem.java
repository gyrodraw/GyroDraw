package ch.epfl.sweng.SDP.game.drawing.items;

import android.os.CountDownTimer;

import ch.epfl.sweng.SDP.game.drawing.PaintView;

public class SwapAxisItem extends Item {

    private SwapAxisItem(int x, int y, int radius) {
        super(x, y, radius);
    }

    public static SwapAxisItem createSwapAxisItem(int x, int y, int radius) {
        return new SwapAxisItem(x, y, radius);
    }

    @Override
    public void activate(final PaintView paintView) {
        paintView.multSpeed(-1);
        new CountDownTimer(super.ITEM_DURATION, super.ITEM_DURATION) {

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
    public String textFeedback() {
        return "SWAPPED! ";
    }


}
