package com.gcloud.gaadi.model;

public class ViewCertificationCarsWarrantyInput {

    private String apikey;
    private String method;
    private String output;
    private String username;
    private String normal_password;
    private String packageID;
    private Paginate paginate;
    private FilterCertificationCars filterValue;
    private String statusID, carID, certificateID, carStatus, remarks, warrantyID;

    public String getPackageID() {
        return packageID;
    }

    public void setPackageID(String packageID) {
        this.packageID = packageID;
    }

    public String getWarrantyID() {
        return warrantyID;
    }

    public void setWarrantyID(String warrantyID) {
        this.warrantyID = warrantyID;
    }

    public String getCertificateID() {
        return certificateID;
    }

    public void setCertificateID(String certificateID) {
        this.certificateID = certificateID;
    }

    public String getCarStatus() {
        return carStatus;
    }

    public void setCarStatus(String carStatus) {
        this.carStatus = carStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getStatusID() {
        return statusID;
    }

    public void setStatusID(String statusID) {
        this.statusID = statusID;
    }

    public String getCarID() {
        return carID;
    }

    public void setCarID(String carID) {
        this.carID = carID;
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

    public FilterCertificationCars getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(FilterCertificationCars filterValue) {
        this.filterValue = filterValue;
    }

    @Override
    public String toString() {
        return "ViewCertificationCarsWarrantyInput [apikey=" + apikey + ", method="
                + method + ", output=" + output + ", username=" + username
                + ", normal_password=" + normal_password + ", paginate="
                + paginate + ", filterValue=" + filterValue + ", toString()="
                + super.toString() + "]";
    }

}
