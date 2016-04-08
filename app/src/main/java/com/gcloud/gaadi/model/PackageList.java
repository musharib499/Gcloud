package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PackageList implements Serializable {

    @SerializedName("pack_id")
    private String pack_id;

    @SerializedName("pack_type")
    private String pack_type;

    @SerializedName("pack_short_name")
    private String pack_short_name;

    @SerializedName("pack_short_type")
    private String pack_short_type;
    @SerializedName("pack_days")
    private String pack_days;
    @SerializedName("pack_name")
    private String pack_name;

    public String getPack_short_name() {
        return pack_short_name;
    }

    public void setPack_short_name(String pack_short_name) {
        this.pack_short_name = pack_short_name;
    }

    public String getPack_short_type() {
        return pack_short_type;
    }

    public void setPack_short_type(String pack_short_type) {
        this.pack_short_type = pack_short_type;
    }

    public String getPack_type() {
        return pack_type;
    }

    public void setPack_type(String pack_type) {
        this.pack_type = pack_type;
    }

    public String getPack_days() {
        return pack_days;
    }

    public void setPack_days(String pack_days) {
        this.pack_days = pack_days;
    }

    public String getPack_id() {
        return pack_id;
    }

    public void setPack_id(String pack_id) {
        this.pack_id = pack_id;
    }

    public String getPack_name() {
        return pack_name;
    }

    public void setPack_name(String pack_name) {
        this.pack_name = pack_name;
    }

}
