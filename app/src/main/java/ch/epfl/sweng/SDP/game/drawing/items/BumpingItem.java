package ch.epfl.sweng.SDP.game.drawing.items;

import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.support.annotation.VisibleForTesting;
import android.widget.ImageView;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.game.drawing.PaintView;

/**
 * Class representing a bumping item.
 */
public class BumpingItem extends Item {

    private ImageView imageView;
    private boolean isActivated = false;

    private BumpingItem(int x, int y, int radius) {
        super(x, y, radius);
    }

    public static BumpingItem createBumpingItem(int x, int y, int radius) {
        return new BumpingItem(x, y, radius);
    }

    /**
     * This method overrides the parent's collision method because,
     * different from the other items, BumpingItem is not removed when
     * there was a collision. So instead it places the paintView outside
     * of its radius again.
     *
     * @param paintView reference to compare with
     * @return always false, because this item will never be removed
     */
    @Override
    public boolean collision(PaintView paintView) {
        if (Math.hypot(this.getX() - paintView.getCircleX(),
                this.getY() - paintView.getCircleY())
                < this.getRadius() + paintView.getCircleRadius()) {
            activate(paintView);
            if (!isActivated && imageView != null) {
                imageView.setImageResource(R.drawable.bumping_item);
                imageView.setColorFilter(new LightingColorFilter(Color.WHITE, Color.GRAY));
                isActivated = true;
            }
        }
        return false;
    }

    /**
     * Places the drawingCircle from paintView outside of the items radius.
     *
     * @param paintView to apply the ability on
     */
    @Override
    public void activate(final PaintView paintView) {
        double angle = Math.atan2(paintView.getCircleY() - this.getY(),
                paintView.getCircleX() - this.getX());
        paintView.setCircle(
                this.getX() + (int) (Math.cos(angle)
                        * (this.getRadius() + paintView.getCircleRadius() + 5)),
                this.getY() + (int) (Math.sin(angle)
                        * (this.getRadius() + paintView.getCircleRadius() + 5)));
    }

    @Override
    public String getTextFeedback() {
        return " ";
    }

    @VisibleForTesting
    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
