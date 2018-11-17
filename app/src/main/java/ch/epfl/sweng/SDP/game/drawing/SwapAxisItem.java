package ch.epfl.sweng.SDP.game.drawing;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.RelativeLayout;
import android.widget.TextView;

class SwapAxisItem extends Item {

    private SwapAxisItem(Context context, int x, int y, int radius, int interval) {
        super(context, x, y, radius, interval);
    }

    public static SwapAxisItem createSwapAxisItem(Context context, int x, int y, int radius, int interval) {
        return new SwapAxisItem(context, x, y, radius, interval);
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
    protected TextView feedbackTextView(RelativeLayout paintViewHolder) {
        TextView feedback = super.createFeedbackTextView(paintViewHolder);
        feedback.setText("SWAP! ");
        return feedback;
    }
}
