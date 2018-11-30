package ch.epfl.sweng.SDP.shop;

public enum ColorsShop {
    RED(150), BLUE(100), YELLOW(200), GREEN(150);

    private int price;

    ColorsShop(int price) {
        this.price = price;
    }

    public int getPrice() {
        return this.price;
    }

    public static ColorsShop getColorFromString(String color) {
        switch(color) {
            case "BLUE":
                return BLUE;
            case "RED":
                return RED;
            case "YELLOW":
                return YELLOW;
            case "GREEN":
                return GREEN;
            default:
                throw new IllegalArgumentException(color + " not found");
        }
    }

}