package ch.epfl.sweng.GyroDraw.shop;

import static ch.epfl.sweng.GyroDraw.shop.ColorsShop.getColorIdFromString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import ch.epfl.sweng.GyroDraw.R;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;

public class ShopTest {

    @Test
    public void addItemShopTest() {
        ShopItem item1 = new ShopItem(ColorsShop.CYAN, 20);
        ShopItem item2 = new ShopItem(ColorsShop.PINK, 20);
        ShopItem item3 = new ShopItem(ColorsShop.PURPLE, 20);
        List<ShopItem> shopItem = Arrays.asList(item1, item2, item3);

        Shop shop = new Shop();

        shop.addItem(item1);
        shop.addItem(item2);
        shop.addItem(item3);

        assertThat(shop.getItemList(), is(shopItem));
    }

    @Test
    public void removeItemShopTest() {
        ShopItem item1 = new ShopItem(ColorsShop.CYAN, 20);
        ShopItem item2 = new ShopItem(ColorsShop.PINK, 20);
        ShopItem item3 = new ShopItem(ColorsShop.PURPLE, 20);
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
        map.put("CYAN", "100");
        map.put("PURPLE", "200");

        List<ShopItem> listItems = new LinkedList<>();
        listItems.add(new ShopItem(ColorsShop.CYAN, 100));
        listItems.add(new ShopItem(ColorsShop.PURPLE, 200));

        assertThat(Shop.firebaseToListShopItem(map), is(listItems));

    }

    @Test
    public void getColorPurpleFromStringTest() {
        assertThat(getColorIdFromString("PURPLE"), is(R.color.colorPurple));
    }


    @Test
    public void getColorCyanFromStringTest() {
        assertThat(getColorIdFromString("CYAN"), is(R.color.colorCyan));
    }

    @Test
    public void getColorOrangeFromStringTest() {
        assertThat(getColorIdFromString("ORANGE"), is(R.color.colorOrange));
    }

    @Test
    public void getColorPinkFromStringTest() {
        assertThat(getColorIdFromString("PINK"), is(R.color.colorPink));
    }

    @Test
    public void getColorBrownFromStringTest() {
        assertThat(getColorIdFromString("BROWN"), is(R.color.colorBrown));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getWrongColorFromStringTest() {
        getColorIdFromString("RAINBOW");
    }
}