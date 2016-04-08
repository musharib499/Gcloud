package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by priyarawat on 17/11/15.
 */
public class UsedCarValuationModel extends GeneralResponse implements Serializable{

    @SerializedName("data")
    private UsedCarData usedCarData;

    public UsedCarData getUsedCarData() {
        return usedCarData;
    }

    public void setUsedCarData(UsedCarData usedCarData) {
        this.usedCarData = usedCarData;
    }

    public class  UsedCarData implements Serializable{
        private String d2IExcellent;
        private String d2IGood;
        private String d2IFair;
        private String  dataID;
        private String i2DFair;
        private String i2DGood;
        private String i2DExcellent;

        private String start_year;
        private String end_year;

        public String getEnd_year() {
            return end_year;
        }

        public void setEnd_year(String end_year) {
            this.end_year = end_year;
        }

        public String getStart_year() {
            return start_year;
        }

        public void setStart_year(String start_year) {
            this.start_year = start_year;
        }

        public String getI2DFair() {
            return i2DFair;
        }

        public void setI2DFair(String i2DFair) {
            this.i2DFair = i2DFair;
        }

        public String getI2DGood() {
            return i2DGood;
        }

        public void setI2DGood(String i2DGood) {
            this.i2DGood = i2DGood;
        }

        public String getI2DExcellent() {
            return i2DExcellent;
        }

        public void setI2DExcellent(String i2DExcellent) {
            this.i2DExcellent = i2DExcellent;
        }

        public String getDataID() {
            return dataID;
        }

        public void setDataID(String dataID) {
            this.dataID = dataID;
        }

        public String getD2IExcellent() {
            return d2IExcellent;
        }

        public void setD2IExcellent(String d2IExcellent) {
            this.d2IExcellent = d2IExcellent;
        }

        public String getD2IGood() {
            return d2IGood;
        }

        public void setD2IGood(String d2IGood) {
            this.d2IGood = d2IGood;
        }

        public String getD2IFair() {
            return d2IFair;
        }

        public void setD2IFair(String d2IFair) {
            this.d2IFair = d2IFair;
        }
    }
}
