package ch.epfl.sweng.SDP;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class BounceInterpolatorUnitTest {

    private final BounceInterpolator interpolator = new BounceInterpolator(0.1, 10);

    @Test(expected = IllegalArgumentException.class)
    public void amplitudeShouldBeDifferentFromZeroOnConstruction() {
        BounceInterpolator wrongInterpolator = new BounceInterpolator(0, 10);
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