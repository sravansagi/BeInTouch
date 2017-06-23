package com.sravan.and.beintouch.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sravan.and.beintouch.R;
import com.sravan.and.beintouch.bean.BeInTouchContact;

/**
 * Created by Sravan on 6/23/2017.
 */

public class ContactsEntryCursorAdapter extends CursorAdapter {


    private static final int NAME_COLUMN  = 1;
    private static final int NUMBER_COLUMN = 2;
    private static final int CONTACTS_ID_COLUMN = 3;
    private static final int LOOKUP_COLUMN = 4;
    private static final int THUMBNAIL_PHOTO_COLUMN = 5;
    private static final int LAST_CONTACTED = 6;
    private static final int PHOTO_ID_COLUMN = 7;


    public ContactsEntryCursorAdapter(Context context, Cursor c) {
        super(context,c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.contacts_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView contactName = (TextView) view.findViewById(R.id.contactname);
        TextView contactLastInteracted = (TextView) view.findViewById(R.id.contactlastinteraction);
        ImageView imageView = (ImageView) view.findViewById(R.id.contactavatar);
        String name = cursor.getString(NAME_COLUMN);
        String picture = cursor.getString(THUMBNAIL_PHOTO_COLUMN);
        contactName.setText(name);
        contactLastInteracted.
                setText(BeInTouchContact.getLastInteraction(cursor.getLong(LAST_CONTACTED)));
        if(picture!= null && picture.length() > 0){
            Glide.with(context).load(picture)
                    .into(imageView);
        } else {
            Glide.with(context).load(R.drawable.ic_contact_thumbnail)
                    .into(imageView);
        }
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
