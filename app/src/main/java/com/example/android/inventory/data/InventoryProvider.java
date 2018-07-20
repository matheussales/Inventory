package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;


public class InventoryProvider extends ContentProvider {

    private InventoryDbHelper mDbHelper;

    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    private static final int LIBRARY = 100;

    private static final int LIBRARY_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, LIBRARY);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", LIBRARY_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());

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
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case LIBRARY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
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
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {

        String name = values.getAsString(InventoryEntry.COLUMN_PRODUCT_NAME);
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Product requires a name");
        }

        Double price = values.getAsDouble(InventoryEntry.COLUMN_PRODUCT_PRICE);
        if (price < 0 && price == null) {
            throw new IllegalArgumentException("Product requires a valid price");
        }

        Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity < 0 && quantity == null) {
            throw new IllegalArgumentException("Product requires a valid quantity");
        }

        String supplier = values.getAsString(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        if (supplier == null || supplier.isEmpty()) {
            throw new IllegalArgumentException("Product requires a supplier");
        }

        Integer supplierPhone = values.getAsInteger(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
        if (supplierPhone < 0 && supplierPhone == null) {
            throw new IllegalArgumentException("Product requires a valid supplier phone number");
        }

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        long id = database.insert(InventoryEntry.TABLE_NAME, null, values);

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

                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(InventoryEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(InventoryEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_PRODUCT_PRICE)) {
            Double price = values.getAsDouble(InventoryEntry.COLUMN_PRODUCT_PRICE);
            if (price < 0 && price == null) {
                throw new IllegalArgumentException("Product requires a valid price");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity < 0 && quantity == null) {
                throw new IllegalArgumentException("Product requires a valid quantity");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) {
            String supplier = values.getAsString(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            if (supplier == null) {
                throw new IllegalArgumentException("Product requires a supplier");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER)) {
            Integer supplierPhone = values.getAsInteger(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
            if (supplierPhone < 0 && supplierPhone == null) {
                throw new IllegalArgumentException("Product requires a valid supplier phone number");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

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

                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);

                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsDeleted;
            case LIBRARY_ID:

                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);

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
                return InventoryEntry.CONTENT_LIST_TYPE;
            case LIBRARY_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}