package ch.epfl.sweng.SDP.utils;

import ch.epfl.sweng.SDP.R;

/**
 * Utility methods when handling with colors.
 */
public final class ColorUtils {

    private ColorUtils() {
    }

    /**
     * Converts the string value of a color into an integer resource ID.
     *
     * @param color String value of the colors
     * @return The integer resource ID of the color
     * @throws IllegalArgumentException if the given string does not correspond to a color
     *                                  in the shop
     */
    public static int getColorIdFromString(String color) {
        switch (color) {
            case "BLUE":
                return R.color.colorBlue;
            case "RED":
                return R.color.colorRed;
            case "YELLOW":
                return R.color.colorYellow;
            case "GREEN":
                return R.color.colorGreen;
            default:
                throw new IllegalArgumentException(color + " resource not found");
        }
    }
}
