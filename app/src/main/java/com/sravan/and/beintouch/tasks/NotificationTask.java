package com.sravan.and.beintouch.tasks;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.sravan.and.beintouch.R;
import com.sravan.and.beintouch.data.BeInTouchContract;
import com.sravan.and.beintouch.ui.MainActivity;

/**
 * Created by HP on 6/24/2017.
 */

public class NotificationTask extends AsyncTask<Void, Void, Void> {

    Context mContext;

    private static final int CONTACT_INTERACT = 1304;
    private static final int CONTACT_PENDING_ID = 1204;

    private static final String SORTORDER_LASTCONTACT_ASC_LIMIT_1 = "lastcontact ASC LIMIT 1";

    private static final String[] CONTACTS_ENTRY_LOADER_PROJECTION = {BeInTouchContract.ContactsEntry._ID,
            BeInTouchContract.ContactsEntry.COLUMN_DISPLAYNAME,
            BeInTouchContract.ContactsEntry.COLUMN_NUMBER,
            BeInTouchContract.ContactsEntry.COLUMN_CONTACT_ID,
            BeInTouchContract.ContactsEntry.COLUMN_LOOKUP,
            BeInTouchContract.ContactsEntry.COLUMN_THUMBNAIL_PHOTO_ID,
            BeInTouchContract.ContactsEntry.COLUMN_LAST_CONTACTED};

    public NotificationTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String contactName = "";
        String phoneNumber = "";
        Cursor cursorContact = mContext.getContentResolver().query(BeInTouchContract.ContactsEntry.CONTENT_URI,
                CONTACTS_ENTRY_LOADER_PROJECTION,
                null,
                null,
                SORTORDER_LASTCONTACT_ASC_LIMIT_1);
        if (cursorContact!=null && cursorContact.moveToFirst()){
            contactName = cursorContact.getString(1);
            phoneNumber = cursorContact.getString(2);
        }

        if ( phoneNumber != null){
            Intent callContactIntent = new Intent(Intent.ACTION_VIEW);
            PackageManager packageManager = mContext.getPackageManager();
            if (callContactIntent.resolveActivity(packageManager) != null) {
                callContactIntent.setData(Uri.parse("tel:"+ phoneNumber));
            }
            PendingIntent callContactPendingIntent = PendingIntent.getActivity(
                    mContext,
                    CONTACT_PENDING_ID,
                    callContactIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationCompat.Action callContactAction = new NotificationCompat.Action(R.drawable.ic_call_24dp,
                    "Call",
                    callContactPendingIntent);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(mContext)
                            .setContentTitle("Talk with " + contactName)
                            .setSmallIcon(R.drawable.ic_call_24dp)
                            .setContentText("Have you both spoke recently? Talk now")
                            .addAction(callContactAction)
                            .setAutoCancel(true);
            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(mContext, MainActivity.class);
            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(CONTACT_PENDING_ID,PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(CONTACT_INTERACT, mBuilder.build());
        }


        return null;
    }
}
