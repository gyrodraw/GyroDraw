package ch.epfl.sweng.SDP.game.drawing;

import android.content.Context;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SpeedupItem extends Item {

    private static final int SPEDUP_FACTOR = 2;

    private SpeedupItem(Context context, int x, int y, int radius, int interval) {
        super(context, x, y, radius, interval);
    }

    protected static SpeedupItem createSpeedupItem(Context context, int x, int y, int radius, int interval) {
        return new SpeedupItem(context, x, y, radius, interval);
    }

    @Override
    protected void activate(final PaintView paintView) {
        super.active = true;
        paintView.speed *= SPEDUP_FACTOR;
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
        super.active = false;
        paintView.speed /= SPEDUP_FACTOR;
    }

    @Override
    protected TextView feedbackTextView(RelativeLayout paintViewHolder) {
        TextView feedback = super.createFeedbackTextView(paintViewHolder);
        feedback.setText("SPEED! ");
        return feedback;
    }
}
