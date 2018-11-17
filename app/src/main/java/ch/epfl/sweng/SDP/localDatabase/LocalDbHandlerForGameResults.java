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
import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.SDP.home.GameResult;

public class LocalDbHandlerForGameResults extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "gameResults.db";
    private static final String TABLE_NAME = "gameResults";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_RANKED_USERNAME = "rankedUsername";
    private static final String COLUMN_RANK = "rank";
    private static final String COLUMN_STARS = "stars";
    private static final String COLUMN_TROPHIES = "trophies";
    private static final String COLUMN_DRAWING = "drawing";

    /**
     * Helper class to save game results in local database.
     */
    public LocalDbHandlerForGameResults(Context context, SQLiteDatabase.CursorFactory factory,
                                        int dbVersion) {
        super(context, DATABASE_NAME, factory, dbVersion);
    }

    // List<String> rankedUsername, int rank, int stars, int trophies, Bitmap drawing

    /**
     * Creates a new database table.
     *
     * @param db database where to create new table in.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID
                + " INTEGER PRIMARY KEY,"
                + COLUMN_RANKED_USERNAME + " RANKED_USERNAME,"
                + COLUMN_RANK + " RANK,"
                + COLUMN_STARS + " STARS,"
                + COLUMN_TROPHIES + " TROPHIES,"
                + COLUMN_DRAWING + " DRAWING )";
        db.execSQL(createTable);
    }

    /**
     * If there exists already a table with this name, which has lower version, drop it.
     *
     * @param db         database to look in
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
    public void addGameResultToDb(GameResult gameResult) {
        // Convert the drawing to a byte array
        Bitmap bitmap = gameResult.getDrawing();
        byte[] byteArray = null;

        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
            byteArray = byteArrayOutputStream.toByteArray();

            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_RANKED_USERNAME, fromListToString(gameResult.getRankedUsername()));
        values.put(COLUMN_RANK, gameResult.getRank());
        values.put(COLUMN_STARS, gameResult.getStars());
        values.put(COLUMN_TROPHIES, gameResult.getTrophies());
        values.put(COLUMN_DRAWING, byteArray);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Retrieves the 10th most recent game results from the table.
     *
     * @return the newest game result
     */
    public List<GameResult> getGameResultsFromDb(Context context) {
        String query = "Select * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC LIMIT 10";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<GameResult> recentResults = new ArrayList<>();

        if (cursor.getPosition() < 0) {
            return recentResults;
        }

        cursor.moveToFirst();
        do {
            List<String> rankedUsername = fromStringToList(cursor.getString(1));
            int rank = cursor.getInt(2);
            int stars = cursor.getInt(3);
            int trophies = cursor.getInt(4);
            byte[] byteArray = cursor.getBlob(5);
            Bitmap drawing = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            recentResults.add(
                    new GameResult(rankedUsername, rank, stars, trophies, drawing, context));
        } while (cursor.move(6));

        cursor.close();
        db.close();
        return recentResults;
    }

    private static String fromListToString(List<String> list) {
        StringBuilder concatList = new StringBuilder();

        for (String username: list) {
            concatList.append(username).append('\n');
        }

        return concatList.toString();
    }

    private static List<String> fromStringToList(String string) {
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        for (char c: string.toCharArray()) {
            if (c != '\n') {
                builder.append(c);
            } else {
                list.add(builder.toString());
                builder = new StringBuilder();
            }
        }

        return list;
    }
}
