package ch.epfl.sweng.SDP.game.drawing.items;

import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.game.drawing.PaintView;

/**
 * Class representing a bonus item which gives 3 stars to the player.
 */
public class AddStarsItem extends Item {

    private static final int ADD_STARS = 3;

    private AddStarsItem(int x, int y, int radius) {
        super(x, y, radius);
    }

    /**
     * Creates a {@link AddStarsItem}.
     *
     * @param x      x position
     * @param y      y position
     * @param radius radius of the item
     * @return the desired item
     */
    public static AddStarsItem createAddStarsItem(int x, int y, int radius) {
        return new AddStarsItem(x, y, radius);
    }

    @Override
    public void activate(final PaintView paintView) {
        Account.getInstance(paintView.getContext()).changeStars(ADD_STARS);
    }

    @Override
    public String textFeedback() {
        return "+" + ADD_STARS + " STARS! ";
    }
}
