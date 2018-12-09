package ch.epfl.sweng.SDP.shop;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class ColorShopTest {

    private ColorsShop color = ColorsShop.BLUE;

    @Test
    public void testGetPrice() {
        assertThat(color.getPrice(), is(15));
    }

    @Test
    public void testGetColorRedFromString() {
        assertThat(ColorsShop.getColorFromString("RED"), is(ColorsShop.RED));
    }

    @Test
    public void testGetColorYellowFromString() {
        assertThat(ColorsShop.getColorFromString("YELLOW"), is(ColorsShop.YELLOW));
    }

    @Test
    public void testGetColorBlueFromString() {
        assertThat(ColorsShop.getColorFromString("BLUE"), is(ColorsShop.BLUE));
    }

    @Test
    public void testGetColorGreenFromString() {
        assertThat(ColorsShop.getColorFromString("GREEN"), is(ColorsShop.GREEN));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUnknownColorFromString() {
        ColorsShop.getColorFromString("MAGENTA");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullString() {
        ColorsShop.getColorFromString(null);
    }

}
