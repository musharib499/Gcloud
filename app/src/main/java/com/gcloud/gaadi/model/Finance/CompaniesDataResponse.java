package com.gcloud.gaadi.model.Finance;

import com.gcloud.gaadi.model.GeneralResponse;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lakshaygirdhar on 7/10/15.
 */
public class CompaniesDataResponse extends GeneralResponse {

    @SerializedName("data")
    CompaniesData[] companiesDatas;

    public CompaniesData[] getCompaniesDatas() {
        return companiesDatas;
    }

    public void setCompaniesDatas(CompaniesData[] companiesDatas) {
        this.companiesDatas = companiesDatas;
    }
}
