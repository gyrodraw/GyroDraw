package ch.epfl.sweng.GyroDraw.game.drawing.items;

import ch.epfl.sweng.GyroDraw.game.drawing.PaintView;

/**
 * Class representing an item which swaps the axis while drawing.
 */
public class SwapAxisItem extends DeactivableItem {

    public SwapAxisItem(int posX, int posY, int radius) {
        super(posX, posY, radius);
    }

    @Override
    public void activate(final PaintView paintView) {
        vibrate(paintView);
        paintView.multSpeed(-1);
        launchCountDownUntilDeactivation(paintView).start();
    }

    @Override
    public void deactivate(PaintView paintView) {
        paintView.multSpeed(-1);
    }

    @Override
    public String getTextFeedback() {
        return "SWAPPED ! ";
    }
}
