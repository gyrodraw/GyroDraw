package ch.epfl.sweng.SDP.game.drawing;

import android.view.View;
import android.widget.ImageView;

public abstract class Item {

    protected int x;
    protected int y;
    private int radius;

    protected Item(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    protected boolean collision(int x, int y) {
        return norm(this.x - x, this.y - y) < radius;
    }

    private double norm(int x, int y) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public View toView() {
        return new ImageView()
    }

    protected abstract void startEffect(PaintView paintView);

    protected abstract void endEffect(PaintView paintView);

}
