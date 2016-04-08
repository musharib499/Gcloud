package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CallTrackModel implements Serializable {

    private String callSource;

    @SerializedName("status")
    private String status;

    @SerializedName("msg")
    private String message;

    @SerializedName("error")
    private String errorMessage;

    @SerializedName("customers")
    private ArrayList<CustomerModel> customers;

    public String getCallSource() {
        return callSource;
    }

    public void setCallSource(String callSource) {
        this.callSource = callSource;
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

    public ArrayList<CustomerModel> getCustomers() {
        return customers;
    }

    public void setCustomers(ArrayList<CustomerModel> customers) {
        this.customers = customers;
    }

    @Override
    public String toString() {
        return "CallTrackModel{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", customers=" + customers +
                '}';
    }
}
