package com.gcloud.gaadi.model;

import java.io.Serializable;

/**
 * Created by ankitgarg on 12/05/15.
 */
public class Car implements Serializable {

    private String carId;
    private String makeName;
    private int makeId;
    private String modelName;
    private int modelId;
    private String versionName;
    private int versionId;
    private String modelYear;
    private String modelMonth;
    private String registrationCity;
    private String kmDriven;
    private String numOwner;
    private String registrationNumber;

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getMakeName() {
        return makeName;
    }

    public void setMakeName(String makeName) {
        this.makeName = makeName;
    }

    public int getMakeId() {
        return makeId;
    }

    public void setMakeId(int makeId) {
        this.makeId = makeId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public String getModelYear() {
        return modelYear;
    }

    public void setModelYear(String modelYear) {
        this.modelYear = modelYear;
    }

    public String getModelMonth() {
        return modelMonth;
    }

    public void setModelMonth(String modelMonth) {
        this.modelMonth = modelMonth;
    }

    public String getRegistrationCity() {
        return registrationCity;
    }

    public void setRegistrationCity(String registrationCity) {
        this.registrationCity = registrationCity;
    }

    public String getKmDriven() {
        return kmDriven;
    }

    public void setKmDriven(String kmDriven) {
        this.kmDriven = kmDriven;
    }

    public String getNumOwner() {
        return numOwner;
    }

    public void setNumOwner(String numOwner) {
        this.numOwner = numOwner;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
}
