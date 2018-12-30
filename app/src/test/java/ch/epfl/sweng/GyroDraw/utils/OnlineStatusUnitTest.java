package ch.epfl.sweng.GyroDraw.utils;

import org.junit.Test;

import static ch.epfl.sweng.GyroDraw.utils.OnlineStatus.OFFLINE;
import static ch.epfl.sweng.GyroDraw.utils.OnlineStatus.ONLINE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class OnlineStatusUnitTest {

    @Test
    public void testFromIntegerZero() {
        assertThat(OnlineStatus.fromInteger(0), is(OFFLINE));
    }

    @Test
    public void testFromIntegerOne() {
        assertThat(OnlineStatus.fromInteger(1), is(ONLINE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromIntegerUnknownNumber() {
        OnlineStatus.fromInteger(10);
    }

}
