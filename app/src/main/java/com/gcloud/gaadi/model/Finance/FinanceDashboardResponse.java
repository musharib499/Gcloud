package com.gcloud.gaadi.model.Finance;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lakshaygirdhar on 1/10/15.
 */
public class FinanceDashboardResponse {

    private String status;

    private FinanceDashboardData data;

    @SerializedName("force_upgrade")
    private boolean forceUpgrade;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public FinanceDashboardData getData() {
        return data;
    }

    public void setData(FinanceDashboardData data) {
        this.data = data;
    }

    public boolean isForceUpgrade() {
        return forceUpgrade;
    }

    public void setForceUpgrade(boolean forceUpgrade) {
        this.forceUpgrade = forceUpgrade;
    }

    @Override
    public String toString() {
        return "FinanceDashboardResponse{" +
                "status='" + status + '\'' +
                ", data=" + data +
                ", forceUpgrade=" + forceUpgrade +
                '}';
    }
}
