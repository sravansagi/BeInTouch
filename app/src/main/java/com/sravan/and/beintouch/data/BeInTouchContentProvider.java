package com.sravan.and.beintouch.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.sravan.and.beintouch.data.BeInTouchContract.ContactsEntry;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Sravan on 6/11/2017.
 */

public class BeInTouchContentProvider extends ContentProvider {


    public static final int CONTACT_ENTRIES = 100;
    public static final int CONTACT_ENTRIES_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private BeInTouchDbHelper beInTouchDbHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(BeInTouchContract.AUTHORITY, BeInTouchContract.PATH_CONTACTSENTRY, CONTACT_ENTRIES);
        uriMatcher.addURI(BeInTouchContract.AUTHORITY, BeInTouchContract.PATH_CONTACTSENTRY + "/#", CONTACT_ENTRIES_WITH_ID);
        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        beInTouchDbHelper = new BeInTouchDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase sqLiteDatabase = beInTouchDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match){
            case CONTACT_ENTRIES:
                retCursor = sqLiteDatabase.query(ContactsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CONTACT_ENTRIES_WITH_ID:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
        //return null;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = beInTouchDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match){
            case CONTACT_ENTRIES:
                long id = db.insert(ContactsEntry.TABLE_NAME, null, values);
                if (id > 0){
                    returnUri = ContentUris.withAppendedId(ContactsEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
        //return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
        //return 0;
    }
}
