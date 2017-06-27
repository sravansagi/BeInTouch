package com.sravan.and.beintouch.bean;

/**
 * Created by Sravan on 6/17/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.sravan.and.beintouch.R;
import com.sravan.and.beintouch.data.BeInTouchContract;

import java.util.Date;

/**
 * The {@link BeInTouchContact} class is the bean class that holds data of a row in the contactentry table of the
 * beintouch database. The class provides the various utility methods.
 */
public class BeInTouchContact implements Parcelable {

    private long _id;
    private long contactID;
    private String lookup;
    private String phoneNumber;
    private String name;
    private String contactThumbnailPhotoID;
    private long lastcontacted;
    private String photoID;

    public BeInTouchContact() {
    }

    public BeInTouchContact(long _id, long lastcontacted) {
        this._id = _id;
        this.lastcontacted = lastcontacted;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long getContactID() {
        return contactID;
    }

    public void setContactID(long contactID) {
        this.contactID = contactID;
    }

    public String getLookup() {
        return lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactThumbnailPhotoID() {
        return contactThumbnailPhotoID;
    }

    public void setContactThumbnailPhotoID(String contactThumbnailPhotoID) {
        this.contactThumbnailPhotoID = contactThumbnailPhotoID;
    }

    public long getLastcontacted() {
        return lastcontacted;
    }

    public void setLastcontacted(long lastcontacted) {
        this.lastcontacted = lastcontacted;
    }

    public String getPhotoID() {
        return photoID;
    }

    public void setPhotoID(String photoID) {
        this.photoID = photoID;
    }

    public ContentValues createCVforContact(){
        ContentValues values = new ContentValues();
        values.put(BeInTouchContract.ContactsEntry.COLUMN_CONTACT_ID,contactID);
        values.put(BeInTouchContract.ContactsEntry.COLUMN_LOOKUP,lookup);
        values.put(BeInTouchContract.ContactsEntry.COLUMN_DISPLAYNAME,name);
        values.put(BeInTouchContract.ContactsEntry.COLUMN_THUMBNAIL_PHOTO_ID, contactThumbnailPhotoID);
        values.put(BeInTouchContract.ContactsEntry.COLUMN_NUMBER, phoneNumber);
        values.put(BeInTouchContract.ContactsEntry.COLUMN_LAST_CONTACTED,lastcontacted);
        values.put(BeInTouchContract.ContactsEntry.COLUMN_PHOTO_ID, photoID);
        return values;
    }

    public static String getLastInteraction(Context context,long lastcontacted){
        if (lastcontacted == 0){
            return context.getResources().getString(R.string.contact_no_interaction);
        }
        Date currentDate = new Date();
        long currentTime = currentDate.getTime();

        long diffTime = currentTime - lastcontacted;
        // Since the time is stored in epoch format, it is converted to sec
        long diffSec = diffTime/1000;
        if(diffSec < 3600){
            return  context.getResources().getString(R.string.contact_entry_mins_ago, diffSec/60);
        } else if (diffSec < 86400){
            return  context.getResources().getString(R.string.contact_entry_hours_ago, (diffSec/60)/60);
        } else {
            return  context.getResources().getString(R.string.contact_entry_days_ago, ((diffSec/60)/60)/24) ;
        }
    }

    public static String getLastInteractedHistory(Context context, long lastcontacted){
        if (lastcontacted == 0){
            return context.getResources().getString(R.string.contact_no_interaction);
        }
        Date currentDate = new Date();
        long currentTime = currentDate.getTime();

        long diffTime = currentTime - lastcontacted;
        // Since the time is stored in epoch format, it is converted to sec
        long diffSec = diffTime/1000;
        if(diffSec < 3600){
            return  context.getResources().getString(R.string.contact_mins_ago, diffSec/60);
        } else if (diffSec < 86400){
            return  context.getResources().getString(R.string.contact_hours_ago, (diffSec/60)/60) ;
        } else {
            return context.getResources().getString(R.string.contact_days_ago, ((diffSec/60)/60)/24);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this._id);
        dest.writeLong(this.contactID);
        dest.writeString(this.lookup);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.name);
        dest.writeString(this.contactThumbnailPhotoID);
        dest.writeLong(this.lastcontacted);
        dest.writeString(this.photoID);
    }

    protected BeInTouchContact(Parcel in) {
        this._id = in.readLong();
        this.contactID = in.readLong();
        this.lookup = in.readString();
        this.phoneNumber = in.readString();
        this.name = in.readString();
        this.contactThumbnailPhotoID = in.readString();
        this.lastcontacted = in.readLong();
        this.photoID = in.readString();
    }

    public static final Parcelable.Creator<BeInTouchContact> CREATOR = new Parcelable.Creator<BeInTouchContact>() {
        @Override
        public BeInTouchContact createFromParcel(Parcel source) {
            return new BeInTouchContact(source);
        }

        @Override
        public BeInTouchContact[] newArray(int size) {
            return new BeInTouchContact[size];
        }
    };
}
