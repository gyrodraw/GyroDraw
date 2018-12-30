package ch.epfl.sweng.GyroDraw.game.drawing.items;

import android.os.CountDownTimer;

import ch.epfl.sweng.GyroDraw.game.drawing.PaintView;

/**
 * Represents all items that act on a {@link PaintView} for a limited amount of time.
 */
public abstract class DeactivableItem extends Item {

    private static final int ITEM_DURATION = 10000;

    DeactivableItem(int posX, int posY, int radius) {
        super(posX, posY, radius);
    }

    /**
     * Deactivates the item's ability.
     */
    protected abstract void deactivate(PaintView paintView);

    /**
     * Launches the countdown until the item will be deactivated.
     *
     * @param paintView the PaintView the item is acting on
     * @return the {@link CountDownTimer}
     */
    CountDownTimer launchCountDownUntilDeactivation(final PaintView paintView) {
        return new CountDownTimer(ITEM_DURATION, ITEM_DURATION) {

            public void onTick(long millisUntilFinished) {
                // Is never called
            }

            public void onFinish() {
                deactivate(paintView);
            }
        };
    }
}
