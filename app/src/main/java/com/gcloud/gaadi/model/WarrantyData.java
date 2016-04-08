package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WarrantyData implements Serializable {

    @SerializedName("regno")
    private String regno;

    @SerializedName("make")
    private String make;

    @SerializedName("make_id")
    private int make_id;
    @SerializedName("model")
    private String model;
    @SerializedName("carversion")
    private String carversion;
    @SerializedName("myear")
    private String myear;
    @SerializedName("OdometerReading")
    private String OdometerReading;
    @SerializedName("color")
    private String color;
    @SerializedName("km")
    private String km;
    @SerializedName("pricefrom")
    private String pricefrom;
    @SerializedName("id")
    private String id;
    @SerializedName("dealer_id")
    private String dealer_id;

    public int getMake_id() {
        return make_id;
    }

    public void setMake_id(int make_id) {
        this.make_id = make_id;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
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

    public String getCarversion() {
        return carversion;
    }

    public void setCarversion(String carversion) {
        this.carversion = carversion;
    }

    public String getMyear() {
        return myear;
    }

    public void setMyear(String myear) {
        this.myear = myear;
    }

    public String getOdometerReading() {
        return OdometerReading;
    }

    public void setOdometerReading(String odometerReading) {
        OdometerReading = odometerReading;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPricefrom() {
        return pricefrom;
    }

    public void setPricefrom(String pricefrom) {
        this.pricefrom = pricefrom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDealer_id() {
        return dealer_id;
    }

    public void setDealer_id(String dealer_id) {
        this.dealer_id = dealer_id;
    }

}
