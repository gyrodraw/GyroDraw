package ch.epfl.sweng.SDP.shop;

public class ShopItem {

    private int price;
    private String color;
    private boolean owned;


    public ShopItem(String color, int price) {
        this.price = price;
        this.color = color;
        this.owned = false;
    }

    public ShopItem(String color, int price, boolean owned) {
        this.price = price;
        this.color = color;
        this.owned = owned;
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

    public void setOwned(boolean owned) {
        this.owned = owned;
    }

    public boolean getOwned() {
        return this.owned;
    }

    @Override
    public boolean equals(Object obj) {
        ShopItem item = (ShopItem) obj;

        return item.getPriceItem() == this.getPriceItem() &&
                (item.getColorItem()).equals(this.getColorItem());
    }
}

