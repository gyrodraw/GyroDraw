package ch.epfl.sweng.SDP.game.drawing;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ch.epfl.sweng.SDP.R;

public abstract class Item {

    protected int x;
    protected int y;
    protected int radius;
    protected int interval;
    protected boolean active = false;

    private Context context;
    private View view;
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

    public View getView() {
        return view;
    }

    private View toView(Context context) {
        ImageView view =  new ImageView(context);
        view.setX(x-radius);
        view.setY(y-radius);
        view.setLayoutParams(new RelativeLayout.LayoutParams(2*radius, 2*radius));
        view.setBackgroundResource(R.drawable.mystery_box);
        return view;
    }

    protected abstract void activate(final PaintView paintView);

    protected abstract void deactivate(PaintView paintView);

}
