package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Seema Pandit on 14-01-2015.
 */
public class AllCars implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("makeID")
    private int makeID;

    @SerializedName("make")
    private String make;

    @SerializedName("modelVersion")
    private String modelVersion;

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
    @SerializedName("hexCode")
    private String hexCode;
    @SerializedName("shareText")
    private String shareText;
    @SerializedName("img_icon")
    private String img_icon;
    @SerializedName("trustmarkCertified")
    private String trusmarkCertified = "1";

    public String getHexCode() {
        return hexCode;
    }

    public void setHexCode(String hexCode) {
        this.hexCode = hexCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrusmarkCertified() {
        return trusmarkCertified;
    }

    public void setTrusmarkCertified(String trusmarkCertified) {
        this.trusmarkCertified = trusmarkCertified;
    }

    public int getMakeID() {
        return makeID;
    }


    public void setMakeID(int makeID) {
        this.makeID = makeID;
    }


    public String getMake() {
        return make;
    }


    public void setMake(String make) {
        this.make = make;
    }


    public String getModelVersion() {
        return modelVersion;
    }


    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
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


    public String getShareText() {
        return shareText;
    }


    public void setShareText(String shareText) {
        this.shareText = shareText;
    }


    public String getImg_icon() {
        return img_icon;
    }


    public void setImg_icon(String img_icon) {
        this.img_icon = img_icon;
    }


}
