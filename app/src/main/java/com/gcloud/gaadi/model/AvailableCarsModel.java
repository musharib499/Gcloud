package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Priya on 26-05-2015.
 */
public class AvailableCarsModel implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("error")
    private String error;

    @SerializedName("msg")
    private String message;

    @SerializedName("carData")
    private ArrayList<CarItemModel> stocks;

    @SerializedName("totalRecords")
    private int totalRecords;

    @SerializedName("hasNext")
    private boolean hasNext;

    @SerializedName("pageNumber")
    private boolean pageNumber;

    @SerializedName("carCount")
    private int carCount;

    public int getCarCount() {
        return carCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<CarItemModel> getStocks() {
        return stocks;
    }

    public void setStocks(ArrayList<CarItemModel> stocks) {
        this.stocks = stocks;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(boolean pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int isCarCount() {
        return carCount;
    }

    public void setCarCount(int carCount) {
        this.carCount = carCount;
    }

    @Override
    public String toString() {
        return "Available Cars Model{" +
                "status='" + status + '\'' +
                ", error='" + error + '\'' +
                ", car items=" + stocks +
                '}';
    }
}
