package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ankit on 9/1/15.
 */
public class InventoriesModel implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("msg")
    private String message;

    @SerializedName("error")
    private String errorMessage;

    @SerializedName("stockData")
    private ArrayList<InventoryModel> inventories;

    @SerializedName("totalRecords")
    private int totalRecords;
    @SerializedName("hasNext")
    private Boolean isNextPossible;
    @SerializedName("pageNumber")
    private String pageNumber;


    @SerializedName("filters")
    private StocksModel.Filters filters;

    public StocksModel.Filters getFilters() {
        return filters;
    }

    public void setFilters(StocksModel.Filters filters) {
        this.filters = filters;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
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

    public ArrayList<InventoryModel> getInventories() {
        return inventories;
    }

    public void setInventories(ArrayList<InventoryModel> inventories) {
        this.inventories = inventories;
    }

    @Override
    public String toString() {
        return "ActiveInventoriesModel{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", inventories=" + inventories +
                '}';
    }
}
