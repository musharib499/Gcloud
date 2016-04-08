package com.gcloud.gaadi.model;

import com.gcloud.gaadi.insurance.InsuranceDashboardModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ankitgarg on 29/05/15.
 */
public class PaymentDetailsModel extends GeneralResponse {

    @SerializedName("insuranceCaseId")
    private int insuranceCaseId;

    @SerializedName("policy_id")
    private int policyId;

    private InsuranceDashboardModel dashboard;

    public InsuranceDashboardModel getDashboard() {
        return dashboard;
    }

    public void setDashboard(InsuranceDashboardModel dashboard) {
        this.dashboard = dashboard;
    }

    public int getInsuranceCaseId() {
        return insuranceCaseId;
    }

    public void setInsuranceCaseId(int insuranceCaseId) {
        this.insuranceCaseId = insuranceCaseId;
    }

    public int getPolicyId() {
        return policyId;
    }

    public void setPolicyId(int policyId) {
        this.policyId = policyId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PaymentDetailsModel{");
        sb.append("insuranceCaseId=").append(insuranceCaseId);
        sb.append(", policyId=").append(policyId);
        sb.append('}');
        return sb.toString();
    }
}
