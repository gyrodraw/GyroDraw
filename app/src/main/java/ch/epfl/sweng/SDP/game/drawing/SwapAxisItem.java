package ch.epfl.sweng.SDP.game.drawing;

import android.os.CountDownTimer;

class SwapAxisItem extends Item {

    private SwapAxisItem(int x, int y, int radius, int interval) {
        super(x, y, radius, interval);
    }

    public static SwapAxisItem createSwapAxisItem(int x, int y, int radius, int interval) {
        return new SwapAxisItem(x, y, radius, interval);
    }

    @Override
    protected void activate(final PaintView paintView) {
        paintView.speed *= -1;
        new CountDownTimer(super.interval, 1000) {

            public void onTick(long millisUntilFinished) {
                // nüt
            }

            public void onFinish() {
                deactivate(paintView);
            }
        }.start();
    }

    @Override
    protected void deactivate(PaintView paintView) {
        paintView.speed *= -1;
    }

    @Override
    protected String textFeedback() {
        return "SWAPPED! ";
    }


}
