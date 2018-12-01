package ch.epfl.sweng.SDP.shop;

import java.util.Comparator;

public class ShopItemComparator implements Comparator<ShopItem> {
    @Override
    public int compare(ShopItem item1, ShopItem item2) {
        return item1.getColorItem().compareTo(item2.getColorItem());
    }
}