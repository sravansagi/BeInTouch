package com.sravan.and.beintouch.bean;

/**
 * Created by Sravan on 6/17/2017.
 */

import android.content.ContentValues;

import com.sravan.and.beintouch.data.BeInTouchContract;

/**
 * The {@link BeInTouchContact} class is the bean class that holds data of a row in the contactentry table of the
 * beintouch database. The class provides the various utility methods.
 */
public class BeInTouchContact {

    private long _id;
    private long contactID;
    private String lookup;
    private String phoneNumber;
    private String name;
    private String contactThumbnailPhotoID;
    private long lastcontacted;

    public BeInTouchContact(long _id, long contactID, String lookup, String phoneNumber, String name, String contactThumbnailPhotoID, long lastcontacted) {
        this._id = _id;
        this.contactID = contactID;
        this.lookup = lookup;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.contactThumbnailPhotoID = contactThumbnailPhotoID;
        this.lastcontacted = lastcontacted;
    }

    public BeInTouchContact(long contactID, String lookup, String phoneNumber, String name, String contactThumbnailPhotoID, long lastcontacted) {
        this.contactID = contactID;
        this.lookup = lookup;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.contactThumbnailPhotoID = contactThumbnailPhotoID;
        this.lastcontacted = lastcontacted;
    }

    public BeInTouchContact() {

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

    public ContentValues createCVforContact(){
        ContentValues values = new ContentValues();
        values.put(BeInTouchContract.ContactsEntry.COLUMN_CONTACT_ID,contactID);
        values.put(BeInTouchContract.ContactsEntry.COLUMN_LOOKUP,lookup);
        values.put(BeInTouchContract.ContactsEntry.COLUMN_DISPLAYNAME,name);
        values.put(BeInTouchContract.ContactsEntry.COLUMN_THUMBNAIL_PHOTO_ID, contactThumbnailPhotoID);
        values.put(BeInTouchContract.ContactsEntry.COLUMN_NUMBER, phoneNumber);
        values.put(BeInTouchContract.ContactsEntry.COLUMN_LAST_CONTACTED,lastcontacted);
        return values;
    }
}
