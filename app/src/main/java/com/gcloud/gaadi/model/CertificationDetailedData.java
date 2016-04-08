package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CertificationDetailedData implements Serializable {

    @SerializedName("make")
    private String make;


    @SerializedName("usedCarID")
    private String usedCarID;
    @SerializedName("model")
    private String model;
    @SerializedName("year")
    private String year;
    @SerializedName("mm")
    private String mm;
    @SerializedName("myear")
    private String myear;
    @SerializedName("carversion")
    private String carversion;
    @SerializedName("regno")
    private String regno;
    @SerializedName("engine_number")
    private String engine_number;
    @SerializedName("chassis_no")
    private String chassis_no;
    @SerializedName("recommendedPackage")
    private String recommendedPackage;
    @SerializedName("certificationID")
    private String certificationID;
    @SerializedName("month")
    private String month;
    @SerializedName("dealer_id")
    private String dealer_id;

    public String getUsedCarID() {
        return usedCarID;
    }

    public void setUsedCarID(String usedCarID) {
        this.usedCarID = usedCarID;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMm() {
        return mm;
    }

    public void setMm(String mm) {
        this.mm = mm;
    }

    public String getMyear() {
        return myear;
    }

    public void setMyear(String myear) {
        this.myear = myear;
    }

    public String getCarversion() {
        return carversion;
    }

    public void setCarversion(String carversion) {
        this.carversion = carversion;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getEngine_number() {
        return engine_number;
    }

    public void setEngine_number(String engine_number) {
        this.engine_number = engine_number;
    }

    public String getChassis_no() {
        return chassis_no;
    }

    public void setChassis_no(String chassis_no) {
        this.chassis_no = chassis_no;
    }

    public String getRecommendedPackage() {
        return recommendedPackage;
    }

    public void setRecommendedPackage(String recommendedPackage) {
        this.recommendedPackage = recommendedPackage;
    }

    public String getCertificationID() {
        return certificationID;
    }

    public void setCertificationID(String certificationID) {
        this.certificationID = certificationID;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDealer_id() {
        return dealer_id;
    }

    public void setDealer_id(String dealer_id) {
        this.dealer_id = dealer_id;
    }
}
