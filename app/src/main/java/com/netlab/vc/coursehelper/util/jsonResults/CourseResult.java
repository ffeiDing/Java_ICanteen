package com.netlab.vc.coursehelper.util.jsonResults;

/**
 * Created by Vc on 2016/11/25.
 */

public class CourseResult {
    private Boolean success;
    private Course [] courses;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Course[] getCourses() {
        return courses;
    }

    public void setCourses(Course[] courses) {
        this.courses = courses;
    }
}
