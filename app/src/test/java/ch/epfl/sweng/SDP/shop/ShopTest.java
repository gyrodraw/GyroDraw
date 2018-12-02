package ch.epfl.sweng.SDP.shop;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.utils.ColorUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ShopTest {

    @Test
    public void addItemShopTest() {
        ShopItem item1 = new ShopItem(ColorsShop.YELLOW, 10, false);
        ShopItem item2 = new ShopItem(ColorsShop.BLUE, 20, false);
        ShopItem item3 = new ShopItem(ColorsShop.GREEN, 30, false);
        List<ShopItem> shopItem = Arrays.asList(item1, item2, item3);

        Shop shop = new Shop();

        shop.addItem(item1);
        shop.addItem(item2);
        shop.addItem(item3);

        assertThat(shop.getItemList(), is(shopItem));
    }

    @Test
    public void removeItemShopTest() {
        ShopItem item1 = new ShopItem(ColorsShop.YELLOW, 10, false);
        ShopItem item2 = new ShopItem(ColorsShop.BLUE, 20, false);
        ShopItem item3 = new ShopItem(ColorsShop.GREEN, 30, false);
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
        listItems.add(new ShopItem(ColorsShop.BLUE, 100, false));
        listItems.add(new ShopItem(ColorsShop.RED, 200, false));

        assertThat(Shop.firebaseToListShopItem(map), is(listItems));

    }

    @Test
    public void getColorBlueFromStringTest() {
        assertThat(ColorUtils.getColorFromString("BLUE"), is(R.color.colorBlue));
    }

    @Test
    public void getColorGreenFromStringTest() {
        assertThat(ColorUtils.getColorFromString("GREEN"), is(R.color.colorGreen));
    }

    @Test
    public void getColorYellowFromStringTest() {
        assertThat(ColorUtils.getColorFromString("YELLOW"), is(R.color.colorYellow));
    }

    @Test
    public void getColorRedFromStringTest() {
        assertThat(ColorUtils.getColorFromString("RED"), is(R.color.colorRed));
    }

    @Test (expected = IllegalStateException.class)
    public void getWrongColorFromStringTest() {
        ColorUtils.getColorFromString("RAINBOW");
    }
}
