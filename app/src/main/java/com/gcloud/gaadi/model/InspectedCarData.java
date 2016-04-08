package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ankitgarg on 21/05/15.
 */
public class InspectedCarData implements Serializable {

    @SerializedName("regno")
    private String registrationNumber;

    @SerializedName("make")
    private String make;

    @SerializedName("model")
    private String model;

    @SerializedName("carversion")
    private String carVersion;

    @SerializedName("myear")
    private String mYear;

    @SerializedName("km")
    private String kmDriven;

    @SerializedName("colour")
    private String colour;

    @SerializedName("pricefrom")
    private String displayPrice;

    @SerializedName("certificationID")
    private String certificationId;

    @SerializedName("dealer_id")
    private String dealerId;

    @SerializedName("usedCarID")
    private String usedCarId;

    @SerializedName("mm")
    private String modelMonth;

    @SerializedName("fuel_type")
    private String fuelType;

    @SerializedName("owner")
    private String owner;

    @SerializedName("transmission")
    private String transmission;

    @SerializedName("regplace")
    private String registrationPlace;

    @SerializedName("listingDate")
    private String listingDate;

    @SerializedName("gaadi_id")
    private String gaadiId;

    @SerializedName("make_id")
    private int makeId;

    @SerializedName("imageIcon")
    private String imageIcon;

    @SerializedName("hexCode")
    private String hexCode;

    public String getHexCode() {
        return hexCode;
    }

    public void setHexCode(String hexCode) {
        this.hexCode = hexCode;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
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

    public String getCarVersion() {
        return carVersion;
    }

    public void setCarVersion(String carVersion) {
        this.carVersion = carVersion;
    }

    public String getmYear() {
        return mYear;
    }

    public void setmYear(String mYear) {
        this.mYear = mYear;
    }

    public String getKmDriven() {
        return kmDriven;
    }

    public void setKmDriven(String kmDriven) {
        this.kmDriven = kmDriven;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getDisplayPrice() {
        return displayPrice;
    }

    public void setDisplayPrice(String displayPrice) {
        this.displayPrice = displayPrice;
    }

    public String getCertificationId() {
        return certificationId;
    }

    public void setCertificationId(String certificationId) {
        this.certificationId = certificationId;
    }

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    public String getUsedCarId() {
        return usedCarId;
    }

    public void setUsedCarId(String usedCarId) {
        this.usedCarId = usedCarId;
    }

    public String getModelMonth() {
        return modelMonth;
    }

    public void setModelMonth(String modelMonth) {
        this.modelMonth = modelMonth;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getRegistrationPlace() {
        return registrationPlace;
    }

    public void setRegistrationPlace(String registrationPlace) {
        this.registrationPlace = registrationPlace;
    }

    public String getListingDate() {
        return listingDate;
    }

    public void setListingDate(String listingDate) {
        this.listingDate = listingDate;
    }

    public String getGaadiId() {
        return gaadiId;
    }

    public void setGaadiId(String gaadiId) {
        this.gaadiId = gaadiId;
    }

    public int getMakeId() {
        return makeId;
    }

    public void setMakeId(int makeId) {
        this.makeId = makeId;
    }

    public String getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(String imageIcon) {
        this.imageIcon = imageIcon;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InspectedCarData{");
        sb.append("registrationNumber='").append(registrationNumber).append('\'');
        sb.append(", make='").append(make).append('\'');
        sb.append(", model='").append(model).append('\'');
        sb.append(", carVersion='").append(carVersion).append('\'');
        sb.append(", mYear='").append(mYear).append('\'');
        sb.append(", kmDriven='").append(kmDriven).append('\'');
        sb.append(", colour='").append(colour).append('\'');
        sb.append(", displayPrice='").append(displayPrice).append('\'');
        sb.append(", certificationId='").append(certificationId).append('\'');
        sb.append(", dealerId='").append(dealerId).append('\'');
        sb.append(", usedCarId='").append(usedCarId).append('\'');
        sb.append(", modelMonth='").append(modelMonth).append('\'');
        sb.append(", fuelType='").append(fuelType).append('\'');
        sb.append(", owner='").append(owner).append('\'');
        sb.append(", transmission='").append(transmission).append('\'');
        sb.append(", registrationPlace='").append(registrationPlace).append('\'');
        sb.append(", listingDate='").append(listingDate).append('\'');
        sb.append(", gaadiId='").append(gaadiId).append('\'');
        sb.append(", makeId=").append(makeId);
        sb.append(", imageIcon='").append(imageIcon).append('\'');
        sb.append(", hexCode='").append(hexCode).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
