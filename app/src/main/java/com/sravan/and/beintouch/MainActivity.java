package com.sravan.and.beintouch;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sravan.and.beintouch.utility.FontCache;
import com.sravan.and.beintouch.utility.SampleMultiplePermissionListener;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final int CONTACT_SELECT_RESULT = 1001;
    private static final String[] CONTACT_PICKER_OUTPUT_PROJECTION = {ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

    private MultiplePermissionsListener allPermissionsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText(getResources().getString(R.string.app_name));
        Typeface typeface = FontCache.get("fonts/Pacifico-Regular.ttf", getApplicationContext());
        if (typeface!=null){
            toolbarText.setTypeface(typeface);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * The FAB is to as a contact picker which will send an intent and wait for the other apps to
                 * handle the contact picker request. The result of the picker is read in the onActivityResult call back
                 */
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                PackageManager packageManager = getPackageManager();
                if (contactPickerIntent.resolveActivity(packageManager) != null) {
                    startActivityForResult(contactPickerIntent, CONTACT_SELECT_RESULT);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "There is not application to access the contacts",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        allPermissionsListener = new SampleMultiplePermissionListener(this);
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS)
                .withListener(allPermissionsListener)
                .onSameThread()
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override public void onError(DexterError error) {
                        Timber.e("There was an error: " + error.toString());
                    }
                }).check();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is part of the dextor library and is called when user allows a permission
     * @param permission the permission contains the name of permission that user allowed
     */
    public void showPermissionGranted(String permission) {
        Timber.d(getTypeFromPermission(permission) + " permission granted");
    }

    /**
     * This method is part of the dextor library and is called when user deny a permission
     * @param permission the permission contains the name of permission that user denied
     */
    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {
        Toast.makeText(this,
                getTypeFromPermission(permission) + " permission is denied. Closing the App. Please provide permissions next time",
                Toast.LENGTH_LONG).show();
        finish();
    }

    /**
     * This method is part of the dextor library and is called when the permission that is already
     * asked earlier is again being asked. A dialog will be shown providing the importance of the permission
     * before continuing the with permission dialog. The permission identifer is very importance in terms of identifying
     * the permission for showing the rationale. If a new dangarous permission is added, Then the corresponding permission
     * has to be checked in the {@link SampleMultiplePermissionListener} onPermissionRationaleShouldBeShown method
     * @param token
     * @param permissionIdentifier
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void showPermissionRationale(final PermissionToken token, int permissionIdentifier) {

        String permissionMessage = "" ;
        switch (permissionIdentifier){
            case 1:
                permissionMessage = "We need permission to read call log for the app";
                break;
            case 2:
                permissionMessage = "We need permissions to read contacts for the app";
                break;
            case 3:
                permissionMessage = "We need permission for call and contacts for the app";
                break;
            default:
                permissionMessage = "We need the required permissions for the app.Please provide";

        }
        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.permission_dialog_title))
                .setMessage(permissionMessage)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.cancelPermissionRequest();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.continuePermissionRequest();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override public void onDismiss(DialogInterface dialog) {
                        token.cancelPermissionRequest();
                    }
                })
                .show();
    }

    private String getTypeFromPermission(String name) {
        String permissionType;

        switch (name) {
            case Manifest.permission.READ_CALL_LOG:
                permissionType = "READ_CALL_LOG";
                break;
            case Manifest.permission.READ_CONTACTS:
                permissionType = "READ_CONTACTS";
                break;
            default:
                throw new RuntimeException("This permission is not required");
        }

        return permissionType;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == CONTACT_SELECT_RESULT) {
            Uri contactPickerResultUri = data.getData();
            Cursor cursor = getContentResolver().query(contactPickerResultUri,
                    CONTACT_PICKER_OUTPUT_PROJECTION,
                    null,
                    null,
                    null);
            if (cursor.moveToFirst()) {
                String phoneNo = cursor.getString(0);
                String name = cursor.getString(1);
                Toast.makeText(this, "The Selected Contact is :" + name + " : " + phoneNo, Toast.LENGTH_SHORT).show();
                Timber.d("The Selected Contact is :" + name + " : " + phoneNo );
            }
            cursor.close();
    }
}

}
