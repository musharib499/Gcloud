package com.gcloud.gaadi.model;

import com.gcloud.gaadi.utils.CommonUtils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ankit on 30/12/14.
 */
public class StocksModel implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("error")
    private String error;

    @SerializedName("msg")
    private String message;

    @SerializedName("stockData")
    private ArrayList<StockItemModel> stocks;

    @SerializedName("totalRecords")
    private int totalRecords;

    @SerializedName("hasNext")
    private boolean hasNext;


    @SerializedName("registrationNumber")
    private String registrationNumber;

   /* @SerializedName("filters")
    private Filters filters;*/

    @SerializedName("stokc_filters")
    private Filters filters;

    @SerializedName("inspected_car_filters")
    private Filters inspectedFilters;

    public Filters getFilters() {
        return filters;
    }

    public void setFilters(Filters filters) {
        this.filters = filters;
    }

    public Filters getInspectedFilters() {
        return inspectedFilters;
    }

    public void setInspectedFilters(Filters inspectedFilters) {
        this.inspectedFilters = inspectedFilters;
    }

    public class Filters implements Serializable {

        @SerializedName("make")
        private ArrayList<StockDetailModel> make;

        @SerializedName("model")
        private ArrayList<StockDetailModel> model;

        @SerializedName("fuel_type")
        private ArrayList<String> fuelType;

        @SerializedName("year")
        private ArrayList<String> year;

        @SerializedName("price")
        private ArrayList<KeyValueModel> price;

        @SerializedName("km")
        private ArrayList<KeyValueModel> km;

        @SerializedName("warrantyType")
        private ArrayList<WarrantyType> warrantyType;

        @SerializedName("warrantyStatus")
        private ArrayList<KeyValueModel> warrantyStatus;

        @SerializedName("status")
        private ArrayList<String> status;

        @SerializedName("lead_source")
        private ArrayList<String> leadSource;

        @SerializedName("insurance_type")
        private ArrayList<String> insuranceType;

        private ArrayList<String> insurer;

        private String updateTime;

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public ArrayList<String> getStatus() {
            ArrayList<String> list = new ArrayList<>();
            for (String value: status) {
                list.add(CommonUtils.camelCase(value));
            }
            return list;
        }

        public void setStatus(ArrayList<String> status) {
            this.status = status;
        }

        public ArrayList<String> getLeadSource() {
            return leadSource;
        }

        public void setLeadSource(ArrayList<String> leadSource) {
            this.leadSource = leadSource;
        }

        public ArrayList<String> getInsuranceType() {
            return insuranceType;
        }

        public void setInsuranceType(ArrayList<String> insuranceType) {
            this.insuranceType = insuranceType;
        }

        public ArrayList<String> getInsurer() {
            return insurer;
        }

        public void setInsurer(ArrayList<String> insurer) {
            this.insurer = insurer;
        }

        public ArrayList<KeyValueModel> getWarrantyStatus() {
            return warrantyStatus;
        }

        public void setWarrantyStatus(ArrayList<KeyValueModel> warrantyStatus) {
            this.warrantyStatus = warrantyStatus;
        }

        public ArrayList<WarrantyType> getWarrantyType() {
            return warrantyType;
        }

        public void setWarrantyType(ArrayList<WarrantyType> warrantyType) {
            this.warrantyType = warrantyType;
        }

        public ArrayList<StockDetailModel> getMake() {
            return make;
        }

        public void setMake(ArrayList<StockDetailModel> make) {
            this.make = make;
        }

        public ArrayList<StockDetailModel> getModel() {
            return model;
        }

        public void setModel(ArrayList<StockDetailModel> model) {
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

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Filters{");
            sb.append("make=").append(make);
            sb.append(", model=").append(model);
            sb.append(", fuelType=").append(fuelType);
            sb.append(", year=").append(year);
            sb.append(", price=").append(price);
            sb.append(", km=").append(km);
            sb.append(", warrantyType=").append(warrantyType);
            sb.append('}');
            return sb.toString();
        }
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ArrayList<StockItemModel> getStocks() {
        return stocks;
    }

    public void setStocks(ArrayList<StockItemModel> stocks) {
        this.stocks = stocks;
    }

    @Override
    public String toString() {
        return "StocksModel{" +
                "status='" + status + '\'' +
                ", error='" + error + '\'' +
                ", stocks=" + stocks +
                '}';
    }

    public class WarrantyType {

        private String packID;

        private String packName;

        public String getPackID() {
            return packID;
        }

        public void setPackID(String packID) {
            this.packID = packID;
        }

        public String getPackName() {
            return packName;
        }

        public void setPackName(String packName) {
            this.packName = packName;
        }
    }
}
