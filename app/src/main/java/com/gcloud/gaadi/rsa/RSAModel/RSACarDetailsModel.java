package com.gcloud.gaadi.rsa.RSAModel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by shivani on 9/3/16.
 */
public class RSACarDetailsModel implements Serializable {

    @SerializedName("car_id")
    private String stockId;
    @SerializedName("make")
    private String makeName;
    @SerializedName("model")
    private String modelName;
    @SerializedName("carversion")
    private String carVersion;
    @SerializedName("hexCode")
    private String hexCode;
    @SerializedName("make_id")
    private String makeID;
    @SerializedName("model_id")
    private String modelID;
    @SerializedName("version_id")
    private String versionID;
    @SerializedName("mmonth")
    private String modelMonth;
    @SerializedName("myear")
    private String modelYear;
    @SerializedName("regno")
    private String registrationNumber;
    @SerializedName("pricefrom")
    private String stockPrice;
    @SerializedName("km")
    private String kms;
    @SerializedName("created_date")
    private String createdDate;
    @SerializedName("fuel_type")
    private String fuelType;
    @SerializedName("image_icon")
    private String imageIcon;
    @SerializedName("colour")
    private String colorValue;

    public String getMakeName() {
        return makeName;
    }

    public void setMakeName(String makeName) {
        this.makeName = makeName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getCarVersion() {
        return carVersion;
    }

    public void setCarVersion(String carVersion) {
        this.carVersion = carVersion;
    }

    public String getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(String imageIcon) {
        this.imageIcon = imageIcon;

    }

    public String getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(String stockPrice) {
        this.stockPrice = stockPrice;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getHexCode() {
        return hexCode;
    }


    public void setHexCode(String hexCode) {
        this.hexCode = hexCode;
    }

    public String getModel() {
        return modelID;
    }

    public void setModel(String modelID) {
        this.modelID = modelID;
    }

    public String getVersion() {
        return versionID;
    }

    public void setVersion(String versionID) {
        this.versionID = versionID;
    }

    public String getModelMonth() {
        return modelMonth;
    }

    public void setModelMonth(String modelMonth) {
        this.modelMonth = modelMonth;
    }


    public String getColorValue() {
        return colorValue;
    }

    public void setColorValue(String colorValue) {
        this.colorValue = colorValue;
    }


    public String getMakeid() {
        return makeID;
    }

    public void setMakeid(String makeID) {
        this.makeID = makeID;
    }

    public String getKms() {
        return kms;
    }

    public void setKms(String kms) {
        this.kms = kms;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public String getModelYear() {
        return modelYear;
    }

    public void setModelYear(String modelYear) {
        this.modelYear = modelYear;
    }


    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "RSACarDetailsModel{" +
                "stockId='" + stockId + '\'' +
                ", hexCode='" + hexCode + '\'' +
                ", makeid=" + makeID +
                ", model='" + modelID + '\'' +
                ", version='" + versionID + '\'' +
                ", modelMonth='" + modelMonth + '\'' +
                ", modelYear='" + modelYear + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", priceFrom='" + stockPrice + '\'' +
                ", kms='" + kms + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", fuelType='" + fuelType + '\'' +
                ", imageIcon='" + imageIcon + '\'' +
                ", colorValue='" + colorValue + '\'' +
                '}';
    }
}
