package ch.epfl.sweng.SDP.utils;

import ch.epfl.sweng.SDP.R;

public class ColorUtils {

    private ColorUtils() {}

    public static int getColorFromString(String color) {
        switch (color) {
            case "blue":
                return R.color.colorBlue;
            case "red":
                return R.color.colorRed;
            case "yellow":
                return R.color.colorYellow;
            case "green":
                return R.color.colorGreen;
            default:
                throw new IllegalStateException();
        }
    }
}
