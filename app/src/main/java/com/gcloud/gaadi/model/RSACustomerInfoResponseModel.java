package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ankitgarg on 21/05/15.
 */
public class RSACustomerInfoResponseModel extends GeneralResponse {

    @SerializedName("rsa_id")
    private String rsaId;

    public String getRsaId() {
        return rsaId;
    }

    public void setRsaId(String rsaId) {
        this.rsaId = rsaId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RSACustomerInfoResponseModel{");
        sb.append("rsaId='").append(rsaId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
