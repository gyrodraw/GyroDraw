package ch.epfl.sweng.SDP;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LocalDBHandlerUnitTest {
    @Test
    public void addingAndRetrievingBitmapFromDBGivesTheSameBitmap() {
        LocalDBHandler localDBHandler = new LocalDBHandler(null, null, null, 1);
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);
        Path path = new Path();
        path.lineTo(50, 50);
        canvas.drawPath(path, paint);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        localDBHandler.addBitmapToDB(bitmap);
        assertEquals(localDBHandler.getLatestBitmapFromDB(), bitmap);
    }
}
