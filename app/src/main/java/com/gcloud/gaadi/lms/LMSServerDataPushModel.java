package com.gcloud.gaadi.lms;

import com.gcloud.gaadi.model.GeneralResponse;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Gaurav on 31-08-2015.
 */
public class LMSServerDataPushModel extends GeneralResponse {

    public class LMSServerDataPushObjectModel extends GeneralResponse {

        @SerializedName("number")
        private String number;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }

    @SerializedName("success")
    private ArrayList<LMSServerDataPushObjectModel> successArrayList;

    public ArrayList<LMSServerDataPushObjectModel> getSuccessArrayList() {
        return successArrayList;
    }

    public void setSuccessArrayList(ArrayList<LMSServerDataPushObjectModel> successArrayList) {
        this.successArrayList = successArrayList;
    }
}
