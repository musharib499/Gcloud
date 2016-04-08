package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class WarrantyDetailsData implements Serializable {

    @SerializedName("engine_no")
    private String engine_no;

    @SerializedName("imageIcon")
    private ArrayList<String> arrayImages;

    @SerializedName("chassis_no")
    private String chassis_no;

    @SerializedName("vehicle_sale_date")
    private String vehicle_sale_date;

    @SerializedName("warranty_sale_date")
    private String warranty_sale_date;

    @SerializedName("cust_address")
    private String cust_address;

    @SerializedName("cust_telephone")
    private String cust_telephone;

    @SerializedName("cust_mobile")
    private String cust_mobile;

    @SerializedName("cust_email")
    private String cust_email;

    @SerializedName("cust_name")
    private String cust_name;

    @SerializedName("warranty_id")
    private String warranty_id;

    @SerializedName("make")
    private String make;

    @SerializedName("make_id")
    private int make_id;
    @SerializedName("model")
    private String model;
    @SerializedName("version")
    private String version;
    @SerializedName("manufacture_year")
    private String manufacture_year;
    @SerializedName("reg_no")
    private String reg_no;
    @SerializedName("warranty_type")
    private String warranty_type;
    @SerializedName("warranty_start_date")
    private String warranty_start_date;
    @SerializedName("warranty_end_date")

    private String warranty_end_date;

    public String getWarrantyTypeName() {
        return warrantyTypeName;
    }

    public void setWarrantyTypeName(String warrantyTypeName) {
        this.warrantyTypeName = warrantyTypeName;
    }

    @SerializedName("warrantyTypeName")
    private  String warrantyTypeName;

    public String getOdometerReading() {
        return odometerReading;
    }

    public void setOdometerReading(String odometerReading) {
        this.odometerReading = odometerReading;
    }

    @SerializedName("odometerReading")
    private String odometerReading;


    public int getMake_id() {
        return make_id;
    }

    public void setMake_id(int make_id) {
        this.make_id = make_id;
    }

    public String getEngine_no() {
        return engine_no;
    }

    public void setEngine_no(String engine_no) {
        this.engine_no = engine_no;
    }

    public String getChassis_no() {
        return chassis_no;
    }

    public void setChassis_no(String chassis_no) {
        this.chassis_no = chassis_no;
    }

    public String getVehicle_sale_date() {
        return vehicle_sale_date;
    }

    public void setVehicle_sale_date(String vehicle_sale_date) {
        this.vehicle_sale_date = vehicle_sale_date;
    }

    public String getWarranty_sale_date() {
        return warranty_sale_date;
    }

    public void setWarranty_sale_date(String warranty_sale_date) {
        this.warranty_sale_date = warranty_sale_date;
    }

    public String getCust_address() {
        return cust_address;
    }

    public void setCust_address(String cust_address) {
        this.cust_address = cust_address;
    }

    public String getCust_telephone() {
        return cust_telephone;
    }

    public void setCust_telephone(String cust_telephone) {
        this.cust_telephone = cust_telephone;
    }

    public String getCust_mobile() {
        return cust_mobile;
    }

    public void setCust_mobile(String cust_mobile) {
        this.cust_mobile = cust_mobile;
    }

    public ArrayList<String> getArrayImages() {
        return arrayImages;
    }

    public void setArrayImages(ArrayList<String> arrayImages) {
        this.arrayImages = arrayImages;
    }

    public String getCust_email() {
        return cust_email;
    }

    public void setCust_email(String cust_email) {
        this.cust_email = cust_email;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getWarranty_id() {
        return warranty_id;
    }

    public void setWarranty_id(String warranty_id) {
        this.warranty_id = warranty_id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getManufacture_year() {
        return manufacture_year;
    }

    public void setManufacture_year(String manufacture_year) {
        this.manufacture_year = manufacture_year;
    }

    public String getReg_no() {
        return reg_no;
    }

    public void setReg_no(String reg_no) {
        this.reg_no = reg_no;
    }

    public String getWarranty_type() {
        return warranty_type;
    }

    public void setWarranty_type(String warranty_type) {
        this.warranty_type = warranty_type;
    }

    public String getWarranty_start_date() {
        return warranty_start_date;
    }

    public void setWarranty_start_date(String warranty_start_date) {
        this.warranty_start_date = warranty_start_date;
    }

    public String getWarranty_end_date() {
        return warranty_end_date;
    }

    public void setWarranty_end_date(String warranty_end_date) {
        this.warranty_end_date = warranty_end_date;
    }
}


