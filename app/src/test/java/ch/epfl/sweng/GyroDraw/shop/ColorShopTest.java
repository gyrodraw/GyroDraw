package ch.epfl.sweng.GyroDraw.shop;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
