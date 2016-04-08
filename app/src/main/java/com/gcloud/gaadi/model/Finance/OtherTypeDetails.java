package com.gcloud.gaadi.model.Finance;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Manish on 10/14/2015.
 */
public class OtherTypeDetails implements Serializable {
    @SerializedName("is_residence_owner")
    private String residenceProof;

    @SerializedName("loan_period")
    private String repaying;

    @SerializedName("previous_loan")
    private String takenLoan;

    @SerializedName("other_employment_status")
    private String employmentStatus;

    public String getResidenceProof() {
        return residenceProof;
    }

    public void setResidenceProof(String residenceProof) {
        this.residenceProof = residenceProof;
    }

    public String getTakenLoan() {
        return takenLoan;
    }

    public void setTakenLoan(String takenLoan) {
        this.takenLoan = takenLoan;
    }

    public String getRepaying() {
        return repaying;
    }

    public void setRepaying(String repaying) {
        this.repaying = repaying;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }
}
