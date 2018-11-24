package ch.epfl.sweng.SDP.utils;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.home.BounceInterpolator;
import ch.epfl.sweng.SDP.home.HomeActivity;
import ch.epfl.sweng.SDP.home.League;

import static ch.epfl.sweng.SDP.home.League.createLeague1;
import static ch.epfl.sweng.SDP.home.League.createLeague2;
import static ch.epfl.sweng.SDP.home.League.createLeague3;
import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;

public class LayoutUtils {

    private static final int MAIN_FREQUENCY = 10;
    private static final double MAIN_AMPLITUDE = 0.1;

    public static final League[] LEAGUES = new League[]{
            createLeague1(),
            createLeague2(),
            createLeague3()
    };

    private LayoutUtils() {
    }

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
     * @param animMode  the animation mode
     * @param context   the context of the view
     */
    public static void bounceButton(final View view, double amplitude, int frequency,
                                    AnimMode animMode, Context context) {
        checkPrecondition(amplitude != 0, "The amplitude should not be zero");
        final Animation bounce =
                AnimationUtils.loadAnimation(context, AnimMode.getBounceAnimId(animMode));
        BounceInterpolator interpolator = new BounceInterpolator(amplitude, frequency);
        bounce.setInterpolator(interpolator);
        view.startAnimation(bounce);
    }

    /**
     * Bounce the given view with default amplitude and default frequency.
     *
     * @param view    the view
     * @param context the context of the view
     */
    public static void bounceButton(final View view, Context context) {
        bounceButton(view, MAIN_AMPLITUDE, MAIN_FREQUENCY, AnimMode.CENTER, context);
    }

    /**
     * Press the given view.
     *
     * @param view     the view
     * @param animMode the animation mode
     * @param context  the context of the view
     */
    public static void pressButton(View view, AnimMode animMode, Context context) {
        final Animation press =
                AnimationUtils.loadAnimation(context, AnimMode.getPressAnimId(animMode));
        press.setFillAfter(true);
        view.startAnimation(press);
    }

    /**
     * Sets listener and animation for exit button.
     */
    public static void setExitListener(final View exitButton, final Activity activity) {
        exitButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressButton(exitButton, AnimMode.CENTER, activity);
                        break;
                    case MotionEvent.ACTION_UP:
                        bounceButton(view, activity);
                        activity.launchActivity(HomeActivity.class);
                        break;
                    default:
                }
                return true;
            }
        });
    }

    /**
     * Get the league's image id.
     *
     * @param league the requested league
     * @return the league's image id
     */
    public static int getLeagueImageId(String league) {
        return getLeagueId(league, R.drawable.league_1, R.drawable.league_2, R.drawable.league_3);
    }

    /**
     * Get the league's color id.
     *
     * @param league the requested league
     * @return the league's color id
     */
    public static int getLeagueColorId(String league) {
        return getLeagueId
                (league, R.color.colorLeague1, R.color.colorLeague2, R.color.colorLeague3);
    }

    /**
     * Get the league's name id.
     *
     * @param league the requested league
     * @return the league's name id
     */
    public static int getLeagueTextId(String league) {
        return getLeagueId(league, R.string.league_1, R.string.league_2, R.string.league_3);
    }

    private static int getLeagueId(String league, int league1Id, int league2Id, int league3Id) {
        if (league.equals(LEAGUES[0].getName())) {
            return league1Id;
        }
        if (league.equals(LEAGUES[1].getName())) {
            return league2Id;
        }
        return league3Id;
    }

    public enum AnimMode {
        CENTER, LEFT, RIGHT;

        /**
         * Converts an AnimMode to an animation id.
         *
         * @param animMode the requested animation mode
         * @return the matching id
         */
        public static int getPressAnimId(AnimMode animMode) {
            switch (animMode) {
                case CENTER:
                    return R.anim.press;
                case RIGHT:
                    return R.anim.press_right;
                case LEFT:
                    return R.anim.press_left;
            }
            return R.anim.press;
        }

        /**
         * Converts an AnimMode to an animation id.
         *
         * @param animMode the requested animation mode
         * @return the matching id
         */
        public static int getBounceAnimId(AnimMode animMode) {
            switch (animMode) {
                case CENTER:
                    return R.anim.bounce;
                case RIGHT:
                    return R.anim.bounce_right;
                case LEFT:
                    return R.anim.bounce_left;
            }
            return R.anim.bounce;
        }
    }
}

