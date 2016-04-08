package com.gcloud.gaadi.model.Finance;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Manish on 10/14/2015.
 */
public class BusinessTypeDetails implements Serializable {

    @SerializedName("bus_start_date")
    private String startDate;

    @SerializedName("bus_last_year_profit")
    private String lastYearPAT;

    @SerializedName("bus_existing_emi")
    private String totalExistingEMI;

    @SerializedName("bus_type")
    private String applicantType;

    @SerializedName("customer_bank_id")
    private String bankName;

    @SerializedName("coa_city")
    private String workCity;

    @SerializedName("office_setup_type")
    private String officeSetUpType;
    @SerializedName("bus_industry")
    private String industryType;

    public String getOfficeSetUpType() {
        return officeSetUpType;
    }

    public void setOfficeSetUpType(String officeSetUpType) {
        this.officeSetUpType = officeSetUpType;
    }

    public String getIndustryType() {
        return industryType;
    }

    public void setIndustryType(String industryType) {
        this.industryType = industryType;
    }

    public String getWorkCity() {
        return workCity;
    }

    public void setWorkCity(String workCity) {
        this.workCity = workCity;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getLastYearPAT() {
        return lastYearPAT;
    }

    public void setLastYearPAT(String lastYearPAT) {
        this.lastYearPAT = lastYearPAT;
    }

    public String getTotalExistingEMI() {
        return totalExistingEMI;
    }

    public void setTotalExistingEMI(String totalExistingEMI) {
        this.totalExistingEMI = totalExistingEMI;
    }

    public String getApplicantType() {
        return applicantType;
    }

    public void setApplicantType(String applicantType) {
        this.applicantType = applicantType;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
