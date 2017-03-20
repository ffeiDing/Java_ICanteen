package com.netlab.vc.coursehelper.util.jsonResults;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Vc on 2016/11/19.
 * 用json处理服务器返回的结果信息
 */

public class LoginResult {


    String _id;
    String token;
    Boolean success;

    /*
    String name;
    String email;
    String phone;
    String realName;
    */
    @SerializedName("_id")
    public String get_id() {
        return _id;
    }
    @SerializedName("_id")
    public void set_id(String _id) {
        this._id = _id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /*
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
    */
}
