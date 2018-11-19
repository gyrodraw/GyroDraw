package ch.epfl.sweng.SDP.game.drawing.items;

import android.os.CountDownTimer;

import ch.epfl.sweng.SDP.game.drawing.PaintView;

public class SpeedupItem extends Item {

    private static final double SPEDUP_FACTOR = 2;

    private SpeedupItem(int x, int y, int radius) {
        super(x, y, radius);
    }

    public static SpeedupItem createSpeedupItem(int x, int y, int radius) {
        return new SpeedupItem(x, y, radius);
    }

    @Override
    public void activate(final PaintView paintView) {
        paintView.multSpeed(SPEDUP_FACTOR);
        new CountDownTimer(super.ITEM_DURATION, 1000) {

            public void onTick(long millisUntilFinished) {
                // nüt
            }

            public void onFinish() {
                deactivate(paintView);
            }
        }.start();
    }

    private void deactivate(PaintView paintView) {
        paintView.multSpeed(1/SPEDUP_FACTOR);
    }

    @Override
    public String textFeedback() {
        return "SPEEDUP! ";
    }
}
