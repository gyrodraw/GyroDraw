package ch.epfl.sweng.SDP;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LocalDBHandler extends SQLiteOpenHelper {

    private static final String TAG = "LocalDBHandler";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "myImages.db";
    private static final String TABLE_NAME = "myImages";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_IMAGE = "image";
    private static final int QUALITY = 20;


    public LocalDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_TIMESTAMP + " TEXT," + COLUMN_IMAGE + " BLOB )";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addBitmapToDB(Bitmap bitmap) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, bos);
        byte[] bArray = bos.toByteArray();
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TIMESTAMP, ts);
        values.put(COLUMN_IMAGE, bArray);
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_NAME, null, values);

        db.close();
    }

    public Bitmap getLatestBitmapFromDB() {
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
