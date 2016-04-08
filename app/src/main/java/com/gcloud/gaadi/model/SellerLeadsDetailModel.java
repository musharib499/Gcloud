package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Priya on 26-08-2015.
 */
public class SellerLeadsDetailModel extends GeneralResponse {

    @SerializedName("leads")
    private LeadDetailModel leadDetails;

    public LeadDetailModel getLeadDetails() {
        return leadDetails;
    }

    public void setLeadDetails(LeadDetailModel leadDetails) {
        this.leadDetails = leadDetails;
    }
}
