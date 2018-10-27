package ch.epfl.sweng.SDP.game.drawing;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.LinkedList;
import java.util.Queue;

public class BucketTool {

    private Bitmap image = null;
    private int[] tolerance = new int[]{0, 0, 0};
    private int width = 0;
    private int height = 0;
    private int[] pixels = null;
    private int fillColor = 0;
    private int[] startColor = new int[]{0, 0, 0};
    private boolean[] pixelsChecked;
    private Queue<FloodFillRange> ranges;

    BucketTool(Bitmap img, int targetColor, int newColor) {
        useImage(img);
        setFillColor(newColor);
        setTargetColor(targetColor);
    }

    public void setTargetColor(int targetColor) {
        startColor[0] = Color.red(targetColor);
        startColor[1] = Color.green(targetColor);
        startColor[2] = Color.blue(targetColor);
    }

    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(int value) {
        fillColor = value;
    }

    public int[] getTolerance() {
        return tolerance;
    }

    public void setTolerance(int[] value) {
        tolerance = value;
    }

    public void setTolerance(int value) {
        tolerance = new int[]{value, value, value};
    }

    public Bitmap getImage() {
        return image;
    }

    private void useImage(Bitmap img) {
        // Use a pre-existing provided BufferedImage and write directly to it
        width = img.getWidth();
        height = img.getHeight();
        image = img;

        pixels = new int[width * height];

        image.getPixels(pixels, 0, width, 0, 0, width, height);
    }

    private void prepare() {
        // Called before starting flood-fill
        pixelsChecked = new boolean[pixels.length];
        ranges = new LinkedList<>();
    }

    /**
     * Fills the specified point on the bitmap with the currently selected fill color.
     *
     * @param x the starting x
     * @param y the starting y
     */
    public void floodFill(int x, int y) {
        prepare();

        if (startColor[0] == 0) {
            int startPixel = pixels[(width * y) + x];
            startColor[0] = (startPixel >> 16) & 0xff;
            startColor[1] = (startPixel >> 8) & 0xff;
            startColor[2] = startPixel & 0xff;
        }

        LinearFill(x, y);
        FloodFillRange range;

        while (ranges.size() > 0) {
            // Get next range off the queue
            range = ranges.remove();

            // Check above and below each pixel in the floodfill range
            int downPxIdx = (width * (range.Y + 1)) + range.startX;
            int upPxIdx = (width * (range.Y - 1)) + range.startX;
            int upY = range.Y - 1;
            int downY = range.Y + 1;

            for (int i = range.startX; i <= range.endX; i++, downPxIdx++, upPxIdx++) {
                // Start fill upwards
                if (range.Y > 0 && (!pixelsChecked[upPxIdx])
                        && CheckPixel(upPxIdx))
                    LinearFill(i, upY);

                // Start fill downwards
                if (range.Y < (height - 1) && (!pixelsChecked[downPxIdx])
                        && CheckPixel(downPxIdx))
                    LinearFill(i, downY);
            }
        }

        image.setPixels(pixels, 0, width, 0, 0, width, height);
    }

    /**
     * Finds the furthermost left and right boundaries of the fill area.
     *
     * @param x the starting x
     * @param y the starting y
     */
    private void LinearFill(int x, int y) {
        // Find left edge of color area
        int lFillLoc = x;
        int pxIdx = (width * y) + x;

        do {
            pixels[pxIdx] = fillColor;
            pixelsChecked[pxIdx] = true;

            lFillLoc--;
            pxIdx--;

            // Exit loop if we're at edge of bitmap or color area
        } while (!(lFillLoc < 0 || (pixelsChecked[pxIdx]) || !CheckPixel(pxIdx)));

        lFillLoc++;
        // Find right edge of color area
        int rFillLoc = x;
        pxIdx = (width * y) + x;

        do {
            pixels[pxIdx] = fillColor;
            pixelsChecked[pxIdx] = true;

            rFillLoc++;
            pxIdx++;

            // Exit loop if we're at edge of bitmap or color area
        } while (!(rFillLoc >= width || pixelsChecked[pxIdx] || !CheckPixel(pxIdx)));

        rFillLoc--;
        // Add range to queue
        FloodFillRange r = new FloodFillRange(lFillLoc, rFillLoc, y);
        ranges.offer(r);
    }

    // Sees if a pixel is within the color tolerance range.
    private boolean CheckPixel(int px) {
        int red = (pixels[px] >>> 16) & 0xff;
        int green = (pixels[px] >>> 8) & 0xff;
        int blue = pixels[px] & 0xff;

        return (red >= (startColor[0] - tolerance[0])
                && red <= (startColor[0] + tolerance[0])
                && green >= (startColor[1] - tolerance[1])
                && green <= (startColor[1] + tolerance[1])
                && blue >= (startColor[2] - tolerance[2]) && blue <= (startColor[2] + tolerance[2]));
    }

    // Represents a linear range to be filled and branched from.
    private class FloodFillRange {
        private int startX;
        private int endX;
        private int Y;

        private FloodFillRange(int startX, int endX, int y) {
            this.startX = startX;
            this.endX = endX;
            this.Y = y;
        }
    }
}
