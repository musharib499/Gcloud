package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ankit on 27/1/15.
 */
public class VariantColorsModel implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("msg")
    private String message;

    @SerializedName("error")
    private String errorMessage;
    @SerializedName("colorList")
    private ArrayList<String> colors;
    @SerializedName("hexCode")
    private ArrayList<String> hexCode;

    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(ArrayList<String> colors) {
        this.colors = colors;
    }

    public ArrayList<String> getHexCode() {
        return hexCode;
    }

    public void setHexCode(ArrayList<String> hexCode) {
        this.hexCode = hexCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    @Override
    public String toString() {
        return "VariantColorsModel{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", colors=" + colors +
                '}';
    }
}
