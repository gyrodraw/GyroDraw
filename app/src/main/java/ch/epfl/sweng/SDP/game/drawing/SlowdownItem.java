package ch.epfl.sweng.SDP.game.drawing;

import android.os.CountDownTimer;

class SlowdownItem extends Item {

    private static final double SLOWDOWN_FACTOR = .5;

    private SlowdownItem(int x, int y, int radius, int interval) {
        super(x, y, radius, interval);
    }

    public static SlowdownItem createSlowdownItem(int x, int y, int radius, int interval) {
        return new SlowdownItem(x, y, radius, interval);
    }

    @Override
    protected void activate(final PaintView paintView) {
        paintView.speed *= SLOWDOWN_FACTOR;
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
        paintView.speed /= SLOWDOWN_FACTOR;
    }

    @Override
    protected String textFeedback() {
        return "SLOWDOWN! ";
    }


}
