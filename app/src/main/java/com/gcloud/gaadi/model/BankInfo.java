package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by priyarawat on 2/2/16.
 */
public class BankInfo implements Serializable {

    @SerializedName("applied_amount")
    String loanAmount;

    @SerializedName("applied_tenure")
    String appliedTenure;

    @SerializedName("applied_roi")
    String appliedRoi;

    @SerializedName("file_date")
    String fileDate;

    @SerializedName("applied_emi")
    String appliedEmi;

    @SerializedName("bank_id")
    String bankId;

    @SerializedName("status")
    String loanCaseStatus;

    @SerializedName("bank_logo")
    String bankLogo;

    @SerializedName("disbursed_amount")
    String disbursed_amount;

    @SerializedName("disbursed_tenure")
    String disbursed_tenure;

    @SerializedName("disbursed_roi")
    String disbursed_roi;

    @SerializedName("disbursed_emi")
    String disbursed_emi;

    @SerializedName("disbursed_date")
    String disbursed_date;

    @SerializedName("approved_amount")
    String approved_amount;

    @SerializedName("approved_tenure")
    String approved_tenure;

    @SerializedName("approved_roi")
    String approved_roi;

    @SerializedName("approved_emi")
    String approved_emi;

    @SerializedName("approval_date")
    String approval_date;

    @SerializedName("rejection_reason")
    String rejectionReason;

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getDisbursed_amount() {
        return disbursed_amount;
    }

    public void setDisbursed_amount(String disbursed_amount) {
        this.disbursed_amount = disbursed_amount;
    }

    public String getDisbursed_tenure() {
        return disbursed_tenure;
    }

    public void setDisbursed_tenure(String disbursed_tenure) {
        this.disbursed_tenure = disbursed_tenure;
    }

    public String getDisbursed_roi() {
        return disbursed_roi;
    }

    public void setDisbursed_roi(String disbursed_roi) {
        this.disbursed_roi = disbursed_roi;
    }

    public String getDisbursed_emi() {
        return disbursed_emi;
    }

    public void setDisbursed_emi(String disbursed_emi) {
        this.disbursed_emi = disbursed_emi;
    }

    public String getDisbursed_date() {
        return disbursed_date;
    }

    public void setDisbursed_date(String disbursed_date) {
        this.disbursed_date = disbursed_date;
    }

    public String getApproved_amount() {
        return approved_amount;
    }

    public void setApproved_amount(String approved_amount) {
        this.approved_amount = approved_amount;
    }

    public String getApproved_tenure() {
        return approved_tenure;
    }

    public void setApproved_tenure(String approved_tenure) {
        this.approved_tenure = approved_tenure;
    }

    public String getApproved_roi() {
        return approved_roi;
    }

    public void setApproved_roi(String approved_roi) {
        this.approved_roi = approved_roi;
    }

    public String getApproved_emi() {
        return approved_emi;
    }

    public void setApproved_emi(String approved_emi) {
        this.approved_emi = approved_emi;
    }

    public String getApproval_date() {
        return approval_date;
    }

    public void setApproval_date(String approval_date) {
        this.approval_date = approval_date;
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getAppliedTenure() {
        return appliedTenure;
    }

    public void setAppliedTenure(String appliedTenure) {
        this.appliedTenure = appliedTenure;
    }

    public String getAppliedRoi() {
        return appliedRoi;
    }

    public void setAppliedRoi(String appliedRoi) {
        this.appliedRoi = appliedRoi;
    }

    public String getFileDate() {
        return fileDate;
    }

    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }

    public String getAppliedEmi() {
        return appliedEmi;
    }

    public void setAppliedEmi(String appliedEmi) {
        this.appliedEmi = appliedEmi;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getLoanCaseStatus() {
        return loanCaseStatus;
    }

    public void setLoanCaseStatus(String loanCaseStatus) {
        this.loanCaseStatus = loanCaseStatus;
    }

    public String getBankLogo() {
        return bankLogo;
    }

    public void setBankLogo(String bankLogo) {
        this.bankLogo = bankLogo;
    }
}
