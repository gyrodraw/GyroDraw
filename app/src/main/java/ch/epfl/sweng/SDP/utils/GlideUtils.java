package ch.epfl.sweng.SDP.utils;

import android.widget.ImageView;

import ch.epfl.sweng.SDP.BaseActivity;
import ch.epfl.sweng.SDP.R;

import com.bumptech.glide.Glide;

/**
 * Utility class used to display animations using Glide.
 */
public final class GlideUtils {

    private GlideUtils() {
    }

    /**
     * Starts the background animation in the given activity.
     *
     * @param activity the activity in which the animation should be displayed
     */
    public static void startBackgroundAnimation(BaseActivity activity) {
        Glide.with(activity).load(R.drawable.background_animation)
                .into((ImageView) activity.findViewById(R.id.backgroundAnimation));
    }

    /**
     * Starts the dots waiting animation in the given activity.
     *
     * @param activity the activity in which the animation should be displayed
     */
    public static void startDotsWaitingAnimation(BaseActivity activity) {
        Glide.with(activity).load(R.drawable.waiting_animation_dots)
                .into((ImageView) activity.findViewById(R.id.waitingAnimationDots));
    }

    /**
     * Starts the square waiting animation in the given activity.
     *
     * @param activity the activity in which the animation should be displayed
     */
    public static void startSquareWaitingAnimation(BaseActivity activity) {
        Glide.with(activity).load(R.drawable.waiting_animation_square)
                .into((ImageView) activity.findViewById(R.id.waitingAnimationSquare));
    }
}
