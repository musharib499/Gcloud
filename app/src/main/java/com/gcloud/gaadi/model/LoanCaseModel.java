package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ankitgarg on 29/05/15.
 */
public class LoanCaseModel extends GeneralResponse {

    @SerializedName("hasNext")
    private boolean hasNext;

    @SerializedName("pageNumber")
    private int pageNumber;

    @SerializedName("carCount")
    private int totalRecords;

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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("LoanCaseModel{");
        sb.append("hasNext=").append(hasNext);
        sb.append(", pageNumber=").append(pageNumber);
        sb.append(", totalRecords=").append(totalRecords);
        sb.append('}');
        return sb.toString() + super.toString();
    }
}
