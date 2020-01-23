package ch.epfl.sweng.GyroDraw.localDatabase;

import android.content.Context;
import android.graphics.Bitmap;
import java.util.List;

/**
 * Interface representing a generic handler for the local database, responsible of operations
 * involving images.
 */
public interface LocalDbForImages {

    /**
     * Adds a bitmap to the local db.
     *
     * @param bitmap to insert
     */
    void addBitmap(Bitmap bitmap, int quality);

    /**
     * Removes all the bitmaps stored in the local db.
     */
    void removeAll();

    /**
     * Retrieves the most recent bitmap from the local db.
     *
     * @return the newest bitmap
     */
    Bitmap getLatestBitmap();

    /**
     * Retrieves the 20th most recent bitmaps from the local db.
     * @param context the context invoking this method
     * @return the newest bitmaps
     */
    List<Bitmap> getBitmaps(Context context);
}
