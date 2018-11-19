package ch.epfl.sweng.SDP.game.drawing;

import android.os.CountDownTimer;

public class SpeedupItem extends Item {

    private static final int SPEDUP_FACTOR = 2;

    private SpeedupItem(int x, int y, int radius) {
        super(x, y, radius);
    }

    protected static SpeedupItem createSpeedupItem(int x, int y, int radius) {
        return new SpeedupItem(x, y, radius);
    }

    @Override
    protected void activate(final PaintView paintView) {
        paintView.speed *= SPEDUP_FACTOR;
        new CountDownTimer(super.ITEM_DURATION, 1000) {

            public void onTick(long millisUntilFinished) {
                // nüt
            }

            public void onFinish() {
                deactivate(paintView);
            }
        }.start();
    }

    protected void deactivate(PaintView paintView) {
        paintView.speed /= SPEDUP_FACTOR;
    }

    @Override
    protected String textFeedback() {
        return "SPEEDUP! ";
    }
}
