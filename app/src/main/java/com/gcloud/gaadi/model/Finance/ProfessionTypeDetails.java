package com.gcloud.gaadi.model.Finance;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Manish on 10/14/2015.
 */
public class ProfessionTypeDetails implements Serializable {

    @SerializedName("pro_start_date")
    private String startDate;

    @SerializedName("pro_last_year_profit")
    private String lastYearPAT;

    @SerializedName("pro_existing_emi")
    private String totalExistingEMI;

    @SerializedName("pro_type")
    private String professionType;

    @SerializedName("customer_bank_id")
    private String bankName;

    @SerializedName("coa_city")
    private String workCity;

    @SerializedName("office_setup_type")
    private String officeSetUpType;

    public String getOfficeSetUpType() {
        return officeSetUpType;
    }

    public void setOfficeSetUpType(String officeSetUpType) {
        this.officeSetUpType = officeSetUpType;
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

    public String getProfessionType() {
        return professionType;
    }

    public void setProfessionType(String professionType) {
        this.professionType = professionType;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
