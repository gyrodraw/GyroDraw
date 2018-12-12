package ch.epfl.sweng.SDP.utils;

import static ch.epfl.sweng.SDP.home.League.createLeague1;
import static ch.epfl.sweng.SDP.home.League.createLeague2;
import static ch.epfl.sweng.SDP.home.League.createLeague3;
import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import ch.epfl.sweng.SDP.BaseActivity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.home.BounceInterpolator;
import ch.epfl.sweng.SDP.home.HomeActivity;
import ch.epfl.sweng.SDP.home.League;

/**
 * Utility class containing methods for layout-related operations.
 */
public final class LayoutUtils {

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
     * Bounces the given view.
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
     * Bounces the given view with default amplitude and default frequency.
     *
     * @param view    the view
     * @param context the context of the view
     */
    public static void bounceButton(final View view, Context context) {
        bounceButton(view, MAIN_AMPLITUDE, MAIN_FREQUENCY, AnimMode.CENTER, context);
    }

    /**
     * Presses the given view.
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
     * Determines if the given point is inside a view.
     *
     * @param posX    the posX coordinate of the point
     * @param posY    the posY coordinate of the point
     * @param view the object to compare
     * @return true if the point is within the view bounds, false otherwise
     */
    public static boolean isPointInsideView(float posX, float posY, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        return (posX > viewX && posX < (viewX + view.getWidth()))
                && (posY > viewY && posY < (viewY + view.getHeight()));
    }

    private static void setExitListener(final View exitButton, final BaseActivity activity,
                                        final int inTranstionId, final int outTransitionId) {
        exitButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressButton(view, AnimMode.CENTER, activity);
                        break;
                    case MotionEvent.ACTION_UP:
                        bounceButton(view, activity);
                        if (isPointInsideView(event.getRawX(), event.getRawY(), view)) {
                            activity.launchActivity(HomeActivity.class);
                            activity.overridePendingTransition(inTranstionId, outTransitionId);
                            activity.finish();
                        }
                        break;
                    default:
                }
                return true;
            }
        });
    }

    /**
     * Sets listener and animation for exit button with a fade transition.
     *
     * @param exitButton the exit button
     * @param activity   the context of the exit button
     */
    public static void setFadingExitListener(final View exitButton, final BaseActivity activity) {
        setExitListener(exitButton, activity, R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Sets listener and animation for exit button with a right slide transition.
     *
     * @param exitButton the exit button
     * @param activity   the context of the exit button
     */
    public static void setSlideRightExitListener(final View exitButton, final BaseActivity activity) {
        setExitListener(exitButton, activity, R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Sets listener and animation for exit button with a left slide transition.
     *
     * @param exitButton the exit button
     * @param activity   the context of the exit button
     */
    public static void setSlideLeftExitListener(final View exitButton, final BaseActivity activity) {
        setExitListener(exitButton, activity, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * Gets the league's image id.
     *
     * @param league the requested league
     * @return the league's image id
     */
    public static int getLeagueImageId(String league) {
        return getLeagueId(league, R.drawable.league_1, R.drawable.league_2, R.drawable.league_3);
    }

    /**
     * Gets the league's color id.
     *
     * @param league the requested league
     * @return the league's color id
     */
    public static int getLeagueColorId(String league) {
        return getLeagueId
                (league, R.color.colorLeague1, R.color.colorLeague2, R.color.colorLeague3);
    }

    /**
     * Gets the league's name id.
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

    /**
     * Enum representing the possible animation modes.
     */
    public enum AnimMode {
        CENTER, LEFT, RIGHT;

        /**
         * Converts an AnimMode to an animation id.
         *
         * @param animMode the requested animation mode
         * @return the matching id
         */
        public static int getPressAnimId(AnimMode animMode) {
            return getAnimId(animMode, R.anim.press, R.anim.press_right, R.anim.press_left);
        }

        /**
         * Converts an AnimMode to an animation id.
         *
         * @param animMode the requested animation mode
         * @return the matching id
         */
        public static int getBounceAnimId(AnimMode animMode) {
            return getAnimId(animMode, R.anim.bounce, R.anim.bounce_right, R.anim.bounce_left);
        }

        private static int getAnimId(AnimMode animMode,
                                     int centerAnimId, int rightAnimId, int leftAnimId) {
            switch (animMode) {
                case CENTER:
                    return centerAnimId;
                case RIGHT:
                    return rightAnimId;
                case LEFT:
                    return leftAnimId;
                default:
                    return centerAnimId;
            }
        }
    }
}
