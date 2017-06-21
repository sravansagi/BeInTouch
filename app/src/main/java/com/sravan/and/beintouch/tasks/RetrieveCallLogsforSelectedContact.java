package com.sravan.and.beintouch.tasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CallLog;

import com.sravan.and.beintouch.bean.BeInTouchContact;
import com.sravan.and.beintouch.bean.CallEntry;
import com.sravan.and.beintouch.ui.ContactDetailFragment;

import java.util.ArrayList;

/**
 * Created by HP on 6/22/2017.
 */

public class RetrieveCallLogsforSelectedContact extends AsyncTask<BeInTouchContact, Void, ArrayList<CallEntry>> {

    ContactDetailFragment contactDetailFragment;
    private ArrayList<CallEntry> callEntries = new ArrayList<CallEntry>();

    public RetrieveCallLogsforSelectedContact(ContactDetailFragment contactDetailFragment) {
        this.contactDetailFragment = contactDetailFragment;
    }

    private static final String[] CALLLOG_CONTACT_PROJECTION = {CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION};

    private static final String SELECTION_CALLLOG_CONTACT = CallLog.Calls.NUMBER + " LIKE ?";


    @SuppressWarnings("MissingPermission")
    @Override
    protected ArrayList<CallEntry> doInBackground(BeInTouchContact... params) {
        if (params.length == 0){
            return null;
        }

        BeInTouchContact beInTouchContact = params[0];

        String phoneNumberwithoutSpaces = beInTouchContact.getPhoneNumber().replaceAll(" ", "");
        String phoneNumberwithoutEncoding = phoneNumberwithoutSpaces.replace("\u202A", "").replace("\u202C", "");
        Cursor callLogofContact = contactDetailFragment.getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI,
                CALLLOG_CONTACT_PROJECTION,
                SELECTION_CALLLOG_CONTACT,
                new String[]{"%" + phoneNumberwithoutEncoding +"%"},
                CallLog.Calls.DEFAULT_SORT_ORDER);
        for (callLogofContact.moveToFirst(); !callLogofContact.isAfterLast(); callLogofContact.moveToNext()) {
            CallEntry callEntry = new CallEntry();
            callEntry.setIncoming(callLogofContact.getInt(2));
            callEntry.setDate(callLogofContact.getLong(3));
            callEntry.setDuration(callLogofContact.getLong(4));
            callEntries.add(callEntry);
        }

        return callEntries;
    }

    @Override
    protected void onPostExecute(ArrayList<CallEntry> callEntries) {
        contactDetailFragment.onCall(callEntries);
    }
}
