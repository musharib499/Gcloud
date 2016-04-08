package com.gcloud.gaadi.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.model.CityModel;
import com.gcloud.gaadi.model.Make;
import com.gcloud.gaadi.model.Model;
import com.gcloud.gaadi.model.Version;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by gauravkumar on 10/9/15.
 */
public class SplashSyncAdapter extends AbstractThreadedSyncAdapter {

    private Context context;

    public SplashSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
    }

    public SplashSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        this.context = context;
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              SyncResult syncResult) {
        makeRequests();
    }

    private void makeRequests() {

        performMakeRequest();
        performModelRequest();
        performVersionRequest();
        performCityRequest();

    }

    private void performMakeRequest() {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.METHOD_LABEL, Constants.MAKE_ALL_METHOD);
        RetrofitRequest.makeMakeRequest(context, params, new Callback<Make>() {
            @Override
            public void success(Make make, retrofit.client.Response response) {
                MakeModelVersionDB makeDB = ApplicationController.getMakeModelVersionDB();
                makeDB.insertMakes(make.getMakeObjects());
            }

            @Override
            public void failure(RetrofitError error) {
                GCLog.e("error: " + error.getMessage());
            }
        });
    }

    private void performModelRequest() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.METHOD_LABEL, Constants.MODEL_ALL_METHOD);
        RetrofitRequest.makeModelRequest(context, params, new Callback<Model>() {
            @Override
            public void success(Model model, retrofit.client.Response response) {
                MakeModelVersionDB modelDB = ApplicationController.getMakeModelVersionDB();
                modelDB.insertModels(model.getModels());
            }

            @Override
            public void failure(RetrofitError error) {
                GCLog.e("error: " + error.getMessage());
            }
        });
    }

    private void performVersionRequest() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.METHOD_LABEL, Constants.VERSION_ALL_METHOD);
        RetrofitRequest.makeVersionRequest(context, params, new Callback<Version>() {
            @Override
            public void success(Version version, retrofit.client.Response response) {
                MakeModelVersionDB versionDB = ApplicationController.getMakeModelVersionDB();
                versionDB.insertVersions(version.getVersions());
            }

            @Override
            public void failure(RetrofitError error) {
                GCLog.e("error: " + error.getMessage());
            }
        });
    }

    private void performCityRequest() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(context, Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.METHOD_LABEL, Constants.CITY_METHOD);
        RetrofitRequest.makeCityRequest(context, params, new Callback<CityModel>() {
            @Override
            public void success(CityModel response, retrofit.client.Response res) {
                if (response.getStatus().equalsIgnoreCase("T")) {
                    if (response.getCityList().size() > 0) {
                        MakeModelVersionDB db = ApplicationController.getMakeModelVersionDB();
                        db.removeCities();
                        db.insertCities(response.getCityList());
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                GCLog.e("error: " + error.getMessage());
            }
        });
    }
}
