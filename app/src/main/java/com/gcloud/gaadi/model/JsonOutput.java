package com.gcloud.gaadi.model;

/**
 * Created by Priya on 26-05-2015.
 */
public class JsonOutput {
    private String apikey;
    private String method;
    private String output;
    private String username;
    private String normal_password;
    private String packageID;
    private String car_id;
    private String bank_id;
    private String customerName;
    private String customerEmail;
    private String customerAddress;
    private String customerMobile;
    private String ucdid;
    private Paginate paginate;
    private String carID;

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public String getUcdid() {
        return ucdid;
    }

    public void setUcdid(String ucdid) {
        this.ucdid = ucdid;
    }

    public String getPackageID() {
        return packageID;
    }

    public void setPackageID(String packageID) {
        this.packageID = packageID;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNormal_password() {
        return normal_password;
    }

    public void setNormal_password(String normal_password) {
        this.normal_password = normal_password;
    }

    public Paginate getPaginate() {
        return paginate;
    }

    public void setPaginate(Paginate paginate) {
        this.paginate = paginate;
    }

    public String getCarID() {
        return carID;
    }

    public void setCarID(String carID) {
        this.carID = carID;
    }

    public String getBank_id() {
        return bank_id;
    }

    public void setBank_id(String bank_id) {
        this.bank_id = bank_id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    @Override
    public String toString() {
        return "JsonObject [apikey=" + apikey + ", method="
                + method + ", output=" + output + ", username=" + username
                + ", normal_password=" + normal_password + ", paginate="
                + paginate + ", toString()="
                + super.toString() + "]";
    }
}
