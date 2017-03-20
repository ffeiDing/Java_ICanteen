package com.netlab.vc.coursehelper.util.jsonResults;

/**
 * Created by dingfeifei on 16/12/23.
 */

public class ForumResult{
    private Boolean success;
    private Forum[] postings;
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Forum[] getForums() {
        return postings;
    }

    public void setForums(Forum[] postings) {
        this.postings = postings;
    }
}