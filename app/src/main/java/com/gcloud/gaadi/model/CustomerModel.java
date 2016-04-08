package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.Locale;

public class CustomerModel implements Serializable {

    private String callSource;
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("name")
    private String name;
    @SerializedName("status")
    private String status;
    @SerializedName("date")
    private String date;

    public CustomerModel(String mobile, String name, String status, String date, String callSource) {
        this.name = name;
        this.mobile = mobile;
        this.status = status;
        this.date = date;
        this.callSource = callSource;
    }

    public String getCallSource() {
        return callSource;
    }

    public void setCallSource(String callSource) {
        this.callSource = callSource;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {


        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime datea = dtf.parseDateTime(date);

        String formattedDate = datea.getDayOfMonth() + " "
                + datea.monthOfYear().getAsShortText(Locale.ENGLISH) + ", "
                + datea.year().getAsShortText(Locale.ENGLISH) + " ~ "
                + datea.hourOfDay().getAsShortText(Locale.ENGLISH) + ":"
                + datea.minuteOfHour().getAsShortText(Locale.ENGLISH);

        return formattedDate;
    }

    public String getTrackerDate() {


        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime datea = dtf.parseDateTime(date);

        String formattedDate = datea.getDayOfMonth() + " "
                + datea.monthOfYear().getAsShortText(Locale.ENGLISH) + " "
                + datea.year().getAsShortText(Locale.ENGLISH);

        return formattedDate;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "CustomerModel{" +
                "mobile='" + mobile + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
