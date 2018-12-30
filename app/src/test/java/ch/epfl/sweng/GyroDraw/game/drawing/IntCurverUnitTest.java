package ch.epfl.sweng.GyroDraw.game.drawing;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class IntCurverUnitTest {

    @Test
    public void testCurverInitialization() {
        assertThat((new IntCurver(5, 0)).getValue(), is(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitializationWithIllegalArgument() {
        new IntCurver(-1, 0);
    }

    @Test
    public void testCurverSetMethod() {
        IntCurver intCurver = new IntCurver(5, 0);
        intCurver.setValue(5);
        assertThat(intCurver.getValue(), is(5));
    }

    @Test
    public void testCurverAddMethod() {
        IntCurver intCurver = new IntCurver(3, 0);
        for (int i = 0; i < 3; ++i) {
            intCurver.addValue(3);
            assertThat(intCurver.getValue(), is(i + 1));
        }
    }

}