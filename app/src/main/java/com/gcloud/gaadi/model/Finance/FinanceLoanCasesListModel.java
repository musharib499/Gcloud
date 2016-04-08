package com.gcloud.gaadi.model.Finance;

import com.gcloud.gaadi.model.LoanApplication;
import com.gcloud.gaadi.model.LoanCaseModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by priyarawat on 2/2/16.
 */
public class FinanceLoanCasesListModel extends LoanCaseModel {

    @SerializedName("data")
    private ArrayList<LoanApplication> loanCasesList;


    @Override
    public String toString() {
        return "FinanceLoanCasesListModel{" +
                "loanCasesList=" + loanCasesList +
                '}';
    }

    public ArrayList<LoanApplication> getLoanCasesList() {
        return loanCasesList;
    }

    public void setLoanCasesList(ArrayList<LoanApplication> loanCasesList) {
        this.loanCasesList = loanCasesList;
    }
}