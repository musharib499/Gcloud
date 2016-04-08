package com.gcloud.gaadi.rsa.RSAModel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by shivani on 8/3/16.
 */
public class RSAPackage implements Serializable {
    @SerializedName("package_id")
    int packageID;

    @SerializedName("package_name")
    String packageName;

    public int getPackageID() {
        return packageID;
    }

    public void setPackageID(int packageID) {
        this.packageID = packageID;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

}
