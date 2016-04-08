package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ankit on 23/1/15.
 */
public class UpgradeCheckModel implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("msg")
    private String message;

    @SerializedName("latest_version")
    private int latestVersion;

    @SerializedName("force_upgrade")
    private boolean forceUpgrade;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(int latestVersion) {
        this.latestVersion = latestVersion;
    }

    public boolean isForceUpgrade() {
        return forceUpgrade;
    }

    public void setForceUpgrade(boolean forceUpgrade) {
        this.forceUpgrade = forceUpgrade;
    }
}
