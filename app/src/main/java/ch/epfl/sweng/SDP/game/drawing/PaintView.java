package ch.epfl.sweng.SDP.game.drawing;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

import ch.epfl.sweng.SDP.LocalDbHandler;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.FbStorage;


public class PaintView extends View {

    public static final int DRAW_WIDTH = 30;

    private Boolean canDraw = true;
    private Boolean isDrawing = false;

    private Paint[] colors = new Paint[6];
    private Path path = new Path();
    private Paint paintC;


    private Bitmap bitmap;
    private Canvas canvas;

    private float circleX = 0;
    private float circleY = 0;

    private int width;
    private int height;
    private int circleRadius;
    private int color = 0;

    /**
     * Constructor for the view.
     *
     * @param context Context of class
     * @param attrs   Attributes of class
     */
    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Resources res = getResources();
        colors[0] = getPaintWithColor(Color.WHITE);
        colors[1] = getPaintWithColor(Color.BLACK);
        colors[2] = getPaintWithColor(res.getColor(R.color.colorBlue));
        colors[3] = getPaintWithColor(res.getColor(R.color.colorGreen));
        colors[4] = getPaintWithColor(res.getColor(R.color.colorYellow));
        colors[5] = getPaintWithColor(res.getColor(R.color.colorRed));

        paintC = colors[1];
        paintC.setStyle(Paint.Style.STROKE);
        paintC.setStrokeWidth(DRAW_WIDTH / 2);

        circleRadius = DRAW_WIDTH;
    }

    private Paint getPaintWithColor(int color) {
        Paint newPaint = new Paint();
        newPaint.setColor(color);
        newPaint.setStyle(Paint.Style.STROKE);
        newPaint.setStrokeJoin(Paint.Join.ROUND);
        newPaint.setStrokeWidth(DRAW_WIDTH);
        newPaint.setStrokeCap(Paint.Cap.ROUND);
        return newPaint;
    }

    public float getCircleX() {
        return circleX;
    }

    public float getCircleY() {
        return circleY;
    }

    public void setCircle(float circleX, float circleY) {
        this.circleX = sanitizeCoordinate(circleX, width);
        this.circleY = sanitizeCoordinate(circleY, height);
        path.lineTo(this.circleX, this.circleY);
    }

    public int getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
    }

    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Keep coordinates within screen boundaries.
     *
     * @param coordinate coordinate to sanitize
     * @param maxBound   maximum bound
     * @return sanitized coordinate
     */
    private float sanitizeCoordinate(float coordinate, float maxBound) {
        return Math.max(0, Math.min(coordinate, maxBound));
    }

    /**
     * Clears the canvas.
     */
    public void clear() {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        this.width = width;
        this.height = height;
        circleX = width / 2;
        circleY = height / 2;
        clear();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
        if (isDrawing) canvas.drawPath(path, colors[color]);
        canvas.drawCircle(circleX, circleY, circleRadius, paintC);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (canDraw) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawStart();
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    drawEnd();
                    invalidate();
                    break;
                default:
            }
        }
        return true;
    }

    private void drawStart() {
        isDrawing = true;
        circleRadius = 3 * DRAW_WIDTH / 4;
        path.reset();
        path.moveTo(circleX, circleY);
    }

    private void drawEnd() {
        isDrawing = false;
        circleRadius = DRAW_WIDTH;
        path.lineTo(circleX, circleY);
        canvas.drawPath(path, colors[color]);
        path.reset();
    }

    /**
     * Gets called when time for drawing is over.
     * Saves the bitmap in the local DB.
     */
    public void saveCanvasInDb(Context context) {
        drawEnd();
        canDraw = false;
        LocalDbHandler localDbHandler = new LocalDbHandler(context, null, 1);
        FbStorage fbStorage = new FbStorage();
        localDbHandler.addBitmapToDb(bitmap, new ByteArrayOutputStream());
        // Create timestamp as name for image. Will include userID in future
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("" + ts + ".jpg");
        fbStorage.sendBitmapToFireBaseStorage(bitmap, imageRef);
    }
}
