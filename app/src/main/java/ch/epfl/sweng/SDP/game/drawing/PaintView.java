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
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForImages;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.FbStorage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;

import java.util.LinkedList;
import java.util.List;

public class PaintView extends View {

    private static final int DRAW_WIDTH = 30;
    private static final int QUALITY = 20;
    private static final double INIT_SPEED = 5;

    private boolean canDraw = true;
    private boolean isDrawing = false;
    private boolean bucketMode = false;

    private List<Paint> colors = new LinkedList<>();
    private Path path = new Path();
    private Paint paintC;

    private Bitmap bitmap;
    private Canvas canvas;

    private final Context context;

    private int circleX = 0;
    private int circleY = 0;

    private int width;
    private int height;
    private int circleRadius;
    private int color = 0;
    private int previousColor = 0;
    private double speed;

    /**
     * Constructor for the view.
     *
     * @param context Context of class
     * @param attrs Attributes of class
     */
    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        colors.add(getPaintWithColor(Color.BLACK));

        paintC = new Paint(Color.BLACK);
        paintC.setStyle(Paint.Style.STROKE);
        paintC.setStrokeWidth(DRAW_WIDTH / 2);

        circleRadius = DRAW_WIDTH;
        speed = INIT_SPEED;
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

    /**
     * This method adds the colors that a player owns into the color picker
     * @param colors List containing the colors that a player owns
     */
    public void setColors(List<Integer> colors) {
        for(int col : colors) {
            this.colors.add(getPaintWithColor(getResources().getColor(col)));
        }
        this.colors.add(getPaintWithColor(Color.WHITE));
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getCircleX() {
        return circleX;
    }

    public int getCircleY() {
        return circleY;
    }

    /**
     * Sets the circle position.
     *
     * @param circleX the x position
     * @param circleY the y position
     */
    public void setCircle(int circleX, int circleY) {
        this.circleX = sanitizeCoordinate(circleX, width);
        this.circleY = sanitizeCoordinate(circleY, height);
        if (isDrawing) {
            path.lineTo(this.circleX, this.circleY);
        }
    }

    public int getCircleRadius() {
        return circleRadius;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void multSpeed(double factor) {
        speed *= factor;
    }

    public double getSpeed() {
        return speed;
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
    }

    /**
     * Sets the x and y coordinates of the painting circle.
     * @param x coordinate
     * @param y coordinate
     */
    public void updateCoordinates(float x, float y) {
        circleX -= x * speed;
        circleY += y * speed;

        setCircle(circleX, circleY);
    }

    /**
     * Returns the value of the the current color.
     *
     * @return the value of the current color
     */
    public int getColor() {
        return colors.get(color).getColor();
    }

    /**
     * Selects the ith color of the color list.
     *
     * @param color the index of the color
     */
    public void setColor(int color) {
        if (this.color != colors.size() - 1) {
            this.color = color;
        }
        previousColor = color;
    }

    /**
     * Selects the pencil tool.
     */
    public void setPencil() {
        bucketMode = false;
        if (isDrawing) {
            drawEnd();
        }
        color = previousColor;
    }

    /**
     * Selects the eraser tool.
     */
    public void setEraser() {
        bucketMode = false;
        if (isDrawing) {
            drawEnd();
        }
        color = colors.size() - 1;
    }

    /**
     * Selects the bucket tool.
     */
    public void setBucket() {
        bucketMode = true;
        if (isDrawing) {
            drawEnd();
        }
        color = previousColor;
    }

    /**
     * Keeps coordinates within screen boundaries.
     *
     * @param coordinate coordinate to sanitize
     * @param maxBound maximum bound
     * @return sanitized coordinate
     */
    private int sanitizeCoordinate(int coordinate, int maxBound) {
        return Math.max(1, Math.min(coordinate, maxBound - 1));
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
        if (isDrawing) {
            canvas.drawPath(path, colors.get(color));
        }
        paintC.setColor(colorToGrey(Color.WHITE - bitmap.getPixel(circleX, circleY) + Color.BLACK));
        canvas.drawCircle(circleX, circleY, circleRadius, paintC);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Draw only if the time is not over
        if (canDraw) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!bucketMode) {
                        drawStart();
                    } else {
                        path.moveTo(circleX, circleY);
                        // Apply the flood fill algorithm
                        new BucketTool(bitmap, bitmap.getPixel(circleX, circleY),
                                colors.get(color).getColor()).floodFill(circleX, circleY);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    drawEnd();
                    break;
                default:
            }
            invalidate();
        }
        return true;
    }

    private int colorToGrey(int color) {
        int red = (color >> 16) & 0xff;
        int green = (color >> 8) & 0xff;
        int blue = (color) & 0xff;
        int mean = (red + green + blue) / 3;
        return Color.argb(0xff, mean, mean, mean);
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
        canvas.drawPath(path, colors.get(color));
        path.reset();
    }

    /**
     * Saves the bitmap in the local database.
     */
    public void saveCanvasInDb(LocalDbHandlerForImages localDbHandler) {
        if (isDrawing) {
            drawEnd();
        }
        canDraw = false;
        localDbHandler.addBitmapToDb(bitmap, QUALITY);
    }

    /**
     * Uploads the bitmap to Firebase Storage.
     *
     * @return the {@link StorageTask} in charge of the upload
     */
    public StorageTask<TaskSnapshot> saveCanvasInStorage() {
        if (isDrawing) {
            drawEnd();
        }
        canDraw = false;

        // Use userId as the name for the image
        String imageName = Account.getInstance(context).getUserId() + ".jpg";
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(imageName);
        return FbStorage.sendBitmapToFirebaseStorage(bitmap, imageRef);
    }
}
