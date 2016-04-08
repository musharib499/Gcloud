package com.gcloud.gaadi.model.Finance;

import java.io.Serializable;
import java.util.Calendar;

public class BankData implements Serializable {

    private String bank_id;
    private String bank_name;
    private String updated_timestamp;

    public BankData() {
        updated_timestamp = Calendar.getInstance().get(Calendar.SECOND) + "";
    }

    public String getBank_id() {
        return bank_id;
    }

    public void setBank_id(String bank_id) {
        this.bank_id = bank_id;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getUpdated_timestamp() {
        return updated_timestamp;
    }

    public void setUpdated_timestamp(String updated_timestamp) {
        this.updated_timestamp = updated_timestamp;
    }

    public BankData(String bank_id, String bank_name, String updated_timestamp) {
        this.bank_id = bank_id;
        this.bank_name = bank_name;
        this.updated_timestamp = updated_timestamp;
    }
}
