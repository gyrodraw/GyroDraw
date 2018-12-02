package ch.epfl.sweng.SDP.game.drawing.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Enum class representing all different item's types.
 */
public enum Items {
    SPEEDUP, SLOWDOWN, SWAP_AXIS, ADD_STARS, BUMP;

    private static final List<Items> ITEMS =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int ITEMS_SIZE = ITEMS.size();

    private static final List<Items> ITEMS_OFFLINE_MODE = createItemsForOfflineMode();
    private static final int ITEMS_OFFLINE_MODE_SIZE = ITEMS_OFFLINE_MODE.size();

    private static final Random RANDOM = new Random();

    private static List<Items> createItemsForOfflineMode() {
        List<Items> items = new ArrayList<>(ITEMS);
        items.remove(ADD_STARS);
        return Collections.unmodifiableList(items);
    }

    /**
     * Picks a random item class.
     *
     * @return a random item class
     */
    protected static Items getRandomItem() {
        return ITEMS.get(RANDOM.nextInt(ITEMS_SIZE));
    }

    /**
     * Picks a random item class for the offline mode (ADD_STARS excluded).
     *
     * @return a random item class
     */
    protected static Items getRandomItemForOfflineMode() {
        return ITEMS_OFFLINE_MODE.get(RANDOM.nextInt(ITEMS_OFFLINE_MODE_SIZE));
    }

}
