package com.sravan.and.beintouch.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.sravan.and.beintouch.data.BeInTouchContract;

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

    private static final String[] CONTACT_ENTRY_ID = {ContactsContract.Contacts._ID};
    private static final String SELECTION = ContactsEntry.COLUMN_DISPLAYNAME + " = ? AND " +
            ContactsEntry.COLUMN_NUMBER + " = ?";

    public AddContactEntry(Context context) {
        this.context = context;
    }

    /**
     * The doInBackground method will take the Uri from the Contact Picker and query the Uri for the details
     * of the contact. After retriving the details of the contact, a query is made again to the contact entry table
     * of to check if the selected contact is already added to the list. If the contact is already available then,
     * a toast is displayed in onPostExecute method that the method is already available. If the selected contact is
     * not added already then the contact is added.
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

            String phoneNo = "";
            String name = "";
            Long contactid = Long.valueOf(0);
            String lookup = "";
            String photoThumbnail = "";
            if (cursor != null && cursor.moveToFirst()) {
                phoneNo = cursor.getString(3);
                name = cursor.getString(4);
                contactid = cursor.getLong(0);
                lookup = cursor.getString(1);
                photoThumbnail = cursor.getString(2);
                String[] selectionargs = {name,phoneNo};
                Cursor cursorContact = context.getContentResolver().query(ContactsEntry.CONTENT_URI,
                        CONTACT_ENTRY_ID,
                        SELECTION,
                        selectionargs,
                        null);
                if (cursorContact != null && cursorContact.moveToFirst()) {
                    cursor.close();
                    cursorContact.moveToFirst();
                    // Todo(1) Add the following string to android strings resource
                    return "The Contact is already added to the list";
                }
                Timber.d("The Selected Contact is :" + name + " : " + phoneNo);
                cursor.close();
            }
            ContentValues values = new ContentValues();
            values.put(ContactsEntry.COLUMN_CONTACT_ID,contactid);
            values.put(ContactsEntry.COLUMN_LOOKUP,lookup);
            values.put(ContactsEntry.COLUMN_DISPLAYNAME,name);
            values.put(ContactsEntry.COLUMN_PHOTO_ID,photoThumbnail);
            values.put(ContactsEntry.COLUMN_NUMBER, phoneNo);
            Uri returnUri = context.getContentResolver().insert(ContactsEntry.CONTENT_URI, values);
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
