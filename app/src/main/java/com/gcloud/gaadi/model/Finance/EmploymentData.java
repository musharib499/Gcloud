package com.gcloud.gaadi.model.Finance;

import java.io.Serializable;

/**
 * Created by priyarawat on 6/10/15.
 */
public class EmploymentData implements Serializable {

    private String employment_id;
    private String employment_cat;
    private String type;
    private String type_values;

    public String getEmployment_id() {
        return employment_id;
    }

    public void setEmployment_id(String employment_id) {
        this.employment_id = employment_id;
    }

    public String getEmployment_cat() {
        return employment_cat;
    }

    public void setEmployment_cat(String employment_cat) {
        this.employment_cat = employment_cat;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType_values() {
        return type_values;
    }

    public void setType_values(String type_values) {
        this.type_values = type_values;
    }

    public EmploymentData(String employment_id, String employment_cat, String type, String type_values) {
        this.employment_id = employment_id;
        this.employment_cat = employment_cat;
        this.type = type;
        this.type_values = type_values;
    }
}
