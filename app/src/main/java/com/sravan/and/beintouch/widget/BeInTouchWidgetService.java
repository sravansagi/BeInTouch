package com.sravan.and.beintouch.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Binder;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.sravan.and.beintouch.R;
import com.sravan.and.beintouch.bean.BeInTouchContact;
import com.sravan.and.beintouch.data.BeInTouchContract;

import java.io.IOException;

import timber.log.Timber;

/**
 * Created by HP on 6/25/2017.
 */

public class BeInTouchWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new BeInTouchViewsFactory(this.getApplicationContext());
    }
}

class BeInTouchViewsFactory implements RemoteViewsService.RemoteViewsFactory{

    Context mContext;
    Cursor mCursor;

    private static final String[] CONTACTS_ENTRY_LOADER_PROJECTION = {BeInTouchContract.ContactsEntry._ID,
            BeInTouchContract.ContactsEntry.COLUMN_DISPLAYNAME,
            BeInTouchContract.ContactsEntry.COLUMN_NUMBER,
            BeInTouchContract.ContactsEntry.COLUMN_CONTACT_ID,
            BeInTouchContract.ContactsEntry.COLUMN_LOOKUP,
            BeInTouchContract.ContactsEntry.COLUMN_THUMBNAIL_PHOTO_ID,
            BeInTouchContract.ContactsEntry.COLUMN_LAST_CONTACTED,
            BeInTouchContract.ContactsEntry.COLUMN_PHOTO_ID};
    private static final int NAME_COLUMN  = 1;
    private static final int NUMBER_COLUMN = 2;
    private static final int CONTACTS_ID_COLUMN = 3;
    private static final int LOOKUP_COLUMN = 4;
    private static final int THUMBNAIL_PHOTO_COLUMN = 5;
    private static final int LAST_CONTACTED = 6;
    private static final int PHOTO_ID_COLUMN = 7;

    public BeInTouchViewsFactory(Context applicationContext) {
        this.mContext = applicationContext;
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(BeInTouchContract.ContactsEntry.CONTENT_URI,
                CONTACTS_ENTRY_LOADER_PROJECTION,
                null,
                null,
                null);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null){
            mCursor.close();
        }
        mCursor = null;

    }

    @Override
    public int getCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Timber.d("getViewAt");
        if (position == AdapterView.INVALID_POSITION ||
                mCursor == null || !mCursor.moveToPosition(position)) {
            return null;
        }
        mCursor.moveToPosition(position);
        RemoteViews views = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_list_item);
        BeInTouchContact beInTouchContact = createContactfromCursor(mCursor);
        views.setTextViewText(R.id.widget_contactname, beInTouchContact.getName());
        views.setTextViewText(R.id.widget_contactlastinteraction, BeInTouchContact.getLastInteraction(mContext,beInTouchContact.getLastcontacted()));
        if (beInTouchContact.getContactThumbnailPhotoID() != null
                && beInTouchContact.getContactThumbnailPhotoID().length() > 0){
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(),Uri.parse(beInTouchContact.getContactThumbnailPhotoID()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null){
                views.setImageViewBitmap(R.id.widget_contactavatar, getCircleBitmap(bitmap));
            }
            //views.setImageViewUri(R.id.widget_contactavatar, Uri.parse(beInTouchContact.getContactThumbnailPhotoID()));
        } else {
            views.setImageViewResource(R.id.widget_contactavatar, R.drawable.ic_contact_thumbnail);
        }
        final Intent fillIntent = new Intent();
        fillIntent.putExtra(Intent.EXTRA_TEXT, beInTouchContact);
        views.setOnClickFillInIntent(R.id.widget_entry_list, fillIntent);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public BeInTouchContact createContactfromCursor(Cursor cursor){
        if (cursor != null) {
            BeInTouchContact beInTouchContact = new BeInTouchContact();
            beInTouchContact.setPhoneNumber(cursor.getString(NUMBER_COLUMN));
            beInTouchContact.setName(cursor.getString(NAME_COLUMN));
            beInTouchContact.setContactID(cursor.getLong(CONTACTS_ID_COLUMN));
            beInTouchContact.setLookup(cursor.getString(LOOKUP_COLUMN));
            beInTouchContact.setContactThumbnailPhotoID(cursor.getString(THUMBNAIL_PHOTO_COLUMN));
            beInTouchContact.setLastcontacted(cursor.getLong(LAST_CONTACTED));
            beInTouchContact.setPhotoID(cursor.getString(PHOTO_ID_COLUMN));
            return beInTouchContact;
        }
        return null;
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        return output;
    }

}
