package com.example.android.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

import java.text.NumberFormat;

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    private TextView mNameTextView;
    private TextView mPriceTextView;
    private TextView mQuantityTextView;

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        mNameTextView = view.findViewById(R.id.book_name);
        mPriceTextView = view.findViewById(R.id.book_price);
        mQuantityTextView = view.findViewById(R.id.book_quantity);

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);

        String name = cursor.getString(nameColumnIndex);
        Double price = cursor.getDouble(priceColumnIndex);
        final String quantity = cursor.getString(quantityColumnIndex);

        NumberFormat format = NumberFormat.getCurrencyInstance();

        mNameTextView.setText(name);
        mPriceTextView.setText(format.format(price));
        mQuantityTextView.setText(quantity);

        final int position = cursor.getPosition();

        Button buttonSell = view.findViewById(R.id.main_btn_sell);
        buttonSell.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cursor.moveToPosition(position);

                int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
                int _id = cursor.getInt(idColumnIndex);

                int quantity = cursor.getInt(quantityColumnIndex);
                
                int newQuantity = quantity - 1;

                if (newQuantity >= 0) {
                    Uri uri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, _id);

                    ContentValues values = new ContentValues();
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);

                    context.getContentResolver().update(uri, values, null, null);
                } else {
                    Toast.makeText(context, context.getString(R.string.info_cannot_sell), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}