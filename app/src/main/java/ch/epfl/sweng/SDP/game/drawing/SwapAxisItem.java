package ch.epfl.sweng.SDP.game.drawing;

import android.os.CountDownTimer;

class SwapAxisItem extends Item implements Deactivable {

    private SwapAxisItem(int x, int y, int radius) {
        super(x, y, radius);
    }

    public static SwapAxisItem createSwapAxisItem(int x, int y, int radius) {
        return new SwapAxisItem(x, y, radius);
    }

    @Override
    protected void activate(final PaintView paintView) {
        paintView.speed *= -1;
        new CountDownTimer(super.ITEM_DURATION, 1000) {

            public void onTick(long millisUntilFinished) {
                // nüt
            }

            public void onFinish() {
                deactivate(paintView);
            }
        }.start();
    }

    public void deactivate(PaintView paintView) {
        paintView.speed *= -1;
    }

    @Override
    protected String textFeedback() {
        return "SWAPPED! ";
    }


}
