package ch.epfl.sweng.SDP.game.drawing;

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

    /**
     * Calculates if there is a collision between the item and the given paintView.
     * @param paintView reference to compare with
     * @return          true if there is a collision, else false
     */
    protected boolean collision(PaintView paintView) {
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
        return norm(this.x - x, this.y - y)
                < this.radius + radius;
    }

    /**
     * Calculates the distance between this item and the given parameters.
     * @param x coordinate
     * @param y coordinate
     * @return  distance between item and (x,y)
     */
    protected double norm(int x, int y) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    /**
     * Activates the items' ability.
     * @param paintView to apply the ability on
     */
    protected abstract void activate(final PaintView paintView);

    /**
     * String to show to the player which item class was picked.
     * @return feedback text
     */
    protected abstract String textFeedback();

}
