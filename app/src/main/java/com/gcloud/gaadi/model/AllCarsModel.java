package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class AllCarsModel implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("msg")
    private String message;

    @SerializedName("error")
    private String errorMessage;

    @SerializedName("allcars")
    private ArrayList<AllCars> allcars;

    @SerializedName("pageNumber")
    private String pageNumber;

    @SerializedName("pageSize")
    private String pageSize;

    @SerializedName("totalRecords")
    private String totalRecords;


    @SerializedName("hasNext")
    private Boolean hasNext;


    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(String totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public ArrayList<AllCars> getAllcars() {
        return allcars;
    }

    public void setAllcars(ArrayList<AllCars> allcars) {
        this.allcars = allcars;
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
