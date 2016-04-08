package com.gcloud.gaadi.model.Finance;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lakshaygirdhar on 19/10/15.
 */
public class FInanceOfferSelectedRequestModel implements Serializable {

    @SerializedName("applied_amount")
    private String applied_amount;
    private String applied_tenure;
    private String applied_roi;
    private String finance_lead_id;
    private String applied_emi;
    private String bank_id;
    private String bank_URL;

    public String getBank_URL() {
        return bank_URL;
    }

    public void setBank_URL(String bank_URL) {
        this.bank_URL = bank_URL;
    }




    public String getBank_id() {
        return bank_id;
    }

    public void setBank_id(String bank_id) {
        this.bank_id = bank_id;
    }

    public FInanceOfferSelectedRequestModel() {
    }

    public FInanceOfferSelectedRequestModel(String applied_loan_amount, String applied_tenure, String applied_roi,String applied_emi) {
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FInanceOfferSelectedRequestModel{");
        sb.append("applied_amount='").append(applied_amount).append('\'');
        sb.append(", applied_tenure='").append(applied_tenure).append('\'');
        sb.append(", applied_roi='").append(applied_roi).append('\'');
        sb.append(", finance_lead_id='").append(finance_lead_id).append('\'');
        sb.append(", applied_emi='").append(applied_emi).append('\'');
        sb.append(", bank_id='").append(bank_id).append('\'');
        sb.append(", bank_URL='").append(bank_URL).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
