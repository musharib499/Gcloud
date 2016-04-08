package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ankit on 7/12/14.
 */
public class VersionObject implements Serializable {

    private int id;

    @SerializedName("version_id")
    private String versionid;

    @SerializedName("version")
    private String versionName;

    @SerializedName("model_id")
    private String modelId;

    @SerializedName("make_id")
    private String makeId;

    @SerializedName("fuelType")
    private String fuelType;

    @SerializedName("transmissionType")
    private String transmission;

    @SerializedName("make")
    private String make;

    @SerializedName("model")
    private String model;

    @SerializedName("modelVersion")
    private String modelVersion;

    @SerializedName("start_year")
    private String start_year;

    @SerializedName("end_year")
    private String end_year;

    @SerializedName("makeModel")
    private String makeModel;


    @SerializedName("makeModelVersion")
    private String makeModelVersion;


    public String getStart_year() {
        return start_year;
    }

    public void setStart_year(String start_year) {
        this.start_year = start_year;
    }

    public String getEnd_year() {
        return end_year;
    }

    public void setEnd_year(String end_year) {
        this.end_year = end_year;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getMakeModel() {
        return makeModel;
    }

    public void setMakeModel(String makeModel) {
        this.makeModel = makeModel;
    }

    public String getMakeModelVersion() {
        return makeModelVersion;
    }

    public void setMakeModelVersion(String makeModelVersion) {
        this.makeModelVersion = makeModelVersion;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersionid() {
        return versionid;
    }

    public void setVersionid(String versionid) {
        this.versionid = versionid;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getMakeId() {
        return makeId;
    }

    public void setMakeId(String makeId) {
        this.makeId = makeId;
    }

    @Override
    public String toString() {
        return "ModelVersion{" +
                "id=" + id +
                ", versionid='" + versionid + '\'' +
                ", versionName='" + versionName + '\'' +
                ", modelId='" + modelId + '\'' +
                ", makeId='" + makeId + '\'' +
                ", start year='" + start_year + '\'' +
                ", end year='" + end_year + '\'' +
                '}';
    }
}
