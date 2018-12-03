package ch.epfl.sweng.SDP.shop;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ShopItemTest {

    @Test
    public void setColorItemTest() {
        ShopItem shopItem = new ShopItem(ColorsShop.BLUE, 10, false);
        shopItem.setColorItem(ColorsShop.YELLOW);
        assertThat(shopItem.getColorItem(), is(ColorsShop.YELLOW));
    }

    @Test
    public void setPriceItemTest() {
        ShopItem shopItem = new ShopItem(ColorsShop.BLUE, 10, true);
        shopItem.setPriceItem(20);
        assertThat(shopItem.getPriceItem(), is(20));
    }

    @Test
    public void setOwnedItemTest() {
        ShopItem shopItem = new ShopItem(ColorsShop.RED, 10, false);
        shopItem.setOwned(true);
        assertThat(shopItem.getOwned(), is(true));
    }

    @Test
    public void equalsTest() {
        ShopItem shopItem1 = new ShopItem(ColorsShop.BLUE, 10, false);
        ShopItem shopItem2 = new ShopItem(ColorsShop.BLUE, 10, true);
        assertThat(shopItem1, is(shopItem2));
    }

    @Test
    public void notEqualsTest() {
        ShopItem shopItem1 = new ShopItem(ColorsShop.BLUE, 20, false);
        ShopItem shopItem2 = new ShopItem(ColorsShop.BLUE, 10, true);
        assertThat(shopItem1, is(not(shopItem2)));
    }

    @Test
    public void notEqualsWithNullTest() {
        ShopItem shopItem1 = new ShopItem(ColorsShop.BLUE, 20, false);
        assertThat(shopItem1, is(not(nullValue())));
    }
}
