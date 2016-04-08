package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SentCarModel implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("msg")
    private String message;

    @SerializedName("error")
    private String errorMessage;

    @SerializedName("sentCars")
    private ArrayList<SentCars> sentcars;

    public ArrayList<SentCars> getSentcars() {
        return sentcars;
    }

    public void setSentcars(ArrayList<SentCars> sentcars) {
        this.sentcars = sentcars;
    }

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


}
