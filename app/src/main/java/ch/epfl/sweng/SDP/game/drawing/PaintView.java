package ch.epfl.sweng.SDP.game.drawing;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.Queue;

import ch.epfl.sweng.SDP.LocalDbHandler;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.FbStorage;


public class PaintView extends View {

    public static final int DRAW_WIDTH = 30;

    private boolean canDraw = true;
    private boolean isDrawing = false;
    private boolean bucketMode = false;

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
    private int color = 1;
    private int previousColor = 1;

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

        paintC = new Paint(Color.BLACK);
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
        if (this.color != 0) {
            this.color = color;
        }
        previousColor = color;
    }

    public void setPencil() {
        bucketMode = false;
        if (isDrawing) {
            drawEnd();
        }
        color = previousColor;
    }

    public void setEraser() {
        bucketMode = false;
        if (isDrawing) {
            drawEnd();
        }
        color = 0;
    }

    public void setBucket() {
        bucketMode = true;
        if (isDrawing) {
            drawEnd();
        }
        color = previousColor;
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
                    if (!bucketMode) {
                        drawStart();
                    } else {
                        floodFill();
                    }
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

    private void floodFill() {
        Point pt = new Point((int) circleX, (int) circleY);

        int targetColor = bitmap.getPixel(pt.x, pt.y);
        int replacementColor = colors[color].getColor();

        Queue<Point> q = new LinkedList<>();
        q.add(pt);

        while (q.size() > 0) {
            Point n = q.poll();
            if (bitmap.getPixel(n.x, n.y) != targetColor)
                continue;

            Point e = new Point(n.x + 1, n.y);
            while ((n.x > 0) && (bitmap.getPixel(n.x, n.y) == targetColor)) {
                bitmap.setPixel(n.x, n.y, replacementColor);
                if ((n.y > 0) && (bitmap.getPixel(n.x, n.y - 1) == targetColor))
                    q.add(new Point(n.x, n.y - 1));
                if ((n.y < bitmap.getHeight() - 1)
                        && (bitmap.getPixel(n.x, n.y + 1) == targetColor))
                    q.add(new Point(n.x, n.y + 1));
                n.x--;
            }
            while ((e.x < bitmap.getWidth() - 1)
                    && (bitmap.getPixel(e.x, e.y) == targetColor)) {
                bitmap.setPixel(e.x, e.y, replacementColor);

                if ((e.y > 0) && (bitmap.getPixel(e.x, e.y - 1) == targetColor))
                    q.add(new Point(e.x, e.y - 1));
                if ((e.y < bitmap.getHeight() - 1)
                        && (bitmap.getPixel(e.x, e.y + 1) == targetColor))
                    q.add(new Point(e.x, e.y + 1));
                e.x++;
            }
        }
    }

    /**
     * Gets called when time for drawing is over.
     * Saves the bitmap in the local DB.
     */
    public void saveCanvasInDb(Context context) {
        if (isDrawing) {
            drawEnd();
        }
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
