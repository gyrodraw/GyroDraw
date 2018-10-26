package ch.epfl.sweng.SDP.game.drawing;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

import ch.epfl.sweng.SDP.LocalDbHandler;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.FbStorage;


public class PaintView extends View {

    public static final int DRAW_WIDTH = 30;

    private Paint paintC;
    private int circleRadius;
    private float circleX = 0;
    private float circleY = 0;
    private int color = 0;
    private Path[] paths = new Path[5];
    private Paint[] colors = new Paint[5];
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

        Resources res = getResources();
        colors[0] = getPaintWithColor(Color.BLACK);
        colors[1] = getPaintWithColor(res.getColor(R.color.colorBlue));
        colors[2] = getPaintWithColor(res.getColor(R.color.colorGreen));
        colors[3] = getPaintWithColor(res.getColor(R.color.colorYellow));
        colors[4] = getPaintWithColor(res.getColor(R.color.colorRed));

        for (int i = 0; i < paths.length; i++) {
            paths[i] = new Path();
        }

        paintC = new Paint();
        paintC.setColor(Color.BLACK);
        paintC.setStyle(Paint.Style.STROKE);
        paintC.setStrokeWidth(DRAW_WIDTH / 2);

        circleRadius = DRAW_WIDTH; //will be modifiable in future, not hardcoded
        draw = false;
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
     * @param width  width of the screen
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
        for (Path path : paths) path.reset();
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
            circleRadius = 3 * DRAW_WIDTH / 4;
            paths[color].lineTo(circleX, circleY);
        } else {
            paintC.setStyle(Paint.Style.STROKE);
            circleRadius = DRAW_WIDTH;
        }
        canvas.drawColor(Color.WHITE);
        for (int i = 0; i < paths.length; i++) {
            canvas.drawPath(paths[i], colors[i]);
        }
        canvas.drawCircle(circleX, circleY, circleRadius, paintC);
        canvas.restore();
        paths[color].moveTo(circleX, circleY);
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
