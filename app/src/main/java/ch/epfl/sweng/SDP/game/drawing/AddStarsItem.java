package ch.epfl.sweng.SDP.game.drawing;

import ch.epfl.sweng.SDP.auth.Account;

class AddStarsItem extends Item {

    private static final int ADD_STARS = 10;

    private AddStarsItem(int x, int y, int radius) {
        super(x, y, radius);
    }

    public static AddStarsItem createAddStarsItem(int x, int y, int radius) {
        return new AddStarsItem(x, y, radius);
    }

    @Override
    protected void activate(final PaintView paintView) {
        Account.getInstance(paintView.getContext()).changeStars(ADD_STARS);
    }

    @Override
    protected String textFeedback() {
        return "+10 STARS! ";
    }
}
