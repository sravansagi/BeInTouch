package com.sravan.and.beintouch.utility;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import com.sravan.and.beintouch.R;

import static com.sravan.and.beintouch.utility.NotificationFirebaseJobService.CONTACT_INTERACT;

/**
 * Created by HP on 6/27/2017.
 */

public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //int notificationId = intent.getIntExtra("notificationId", 0);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(CONTACT_INTERACT);
        String phoneNumber = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (phoneNumber != null && phoneNumber.length()> 5 ){
            Intent callContactIntent =  new Intent(Intent.ACTION_VIEW);
            PackageManager packageManager = context.getPackageManager();
            if (callContactIntent.resolveActivity(packageManager) != null) {
                Utilities.logFirebaseEvent(context, context.getResources().getString(R.string.call_contact_from_notification_event));
                callContactIntent.setData(Uri.parse("tel:"+ phoneNumber));
                context.startActivity(callContactIntent);
            } else {
                Toast.makeText(context,context.getResources().getString(R.string.no_application_to_make_phone_call),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context,context.getResources().getString(R.string.problem_opening_selected_contact),
                    Toast.LENGTH_LONG).show();
        }
    }
}
