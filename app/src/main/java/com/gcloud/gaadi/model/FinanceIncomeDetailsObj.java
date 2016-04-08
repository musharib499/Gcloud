package com.gcloud.gaadi.model;

import com.gcloud.gaadi.model.Finance.BusinessTypeDetails;
import com.gcloud.gaadi.model.Finance.OtherTypeDetails;
import com.gcloud.gaadi.model.Finance.ProfessionTypeDetails;
import com.gcloud.gaadi.model.Finance.SalariedTypeDetail;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Lakshay on 08-09-2015.
 */
public class FinanceIncomeDetailsObj implements Serializable {

    @SerializedName("employement_type")
    private String employmentType;

    @SerializedName("SalaryDetails")
    private SalariedTypeDetail mSalariedTypeDetail;

    @SerializedName("BusinessDetails")
    private BusinessTypeDetails mBusinessTypeDetails;

    @SerializedName("ProfessionDetails")
    private ProfessionTypeDetails mProfessionTypeDetails;

    @SerializedName("OtherDetails")
    private OtherTypeDetails mOtherTypeDetails;

    public OtherTypeDetails getmOtherTypeDetails() {
        return mOtherTypeDetails;
    }

    public void setmOtherTypeDetails(OtherTypeDetails mOtherTypeDetails) {
        this.mOtherTypeDetails = mOtherTypeDetails;
    }

    public ProfessionTypeDetails getmProfessionTypeDetails() {
        return mProfessionTypeDetails;
    }

    public void setmProfessionTypeDetails(ProfessionTypeDetails mProfessionTypeDetails) {
        this.mProfessionTypeDetails = mProfessionTypeDetails;
    }

    private SalariedTypeDetail getmSalariedTypeDetail() {
        return mSalariedTypeDetail;
    }

    public void setmSalariedTypeDetail(SalariedTypeDetail mSalariedTypeDetail) {
        this.mSalariedTypeDetail = mSalariedTypeDetail;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public BusinessTypeDetails getmBusinessTypeDetails() {
        return mBusinessTypeDetails;
    }

    public void setmBusinessTypeDetails(BusinessTypeDetails mBusinessTypeDetails) {
        this.mBusinessTypeDetails = mBusinessTypeDetails;
    }
}
