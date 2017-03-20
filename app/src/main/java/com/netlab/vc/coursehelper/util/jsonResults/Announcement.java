package com.netlab.vc.coursehelper.util.jsonResults;

import java.io.Serializable;

/**
 * Created by dingfeifei on 16/12/23.
 */

public class Announcement implements Serializable {
    private String title;
    private String content;
    private String poster_name;
    private long create_at;
    private Boolean success;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPoster_name() {
        return poster_name;
    }

    public void setPoster_name(String poster_name) {
        this.poster_name = poster_name;
    }

    public long getCreate_at() {
        return create_at;
    }

    public void setCreate_at(long create_at) {
        this.create_at = create_at;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
