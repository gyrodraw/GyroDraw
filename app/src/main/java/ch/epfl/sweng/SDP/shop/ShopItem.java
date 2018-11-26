package ch.epfl.sweng.SDP.shop;

public class ShopItem {

    private int price;
    private String color;
    private boolean owned;


    /**
     * Constructor of a ShopItem.
     *
     * @param color Color of the item
     * @param price Price of the item
     */
    public ShopItem(String color, int price) {
        this.price = price;
        this.color = color;
        this.owned = false;
    }

    /**
     * Constructor of a ShopItem.
     *
     * @param color Color of the item
     * @param price Price of the item
     * @param owned Is this item owned by the player
     */
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

    public void setColorItem(String color) {
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
        if(obj != null) {
            ShopItem item = (ShopItem) obj;

            return item.getPriceItem() == this.getPriceItem()
                    && (item.getColorItem()).equals(this.getColorItem());
        }
        return false;
    }
}

