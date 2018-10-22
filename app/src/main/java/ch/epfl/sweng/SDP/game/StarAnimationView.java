package ch.epfl.sweng.SDP.game;

import android.animation.TimeAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

import ch.epfl.sweng.SDP.R;

/**
 * Animation of stars when a player votes for the current picture
 */
public class StarAnimationView extends View {

    private static class Star {
        private float x;
        private float y;
        private float speed;
    }

    private static final int BASE_SPEED_DP_PER_S = 200;
    private static final int COUNT = 32;
    private static final int SEED = 1337;

    private final Star[] mStars = new Star[COUNT];
    private final Random mRnd = new Random(SEED);

    private TimeAnimator mTimeAnimator;
    private Drawable mDrawable;

    private float starSize;
    private float starSpeed;

    /**
     * @see View#View(Context)
     */
    public StarAnimationView(Context context) {
        super(context);
        init();
    }

    /**
     * @see View#View(Context, AttributeSet)
     */
    public StarAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * @see View#View(Context, AttributeSet, int)
     */
    public StarAnimationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mDrawable = ContextCompat.getDrawable(getContext(), R.drawable.star);
        starSize = Math.max(mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight()) / 2f;
        starSpeed = BASE_SPEED_DP_PER_S * getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);

        // The starting position is dependent on the size of the view,
        // which is why the model is initialized here, when the view is measured.
        for (int i = 0; i < mStars.length; i++) {
            final Star star = new Star();
            initializeStar(star, width, height);
            mStars[i] = star;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int viewHeight = getHeight();
        for (final Star star : mStars) {
            // Ignore the star if it's outside of the view bounds
            if (star.y + starSize < 0 || star.y - starSize > viewHeight) {
                continue;
            }

            // Save the current canvas state
            final int save = canvas.save();

            // Move the canvas to the center of the star
            canvas.translate(star.x, star.y);

            // Rotate the canvas based on how far the star has moved
            final float progress = (star.y + starSize) / viewHeight;
            canvas.rotate(360 * progress);

            // Prepare the size
            final int size = Math.round(starSize);
            mDrawable.setBounds(-size, -size, size, size);

            // Draw the star to the canvas
            mDrawable.draw(canvas);

            // Restore the canvas to it's previous position and rotation
            canvas.restoreToCount(save);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mTimeAnimator = new TimeAnimator();
        mTimeAnimator.setTimeListener(new TimeAnimator.TimeListener() {
            @Override
            public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
                if (!ViewCompat.isLaidOut(StarAnimationView.this)) {
                    // Ignore all calls before the view has been measured and laid out.
                    return;
                }
                updateState(deltaTime);
                invalidate();
            }
        });
        mTimeAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTimeAnimator.cancel();
        mTimeAnimator.setTimeListener(null);
        mTimeAnimator.removeAllListeners();
        mTimeAnimator = null;
    }

    /**
     * Progress the animation by moving the stars based on the elapsed time
     *
     * @param deltaMs time delta since the last frame, in millis
     */
    private void updateState(float deltaMs) {
        // Converting to seconds since PX/S constants are easier to understand
        final float deltaSeconds = deltaMs / 1000f;
        final int viewWidth = getWidth();
        final int viewHeight = getHeight();

        for (final Star star : mStars) {
            // Move the star based on the elapsed time and it's speed
            star.y -= star.speed * deltaSeconds;

            // If the star is completely outside of the view bounds after
            // updating it's position, recycle it.
            if (star.y + starSize < 0) {
                initializeStar(star, viewWidth, viewHeight);
            }
        }
    }

    /**
     * Initialize the given star by randomizing it's position, scale and alpha
     *
     * @param star       the star to initialize
     * @param viewWidth  the view width
     * @param viewHeight the view height
     */
    private void initializeStar(Star star, int viewWidth, int viewHeight) {
        star.x = viewWidth * mRnd.nextFloat();
        // Subtract the size to 0 (the top of the view)
        // to make sure it starts outside of the view bound
        star.y = -starSize;
        // Add a random offset to create a small delay before the
        // star appears again.
        star.y += viewHeight * mRnd.nextFloat() / 4f;
        star.speed = starSpeed;
    }
}