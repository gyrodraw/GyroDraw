package ch.epfl.sweng.SDP.game.drawing;

public abstract class Item {

    protected int x;
    protected int y;
    protected int radius;
    protected int interval;

    protected Item(int x, int y, int radius, int interval) {
        if(x < 0 || y < 0 || radius < 0 || interval < 0) {
            throw new IllegalArgumentException("Item arguments must not be negative");
        }
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.interval = interval;
    }

    protected boolean collision(int x, int y, int radius) {
        if(x < 0 || y < 0 || radius < 0) {
            throw new IllegalArgumentException("Coordinates and radius must not be negative");
        }
        return norm(this.x - x, this.y - y) <= this.radius + radius;
    }

    private double norm(int x, int y) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    protected abstract void activate(final PaintView paintView);

    protected abstract void deactivate(PaintView paintView);

    protected abstract String textFeedback();

}
