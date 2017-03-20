package com.netlab.vc.coursehelper.util.jsonResults;

import java.io.Serializable;

/**
 * Created by Vc on 2016/11/24.
 */

public class SignIn implements Serializable {
    private LastSignIn last;
    private int total;
    private boolean enable;
    private int self;

    public LastSignIn getLast() {
        return last;
    }

    public void setLast(LastSignIn last) {
        this.last = last;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getSelf() {
        return self;
    }

    public void setSelf(int self) {
        this.self = self;
    }
}
