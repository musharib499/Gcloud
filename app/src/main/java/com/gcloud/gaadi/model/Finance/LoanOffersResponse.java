package com.gcloud.gaadi.model.Finance;

import com.gcloud.gaadi.model.GeneralResponse;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lakshaygirdhar on 16/10/15.
 */
public class LoanOffersResponse extends GeneralResponse implements Serializable {


    @SerializedName("bank_offer")
    private ArrayList<BankOffers> bank_offer;

    private String finance_lead_id;

    private String customer_id;


    public ArrayList<BankOffers> getBank_offer() {
        return bank_offer;
    }

    public void setBank_offer(ArrayList<BankOffers> bank_offer) {
        this.bank_offer = bank_offer;
    }

    public String getFinance_lead_id ()
    {
        return finance_lead_id;
    }

    public void setFinance_lead_id (String finance_lead_id)
    {
        this.finance_lead_id = finance_lead_id;
    }

    public String getCustomer_id ()
    {
        return customer_id;
    }

    public void setCustomer_id (String customer_id)
    {
        this.customer_id = customer_id;
    }

    @Override
    public String toString() {
        return "LoanOffersResponse{" +
                "bank_offer=" + bank_offer +
                ", finance_lead_id='" + finance_lead_id + '\'' +
                ", customer_id='" + customer_id + '\'' +
                '}';
    }
}
