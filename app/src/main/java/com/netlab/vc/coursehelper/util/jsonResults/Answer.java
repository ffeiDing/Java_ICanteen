package com.netlab.vc.coursehelper.util.jsonResults;

/**
 * Created by Vc on 2016/12/9.
 */

public class Answer {
    private String question_id;
    private String[] originAnswer;
    int score;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String[] getOriginAnswer() {
        return originAnswer;
    }

    public void setOriginAnswer(String[] originAnswer) {
        this.originAnswer = originAnswer;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }


}