package ch.epfl.sweng.SDP.game.drawing.items;

import ch.epfl.sweng.SDP.game.drawing.PaintView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Class representing a random {@link Item} generator.
 */
public final class RandomItemGenerator {

    public static final int ITEM_RADIUS = 50;

    private static final List<Items> VALUES =
            Collections.unmodifiableList(Arrays.asList(Items.values()));
    private static final int VALUES_SIZE = VALUES.size();

    private static final List<Items> VALUES_OFFLINE_MODE = createItemsForOfflineMode();
    private static final int VALUES_OFFLINE_MODE_SIZE = VALUES_OFFLINE_MODE.size();

    private static final Random RANDOM = new Random();

    private RandomItemGenerator() {
    }

    private static List<Items> createItemsForOfflineMode() {
        List<Items> items = new ArrayList<>(VALUES);
        items.remove(Items.ADD_STARS);
        return Collections.unmodifiableList(items);
    }

    /**
     * Picks a random item class.
     */
    private static Items getRandomItem() {
        return VALUES.get(RANDOM.nextInt(VALUES_SIZE));
    }

    /**
     * Picks a random item class for the offline mode ({@link Items#ADD_STARS} excluded).
     */
    private static Items getRandomItemForOfflineMode() {
        return VALUES_OFFLINE_MODE.get(RANDOM.nextInt(VALUES_OFFLINE_MODE_SIZE));
    }

    /**
     * Generates a random item at a random position.
     *
     * @param paintView the {@link PaintView} where the item should be placed
     * @return the generated item
     */
    public static Item generateItem(PaintView paintView) {
        Items item = getRandomItem();
        int x = getRandomIntWithinBounds(paintView.getWidth());
        int y = getRandomIntWithinBounds(paintView.getHeight());
        switch (item) {
            case SPEEDUP:
                return new SpeedupItem(x, y, ITEM_RADIUS);
            case SLOWDOWN:
                return new SlowdownItem(x, y, ITEM_RADIUS);
            case SWAP_AXIS:
                return new SwapAxisItem(x, y, ITEM_RADIUS);
            case ADD_STARS:
                return new AddStarsItem(x, y, ITEM_RADIUS);
            case BUMP:
                return new BumpingItem(x, y, ITEM_RADIUS);
            default:
                throw new IllegalArgumentException("Unknown item type");
        }
    }

    /**
     * Generates a random item (add stars excluded) at a random position.
     *
     * @param paintView the {@link PaintView} where the item should be placed
     * @return the generated item
     */
    public static Item generateItemForOfflineMode(PaintView paintView) {
        Items item = getRandomItemForOfflineMode();
        int x = getRandomIntWithinBounds(paintView.getWidth());
        int y = getRandomIntWithinBounds(paintView.getHeight());
        switch (item) {
            case SPEEDUP:
                return new SpeedupItem(x, y, ITEM_RADIUS);
            case SLOWDOWN:
                return new SlowdownItem(x, y, ITEM_RADIUS);
            case SWAP_AXIS:
                return new SwapAxisItem(x, y, ITEM_RADIUS);
            case BUMP:
                return new BumpingItem(x, y, ITEM_RADIUS);
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
