package ch.epfl.sweng.GyroDraw.game.drawing.items;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import ch.epfl.sweng.GyroDraw.R;
import ch.epfl.sweng.GyroDraw.game.drawing.PaintView;

import static ch.epfl.sweng.GyroDraw.utils.Preconditions.checkPrecondition;

/**
 * Abstract class representing an item.
 */
public abstract class Item {

    private final int posX;
    private final int posY;
    private final int radius;

    Item(int posX, int posY, int radius) {
        checkPrecondition(posX >= 0 && posY >= 0 && radius >= 0,
                "Coordinates and radius must not be negative");
        this.posX = posX;
        this.posY = posY;
        this.radius = radius;
    }

    public int getX() {
        return posX;
    }

    public int getY() {
        return posY;
    }

    public int getRadius() {
        return radius;
    }

    /**
     * Calculates if there is a collision between the item and the given {@link PaintView}.
     *
     * @param paintView reference to compare with
     * @return true if there is a collision, false otherwise
     */
    public boolean collision(PaintView paintView) {
        return collision(paintView.getCircleX(),
                paintView.getCircleY(),
                paintView.getCircleRadius());
    }

    /**
     * Calculates if there is a collision between the item and the given parameters.
     *
     * @param posX x coordinate to check
     * @param posY y coordinate to check
     * @param radius radius of circle
     * @return true if there is collision, false otherwise
     */
    protected boolean collision(int posX, int posY, int radius) {
        return Math.hypot(this.posX - posX, this.posY - posY)
                < this.radius + radius;
    }

    /**
     * Activates the items' ability.
     *
     * @param paintView to apply the ability on
     */
    public abstract void activate(final PaintView paintView);

    /**
     * Returns a string to show to the player which item class was picked.
     *
     * @return feedback text
     */
    public abstract String getTextFeedback();

    /**
     * Returns the id of the feedback text's color.
     *
     * @return the color id
     */
    public int getColorId() {
        return R.color.colorExitRed;
    }

    /**
     * Creates a short vibration feedback.
     *
     * @param paintView used to get the context from
     */
    void vibrate(PaintView paintView) {
        Vibrator vibrator = (Vibrator) paintView.getContext()
                .getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(
                    100, 1));
        } else {
            // Deprecated in API 26
            vibrator.vibrate(100);
        }
    }
}
