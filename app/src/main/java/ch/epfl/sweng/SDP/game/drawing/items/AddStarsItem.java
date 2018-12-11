package ch.epfl.sweng.SDP.game.drawing.items;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.game.drawing.PaintView;

/**
 * Class representing a bonus item which gives 3 stars to the player.
 */
public class AddStarsItem extends Item {

    private static final int ADD_STARS = 3;

    public AddStarsItem(int posX, int posY, int radius) {
        super(posX, posY, radius);
    }

    @Override
    public void activate(final PaintView paintView) {
        vibrate(paintView);
        Account.getInstance(paintView.getContext()).changeStars(ADD_STARS);
    }

    @Override
    public void deactivate(PaintView paintView) {
        // Must be implemented, but does nothing.
    }

    @Override
    public String getTextFeedback() {
        return "+" + ADD_STARS + " STARS ! ";
    }

    @Override
    public int getColorId() {
        return R.color.colorGreen;
    }
}
