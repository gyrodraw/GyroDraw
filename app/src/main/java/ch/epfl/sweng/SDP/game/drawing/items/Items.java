package ch.epfl.sweng.SDP.game.drawing.items;

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
     * @return random item class
     */
    protected static Items randomItem()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

}
