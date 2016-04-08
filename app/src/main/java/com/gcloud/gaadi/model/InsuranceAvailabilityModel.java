package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ankitgarg on 27/05/15.
 */
public class InsuranceAvailabilityModel extends GeneralResponse {

    @SerializedName("agentId")
    private String agentId;

    @SerializedName("insurance_availability")
    private String insuranceAvailability;

    @SerializedName("is_finance")
    private String financeAvailability;

    @SerializedName("is_rsa")
    private int isRsa;

    @SerializedName("server_time")
    private String serverTime;

    @SerializedName("cities")
    private ArrayList<String> cities;

    @SerializedName("finance_company")
    private ArrayList<FinanceCompany> financeCompany;

    @SerializedName("prev_policy_insurer")
    private ArrayList<PreviousPolicyInsurer> prevPolicyInsurer;

    @SerializedName("bank_list")
    private ArrayList<String> bankList;

    @SerializedName("is_cash")
    private int isCash;

    @SerializedName("is_lms")
    private int isLMS;


    @SerializedName("is_seller")
    private int isSeller;

    @SerializedName("knowlarity")
    private ArrayList<KnowlarityNumberModel> knowlarityNumbers;

    public int getIsSeller() {
        return isSeller;
    }

    public void setIsSeller(int isSeller) {
        this.isSeller = isSeller;
    }

    public ArrayList<String> getBankList() {
        return bankList;
    }

    public void setBankList(ArrayList<String> bankList) {
        this.bankList = bankList;
    }

    public ArrayList<PreviousPolicyInsurer> getPrevPolicyInsurer() {
        return prevPolicyInsurer;
    }

    public void setPrevPolicyInsurer(ArrayList<PreviousPolicyInsurer> prevPolicyInsurer) {
        this.prevPolicyInsurer = prevPolicyInsurer;
    }

    public ArrayList<FinanceCompany> getFinanceCompany() {
        return financeCompany;
    }

    public void setFinanceCompany(ArrayList<FinanceCompany> financeCompany) {
        this.financeCompany = financeCompany;
    }

    public String getFinanceAvailability() {
        return financeAvailability;
    }

    public void setFinanceAvailability(String financeAvailability) {
        this.financeAvailability = financeAvailability;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getInsuranceAvailability() {
        return insuranceAvailability;
    }

    public void setInsuranceAvailability(String insuranceAvailability) {
        this.insuranceAvailability = insuranceAvailability;
    }

    public ArrayList<String> getCities() {
        return cities;
    }

    public void setCities(ArrayList<String> cities) {
        this.cities = cities;
    }

    public int getIsCash() {
        return isCash;
    }

    public void setIsCash(int isCash) {
        this.isCash = isCash;
    }

    public int getIsLMS() {
        return isLMS;
    }

    public void setIsLMS(int isLMS) {
        this.isLMS = isLMS;
    }

    public ArrayList<KnowlarityNumberModel> getKnowlarityNumbers() {
        return knowlarityNumbers;
    }

    public void setKnowlarityNumbers(ArrayList<KnowlarityNumberModel> knowlarityNumbers) {
        this.knowlarityNumbers = knowlarityNumbers;
    }

    public boolean isRsa() {
        return isRsa == 1;
    }

    public void setIsRsa(int isRsa) {
        this.isRsa = isRsa;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("InsuranceAvailabilityModel{");
        sb.append("agentId='").append(agentId).append('\'');
        sb.append(", insuranceAvailability='").append(insuranceAvailability).append('\'');
        sb.append(", financeAvailability='").append(financeAvailability).append('\'');
        sb.append(", cities=").append(cities);
        sb.append('}');
        return sb.toString();
    }

    public class KnowlarityNumberModel implements Serializable {

        @SerializedName("past")
        private String pastNumber;

        @SerializedName("new")
        private String newNumber;

        public String getPastNumber() {
            return pastNumber;
        }

        public void setPastNumber(String pastNumber) {
            this.pastNumber = pastNumber;
        }

        public String getNewNumber() {
            return newNumber;
        }

        public void setNewNumber(String newNumber) {
            this.newNumber = newNumber;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("KnowlarityNumberModel{");
            sb.append("pastNumber='").append(pastNumber).append('\'');
            sb.append(", newNumber='").append(newNumber).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
