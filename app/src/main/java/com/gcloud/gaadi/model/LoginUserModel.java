package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ankit on 17/11/14.
 */
public class LoginUserModel implements Serializable {

    @SerializedName("error")
    private String error;

    @SerializedName("msg")
    private String message;

    @SerializedName("status")
    private String status;

    @SerializedName("executiveData")
    private ExecutiveData executiveData;

    @SerializedName("userdetail")
    private UserDetail userDetail;

    @SerializedName("showrooms")
    private ArrayList<ShowroomData> showrooms;

    @SerializedName("dealerData")
    private ArrayList<DealerData> dealers = null;

    public ArrayList<ShowroomData> getShowrooms() {
        return showrooms;
    }

    public void setShowrooms(ArrayList<ShowroomData> showrooms) {
        this.showrooms = showrooms;
    }

    public ArrayList<DealerData> getDealers() {
        return dealers;
    }

    public void setDealers(ArrayList<DealerData> dealers) {
        this.dealers = dealers;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

    public ExecutiveData getExecutiveData() {
        return executiveData;
    }

    public void setExecutiveData(ExecutiveData executiveData) {
        this.executiveData = executiveData;
    }

    @Override
    public String toString() {
        return "LoginUserModel{" +
                "error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                ", userDetail=" + userDetail +
                ", dealers=" + dealers +
                '}';
    }

    public class ExecutiveData implements Serializable {
        @SerializedName("id")
        private String executiveId;

        public String getExecutiveId() {
            return executiveId;
        }

        public void setExecutiveId(String executiveId) {
            this.executiveId = executiveId;
        }
    }
}
