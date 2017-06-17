package com.sravan.and.beintouch.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.sravan.and.beintouch.R;
import com.sravan.and.beintouch.adapters.ContactsEntryCursorAdapter;
import com.sravan.and.beintouch.data.BeInTouchContract;
import com.sravan.and.beintouch.tasks.AddContactEntry;
import com.sravan.and.beintouch.utility.FontCache;
import com.sravan.and.beintouch.utility.SampleMultiplePermissionListener;
import com.sravan.and.beintouch.utility.Utilities;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,ContactsEntryCursorAdapter.OnItemClickListener {

    private static final int CONTACT_PICKER_RESULT = 1001;
    private static final int CONTACTS_ENTRY_LOADER = 2001;
    private static final int CALL_LOG_LOADER = 2002;


    private static final String[] CONTACTS_ENTRY_LOADER_PROJECTION = {BeInTouchContract.ContactsEntry._ID,
            BeInTouchContract.ContactsEntry.COLUMN_DISPLAYNAME,
            BeInTouchContract.ContactsEntry.COLUMN_NUMBER,
            BeInTouchContract.ContactsEntry.COLUMN_CONTACT_ID,
            BeInTouchContract.ContactsEntry.COLUMN_LOOKUP,
            BeInTouchContract.ContactsEntry.COLUMN_THUMBNAIL_PHOTO_ID,
            BeInTouchContract.ContactsEntry.COLUMN_LAST_CONTACTED};

    private static final String[] CALLLOG_PROJECTION = {CallLog.Calls._ID,CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
            CallLog.Calls.CACHED_NAME};

    private MultiplePermissionsListener allPermissionsListener;
    RecyclerView mRecyclerView;
    TextView emptytextView;
    RecyclerView.LayoutManager layoutManager;
    ContactsEntryCursorAdapter contactsEntryCursorAdapter;

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
        allPermissionsListener = new SampleMultiplePermissionListener(this);
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS)
                .withListener(allPermissionsListener)
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override public void onError(DexterError error) {
                        Timber.e("There was an error: " + error.toString());
                    }
                }).check();

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
                    startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "There is not application to access the contacts",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        emptytextView = (TextView) findViewById(R.id.contacts_entry_empty_textview);
        mRecyclerView = (RecyclerView) findViewById(R.id.contacts_entry_recycleview);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        contactsEntryCursorAdapter = new ContactsEntryCursorAdapter(null,BeInTouchContract.ContactsEntry._ID, this, this);
        mRecyclerView.setAdapter(contactsEntryCursorAdapter);
        getLoaderManager().initLoader(CONTACTS_ENTRY_LOADER, null, this);
        if(Utilities.checkPermission(this)){
            getLoaderManager().initLoader(CALL_LOG_LOADER, null, this);
        }
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
        if(Utilities.checkPermission(this)){
            getLoaderManager().initLoader(CALL_LOG_LOADER, null, this);
        }
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
        if (resultCode == Activity.RESULT_OK && requestCode == CONTACT_PICKER_RESULT) {
            Uri contactPickerResultUri = data.getData();
            AddContactEntry addContactEntry = new AddContactEntry(this);
            addContactEntry.execute(contactPickerResultUri);
    }
}

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CONTACTS_ENTRY_LOADER:
                return new CursorLoader(
                        this,
                        BeInTouchContract.ContactsEntry.CONTENT_URI,
                        CONTACTS_ENTRY_LOADER_PROJECTION,
                        null,
                        null,
                        null);
            case CALL_LOG_LOADER:
                return new CursorLoader(
                        this,
                        CallLog.Calls.CONTENT_URI,
                        CALLLOG_PROJECTION,
                        null,
                        null,
                        CallLog.Calls.DEFAULT_SORT_ORDER);
            default:
                throw new UnsupportedOperationException("Unknown cursor loader id");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Timber.d( "loader" + loader.getId() + "");
        for (String e: data.getColumnNames()) {
            Timber.d(e);
        }
        switch (loader.getId()){
            case CONTACTS_ENTRY_LOADER:
                Timber.d("Contacts Entry result");
                contactsEntryCursorAdapter.changeCursor(data);
                if (contactsEntryCursorAdapter.getItemCount() > 0){
                    emptytextView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    emptytextView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
                break;
            case CALL_LOG_LOADER:
                for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                    Timber.d("Call log result");
                }
                break;

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * onItemClick method is the call back listener that is defined and called when an item is clicked in the
     * recycler adapter.
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        if (contactsEntryCursorAdapter != null){
            Uri uri = contactsEntryCursorAdapter.getContactLookupUri(position);
            //Toast.makeText(this, "Item at" + uri.toString() + " position is clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, ContactDetailActivity.class);
            startActivity(intent);
        }

    }
}
