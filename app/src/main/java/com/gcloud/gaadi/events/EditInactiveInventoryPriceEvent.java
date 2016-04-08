package com.gcloud.gaadi.events;

import java.io.Serializable;

/**
 * Created by ankit on 22/1/15.
 */
public class EditInactiveInventoryPriceEvent implements Serializable {
    private String carId;
    private String currentPrice;
    private String action;

    public EditInactiveInventoryPriceEvent(String carId, String currentPrice) {
        this.carId = carId;
        this.currentPrice = currentPrice;
    }

    public EditInactiveInventoryPriceEvent(String carId, String currentPrice, String addremove) {
        this.carId = carId;
        this.currentPrice = currentPrice;
        this.action = addremove;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    @Override
    public String toString() {
        return "EditInactiveInventoryPriceEvent{" +
                "carId='" + carId + '\'' +
                ", currentPrice='" + currentPrice + '\'' +
                '}';
    }
}
