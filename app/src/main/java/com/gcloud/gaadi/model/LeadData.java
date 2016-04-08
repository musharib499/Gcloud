package com.gcloud.gaadi.model;

import java.io.Serializable;

/**
 * Created by Gaurav on 10-06-2015.
 */
public class LeadData implements Serializable {
    //    private String rowId;
    private String name;
    private String leadId;
    private String makeId;
    private String modelName;
    //    private String date;
    private String status;
    private String apiStatus;
    private String number;
    private long notificationTime, changeTime;
    private int requestCode;

    public LeadData(String... values) {
        this.name = values[0];
        this.leadId = values[1];
        this.makeId = values[2];
        this.modelName = values[3];
//        this.date = values[4];
    }

    public LeadData() {

    }

    public LeadData(LeadDetailModel model) {
        this.leadId = model.getLeadId();
        this.makeId = model.getMakename();
        this.modelName = model.getModel();
        this.name = model.getName();
//        this.date = model.getDateTime();
        this.apiStatus = model.getLeadStatus();
        this.number = model.getNumber();
    }

    /*public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeadId() {
        return leadId;
    }

    public void setLeadId(String leadId) {
        this.leadId = leadId;
    }

    public String getMakeId() {
        return makeId;
    }

    public void setMakeId(String makeId) {
        this.makeId = makeId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

   /* public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }*/

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(String apiStatus) {
        this.apiStatus = apiStatus;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(long notificationTime) {
        this.notificationTime = notificationTime;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public long getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(long changeTime) {
        this.changeTime = changeTime;
    }
}
