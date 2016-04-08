package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gauravkumar on 23/9/15.
 */
public class InventoryImageDeleteResponse extends GeneralResponse {

    @SerializedName("data")
    private ArrayList<GeneralResponse> responses;

    public ArrayList<GeneralResponse> getResponses() {
        return responses;
    }

    public void setResponses(ArrayList<GeneralResponse> responses) {
        this.responses = responses;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InventoryImageDeleteResponse{");
        sb.append("responses=").append(responses);
        sb.append('}');
        return sb.toString();
    }
}
