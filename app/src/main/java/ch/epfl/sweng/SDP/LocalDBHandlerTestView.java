package ch.epfl.sweng.SDP;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LocalDBHandlerTestView extends AppCompatActivity {

    LocalDBHandler localDBHandler;
    private Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
    private Canvas canvas = new Canvas(bitmap);
    private Paint paint = new Paint();
    private Path path = new Path();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localdbtestview);
        localDBHandler = new LocalDBHandler(LocalDBHandlerTestView.this, "myImages.db", null, 1);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);

        path.lineTo(50, 50);
        canvas.drawPath(path, paint);
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    public void clickAndAdd(View view){
        localDBHandler.addBitmapToDB(bitmap);
        localDBHandler.getLatestBitmapFromDB();
    }

}
