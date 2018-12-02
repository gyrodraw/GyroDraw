package ch.epfl.sweng.SDP.shop;

import android.support.annotation.VisibleForTesting;

import java.util.Objects;

import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;

/**
 * Class representing an item that can be bought in the shop. For the moment, only colors can be bought.
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
        checkPrecondition(price >= 0, "price is negative");
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
        checkPrecondition(price >= 0, "price is negative");
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

    @VisibleForTesting
    void setPriceItem(int price) {
        this.price = price;
    }

    @VisibleForTesting
    void setColorItem(ColorsShop color) {
        this.color = color;
    }

    void setOwned(boolean owned) {
        this.owned = owned;
    }

    boolean getOwned() {
        return this.owned;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ShopItem shopItem = (ShopItem) obj;
        return getPriceItem() == shopItem.getPriceItem()
                && getColorItem() == shopItem.getColorItem();
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, color);
    }
}

