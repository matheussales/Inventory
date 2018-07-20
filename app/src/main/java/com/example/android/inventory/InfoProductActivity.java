package com.example.android.inventory;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

import java.text.NumberFormat;

public class InfoProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private TextView mNameTextView;
    private TextView mPriceTextView;
    private TextView mQuantityTextView;
    private TextView mSupplierTextView;
    private TextView mSupplierPhoneTextView;

    private Uri mUri;

    private static final int LOADER = 0;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_product);

        Intent intent = getIntent();
        mUri = intent.getData();

        mNameTextView = findViewById(R.id.info_book_name);
        mPriceTextView = findViewById(R.id.info_book_price);
        mQuantityTextView = findViewById(R.id.info_book_quantity);
        mSupplierTextView = findViewById(R.id.info_book_supplier);
        mSupplierPhoneTextView = findViewById(R.id.info_book_supplier_phone);

        Button buttonSell = findViewById(R.id.info_btn_sell);
        Button buttonAddStock = findViewById(R.id.info_btn_add);
        Button buttonContactSeller = findViewById(R.id.btn_contact_seller);

        buttonSell.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sellProduct();
            }
        });

        buttonAddStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStock();
            }
        });

        buttonContactSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:" + mSupplierPhoneTextView.getText()));

                if (ActivityCompat.checkSelfPermission(InfoProductActivity.this, Manifest.permission.CALL_PHONE) !=
                        PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(InfoProductActivity.this,
                            Manifest.permission.CALL_PHONE)) {

                    } else {
                        ActivityCompat.requestPermissions(InfoProductActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    }
                    return;
                }
                startActivity(phoneIntent);
            }
        });

        getLoaderManager().initLoader(LOADER, null, this);

    }


    private void sellProduct() {
        String quantityString = mQuantityTextView.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString) - 1;

        if (quantity >= 0) {
            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);

            getContentResolver().update(mUri, values, null, null);
        } else {
            Toast.makeText(this, getString(R.string.info_cannot_sell), Toast.LENGTH_SHORT).show();
        }
    }

    private void addStock() {
        String quantityString = mQuantityTextView.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString) + 1;

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);

        getContentResolver().update(mUri, values, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(InfoProductActivity.this, EditProductActivity.class);
                intent.setData(mUri);
                startActivity(intent);
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);

        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (mUri != null) {
            getContentResolver().delete(mUri, null, null);

            finish();
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
            Double price = cursor.getDouble(priceColumnIndex);
            String quantity = cursor.getString(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            NumberFormat format = NumberFormat.getCurrencyInstance();

            mNameTextView.setText(name);
            mPriceTextView.setText(format.format(price));
            mQuantityTextView.setText(quantity);
            mSupplierTextView.setText(supplierName);
            mSupplierPhoneTextView.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}