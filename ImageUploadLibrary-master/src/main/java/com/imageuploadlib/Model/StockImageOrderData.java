package com.imageuploadlib.Model;

//import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Seema Pandit on 27-03-2015.
 */
public class StockImageOrderData implements Serializable {
    //    @SerializedName("id")
    private int id;
    //    @SerializedName("carid")
    private String carID;
    //    @SerializedName("order")
    private String imageUploadOrder;
    //    @SerializedName("currentTimeStamp")
    private String currentTimeStamp;
    private String mapOrder;

    public StockImageOrderData() {
    }

    public StockImageOrderData(String carID, String imageUploadOrder, String mapOrder) {
        this.carID = carID;
        this.imageUploadOrder = imageUploadOrder;
        this.mapOrder = mapOrder;
    }

    public String getMapOrder() {
        return mapOrder;
    }

    public void setMapOrder(String mapOrder) {
        this.mapOrder = mapOrder;
    }

    public String getImageUploadOrder() {
        return imageUploadOrder;
    }

    public void setImageUploadOrder(String imageUploadOrder) {
        this.imageUploadOrder = imageUploadOrder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCarID() {
        return carID;
    }

    public void setCarID(String carID) {
        this.carID = carID;
    }

    public long getCurrentTimeStamp() {
        return System.currentTimeMillis();
    }

    public void setCurrentTimeStamp(String currentTimeStamp) {
        this.currentTimeStamp = currentTimeStamp;
    }

    @Override
    public String toString() {
        return "StockImageOrderData{" +
                "carID='" + carID + '\'' +
                ", imageUploadOrder='" + imageUploadOrder + '\'' +
                ", currentTimeStamp='" + currentTimeStamp + '\'' +
                ", mapOrder='" + mapOrder + '\'' +
                '}';
    }
}
