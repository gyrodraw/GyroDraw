package ch.epfl.sweng.SDP.localDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Local database handler for storing and retrieving the user's images.
 */
public class LocalDbHandlerForImages extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "myImages.db";
    private static final String TABLE_NAME = "myImages";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_IMAGE = "image";

    /**
     * Helper class to save images in local database.
     */
    public LocalDbHandlerForImages(Context context, SQLiteDatabase.CursorFactory factory,
            int dbVersion) {
        super(context, DATABASE_NAME, factory, dbVersion);
    }

    /**
     * Creates a new database table.
     *
     * @param db database where to create new table in.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID
                + " INTEGER PRIMARY KEY," + COLUMN_TIMESTAMP + " TEXT," + COLUMN_IMAGE + " BLOB )";

        db.execSQL(createTable);
    }

    /**
     * If there exists already a table with this name, which has lower version, drop it.
     *
     * @param db database to look in
     * @param oldVersion old version number
     * @param newVersion new version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Adds a bitmap to the local db.
     *
     * @param bitmap to insert
     */
    public void addBitmapToDb(Bitmap bitmap, int quality) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();

            ContentValues values = new ContentValues();
            values.put(COLUMN_TIMESTAMP, ts);
            values.put(COLUMN_IMAGE, byteArray);
            SQLiteDatabase db = this.getWritableDatabase();

            db.insert(TABLE_NAME, null, values);

            db.close();
        }
    }

    /**
     * Retrieves the most recent bitmap from the table.
     *
     * @return the newest bitmap
     */
    public Bitmap getLatestBitmapFromDb() {
        String query = "Select * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC LIMIT 1";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Bitmap bitmap;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            byte[] byteArray = cursor.getBlob(2);
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            cursor.close();
        } else {
            bitmap = null;
        }
        db.close();
        return bitmap;
    }
}
