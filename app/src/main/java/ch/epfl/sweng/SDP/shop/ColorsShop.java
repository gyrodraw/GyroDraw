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
            case "RED":
                return RED;
            case "YELLOW":
                return YELLOW;
            case "GREEN":
                return GREEN;
            default:
                throw new IllegalStateException();
        }
    }

}