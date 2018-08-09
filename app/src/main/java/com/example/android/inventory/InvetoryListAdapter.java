package com.example.android.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

import java.text.NumberFormat;

public class InvetoryListAdapter extends RecyclerView.Adapter<InvetoryListAdapter.InventoryViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public InvetoryListAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor  = cursor;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class InventoryViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView priceTextView;
        public TextView quantityTextView;
        public Button sellButton;


        public InventoryViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.product_name);
            priceTextView = itemView.findViewById(R.id.product_price);
            quantityTextView = itemView.findViewById(R.id.product_quantity);
            sellButton = itemView.findViewById(R.id.main_btn_sell);
        }
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View createdView = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item, parent, false);

        final InventoryViewHolder viewHolder = new InventoryViewHolder(createdView);

        createdView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                mCursor.moveToPosition(position);
                if (onItemClickListener != null) onItemClickListener.onItemClick(mCursor);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(InventoryViewHolder holder, final int position) {
        mCursor.moveToPosition(position);

        int nameColumnIndex = mCursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = mCursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        final int quantityColumnIndex = mCursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);

        String name = mCursor.getString(nameColumnIndex);
        Double price = mCursor.getDouble(priceColumnIndex);
        String quantity = mCursor.getString(quantityColumnIndex);

        NumberFormat format = NumberFormat.getCurrencyInstance();

        holder.nameTextView.setText(name);
        holder.priceTextView.setText(format.format(price));
        holder.quantityTextView.setText(quantity);

        holder.sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);

                int idColumnIndex = mCursor.getColumnIndex(InventoryEntry._ID);
                int _id = mCursor.getInt(idColumnIndex);

                int quantity = mCursor.getInt(quantityColumnIndex);

                int newQuantity = quantity - 1;

                if (newQuantity >= 0) {
                    Uri uri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, _id);

                    ContentValues values = new ContentValues();
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);

                    mContext.getContentResolver().update(uri, values, null, null);
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.info_cannot_sell), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mCursor !=  null) {
            return mCursor.getCount();
        }

        return 0;
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    public Cursor getCursor(){
        return mCursor;
    }
}
