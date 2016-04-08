package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;

public class CertificationDetailModel implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("msg")
    private String msg;

    @SerializedName("warrantyID")
    private String warrantyID;

    @SerializedName("rsa_detail")
    private RSACustomerInfoResponseModel rsaDetails;

    @SerializedName("certificationData")
    private CertificationDetailedData certificationData;

    @SerializedName("packageList")
    private PackageList[] packageList;

    @SerializedName("error")
    private String error;

    public RSACustomerInfoResponseModel getRSADetails() {
        return rsaDetails;
    }

    public void setRsaID(RSACustomerInfoResponseModel rsaDetails) {
        this.rsaDetails = rsaDetails;
    }

    public String getWarrantyID() {
        return warrantyID;
    }

    public void setWarrantyID(String warrantyID) {
        this.warrantyID = warrantyID;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public CertificationDetailedData getCertificationData() {
        return certificationData;
    }

    public void setCertificationData(CertificationDetailedData certificationData) {
        this.certificationData = certificationData;
    }


    public PackageList[] getPackageList() {
        return packageList;
    }

    public void setPackageList(PackageList[] packageList) {
        this.packageList = packageList;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CertificationDetailModel{");
        sb.append("status='").append(status).append('\'');
        sb.append(", msg='").append(msg).append('\'');
        sb.append(", warrantyID='").append(warrantyID).append('\'');
        sb.append(", rsaDetails=").append(rsaDetails);
        sb.append(", certificationData=").append(certificationData);
        sb.append(", packageList=").append(packageList == null ? "null" : Arrays.asList(packageList).toString());
        sb.append(", error='").append(error).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
