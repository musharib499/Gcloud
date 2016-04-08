package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ankitgarg on 29/05/15.
 */
public class InsuranceCaseStatusData implements Serializable {

    @SerializedName("requestNo")
    private int requestNo;

    @SerializedName("makeid")
    private int makeId;

    @SerializedName("make")
    private String make;

    @SerializedName("model")
    private String model;

    @SerializedName("version")
    private String version;

    @SerializedName("requestDate")
    private String requestDate;

    @SerializedName("insurer")
    private String insurer;

    @SerializedName("premiumAmount")
    private String premiumAmount;

    @SerializedName("inspectionType")
    private String inspectionType;

    @SerializedName("insuranceStatus")
    private String insuranceStatus;

    public int getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(int requestNo) {
        this.requestNo = requestNo;
    }

    public int getMakeId() {
        return makeId;
    }

    public void setMakeId(int makeId) {
        this.makeId = makeId;
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

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getInsurer() {
        return insurer;
    }

    public void setInsurer(String insurer) {
        this.insurer = insurer;
    }

    public String getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(String premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public String getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(String inspectionType) {
        this.inspectionType = inspectionType;
    }

    public String getInsuranceStatus() {
        return insuranceStatus;
    }

    public void setInsuranceStatus(String insuranceStatus) {
        this.insuranceStatus = insuranceStatus;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("InsuranceCaseStatusData{");
        sb.append("requestNo=").append(requestNo);
        sb.append(", makeId=").append(makeId);
        sb.append(", make='").append(make).append('\'');
        sb.append(", model='").append(model).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", requestDate='").append(requestDate).append('\'');
        sb.append(", insurer='").append(insurer).append('\'');
        sb.append(", premiumAmount='").append(premiumAmount).append('\'');
        sb.append(", inspectionType='").append(inspectionType).append('\'');
        sb.append(", insuranceStatus='").append(insuranceStatus).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
