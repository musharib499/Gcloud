package com.gcloud.gaadi.model.Finance;

import com.gcloud.gaadi.model.GeneralResponse;
import com.google.gson.annotations.SerializedName;

/**
 * Created by priyarawat on 28/3/16.
 */
public class IndustriesDataResponse extends GeneralResponse {
    @SerializedName("data")
    String[] industriesList;

    public String[] getIndustriesList() {
        return industriesList;
    }

    public void setIndustriesList(String[] industriesList) {
        this.industriesList = industriesList;
    }
}
