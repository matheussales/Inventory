package com.example.android.inventory;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

class InventoryItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private Context mContext;
    private InvetoryListAdapter mAdapter;

    public InventoryItemTouchHelperCallback(Context context, InvetoryListAdapter adapter) {
        mContext = context;
        mAdapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getLayoutPosition();

        Cursor cursor = mAdapter.getCursor();
        cursor.moveToPosition(position);

        int _idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
        int id = cursor.getInt(_idColumnIndex);

        Uri uri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

        mContext.getContentResolver().delete(uri, null,null);
        mAdapter.notifyItemRemoved(position);
    }
}