package com.gcloud.gaadi.rsa.RSAModel;

import com.gcloud.gaadi.model.GeneralResponse;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by shivani on 7/3/16.
 */
public class RSADashboardResponseModel extends GeneralResponse {


    @SerializedName("rsa_packages")
    ArrayList<RSAPackage> rsaPackages;
    @SerializedName("rsa_balance")
    int balance;
    @SerializedName("total_cases")
    int totalCases;

    public int getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(int totalCases) {
        this.totalCases = totalCases;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public ArrayList<RSAPackage> getRsaPackages() {
        return rsaPackages;
    }

    public void setRsaPackages(ArrayList<RSAPackage> rsaPackages) {
        this.rsaPackages = rsaPackages;
    }


}
