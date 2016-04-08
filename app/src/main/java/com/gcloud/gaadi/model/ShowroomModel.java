package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ankit on 8/1/15.
 */
public class ShowroomModel implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("msg")
    private String message;

    @SerializedName("error")
    private String errorMessage;

    @SerializedName("showroomList")
    private ArrayList<ShowroomData> showroomList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ArrayList<ShowroomData> getShowroomList() {
        return showroomList;
    }

    public void setShowroomList(ArrayList<ShowroomData> showroomList) {
        this.showroomList = showroomList;
    }

    @Override
    public String toString() {
        return "ShowroomModel{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", showroomList=" + showroomList +
                '}';
    }
}
