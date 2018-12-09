package ch.epfl.sweng.SDP.shop;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.sweng.SDP.R;

import static ch.epfl.sweng.SDP.shop.ColorsShop.getColorIdFromString;
import static org.hamcrest.CoreMatchers.is;
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

        assertThat(Shop.firebaseToListShopItem(map), is(listItems));

    }

    @Test
    public void getColorBlueFromStringTest() {
        assertThat(getColorIdFromString("BLUE"), is(R.color.colorBlue));
    }

    @Test
    public void getColorGreenFromStringTest() {
        assertThat(getColorIdFromString("GREEN"), is(R.color.colorGreen));
    }

    @Test
    public void getColorYellowFromStringTest() {
        assertThat(getColorIdFromString("YELLOW"), is(R.color.colorYellow));
    }

    @Test
    public void getColorRedFromStringTest() {
        assertThat(getColorIdFromString("RED"), is(R.color.colorRed));
    }

    @Test (expected = IllegalArgumentException.class)
    public void getWrongColorFromStringTest() {
        getColorIdFromString("RAINBOW");
    }
}
