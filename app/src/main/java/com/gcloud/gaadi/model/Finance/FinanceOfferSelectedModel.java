package com.gcloud.gaadi.model.Finance;

import com.google.gson.annotations.SerializedName;

/**
 * Created by shivani on 9/2/16.
 */
public class FinanceOfferSelectedModel {

    @SerializedName("applied_amount")
    private String applied_amount;
    private String applied_tenure;
    private String applied_roi;
    private String finance_lead_id;
    private String applied_emi;
    private String bank_id;

    public String getBank_id() {
        return bank_id;
    }

    public void setBank_id(String bank_id) {
        this.bank_id = bank_id;
    }

    public FinanceOfferSelectedModel() {
    }

    public FinanceOfferSelectedModel(String applied_loan_amount, String applied_tenure, String applied_roi,String applied_emi) {
        this.applied_amount = applied_loan_amount;
        this.applied_tenure = applied_tenure;
        this.applied_roi = applied_roi;
        this.applied_emi = applied_emi;
    }

    public String getApplied_loan_amount() {
        return applied_amount;
    }

    public void setApplied_loan_amount(String applied_loan_amount) {
        this.applied_amount = applied_loan_amount;
    }

    public String getApplied_tenure() {
        return applied_tenure;
    }

    public String getApplied_emi() {
        return applied_emi;
    }

    public void setApplied_emi(String applied_emi) {
        this.applied_emi = applied_emi;
    }

    public void setApplied_tenure(String applied_tenure) {
        this.applied_tenure = applied_tenure;
    }

    public String getApplied_roi() {
        return applied_roi;
    }

    public void setApplied_roi(String applied_roi) {
        this.applied_roi = applied_roi;
    }

    public String getFinance_lead_id() {
        return finance_lead_id;
    }

    public void setFinance_lead_id(String finance_lead_id) {
        this.finance_lead_id = finance_lead_id;
    }
}