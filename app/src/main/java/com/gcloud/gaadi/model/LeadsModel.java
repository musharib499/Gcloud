package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ankit on 6/1/15.
 */
public class LeadsModel implements Serializable {

    @SerializedName("error")
    private String error;

    @SerializedName("msg")
    private String message;

    @SerializedName("status")
    private String status;

    @SerializedName("filters")
    private StocksModel.Filters filters;

    @SerializedName("leads")
    private ArrayList<LeadDetailModel> leads;

    @SerializedName("totalRecords")
    private int totalRecords;

    @SerializedName("pageSize")
    private int pageSize;

    @SerializedName("pageNumber")
    private int pageNumber;

    @SerializedName("hasNext")
    private Boolean nextPossible;

    public StocksModel.Filters getFilters() {
        return filters;
    }

    public void setFilters(StocksModel.Filters filters) {
        this.filters = filters;
    }

    public Boolean getNextPossible() {
        return nextPossible;
    }

    public void setNextPossible(Boolean nextPossible) {
        this.nextPossible = nextPossible;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<LeadDetailModel> getLeads() {
        return leads;
    }

    public void setLeads(ArrayList<LeadDetailModel> leads) {
        this.leads = leads;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "LeadsModel{" +
                "error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                ", leads=" + leads +
                ", totalRecords=" + totalRecords +
                ", pageSize=" + pageSize +
                '}';
    }
}
