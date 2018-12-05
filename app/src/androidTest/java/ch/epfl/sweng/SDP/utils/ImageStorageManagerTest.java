package ch.epfl.sweng.SDP.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.test.rule.ActivityTestRule;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.game.drawing.DrawingOffline;
import ch.epfl.sweng.SDP.home.HomeActivity;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ImageStorageManagerTest {

    @Rule
    public final ActivityTestRule<HomeActivity> activityRule =
            new ActivityTestRule<>(HomeActivity.class);

    @Test
    public void saveImage() {
        String imgName = "TEST";
        Bitmap bm = BitmapFactory.decodeResource(activityRule.getActivity()
                .getResources(), R.drawable.league_1);
        ImageStorageManager.saveImage(bm, imgName, activityRule.
                getActivity().getApplicationContext());

        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+ "/Camera/Your_Directory_Name/Image-" + imgName + ".png";
        File myDir = new File(root);
        assertThat(myDir.exists(), is(true));

    }

}