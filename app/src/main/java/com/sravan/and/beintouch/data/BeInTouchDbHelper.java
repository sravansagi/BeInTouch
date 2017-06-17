package com.sravan.and.beintouch.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.sravan.and.beintouch.data.BeInTouchContract.ContactsEntry;

/**
 * Created by HP on 6/10/2017.
 */

public class BeInTouchDbHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "beintouch.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 1;

    public BeInTouchDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_TABLE = "CREATE TABLE " + ContactsEntry.TABLE_NAME + " (" +
                ContactsEntry._ID                   + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                ContactsEntry.COLUMN_CONTACT_ID     + " INTERGER NOT NULL, "+
                ContactsEntry.COLUMN_LOOKUP         + " TEXT NOT NULL, "+
                ContactsEntry.COLUMN_NUMBER         + " TEXT, " +
                ContactsEntry.COLUMN_DISPLAYNAME    + " TEXT, " +
                ContactsEntry.COLUMN_THUMBNAIL_PHOTO_ID + " TEXT," +
                ContactsEntry.COLUMN_LAST_CONTACTED + " INTERGER DEFAULT 0);";
        sqLiteDatabase.execSQL(CREATE_TABLE);
        }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ContactsEntry.TABLE_NAME);
        onCreate(db);

    }
}
