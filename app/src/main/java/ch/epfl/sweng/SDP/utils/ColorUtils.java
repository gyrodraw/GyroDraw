package ch.epfl.sweng.SDP.utils;

import ch.epfl.sweng.SDP.R;

public class ColorUtils {

    private ColorUtils() {}

    /**
     * Convert the string value of a color into an integer ID.
     * @param color String value of the colors
     * @return The integer ID of the color
     */
    public static int getColorFromString(String color) {
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
                throw new IllegalStateException();
        }
    }
}
