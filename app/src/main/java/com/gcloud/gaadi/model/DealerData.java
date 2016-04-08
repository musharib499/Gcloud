package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ankit on 29/12/14.
 */
public class DealerData implements Serializable {

    private long mId;

    @SerializedName("id")
    private String mDealerId;
    @SerializedName("showrooms")
    private ArrayList<ShowroomData> showrooms;
    @SerializedName("organization")
    private String organization;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("mobilesms")
    private String mobileSms;
    @SerializedName("used_car_dealer_id")
    private String ucdid;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("normal_password")
    private String dealerPassword;
    @SerializedName("city")
    private String dealerCity;
    @SerializedName("only_warranty_prg")
    private String warrantOnlyDealer;
    @SerializedName("upload_inventory_on_cardekho")
    private String cardekhoInventory;

    public ArrayList<ShowroomData> getShowrooms() {
        return showrooms;
    }

    public void setShowrooms(ArrayList<ShowroomData> showrooms) {
        this.showrooms = showrooms;
    }

    public String getCardekhoInventory() {
        return cardekhoInventory;
    }

    public void setCardekhoInventory(String cardekhoInventory) {
        this.cardekhoInventory = cardekhoInventory;
    }

    public String getWarrantOnlyDealer() {
        return warrantOnlyDealer;
    }

    public void setWarrantOnlyDealer(String warrantOnlyDealer) {
        this.warrantOnlyDealer = warrantOnlyDealer;
    }

    public String getDealerCity() {
        return dealerCity;
    }

    public void setDealerCity(String dealerCity) {
        this.dealerCity = dealerCity;
    }

    public String getmDealerId() {
        return mDealerId;
    }

    public void setmDealerId(String mDealerId) {
        this.mDealerId = mDealerId;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getUcdid() {
        return ucdid;
    }

    public void setUcdid(String ucdid) {
        this.ucdid = ucdid;
    }

    public String getDealerPassword() {
        return dealerPassword;
    }

    public void setDealerPassword(String dealerPassword) {
        this.dealerPassword = dealerPassword;
    }

    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public String getDealerId() {
        return mDealerId;
    }

    public void setDealerId(String mDealerId) {
        this.mDealerId = mDealerId;
    }

    public String getMobileSms() {
        return mobileSms;
    }

    public void setMobileSms(String mobileSms) {
        this.mobileSms = mobileSms;
    }

    public String getUCdid() {
        return ucdid;
    }

    public void setUCdid(String ucdid) {
        this.ucdid = ucdid;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @Override
    public String toString() {
        return "DealerData{" +
                "mId=" + mId +
                ", mDealerId='" + mDealerId + '\'' +
                ", organization='" + organization + '\'' +
                ", mUsername='" + mUsername + '\'' +
                ", mobileSms='" + mobileSms + '\'' +
                ", ucdid='" + ucdid + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", dealerPassword='" + dealerPassword + '\'' +
                ", dealerCity='" + dealerCity + '\'' +
                ", warrantOnlyDealer='" + warrantOnlyDealer + '\'' +
                ", cardekhoInventory='" + cardekhoInventory + '\'' +
                '}';
    }
}
