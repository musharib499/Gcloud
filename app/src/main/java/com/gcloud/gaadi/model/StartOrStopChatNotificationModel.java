package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Priya on 19-01-2015.
 */
public class StartOrStopChatNotificationModel implements Serializable {

    @SerializedName("error")
    private String error;

    @SerializedName("msg")
    private String message;

    @SerializedName("status")
    private String status;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return " StartOrStopChatNotificationModel {" +
                "error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status +
                '}';
    }
}
