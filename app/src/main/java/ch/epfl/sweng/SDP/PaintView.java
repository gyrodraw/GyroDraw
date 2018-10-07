package ch.epfl.sweng.SDP;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class PaintView extends View {

    private static final String TAG = "PaintViewID";
    private static final int BRUSH_SIZE = 10;
    private static final int DEFAULT_COLOR = Color.RED;
    private static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private float xpos;
    private float ypos;
    private Path path;
    private Paint paint;
    private ArrayList<FingerPath> paths = new ArrayList<>();
    private int currentColor = DEFAULT_COLOR;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private boolean emboss;
    private boolean blur;
    private Bitmap bitmap;
    private MaskFilter embossMask;
    private MaskFilter blurMask;
    private Paint bitmapPaint = new Paint(Paint.DITHER_FLAG);

    public PaintView(Context context) {
        this(context, null);
    }

    /**
     * Constructor of our class, initialize the core parameters for our drawing.
     *
     * @param context Actual context for drawing.
     * @param attrs XML attributes of the view.
     */
    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(DEFAULT_COLOR);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setXfermode(null);
        paint.setAlpha(0xff);

        embossMask = new EmbossMaskFilter(new float[] {1, 1, 1}, 0.4f, 6, 3.5f);
        blurMask = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);
    }

    /**
     * Initialise the canvas according the dimensions in
     * pixels of the screen.
     *
     * @param metrics Object containing the dimensions of the screen
     */
    public void init(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;
    }

    public void normal() {
        emboss = false;
        blur = false;
    }

    public void emboss() {
        emboss = true;
        blur = false;
    }

    public void blur() {
        emboss = false;
        blur = true;
    }

    /**
     * Clear everything that is drawn.
     */
    public void clear() {
        backgroundColor = DEFAULT_BG_COLOR;
        paths.clear();
        normal();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.drawColor(backgroundColor);

        for (FingerPath fp : paths) {
            paint.setColor(fp.getColor());
            paint.setStrokeWidth(fp.getStrokeWidth());
            paint.setMaskFilter(null);

            if (fp.getEmboss()) {
                paint.setMaskFilter(embossMask);
            }
            else if (fp.getBlur()) {
                paint.setMaskFilter(blurMask);
            }
            else {
                Log.i(TAG, "Should not pass here");
            }

            canvas.drawPath(fp.getPath(), paint);
        }

        canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
        canvas.restore();
    }

    private void touchStart(float xx, float yy) {
        path = new Path();
        FingerPath fp = new FingerPath(currentColor, emboss, blur, strokeWidth, path);
        paths.add(fp);

        path.reset();
        path.moveTo(xx, yy);
        xpos = xx;
        ypos = yy;
    }

    private void touchMove(float xx, float yy) {
        float dx = Math.abs(xx - xpos);
        float dy = Math.abs(yy - ypos);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(xpos, ypos, (xx + xpos) / 2, (yy + ypos) / 2);
            xpos = xx;
            ypos = yy;
        }
    }

    private void touchUp() {
        path.lineTo(xpos, ypos);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xx = event.getX();
        float yy = event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                touchStart(xx, yy);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE :
                touchMove(xx, yy);
                invalidate();
                break;
            case MotionEvent.ACTION_UP :
                touchUp();
                invalidate();
                break;
            default:
        }

        return true;
    }
}
