package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ankitgarg on 20/05/15.
 */
public class ValidCarRSAModel extends GeneralResponse {

    @SerializedName("hasNext")
    private boolean hasNext;

    @SerializedName("pageNumber")
    private int pageNumber;

    @SerializedName("carCount")
    private int totalRecords;

    @SerializedName("carData")
    private ArrayList<InspectedCarData> inspectedCars;

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public ArrayList<InspectedCarData> getInspectedCars() {
        return inspectedCars;
    }

    public void setInspectedCars(ArrayList<InspectedCarData> inspectedCars) {
        this.inspectedCars = inspectedCars;
    }

    @Override
    public String toString() {
        super.toString();
        final StringBuffer sb = new StringBuffer("ValidCarRSAModel{");
        sb.append("hasNext=").append(hasNext);
        sb.append(", pageNumber=").append(pageNumber);
        sb.append(", totalRecords=").append(totalRecords);
        sb.append(", inspectedCars=").append(inspectedCars);
        sb.append('}');
        return sb.toString();
    }
}
