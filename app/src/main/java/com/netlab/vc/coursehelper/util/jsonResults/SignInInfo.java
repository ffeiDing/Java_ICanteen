package com.netlab.vc.coursehelper.util.jsonResults;

/**
 * Created by Vc on 2016/12/16.
 */

public class SignInInfo {
    String uuid;
    boolean enable;
    int total;
    String signin_id;
    int user;
    boolean success;

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getSignin_id() {
        return signin_id;
    }

    public void setSignin_id(String signin_id) {
        this.signin_id = signin_id;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
