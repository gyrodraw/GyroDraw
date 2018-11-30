package ch.epfl.sweng.SDP.game.drawing.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Enum class to represent all different item types.
 */
public enum Items {
    SPEEDUP, SLOWDOWN, SWAPAXIS, ADDSTARS, BUMP;

    private static final List<Items> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));

    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    /**
     * Function to pick a random item class.
     * @return a random item class
     */
    protected static Items getRandomItem()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

    /**
     * Function to pick a random item class for the offline mode (ADDSTARS excluded).
     * @return a random item class
     */
    protected static Items getRandomItemForOfflineMode() {
        List<Items> values = new ArrayList<>(VALUES);
        values.remove(ADDSTARS);
        return values.get(RANDOM.nextInt(values.size()));
    }

}
