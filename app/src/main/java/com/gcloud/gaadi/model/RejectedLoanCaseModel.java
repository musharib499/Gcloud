package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ankitgarg on 29/05/15.
 */
public class RejectedLoanCaseModel extends LoanCaseModel implements Serializable {

    @SerializedName("data")
    private ArrayList<LoanApplication> rejectedLoanCases;

    public ArrayList<LoanApplication> getRejectedLoanCases() {
        return rejectedLoanCases;
    }

    public void setRejectedLoanCases(ArrayList<LoanApplication> rejectedLoanCases) {
        this.rejectedLoanCases = rejectedLoanCases;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RejectedLoanCaseModel{");
        sb.append("rejectedLoanCases=").append(rejectedLoanCases);
        sb.append('}');
        return sb.toString();
    }
}
