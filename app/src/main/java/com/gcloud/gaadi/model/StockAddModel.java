package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ankit on 13/1/15.
 */
public class StockAddModel implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("msg")
    private String message;

    @SerializedName("error")
    private String errorMessage;

    @SerializedName("car_id")
    private String carId;

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

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    @Override
    public String toString() {
        return "StockAddModel{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", carId='" + carId + '\'' +
                '}';
    }
}
