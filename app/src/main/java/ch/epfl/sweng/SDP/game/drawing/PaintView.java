package ch.epfl.sweng.SDP.game.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

import ch.epfl.sweng.SDP.LocalDbHandler;
import ch.epfl.sweng.SDP.firebase.FbStorage;


public class PaintView extends View {

    public static final int DRAW_WIDTH = 30;

    private Paint paint;
    private Paint paintC;
    private int circleRadius;
    private float circleX = 0;
    private float circleY = 0;
    private Path path;
    private Boolean draw;
    private Bitmap bitmap;
    private Canvas canvas;
    private int width;
    private int height;

    /**
     * Constructor for the view.
     *
     * @param context Context of class
     * @param attrs   Attributes of class
     */
    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paintC = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(DRAW_WIDTH);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paintC.setColor(Color.BLACK);
        paintC.setStyle(Paint.Style.STROKE);
        paintC.setStrokeWidth(DRAW_WIDTH);

        circleRadius = DRAW_WIDTH; //will be modifiable in future, not hardcoded
        draw = false;
        path = new Path();
        path.moveTo(circleX, circleY);
    }

    public float getCircleX() {
        return circleX;
    }

    public void setCircleX(float circleX) {
        this.circleX = sanitizeCoordinate(circleX, width);
    }

    public float getCircleY() {
        return circleY;
    }

    public void setCircleY(float circleY) {
        this.circleY = sanitizeCoordinate(circleY, height);
    }

    public int getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
    }

    public boolean getDraw() {
        return draw;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    /**
     * Keep coordinates within screen boundaries.
     *
     * @param coordinate coordinate to sanitize
     * @param maxBound   maximum bound
     * @return sanitized coordinate
     */
    private float sanitizeCoordinate(float coordinate, float maxBound) {
        if (coordinate < 0) {
            return 0;
        } else if (coordinate > maxBound) {
            return maxBound;
        } else {
            return coordinate;
        }
    }

    /**
     * Initializes coordinates of pen and size of bitmap.
     * Creates a bitmap and a canvas.
     *
     * @param width width of the screen
     * @param height height of the screen
     */
    public void setSizeAndInit(int width, int height) {
        this.width = width;
        this.height = height;
        circleX = width / 2;
        circleY = height / 2;
        bitmap = Bitmap.createBitmap(width,
                (int) (((float) height / width) * width), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    /**
     * Clears the canvas.
     */
    public void clear() {
        path.reset();
    }

    /**
     * Draws the path and circle, if draw is set.
     *
     * @param canvas to draw on
     */
    @Override
    public void onDraw(Canvas canvas) {
        canvas.save();
        if (draw) {
            paintC.setStyle(Paint.Style.FILL);
            paintC.setStrokeWidth(DRAW_WIDTH);
            path.lineTo(circleX, circleY);
        } else {
            paintC.setStyle(Paint.Style.STROKE);
            paintC.setStrokeWidth(DRAW_WIDTH / 2);
        }
        canvas.drawColor(Color.WHITE);
        canvas.drawPath(path, paint);
        canvas.drawCircle(circleX, circleY, circleRadius, paintC);
        canvas.restore();
        path.moveTo(circleX, circleY);
    }

    /**
     * Gets called when time for drawing is over.
     * Saves the bitmap in the local DB.
     */
    public void saveCanvasInDb(Context context) {
        this.draw(canvas);
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
