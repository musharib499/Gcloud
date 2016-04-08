package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Priya on 17-07-2015.
 */
public class PreviousPolicyInsurer implements Serializable {

    @SerializedName("prev_policy_insurer_slug")
    private String prevPolicyInsurerSlug;

    @SerializedName("prev_policy_insurer_name")
    private String prevPolicyInsurerName;

    public String getPrevPolicyInsurerSlug() {
        return prevPolicyInsurerSlug;
    }

    public void setPrevPolicyInsurerSlug(String prevPolicyInsurerSlug) {
        this.prevPolicyInsurerSlug = prevPolicyInsurerSlug;
    }

    public String getPrevPolicyInsurerName() {
        return prevPolicyInsurerName;
    }

    public void setPrevPolicyInsurerName(String prevPolicyInsurerName) {
        this.prevPolicyInsurerName = prevPolicyInsurerName;
    }
}
