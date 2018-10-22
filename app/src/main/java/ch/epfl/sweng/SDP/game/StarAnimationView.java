package ch.epfl.sweng.SDP.game;

import android.animation.TimeAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
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
        private float rotation;
    }

    private static final int INIT_SPEED = 0;
    private static final int SEED = 1337;

    private final ArrayList<Star> stars = new ArrayList<>();
    private final Random rand = new Random(SEED);

    private TimeAnimator timeAnimator;
    private Drawable starDrawable;

    private int starSize;
    private int starSpeed;

    private int height;
    private int width;

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
        starDrawable = ContextCompat.getDrawable(getContext(), R.drawable.star);
        starSize = (int) (Math.max(starDrawable.getIntrinsicWidth(), starDrawable.getIntrinsicHeight()) / 2f);
        starSpeed = (int) (INIT_SPEED * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        this.width = width;
        this.height = height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (final Star star : stars) {
            // Ignore the star if it's outside of the view bounds
            if (star.y + starSize < 0 || star.y - starSize > height) {
                continue;
            }

            final int save = canvas.save();
            canvas.translate(star.x, star.y);

            final float progress = (star.y + starSize) / (2 * height);
            canvas.rotate(star.rotation * 360 * progress);

            starDrawable.setBounds(-starSize, -starSize, starSize, starSize);
            starDrawable.draw(canvas);

            canvas.restoreToCount(save);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        timeAnimator = new TimeAnimator();
        timeAnimator.setTimeListener(new TimeAnimator.TimeListener() {
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
        timeAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        timeAnimator.cancel();
        timeAnimator.setTimeListener(null);
        timeAnimator.removeAllListeners();
        timeAnimator = null;
    }

    private void updateState(float deltaMs) {
        for (int i = 0; i < stars.size(); i++) {
            Star star = stars.get(i);
            star.y += star.speed * deltaMs / 1000f;
            star.speed += deltaMs * 2;

            // Remove when the star is completely gone
            if (star.y - starSize > height) {
                stars.remove(star);
            }
        }
    }

    private void initializeStar(Star star) {
        star.x = width * rand.nextFloat();
        // Subtract the size to 0 (the top of the view)
        // to make sure it starts outside of the view bound
        star.y = -starSize;
        // Add a random offset to create a small delay before the star appears again.
        star.y -= height * rand.nextFloat() / 4f;
        star.speed = starSpeed;
        star.rotation = rand.nextBoolean() ? -1 : 1;
    }

    /**
     * Animates the given number of stars.
     *
     * @param n The number of stars
     */
    public void addStars(int n) {
        for (int i = 0; i < n; i++) {
            final Star star = new Star();
            initializeStar(star);
            stars.add(star);
        }
    }
}