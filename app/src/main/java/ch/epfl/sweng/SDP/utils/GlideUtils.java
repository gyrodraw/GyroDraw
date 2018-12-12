package ch.epfl.sweng.SDP.utils;

import android.widget.ImageView;
import ch.epfl.sweng.SDP.BaseActivity;
import ch.epfl.sweng.SDP.R;
import com.bumptech.glide.Glide;

public final class GlideUtils {
    private GlideUtils() {}

    public static void startBackgroundAnimation(BaseActivity activity, int resId) {
        Glide.with(activity).load(R.drawable.background_animation)
                .into((ImageView) activity.findViewById(resId));
    }

    public static void startDotsWaitingAnimation(BaseActivity activity, int resId) {
        Glide.with(activity).load(R.drawable.waiting_animation_dots)
                .into((ImageView) activity.findViewById(resId));
    }

    public static void startSquareWaitingAnimation(BaseActivity activity, int resId) {
        Glide.with(activity).load(R.drawable.waiting_animation_square)
                .into((ImageView) activity.findViewById(resId));
    }
}
