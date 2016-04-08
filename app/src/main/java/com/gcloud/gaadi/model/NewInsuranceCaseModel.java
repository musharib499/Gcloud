package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ankitgarg on 29/05/15.
 */
public class NewInsuranceCaseModel extends GeneralResponse {

    @SerializedName("process_id")
    private String processId;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }
}
