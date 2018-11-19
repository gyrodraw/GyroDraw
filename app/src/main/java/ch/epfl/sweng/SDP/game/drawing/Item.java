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
     * Calculates if there is a collision between the item and the given parameters.
     * @param paintView reference to compare with
     * @return          true if there is a collision, else false
     */
    protected boolean collision(PaintView paintView) {
        return collision(paintView.getCircleX(),
                paintView.getCircleY(),
                paintView.getCircleRadius());
    }

    protected boolean collision(int x, int y, int radius) {
        return norm(this.x - x, this.y - y)
                < this.radius + radius;
    }

    protected double norm(int x, int y) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    protected abstract void activate(final PaintView paintView);

    protected abstract String textFeedback();

}
