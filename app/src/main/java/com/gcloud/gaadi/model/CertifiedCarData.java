package com.gcloud.gaadi.model;

import android.graphics.Color;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

public class CertifiedCarData implements Serializable {

    @SerializedName("regno")
    private String regno;

    @SerializedName("make")
    private String make;

    @SerializedName("warranty_start_date")
    private String warrantyStartDate;

    @SerializedName("warranty_end_date")
    private String warrantyEndDate;

    @SerializedName("pricefrom")
    private String pricefrom;

    @SerializedName("id")
    private String id;

    @SerializedName("dealer_id")
    private String dealer_id;

    @SerializedName("warrantyTypeName")
    private String warrantyTypeName;

    @SerializedName("warranty_type")
    private String warrantyType;

    @SerializedName("warranty_status")
    private String warrantyStatus;

    @SerializedName("warranty_id")
    private String warranty_id;

    @SerializedName("imageIcon")
    private String imageIcon;

    @SerializedName("mm")
    private String mm;

    @SerializedName("fuel_type")
    private String fuel_type;

    @SerializedName("regcityrto")
    private String regcityrto;

    @SerializedName("owner")
    private String owner;

    @SerializedName("transmission")
    private String transmission;

    @SerializedName("regplace")
    private String regplace;

    @SerializedName("expiry_date")
    private String expiry_date;


    @SerializedName("listingDate")
    private String listingDate;


    @SerializedName("certification_date")
    private String certification_date;

    @SerializedName("gaadi_id")
    private String gaadi_id;

    @SerializedName("recommendedPackage")
    private String recommendedPackage;

    @SerializedName("usedCarID")
    private String usedCarID;

    @SerializedName("make_id")
    private int make_id;

    @SerializedName("cust_mobile")
    private String custMobile;
    @SerializedName("cust_name")
    private String custName;
    private String hexcode;
    @SerializedName("model")
    private String model;
    @SerializedName("carversion")
    private String carversion;
    @SerializedName("myear")
    private String myear;
    @SerializedName("OdometerReading")
    private String OdometerReading;
    @SerializedName("colour")
    private String colour;
    @SerializedName("km")
    private String km;
    @SerializedName("certificationID")
    private String certificationID;
    @SerializedName("recommendedPackageNames")
    private ArrayList<String> recommendedPackageNames;

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getWarrantyStartDate() {

        DateTimeFormatter dtf = DateTimeFormat
                .forPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = "";
        if (!warrantyStartDate.trim().equals("")) {
            try {
                DateTime date = dtf.parseDateTime(warrantyStartDate);

                formattedDate = date.getDayOfMonth() + " "
                        + date.monthOfYear().getAsShortText(Locale.ENGLISH) + ", "
                        + date.year().getAsShortText(Locale.ENGLISH);
            } catch (IllegalArgumentException e) {

            }
        }

        return formattedDate;

    }

    public void setWarrantyStartDate(String warrantyStartDate) {
        this.warrantyStartDate = warrantyStartDate;
    }

    public String getWarrantyEndDate() {

        DateTimeFormatter dtf = DateTimeFormat
                .forPattern("yyyy-MM-dd");
        String formattedDate = "";
        if (!warrantyEndDate.trim().equals("")) {
            try {
                DateTime date = dtf.parseDateTime(warrantyEndDate);

                formattedDate = date.getDayOfMonth() + " "
                        + date.monthOfYear().getAsShortText(Locale.ENGLISH) + ", "
                        + date.year().getAsShortText(Locale.ENGLISH);
            } catch (IllegalArgumentException e) {

            }
        }

        return formattedDate;

    }

    public void setWarrantyEndDate(String warrantyEndDate) {
        this.warrantyEndDate = warrantyEndDate;
    }

    public String getWarrantyType() {
        return warrantyType;
    }

    public void setWarrantyType(String warrantyType) {
        this.warrantyType = warrantyType;
    }

    public String getWarrantyTypeName() {
        return warrantyTypeName;
    }

    public void setWarrantyTypeName(String warrantyTypeName) {
        this.warrantyTypeName = warrantyTypeName;
    }

    public String getWarrantyStatus() {
        return warrantyStatus;
    }

    public void setWarrantyStatus(String warrantyStatus) {
        this.warrantyStatus = warrantyStatus;
    }

    public ArrayList<String> getRecommendedPackageNames() {
        return recommendedPackageNames;
    }

    public void setRecommendedPackageNames(ArrayList<String> recommendedPackageNames) {
        this.recommendedPackageNames = recommendedPackageNames;
    }

    public String getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(String imageIcon) {
        this.imageIcon = imageIcon;
    }

    public String getWarranty_id() {
        return warranty_id;
    }

    public void setWarranty_id(String warranty_id) {
        this.warranty_id = warranty_id;
    }

    public String getMm() {
        return mm;
    }

    public void setMm(String mm) {
        this.mm = mm;
    }

    public String getFuel_type() {
        return fuel_type;
    }

    public void setFuel_type(String fuel_type) {
        this.fuel_type = fuel_type;
    }

