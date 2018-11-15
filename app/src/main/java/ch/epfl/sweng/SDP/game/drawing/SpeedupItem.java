package ch.epfl.sweng.SDP.game.drawing;

public class SpeedupItem extends Item {

    private static final int SPPEDUP_FACTOR = 2;

    private SpeedupItem(int x, int y, int radius) {
        super(x, y, radius);
    }

    protected static SpeedupItem createSpeedupItem(int x, int y, int radius) {
        return new SpeedupItem(x, y, radius);
    }

    @Override
    protected void startEffect(PaintView paintView) {
        paintView.speed *= SPPEDUP_FACTOR;
    }

    @Override
    protected void endEffect(PaintView paintView) {
        paintView.speed /= SPPEDUP_FACTOR;
    }
}
