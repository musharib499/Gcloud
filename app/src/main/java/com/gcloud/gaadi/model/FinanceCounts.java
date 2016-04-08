package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Lakshay on 27-05-2015.
 */
public class FinanceCounts implements Serializable {

    @SerializedName("pending")
    String pendingCount;

    @SerializedName("approved")
    String approvedCount;

    @SerializedName("rejected")
    String rejectedCount;

    @SerializedName("total_count")
    String totalCount;

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(String pendingCount) {
        this.pendingCount = pendingCount;
    }

    public String getApprovedCount() {
        return approvedCount;
    }

    public void setApprovedCount(String approvedCount) {
        this.approvedCount = approvedCount;
    }

    public String getRejectedCount() {
        return rejectedCount;
    }

    public void setRejectedCount(String rejectedCount) {
        this.rejectedCount = rejectedCount;
    }
}
