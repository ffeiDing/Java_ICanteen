package com.netlab.vc.coursehelper.util.jsonResults;

/**
 * Created by dingfeifei on 17/1/6.
 */

public class GroupWaitingResult {
    private String group_id;
    private Member[] waiting_member;
    private Boolean success;

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public Member[] getWaiting_member() {
        return waiting_member;
    }

    public void setWaiting_member(Member[] waiting_member) {
        this.waiting_member = waiting_member;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
