package com.sravan.and.beintouch.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sravan.and.beintouch.R;
import com.sravan.and.beintouch.data.BeInTouchContract;
import com.sravan.and.beintouch.utility.CursorRecyclerViewAdapter;
/**
 * Created by skyfishjy on 10/31/14.
 * Edited by sravan on 13/6/2017
 */
public class ContactsEntryCursorAdapter extends CursorRecyclerViewAdapter<ContactsEntryCursorAdapter.ViewHolder> {

    public ContactsEntryCursorAdapter(Cursor cursor, String comparisonColumn, OnItemClickListener listener){
        super(cursor, comparisonColumn);
        this.listener = listener;
    }


    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView contactName;
        public TextView contactLastInteracted;
        public ImageView imageView;
        public ViewHolder(View view) {
            super(view);
            contactName = (TextView) view.findViewById(R.id.contactname);
            contactLastInteracted = (TextView) view.findViewById(R.id.contactlastinteraction);
            imageView = (ImageView) view.findViewById(R.id.contactavatar);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                    }
                }
            });
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_list_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor, int position) {
        String phNumber = cursor.getString(1);
        viewHolder.contactName.setText(phNumber);
        viewHolder.contactLastInteracted.setText(cursor.getString(2));
    }

    /**
     * getContactLookupUri method returns the URI of the contact that is available in the provided position
     * @param position the item with in the recyclerview items
     * @return
     */
    public Uri getContactLookupUri (int position){
        Cursor cursor = getCursor();
        if (cursor != null && cursor.moveToPosition(position)) {
            return ContactsContract.Contacts.getLookupUri(cursor.getLong(3),
                    cursor.getString(4));
        }
        return null;
    }

}