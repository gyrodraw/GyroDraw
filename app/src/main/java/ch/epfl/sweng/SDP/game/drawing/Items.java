package ch.epfl.sweng.SDP.game.drawing;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Items {
    SPEEDUP, SLOWDOWN, SWAPAXIS, ADDSTARS, LOSESTARS;

    private static final List<Items> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static Items randomItem()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

}
