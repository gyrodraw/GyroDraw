package ch.epfl.sweng.SDP.game.drawing;

import android.content.Context;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.widget.RelativeLayout;
import android.widget.TextView;

class SlowdownItem extends Item {

    private static final double SLOWDOWN_FACTOR = .5;

    private SlowdownItem(Context context, int x, int y, int radius, int interval) {
        super(context, x, y, radius, interval);
    }

    public static SlowdownItem createSlowdownItem(Context context, int x, int y, int radius, int interval) {
        return new SlowdownItem(context, x, y, radius, interval);
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
    protected TextView feedbackTextView(RelativeLayout paintViewHolder) {
        TextView feedback = super.createFeedbackTextView(paintViewHolder);
        feedback.setText("SLOW! ");
        return feedback;
    }
}
