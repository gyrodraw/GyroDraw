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
        ShopItem item1 = new ShopItem(ColorsShop.YELLOW, 10);
        ShopItem item2 = new ShopItem(ColorsShop.BLUE, 20);
        ShopItem item3 = new ShopItem(ColorsShop.GREEN, 30);
        List<ShopItem> shopItem = Arrays.asList(item1, item2, item3);

        Shop shop = new Shop();

        shop.addItem(item1);
        shop.addItem(item2);
        shop.addItem(item3);

        assertThat(shop.getItemList(), is(shopItem));
    }

    @Test
    public void removeItemShopTest() {
        ShopItem item1 = new ShopItem(ColorsShop.YELLOW, 10);
        ShopItem item2 = new ShopItem(ColorsShop.BLUE, 20);
        ShopItem item3 = new ShopItem(ColorsShop.GREEN, 30);
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
        map.put("BLUE", "100");
        map.put("RED", "200");

        List<ShopItem> listItems = new LinkedList<>();
        listItems.add(new ShopItem(ColorsShop.BLUE, 100));
        listItems.add(new ShopItem(ColorsShop.RED, 200));

        assertEquals(Shop.firebaseToListShopItem(map), listItems);

    }

    @Test
    public void getColorBlueFromStringTest() {
        assertEquals(ColorUtils.getColorFromString("BLUE"), R.color.colorBlue);
    }

    @Test
    public void getColorGreenFromStringTest() {
        assertEquals(ColorUtils.getColorFromString("GREEN"), R.color.colorGreen);
    }

    @Test
    public void getColorYellowFromStringTest() {
        assertEquals(ColorUtils.getColorFromString("YELLOW"), R.color.colorYellow);
    }

    @Test
    public void getColorRedFromStringTest() {
        assertEquals(ColorUtils.getColorFromString("RED"), R.color.colorRed);
    }

    @Test (expected = IllegalStateException.class)
    public void getWrongColorFromStringTest() {
        ColorUtils.getColorFromString("RAINBOW");
    }
}
