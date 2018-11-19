package ch.epfl.sweng.SDP.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Shop {

    private static Shop instance = null;
    private List<ShopItem> itemList;

    public Shop() {
        itemList = new ArrayList<>();
    }

    public static Shop getInstance() {
        if(instance == null) {
            instance = new Shop();
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

    public void addItem(ShopItem shopItem) {
        itemList.add(shopItem);
    }

    public List<ShopItem> getItemList() {
        return itemList;
    }

    public static List<ShopItem> firebaseToListShopItem(HashMap<String, String> map) {
        List<ShopItem> listItem = new ArrayList<>();

        if(map != null) {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                int value = Integer.parseInt(entry.getValue().toString());
                listItem.add(new ShopItem(entry.getKey().toString(), value));
            }
        }

        return listItem;
    }
}
