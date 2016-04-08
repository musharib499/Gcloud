package com.gcloud.gaadi.model;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.BuildConfig;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.utils.CommonUtils;

public class IssueWarrantyModel {

    String usedCarID;
    private String apikey, output, username, normal_password, certificationID, custName, custAddress, custMobile, custTelePhone, warrantySaleDate, vehicleSaleDate,
            dealerID, chasisNo, engineNo, carMakeModelVersion, carMfgYear, carRegNo, carRegYear, carRegMonth, warrantyStartDate, warrantyEndDate, warrantyType, carID, custEmailId, method;
    private String custCity, odometerReading, price;
    private String packageName = CommonUtils.getStringSharedPreference(ApplicationController.getInstance(), Constants.APP_PACKAGE_NAME, "");
    private static final String source = BuildConfig.APP_SOURCE;

    public String getUsedCarID() {
        return usedCarID;
    }

    public void setUsedCarID(String usedCarID) {
        this.usedCarID = usedCarID;
    }

    public String getCity() {
        return custCity;
    }

    public void setCity(String city) {
        this.custCity = city;
    }

    public String getOdometerreading() {
        return odometerReading;
    }

    public void setOdometerreading(String odometerreading) {
        this.odometerReading = odometerreading;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNormal_password() {
        return normal_password;
    }

    public void setNormal_password(String normal_password) {
        this.normal_password = normal_password;
    }

    public String getCertificationID() {
        return certificationID;
    }

    public void setCertificationID(String certificationID) {
        this.certificationID = certificationID;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustAddress() {
        return custAddress;
    }

    public void setCustAddress(String custAddress) {
        this.custAddress = custAddress;
    }

    public String getCustMobile() {
        return custMobile;
    }

    public void setCustMobile(String custMobile) {
        this.custMobile = custMobile;
    }

    public String getCustTelePhone() {
        return custTelePhone;
    }

    public void setCustTelePhone(String custTelePhone) {
        this.custTelePhone = custTelePhone;
    }

    public String getWarrantySaleDate() {
        return warrantySaleDate;
    }

    public void setWarrantySaleDate(String warrantySaleDate) {
        this.warrantySaleDate = warrantySaleDate;
    }

    public String getVehicleSaleDate() {
        return vehicleSaleDate;
    }

    public void setVehicleSaleDate(String vehicleSaleDate) {
        this.vehicleSaleDate = vehicleSaleDate;
    }

    public String getDealerID() {
        return dealerID;
    }

    public void setDealerID(String dealerID) {
        this.dealerID = dealerID;
    }

    public String getChasisNo() {
        return chasisNo;
    }

    public void setChasisNo(String chasisNo) {
        this.chasisNo = chasisNo;
    }

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    public String getCarMakeModelVersion() {
        return carMakeModelVersion;
    }

    public void setCarMakeModelVersion(String carMakeModelVersion) {
        this.carMakeModelVersion = carMakeModelVersion;
    }

    public String getCarMfgYear() {
        return carMfgYear;
    }

    public void setCarMfgYear(String carMfgYear) {
        this.carMfgYear = carMfgYear;
    }

    public String getCarRegNo() {
        return carRegNo;
    }

    public void setCarRegNo(String carRegNo) {
        this.carRegNo = carRegNo;
    }

    public String getCarRegYear() {
        return carRegYear;
    }

    public void setCarRegYear(String carRegYear) {
        this.carRegYear = carRegYear;
    }

    public String getCarRegMonth() {
        return carRegMonth;
    }

    public void setCarRegMonth(String carRegMonth) {
        this.carRegMonth = carRegMonth;
    }

    public String getWarrantyStartDate() {
        return warrantyStartDate;
    }

    public void setWarrantyStartDate(String warrantyStartDate) {
        this.warrantyStartDate = warrantyStartDate;
    }

    public String getWarrantyEndDate() {
        return warrantyEndDate;
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

    public String getCarID() {
        return carID;
    }

    public void setCarID(String carID) {
        this.carID = carID;
    }

    public String getCustEmailId() {
        return custEmailId;
    }

    public void setCustEmailId(String custEmailId) {
        this.custEmailId = custEmailId;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
