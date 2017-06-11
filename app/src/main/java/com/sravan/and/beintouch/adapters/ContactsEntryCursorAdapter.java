package com.sravan.and.beintouch.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sravan.and.beintouch.R;
import com.sravan.and.beintouch.utility.CursorRecyclerViewAdapter;
/**
 * Created by skyfishjy on 10/31/14.
 */
public class ContactsEntryCursorAdapter extends CursorRecyclerViewAdapter<ContactsEntryCursorAdapter.ViewHolder> {

    public ContactsEntryCursorAdapter(Cursor cursor, String comparisonColumn){
        super(cursor, comparisonColumn);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView contactName;
        public TextView contactLastInteracted;
        public ImageView imageView;
        public ViewHolder(View view) {
            super(view);
            contactName = (TextView) view.findViewById(R.id.contactname);
            contactLastInteracted = (TextView) view.findViewById(R.id.contactlastinteraction);
            imageView = (ImageView) view.findViewById(R.id.contactavatar);
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

}