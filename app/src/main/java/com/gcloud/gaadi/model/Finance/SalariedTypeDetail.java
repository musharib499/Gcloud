package com.gcloud.gaadi.model.Finance;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Manish on 10/14/2015.
 */
public class SalariedTypeDetail implements Serializable {

    @SerializedName("employer_name")
    private String company;

    @SerializedName("joining_date")
    private String joinDate;

    @SerializedName("year_of_experience")
    private String workExp;

    @SerializedName("gross_monthly_income")
    private String grossIncome;

    @SerializedName("existing_emi")
    private String existingEMI;

    @SerializedName("customer_bank_id")
    private String bankName;

    @SerializedName("coa_city")
    private String workCity;

    @SerializedName("sal_credit_type")
    private String salaryType;

    @SerializedName("curr_job_status")
    private String currentJobStatus;

    public String getCurrentJobStatus() {
        return currentJobStatus;
    }

    public void setCurrentJobStatus(String currentJobStatus) {
        this.currentJobStatus = currentJobStatus;
    }

    public String getSalaryType() {
        return salaryType;
    }

    public void setSalaryType(String salaryType) {
        this.salaryType = salaryType;
    }

    public String getWorkCity() {
        return workCity;
    }

    public void setWorkCity(String workCity) {
        this.workCity = workCity;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public String getWorkExp() {
        return workExp;
    }

    public void setWorkExp(String workExp) {
        this.workExp = workExp;
    }

    public String getGrossIncome() {
        return grossIncome;
    }

    public void setGrossIncome(String grossIncome) {
        this.grossIncome = grossIncome;
    }

    public String getExistingEMI() {
        return existingEMI;
    }

    public void setExistingEMI(String existingEMI) {
        this.existingEMI = existingEMI;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
