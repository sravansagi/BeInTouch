package com.sravan.and.beintouch.utility;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.sravan.and.beintouch.R;
import com.sravan.and.beintouch.data.BeInTouchContract;
import com.sravan.and.beintouch.ui.MainActivity;

import timber.log.Timber;

/**
 * Created by HP on 6/24/2017.
 */

public class NotificationFirebaseJobService extends JobService {

    public static final int CONTACT_INTERACT = 1304;
    private static final int CONTACT_PENDING_ID = 1204;

    private static final String SORTORDER_LASTCONTACT_ASC_LIMIT_1 = "lastcontact ASC LIMIT 1";

    private static final String[] CONTACTS_ENTRY_LOADER_PROJECTION = {BeInTouchContract.ContactsEntry._ID,
            BeInTouchContract.ContactsEntry.COLUMN_DISPLAYNAME,
            BeInTouchContract.ContactsEntry.COLUMN_NUMBER,
            BeInTouchContract.ContactsEntry.COLUMN_CONTACT_ID,
            BeInTouchContract.ContactsEntry.COLUMN_LOOKUP,
            BeInTouchContract.ContactsEntry.COLUMN_THUMBNAIL_PHOTO_ID,
            BeInTouchContract.ContactsEntry.COLUMN_LAST_CONTACTED};

    @Override
    public boolean onStartJob(JobParameters params) {
        String contactName = "";
        String phoneNumber = "";
        Timber.d("Inside the Notification service");
        Cursor cursorContact = this.getContentResolver().query(BeInTouchContract.ContactsEntry.CONTENT_URI,
                CONTACTS_ENTRY_LOADER_PROJECTION,
                null,
                null,
                SORTORDER_LASTCONTACT_ASC_LIMIT_1);
        if (cursorContact != null && cursorContact.moveToFirst()) {
            contactName = cursorContact.getString(1);
            phoneNumber = cursorContact.getString(2);
            cursorContact.close();
            if (phoneNumber != null && phoneNumber.length() > 5) {
                Intent buttonIntent = new Intent(this, NotificationActionReceiver.class);
                buttonIntent.putExtra(Intent.EXTRA_TEXT, phoneNumber);
                PendingIntent btPendingIntent = PendingIntent.getBroadcast(this, 0, buttonIntent, 0);
                NotificationCompat.Action callContactAction = new NotificationCompat.Action(R.drawable.ic_call_24dp,
                        this.getResources().getString(R.string.notification_action),
                        btPendingIntent);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setContentTitle(this.getResources().getString(R.string.notification_contact_name, contactName))
                                .setSmallIcon(R.drawable.ic_notification)
                                .setContentText(this.getResources().getString(R.string.notification_text))
                                .addAction(callContactAction)
                                .setAutoCancel(true);
                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(this, MainActivity.class);
                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MainActivity.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(CONTACT_PENDING_ID, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                mNotificationManager.notify(CONTACT_INTERACT, mBuilder.build());
            }
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
