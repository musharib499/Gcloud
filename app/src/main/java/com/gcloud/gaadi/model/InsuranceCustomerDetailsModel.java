package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ankitgarg on 29/05/15.
 */
public class InsuranceCustomerDetailsModel extends GeneralResponse {

    @SerializedName("car_id")
    private String carId;

    @SerializedName("process_id")
    private String processId;

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }
}
