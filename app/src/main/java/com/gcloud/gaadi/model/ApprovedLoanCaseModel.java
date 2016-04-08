package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ApprovedLoanCaseModel extends LoanCaseModel implements Serializable {

    @SerializedName("data")
    private ArrayList<LoanApplication> approvedLoanCases;

    public ArrayList<LoanApplication> getApprovedLoanCases() {
        return approvedLoanCases;
    }

    public void setApprovedLoanCases(ArrayList<LoanApplication> approvedLoanCases) {
        this.approvedLoanCases = approvedLoanCases;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ApprovedLoanCaseModel{");
        sb.append("approvedLoanCases=").append(approvedLoanCases);
        sb.append('}');
        return sb.toString() + super.toString();
    }
}
