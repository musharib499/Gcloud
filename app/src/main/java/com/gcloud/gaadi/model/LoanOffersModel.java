/*
 * Copyright (C) 2014 Francesco Azzola
 *  Surviving with Android (http://www.survivingwithandroid.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.gcloud.gaadi.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoanOffersModel implements Serializable {
    public static final String NAME_PREFIX = "Name_";
    public static final String SURNAME_PREFIX = "Surname_";
    public static final String EMAIL_PREFIX = "email_";
    public String name;
    public String surname;
    public String email;
    @SerializedName("status")
    private String status;
    @SerializedName("msg")
    private String message;
    @SerializedName("error")
    private String errorMessage;
    @SerializedName("car_data")
    private CarItemModel car_data;

    public CarItemModel getCar_data() {
        return car_data;
    }

    public void setCar_data(CarItemModel car_data) {
        this.car_data = car_data;
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


}

