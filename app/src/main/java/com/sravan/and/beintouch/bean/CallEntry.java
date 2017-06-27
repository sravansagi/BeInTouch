package com.sravan.and.beintouch.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.CallLog;

/**
 * Created by HP on 6/21/2017.
 */

public class CallEntry implements Parcelable {
    public static final Parcelable.Creator<CallEntry> CREATOR = new Parcelable.Creator<CallEntry>() {
        @Override
        public CallEntry createFromParcel(Parcel source) {
            return new CallEntry(source);
        }

        @Override
        public CallEntry[] newArray(int size) {
            return new CallEntry[size];
        }
    };
    private long date;
    private long duration;
    private boolean incoming;

    public CallEntry() {
    }

    protected CallEntry(Parcel in) {
        this.date = in.readLong();
        this.duration = in.readLong();
        this.incoming = in.readByte() != 0;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean getIncoming() {
        return incoming;
    }

    public void setIncoming(int type) {
        switch (type) {
            case CallLog.Calls.OUTGOING_TYPE:
                incoming = false;
                break;
            default:
                incoming = true;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.date);
        dest.writeLong(this.duration);
        dest.writeByte(this.incoming ? (byte) 1 : (byte) 0);
    }
}
