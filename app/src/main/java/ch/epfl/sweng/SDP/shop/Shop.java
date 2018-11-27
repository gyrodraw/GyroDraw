package ch.epfl.sweng.SDP.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Shop {

    private List<ShopItem> itemList;

    public Shop() {
        itemList = new ArrayList<>();
    }

    public void removeItem(ShopItem shopItem) {
        itemList.remove(shopItem);
    }

    public void addItem(ShopItem shopItem) {
        itemList.add(shopItem);
    }

    public List<ShopItem> getItemList() {
        return itemList;
    }

    /**
     * Convert an hashmap into a list of shop items.
     * @param map Map of the colors and prices
     * @return List of ShopItems
     */
    public static List<ShopItem> firebaseToListShopItem(HashMap<String, String> map) {
        List<ShopItem> listItem = new ArrayList<>();

        if(map != null) {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                int value = Integer.parseInt(entry.getValue().toString());
                listItem.add(new ShopItem(ColorsShop.getColorFromString(entry.getKey().toString()),
                        value));
            }
        }

        return listItem;
    }
}
