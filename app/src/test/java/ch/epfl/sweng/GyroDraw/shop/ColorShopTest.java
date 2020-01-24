package ch.epfl.sweng.GyroDraw.shop;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class ColorShopTest {

    private ColorsShop color = ColorsShop.CYAN;

    @Test
    public void testGetPrice() {
        assertThat(color.getPrice(), is(20));
    }

    @Test
    public void testGetColorPurpleFromString() {
        assertThat(ColorsShop.getColorFromString("PURPLE"), is(ColorsShop.PURPLE));
    }

    @Test
    public void testGetColorPinkFromString() {
        assertThat(ColorsShop.getColorFromString("PINK"), is(ColorsShop.PINK));
    }

    @Test
    public void testGetColorOrangeFromString() {
        assertThat(ColorsShop.getColorFromString("ORANGE"), is(ColorsShop.ORANGE));
    }

    @Test
    public void testGetColorCyanFromString() {
        assertThat(ColorsShop.getColorFromString("CYAN"), is(ColorsShop.CYAN));
    }

    @Test
    public void testGetColorBrownFromString() {
        assertThat(ColorsShop.getColorFromString("BROWN"), is(ColorsShop.BROWN));
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
