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

import ch.epfl.sweng.SDP.home.GameResult;

public class LocalDbHandlerForGameResults extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "gameResults.db";
    private static final String TABLE_NAME = "gameResults";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_GAME_RESULT = "gameResult";


    /**
     * Helper class to save game results in local database.
     */
    public LocalDbHandlerForGameResults(Context context, SQLiteDatabase.CursorFactory factory,
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
                + " INTEGER PRIMARY KEY," + COLUMN_TIMESTAMP + " RESULT_ID,"
                + COLUMN_GAME_RESULT + " GAME_RESULT )";

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
     * Adds a game result to the local db.
     *
     * @param gameResult to insert
     */
    public void addBitmapToDb(GameResult gameResult) {
        if (gameResult != null) {
            byte[] byteGameResult = GameResult.toByteArray(gameResult);

            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();

            ContentValues values = new ContentValues();
            values.put(COLUMN_TIMESTAMP, ts);
            values.put(COLUMN_GAME_RESULT, byteGameResult);
            SQLiteDatabase db = this.getWritableDatabase();

            db.insert(TABLE_NAME, null, values);

            db.close();
        }
    }

    /**
     * Retrieves the most recent game result from the table.
     *
     * @return the newest game result
     */
    public GameResult getLatestBitmapFromDb() {
        String query = "Select * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC LIMIT 1";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        GameResult gameResult;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            byte[] byteArray = cursor.getBlob(2);
            gameResult = GameResult.fromByteArray(byteArray);
            cursor.close();
        } else {
            gameResult = null;
        }

        db.close();
        return gameResult;
    }
}
