package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Ambujesh on 5/27/2015.
 */
public class QuoteDetails implements Serializable {

    @SerializedName("insurer_id")
    private String insurerID;

    @SerializedName("insurer_logo")
    private String insurerLogo;

    @SerializedName("quote_id")
    private String quoteId;

    @SerializedName("quote_number")
    private String quoteNumber;

    @SerializedName("broker_id")
    private String brokerId;

    @SerializedName("broker_name")
    private String brokerName;

    @SerializedName("insurer_name")
    private String insurerName;

    @SerializedName("insurer_name_trimmed")
    private String insurerNameTrimmed;

    @SerializedName("net_premium")
    private String netPremium;

    @SerializedName("idv")
    private String idvValue;

    @SerializedName("od_dis_premium")
    private String odDiscountPremium;

    @SerializedName("od_basic")
    private String odBasic;

    @SerializedName("final_od")
    private String finalOd;


    @SerializedName("od_dis_per")
    private String odDiscountPercent;

    @SerializedName("cng_tp_charges")
    private String cngTpCharges;

    @SerializedName("cng_value")
    private String cngValue;


    @SerializedName("cng_premium")
    private String cngPremium;

    @SerializedName("elec_accessories_value")
    private String elecAccessoriesValue;

    @SerializedName("elec_accessories_premium")
    private String elecAccessoriesPremium;

    @SerializedName("non_elec_accessories_value")
    private String nonElecAccessoriesValue;

    @SerializedName("non_elec_accessories_premium")
    private String nonElecAccessoriesPremium;

    @SerializedName("ncb_per")
    private String ncbPercent;

    @SerializedName("ncb_amount")
    private String ncbAmount;

    @SerializedName("third_party")
    private String thirdParty;


    @SerializedName("total_tp")
    private String totalTp;

    @SerializedName("service_tax")
    private String serviceTax;

    @SerializedName("service_tax_amt")
    private String serviceTaxAmount;

    @SerializedName("cheque_in_favour")
    private String chequeInFavour;
    @SerializedName("insurance_case_id")
    private String insurance_case_id;

    public String getVoluntaryDeductiblePremium() {
        return voluntaryDeductiblePremium;
    }

    public void setVoluntaryDeductiblePremium(String voluntaryDeductiblePremium) {
        this.voluntaryDeductiblePremium = voluntaryDeductiblePremium;
    }

    @SerializedName("voluntary_deductible_premium")
    private String voluntaryDeductiblePremium;

    public String getZeroDepPremium() {
        return zeroDepPremium;
    }

    public void setZeroDepPremium(String zeroDepPremium) {
        this.zeroDepPremium = zeroDepPremium;
    }

    @SerializedName("zero_dep_premium")
    private String zeroDepPremium;

    public String getIdv_range() {
        return idv_range;
    }

    public void setIdv_range(String idv_range) {
        this.idv_range = idv_range;
    }

    @SerializedName("min_max_idv")
    private String idv_range;

    public String getOdDiscountPercent() {
        return odDiscountPercent;
    }

    public void setOdDiscountPercent(String odDiscountPercent) {
        this.odDiscountPercent = odDiscountPercent;
    }

    public String getChequeInFavour() {
        return chequeInFavour;
    }

    public void setChequeInFavour(String chequeInFavour) {
        this.chequeInFavour = chequeInFavour;
    }

    public String getServiceTaxAmount() {
        return serviceTaxAmount;
    }

    public void setServiceTaxAmount(String serviceTaxAmount) {
        this.serviceTaxAmount = serviceTaxAmount;
    }

    public String getInsurerNameTrimmed() {
        return insurerNameTrimmed;
    }

    public void setInsurerNameTrimmed(String insurerNameTrimmed) {
        this.insurerNameTrimmed = insurerNameTrimmed;
    }

    public String getInsurerLogo() {
        return insurerLogo;
    }

    public void setInsurerLogo(String insurerLogo) {
        this.insurerLogo = insurerLogo;
    }

    public String getInsurerID() {
        return insurerID;
    }

