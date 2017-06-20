package com.sravan.and.beintouch.data;

/**
 * Created by Sravan on 6/10/2017.
 */

import android.content.ContentProvider;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * BeInTouchContract defines the tables and the details of the content provider used in BeInTouch application
 */
public class BeInTouchContract {


    /**
     * The contactsentry table contains all the favorite contacts that are added to the BeInTouch application
     */
    public static final String AUTHORITY = "com.sravan.and.beintouch";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_CONTACTSENTRY = "contactsentry";

    public static final class ContactsEntry implements BaseColumns{


        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_CONTACTSENTRY)
                .build();

        public static final String TABLE_NAME = "contactsentry";
        public static final String COLUMN_CONTACT_ID = "contactid";
        public static final String COLUMN_LOOKUP = "lookup";
        public static final String COLUMN_NUMBER = "number";
        public static final String COLUMN_DISPLAYNAME = "displayname";
        public static final String COLUMN_THUMBNAIL_PHOTO_ID ="thumbnailphotoid";
        public static final String COLUMN_PHOTO_ID ="photoid";
        public static final String COLUMN_LAST_CONTACTED ="lastcontact";
    }


}
