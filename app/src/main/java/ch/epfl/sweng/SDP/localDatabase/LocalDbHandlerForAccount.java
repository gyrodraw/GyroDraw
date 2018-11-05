package ch.epfl.sweng.SDP.localDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import ch.epfl.sweng.SDP.auth.Account;

public class LocalDbHandlerForAccount extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "account.db";
    private static final String TABLE_NAME = "account";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_USER_ID = "userId";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_CURRENT_LEAGUE = "currentLeague";
    private static final String COLUMN_TROPHIES = "trophies";
    private static final String COLUMN_STARS = "stars";

    /**
     * Helper class to save the account in local database.
     */
    public LocalDbHandlerForAccount(Context context, SQLiteDatabase.CursorFactory factory,
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
                + " INTEGER PRIMARY KEY," + COLUMN_USER_ID + " USER_ID," + COLUMN_USERNAME
                + " USERNAME," + COLUMN_CURRENT_LEAGUE + " CURRENT_LEAGUE," + COLUMN_TROPHIES
                + " TROPHIES," + COLUMN_STARS + " STARS )";

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
     * Save the given account in the local database.
     *
     * @param account the account to be saved
     */
    public void saveAccount(Account account) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, account.getUserId());
        values.put(COLUMN_USERNAME, account.getUsername());
        values.put(COLUMN_CURRENT_LEAGUE, account.getCurrentLeague());
        values.put(COLUMN_TROPHIES, account.getTrophies());
        values.put(COLUMN_STARS, account.getStars());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }


    /**
     * Retrieve the account data stored in the local database and update the given account with it.
     *
     * @param account the account to be updated
     */
    public void retrieveAccount(Account account) {
        String query = "Select * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC LIMIT 1";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            account.setUserId(cursor.getString(1));
            account.setUsername(cursor.getString(2));
            account.setCurrentLeague(cursor.getString(3));
            account.setTrophies(cursor.getInt(4));
            account.setStars(cursor.getInt(5));
            //account.setUsersRef(Database.INSTANCE.getReference("users"));
            cursor.close();
        }
        db.close();
    }
}
