package com.gcloud.gaadi.model;

import java.io.Serializable;

/**
 * Created by ankitgarg on 29/05/15.
 */
public class InsuranceDocumentModel implements Serializable {

    String startDate;
    String endDate;
    String prevPolicyInsurer;
    String prevPoEndDate;
    String ownerName;

    public String getStartDate() {
        return this.startDate;
    }

    public void setStartDate(String date) {
        this.startDate = date;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public void setEndDate(String date) {
        this.endDate = date;
    }

    public String getPrevPolicyInsurer() {
        return this.prevPolicyInsurer;
    }

    public void setPrevPolicyInsurer(String insurer) {
        this.prevPolicyInsurer = insurer;
    }

    public String getPrevPoEndDate() {
        return this.prevPoEndDate;
    }

    public void setPrevPoEndDate(String date) {
        this.prevPoEndDate = date;
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    public void setOwnerName(String name) {
        this.ownerName = name;
    }

}
