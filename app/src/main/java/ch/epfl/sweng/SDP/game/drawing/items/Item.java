package ch.epfl.sweng.SDP.game.drawing.items;

import ch.epfl.sweng.SDP.game.drawing.PaintView;
import ch.epfl.sweng.SDP.utils.Preconditions;

public abstract class Item {

    protected static final int ITEM_DURATION = 10000;

    protected int x;
    protected int y;
    protected int radius;

    protected Item(int x, int y, int radius) {
        Preconditions.checkPrecondition(x >= 0 && y >= 0 && radius >= 0,
                "Coordinates and radius must not be negative");
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getRadius() {
        return radius;
    }

    /**
     * Calculates if there is a collision between the item and the given paintView.
     * @param paintView reference to compare with
     * @return          true if there is a collision, else false
     */
    public boolean collision(PaintView paintView) {
        return collision(paintView.getCircleX(),
                paintView.getCircleY(),
                paintView.getCircleRadius());
    }

    /**
     * Calculates if there is a collision between the item and the given parameters.
     * @param x         x coordinate to check
     * @param y         y coordinate to check
     * @param radius    radius of circle
     * @return          true if there is collision, else false
     */
    protected boolean collision(int x, int y, int radius) {
        return Math.hypot(this.x - x, this.y - y)
                < this.radius + radius;
    }

    /**
     * Activates the items' ability.
     * @param paintView to apply the ability on
     */
    public abstract void activate(final PaintView paintView);

    /**
     * String to show to the player which item class was picked.
     * @return feedback text
     */
    public abstract String textFeedback();

}