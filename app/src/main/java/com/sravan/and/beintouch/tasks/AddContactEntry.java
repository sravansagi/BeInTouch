package com.sravan.and.beintouch.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.sravan.and.beintouch.bean.BeInTouchContact;
import com.sravan.and.beintouch.utility.Utilities;
import timber.log.Timber;
import static com.sravan.and.beintouch.data.BeInTouchContract.ContactsEntry;

/**
 * Created by Sravan on 6/11/2017.
 */

public class AddContactEntry extends AsyncTask<Uri, Void, String> {

    private Context context;

    private static final String[] CONTACT_PICKER_OUTPUT_PROJECTION = {ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

    private static final int CONTACTS_ID_COLUMN = 0;
    private static final int LOOKUP_COLUMN = 1;
    private static final int THUMBNAIL_PHOTO_COLUMN = 2;
    private static final int NUMBER_COLUMN = 3;
    private static final int NAME_COLUMN  = 4;


    private static final String[] CALLLOG_CONTACT_PROJECTION = {CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION};

    private static final int DATE_COLUMN  = 3;

    private static final String[] CONTACT_ENTRY_ID = {ContactsContract.Contacts._ID};

    private static final String SELECTION_CONTACT_ENTRY = ContactsEntry.COLUMN_DISPLAYNAME + " = ? AND " +
            ContactsEntry.COLUMN_NUMBER + " = ?";

    private static final String SELECTION_CALLLOG_CONTACT = CallLog.Calls.NUMBER + " = ?";

    public AddContactEntry(Context context) {
        this.context = context;
    }

    /**
     * The doInBackground method will take the Uri from the Contact Picker and query the Uri for the details
     * of the contact. After retriving the details of the contact, a query is made again to the contact entry table
     * of to check if the selected contact is already added to the list. If the contact is already available then,
     * a toast is displayed in onPostExecute method that the method is already available. If the selected contact is
     * not added already then Android Call Log Content Provider will be queried to get the last contacted details of the
     * selected contact. If the selected contact has a valid last interaction value, that value gets added in the
     * last contacted column of the contact table     *
     * @param uri
     * @return
     */
    @Override
    protected String doInBackground(Uri... uri) {
        Uri contactCntPro ;
        if (uri!=null && uri[0].toString().length() != 0){
            contactCntPro = uri[0];
            Cursor cursor = context.getContentResolver().query(contactCntPro,
                    CONTACT_PICKER_OUTPUT_PROJECTION,
                    null,
                    null,
                    null);
            BeInTouchContact beInTouchContact = new BeInTouchContact();
            if (cursor != null && cursor.moveToFirst()) {
                beInTouchContact.setPhoneNumber(cursor.getString(NUMBER_COLUMN));
                beInTouchContact.setName(cursor.getString(NAME_COLUMN));
                beInTouchContact.setContactID(cursor.getLong(CONTACTS_ID_COLUMN));
                beInTouchContact.setLookup(cursor.getString(LOOKUP_COLUMN));
                beInTouchContact.setContactThumbnailPhotoID(cursor.getString(THUMBNAIL_PHOTO_COLUMN));
                String[] selectionargs = {beInTouchContact.getName(),beInTouchContact.getPhoneNumber()};

                // The below query is to check if the contact is already added to the database table
                Cursor cursorContact = context.getContentResolver().query(ContactsEntry.CONTENT_URI,
                        CONTACT_ENTRY_ID,
                        SELECTION_CONTACT_ENTRY,
                        selectionargs,
                        null);
                if (cursorContact != null && cursorContact.moveToFirst()) {
                    cursor.close();
                    cursorContact.close();
                    // Todo(1) Add the following string to android strings resource
                    return "The Contact is already added to the list";
                }

                // The below query is to check if the user has contacted the selected contact
                if(Utilities.checkPermission(context)){
                    Cursor callLogofContact = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                            CALLLOG_CONTACT_PROJECTION,
                            SELECTION_CALLLOG_CONTACT,
                            new String[]{beInTouchContact.getPhoneNumber()},
                            CallLog.Calls.DEFAULT_SORT_ORDER);

                    if (callLogofContact!= null && callLogofContact.moveToFirst()){
                        beInTouchContact.setLastcontacted(callLogofContact.getLong(DATE_COLUMN));
                        callLogofContact.close();
                    }
                }
                Timber.d("The Selected Contact is :" + beInTouchContact.getName() + " : " + beInTouchContact.getPhoneNumber());
                cursor.close();
            }
            ContentValues contactCV = beInTouchContact.createCVforContact();
            Uri returnUri = context.getContentResolver().insert(ContactsEntry.CONTENT_URI, contactCV);
            if (returnUri.toString().length() > 0){
                return "The Value has been added";
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
    }
}
