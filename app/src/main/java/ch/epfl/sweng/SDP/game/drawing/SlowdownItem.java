package ch.epfl.sweng.SDP.game.drawing;

import android.os.CountDownTimer;

class SlowdownItem extends Item implements Deactivable {

    private static final double SLOWDOWN_FACTOR = .5;

    private SlowdownItem(int x, int y, int radius) {
        super(x, y, radius);
    }

    public static SlowdownItem createSlowdownItem(int x, int y, int radius) {
        return new SlowdownItem(x, y, radius);
    }

    @Override
    protected void activate(final PaintView paintView) {
        paintView.speed *= SLOWDOWN_FACTOR;
        new CountDownTimer(super.ITEM_DURATION, super.ITEM_DURATION) {

            public void onTick(long millisUntilFinished) {
                // nüt
            }

            public void onFinish() {
                deactivate(paintView);
            }
        }.start();
    }

    public void deactivate(PaintView paintView) {
        paintView.speed /= SLOWDOWN_FACTOR;
    }

    @Override
    protected String textFeedback() {
        return "SLOWDOWN! ";
    }


}