    public void setInsurerID(String insurerID) {
        this.insurerID = insurerID;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getQuoteNumber() {
        return quoteNumber;
    }

    public void setQuoteNumber(String quoteNumber) {
        this.quoteNumber = quoteNumber;
    }

    public String getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public String getInsurerName() {
        return insurerName;
    }

    public void setInsurerName(String insurerName) {
        this.insurerName = insurerName;
    }

    public String getNetPremium() {
        return netPremium;
    }

    public void setNetPremium(String netPremium) {
        this.netPremium = netPremium;
    }

    public String getIdvValue() {
        return idvValue;
    }

    public void setIdvValue(String idvValue) {
        this.idvValue = idvValue;
    }

    public String getOdDiscountPremium() {
        return odDiscountPremium;
    }

    public void setOdDiscountPremium(String odDiscountPremium) {
        this.odDiscountPremium = odDiscountPremium;
    }

    public String getOdBasic() {
        return odBasic;
    }

    public void setOdBasic(String odBasic) {
        this.odBasic = odBasic;
    }

    public String getCngTpCharges() {
        return cngTpCharges;
    }

    public void setCngTpCharges(String cngTpCharges) {
        this.cngTpCharges = cngTpCharges;
    }

    public String getCngValue() {
        return cngValue;
    }

    public void setCngValue(String cngValue) {
        this.cngValue = cngValue;
    }

    public String getCngPremium() {
        return cngPremium;
    }

    public void setCngPremium(String cngPremium) {
        this.cngPremium = cngPremium;
    }

    public String getElecAccessoriesValue() {
        return elecAccessoriesValue;
    }

    public void setElecAccessoriesValue(String elecAccessoriesValue) {
        this.elecAccessoriesValue = elecAccessoriesValue;
    }

    public String getElecAccessoriesPremium() {
        return elecAccessoriesPremium;
    }

    public void setElecAccessoriesPremium(String elecAccessoriesPremium) {
        this.elecAccessoriesPremium = elecAccessoriesPremium;
    }

    public String getNonElecAccessoriesValue() {
        return nonElecAccessoriesValue;
    }

    public void setNonElecAccessoriesValue(String nonElecAccessoriesValue) {
        this.nonElecAccessoriesValue = nonElecAccessoriesValue;
    }

    public String getNonElecAccessoriesPremium() {
        return nonElecAccessoriesPremium;
    }

    public void setNonElecAccessoriesPremium(String nonElecAccessoriesPremium) {
        this.nonElecAccessoriesPremium = nonElecAccessoriesPremium;
    }

    public String getNcbPercent() {
        return ncbPercent;
    }

    public void setNcbPercent(String ncbPercent) {
        this.ncbPercent = ncbPercent;
    }

    public String getNcbAmount() {
        return ncbAmount;
    }

    public void setNcbAmount(String ncbAmount) {
        this.ncbAmount = ncbAmount;
    }

    public String getThirdParty() {
        return thirdParty;
    }

    public void setThirdParty(String thirdParty) {
        this.thirdParty = thirdParty;
    }

    public String getFinalOd() {
        return finalOd;
    }

    public void setFinalOd(String finalOd) {
        this.finalOd = finalOd;
    }

    public String getTotalTp() {
        return totalTp;
    }

    public void setTotalTp(String totalTp) {
        this.totalTp = totalTp;
    }

    public String getServiceTax() {
        return serviceTax;
    }

    public void setServiceTax(String serviceTax) {
        this.serviceTax = serviceTax;
    }

    public String getInsurance_case_id() {
        return insurance_case_id;
    }

    public void setInsurance_case_id(String insurance_case_id) {
        this.insurance_case_id = insurance_case_id;
    }

    public JSONObject getJsonObject() {

        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject temp = new JSONObject();
            temp.put("insurer_id", getInsurerID());
            JSONArray array = new JSONArray();
            array.put(temp);
            jsonObject.put("quoteDetails", array);
            return jsonObject;
        } catch (JSONException jsonObject) {

        }
        return null;

    }

}
