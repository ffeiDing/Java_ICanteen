package com.netlab.vc.coursehelper.util.jsonResults;

import java.io.Serializable;

/**
 * Created by Vc on 2016/11/24.
 */

public class LastSignIn implements Serializable {
    private String _id;
    private String uuid;
    private int count;
    private long from;
    private long to;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }
}
