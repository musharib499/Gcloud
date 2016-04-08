package com.imageuploadlib.Model;

//import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Seema Pandit on 27-03-2015.
 */
public class StockImageData implements Serializable {

    //    @SerializedName("id")
    private int Id;
    //    @SerializedName("carid")
    private String carId;
    //    @SerializedName("imagepath")
    private String imagePath;
    //    @SerializedName("imageuploadstatus")
    private String imageUploadStatus;
    //    @SerializedName("imagesource")
    private String imageSource;
    //    @SerializedName("tag")
    private String tag;
    //    @SerializedName("requestname")
    private String requestImageName;
    //    @SerializedName("responsename")
    private String responseImageName;
    //    @SerializedName("mmv")
    private String makeModelVersion;
    //    @SerializedName("currenttimestamp")
    private String currentTimeStamp;
    private int dealerId;

    public StockImageData() {
    }

    public StockImageData(String carId, String imagePath, String imageUploadStatus, String makeModelVersion, String imageSource, int dealerId) {
        this.carId = carId;
        this.imagePath = imagePath;
        this.imageUploadStatus = imageUploadStatus;
        this.makeModelVersion = makeModelVersion;
        this.imageSource = imageSource;
        this.dealerId = dealerId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageUploadStatus() {
        return imageUploadStatus;
    }

    public void setImageUploadStatus(String imageUploadStatus) {
        this.imageUploadStatus = imageUploadStatus;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getRequestImageName() {
        return requestImageName;
    }

    public void setRequestImageName(String requestImageName) {
        this.requestImageName = requestImageName;
    }

    public String getResponseImageName() {
        return responseImageName;
    }

    public void setResponseImageName(String responseImageName) {
        this.responseImageName = responseImageName;
    }

    public String getMakeModelVersion() {
        return makeModelVersion;
    }

    public void setMakeModelVersion(String makeModelVersion) {
        this.makeModelVersion = makeModelVersion;
    }

    public long getCurrentTimeStamp() {
        return System.currentTimeMillis();
    }

    public void setCurrentTimeStamp(String currentTimeStamp) {
        this.currentTimeStamp = currentTimeStamp;
    }

    @Override
    public String toString() {
        return "StockImageData{" +
                "Id=" + Id +
                ", carId='" + carId + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", imageUploadStatus='" + imageUploadStatus + '\'' +
                ", imageSource='" + imageSource + '\'' +
                ", tag='" + tag + '\'' +
                ", requestImageName='" + requestImageName + '\'' +
                ", responseImageName='" + responseImageName + '\'' +
                ", makeModelVersion='" + makeModelVersion + '\'' +
                ", currentTimeStamp='" + currentTimeStamp + '\'' +
                ", dealerId=" + dealerId +
                '}';
    }

    public int getDealerId() {
        return dealerId;
    }

    public void setDealerId(int dealerId) {
        this.dealerId = dealerId;
    }
}
