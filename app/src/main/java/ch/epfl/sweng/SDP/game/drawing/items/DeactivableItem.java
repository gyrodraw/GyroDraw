package ch.epfl.sweng.SDP.game.drawing.items;

import android.os.CountDownTimer;

import ch.epfl.sweng.SDP.game.drawing.PaintView;

/**
 * Represents all items that act on a PaintView for a limited amount of time.
 */
public abstract class DeactivableItem extends Item {

    protected DeactivableItem(int posX, int posY, int radius) {
        super(posX, posY, radius);
    }

    /**
     * Deactivates the item's ability.
     */
    protected abstract void deactivate(PaintView paintView);

    /**
     * Launches the countdown until the item will be deactivated.
     *
     * @param   paintView the item is acting on
     * @return  the countdown
     */
    protected CountDownTimer launchCountDownUntilDeactivation(final PaintView paintView) {
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
