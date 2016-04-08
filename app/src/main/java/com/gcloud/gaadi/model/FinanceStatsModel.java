package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Lakshay on 27-05-2015.
 */
public class FinanceStatsModel extends GeneralResponse implements Serializable {

    @SerializedName("application_status_count")
    FinanceCounts counts;

    public FinanceCounts getCounts() {
        return counts;
    }

    public void setCounts(FinanceCounts counts) {
        this.counts = counts;
    }
}
