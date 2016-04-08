package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ankitgarg on 15/03/16.
 */
public class SFAUserRight implements Serializable {

    @SerializedName("isEnabled")
    private String isUserRightEnabled;

    @SerializedName("id")
    private String userRightId;

    @SerializedName("name")
    private String userRightName;

    public String getIsUserRightEnabled() {
        return isUserRightEnabled;
    }

    public void setIsUserRightEnabled(String isUserRightEnabled) {
        this.isUserRightEnabled = isUserRightEnabled;
    }

    public String getUserRightId() {
        return userRightId;
    }

    public void setUserRightId(String userRightId) {
        this.userRightId = userRightId;
    }

    public String getUserRightName() {
        return userRightName;
    }

    public void setUserRightName(String userRightName) {
        this.userRightName = userRightName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SFAUserRight{");
        sb.append("isUserRightEnabled='").append(isUserRightEnabled).append('\'');
        sb.append(", userRightId='").append(userRightId).append('\'');
        sb.append(", userRightName='").append(userRightName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
