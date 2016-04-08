package com.gcloud.gaadi.model;

import java.io.Serializable;

/**
 * Created by ashishkumar on 4/12/15.
 */
public class UsedCarDetailsModel implements Serializable {

    private String make, version, regyear, carkm, owner, buyer, city;

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRegyear() {
        return regyear;
    }

    public void setRegyear(String regyear) {
        this.regyear = regyear;
    }

    public String getCarkm() {
        return carkm;
    }

    public void setCarkm(String carkm) {
        this.carkm = carkm;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UsedCarDetailsModel{");
        sb.append("make='").append(make).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", regyear='").append(regyear).append('\'');
        sb.append(", carkm='").append(carkm).append('\'');
        sb.append(", owner='").append(owner).append('\'');
        sb.append(", buyer='").append(buyer).append('\'');
        sb.append(", city='").append(city).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
