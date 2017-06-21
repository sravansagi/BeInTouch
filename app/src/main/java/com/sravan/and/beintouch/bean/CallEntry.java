package com.sravan.and.beintouch.bean;

import android.provider.CallLog;

import java.util.ArrayList;

/**
 * Created by HP on 6/21/2017.
 */

public class CallEntry {
    private long date;
    private long duration;
    private boolean incoming;

    public CallEntry() {
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
        switch (type){
            case CallLog.Calls.OUTGOING_TYPE:
                incoming = false;
                break;
            default: incoming = true;
        }
    }


}
