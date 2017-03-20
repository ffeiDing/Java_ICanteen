package com.netlab.vc.coursehelper.util.jsonResults;

/**
 * Created by Vc on 2016/12/11.
 */

public class QuestionResult {
    Question[] questions;
    int length;
    Boolean success;

    public Question[] getQuestions() {
        return questions;
    }

    public void setQuestions(Question[] questions) {
        this.questions = questions;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
