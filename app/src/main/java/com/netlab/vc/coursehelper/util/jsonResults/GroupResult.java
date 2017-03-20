package com.netlab.vc.coursehelper.util.jsonResults;

/**
 * Created by dingfeifei on 17/1/5.
 */

public class GroupResult {
    private Group[] groups;
    private Boolean success;

    public Group[] getGroups() {
        return groups;
    }

    public void setGroups(Group[] groups) {
        this.groups = groups;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
