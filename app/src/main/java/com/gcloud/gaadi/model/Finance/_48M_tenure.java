package com.gcloud.gaadi.model.Finance;

import java.io.Serializable;

/**
 * Created by priyarawat on 3/12/15.
 */
public class _48M_tenure implements Serializable {
    private String fixed_roi;
    private String max_eligibility;
    private String monthly_emi;

    public String getFixed_roi() {
        return fixed_roi;
    }

    public void setFixed_roi(String fixed_roi) {
        this.fixed_roi = fixed_roi;
    }

    public String getMax_eligibility() {
        return max_eligibility;
    }

    public void setMax_eligibility(String max_eligibility) {
        this.max_eligibility = max_eligibility;
    }

    public String getMonthly_emi() {
        return monthly_emi;
    }

    public void setMonthly_emi(String monthly_emi) {
        this.monthly_emi = monthly_emi;
    }

    @Override
    public String toString() {
        return "_48M_tenure{" +
                "fixed_roi='" + fixed_roi + '\'' +
                ", max_eligibility='" + max_eligibility + '\'' +
                ", monthly_emi='" + monthly_emi + '\'' +
                '}';
    }
}