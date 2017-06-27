/*
 *
 *
 *
 */

package com.bamboosoft.spirittreeapp.repository.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class UsersDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "spirittreedb.db";

    private static final String TEXT_TYPE = " TEXT";

    private static final String DATETIME_TYPE = " DATETIME";

    private static final String VARCHAR_TYPE = " VARCHAR";

    private static final String INT_TYPE = " INT";

    private static final String INTEGER_TYPE = " INTEGER";

    private static final String BOOLEAN_TYPE = " INTEGER";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UsersPersistenceContract.UserEntry.TABLE_NAME + " (" +
                    UsersPersistenceContract.UserEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
                    UsersPersistenceContract.UserEntry.COLUMN_ID + INTEGER_TYPE + COMMA_SEP +
                    UsersPersistenceContract.UserEntry.COLUMN_ACCOUNT + VARCHAR_TYPE + COMMA_SEP +
                    UsersPersistenceContract.UserEntry.COLUMN_MOBILE + INT_TYPE + COMMA_SEP +
                    UsersPersistenceContract.UserEntry.COLUMN_EMAIL + VARCHAR_TYPE + COMMA_SEP +
                    UsersPersistenceContract.UserEntry.COLUMN_PASSWORD + VARCHAR_TYPE + COMMA_SEP +
                    UsersPersistenceContract.UserEntry.COLUMN_CREATETIME + DATETIME_TYPE + COMMA_SEP +
                    UsersPersistenceContract.UserEntry.COLUMN_STATUS + INTEGER_TYPE + COMMA_SEP +
                    UsersPersistenceContract.UserEntry.COLUMN_TYPE + INTEGER_TYPE + COMMA_SEP +
                    UsersPersistenceContract.UserEntry.COLUMN_DESCRIPTION + TEXT_TYPE +
            " )";

    public UsersDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }
}
