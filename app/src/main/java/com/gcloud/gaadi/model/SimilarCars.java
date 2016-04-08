package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Seema Pandit on 14-01-2015.
 */
public class SimilarCars implements Serializable {

    @SerializedName("make")
    private String make;
    @SerializedName("model")
    private String model;
    @SerializedName("car_id")
    private String car_id;
    @SerializedName("colour")
    private String colour;
    @SerializedName("Kms")
    private String Kms;
    @SerializedName("price")
    private String price;
    @SerializedName("myear")
    private String myear;
    @SerializedName("websiteUrl")
    private String websiteUrl;
    @SerializedName("img_icon")
    private String img_icon;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getKms() {
        return Kms;
    }

    public void setKms(String kms) {
        Kms = kms;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMyear() {
        return myear;
    }

    public void setMyear(String myear) {
        this.myear = myear;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getImg_icon() {
        return img_icon;
    }

    public void setImg_icon(String img_icon) {
        this.img_icon = img_icon;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }
}
