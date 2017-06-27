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
import android.os.Parcelable;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.sravan.and.beintouch.R;
import com.sravan.and.beintouch.adapters.ContactsEntryCursorAdapter;
import com.sravan.and.beintouch.bean.BeInTouchContact;
import com.sravan.and.beintouch.data.BeInTouchContract;
import com.sravan.and.beintouch.tasks.AddContactEntry;
import com.sravan.and.beintouch.tasks.DeleteContactEntry;
import com.sravan.and.beintouch.tasks.UpdateContactLastInteraction;
import com.sravan.and.beintouch.utility.FontCache;
import com.sravan.and.beintouch.utility.SampleMultiplePermissionListener;
import com.sravan.and.beintouch.utility.Utilities;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,ContactsEntryCursorAdapter.OnItemClickListener {

    private static final int CONTACT_PICKER_RESULT = 1001;
    private static final int CONTACTS_ENTRY_LOADER = 2001;
    private static final int CALL_LOG_LOADER = 2002;

    private Parcelable mLayoutManagerSavedState;

    private static final String[] CONTACTS_ENTRY_LOADER_PROJECTION = {BeInTouchContract.ContactsEntry._ID,
            BeInTouchContract.ContactsEntry.COLUMN_DISPLAYNAME,
            BeInTouchContract.ContactsEntry.COLUMN_NUMBER,
            BeInTouchContract.ContactsEntry.COLUMN_CONTACT_ID,
            BeInTouchContract.ContactsEntry.COLUMN_LOOKUP,
            BeInTouchContract.ContactsEntry.COLUMN_THUMBNAIL_PHOTO_ID,
            BeInTouchContract.ContactsEntry.COLUMN_LAST_CONTACTED,
            BeInTouchContract.ContactsEntry.COLUMN_PHOTO_ID};

    private static final String[] CALLLOG_PROJECTION = {CallLog.Calls._ID,CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
            CallLog.Calls.CACHED_NAME};

    private MultiplePermissionsListener allPermissionsListener;
    RecyclerView mRecyclerView;
    TextView emptytextView;
    Cursor mcursor;
    RecyclerView.LayoutManager layoutManager;
    ContactsEntryCursorAdapter contactsEntryCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("LAYOUTPOSITION")){
            mLayoutManagerSavedState = savedInstanceState.getParcelable("LAYOUTPOSITION");
        }
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
        Utilities.scheduleNotification(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                 * The FAB is to as a contact picker which will send an intent and wait for the other apps to
                 * handle the contact picker request. The result of the picker is read in the onActivityResult call back
                 */
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                PackageManager packageManager = getPackageManager();
                if (contactPickerIntent.resolveActivity(packageManager) != null) {
                    Utilities.logFirebaseEvent(MainActivity.this, getResources().getString(R.string.add_contact_event));
                    startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
                } else {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.no_application_to_access_contacts),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        emptytextView = (TextView) findViewById(R.id.contacts_entry_empty_textview);
        mRecyclerView = (RecyclerView) findViewById(R.id.contacts_entry_recycleview);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        contactsEntryCursorAdapter = new ContactsEntryCursorAdapter(null,BeInTouchContract.ContactsEntry.COLUMN_LAST_CONTACTED, this, this);
        mRecyclerView.setAdapter(contactsEntryCursorAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(MainActivity.this, "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();
                if (mcursor != null && mcursor.moveToPosition(position)){
                    DeleteContactEntry deleteContactEntry = new DeleteContactEntry(MainActivity.this, contactsEntryCursorAdapter);
                    deleteContactEntry.execute(mcursor.getLong(0));
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        getLoaderManager().initLoader(CONTACTS_ENTRY_LOADER, null, this);
        if(Utilities.checkPermission(this)){
            getLoaderManager().initLoader(CALL_LOG_LOADER, null, this);
        }

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("6C21B0458E3BA016232450AFD157E6C0")
                .build();
        mAdView.loadAd(adRequest);
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
                getResources().getString(R.string.permission_denied,getTypeFromPermission(permission)),
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
                permissionMessage = getResources().getString(R.string.permission_rationale_call);
                break;
            case 2:
                permissionMessage = getResources().getString(R.string.permission_rationale_contacts);
                break;
            case 3:
                permissionMessage = getResources().getString(R.string.permission_rationale_call_contacts);
                break;
            default:
                permissionMessage = getResources().getString(R.string.permission_rationale_default);

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
        switch (loader.getId()){
            case CONTACTS_ENTRY_LOADER:
                mcursor = data;
                contactsEntryCursorAdapter.changeCursor(mcursor);
                if (contactsEntryCursorAdapter.getItemCount() > 0){
                    emptytextView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    // notifyDataSetChanged is to resolve the issue coming when all the items are deleted from the
                    // recycler view(https://stackoverflow.com/questions/35653439/recycler-view-inconsistency-detected-invalid-view-holder-adapter-positionviewh)

                    contactsEntryCursorAdapter.notifyDataSetChanged();
                    emptytextView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }

                if(mRecyclerView.getVisibility() == View.VISIBLE
                        && mLayoutManagerSavedState != null
                        && layoutManager != null){
                    layoutManager.onRestoreInstanceState(mLayoutManagerSavedState);
                    mLayoutManagerSavedState = null;
                }
                break;
            case CALL_LOG_LOADER:
                UpdateContactLastInteraction updateContactLastInteraction = new UpdateContactLastInteraction(this);
                updateContactLastInteraction.execute();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * onItemClick method is the call back listener that is defined and called when an item is clicked in the
     * recycler adapter. Here the required parcelable infromation will is captured in an intent and is passed to
     * the required activity/fragment
     * @param position the number in the list displayed in the recycler view
     */
    @Override
    public void onItemClick(int position) {
        if (contactsEntryCursorAdapter != null){
            //Uri uri = contactsEntryCursorAdapter.getContactLookupUri(position);
            BeInTouchContact beInTouchContact = contactsEntryCursorAdapter.createContactfromCursor(position);
            //Toast.makeText(this, "Item at" + uri.toString() + " position is clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, ContactDetailActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, beInTouchContact);
            startActivity(intent);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(contactsEntryCursorAdapter != null){
            outState.putParcelable("LAYOUTPOSITION", mRecyclerView.getLayoutManager().onSaveInstanceState());
        }
        super.onSaveInstanceState(outState);
    }
}
