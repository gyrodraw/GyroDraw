package ch.epfl.sweng.SDP.game.drawing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import ch.epfl.sweng.SDP.LocalDbHandler;
import ch.epfl.sweng.SDP.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static ch.epfl.sweng.SDP.game.VotingPageActivity.disableAnimations;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DrawingActivityTest {
    @Rule
    public final ActivityTestRule<DrawingActivity> activityRule =
            new ActivityTestRule<DrawingActivity>(DrawingActivity.class){

                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    intent.putExtra("RoomID", "0123457890");
                    intent.putExtra("WinningWord", "word1Mock");

                    return intent;
                }
            };

    private PaintView paintView;

    @Before
    public void init(){
        paintView = activityRule.getActivity().findViewById(R.id.paintView);
    }

    @Test
    public void testCanvas() {
        onView(ViewMatchers.withId(R.id.paintView)).perform(click());
    }

    @Test
    public void testDrawToggleReactsCorrectlyToClicking(){
        onView(withId(R.id.flyOrDraw)).check(matches(isNotChecked()));
        onView(withId(R.id.flyOrDraw)).perform(click());
        onView(withId(R.id.flyOrDraw)).check(matches(isChecked()));
        onView(withId(R.id.flyOrDraw)).perform(click());
        onView(withId(R.id.flyOrDraw)).check(matches(isNotChecked()));
    }

    @Test
    public void testClearButtonIsClickable() {
        onView(withId(R.id.clearCanvas)).perform(click());
    }

    @Test
    public void testPaintViewFullyDisplayed() {
        onView(withId(R.id.paintView)).perform(click());
    }

    @Test
    public void testPaintViewGettersSetters(){
        paintView.setCircleX(10);
        paintView.setCircleY(15);
        paintView.setCircleRadius(12);
        paintView.setDraw(true);
        assertTrue(paintView.getCircleX()==10);
        assertTrue(paintView.getCircleY()==15);
        assertTrue(paintView.getCircleRadius()==12);
        assertTrue(paintView.getDraw());
    }

    @Test
    public void testInitWorks(){
        Point point = new Point(100, 100);
        paintView.setSizeAndInit(point);
        paintView.setCircleRadius(10);
        assertTrue(paintView.getCircleX()==40.0);
    }

    @Test
    public void testLocalDbHandler(){
        Paint paint = initializedPaint();

        Path path = new Path();
        path.lineTo(50, 50);

        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = initializedCanvas(bitmap, paint, path);
        canvas.drawColor(Color.WHITE);
        canvas.drawPath(path, paint);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        LocalDbHandler localDbHandler =
                new LocalDbHandler(activityRule.getActivity(), null, 1);
        localDbHandler.addBitmapToDb(bitmap, 100);

        bitmap = compressBitmap(bitmap, 100);

        Bitmap newBitmap = localDbHandler.getLatestBitmapFromDb();

        for(int i = 0; i < 100; ++i){
            for(int j = 0; j < 100; ++j){
                assertTrue(bitmap.getPixel(i,j) == newBitmap.getPixel(i,j));
            }
        }
    }

    private Paint initializedPaint(){
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        return paint;
    }

    private Canvas initializedCanvas(Bitmap bitmap, Paint paint, Path path){
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawPath(path, paint);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return canvas;
    }

    private Bitmap compressBitmap(Bitmap bitmap, int quality){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}