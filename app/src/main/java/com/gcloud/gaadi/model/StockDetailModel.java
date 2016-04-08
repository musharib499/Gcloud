package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ankit on 30/12/14.
 */
public class StockDetailModel implements Serializable {

    @SerializedName("id")
    private String stockId;

    @SerializedName("modelVersion")
    private String modelVersion;
    @SerializedName("hexCode")
    private String hexCode;
    @SerializedName("color_hex")
    private String colorHexCode;

    @SerializedName("status")
    private String status;
    @SerializedName("makeID")
    private int makeid;
    @SerializedName("make")
    private String make;
    @SerializedName("model")
    private String model;
    @SerializedName("carversion")
    private String version;
    @SerializedName("mm")
    private String modelMonth;
    @SerializedName("myear")
    private String modelYear;
    @SerializedName("regplace")
    private String registrationPlace;
    @SerializedName("regno")
    private String registrationNumber;
    @SerializedName("km")
    private String kms;
    @SerializedName("pricefrom")
    private String stockPrice;
    @SerializedName("created_date")
    private String createdDate;
    @SerializedName("updated_date")
    private String updatedDate;
    @SerializedName("certification")
    private String certifiedBy;
    @SerializedName("sell_dealer")
    private String sellToDealer;
    @SerializedName("dealer_price")
    private String d2dPrice;
    @SerializedName("d2dmobile")
    private String d2dMobile;
    @SerializedName("arr_images")
    private ArrayList<String> arrayImages;
    @SerializedName("fuel_type")
    private String fuelType;
    @SerializedName("owner")
    private String ownerNumber;
    @SerializedName("active")
    private String active;
    @SerializedName("tax")
    private String tax;
    @SerializedName("insurance")
    private String insurance;
    @SerializedName("month")
    private String insuranceMonth;
    @SerializedName("year")
    private String year;
    @SerializedName("showroomid")
    private String showroomId;
    @SerializedName("dealer_id")
    private String dealerId;
    @SerializedName("gaadi_id")
    private String gaadiId;
    @SerializedName("shareText")
    private String shareText;
    @SerializedName("colour")
    private String colorValue;
    @SerializedName("cngFitted")
    private String cngFitted;
    @SerializedName("totalLeads")
    private String totalLeads;
    @SerializedName("editKm")
    private String editKm;
    @SerializedName("editPrice")
    private String editPrice;
    @SerializedName("isEditable")
    private String editable;
    @SerializedName("editDealerPrice")
    private String editDealerPrice;
    @SerializedName("showroomName")
    private String showroom;
    @SerializedName("ispremium")
    private String premiumInventory;
    @SerializedName("certificationID")
    private String certificationId;
    @SerializedName("chassisNumber")
    private String chassisNumber;
    @SerializedName("isInspected")
    private int inspected;
    @SerializedName("trustmarkCertified")
    private int trustmarkCertified;

    @SerializedName("odd_even")
    private int evenOdd ;

    public int getEvenOdd() {
        return evenOdd;
    }

    public void setEvenOdd(int evenOdd) {
        this.evenOdd = evenOdd;
    }

    public String getHexCode() {
        return hexCode;
    }

    public String getColorHexCode() {
        return colorHexCode;
    }

    public void setColorHexCode(String colorHexCode) {
        this.colorHexCode = colorHexCode;
    }

