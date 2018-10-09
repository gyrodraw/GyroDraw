package ch.epfl.sweng.SDP;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class PaintView extends View {

    private static final String TAG = "PaintViewID";
    private Paint paint;
    private Paint paintc;
    public int circleRadius;
    public float circleX;
    public float circleY;
    private Path path;
    public Boolean draw;

    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs){
        super(context, attrs);
        setFocusable(true);
        paint = new Paint();
        paintc = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paintc.setColor(Color.RED);
        paintc.setStyle(Paint.Style.STROKE);
        paintc.setStrokeJoin(Paint.Join.ROUND);
        paintc.setStrokeWidth(10);
        paintc.setStrokeCap(Paint.Cap.ROUND);

        circleRadius = 10;
        circleX = 0;
        circleY = 0;
        draw = false;
        path = new Path();
        path.moveTo(circleX, circleY);
    }

    public void clear() {
        path.reset();
    }


    public void onDraw(Canvas canvas){
        canvas.save();
        if(draw){
            paintc.setStyle(Paint.Style.FILL);
            paintc.setStrokeWidth(10);
            path.lineTo(circleX, circleY);
        } else {
            paintc.setStyle(Paint.Style.STROKE);
            paintc.setStrokeWidth(5);
        }
        canvas.drawCircle(circleX, circleY, circleRadius, paintc);
        canvas.drawPath(path, paint);
        canvas.restore();
        path.moveTo(circleX, circleY);
    }
}
