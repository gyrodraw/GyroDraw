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
        int x = randomIntWithinBounds(paintView.getWidth());
        int y = randomIntWithinBounds(paintView.getHeight());
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

    protected Item[] generateAllDifferentItems() {
        int x = randomIntWithinBounds(paintView.getWidth());
        int y = randomIntWithinBounds(paintView.getHeight());
        SpeedupItem speedupItem = SpeedupItem.createSpeedupItem(x, y, 10, 10);
        x = randomIntWithinBounds(paintView.getWidth());
        y = randomIntWithinBounds(paintView.getHeight());
        SlowdownItem slowdownItem = SlowdownItem.createSlowdownItem(x, y, 10, 10);
        x = randomIntWithinBounds(paintView.getWidth());
        y = randomIntWithinBounds(paintView.getHeight());
        SwapAxisItem swapAxisItem = SwapAxisItem.createSwapAxisItem(x, y, 10, 10);
        return new Item[]{speedupItem, slowdownItem, swapAxisItem};
    }

    private int randomIntWithinBounds(int max) {
        Random random = new Random();
        return 2*ITEM_RADIUS + random.nextInt(max - 4*ITEM_RADIUS);
    }


}
