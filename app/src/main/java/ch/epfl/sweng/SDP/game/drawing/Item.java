package ch.epfl.sweng.SDP.game.drawing;

import android.content.Context;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ch.epfl.sweng.SDP.R;

public abstract class Item {

    protected int x;
    protected int y;
    protected int radius;
    protected int interval;
    protected boolean active = false;

    private Context context;
    private ImageView view;
    private RelativeLayout.LayoutParams imageViewParam;

    protected Item(Context context, int x, int y, int radius, int interval) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.interval = interval;

        this.context = context;
        this.view = toView(context);

        imageViewParam = new RelativeLayout.LayoutParams(radius, radius);
    }

    protected boolean collision(int x, int y, int radius) {
        return norm(this.x - x, this.y - y) <= this.radius + radius;
    }

    private double norm(int x, int y) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public ImageView getView() {
        return view;
    }

    private ImageView toView(Context context) {
        ImageView view =  new ImageView(context);
        view.setX(x-radius);
        view.setY(y-radius);
        view.setLayoutParams(new RelativeLayout.LayoutParams(2*radius, 2*radius));
        view.setBackgroundResource(R.drawable.mystery_box);
        return view;
    }

    protected abstract void activate(final PaintView paintView);

    protected abstract void deactivate(PaintView paintView);

    protected abstract TextView feedbackTextView(RelativeLayout paintViewHolder);

    protected TextView createFeedbackTextView(final RelativeLayout paintViewHolder) {
        final FeedbackTextView feedback = new FeedbackTextView(context);

        new CountDownTimer(800, 10) {

            public void onTick(long millisUntilFinished) {
                feedback.setTextSize(60-millisUntilFinished/15);
            }

            public void onFinish() {
                paintViewHolder.removeView(feedback);
            }
        }.start();
        return feedback;
    }

}

class FeedbackTextView extends android.support.v7.widget.AppCompatTextView {

    protected FeedbackTextView(Context context) {
        super(context);
        setTextColor(context.getResources().getColor(R.color.colorDrawYellow));
        setShadowLayer(10, 0, 0, context.getResources().getColor(R.color.colorGrey));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        setTextSize(1);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        setLayoutParams(layoutParams);
        Typeface typeMuro = Typeface.createFromAsset(context.getAssets(), "fonts/Muro.otf");
        setTypeface(typeMuro, Typeface.ITALIC);
    }


}
