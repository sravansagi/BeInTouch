package com.sravan.and.beintouch.tasks;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CallLog;

import com.sravan.and.beintouch.R;
import com.sravan.and.beintouch.bean.BeInTouchContact;
import com.sravan.and.beintouch.data.BeInTouchContract;
import com.sravan.and.beintouch.utility.Utilities;
import com.sravan.and.beintouch.widget.BeInTouchWidget;

import java.util.HashMap;

/**
 * Created by HP on 6/20/2017.
 * This task updates the last interacted time of all the contains that have been added to the contacts entry table
 * It first retrieves all the contacts stored in the contacts entry table then gets the call log information of the contact.
 * It checks for the latest values of the call log and will update the table accordingly
 */

public class UpdateContactLastInteraction extends AsyncTask<Void, Void, Void> {

    private static final String SORTORDER_DATE_DESC_LIMIT_1 = "date DESC LIMIT 1";
    private static final String[] CONTACTS_ENTRY_LOADER_PROJECTION = {BeInTouchContract.ContactsEntry._ID,
            BeInTouchContract.ContactsEntry.COLUMN_DISPLAYNAME,
            BeInTouchContract.ContactsEntry.COLUMN_NUMBER,
            BeInTouchContract.ContactsEntry.COLUMN_CONTACT_ID,
            BeInTouchContract.ContactsEntry.COLUMN_LOOKUP,
            BeInTouchContract.ContactsEntry.COLUMN_THUMBNAIL_PHOTO_ID,
            BeInTouchContract.ContactsEntry.COLUMN_LAST_CONTACTED};
    private static final String[] CALLLOG_CONTACT_PROJECTION = {CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.DATE};
    private static final String SELECTION_CALLLOG_CONTACT = CallLog.Calls.NUMBER + " LIKE ?";
    private static final String SELECTION_CONTACT_ENTRY = BeInTouchContract.ContactsEntry._ID + " = ?";
    Context context;

    public UpdateContactLastInteraction(Context mContext) {
        this.context = mContext;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Cursor cursorContact = context.getContentResolver().query(BeInTouchContract.ContactsEntry.CONTENT_URI,
                CONTACTS_ENTRY_LOADER_PROJECTION,
                null,
                null,
                null);
        HashMap<String, BeInTouchContact> contactEntryList = new HashMap<String, BeInTouchContact>();
        for (cursorContact.moveToFirst(); !cursorContact.isAfterLast(); cursorContact.moveToNext()) {
            BeInTouchContact beInTouchContact = new BeInTouchContact(cursorContact.getLong(0), cursorContact.getLong(6));
            contactEntryList.put(cursorContact.getString(2), beInTouchContact);
        }
        if (cursorContact != null) {
            cursorContact.close();
        }
        for (String contactNumber : contactEntryList.keySet()) {
            long lastContact = 0;
            int rowsUpdated = 0;
            if (Utilities.checkPermission(context)) {
                String phoneNumberwithoutSpaces = contactNumber.replaceAll(" ", "");
                String phoneNumberwithoutEncoding = phoneNumberwithoutSpaces.replace("\u202A", "").replace("\u202C", "");
                Cursor callLogofContact = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                        CALLLOG_CONTACT_PROJECTION,
                        SELECTION_CALLLOG_CONTACT,
                        new String[]{"%" + phoneNumberwithoutEncoding + "%"},
                        SORTORDER_DATE_DESC_LIMIT_1);

                if (callLogofContact != null && callLogofContact.moveToFirst()) {
                    lastContact = callLogofContact.getLong(2);
                    callLogofContact.close();
                }

                if (lastContact > 0 & lastContact > contactEntryList.get(contactNumber).getLastcontacted()) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(BeInTouchContract.ContactsEntry.COLUMN_LAST_CONTACTED, lastContact);
                    int rowsUpdatedSelectedContact = context.getContentResolver().update(BeInTouchContract.ContactsEntry.CONTENT_URI, contentValues,
                            SELECTION_CONTACT_ENTRY, new String[]{contactEntryList.get(contactNumber).get_id() + ""});
                    rowsUpdated = rowsUpdated + rowsUpdatedSelectedContact;
                }
                if (rowsUpdated > 0) {
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                            new ComponentName(context, BeInTouchWidget.class));
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
                }
            }
        }
        return null;
    }
}
