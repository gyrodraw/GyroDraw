package ch.epfl.sweng.GyroDraw.utils;

import org.junit.Test;

public class BounceInterpolatorUnitTest {

    private final BounceInterpolator interpolator = new BounceInterpolator(0.1, 10);

    @Test(expected = IllegalArgumentException.class)
    public void amplitudeShouldBeDifferentFromZeroOnConstruction() {
        new BounceInterpolator(0, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getInterpolationWithNegativeTime() {
        interpolator.getInterpolation(-2.0f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getInterpolationWithGreaterThanOneTime() {
        interpolator.getInterpolation(12f);
    }

    @Test
    public void getInterpolationWithValidTime() {
        interpolator.getInterpolation(0.5f);
    }

    @Test
    public void getInterpolationWithEqualToOneTime() {
        interpolator.getInterpolation(1f);
    }

    @Test
    public void getInterpolationWithEqualToZeroTime() {
        interpolator.getInterpolation(0f);
    }
}