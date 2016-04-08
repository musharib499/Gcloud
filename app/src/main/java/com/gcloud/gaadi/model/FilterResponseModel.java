package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by gauravkumar on 1/10/15.
 */
public class FilterResponseModel extends GeneralResponse {

    @SerializedName("filters")
    private Filters filters;

    public Filters getFilters() {
        return filters;
    }

    public void setFilters(Filters filters) {
        this.filters = filters;
    }

    public class Filters implements Serializable {

        @SerializedName("make")
        private ArrayList<String> make;

        @SerializedName("model")
        private ArrayList<String> model;

        @SerializedName("fuel_type")
        private ArrayList<String> fuelType;

        @SerializedName("year")
        private ArrayList<String> year;

        @SerializedName("price")
        private ArrayList<KeyValueModel> price;

        @SerializedName("km")
        private ArrayList<KeyValueModel> km;

        public ArrayList<String> getMake() {
            return make;
        }

        public void setMake(ArrayList<String> make) {
            this.make = make;
        }

        public ArrayList<String> getModel() {
            return model;
        }

        public void setModel(ArrayList<String> model) {
            this.model = model;
        }

        public ArrayList<String> getFuelType() {
            return fuelType;
        }

        public void setFuelType(ArrayList<String> fuelType) {
            this.fuelType = fuelType;
        }

        public ArrayList<String> getYear() {
            return year;
        }

        public void setYear(ArrayList<String> year) {
            this.year = year;
        }

        public ArrayList<KeyValueModel> getPrice() {
            return price;
        }

        public void setPrice(ArrayList<KeyValueModel> price) {
            this.price = price;
        }

        public ArrayList<KeyValueModel> getKm() {
            return km;
        }

        public void setKm(ArrayList<KeyValueModel> km) {
            this.km = km;
        }

        public class KeyValueModel implements Serializable {

            @SerializedName("key")
            private String key;

            @SerializedName("value")
            private String value;

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }
}
