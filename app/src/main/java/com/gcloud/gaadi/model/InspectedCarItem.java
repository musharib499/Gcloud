package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Priya on 29-05-2015.
 */
public class InspectedCarItem {

    @SerializedName("car_id")
    private String car_id;

    @SerializedName("reg_date")
    private String reg_date;

    @SerializedName("reg_city")
    private String reg_city;

    @SerializedName("city_id")
    private String city_id;
    @SerializedName("makeid")
    private String makeid;

    @SerializedName("make")
    private String make;

    @SerializedName("model")
    private String model;

    @SerializedName("version")
    private String version;

    @SerializedName("modelYear")
    private String modelYear;

    @SerializedName("regNo")
    private String regNo;

    @SerializedName("existingIDV")
    private String existingIDV;

    @SerializedName("registrationMonth")
    private String registrationMonth;

    @SerializedName("registrationYear")
    private String registrationYear;

    @SerializedName("engineNo")
    private String engineNo;

    @SerializedName("chassisNo")
    private String chassisNo;


    @SerializedName("showSeatingCapacity")
    private boolean showSeatingCapacity;

    @SerializedName("power")
    private String power;

    @SerializedName("passenger_seats")
    private String passenger_seats;

    @SerializedName("report_url")
    private String report_url;

    @SerializedName("fuel_type")
    private String fuel_type;

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public String getReg_date() {
        return reg_date;
    }

    public void setReg_date(String reg_date) {
        this.reg_date = reg_date;
    }

    public String getReg_city() {
        return reg_city;
    }

    public void setReg_city(String reg_city) {
        this.reg_city = reg_city;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getMakeid() {
        return makeid;
    }

    public void setMakeid(String makeid) {
        this.makeid = makeid;
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

    public String getModelYear() {
        return modelYear;
    }

    public void setModelYear(String modelYear) {
        this.modelYear = modelYear;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getExistingIDV() {
        return existingIDV;
    }

    public void setExistingIDV(String existingIDV) {
        this.existingIDV = existingIDV;
    }

    public String getRegistrationMonth() {
        return registrationMonth;
    }

    public void setRegistrationMonth(String registrationMonth) {
        this.registrationMonth = registrationMonth;
    }

    public String getRegistrationYear() {
        return registrationYear;
    }

    public void setRegistrationYear(String registrationYear) {
        this.registrationYear = registrationYear;
    }

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    public String getChassisNo() {
        return chassisNo;
    }

    public void setChassisNo(String chassisNo) {
        this.chassisNo = chassisNo;
    }

    public boolean isShowSeatingCapacity() {
        return showSeatingCapacity;
    }

    public void setShowSeatingCapacity(boolean showSeatingCapacity) {
        this.showSeatingCapacity = showSeatingCapacity;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getPassenger_seats() {
        return passenger_seats;
    }

    public void setPassenger_seats(String passenger_seats) {
        this.passenger_seats = passenger_seats;
    }

    public String getReport_url() {
        return report_url;
    }

    public void setReport_url(String report_url) {
        this.report_url = report_url;
    }

    public String getFuel_type() {
        return fuel_type;
    }

    public void setFuel_type(String fuel_type) {
        this.fuel_type = fuel_type;
    }
}
