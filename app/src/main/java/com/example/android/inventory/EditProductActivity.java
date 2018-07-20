package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

public class EditProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierEditText;
    private EditText mSupplierPhoneEditText;

    private Uri mUri;

    private static final int LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        Intent intent = getIntent();
        mUri = intent.getData();

        if (mUri == null) {
            setTitle(getString(R.string.title_activity_add_product));
        } else {
            setTitle(getString(R.string.title_activity_edit_product));

            getLoaderManager().initLoader(LOADER, null, this);
        }

        mNameEditText = findViewById(R.id.edit_book_name);
        mPriceEditText = findViewById(R.id.edit_book_price);
        mQuantityEditText = findViewById(R.id.edit_book_quantity);
        mSupplierEditText = findViewById(R.id.edit_book_supplier);
        mSupplierPhoneEditText = findViewById(R.id.edit_book_supplier_phone);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_save:
                saveBook();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveBook() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quanitityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();

        ContentValues values = new ContentValues();

        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quanitityString);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierString);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, supplierPhoneString);

        if (mUri == null &&
                TextUtils.isEmpty(nameString) &&
                priceString.isEmpty() &&
                quanitityString.isEmpty() &&
                supplierString.isEmpty() &&
                supplierPhoneString.isEmpty()) {

            return ;
        }


        if (mUri != null) {
            try {
                int rowsAffected = getContentResolver().update(mUri, values, null, null);

                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.editor_update_product_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_update_product_successful),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, getString(R.string.editor_catch_error), Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                Toast.makeText(this, getString(R.string.editor_catch_error), Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
                Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, getString(R.string.editor_catch_error), Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                Toast.makeText(this, getString(R.string.editor_catch_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER};

        return new CursorLoader(
                this,
                mUri,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            String quantity = cursor.getString(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            mNameEditText.setText(name);
            mPriceEditText.setText(price);
            mQuantityEditText.setText(quantity);
            mSupplierEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}