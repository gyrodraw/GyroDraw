package ch.epfl.sweng.SDP.shop;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.utils.ColorUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ShopTest {

    @Test
    public void addItemShopTest() {
        ShopItem item1 = new ShopItem("yellow", 10);
        ShopItem item2 = new ShopItem("blue", 20);
        ShopItem item3 = new ShopItem("green", 30);
        List<ShopItem> shopItem = Arrays.asList(item1, item2, item3);

        Shop shop = new Shop();

        shop.addItem(item1);
        shop.addItem(item2);
        shop.addItem(item3);

        assertThat(shop.getItemList(), is(shopItem));
    }

    @Test
    public void removeItemShopTest() {
        ShopItem item1 = new ShopItem("yellow", 10);
        ShopItem item2 = new ShopItem("blue", 20);
        ShopItem item3 = new ShopItem("green", 30);
        List<ShopItem> shopItem = Arrays.asList(item2, item3);

        Shop shop = new Shop();

        shop.addItem(item1);
        shop.addItem(item2);
        shop.addItem(item3);

        shop.removeItem(item1);

        assertThat(shop.getItemList(), is(shopItem));
    }

    @Test
    public void firebaseToListTest() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("blue", "100");
        map.put("red", "200");

        List<ShopItem> listItems = new LinkedList<>();
        listItems.add(new ShopItem("blue", 100));
        listItems.add(new ShopItem("red", 200));

        assertEquals(Shop.firebaseToListShopItem(map), listItems);

    }

    @Test
    public void getColorBlueFromStringTest() {
        assertEquals(ColorUtils.getColorFromString("blue"), R.color.colorBlue);
    }

    @Test
    public void getColorGreenFromStringTest() {
        assertEquals(ColorUtils.getColorFromString("green"), R.color.colorGreen);
    }

    @Test
    public void getColorYellowFromStringTest() {
        assertEquals(ColorUtils.getColorFromString("yellow"), R.color.colorYellow);
    }

    @Test
    public void getColorRedFromStringTest() {
        assertEquals(ColorUtils.getColorFromString("red"), R.color.colorRed);
    }

    @Test (expected = IllegalStateException.class)
    public void getWrongColorFromStringTest() {
        ColorUtils.getColorFromString("rainbow");
    }
}
