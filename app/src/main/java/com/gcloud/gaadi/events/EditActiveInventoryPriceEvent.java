package com.gcloud.gaadi.events;

import java.io.Serializable;

/**
 * Created by ankit on 22/1/15.
 */
public class EditActiveInventoryPriceEvent implements Serializable {
    private String carId;
    private String currentPrice;

    public EditActiveInventoryPriceEvent(String carId, String currentPrice) {
        this.carId = carId;
        this.currentPrice = currentPrice;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    @Override
    public String toString() {
        return "EditActiveInventoryPriceEvent{" +
                "carId='" + carId + '\'' +
                ", currentPrice='" + currentPrice + '\'' +
                '}';
    }
}
