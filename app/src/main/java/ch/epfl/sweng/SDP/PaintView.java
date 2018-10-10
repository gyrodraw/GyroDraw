package ch.epfl.sweng.SDP;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;


public class PaintView extends View {

    private Paint paint;
    private Paint paintC;
    private int circleRadius;
    private float circleX;
    private float circleY;
    private Path path;
    private Boolean draw;

    public PaintView(Context context) {
        this(context, null);
    }

    /**
     * constructor for the view
     * @param context 
     * @param attrs
     */
    public PaintView(Context context, AttributeSet attrs){
        super(context, attrs);
        setFocusable(true);
        paint = new Paint();
        paintC = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paintC.setColor(Color.RED);
        paintC.setStyle(Paint.Style.STROKE);
        paintC.setStrokeJoin(Paint.Join.ROUND);
        paintC.setStrokeWidth(10);
        paintC.setStrokeCap(Paint.Cap.ROUND);

        circleRadius = 10; //will be modifiable in future, not hardcoded
        circleX = 0;
        circleY = 0;
        draw = false;
        path = new Path();
        path.moveTo(circleX, circleY);
    }

    public float getCircleX() {
        return circleX;
    }

    public float getCircleY(){
        return circleY;
    }

    public int getCircleRadius() {
        return circleRadius;
    }

    public boolean getDraw(){
        return draw;
    }

    public void setCircleX(float x){
        circleX = x;
    }

    public void setCircleY(float y){
        circleY = y;
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
    }

    public void setDraw(boolean d){
        draw = d;
    }

    /**
     * clears the canvas
     */
    public void clear() {
        path.reset();
    }

    /**
     * draws the path and circle, if draw is set
     * @param canvas to draw on
     */
    public void onDraw(Canvas canvas){
        canvas.save();
        if(draw){
            paintC.setStyle(Paint.Style.FILL);
            paintC.setStrokeWidth(10);
            path.lineTo(circleX, circleY);
        } else {
            paintC.setStyle(Paint.Style.STROKE);
            paintC.setStrokeWidth(5);
        }
        canvas.drawCircle(circleX, circleY, circleRadius, paintC);
        canvas.drawPath(path, paint);
        canvas.restore();
        path.moveTo(circleX, circleY);
    }
}
