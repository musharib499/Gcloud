package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ankitgarg on 28/05/15.
 */
public class InsuranceInspectedCarData implements Serializable {

    @SerializedName("car_id")
    private String carId;

    @SerializedName("reg_date")
    private String registrationDate;

    @SerializedName("reg_city")
    private String registrationCity;

    @SerializedName("city_id")
    private String cityId;

    @SerializedName("make_id")
    private int makeId;

    @SerializedName("make")
    private String make;

    @SerializedName("model")
    private String model;

    @SerializedName("carversion")
    private String version;

    @SerializedName("version")
    private String carVersion;

    @SerializedName("modelYear")
    private String modelYear;

    @SerializedName("regNo")
    private String regNo;

    @SerializedName("existingIDV")
    private String existingIDV;

    @SerializedName("reg_month")
    private String regMonth;

    @SerializedName("reg_year")
    private String regYear;

    @SerializedName("engineNo")
    private String engineNo;

    @SerializedName("chassisNo")
    private String chassisNo;

    @SerializedName("showSeatingCapacity")
    private boolean showSeatingCapacity;

    @SerializedName("power")
    private String power;

    @SerializedName("passenger_seats")
    private String passengerSeats;

    @SerializedName("report_url")
    private String certificationReportURL;

    @SerializedName("fuel_type")
    private String fuelType;

    @SerializedName("flipment")
    private String fitmentType;

    @SerializedName("CNGLPGEndorsementinRC")
    private String CNGLPGEndorsementinRC;

    @SerializedName("rcUrl")
    private String rcUrl;

    @SerializedName("rcUrl2")
    private String rcUrl2;

    @SerializedName("km")
    private String kmsDriven;

    @SerializedName("colour")
    private String colour;

    @SerializedName("price")
    private String price;

    @SerializedName("image")
    private String image;

    @SerializedName("request_id")
    private String requestId;

    @SerializedName("request_date")
    private String requestDate;

    @SerializedName("insurer")
    private String insurer;

    @SerializedName("net_premium")
    private String premium;

    @SerializedName("booking_status")
    private String bookingStatus;

    @SerializedName("cancel_reason")
    private String cancelReason;

    @SerializedName("booking_date")
    private String bookingDate;

    @SerializedName("car_type")
    private String carType;

    @SerializedName("policy_doc")
    private String policyDocUrl;

    public String getRcUrl2() {
        return rcUrl2 == null ? "" : rcUrl2;
    }

    public void setRcUrl2(String rcUrl2) {
        this.rcUrl2 = rcUrl2;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getRegistrationCity() {
        return registrationCity;
    }

    public void setRegistrationCity(String registrationCity) {
        this.registrationCity = registrationCity;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public int getMakeId() {
        return makeId;
    }

    public void setMakeId(int makeId) {
        this.makeId = makeId;
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

    public String getRegMonth() {
        return regMonth;
    }

    public void setRegMonth(String regMonth) {
        this.regMonth = regMonth;
    }

    public String getRegYear() {
        return regYear;
    }

    public void setRegYear(String regYear) {
        this.regYear = regYear;
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

    public String getPassengerSeats() {
        return passengerSeats;
    }

    public void setPassengerSeats(String passengerSeats) {
        this.passengerSeats = passengerSeats;
    }

    public String getCertificationReportURL() {
        return certificationReportURL;
    }

    public void setCertificationReportURL(String certificationReportURL) {
        this.certificationReportURL = certificationReportURL;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getFitmentType() {
        return fitmentType;
    }

    public void setFitmentType(String fitmentType) {
        this.fitmentType = fitmentType;
    }

    public String getCNGLPGEndorsementinRC() {
        return CNGLPGEndorsementinRC;
    }

    public void setCNGLPGEndorsementinRC(String CNGLPGEndorsementinRC) {
        this.CNGLPGEndorsementinRC = CNGLPGEndorsementinRC;
    }

    public String getRcUrl() {
        return rcUrl == null ? "" : rcUrl;
    }

    public void setRcUrl(String rcUrl) {
        this.rcUrl = rcUrl;
    }

    public String getKmsDriven() {
        return kmsDriven;
    }

    public void setKmsDriven(String kmsDriven) {
        this.kmsDriven = kmsDriven;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getInsurer() {
        return insurer;
    }

    public void setInsurer(String insurer) {
        this.insurer = insurer;
    }

    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getCarVersion() {
        return carVersion;
    }

    public void setCarVersion(String carVersion) {
        this.carVersion = carVersion;
    }

    public String getPolicyDocUrl() {
        return policyDocUrl;
    }

    public void setPolicyDocUrl(String policyDocUrl) {
        this.policyDocUrl = policyDocUrl;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("InspectedCarsInsuranceModel{");
        sb.append("carId='").append(carId).append('\'');
        sb.append(", registrationDate='").append(registrationDate).append('\'');
        sb.append(", registrationCity='").append(registrationCity).append('\'');
        sb.append(", cityId='").append(cityId).append('\'');
        sb.append(", makeId=").append(makeId);
        sb.append(", make='").append(make).append('\'');
        sb.append(", model='").append(model).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", modelYear='").append(modelYear).append('\'');
        sb.append(", regNo='").append(regNo).append('\'');
        sb.append(", existingIDV='").append(existingIDV).append('\'');
        sb.append(", regMonth='").append(regMonth).append('\'');
        sb.append(", regYear='").append(regYear).append('\'');
        sb.append(", engineNo='").append(engineNo).append('\'');
        sb.append(", chassisNo='").append(chassisNo).append('\'');
        sb.append(", showSeatingCapacity=").append(showSeatingCapacity);
        sb.append(", power='").append(power).append('\'');
        sb.append(", passengerSeats='").append(passengerSeats).append('\'');
        sb.append(", certificationReportURL='").append(certificationReportURL).append('\'');
        sb.append(", fuelType='").append(fuelType).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
