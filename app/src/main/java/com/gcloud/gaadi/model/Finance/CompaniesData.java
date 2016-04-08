package com.gcloud.gaadi.model.Finance;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Calendar;

public class CompaniesData implements Serializable {

    @SerializedName("id")
    private String company_id;
    private String company_name;
    private String type;
    private String updated_time;
    private String category;

    public CompaniesData() {
        updated_time = Calendar.getInstance().get(Calendar.SECOND) + "";
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpdated_time() {
        return updated_time;
    }

    public void setUpdated_time(String updated_time) {
        this.updated_time = updated_time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public CompaniesData(String company_id, String company_name, String type, String updated_time, String category) {
        this.company_id = company_id;
        this.company_name = company_name;
        this.type = type;
        this.updated_time = updated_time;
        this.category = category;
    }
}
