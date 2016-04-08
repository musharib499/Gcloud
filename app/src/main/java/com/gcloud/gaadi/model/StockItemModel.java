package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by ankit on 31/12/14.
 */
public class StockItemModel implements Serializable {

    @SerializedName("id")
    private String stockId;

    @SerializedName("dealer_platform")
    private String dealerPlatform;

    @SerializedName("finance_list")
    private String financeList;

    @SerializedName("changetime")
    private String changeTime;

    @SerializedName("car_certification")
    private String carCertification;

    @SerializedName("username")
    private String userName;

    @SerializedName("make")
    private int make;

    @SerializedName("modelVersion")
    private String modelVersion;

    @SerializedName("km")
    private String kms;

    @SerializedName("pricefrom")
    private String stockPrice;

    @SerializedName("colour")
    private String color;

    @SerializedName("mm")
    private String mm;

    @SerializedName("fuel_type")
    private String fuelType;

    @SerializedName("regno")
    private String registrationNumber;

    @SerializedName("myear")
    private String modelYear;

    @SerializedName("mobile")
    private String d2dMobile;
    @SerializedName("email")
    private String d2dEmail;

    @SerializedName("gaadi_id")
    private String gaadiId;

    @SerializedName("created_date")
    private String createdDate;

    @SerializedName("updated_date")
    private String updatedDate;


    @SerializedName("domain")
    private String domain;

    @SerializedName("hexCode")
    private String hexCode;


    @SerializedName("trustmarkCertified")
    private String trusmarkCertified = "1";

    @SerializedName("active")
    private String active;

    @SerializedName("shareText")
    private String shareText;

    @SerializedName("totalLeads")
    private String totalLeads;


    @SerializedName("showroomid")
    private String showroomId;

    @SerializedName("car_id")
    private String carId;


    @SerializedName("image_icon")
    private String imageIcon;

    @SerializedName("dealer_price")
    private String dealerPrice;

    @SerializedName("km_int")
    private int kmSort;

    @SerializedName("pricefrom_int")
    private int priceSort;

    @SerializedName("area_of_cover")
    private String areaOfCover;

    @SerializedName("make_name")
    private String makeName;

    @SerializedName("model_name")
    private String modelName;

    @SerializedName("version_name")
    private String versionName;


    public String getMakeName() {
        return makeName;
    }

    public String getModelName() {
        return modelName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setMakeName(String makeName) {
        this.makeName = makeName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getKmSort() {
        return kmSort;
    }

    public void setKmSort(int kmSort) {
        this.kmSort = kmSort;
    }

    public int getPriceSort() {
        return priceSort;
    }

    public void setPriceSort(int priceSort) {
        this.priceSort = priceSort;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public String getDealerPlatform() {
        return dealerPlatform;
    }

    public void setDealerPlatform(String dealerPlatform) {
        this.dealerPlatform = dealerPlatform;
    }

    public String getFinanceList() {
        return financeList;
    }

    public void setFinanceList(String financeList) {
        this.financeList = financeList;
    }

    public String getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(String changeTime) {
        this.changeTime = changeTime;
    }

    public String getCarCertification() {
        return carCertification;
    }

    public void setCarCertification(String carCertification) {
        this.carCertification = carCertification;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getMake() {
        return make;
    }

    public void setMake(int make) {
        this.make = make;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getKms() {
        return kms;
    }

    public void setKms(String kms) {
        this.kms = kms;
    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMm() {
        return mm;
    }

    public void setMm(String mm) {
        this.mm = mm;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getModelYear() {
        return modelYear;
    }

    public void setModelYear(String modelYear) {
        this.modelYear = modelYear;
    }

    public String getD2dMobile() {
        return d2dMobile;
    }

    public void setD2dMobile(String d2dMobile) {
        this.d2dMobile = d2dMobile;
    }

    public String getD2dEmail() {
        return d2dEmail;
    }

    public void setD2dEmail(String d2dEmail) {
        this.d2dEmail = d2dEmail;
    }

    public String getGaadiId() {
        return gaadiId;
    }

    public void setGaadiId(String gaadiId) {
        this.gaadiId = gaadiId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getShareText() {
        return shareText;
    }

    public void setShareText(String shareText) {
        this.shareText = shareText;
    }

    public String getTotalLeads() {
        return totalLeads;
    }

    public void setTotalLeads(String totalLeads) {
        this.totalLeads = totalLeads;
    }

    public String getDealerPrice() {
        return dealerPrice;
    }

    public void setDealerPrice(String dealerPrice) {
        this.dealerPrice = dealerPrice;
    }

    public String getShowroomId() {
        return showroomId;
    }

    public void setShowroomId(String showroomId) {
        this.showroomId = showroomId;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
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

    public String getAreaOfCover() {
        return areaOfCover;
    }

    public void setAreaOfCover(String areaOfCover) {
        this.areaOfCover = areaOfCover;
    }

    public String getInspectedCar() {
        return inspectedCar;
    }

    public void setInspectedCar(String inspectedCar) {
        this.inspectedCar = inspectedCar;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @SerializedName("inspected_car")
    private String inspectedCar;

    @SerializedName("certification")
    private String certification;

    private String updateTime;

    private int type;

    @Override
    public String toString() {
        return "StockItemModel{" +
                "stockId='" + stockId + '\'' +
                ", dealerPlatform='" + dealerPlatform + '\'' +
                ", financeList='" + financeList + '\'' +
                ", changeTime='" + changeTime + '\'' +
                ", carCertification='" + carCertification + '\'' +
                ", userName='" + userName + '\'' +
                ", make=" + make +
                ", modelVersion='" + modelVersion + '\'' +
                ", kms='" + kms + '\'' +
                ", stockPrice='" + stockPrice + '\'' +
                ", color='" + color + '\'' +
                ", mm='" + mm + '\'' +
                ", fuelType='" + fuelType + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", modelYear='" + modelYear + '\'' +
                ", d2dMobile='" + d2dMobile + '\'' +
                ", d2dEmail='" + d2dEmail + '\'' +
                ", gaadiId='" + gaadiId + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", updatedDate='" + updatedDate + '\'' +
                ", domain='" + domain + '\'' +
                ", hexCode='" + hexCode + '\'' +
                ", trusmarkCertified='" + trusmarkCertified + '\'' +
                ", active='" + active + '\'' +
                ", shareText='" + shareText + '\'' +
                ", totalLeads='" + totalLeads + '\'' +
                ", showroomId='" + showroomId + '\'' +
                ", carId='" + carId + '\'' +
                ", imageIcon='" + imageIcon + '\'' +
                ", dealerPrice='" + dealerPrice + '\'' +
                ", areaOfCover='" + areaOfCover + '\'' +
                ", inspectedCar='" + inspectedCar + '\'' +
                ", certification='" + certification + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", type=" + type +
                '}';
    }
}
