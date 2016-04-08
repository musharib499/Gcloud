package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class AddLeadModel implements Serializable {

    @SerializedName("msg")
    private String msg;

    @SerializedName("error")
    private String error;
    @SerializedName("cars_interested")
    private ArrayList<AllCars> cars_interested;
    @SerializedName("similar_cars")
    private ArrayList<SimilarCars> similar_cars;
    @SerializedName("status")
    private String status;

    public ArrayList<AllCars> getCars_interested() {
        return cars_interested;
    }

    public void setCars_interested(ArrayList<AllCars> cars_interested) {
        this.cars_interested = cars_interested;
    }

    public ArrayList<SimilarCars> getSimilar_cars() {
        return similar_cars;
    }

    public void setSimilar_cars(ArrayList<SimilarCars> similar_cars) {
        this.similar_cars = similar_cars;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