    public String getRegcityrto() {
        return regcityrto;
    }

    public void setRegcityrto(String regcityrto) {
        this.regcityrto = regcityrto;
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

    public String getRegplace() {
        return regplace;
    }

    public void setRegplace(String regplace) {
        this.regplace = regplace;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getListingDate() {
        return listingDate;
    }

    public void setListingDate(String listingDate) {
        this.listingDate = listingDate;
    }

    public String getCertification_date() {

        DateTimeFormatter dtf = DateTimeFormat
                .forPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = "";
        if (!certification_date.trim().equals("")) {
            try {
                DateTime date = dtf.parseDateTime(certification_date);

                formattedDate = date.getDayOfMonth() + " "
                        + date.monthOfYear().getAsShortText(Locale.ENGLISH) + ", "
                        + date.year().getAsShortText(Locale.ENGLISH) + " at "
                        + date.hourOfDay().getAsShortText(Locale.ENGLISH) + ":"
                        + date.minuteOfHour().getAsShortText(Locale.ENGLISH);
            } catch (IllegalArgumentException e) {

            }
        }

        return formattedDate;
    }

    public void setCertification_date(String certification_date) {
        this.certification_date = certification_date;
    }

    public String getGaadi_id() {
        return gaadi_id;
    }

    public void setGaadi_id(String gaadi_id) {
        this.gaadi_id = gaadi_id;
    }

    public String getRecommendedPackage() {
        return recommendedPackage;
    }

    public void setRecommendedPackage(String recommendedPackage) {
        this.recommendedPackage = recommendedPackage;
    }

    public String getUsedCarID() {
        return usedCarID;
    }

    public void setUsedCarID(String usedCarID) {
        this.usedCarID = usedCarID;
    }

    public int getMake_id() {
        return make_id;
    }

    public void setMake_id(int make_id) {
        this.make_id = make_id;
    }


    public String getCustMobile() {
        return custMobile;
    }

    public void setCustMobile(String custMobile) {
        this.custMobile = custMobile;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getCertificationID() {
        return certificationID;
    }

    public void setCertificationID(String certificationID) {
        this.certificationID = certificationID;
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
        return colour;
    }

    public void setColor(String color) {
        this.colour = color;
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

    public int getHexcode() {
        int colorCode = Color.parseColor("#ffffff");
        try {
            colorCode = Color.parseColor(hexcode == null || hexcode.isEmpty() ? "#ffffff" : hexcode);
        } catch (IllegalArgumentException ex) {

        }
        return colorCode;
    }

    public void setHexcode(String hexcode) {
        this.hexcode = hexcode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CertifiedCarData{");
        sb.append("regno='").append(regno).append('\'');
        sb.append(", make='").append(make).append('\'');
        sb.append(", warrantyStartDate='").append(warrantyStartDate).append('\'');
        sb.append(", warrantyEndDate='").append(warrantyEndDate).append('\'');
        sb.append(", pricefrom='").append(pricefrom).append('\'');
        sb.append(", id='").append(id).append('\'');
        sb.append(", dealer_id='").append(dealer_id).append('\'');
        sb.append(", warrantyTypeName='").append(warrantyTypeName).append('\'');
        sb.append(", warrantyType='").append(warrantyType).append('\'');
        sb.append(", warrantyStatus='").append(warrantyStatus).append('\'');
        sb.append(", warranty_id='").append(warranty_id).append('\'');
        sb.append(", imageIcon='").append(imageIcon).append('\'');
        sb.append(", mm='").append(mm).append('\'');
        sb.append(", fuel_type='").append(fuel_type).append('\'');
        sb.append(", regcityrto='").append(regcityrto).append('\'');
        sb.append(", owner='").append(owner).append('\'');
        sb.append(", transmission='").append(transmission).append('\'');
        sb.append(", regplace='").append(regplace).append('\'');
        sb.append(", expiry_date='").append(expiry_date).append('\'');
        sb.append(", listingDate='").append(listingDate).append('\'');
        sb.append(", certification_date='").append(certification_date).append('\'');
        sb.append(", gaadi_id='").append(gaadi_id).append('\'');
        sb.append(", recommendedPackage='").append(recommendedPackage).append('\'');
        sb.append(", usedCarID='").append(usedCarID).append('\'');
        sb.append(", make_id=").append(make_id);
        sb.append(", custMobile='").append(custMobile).append('\'');
        sb.append(", custName='").append(custName).append('\'');
        sb.append(", hexcode='").append(hexcode).append('\'');
        sb.append(", model='").append(model).append('\'');
        sb.append(", carversion='").append(carversion).append('\'');
        sb.append(", myear='").append(myear).append('\'');
        sb.append(", OdometerReading='").append(OdometerReading).append('\'');
        sb.append(", colour='").append(colour).append('\'');
        sb.append(", km='").append(km).append('\'');
        sb.append(", certificationID='").append(certificationID).append('\'');
        sb.append(", recommendedPackageNames=").append(recommendedPackageNames);
        sb.append('}');
        return sb.toString();
    }
}
