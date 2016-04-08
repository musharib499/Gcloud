package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ankitgarg on 29/05/15.
 */
public class InsuranceCaseStatusModel extends GeneralResponse {

    @SerializedName("totalRecords")
    private int totalRecords;

    @SerializedName("inpsectedCars")
    private ArrayList<InsuranceCaseStatusData> insuranceCases;

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public ArrayList<InsuranceCaseStatusData> getInsuranceCases() {
        return insuranceCases;
    }

    public void setInsuranceCases(ArrayList<InsuranceCaseStatusData> insuranceCases) {
        this.insuranceCases = insuranceCases;
    }

    @Override
    public String toString() {
        super.toString();
        final StringBuffer sb = new StringBuffer("InsuranceCaseStatusModel{");
        sb.append("totalRecords=").append(totalRecords);
        sb.append(", insuranceCases=").append(insuranceCases);
        sb.append('}');
        return sb.toString();
    }
}
