package com.gcloud.gaadi.model;

import java.io.Serializable;

/**
 * Created by ankit on 24/11/14.
 */
public class FollowupTime implements Serializable {

    private int hourOfDay;
    private int minute;

    public FollowupTime(int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
