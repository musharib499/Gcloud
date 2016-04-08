package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ankit on 8/1/15.
 */
public class ShowroomData implements Serializable {

    @SerializedName("id")
    private String showroomId;

    @SerializedName("dealer_id")
    private String dealerId;

    @SerializedName("name")
    private String showroomName;

    public String getShowroomId() {
        return showroomId;
    }

    public void setShowroomId(String showroomId) {
        this.showroomId = showroomId;
    }

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    public String getShowroomName() {
        return showroomName;
    }

    public void setShowroomName(String showroomName) {
        this.showroomName = showroomName;
    }

    @Override
    public String toString() {
        return "ShowroomData{" +
                "showroomId='" + showroomId + '\'' +
                ", dealerId='" + dealerId + '\'' +
                ", showroomName='" + showroomName + '\'' +
                '}';
    }
}
