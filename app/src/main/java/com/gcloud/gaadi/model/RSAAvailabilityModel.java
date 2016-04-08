package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ankitgarg on 20/05/15.
 */
public class RSAAvailabilityModel extends GeneralResponse {

    @SerializedName("errorCode")
    private String errorCode = "";

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        String prepend = super.toString();
        final StringBuffer sb = new StringBuffer("RSAAvailabilityModel{");
        sb.append(prepend).append("\n");
        sb.append("errorCode='").append(errorCode).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
