package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Comments implements Serializable {

    @SerializedName("comment")
    private String cmnts;

    @SerializedName("date")
    private String date;

    @SerializedName("added")
    private String commentDate;

    public Comments() {
    }

    public Comments(String cmnts, String date) {
        this.cmnts = cmnts;
        this.commentDate = date;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getCmnts() {
        return cmnts;
    }

    public void setCmnts(String cmnts) {
        this.cmnts = cmnts;
    }

    public String getDate() {

        return date;
        /*DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        try {
            DateTime datea = dtf.parseDateTime(date);

            String formattedDate = datea.getDayOfMonth() + " "
                    + datea.monthOfYear().getAsShortText(Locale.ENGLISH) + ", "
                    + datea.year().getAsShortText(Locale.ENGLISH) + " ~ "
                    + datea.hourOfDay().getAsShortText(Locale.ENGLISH) + ":"
                    + datea.minuteOfHour().getAsShortText(Locale.ENGLISH);

            return formattedDate;
        } catch (NullPointerException e) {
            GCLog.e(e.getMessage());
            return null;
        }*/

    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Comments{");
        sb.append("cmnts='").append(cmnts).append('\'');
        sb.append(", date='").append(date).append('\'');
        sb.append(", commentDate='").append(commentDate).append('\'');
        sb.append('}');
        return sb.toString();
    }
}