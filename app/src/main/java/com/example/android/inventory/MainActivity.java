package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private InvetoryListAdapter mAdapter;
    private static final int LOADER = 0;
    private RecyclerView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton insertButton = findViewById(R.id.insert_button);
        list = findViewById(R.id.list);


//        View emptyView = findViewById(R.id.empty_view);
//        list.setEmptyView(emptyView);

        insertButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditProductActivity.class);
                startActivity(intent);
            }
        });

        mAdapter = new InvetoryListAdapter(this, null);
        list.setAdapter(mAdapter);
        list.setLayoutManager(new LinearLayoutManager(this));

        mAdapter.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(Cursor cursor) {
                long id = cursor.getLong(cursor.getColumnIndex(InventoryEntry._ID));
                Intent intent = new Intent(MainActivity.this, InfoProductActivity.class);

                Uri uri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(LOADER, null, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY
        };

        String sql_order = InventoryEntry._ID + " DESC";

        return new CursorLoader(this,
                InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                sql_order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.swapCursor(null);
    }
}