package ch.epfl.sweng.SDP.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Shop {

    private static Shop instance = null;
    private List<ShopItem> itemList;

    public Shop(Map<String, Integer> itemsList) {
        itemList = new ArrayList<>();
        for(Map.Entry<String, Integer> item: itemsList.entrySet()) {
            itemList.add(new ShopItem(item.getKey(), item.getValue()));
        }
    }

    public static Shop getInstance(Map<String, Integer> itemsList) {
        if(instance == null) {
            instance = new Shop(itemsList);
        }

        return instance;
    }

    public void buyItem(ShopItem shopItem) {
        // 1. Remove from the list
        // 2. Withdraw the money (trophies)
        // 3. Update the database
        removeItem(shopItem);
    }

    public void removeItem(ShopItem shopItem) {
        itemList.remove(shopItem);
    }

    public List<ShopItem> getItemList() {
        return itemList;
    }
}
