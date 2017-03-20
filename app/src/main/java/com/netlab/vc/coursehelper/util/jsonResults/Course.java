package com.netlab.vc.coursehelper.util.jsonResults;

/**
 * Created by Vc on 2016/11/24.
 */

import java.io.Serializable;

public class Course implements Serializable {

    private String course_id;
    private String name;
    private String introduction;
    private long midterm;
    private long finalExam;
    private int maxStudentsNumber;
    private LectureTime[] lectureTime;
    private String[] teacherNames;
    private String term;
    private int studentCount;
    private long startDate;
    private long endDate;
    private String[] avatars;
    private boolean joined;
    private SignIn signIn;
    private static final long serialVersionUID = -103L;
    private Boolean success;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
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

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public long getMidterm() {
        return midterm;
    }

    public void setMidterm(long midterm) {
        this.midterm = midterm;
    }

    public long getFinalExam() {
        return finalExam;
    }

    public void setFinalExam(long finalExam) {
        this.finalExam = finalExam;
    }

    public int getMaxStudentsNumber() {
        return maxStudentsNumber;
    }

    public void setMaxStudentsNumber(int maxStudentsNumber) {
        this.maxStudentsNumber = maxStudentsNumber;
    }

    public LectureTime[] getLectureTime() {
        return lectureTime;
    }

    public void setLectureTime(LectureTime[] lectureTime) {
        this.lectureTime = lectureTime;
    }

    public String[] getTeacherNames() {
        return teacherNames;
    }

    public void setTeacherNames(String[] teacherNames) {
        this.teacherNames = teacherNames;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String[] getAvatars() {
        return avatars;
    }

    public void setAvatars(String[] avatars) {
        this.avatars = avatars;
    }

    public boolean isJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    public SignIn getSignIn() {
        return signIn;
    }

    public void setSignIn(SignIn signIn) {
        this.signIn = signIn;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
