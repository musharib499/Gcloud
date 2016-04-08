package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Priya on 17-07-2015.
 */
public class FinanceCompany implements Serializable {

    @SerializedName("finance_company_slug")
    private String financeCompanySlug;

    @SerializedName("finance_company_name")
    private String financeCompanyName;

    public String getFinanceCompanySlug() {
        return financeCompanySlug;
    }

    public void setFinanceCompanySlug(String financeCompanySlug) {
        this.financeCompanySlug = financeCompanySlug;
    }

    public String getFinanceCompanyName() {
        return financeCompanyName;
    }

    public void setFinanceCompanyName(String financeCompanyName) {
        this.financeCompanyName = financeCompanyName;
    }
}
