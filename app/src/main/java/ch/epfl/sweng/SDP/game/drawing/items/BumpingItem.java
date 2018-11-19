package ch.epfl.sweng.SDP.game.drawing.items;

import ch.epfl.sweng.SDP.game.drawing.PaintView;

public class BumpingItem extends Item {

    private BumpingItem(int x, int y, int radius) {
        super(x, y, radius);
    }

    public static BumpingItem createBumpingItem(int x, int y, int radius) {
        return new BumpingItem(x, y, radius);
    }

    @Override
    public boolean collision(PaintView paintView) {
        activate(paintView);
        return false;
    }

    @Override
    public void activate(final PaintView paintView) {
        if (Math.hypot(this.x - paintView.getCircleX(),
                this.y - paintView.getCircleY())
                < this.radius + paintView.getCircleRadius()) {
            double angle = Math.atan2(paintView.getCircleY() - this.y,
                                        paintView.getCircleX() - this.x);
            paintView.setCircle(
                    this.x + (int) (Math.cos(angle)
                            * (this.radius + paintView.getCircleRadius() + 5)),
                    this.y + (int) (Math.sin(angle)
                            * (this.radius + paintView.getCircleRadius() + 5)));
        }
    }

    @Override
    public String textFeedback() {
        return " ";
    }
}
