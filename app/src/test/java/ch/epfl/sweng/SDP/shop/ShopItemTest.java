package ch.epfl.sweng.SDP.shop;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ShopItemTest {

    @Test
    public void setColorItemTest() {
        ShopItem shopItem = new ShopItem(ColorsShop.BLUE, 10);
        shopItem.setColorItem(ColorsShop.YELLOW);
        assertThat(shopItem.getColorItem(), is(ColorsShop.YELLOW));
    }

    @Test
    public void setPriceItemTest() {
        ShopItem shopItem = new ShopItem(ColorsShop.BLUE, 10, true);
        shopItem.setPriceItem(20);
        assertThat(shopItem.getPriceItem(),is(20));
    }

    @Test
    public void setOwnedItemTest() {
        ShopItem shopItem = new ShopItem(ColorsShop.RED, 10, false);
        shopItem.setOwned(true);
        assertTrue(shopItem.getOwned());
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
        assertNotEquals(shopItem1, shopItem2);
    }

    @Test
    public void notEqualsWithNullTest() {
        ShopItem shopItem1 = new ShopItem(ColorsShop.BLUE, 20, false);
        assertNotEquals(shopItem1, null);
    }
}
