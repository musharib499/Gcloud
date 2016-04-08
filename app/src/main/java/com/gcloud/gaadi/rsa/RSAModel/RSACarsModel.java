package com.gcloud.gaadi.rsa.RSAModel;

import com.gcloud.gaadi.model.GeneralResponse;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by shivani on 8/3/16.
 */
public class RSACarsModel extends GeneralResponse {
    @SerializedName("total_records")
    String totalRecords;

    @SerializedName("stockData")
    ArrayList<RSACarDetailsModel> stockData;

    @SerializedName("pageNumber")
    String pageNumber;

    @SerializedName("pageSize")
    String pageSize;

    @SerializedName("hasNext")
    boolean hasNext;

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(String totalRecords) {
        this.totalRecords = totalRecords;
    }

    public ArrayList<RSACarDetailsModel> getStockData() {
        return stockData;
    }

    public void setStockData(ArrayList<RSACarDetailsModel> stockData) {
        this.stockData = stockData;
    }


}
