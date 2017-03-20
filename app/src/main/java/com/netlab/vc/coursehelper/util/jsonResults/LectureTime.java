package com.netlab.vc.coursehelper.util.jsonResults;

/**
 * Created by Vc on 2016/11/24.
 */

import java.io.Serializable;

class LectureTime implements Serializable {
    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    private int startTime;
    private int endTime;
    private int weekday;
    private static final long serialVersionUID = -105L;
}
