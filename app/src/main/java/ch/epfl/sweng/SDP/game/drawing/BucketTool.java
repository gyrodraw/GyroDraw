package ch.epfl.sweng.SDP.game.drawing;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Class modelling the bucket tool used in the drawing activities.
 */
final class BucketTool {

    private Bitmap image = null;

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

    private void setTargetColor(int targetColor) {
        startColor[0] = Color.red(targetColor);
        startColor[1] = Color.green(targetColor);
        startColor[2] = Color.blue(targetColor);
    }

    private void setFillColor(int value) {
        fillColor = value;
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
    void floodFill(int x, int y) {
        prepare();

        if (startColor[0] == 0) {
            int startPixel = pixels[(width * y) + x];
            startColor[0] = (startPixel >> 16) & 0xff;
            startColor[1] = (startPixel >> 8) & 0xff;
            startColor[2] = startPixel & 0xff;
        }

        linearFill(x, y);
        FloodFillRange range;

        while (ranges.size() > 0) {
            range = ranges.remove();
            // Check above and below each pixel in the floodfill range
            int downPxIdx = (width * (range.y + 1)) + range.startX;
            int upPxIdx = (width * (range.y - 1)) + range.startX;
            int upY = range.y - 1;
            int downY = range.y + 1;

            for (int i = range.startX; i <= range.endX; i++, downPxIdx++, upPxIdx++) {
                // Start fill upwards
                if (range.y > 0 && (!pixelsChecked[upPxIdx]) && checkPixel(upPxIdx)) {
                    linearFill(i, upY);
                }

                // Start fill downwards
                if (range.y < (height - 1) && (!pixelsChecked[downPxIdx])
                        && checkPixel(downPxIdx)) {
                    linearFill(i, downY);
                }
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
    private void linearFill(int x, int y) {
        // Find left edge of color area
        int leftFillLoc = x;
        int pxIdx = (width * y) + x;

        do {
            pixels[pxIdx] = fillColor;
            pixelsChecked[pxIdx] = true;

            leftFillLoc--;
            pxIdx--;

            // Exit loop if we're at edge of bitmap or color area
        }
        while (!(leftFillLoc < 0 || (pixelsChecked[pxIdx]) || !checkPixel(pxIdx)));

        leftFillLoc++;

        // Find right edge of color area
        int rightFillLoc = x;
        pxIdx = (width * y) + x;

        do {
            pixels[pxIdx] = fillColor;
            pixelsChecked[pxIdx] = true;

            rightFillLoc++;
            pxIdx++;

            // Exit loop if we're at edge of bitmap or color area
        }
        while (!(rightFillLoc >= width || pixelsChecked[pxIdx] || !checkPixel(pxIdx)));

        rightFillLoc--;

        // Add range to queue
        ranges.offer(new FloodFillRange(leftFillLoc, rightFillLoc, y));
    }

    // Sees if a pixel is within the color tolerance range.
    private boolean checkPixel(int px) {
        int red = (pixels[px] >>> 16) & 0xff;
        int green = (pixels[px] >>> 8) & 0xff;
        int blue = pixels[px] & 0xff;

        return (red >= startColor[0] && red <= startColor[0]
                && green >= startColor[1] && green <= startColor[1]
                && blue >= startColor[2] && blue <= startColor[2]);
    }

    // Represents a linear range to be filled and branched from.
    private class FloodFillRange {

        private int startX;
        private int endX;
        private int y;

        private FloodFillRange(int startX, int endX, int y) {
            this.startX = startX;
            this.endX = endX;
            this.y = y;
        }
    }
}
