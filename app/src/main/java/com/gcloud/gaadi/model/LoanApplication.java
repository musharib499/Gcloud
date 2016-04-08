package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Lakshay on 29-05-2015.
 */
public class LoanApplication implements Serializable {

    @SerializedName("make")
    String makeValue;

    @SerializedName("make_id")
    int make;

    @SerializedName("bank_id")
    String bank_id;

    @SerializedName("bank_logo")
    String bank_logo;

    @SerializedName("model")
    String model;

    @SerializedName("myear")
    String year;

    @SerializedName("km")
    String kmDriven;

    @SerializedName("colour")
    String color;

    @SerializedName("applied_amount")
    String stockPrice;

    @SerializedName("finance_lead_id")
    String applicationId;

    @SerializedName("imageIcon")
    String imageIcon;

    @SerializedName("regno")
    String regNo;

    @SerializedName("usedCarID")
    String carId;


    @SerializedName("customer_name")
    String customarName;

    @SerializedName("pending_from")
    String pendingStatus;

    @SerializedName("loan_approval_status")
    String loanApprovalStatus;

    @SerializedName("approved_loan_amount")
    String loanAmount;

    @SerializedName("approved_tenure")
    String approvedTenure;

    @SerializedName("approved_roi")
    String approvedRoi;

    @SerializedName("customer_id")
    String customarId;

    @SerializedName("approved_emi")
    String approvedEmi;

    @SerializedName("rejection_reason")
    String rejectionReason;

    @SerializedName("bank_info")
    ArrayList<BankInfo> bankInfoList;

    boolean isExpanded;

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setIsExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    public ArrayList<BankInfo> getBankInfoList() {
        return bankInfoList;
    }

    public void setBankInfoList(ArrayList<BankInfo> bankInfoList) {
        this.bankInfoList = bankInfoList;
    }

    public String getLoanApprovalStatus() {
        return loanApprovalStatus;
    }

    public void setLoanApprovalStatus(String loanApprovalStatus) {
        this.loanApprovalStatus = loanApprovalStatus;
    }

    public String getBank_id() {
        return bank_id;
    }

    public void setBank_id(String bank_id) {
        this.bank_id = bank_id;
    }

    public String getMakeValue() {
        return makeValue;
    }

    public void setMakeValue(String makeValue) {
        this.makeValue = makeValue;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getApprovedEmi() {
        return approvedEmi;
    }

    public void setApprovedEmi(String approvedEmi) {
        this.approvedEmi = approvedEmi;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getCustomarId() {
        return customarId;
    }

    public void setCustomarId(String customarId) {
        this.customarId = customarId;
    }

    public String getApprovedRoi() {
        return approvedRoi;
    }

    public void setApprovedRoi(String approvedRoi) {
        this.approvedRoi = approvedRoi;
    }

    public String getApprovedTenure() {
        return approvedTenure;
    }

    public void setApprovedTenure(String approvedTenure) {
        this.approvedTenure = approvedTenure;
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getPendingStatus() {
        return pendingStatus;
    }

    public void setPendingStatus(String pendingStatus) {
        this.pendingStatus = pendingStatus;
    }

    public String getCustomarName() {
        return customarName;
    }

    public void setCustomarName(String customarName) {
        this.customarName = customarName;
    }

    public String getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(String imageIcon) {
        this.imageIcon = imageIcon;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public int getMake() {
        return make;
    }

    public void setMake(int make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getKmDriven() {
        return kmDriven;
    }

    public void setKmDriven(String kmDriven) {
        this.kmDriven = kmDriven;
    }

    public String getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(String stockPrice) {
        this.stockPrice = stockPrice;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBank_logo() {
        return bank_logo;
    }

    public void setBank_logo(String bank_logo) {
        this.bank_logo = bank_logo;
    }

    @Override
    public String toString() {
        return "LoanApplication{" +
                "makeValue='" + makeValue + '\'' +
                ", make=" + make +
                ", bank_id='" + bank_id + '\'' +
                ", bank_logo='" + bank_logo + '\'' +
                ", model='" + model + '\'' +
                ", year='" + year + '\'' +
                ", kmDriven='" + kmDriven + '\'' +
                ", color='" + color + '\'' +
                ", stockPrice='" + stockPrice + '\'' +
                ", applicationId='" + applicationId + '\'' +
                ", imageIcon='" + imageIcon + '\'' +
                ", regNo='" + regNo + '\'' +
                ", carId='" + carId + '\'' +
                ", customarName='" + customarName + '\'' +
                ", pendingStatus='" + pendingStatus + '\'' +
                ", loanApprovalStatus='" + loanApprovalStatus + '\'' +
                ", loanAmount='" + loanAmount + '\'' +
                ", approvedTenure='" + approvedTenure + '\'' +
                ", approvedRoi='" + approvedRoi + '\'' +
                ", customarId='" + customarId + '\'' +
                ", approvedEmi='" + approvedEmi + '\'' +
                ", rejectionReason='" + rejectionReason + '\'' +
                '}';
    }
}
