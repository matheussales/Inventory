package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventory.data.LibraryContract.LibraryEntry;


public class LibraryProvider extends ContentProvider {

    private LibraryDbHelper mDbHelper;

    public static final String LOG_TAG = LibraryProvider.class.getSimpleName();

    private static final int LIBRARY = 100;

    private static final int LIBRARY_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(LibraryContract.CONTENT_AUTHORITY, LibraryContract.PATH_LIBRARY, LIBRARY);
        sUriMatcher.addURI(LibraryContract.CONTENT_AUTHORITY, LibraryContract.PATH_LIBRARY + "/#", LIBRARY_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new LibraryDbHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case LIBRARY:
                cursor = database.query(LibraryContract.LibraryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case LIBRARY_ID:
                selection = LibraryContract.LibraryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(LibraryContract.LibraryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LIBRARY:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {

        String name = values.getAsString(LibraryEntry.COLUMN_LIBRARY_NAME);
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Book requires a name");
        }

        Double price = values.getAsDouble(LibraryEntry.COLUMN_LIBRARY_PRICE);
        if (price < 0 && price == null) {
            throw new IllegalArgumentException("Book requires a valid price");
        }

        Integer quantity = values.getAsInteger(LibraryEntry.COLUMN_LIBRARY_QUANTITY);
        if (quantity < 0 && quantity == null) {
            throw new IllegalArgumentException("Book requires a valid quantity");
        }

        String supplier = values.getAsString(LibraryEntry.COLUMN_LIBRARY_SUPPLIER_NAME);
        if (supplier == null || supplier.isEmpty()) {
            throw new IllegalArgumentException("Book requires a supplier");
        }

        Integer supplierPhone = values.getAsInteger(LibraryEntry.COLUMN_LIBRARY_SUPPLIER_PHONE_NUMBER);
        if (supplierPhone < 0 && supplierPhone == null) {
            throw new IllegalArgumentException("Book requires a valid supplier phone number");
        }

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        long id = database.insert(LibraryEntry.TABLE_NAME, null, values);

        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LIBRARY:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case LIBRARY_ID:

                selection = LibraryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(LibraryEntry.COLUMN_LIBRARY_NAME)) {
            String name = values.getAsString(LibraryEntry.COLUMN_LIBRARY_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Book requires a name");
            }
        }

        if (values.containsKey(LibraryEntry.COLUMN_LIBRARY_PRICE)) {
            Double price = values.getAsDouble(LibraryEntry.COLUMN_LIBRARY_PRICE);
            if (price < 0 && price == null) {
                throw new IllegalArgumentException("Book requires a valid price");
            }
        }

        if (values.containsKey(LibraryEntry.COLUMN_LIBRARY_QUANTITY)) {
            Integer quantity = values.getAsInteger(LibraryEntry.COLUMN_LIBRARY_QUANTITY);
            if (quantity < 0 && quantity == null) {
                throw new IllegalArgumentException("Book requires a valid quantity");
            }
        }

        if (values.containsKey(LibraryEntry.COLUMN_LIBRARY_SUPPLIER_NAME)) {
            String supplier = values.getAsString(LibraryEntry.COLUMN_LIBRARY_SUPPLIER_NAME);
            if (supplier == null) {
                throw new IllegalArgumentException("Book requires a supplier");
            }
        }

        if (values.containsKey(LibraryEntry.COLUMN_LIBRARY_SUPPLIER_PHONE_NUMBER)) {
            Integer supplierPhone = values.getAsInteger(LibraryEntry.COLUMN_LIBRARY_SUPPLIER_PHONE_NUMBER);
            if (supplierPhone < 0 && supplierPhone == null) {
                throw new IllegalArgumentException("Book requires a valid supplier phone number");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(LibraryEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LIBRARY:

                rowsDeleted= database.delete(LibraryEntry.TABLE_NAME, selection, selectionArgs);

                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsDeleted;
            case LIBRARY_ID:

                selection = LibraryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                rowsDeleted = database.delete(LibraryEntry.TABLE_NAME, selection, selectionArgs);

                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LIBRARY:
                return LibraryEntry.CONTENT_LIST_TYPE;
            case LIBRARY_ID:
                return LibraryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}