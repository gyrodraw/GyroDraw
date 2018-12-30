package ch.epfl.sweng.GyroDraw.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Utility class containing methods for manipulating bitmaps.
 */
public final class BitmapManipulator {

    private BitmapManipulator() {
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                             int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Decodes the {@link Bitmap} generated from the given resource optimising it for the given
     * view's dimensions.
     *
     * @param res       the application's resources
     * @param resId     the id of the resource
     * @param reqWidth  the ideal width
     * @param reqHeight the ideal height
     * @return the sampled Bitmap
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Decodes the {@link Bitmap} generated from the given byte array optimising it for the given
     * view's dimensions.
     *
     * @param array     the byte array containing the data
     * @param offset    the offset from which the decoding should begin parsing
     * @param length    the number of bytes to parse
     * @param reqWidth  the ideal width
     * @param reqHeight the ideal height
     * @return the sampled Bitmap
     */
    public static Bitmap decodeSampledBitmapFromByteArray(byte[] array, int offset, int length,
                                                          int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(array, offset, length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(array, offset, length, options);
    }
}
