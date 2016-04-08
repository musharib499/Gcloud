package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ankit on 17/11/14.
 */
public class UserDetail implements Serializable {

    @SerializedName("sfa_user_id")
    private int sfaUserId;

    @SerializedName("name")
    private String name;

    @SerializedName("inventorytolist")
    private String numInventoriesList;

    @SerializedName("normal_password")
    private String password;

    @SerializedName("dealer_mobile")
    private String mobile;

    @SerializedName("username")
    private String username;

    @SerializedName("listing_type")
    private String listingType;

    @SerializedName("pincode")
    private String pincode;

    @SerializedName("email")
    private String email;

    @SerializedName("access_mod")
    private String access;

    @SerializedName("address")
    private String address;

    @SerializedName("used_car_dealer_id")
    private String ucdealerId;

    @SerializedName("locality_id")
    private String localityId;

    @SerializedName("mobilesms")
    private String mobileSMS;

    @SerializedName("id")
    private String id;

    @SerializedName("organization")
    private String organization;

    @SerializedName("city")
    private String dealerCity;

    @SerializedName("only_warranty_prg")
    private String warrantyOnlyDealer;

    @SerializedName("upload_inventory_on_cardekho")
    private String cardekhoInventory;

    @SerializedName("is_seller")
    private int isSeller;

    public int getSfaUserId() {
        return sfaUserId;
    }

    public void setSfaUserId(int sfaUserId) {
        this.sfaUserId = sfaUserId;
    }

    public int getIsSeller() {
        return isSeller;
    }

    public void setIsSeller(int isSeller) {
        this.isSeller = isSeller;
    }

    public String getCardekhoInventory() {
        return cardekhoInventory;
    }

    public void setCardekhoInventory(String cardekhoInventory) {
        this.cardekhoInventory = cardekhoInventory;
    }

    public String getWarrantyOnlyDealer() {
        return warrantyOnlyDealer;
    }

    public void setWarrantyOnlyDealer(String warrantyOnlyDealer) {
        this.warrantyOnlyDealer = warrantyOnlyDealer;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDealerCity() {
        return dealerCity;
    }

    public void setDealerCity(String dealerCity) {
        this.dealerCity = dealerCity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumInventoriesList() {
        return numInventoriesList;
    }

    public void setNumInventoriesList(String numInventoriesList) {
        this.numInventoriesList = numInventoriesList;
    }

    public String getDealerPassword() {
        return password;
    }

    public void setDealerPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getListingType() {
        return listingType;
    }

    public void setListingType(String listingType) {
        this.listingType = listingType;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUcdealerId() {
        return ucdealerId;
    }

    public void setUcdealerId(String ucdealerId) {
        this.ucdealerId = ucdealerId;
    }

    public String getLocalityId() {
        return localityId;
    }

    public void setLocalityId(String localityId) {
        this.localityId = localityId;
    }

    public String getMobileSMS() {
        return mobileSMS;
    }

    public void setMobileSMS(String mobileSMS) {
        this.mobileSMS = mobileSMS;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @Override
    public String toString() {
        return "UserDetail{" +
                "name='" + name + '\'' +
                ", numInventoriesList='" + numInventoriesList + '\'' +
                ", password='" + password + '\'' +
                ", mobile='" + mobile + '\'' +
                ", username='" + username + '\'' +
                ", listingType='" + listingType + '\'' +
                ", pincode='" + pincode + '\'' +
                ", email='" + email + '\'' +
                ", access='" + access + '\'' +
                ", address='" + address + '\'' +
                ", ucdealerId='" + ucdealerId + '\'' +
                ", localityId='" + localityId + '\'' +
                ", mobileSMS='" + mobileSMS + '\'' +
                ", id='" + id + '\'' +
                ", organization='" + organization + '\'' +
                '}';
    }
}
