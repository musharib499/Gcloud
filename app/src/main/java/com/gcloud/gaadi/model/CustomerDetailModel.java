package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Priya on 27-05-2015.
 */
public class CustomerDetailModel implements Serializable {
    @SerializedName("status")
    private String status;

    @SerializedName("msg")
    private String message;

    @SerializedName("error")
    private String errorMessage;

    @SerializedName("customer_id")
    private String customer_id;

    @SerializedName("finance_lead_id")
    private String finance_lead_id;

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

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getFinance_lead_id() {
        return finance_lead_id;
    }

    public void setFinance_lead_id(String finance_lead_id) {
        this.finance_lead_id = finance_lead_id;
    }

    @Override
    public String toString() {
        return "CustomerDetailModel{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", customer_id='" + customer_id + '\'' +
                ", finance_lead_id='" + finance_lead_id + '\'' +
                '}';
    }
}
