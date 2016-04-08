package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Priya on 27-08-2015.
 */
public class SellerLeadsCarModel implements Serializable {

    @SerializedName("make")
    private String make;

    @SerializedName("model")
    private String model;

    @SerializedName("variant")
    private String variant;

    @SerializedName("color")
    private String color;

    @SerializedName("color_hex")
    private String colorHex;

    @SerializedName("km")
    private String kmsDriven;

    @SerializedName("year")
    private String year;

    @SerializedName("fuel_type")
    private String fuel_type;

    @SerializedName("dateTime")
    private String dateTime;

    @SerializedName("price")
    private String price;

    @SerializedName("makeID")
    private int makeID;

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

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getKmsDriven() {
        return kmsDriven;
    }

    public void setKmsDriven(String kmsDriven) {
        this.kmsDriven = kmsDriven;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getFuel_type() {
        return fuel_type;
    }

    public void setFuel_type(String fuel_type) {
        this.fuel_type = fuel_type;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getMakeID() {
        return makeID;
    }

    public void setMakeID(int makeID) {
        this.makeID = makeID;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }
}
