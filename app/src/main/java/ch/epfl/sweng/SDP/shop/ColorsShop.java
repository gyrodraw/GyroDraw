package ch.epfl.sweng.SDP.shop;

import ch.epfl.sweng.SDP.R;

import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;

/**
 * Enum representing the colors in the shop.
 */
public enum ColorsShop {
    PURPLE(20), BLUE(15), CYAN(20), GREEN(15), YELLOW(15), PINK(20), ORANGE(20), RED(15), BROWN(20);

    private int price;

    ColorsShop(int price) {
        checkPrecondition(price >= 0, "price is negative");
        this.price = price;
    }

    public int getPrice() {
        return this.price;
    }

    /**
     * Converts the given string to the related color.
     *
     * @param color the string describing the color
     * @return the color represented by the string
     * @throws IllegalArgumentException if the given string does not correspond to a color
     *                                  in the shop
     */
    public static ColorsShop getColorFromString(String color) {
        checkPrecondition(color != null, "color is null");
        switch (color) {
            case "PURPLE":
                return PURPLE;
            case "BLUE":
                return BLUE;
            case "CYAN":
                return CYAN;
            case "GREEN":
                return GREEN;
            case "YELLOW":
                return YELLOW;
            case "ORANGE":
                return ORANGE;
            case "PINK":
                return PINK;
            case "RED":
                return RED;
            case "BROWN":
                return BROWN;
            default:
                throw new IllegalArgumentException(color + " not found");
        }
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
            case "PURPLE":
                return R.color.colorPurple;
            case "BLUE":
                return R.color.colorBlue;
            case "CYAN":
                return R.color.colorCyan;
            case "GREEN":
                return R.color.colorGreen;
            case "YELLOW":
                return R.color.colorYellow;
            case "ORANGE":
                return R.color.colorOrange;
            case "PINK":
                return R.color.colorPink;
            case "RED":
                return R.color.colorRed;
            case "BROWN":
                return R.color.colorBrown;
            default:
                throw new IllegalArgumentException(color + " resource not found");
        }
    }
}
