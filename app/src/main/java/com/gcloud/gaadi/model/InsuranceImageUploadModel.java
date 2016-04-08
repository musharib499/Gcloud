package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Vaibhav on 5/29/2015.
 */
public class InsuranceImageUploadModel extends GeneralResponse {
    @SerializedName("filePath")
    String filePath;

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String path) {
        this.filePath = path;
    }
}
