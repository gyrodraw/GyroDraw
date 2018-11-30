package ch.epfl.sweng.SDP.game.drawing.items;

import java.util.Random;

import ch.epfl.sweng.SDP.game.drawing.PaintView;

/**
 * Class representing a random {@link Item} generator.
 */
public final class RandomItemGenerator {

    private static final int ITEM_RADIUS = 50;

    private RandomItemGenerator() {
    }

    /**
     * Generates a random item at a random position.
     *
     * @return the generated item
     */
    public static Item generateItem(PaintView paintView) {
        Items item = Items.getRandomItem();
        int x = getRandomIntWithinBounds(paintView.getWidth());
        int y = getRandomIntWithinBounds(paintView.getHeight());
        switch (item) {
            case SPEEDUP:
                return SpeedupItem.createSpeedupItem(x, y, ITEM_RADIUS);
            case SLOWDOWN:
                return SlowdownItem.createSlowdownItem(x, y, ITEM_RADIUS);
            case SWAP_AXIS:
                return SwapAxisItem.createSwapAxisItem(x, y, ITEM_RADIUS);
            case ADD_STARS:
                return AddStarsItem.createAddStarsItem(x, y, ITEM_RADIUS);
            case BUMP:
                return BumpingItem.createBumpingItem(x, y, ITEM_RADIUS);
            default:
                throw new IllegalArgumentException("Unknown item type");
        }
    }

    /**
     * Generates a random item (add stars excluded) at a random position.
     *
     * @return the generated item
     */
    public static Item generateItemForOfflineMode(PaintView paintView) {
        Items item = Items.getRandomItemForOfflineMode();
        int x = getRandomIntWithinBounds(paintView.getWidth());
        int y = getRandomIntWithinBounds(paintView.getHeight());
        switch (item) {
            case SPEEDUP:
                return SpeedupItem.createSpeedupItem(x, y, ITEM_RADIUS);
            case SLOWDOWN:
                return SlowdownItem.createSlowdownItem(x, y, ITEM_RADIUS);
            case SWAP_AXIS:
                return SwapAxisItem.createSwapAxisItem(x, y, ITEM_RADIUS);
            case BUMP:
                return BumpingItem.createBumpingItem(x, y, ITEM_RADIUS);
            default:
                throw new IllegalArgumentException("Unknown item type");
        }
    }

    /**
     * Returns a random position which is fully visible on screen.
     *
     * @param max upper bound of screen
     * @return a random position
     */
    private static int getRandomIntWithinBounds(int max) {
        Random random = new Random();
        return 2 * ITEM_RADIUS + random.nextInt(max - 4 * ITEM_RADIUS);
    }
}
