package com.example.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class LibraryContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_LIBRARY = "library";


    public static abstract class LibraryEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIBRARY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIBRARY;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_LIBRARY);

        public  static final String TABLE_NAME = "library";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_LIBRARY_NAME = "name";
        public static final String COLUMN_LIBRARY_PRICE = "price";
        public static final String COLUMN_LIBRARY_QUANTITY = "quantity";
        public static final String COLUMN_LIBRARY_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_LIBRARY_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";
    }
}