package ch.epfl.sweng.SDP.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.home.BounceInterpolator;

public class AnimUtils {

    private static final int MAIN_FREQUENCY = 10;
    private static final double MAIN_AMPLITUDE = 0.1;

    private AnimUtils() {}

    public static int getMainFrequency() {
        return MAIN_FREQUENCY;
    }

    public static double getMainAmplitude() {
        return MAIN_AMPLITUDE;
    }

    /**
     * Bounce the given view.
     *
     * @param view      the view
     * @param amplitude the amplitude of the bounce
     * @param frequency the frequency of the bounce
     * @param context   the context of the view
     */
    public static void bounceButton(final View view, double amplitude,
                                    int frequency, Context context) {
        assert amplitude != 0;
        final Animation bounce = AnimationUtils.loadAnimation(context, R.anim.bounce);
        BounceInterpolator interpolator = new BounceInterpolator(amplitude, frequency);
        bounce.setInterpolator(interpolator);
        view.startAnimation(bounce);
    }

    /**
     * Press the given view.
     *
     * @param view      the view
     * @param context   the context of the view
     */
    public static void pressButton(View view, Context context) {
        final Animation press = AnimationUtils.loadAnimation(context, R.anim.press);
        press.setFillAfter(true);
        view.startAnimation(press);
    }
}
