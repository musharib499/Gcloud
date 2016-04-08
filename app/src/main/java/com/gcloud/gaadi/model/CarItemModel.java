package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Priya on 26-05-2015.
 */
public class CarItemModel implements Serializable {


    @SerializedName("regno")
    private String regno = "";

    @SerializedName("model")
    private String model;

    @SerializedName("make")
    private String make;

    @SerializedName("carversion")
    private String carversion;

    @SerializedName("myear")
    private String myear;

    @SerializedName("km")
    private String km;

    @SerializedName("colour")
    private String colour;

    @SerializedName("pricefrom")
    private String pricefrom;

    @SerializedName("certificationID")
    private String certificationID;

    @SerializedName("customer_name")
    private String customarName;

    @SerializedName("loan_approval_status")
    private String loanStatus;

    public String getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }

    public String getCustomarName() {
        return customarName;
    }

    public void setCustomarName(String customarName) {
        this.customarName = customarName;
    }

    @SerializedName("certification_status")
    private String certification_status;

    @SerializedName("dealer_id")
    private String dealer_id;

    @SerializedName("usedCarID")
    private String usedCarID;

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

    @SerializedName("listingDate")
    private String listingDate;

    @SerializedName("certification_date")
    private String certification_date;

    @SerializedName("expiry_date")
    private String expiry_date;

    @SerializedName("gaadi_id")
    private String gaadi_id;

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
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

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getPricefrom() {
        return pricefrom;
    }

    public void setPricefrom(String pricefrom) {
        this.pricefrom = pricefrom;
    }

    public String getCertificationID() {
        return certificationID;
    }

    public void setCertificationID(String certificationID) {
        this.certificationID = certificationID;
    }

    public String getCertification_status() {
        return certification_status;
    }

    public void setCertification_status(String certification_status) {
        this.certification_status = certification_status;
    }

    public String getDealer_id() {
        return dealer_id;
    }

    public void setDealer_id(String dealer_id) {
        this.dealer_id = dealer_id;
    }

    public String getUsedCarID() {
        return usedCarID;
    }

    public void setUsedCarID(String usedCarID) {
        this.usedCarID = usedCarID;
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

    public String getListingDate() {
        return listingDate;
    }

    public void setListingDate(String listingDate) {
        this.listingDate = listingDate;
    }

    public String getCertification_date() {
        return certification_date;
    }

    public void setCertification_date(String certification_date) {
        this.certification_date = certification_date;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
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

    public int getMake_id() {
        return make_id;
    }

    public void setMake_id(int make_id) {
        this.make_id = make_id;
    }

    public String getCertificationReportUrl() {
        return certificationReportUrl;
    }

    public void setCertificationReportUrl(String certificationReportUrl) {
        this.certificationReportUrl = certificationReportUrl;
    }

    public String getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(String imageIcon) {
        this.imageIcon = imageIcon;
    }

    @SerializedName("recommendedPackage")
    private String recommendedPackage;

    @SerializedName("make_id")
    private int make_id;

    @SerializedName("certificationReportUrl")
    private String certificationReportUrl;


    @SerializedName("imageIcon")
    private String imageIcon;

    @SerializedName("bank_offers")
    private ArrayList<LoanOffers> bank_offers;

    public ArrayList<LoanOffers> getBank_offers() {
        return bank_offers;
    }

    public void setBank_offers(ArrayList<LoanOffers> bank_offers) {
        this.bank_offers = bank_offers;
    }

    @Override
    public String toString() {
        return "CarItemModel{" +
                "regno='" + regno + '\'' +
                ", model='" + model + '\'' +
                ", make='" + make + '\'' +
                ", carversion='" + carversion + '\'' +
                ", myear='" + myear + '\'' +
                ", km='" + km + '\'' +
                ", colour='" + colour + '\'' +
                ", pricefrom='" + pricefrom + '\'' +
                ", certificationID='" + certificationID + '\'' +
                ", certification_status='" + certification_status + '\'' +
                ", dealer_id='" + dealer_id + '\'' +
                ", usedCarID='" + usedCarID + '\'' +
                ", mm='" + mm + '\'' +
                ", fuel_type='" + fuel_type + '\'' +
                ", regcityrto='" + regcityrto + '\'' +
                ", owner='" + owner + '\'' +
                ", transmission='" + transmission + '\'' +
                ", regplace='" + regplace + '\'' +
                ", listingDate='" + listingDate + '\'' +
                ", certification_date='" + certification_date + '\'' +
                ", expiry_date='" + expiry_date + '\'' +
                ", gaadi_id='" + gaadi_id + '\'' +
                ", recommendedPackage='" + recommendedPackage + '\'' +
                ", make_id=" + make_id +
                ", certificationReportUrl='" + certificationReportUrl + '\'' +
                ", imageIcon='" + imageIcon + '\'' +
                ", bank_offers=" + bank_offers +
                '}';
    }
}
