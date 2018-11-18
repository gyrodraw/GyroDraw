package ch.epfl.sweng.SDP.game.drawing;

import java.util.Random;

public class RandomItemGenerator {

    private static final int INTERVAL = 10000;
    private static final int ITEM_RADIUS = 80;

    PaintView paintView;

    protected RandomItemGenerator(PaintView paintView) {
        this.paintView = paintView;
    }

    protected Item generateItem() {
        Items item = Items.randomItem();
        Random random = new Random();
        int x = 2*ITEM_RADIUS + random.nextInt(paintView.getWidth() - 4*ITEM_RADIUS);
        int y = 2*ITEM_RADIUS + random.nextInt(paintView.getHeight() - 4*ITEM_RADIUS);
        switch (item) {
            case SPEEDUP:
                return SpeedupItem.createSpeedupItem(x, y, ITEM_RADIUS, INTERVAL);
            case SLOWDOWN:
                return SlowdownItem.createSlowdownItem(x, y, ITEM_RADIUS, INTERVAL);
            case SWAPAXIS:
                return SwapAxisItem.createSwapAxisItem(x, y, ITEM_RADIUS, INTERVAL);
            //case ADDSTARS:
            //    return AddStarsItem.createAddStarsItem(x, y, ITEM_RADIUS, INTERVAL);
            //case LOSESTARS:
            //    return LoseStarsItem.createLoseStarsItem(x, y, ITEM_RADIUS, INTERVAL);

            default:
                throw new IllegalArgumentException("Unknown item type");
        }
    }



}
