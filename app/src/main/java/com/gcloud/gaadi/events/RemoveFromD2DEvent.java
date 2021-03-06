package com.gcloud.gaadi.events;

import java.io.Serializable;

/**
 * Created by ankit on 21/1/15.
 */
public class RemoveFromD2DEvent implements Serializable {

    private String carId;

    public RemoveFromD2DEvent(String carId) {
        this.carId = carId;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    @Override
    public String toString() {
        return "RemoveFromD2DEvent{" +
                "carId='" + carId + '\'' +
                '}';
    }
}
