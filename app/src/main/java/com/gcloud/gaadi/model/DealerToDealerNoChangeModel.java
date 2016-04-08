package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Arun Singh Chauhan on 9/7/15.
 */
public class DealerToDealerNoChangeModel implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("msg")
    private String message;

    @SerializedName("error")
    private String errorMessage;

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


    @Override
    public String toString() {
        return "ChangePasswordModel{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", errorMessage='" + errorMessage + " }";
    }
}
