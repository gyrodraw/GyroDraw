package ch.epfl.sweng.SDP.game.drawing;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Items {
    SPEEDUP, SLOWDOWN, SWAPAXIS;

    private static final List<Items> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    protected static Items randomItem()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

    protected static Items itemToEnum(Item item) {
        if(item instanceof SpeedupItem) {
            return SPEEDUP;
        } else if (item instanceof SlowdownItem) {
            return SLOWDOWN;
        } else if (item instanceof SwapAxisItem) {
            return SWAPAXIS;
        } else {
            throw new IllegalArgumentException("Unknown Item Type");
        }
    }

}
