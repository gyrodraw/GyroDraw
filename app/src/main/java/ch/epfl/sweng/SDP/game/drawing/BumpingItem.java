package ch.epfl.sweng.SDP.game.drawing;

import android.os.CountDownTimer;

class BumpingItem extends Item {

    private BumpingItem(int x, int y, int radius) {
        super(x, y, radius);
    }

    public static BumpingItem createBumpingItem(int x, int y, int radius) {
        return new BumpingItem(x, y, radius);
    }

    @Override
    protected boolean collision(PaintView paintView) {
        activate(paintView);
        return false;
    }

    @Override
    protected void activate(final PaintView paintView) {
        if (super.norm(this.x - paintView.getCircleX(),
                this.y - paintView.getCircleY())
                < this.radius + paintView.getCircleRadius()) {
            double angle = Math.atan2(paintView.getCircleY() - this.y,
                                        paintView.getCircleX() - this.x);
            paintView.setCircle(
                    this.x + (int) (Math.cos(angle) *
                            (this.radius + paintView.getCircleRadius() + 5)),
                    this.y + (int) (Math.sin(angle) *
                            (this.radius + paintView.getCircleRadius() + 5)));
        }
    }

    @Override
    protected String textFeedback() {
        return " ";
    }
}
