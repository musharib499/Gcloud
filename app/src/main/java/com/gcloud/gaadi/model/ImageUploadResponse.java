package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Gaurav on 22-05-2015.
 */
public class ImageUploadResponse extends GeneralResponse {

    @SerializedName("request_image_name")
    private String request_image_name;

    @SerializedName("response_image_name")
    private String response_image_name;

    @SerializedName("finance_lead_id")
    private String finance_lead_id;

    public String getRequest_image_name() {
        return request_image_name;
    }

    public void setRequest_image_name(String s) {
        this.request_image_name = s;
    }

    public String getResponse_image_name() {
        return response_image_name;
    }

    public void setResponse_image_name(String s) {
        this.response_image_name = s;
    }

    public String getFinance_lead_id() {
        return finance_lead_id;
    }

    public void setFinance_lead_id(String id) {
        finance_lead_id = id;
    }

    @Override
    public String toString() {
        return super.toString()
                + " request_image_name:" + request_image_name
                + "response_image_name:" + response_image_name
                + "finance_lead_id:" + finance_lead_id;
    }
}
