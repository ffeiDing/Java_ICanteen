package com.netlab.vc.coursehelper.util.jsonResults;

/**
 * Created by Vc on 2016/12/18.
 */

public class LessonInfo {
    public Lesson[] lessons;
    public Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Lesson[] getLessons() {
        return lessons;
    }

    public void setLessons(Lesson[] lessons) {
        this.lessons = lessons;
    }

}
