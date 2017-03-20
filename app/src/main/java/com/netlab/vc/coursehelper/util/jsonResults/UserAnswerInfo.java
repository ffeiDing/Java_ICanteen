package com.netlab.vc.coursehelper.util.jsonResults;

/**
 * Created by Vc on 2016/12/18.
 */

public class UserAnswerInfo {
    String user_id;
    String course_id;
    String quiz_id;
    Answer[] status;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public Answer[] getStatus() {
        return status;
    }

    public void setStatus(Answer[] status) {
        this.status = status;
    }
}
