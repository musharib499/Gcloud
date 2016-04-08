package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Priya on 29-05-2015.
 */
public class InspectedCarsListModel extends GeneralResponse {
    @SerializedName("totalRecords")
    private String totalRecords;

    @SerializedName("inpsectedCars")
    private ArrayList<InspectedCarItem> inpsectedCars;

    public String getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(String totalRecords) {
        this.totalRecords = totalRecords;
    }

    public ArrayList<InspectedCarItem> getInpsectedCars() {
        return inpsectedCars;
    }

    public void setInpsectedCars(ArrayList<InspectedCarItem> inpsectedCars) {
        this.inpsectedCars = inpsectedCars;
    }
}
