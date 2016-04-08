package com.gcloud.gaadi.model;

import java.io.Serializable;

/**
 * Created by ankit on 3/12/14.
 */
public class MakeModelVersionModel implements Serializable {

    private int id;
    private String mmv;
    private String make;
    private String model;
    private String version;
    private String makeId;
    private String modelId;
    private String versionId;
    private String makeModel;
    private String modelVersion;
    private String makeModelVersion;
    private String fuelTye;
    private String transmissionType;

    public MakeModelVersionModel(int id, String makeId, String make, String modelId, String model) {
        this.id = id;
        this.makeId = makeId;
        this.make = make;
        this.modelId = modelId;
        this.model = model;

    }

    public String getMakeModel() {
        return makeModel;
    }

    public void setMakeModel(String makeModel) {
        this.makeModel = makeModel;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getMakeModelVersion() {
        return makeModelVersion;
    }

    public void setMakeModelVersion(String makeModelVersion) {
        this.makeModelVersion = makeModelVersion;
    }

    public String getFuelTye() {
        return fuelTye;
    }

    public void setFuelTye(String fuelTye) {
        this.fuelTye = fuelTye;
    }

    public String getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(String transmissionType) {
        this.transmissionType = transmissionType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMmv() {
        return mmv;
    }

    public void setMmv(String mmv) {
        this.mmv = mmv;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMakeId() {
        return makeId;
    }

    public void setMakeId(String makeId) {
        this.makeId = makeId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    @Override
    public String toString() {
        return "MakeModelVersionModel{" +
                "id=" + id +
                ", mmv='" + mmv + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", version='" + version + '\'' +
                ", makeId='" + makeId + '\'' +
                ", modelId='" + modelId + '\'' +
                ", versionId='" + versionId + '\'' +
                '}';
    }
}
