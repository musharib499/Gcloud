package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Lakshay on 08-09-2015.
 */
public class FinancePersonalDetails implements Serializable {

    @SerializedName("name")
    private String firstName;
    private String lastName;

    @SerializedName("email")
    private String email;

    @SerializedName("mobile")
    private String phone;

    @SerializedName("date_of_birth")
    private String dateOfBirth;
    private String gender;

    @SerializedName("residence_type")
    private String residenceType;

    @SerializedName("length_of_stay")
    private String stayInYears;

    @SerializedName("car_id")
    private String carId;

    @SerializedName("cra_city")
    private String homeCity;

    public String getHomeCity() {
        return homeCity;
    }

    public void setHomeCity(String homeCity) {
        this.homeCity = homeCity;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getResidenceType() {
        return residenceType;
    }

    public void setResidenceType(String residenceType) {
        this.residenceType = residenceType;
    }

    public String getStayInYears() {
        return stayInYears;
    }

    public void setStayInYears(String stayInYears) {
        this.stayInYears = stayInYears;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
