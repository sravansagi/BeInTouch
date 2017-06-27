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

import com.bumptech.glide.Glide;
import com.sravan.and.beintouch.R;
import com.sravan.and.beintouch.bean.BeInTouchContact;
import com.sravan.and.beintouch.utility.CursorRecyclerViewAdapter;
/**
 * Created by skyfishjy on 10/31/14.
 * Modified by emuneee on 1/5/16.
 * Edited by sravan on 13/6/2017
 */
public class ContactsEntryCursorAdapter extends CursorRecyclerViewAdapter<ContactsEntryCursorAdapter.ViewHolder> {

    /**
     * The context has been added to load the contact image using glide library
     */
    Context context;
    private static final int NAME_COLUMN  = 1;
    private static final int NUMBER_COLUMN = 2;
    private static final int CONTACTS_ID_COLUMN = 3;
    private static final int LOOKUP_COLUMN = 4;
    private static final int THUMBNAIL_PHOTO_COLUMN = 5;
    private static final int LAST_CONTACTED = 6;
    private static final int PHOTO_ID_COLUMN = 7;




    public ContactsEntryCursorAdapter(Cursor cursor, String comparisonColumn, OnItemClickListener listener, Context context){
        super(cursor, comparisonColumn);
        this.listener = listener;
        this.context = context;
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
        String name = cursor.getString(NAME_COLUMN);
        String picture = cursor.getString(THUMBNAIL_PHOTO_COLUMN);
        viewHolder.contactName.setText(name);
        viewHolder.contactLastInteracted.
                setText(BeInTouchContact.getLastInteraction(context,cursor.getLong(LAST_CONTACTED)));
        if(picture!= null && picture.length() > 0){
            Glide.with(context).load(picture)
                    .into(viewHolder.imageView);
        } else {
            Glide.with(context).load(R.drawable.ic_contact_thumbnail)
                    .into(viewHolder.imageView);
        }


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


    public BeInTouchContact createContactfromCursor(int position){
        Cursor cursor = getCursor();
        if (cursor != null && cursor.moveToPosition(position)) {
            BeInTouchContact beInTouchContact = new BeInTouchContact();
            beInTouchContact.setPhoneNumber(cursor.getString(NUMBER_COLUMN));
            beInTouchContact.setName(cursor.getString(NAME_COLUMN));
            beInTouchContact.setContactID(cursor.getLong(CONTACTS_ID_COLUMN));
            beInTouchContact.setLookup(cursor.getString(LOOKUP_COLUMN));
            beInTouchContact.setContactThumbnailPhotoID(cursor.getString(THUMBNAIL_PHOTO_COLUMN));
            beInTouchContact.setLastcontacted(cursor.getLong(LAST_CONTACTED));
            beInTouchContact.setPhotoID(cursor.getString(PHOTO_ID_COLUMN));
            return beInTouchContact;
        }
        return null;
    }

}