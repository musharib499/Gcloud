package com.gcloud.gaadi.model.Finance;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lakshaygirdhar on 16/10/15.
 */
public class BankOffers implements Serializable {

  /*  private String cust_max_eligibility_48;
    private String cust_max_eligibility_60;

    public String getCust_max_eligibility_60() {
        return cust_max_eligibility_60;
    }

    public void setCust_max_eligibility_60(String cust_max_eligibility_60) {
        this.cust_max_eligibility_60 = cust_max_eligibility_60;
    }*/

    private String bank_id;
    private String bank_name;
    private String bank_logo;
    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
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

    /*  private String cust_month_48_emi;

    private String cust_max_eligibility_36;

    private String cust_month_36_emi;

    private String cust_month_24_emi;

    private String fixed_roi;

    private String cust_month_60_emi;*/

    private String max_loan_tenure;

    private String min_loan_amt;

    private String default_loan_tenure;

    @SerializedName("12M")
    private _12M_tenure _12month_tenure;

    @SerializedName("24M")
    private _24M_tenure _24month_tenure;

    @SerializedName("36M")
    private _36M_tenure _36month_tenure;

    @SerializedName("48M")
    private _48M_tenure _48month_tenure;

    @SerializedName("60M")
    private _60M_tenure _60month_tenure;

    public String getMin_loan_amt() {
        return min_loan_amt;
    }

    public void setMin_loan_amt(String min_loan_amt) {
        this.min_loan_amt = min_loan_amt;
    }

    public String getDefault_loan_tenure() {
        return default_loan_tenure;
    }

    public void setDefault_loan_tenure(String default_loan_tenure) {
        this.default_loan_tenure = default_loan_tenure;
    }

    public _12M_tenure get_12month_tenure() {
        return _12month_tenure;
    }

    public void set_12month_tenure(_12M_tenure _12month_tenure) {
        this._12month_tenure = _12month_tenure;
    }

    public _24M_tenure get_24month_tenure() {
        return _24month_tenure;
    }

    public void set_24month_tenure(_24M_tenure _24month_tenure) {
        this._24month_tenure = _24month_tenure;
    }

    public _36M_tenure get_36month_tenure() {
        return _36month_tenure;
    }

    public void set_36month_tenure(_36M_tenure _36month_tenure) {
        this._36month_tenure = _36month_tenure;
    }

    public _48M_tenure get_48month_tenure() {
        return _48month_tenure;
    }

    public void set_48month_tenure(_48M_tenure _48month_tenure) {
        this._48month_tenure = _48month_tenure;
    }

    public _60M_tenure get_60month_tenure() {
        return _60month_tenure;
    }

    public void set_60month_tenure(_60M_tenure _60month_tenure) {
        this._60month_tenure = _60month_tenure;
    }

    public String getMax_loan_tenure() {
        return max_loan_tenure;
    }

    public void setMax_loan_tenure(String max_loan_tenure) {
        this.max_loan_tenure = max_loan_tenure;
    }

   /* public String getCust_max_eligibility_48() {
        return cust_max_eligibility_48;
    }

    public void setCust_max_eligibility_48(String cust_max_eligibility_48) {
        this.cust_max_eligibility_48 = cust_max_eligibility_48;
    }
*/
    public String getBank_id() {
        return bank_id;
    }

    public void setBank_id(String bank_id) {
        this.bank_id = bank_id;
    }

    /*public String getCust_month_48_emi() {
        return cust_month_48_emi;
    }

    public void setCust_month_48_emi(String cust_month_48_emi) {
        this.cust_month_48_emi = cust_month_48_emi;
    }

    public String getCust_max_eligibility_36() {
        return cust_max_eligibility_36;
    }

    public void setCust_max_eligibility_36(String cust_max_eligibility_36) {
        this.cust_max_eligibility_36 = cust_max_eligibility_36;
    }

    public String getCust_month_36_emi() {
        return cust_month_36_emi;
    }

    public void setCust_month_36_emi(String cust_month_36_emi) {
        this.cust_month_36_emi = cust_month_36_emi;
    }

    public String getCust_month_24_emi() {
        return cust_month_24_emi;
    }

    public void setCust_month_24_emi(String cust_month_24_emi) {
        this.cust_month_24_emi = cust_month_24_emi;
    }

    public String getFixed_roi() {
        return fixed_roi;
    }

    public void setFixed_roi(String fixed_roi) {
        this.fixed_roi = fixed_roi;
    }

    public String getCust_month_60_emi() {
        return cust_month_60_emi;
    }

    public void setCust_month_60_emi(String cust_month_60_emi) {
        this.cust_month_60_emi = cust_month_60_emi;
    }*/


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BankOffers{");
        sb.append("bank_id='").append(bank_id).append('\'');
        sb.append(", bank_name='").append(bank_name).append('\'');
        sb.append(", bank_logo='").append(bank_logo).append('\'');
        sb.append(", isChecked=").append(isChecked);
        sb.append(", max_loan_tenure='").append(max_loan_tenure).append('\'');
        sb.append(", min_loan_amt='").append(min_loan_amt).append('\'');
        sb.append(", default_loan_tenure='").append(default_loan_tenure).append('\'');
        sb.append(", _12month_tenure=").append(_12month_tenure);
        sb.append(", _24month_tenure=").append(_24month_tenure);
        sb.append(", _36month_tenure=").append(_36month_tenure);
        sb.append(", _48month_tenure=").append(_48month_tenure);
        sb.append(", _60month_tenure=").append(_60month_tenure);
        sb.append('}');
        return sb.toString();
    }
}
