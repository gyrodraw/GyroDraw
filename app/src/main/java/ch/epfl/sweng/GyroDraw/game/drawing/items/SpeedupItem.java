package ch.epfl.sweng.GyroDraw.game.drawing.items;

import ch.epfl.sweng.GyroDraw.game.drawing.PaintView;

/**
 * Class representing an item which speeds up the player's cursor.
 */
public class SpeedupItem extends DeactivableItem {

    private static final double SPEEDUP_FACTOR = 2;

    public SpeedupItem(int posX, int posY, int radius) {
        super(posX, posY, radius);
    }

    @Override
    public void activate(final PaintView paintView) {
        vibrate(paintView);
        paintView.multSpeed(SPEEDUP_FACTOR);
        launchCountDownUntilDeactivation(paintView).start();
    }

    @Override
    public void deactivate(PaintView paintView) {
        paintView.multSpeed(1 / SPEEDUP_FACTOR);
    }

    @Override
    public String getTextFeedback() {
        return "SPEEDUP ! ";
    }
}
