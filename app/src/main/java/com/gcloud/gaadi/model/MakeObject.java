package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ankit on 5/12/14.
 */
public class MakeObject implements Serializable {

    private int id;

    @SerializedName("make")
    private String make;

    @SerializedName("make_id")
    private String makeId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getMakeId() {
        return makeId;
    }

    public void setMakeId(String makeId) {
        this.makeId = makeId;
    }

    @Override
    public String toString() {
        return "MakeObject{" +
                "id=" + id +
                ", make='" + make + '\'' +
                ", makeId='" + makeId + '\'' +
                '}';
    }
}
