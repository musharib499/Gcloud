package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ankit on 9/1/15.
 */
public class InventoryModel implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("fuel_type")
    private String fuel_type;

    @SerializedName("pricefrom")
    private String pricefrom;
    @SerializedName("username")
    private String username;
    @SerializedName("make")
    private int make;
    @SerializedName("km")
    private String kmsDriven;
    @SerializedName("dealer_price")
    private String dealerPrice;
    @SerializedName("image_icon")
    private String imageIcon;
    @SerializedName("colour")
    private String color;
    @SerializedName("myear")
    private String year;
    @SerializedName("email")
    private String email;
    @SerializedName("hexCode")
    private String hexCode;
    @SerializedName("created_date")
    private String createdDate;
    @SerializedName("car_id")
    private String carId;
    @SerializedName("modelVersion")
    private String modelVersion;
    @SerializedName("shareText")
    private String shareText;
    @SerializedName("trustmarkCertified")
    private String trusmarkCertified = "1";

    public String getPricefrom() {
        return pricefrom;
    }

    public void setPricefrom(String pricefrom) {
        this.pricefrom = pricefrom;
    }

    public String getHexCode() {
        return hexCode;
    }

    public void setHexCode(String hexCode) {
        this.hexCode = hexCode;
    }

    public String getTrusmarkCertified() {
        return trusmarkCertified;
    }

    public void setTrusmarkCertified(String trusmarkCertified) {
        this.trusmarkCertified = trusmarkCertified;
    }

    public String getFuel_type() {
        return fuel_type;
    }

    public void setFuel_type(String fuel_type) {
        this.fuel_type = fuel_type;
    }

    public String getShareText() {
        return shareText;
    }

    public void setShareText(String shareText) {
        this.shareText = shareText;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getMake() {
        return make;
    }

    public void setMake(int make) {
        this.make = make;
    }

    public String getKmsDriven() {
        return kmsDriven;
    }

    public void setKmsDriven(String kmsDriven) {
        this.kmsDriven = kmsDriven;
    }

    public String getDealerPrice() {
        return dealerPrice;
    }

    public void setDealerPrice(String dealerPrice) {
        this.dealerPrice = dealerPrice;
    }

    public String getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(String imageIcon) {
        this.imageIcon = imageIcon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    @Override
    public String toString() {
        return "InventoryModel{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", make=" + make +
                ", kmsDriven='" + kmsDriven + '\'' +
                ", dealerPrice='" + dealerPrice + '\'' +
                ", imageIcon='" + imageIcon + '\'' +
                ", hexCode='" + hexCode + '\'' +
                ", color='" + color + '\'' +
                ", year='" + year + '\'' +
                ", email='" + email + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", carId='" + carId + '\'' +
                '}';
    }
}
