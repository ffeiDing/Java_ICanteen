package com.netlab.vc.coursehelper.util.jsonResults;

/**
 * Created by Vc on 2016/12/8.
 */

public class TestList {
    private Quiz[] quizzes;
    private Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Quiz[] getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(Quiz[] quizzes) {
        this.quizzes = quizzes;
    }


    public class Quiz{
        private String quiz_id;
        private String course_id;
        private String name;
        private long from;
        private long to;
        private int total;
        private int answered;
        private int correctAnswer;

        public int getAnswered() {
            return answered;
        }

        public void setAnswered(int answered) {
            this.answered = answered;
        }

        public int getCorrectAnswer() {
            return correctAnswer;
        }

        public void setCorrectAnswer(int correctAnswer) {
            this.correctAnswer = correctAnswer;
        }

        public long getFrom() {
            return from;
        }

        public void setFrom(long from) {
            this.from = from;
        }

        public long getTo() {
            return to;
        }

        public void setTo(long to) {
            this.to = to;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public String getQuiz_id() {
            return quiz_id;
        }

        public void setQuiz_id(String quiz_id) {
            this.quiz_id = quiz_id;
        }

        public String getCourse_id() {
            return course_id;
        }

        public void setCourse_id(String course_id) {
            this.course_id = course_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    public class Answer{
        private String quiz_id;
        private int total;

        public String getQuiz_id() {
            return quiz_id;
        }

        public void setQuiz_id(String quiz_id) {
            this.quiz_id = quiz_id;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
