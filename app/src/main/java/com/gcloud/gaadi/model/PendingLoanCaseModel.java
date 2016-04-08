package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ankitgarg on 29/05/15.
 */
public class PendingLoanCaseModel extends LoanCaseModel {

    @SerializedName("data")
    private ArrayList<LoanApplication> pendingLoanCases;

    public ArrayList<LoanApplication> getPendingLoanCases() {
        return pendingLoanCases;
    }

    public void setPendingLoanCases(ArrayList<LoanApplication> pendingLoanCases) {
        this.pendingLoanCases = pendingLoanCases;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PendingLoanCaseModel{");
        sb.append("pendingLoanCases=").append(pendingLoanCases);
        sb.append('}');
        return sb.toString();
    }
}
