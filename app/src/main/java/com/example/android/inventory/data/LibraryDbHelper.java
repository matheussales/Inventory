package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventory.data.LibraryContract.LibraryEntry;

public class LibraryDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = LibraryDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "library.db";
    private static final int DATABASE_VERSION = 1;

    public LibraryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_LIBRARY_TABLES = "CREATE TABLE " + LibraryEntry.TABLE_NAME + " ("
                + LibraryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + LibraryEntry.COLUMN_LIBRARY_NAME + " TEXT NOT NULL, "
                + LibraryEntry.COLUMN_LIBRARY_PRICE + " INTEGER NOT NULL, "
                + LibraryEntry.COLUMN_LIBRARY_QUANTITY + " INTEGER NOT NULL, "
                + LibraryEntry.COLUMN_LIBRARY_SUPPLIER_NAME + " TEXT NOT NULL, "
                + LibraryEntry.COLUMN_LIBRARY_SUPPLIER_PHONE_NUMBER + " INTERGER NOT NULL );";

        db.execSQL(SQL_CREATE_LIBRARY_TABLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}