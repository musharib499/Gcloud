package com.gcloud.gaadi.model;

import android.util.SparseArray;

import java.io.Serializable;

/**
 * Created by ankit on 24/11/14.
 */
public class FollowupDate implements Serializable {

    SparseArray<String> monthNameMapping = new SparseArray<String>() {
        {
            put(0, "Jan");
            put(1, "Feb");
            put(2, "Mar");
            put(3, "Apr");
            put(4, "May");
            put(5, "Jun");
            put(6, "Jul");
            put(7, "Aug");
            put(8, "Sep");
            put(9, "Oct");
            put(10, "Nov");
            put(11, "Dec");
        }
    };
    private int year;
    private int month;
    private int day;


    public FollowupDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getMonthName() {
        return monthNameMapping.get(this.month);
    }

}
