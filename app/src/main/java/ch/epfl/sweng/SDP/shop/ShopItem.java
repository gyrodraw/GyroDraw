package ch.epfl.sweng.SDP.shop;

/**
 * Item that can be bought in the shop for the moment only colors can be bought.
 */
public class ShopItem {

    private int price;
    private ColorsShop color;
    private boolean owned;

    /**
     * Constructor of a ShopItem.
     *
     * @param color Color of the item
     * @param price Price of the item
     */
    public ShopItem(ColorsShop color, int price) {
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
    public ShopItem(ColorsShop color, int price, boolean owned) {
        this.price = price;
        this.color = color;
        this.owned = owned;
    }

    public ColorsShop getColorItem() {
        return color;
    }

    public int getPriceItem() {
        return price;
    }

    public void setPriceItem(int price) {
        this.price = price;
    }

    public void setColorItem(ColorsShop color) {
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

