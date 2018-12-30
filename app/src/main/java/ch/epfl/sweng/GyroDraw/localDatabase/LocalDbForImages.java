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
    void addBitmapToDb(Bitmap bitmap, int quality);

    /**
     * Retrieves the most recent bitmap from the table.
     *
     * @return the newest bitmap
     */
    Bitmap getLatestBitmapFromDb();

    /**
     * TODO
     * @param context
     * @return
     */
    List<Bitmap> getBitmapsFromDb(Context context);
}
