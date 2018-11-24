package ch.epfl.sweng.SDP.shop;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ShopItemTest {

    @Test
    public void setColorItemTest() {
        ShopItem shopItem = new ShopItem("blue", 10);
        shopItem.setColorItem("yellow");
        assertEquals(shopItem.getColorItem(), "yellow");
    }

    @Test
    public void setPriceItemTest() {
        ShopItem shopItem = new ShopItem("blue", 10, true);
        shopItem.setPriceItem(20);
        assertEquals(shopItem.getPriceItem(), 20);
    }

    @Test
    public void setOwnedItemTest() {
        ShopItem shopItem = new ShopItem("red", 10, false);
        shopItem.setOwned(true);
        assertTrue(shopItem.getOwned());
    }

    @Test
    public void equalsTest() {
        ShopItem shopItem1 = new ShopItem("blue", 10, false);
        ShopItem shopItem2 = new ShopItem("blue", 10, true);
        assertEquals(shopItem1, shopItem2);
    }

    @Test
    public void notEqualsTest() {
        ShopItem shopItem1 = new ShopItem("blue", 20, false);
        ShopItem shopItem2 = new ShopItem("blue", 10, true);
        assertNotEquals(shopItem1, shopItem2);
    }
}
