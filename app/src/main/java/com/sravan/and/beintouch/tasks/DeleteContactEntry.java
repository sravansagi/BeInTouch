package com.sravan.and.beintouch.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.sravan.and.beintouch.adapters.ContactsEntryCursorAdapter;
import com.sravan.and.beintouch.data.BeInTouchContract;

/**
 * Created by HP on 6/22/2017.
 */

public class DeleteContactEntry extends AsyncTask<Long,Void,Void> {

    Context mContext;
    ContactsEntryCursorAdapter contactsEntryCursorAdapter;

    public DeleteContactEntry(Context mContext, ContactsEntryCursorAdapter contactsEntryCursorAdapter) {
        this.mContext = mContext;
        this.contactsEntryCursorAdapter = contactsEntryCursorAdapter;
    }

    private static final String SELECTION_CONTACT_ENTRY = BeInTouchContract.ContactsEntry._ID + " = ?";

    @Override
    protected Void doInBackground(Long... params) {
        if (params.length == 0){
            return null;
        }
        long contactIDDelete = params[0];
        mContext.getContentResolver().delete(BeInTouchContract.ContactsEntry.CONTENT_URI,
                SELECTION_CONTACT_ENTRY, new String[]{contactIDDelete + ""});
        return null;
    }
}