    public void setHexCode(String hexCode) {
        this.hexCode = hexCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getInspected() {
        return inspected;
    }

    public void setInspected(int inspected) {
        this.inspected = inspected;
    }

    public int getTrustmarkCertified() {
        return trustmarkCertified;
    }

    public void setTrustmarkCertified(int trustmarkCertified) {
        this.trustmarkCertified = trustmarkCertified;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getCertificationId() {
        return certificationId;
    }

    public void setCertificationId(String certificationId) {
        this.certificationId = certificationId;
    }

    public String getPremiumInventory() {
        return premiumInventory;
    }

    public void setPremiumInventory(String premiumInventory) {
        this.premiumInventory = premiumInventory;
    }

    public String getShowroom() {
        return showroom;
    }

    public void setShowroom(String showroom) {
        this.showroom = showroom;
    }

    public String getEditDealerPrice() {
        return editDealerPrice;
    }

    public void setEditDealerPrice(String editDealerPrice) {
        this.editDealerPrice = editDealerPrice;
    }

    public String getCngFitted() {
        return cngFitted;
    }

    public void setCngFitted(String cngFitted) {
        this.cngFitted = cngFitted;
    }

    public String getEditable() {
        return editable;
    }

    public void setEditable(String editable) {
        this.editable = editable;
    }

    public String getInsuranceMonth() {
        return insuranceMonth;
    }

    public void setInsuranceMonth(String insuranceMonth) {
        this.insuranceMonth = insuranceMonth;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getEditKm() {
        return editKm;
    }

    public void setEditKm(String editKm) {
        this.editKm = editKm;
    }

    public String getEditPrice() {
        return editPrice;
    }

    public void setEditPrice(String editPrice) {
        this.editPrice = editPrice;
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

    public String getModelMonth() {
        return modelMonth;
    }

    public void setModelMonth(String modelMonth) {
        this.modelMonth = modelMonth;
    }

    public String getTotalLeads() {
        return totalLeads;
    }

    public void setTotalLeads(String totalLeads) {
        this.totalLeads = totalLeads;
    }

    public String getColorValue() {
        return colorValue;
    }

    public void setColorValue(String colorValue) {
        this.colorValue = colorValue;
    }

    public String getShareText() {
        return shareText;
    }

    public void setShareText(String shareText) {
        this.shareText = shareText;
    }

    public String getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(String stockPrice) {
        this.stockPrice = stockPrice;
    }

    public int getMakeid() {
        return makeid;
    }

    public void setMakeid(int makeid) {
        this.makeid = makeid;
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

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getModelYear() {
        return modelYear;
    }

    public void setModelYear(String modelYear) {
        this.modelYear = modelYear;
    }

    public String getRegistrationPlace() {
        return registrationPlace;
    }

    public void setRegistrationPlace(String registrationPlace) {
        this.registrationPlace = registrationPlace;
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

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getCertifiedBy() {
        return certifiedBy;
    }

    public void setCertifiedBy(String certifiedBy) {
        this.certifiedBy = certifiedBy;
    }

    public String getSellToDealer() {
        return sellToDealer;
    }

    public void setSellToDealer(String sellToDealer) {
        this.sellToDealer = sellToDealer;
    }

    public String getD2dPrice() {
        return d2dPrice;
    }

    public void setD2dPrice(String d2dPrice) {
        this.d2dPrice = d2dPrice;
    }

    public String getD2dMobile() {
        return d2dMobile;
    }

    public void setD2dMobile(String d2dMobile) {
        this.d2dMobile = d2dMobile;
    }

    public ArrayList<String> getArrayImages() {
        return arrayImages;
    }

    public void setArrayImages(ArrayList<String> arrayImages) {
        this.arrayImages = arrayImages;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getOwnerNumber() {
        return ownerNumber;
    }

    public void setOwnerNumber(String ownerNumber) {
        this.ownerNumber = ownerNumber;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getShowroomId() {
        return showroomId;
    }

    public void setShowroomId(String showroomId) {
        this.showroomId = showroomId;
    }

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    public String getGaadiId() {
        return gaadiId;
    }

    public void setGaadiId(String gaadiId) {
        this.gaadiId = gaadiId;
    }

    @Override
    public String toString() {
        return "StockDetailModel{" +
                "stockId='" + stockId + '\'' +
                ", modelVersion='" + modelVersion + '\'' +
                ", makeid=" + makeid +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", version='" + version + '\'' +
                ", modelMonth='" + modelMonth + '\'' +
                ", modelYear='" + modelYear + '\'' +
                ", registrationPlace='" + registrationPlace + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", kms='" + kms + '\'' +
                ", stockPrice='" + stockPrice + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", updatedDate='" + updatedDate + '\'' +
                ", certifiedBy='" + certifiedBy + '\'' +
                ", sellToDealer='" + sellToDealer + '\'' +
                ", d2dPrice='" + d2dPrice + '\'' +
                ", d2dMobile='" + d2dMobile + '\'' +
                ", arrayImages=" + arrayImages +
                ", fuelType='" + fuelType + '\'' +
                ", ownerNumber='" + ownerNumber + '\'' +
                ", active='" + active + '\'' +
                ", hexCode='" + hexCode + '\'' +
                ", tax='" + tax + '\'' +
                ", insurance='" + insurance + '\'' +
                ", insuranceMonth='" + insuranceMonth + '\'' +
                ", year='" + year + '\'' +
                ", showroomId='" + showroomId + '\'' +
                ", dealerId='" + dealerId + '\'' +
                ", gaadiId='" + gaadiId + '\'' +
                ", shareText='" + shareText + '\'' +
                ", colorValue='" + colorValue + '\'' +
                ", cngFitted='" + cngFitted + '\'' +
                ", totalLeads='" + totalLeads + '\'' +
                ", editKm='" + editKm + '\'' +
                ", editPrice='" + editPrice + '\'' +
                ", editable='" + editable + '\'' +
                ", editDealerPrice='" + editDealerPrice + '\'' +
                ", showroom='" + showroom + '\'' +
                ", premiumInventory='" + premiumInventory + '\'' +
                '}';
    }
}
