package ch.epfl.sweng.SDP.game.drawing.items;

import java.util.Random;

import ch.epfl.sweng.SDP.game.drawing.PaintView;
import ch.epfl.sweng.SDP.game.drawing.items.AddStarsItem;
import ch.epfl.sweng.SDP.game.drawing.items.BumpingItem;
import ch.epfl.sweng.SDP.game.drawing.items.Item;
import ch.epfl.sweng.SDP.game.drawing.items.Items;
import ch.epfl.sweng.SDP.game.drawing.items.SlowdownItem;
import ch.epfl.sweng.SDP.game.drawing.items.SpeedupItem;
import ch.epfl.sweng.SDP.game.drawing.items.SwapAxisItem;

public class RandomItemGenerator {

    private static final int ITEM_RADIUS = 80;

    private RandomItemGenerator() {}

    /**
     * Generates a random item at a random position.
     * @return the generated item
     */
    public static Item generateItem(PaintView paintView) {
        Items item = Items.randomItem();
        int x = randomIntWithinBounds(paintView.getWidth());
        int y = randomIntWithinBounds(paintView.getHeight());
        switch (item) {
            case SPEEDUP:
                return SpeedupItem.createSpeedupItem(x, y, ITEM_RADIUS);
            case SLOWDOWN:
                return SlowdownItem.createSlowdownItem(x, y, ITEM_RADIUS);
            case SWAPAXIS:
                return SwapAxisItem.createSwapAxisItem(x, y, ITEM_RADIUS);
            case ADDSTARS:
                return AddStarsItem.createAddStarsItem(x, y, ITEM_RADIUS);
            case BUMP:
                return BumpingItem.createBumpingItem(x, y, ITEM_RADIUS);

            default:
                throw new IllegalArgumentException("Unknown item type");
        }
    }

    /**
     * Random position which is fully visible on screen.
     * @param max upper bound of screen
     * @return random position
     */
    private static int randomIntWithinBounds(int max) {
        Random random = new Random();
        return 2*ITEM_RADIUS + random.nextInt(max - 4*ITEM_RADIUS);
    }


}
