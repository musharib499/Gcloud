package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Priya on 13-05-2015.
 */
public class LoanOffers implements Serializable {

    @SerializedName("bank_id")
    private String bank_id;

    @SerializedName("bank_name")
    private String bank_name;

    @SerializedName("bank_logo")
    private String bank_logo;

    @SerializedName("max_eligibility")
    private String max_eligibility;

    @SerializedName("fixed_roi")
    private String fixed_roi;

    @SerializedName("month_36_emi")
    private String month_36_emi;

    @SerializedName("fiveYearEMI")
    private String fiveYearEMI;

    @SerializedName("month_48_emi")
    private String month_48_emi;


    @SerializedName("month_60_emi")
    private String month_60_emi;

    @SerializedName("percent_upto_36_month")
    private String percent36Month;

    @SerializedName("percent_37_48_month")
    private String percent_48_month;

    @SerializedName("percent_49_60_month")
    private String percent60Month;

    @SerializedName("processing_fee")
    private String processing_fee;

    public String getPercent36Month() {
        return percent36Month;
    }

    public void setPercent36Month(String percent36Month) {
        this.percent36Month = percent36Month;
    }

    public String getPercent_48_month() {
        return percent_48_month;
    }

    public void setPercent_48_month(String percent_48_month) {
        this.percent_48_month = percent_48_month;
    }

    public String getPercent60Month() {
        return percent60Month;
    }

    public void setPercent60Month(String percent60Month) {
        this.percent60Month = percent60Month;
    }

    public String getBank_id() {
        return bank_id;
    }

    public void setBank_id(String bank_id) {
        this.bank_id = bank_id;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBank_logo() {
        return bank_logo;
    }

    public void setBank_logo(String bank_logo) {
        this.bank_logo = bank_logo;
    }

    public String getMax_eligibility() {
        return max_eligibility;
    }

    public void setMax_eligibility(String max_eligibility) {
        this.max_eligibility = max_eligibility;
    }

    public String getFixed_roi() {
        return fixed_roi;
    }

    public void setFixed_roi(String fixed_roi) {
        this.fixed_roi = fixed_roi;
    }

    public String getMonth_36_emi() {
        return month_36_emi;
    }

    public void setMonth_36_emi(String month_36_emi) {
        this.month_36_emi = month_36_emi;
    }

    public String getFiveYearEMI() {
        return fiveYearEMI;
    }

    public void setFiveYearEMI(String fiveYearEMI) {
        this.fiveYearEMI = fiveYearEMI;
    }

    public String getMonth_48_emi() {
        return month_48_emi;
    }

    public void setMonth_48_emi(String month_48_emi) {
        this.month_48_emi = month_48_emi;
    }

    public String getMonth_60_emi() {
        return month_60_emi;
    }

    public void setMonth_60_emi(String month_60_emi) {
        this.month_60_emi = month_60_emi;
    }

    public String getProcessing_fee() {
        return processing_fee;
    }

    public void setProcessing_fee(String processing_fee) {
        this.processing_fee = processing_fee;
    }
}
