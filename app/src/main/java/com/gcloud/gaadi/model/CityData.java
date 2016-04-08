package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ankit on 7/1/15.
 */
public class CityData implements Serializable {

    @SerializedName("city_id")
    private String cityId;

    @SerializedName("city_name")
    private String cityName;

    @SerializedName("state_id")
    private String stateId;

    @SerializedName("region_id")
    private String regionId;

    @SerializedName("order_by")
    private String orderBy;

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    @Override
    public String toString() {
        return "CityData{" +
                "cityId='" + cityId + '\'' +
                ", cityName='" + cityName + '\'' +
                ", stateId='" + stateId + '\'' +
                ", regionId='" + regionId + '\'' +
                '}';
    }
}
