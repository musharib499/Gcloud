package com.gcloud.gaadi.lms;

import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.LeadDetailModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Gaurav on 27-08-2015.
 */
public class LMSServerDataPullModel extends GeneralResponse {

    @SerializedName("data")
    private ArrayList<LeadDetailModel> leadDetailModelArrayList;

    @SerializedName("chunkSize")
    private String chunkSize;

    public ArrayList<LeadDetailModel> getLeadDetailModelArrayList() {
        return leadDetailModelArrayList;
    }

    public void setLeadDetailModelArrayList(ArrayList<LeadDetailModel> leadDetailModelArrayList) {
        this.leadDetailModelArrayList = leadDetailModelArrayList;
    }

    public int getChunkSize() {
        return Integer.valueOf(chunkSize);
    }

    public void setChunkSize(String chunkSize) {
        this.chunkSize = chunkSize;
    }
}
