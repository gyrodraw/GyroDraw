package ch.epfl.sweng.SDP.shop;

public class ShopItem {

    private int price;
    private String color;


    public ShopItem(String color, int price) {
        this.price = price;
        this.color = color;
    }

    public String getColorItem() {
        return color;
    }

    public int getPriceItem() {
        return price;
    }

    public void setPriceItem(int price) {
        this.price = price;
    }

    public void setColorItem(String item) {
        this.color = color;
    }
}

