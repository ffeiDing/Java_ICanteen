package com.netlab.vc.coursehelper.util.jsonResults;



public class AnnouncementResult{
    private Boolean success;
    private Announcement[] notifications;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Announcement[] getAnnouncements() {
        return notifications;
    }

    public void setAnnouncements(Announcement[] notifications) {
        this.notifications = notifications;
    }

}