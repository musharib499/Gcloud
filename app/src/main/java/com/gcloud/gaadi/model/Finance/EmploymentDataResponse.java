package com.gcloud.gaadi.model.Finance;

import com.gcloud.gaadi.model.GeneralResponse;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lakshaygirdhar on 7/10/15.
 */
public class EmploymentDataResponse extends GeneralResponse {

    @SerializedName("data")
    EmploymentData[] employmentDatas;

    public EmploymentData[] getEmploymentDatas() {
        return employmentDatas;
    }

    public void setEmploymentDatas(EmploymentData[] employmentDatas) {
        this.employmentDatas = employmentDatas;
    }
}
