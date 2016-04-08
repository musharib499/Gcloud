package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ankitgarg on 28/05/15.
 */
public class InspectedCarsInsuranceModel extends GeneralResponse {

    @SerializedName("total_records")
    private int totalRecords;

    @SerializedName("hasNext")
    private int hasNext;

    @SerializedName("data")
    private ArrayList<InsuranceInspectedCarData> inspectedCars;

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

    public ArrayList<InsuranceInspectedCarData> getInspectedCars() {
        return inspectedCars;
    }

    public void setInspectedCars(ArrayList<InsuranceInspectedCarData> inspectedCars) {
        this.inspectedCars = inspectedCars;
    }

    public int getHasNext() {
        return hasNext;
    }

    public void setHasNext(int hasNext) {
        this.hasNext = hasNext;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("InspectedCarsInsuranceModel{");
        sb.append("totalRecords=").append(totalRecords);
        sb.append(", inspectedCars=").append(inspectedCars);
        sb.append('}');
        return sb.toString();
    }
}
