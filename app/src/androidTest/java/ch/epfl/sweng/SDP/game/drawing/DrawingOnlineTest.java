package ch.epfl.sweng.SDP.game.drawing;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.game.VotingPageActivity;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForImages;
import com.google.firebase.database.DataSnapshot;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(AndroidJUnit4.class)
public class DrawingOnlineTest {

    @Rule
    public final ActivityTestRule<DrawingOnline> activityRule =
            new ActivityTestRule<DrawingOnline>(DrawingOnline.class){

                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    intent.putExtra("RoomID", "0123457890");
                    intent.putExtra("WinningWord", "word1Mock");

                    return intent;
                }
            };

    private PaintView paintView;
    private Resources res;
    private DataSnapshot dataSnapshotMock;

    /**
     * Initialise mock elements and get UI elements.
     */
    @Before
    public void init() {
        paintView = activityRule.getActivity().findViewById(R.id.paintView);
        res = activityRule.getActivity().getResources();
        dataSnapshotMock = Mockito.mock(DataSnapshot.class);
    }

    @Test
    public void testCorrectLayout() {
        int layoutId = activityRule.getActivity().getLayoutId();
        assertEquals(layoutId, R.layout.activity_drawing);
    }

    @Test
    public void testCanvas() {
        onView(ViewMatchers.withId(R.id.paintView)).perform(click());
    }

    @Test
    public void testPaintViewFullyDisplayed() {
        onView(withId(R.id.paintView)).perform(click());
    }

    @Test
    public void testPaintViewGettersSetters() {
        paintView.setCircle(10, 15);
        paintView.setCircleRadius(12);
        assertTrue(paintView.getCircleX() == 10);
        assertTrue(paintView.getCircleY() == 15);
        assertTrue(paintView.getCircleRadius() == 12);
    }

    public void testSetCircleWorks() {
        paintView.setCircle(30, -10);
        assertEquals(30, paintView.getCircleX());
        assertEquals(1, paintView.getCircleY());
    }

    @Test
    public void testStateChange() {
        Intents.init();
        when(dataSnapshotMock.getValue(Integer.class)).thenReturn(3);
        activityRule.getActivity().listenerState.onDataChange(dataSnapshotMock);

        intended(hasComponent(VotingPageActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void testListenerTimer() {
        when(dataSnapshotMock.getValue(Integer.class)).thenReturn(5);
        activityRule.getActivity().callOnDataChangeTimer(dataSnapshotMock);
        onView(withId(R.id.timeRemaining)).check(matches(withText("5")));
    }

    @Test
    public void testLocalDbHandler(){
        Bitmap bitmap = initializedBitmap();

        LocalDbHandlerForImages localDbHandler =
                new LocalDbHandlerForImages(activityRule.getActivity(), null, 1);
        localDbHandler.addBitmapToDb(bitmap, 100);

        bitmap = compressBitmap(bitmap, 100);
        Bitmap newBitmap = localDbHandler.getLatestBitmapFromDb();

        bitmapEqualsNewBitmap(bitmap, newBitmap);
    }

    @Test
    public void testFloodFill() {
        int[] source = { Color.BLACK, Color.BLACK, Color.BLACK, Color.WHITE };
        Bitmap bitmap = Bitmap.createBitmap(source,2, 2, Bitmap.Config.ARGB_8888)
                .copy(Bitmap.Config.ARGB_8888, true);
        new BucketTool(bitmap, Color.BLACK, Color.YELLOW).floodFill(0, 0);
        assertEquals(Color.YELLOW, bitmap.getPixel(0, 0));
        assertEquals(Color.YELLOW, bitmap.getPixel(1, 0));
        assertEquals(Color.YELLOW, bitmap.getPixel(0, 1));
        assertEquals(Color.WHITE, bitmap.getPixel(1, 1));
    }

    @Test
    public void testBlackButton() {
        onView(ViewMatchers.withId(R.id.blackButton)).perform(click());
        assertEquals(Color.BLACK, paintView.getColor());
    }

    @Test
    public void testBlueButton() {
        onView(ViewMatchers.withId(R.id.blueButton)).perform(click());
        assertEquals(res.getColor(R.color.colorBlue), paintView.getColor());
    }

    @Test
    public void testGreenButton() {
        onView(ViewMatchers.withId(R.id.greenButton)).perform(click());
        assertEquals(res.getColor(R.color.colorGreen), paintView.getColor());
    }

    @Test
    public void testYellowButton() {
        onView(ViewMatchers.withId(R.id.yellowButton)).perform(click());
        assertEquals(res.getColor(R.color.colorYellow), paintView.getColor());
    }

    @Test
    public void testRedButton() {
        onView(ViewMatchers.withId(R.id.redButton)).perform(click());
        assertEquals(res.getColor(R.color.colorRed), paintView.getColor());
    }

    @Test
    public void testPencilTool() {
        onView(ViewMatchers.withId(R.id.eraserButton)).perform(click());
        onView(ViewMatchers.withId(R.id.pencilButton)).perform(click());
        onView(ViewMatchers.withId(R.id.paintView)).perform(click());
        assertEquals(Color.WHITE,
                paintView.getBitmap().getPixel(paintView.getCircleX(), paintView.getCircleY()));
    }

    @Test
    public void testEraserTool() {
        onView(ViewMatchers.withId(R.id.eraserButton)).perform(click());
        onView(ViewMatchers.withId(R.id.paintView)).perform(click());
        assertEquals(Color.WHITE,
                paintView.getBitmap().getPixel(paintView.getCircleX(), paintView.getCircleY()));
    }

    @Test
    public void testBucketTool() {
        activityRule.getActivity().clear(null);
        onView(ViewMatchers.withId(R.id.bucketButton)).perform(click());
        onView(ViewMatchers.withId(R.id.paintView)).perform(click());
        assertEquals(paintView.getColor(),
                paintView.getBitmap().getPixel(paintView.getCircleX(), paintView.getCircleY()));
    }

    public static Bitmap initializedBitmap() {
        Paint paint = initializedPaint();

        Path path = new Path();
        path.lineTo(50, 50);

        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = initializedCanvas(bitmap, paint, path);
        canvas.drawColor(Color.WHITE);
        canvas.drawPath(path, paint);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return bitmap;
    }

    public static void bitmapEqualsNewBitmap(Bitmap bitmap, Bitmap newBitmap) {
        for(int i = 0; i < 100; ++i){
            for(int j = 0; j < 100; ++j){
                assertEquals(bitmap.getPixel(i, j), newBitmap.getPixel(i, j));
            }
        }
    }

    private static Paint initializedPaint(){
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        return paint;
    }

    private static Canvas initializedCanvas(Bitmap bitmap, Paint paint, Path path){
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawPath(path, paint);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return canvas;
    }

    public static Bitmap compressBitmap(Bitmap bitmap, int quality){
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