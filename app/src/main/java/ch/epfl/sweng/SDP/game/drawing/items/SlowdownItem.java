package ch.epfl.sweng.SDP.game.drawing.items;

import android.os.CountDownTimer;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.game.drawing.PaintView;

public class SlowdownItem extends Item {

    private static final double SLOWDOWN_FACTOR = .5;

    private SlowdownItem(int x, int y, int radius) {
        super(x, y, radius);
    }

    public static SlowdownItem createSlowdownItem(int x, int y, int radius) {
        return new SlowdownItem(x, y, radius);
    }

    @Override
    public void activate(final PaintView paintView) {
        paintView.multSpeed(SLOWDOWN_FACTOR);
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
        paintView.multSpeed(1 / SLOWDOWN_FACTOR);
    }

    @Override
    public String getTextFeedback() {
        return "SLOWDOWN ! ";
    }

    @Override
    public int getColorId() {
        return R.color.colorGreen;
    }
}
