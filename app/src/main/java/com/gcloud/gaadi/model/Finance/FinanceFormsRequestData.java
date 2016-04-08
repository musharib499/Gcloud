package com.gcloud.gaadi.model.Finance;

import com.gcloud.gaadi.model.FinanceIncomeDetailsObj;
import com.gcloud.gaadi.model.FinancePersonalDetails;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lakshaygirdhar on 14/10/15.
 */
public class FinanceFormsRequestData implements Serializable {

    @SerializedName("PersonalDetails")
    private FinancePersonalDetails personalDetails;

    @SerializedName("IncomeDetails")
    private FinanceIncomeDetailsObj incomeDetailsObj;

    public FinancePersonalDetails getPersonalDetails() {
        return personalDetails;
    }

    public void setPersonalDetails(FinancePersonalDetails personalDetails) {
        this.personalDetails = personalDetails;
    }

    public FinanceIncomeDetailsObj getIncomeDetailsObj() {
        return incomeDetailsObj;
    }

    public void setIncomeDetailsObj(FinanceIncomeDetailsObj incomeDetailsObj) {
        this.incomeDetailsObj = incomeDetailsObj;
    }
}
