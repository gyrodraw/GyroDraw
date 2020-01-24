package ch.epfl.sweng.GyroDraw.shop;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class ShopItemTest {

    @Test
    public void setOwnedItemTest() {
        ShopItem shopItem = new ShopItem(ColorsShop.CYAN, 20, false);
        shopItem.setOwned(true);
        assertThat(shopItem.getOwned(), is(true));
    }

    @Test
    public void equalsTest() {
        ShopItem shopItem1 = new ShopItem(ColorsShop.CYAN, 20, false);
        ShopItem shopItem2 = new ShopItem(ColorsShop.CYAN, 20, true);
        assertThat(shopItem1, is(shopItem2));
    }

    @Test
    public void notEqualsTest() {
        ShopItem shopItem1 = new ShopItem(ColorsShop.CYAN, 20, false);
        ShopItem shopItem2 = new ShopItem(ColorsShop.CYAN, 10, true);
        assertThat(shopItem1, is(not(shopItem2)));
    }

    @Test
    public void notEqualsWithNullTest() {
        ShopItem shopItem1 = new ShopItem(ColorsShop.CYAN, 20, false);
        assertThat(shopItem1, is(not(nullValue())));
    }
}
