package ch.epfl.sweng.SDP.game.drawing;

import android.content.Context;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.game.drawing.items.Item;
import ch.epfl.sweng.SDP.utils.TypefaceLibrary;

/**
 * Helper class that defines the style of the text feedback.
 */
class FeedbackTextView extends android.support.v7.widget.AppCompatTextView {

    private FeedbackTextView(Context context, String text, int colorId) {
        super(context);
        setTextColor(context.getResources().getColor(colorId));
        setShadowLayer(10, 0, 0, context.getResources().getColor(R.color.colorPrimaryDark));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        setTextSize(1);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        setLayoutParams(layoutParams);
        Typeface typeMuro = TypefaceLibrary.getTypeMuro();
        setTypeface(typeMuro, Typeface.ITALIC);
        setText(text);
    }

    /**
     * Creates a text feedback to inform the player which item has been picked up.
     *
     * @param item            that was activated
     * @param paintViewHolder the holder of the view
     * @param context         the context where we want to put the TextView
     * @return feedback text
     */
    static TextView itemTextFeedback(Item item, final RelativeLayout paintViewHolder,
                                     Context context) {
        final FeedbackTextView feedback = new FeedbackTextView(context, item.getTextFeedback(),
                item.getColorId());

        new CountDownTimer(800, 40) {

            public void onTick(long millisUntilFinished) {
                feedback.setTextSize(60 - millisUntilFinished / 15);
            }

            public void onFinish() {
                paintViewHolder.removeView(feedback);
            }
        }.start();
        return feedback;
    }

    /**
     * Creates a text feedback to inform the player that the time is over.
     *
     * @param context the context where we want to put the TextView
     * @return feedback text
     */
    static TextView timeIsUpTextFeedback(Context context) {
        final FeedbackTextView feedback = new FeedbackTextView(context, "TIME'S UP ! ",
                R.color.colorDrawYellow);

        new CountDownTimer(800, 40) {

            public void onTick(long millisUntilFinished) {
                feedback.setTextSize(60 - millisUntilFinished / 15);
            }

            public void onFinish() {
                // Nothing to do, the time's up message stays until the end
            }
        }.start();
        return feedback;
    }
}
