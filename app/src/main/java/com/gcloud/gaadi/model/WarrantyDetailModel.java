package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WarrantyDetailModel implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("msg")
    private String msg;

    @SerializedName("error")
    private String error;

    @SerializedName("warrantyData")
    private WarrantyDetailsData warrantyData;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public WarrantyDetailsData getWarrantyData() {
        return warrantyData;
    }

    public void setWarrantyData(WarrantyDetailsData warrantyData) {
        this.warrantyData = warrantyData;
    }


}
