package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ViewCertifiedCarModel {

    @SerializedName("status")
    private String status;

    @SerializedName("msg")
    private String msg;

    @SerializedName("error")
    private String error;

    @SerializedName("carCount")
    private String carCount;

    @SerializedName("carData")
    private ArrayList<CertifiedCarData> carData;

    @SerializedName("hasNext")
    private Boolean isNextPossible;
    @SerializedName("pageNumber")
    private String pageNumber;

    private StocksModel.Filters filterKeys;

    public StocksModel.Filters getFilterKeys() {
        return filterKeys;
    }

    public void setFilterKeys(StocksModel.Filters filterKeys) {
        this.filterKeys = filterKeys;
    }

    public Boolean getIsNextPossible() {
        return isNextPossible;
    }

    public void setIsNextPossible(Boolean isNextPossible) {
        this.isNextPossible = isNextPossible;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

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

    public String getCarCount() {
        return carCount;
    }

    public void setCarCount(String carCount) {
        this.carCount = carCount;
    }

    public ArrayList<CertifiedCarData> getCarData() {
        return carData;
    }

    public void setCarData(ArrayList<CertifiedCarData> carData) {
        this.carData = carData;
    }


}
