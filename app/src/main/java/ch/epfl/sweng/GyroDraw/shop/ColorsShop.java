package ch.epfl.sweng.GyroDraw.shop;

import static ch.epfl.sweng.GyroDraw.utils.Preconditions.checkPrecondition;

import ch.epfl.sweng.GyroDraw.R;

/**
 * Enum representing the colors in the shop.
 */
public enum ColorsShop {
    PURPLE(20), CYAN(20), PINK(20), ORANGE(20), BROWN(20);

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
     * @throws IllegalArgumentException if the given string does not correspond to a color in the
     * shop
     */
    public static ColorsShop getColorFromString(String color) {
        checkPrecondition(color != null, "color is null");
        switch (color) {
            case "PURPLE":
                return PURPLE;
            case "CYAN":
                return CYAN;
            case "PINK":
                return PINK;
            case "ORANGE":
                return ORANGE;
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
     * @throws IllegalArgumentException if the given string does not correspond to a color in the
     * shop
     */
    public static int getColorIdFromString(String color) {
        switch (color) {
            case "PURPLE":
                return R.color.colorPurple;
            case "CYAN":
                return R.color.colorCyan;
            case "PINK":
                return R.color.colorPink;
            case "ORANGE":
                return R.color.colorOrange;
            case "BROWN":
                return R.color.colorBrown;
            default:
                throw new IllegalArgumentException(color + " resource not found");
        }
    }
}
