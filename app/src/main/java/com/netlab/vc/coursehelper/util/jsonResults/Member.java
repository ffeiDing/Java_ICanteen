package com.netlab.vc.coursehelper.util.jsonResults;

/**
 * Created by dingfeifei on 17/1/5.
 */

public class Member {
    private String member_id;
    private String member_name;

    public Member(String _member_id, String _member_name){
        this.member_id = _member_id;
        this.member_name = _member_name;
    }
    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }
}
