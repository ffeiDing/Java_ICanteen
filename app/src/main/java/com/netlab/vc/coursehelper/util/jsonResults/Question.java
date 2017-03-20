package com.netlab.vc.coursehelper.util.jsonResults;

/**
 * Created by Vc on 2016/12/9.
 */

public class Question {
    private String content;
    private String type;
    private String _id;
    private String[] correctAnswer;
    private Option[] options;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String[] correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Option[] getOptions() {
        return options;
    }

    public void setOptions(Option[] options) {
        this.options = options;
    }
}