package com.netlab.vc.coursehelper.util.jsonResults;

/**
 * Created by dingfeifei on 17/1/6.
 */

public class NoGroupResult {
    private Member[] members;
    private Boolean success;

    public Member[] getMembers() {
        return members;
    }

    public void setMembers(Member[] members) {
        this.members = members;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
