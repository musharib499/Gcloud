package com.gcloud.gaadi.rsa;

import com.gcloud.gaadi.rsa.RSAModel.RSACarDetailsModel;

import java.io.Serializable;

/**
 * Created by ankitgarg on 22/05/15.
 */
public class RSACarSelectedEvent implements Serializable {

    private RSACarDetailsModel carData;

    public RSACarSelectedEvent(RSACarDetailsModel carData) {
        this.carData = carData;
    }

    public RSACarDetailsModel getCarData() {
        return carData;
    }

    public void setCarData(RSACarDetailsModel carData) {
        this.carData = carData;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RSACarSelectedEvent{");
        sb.append("carData=").append(carData);
        sb.append('}');
        return sb.toString();
    }
}
