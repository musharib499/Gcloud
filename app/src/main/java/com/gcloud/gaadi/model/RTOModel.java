package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Priya on 20-07-2015.
 */
public class RTOModel extends GeneralResponse {

    @SerializedName("rto")
    private String rto;

    public String getRto() {
        return rto;
    }

    public void setRto(String rto) {
        this.rto = rto;
    }
}
