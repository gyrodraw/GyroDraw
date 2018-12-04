package ch.epfl.sweng.SDP.shop;

public enum ColorsShop {
    BLUE(100), GREEN(150), YELLOW(200), RED(150);

    private int price;

    ColorsShop(int price) {
        this.price = price;
    }

    public int getPrice() {
        return this.price;
    }

    public static ColorsShop getColorFromString(String color) {
        switch (color) {
            case "BLUE":
                return BLUE;
            case "GREEN":
                return GREEN;
            case "YELLOW":
                return YELLOW;
            case "RED":
                return RED;
            default:
                throw new IllegalArgumentException(color + " not found");
        }
    }

}