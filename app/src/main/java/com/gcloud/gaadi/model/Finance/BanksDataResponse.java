package com.gcloud.gaadi.model.Finance;

import com.gcloud.gaadi.model.GeneralResponse;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lakshaygirdhar on 6/10/15.
 */
public class BanksDataResponse extends GeneralResponse {

    @SerializedName("data")
    BankData[] bankDatas;

    public BanksDataResponse(BankData[] bankDatas) {
        this.bankDatas = bankDatas;
    }

    public BankData[] getBankDatas() {
        return bankDatas;
    }

    public void setBankDatas(BankData[] bankDatas) {
        this.bankDatas = bankDatas;
    }
}
