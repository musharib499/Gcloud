package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Lakshay on 29-01-2015.
 */
public class GeneralResponse implements Serializable {

    @SerializedName("error")
    private String Error;

    @SerializedName("msg")
    private String message;

    @SerializedName("status")
    private String status;

    public String getError() {
        return Error;
    }

    public void setError(String error) {
        Error = error;
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
        return "GeneralResponse{" +
                "Error='" + Error + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
