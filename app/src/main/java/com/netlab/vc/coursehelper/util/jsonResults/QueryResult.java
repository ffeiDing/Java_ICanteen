package com.netlab.vc.coursehelper.util.jsonResults;

/**
 * Created by dingfeifei on 17/1/5.
 */

public class QueryResult {
    private String status;
    private Boolean success;
    private String group_id;

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
