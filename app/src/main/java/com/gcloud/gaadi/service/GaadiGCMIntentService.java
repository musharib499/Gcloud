package com.gcloud.gaadi.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by ankitgarg on 01/04/15.
 */
public class GaadiGCMIntentService extends IntentService implements OnNoInternetConnectionListener {

    public GaadiGCMIntentService() {
        super("GaadiGCMIntentService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GaadiGCMIntentService(String name) {
        super("GaadiGCMIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        GCLog.e("messageType: " + messageType);
        try {
            String registrationId = gcm.register(Constants.GCM_SENDER_ID);
            GCLog.e("GCM Registration ID : " + registrationId);
            CommonUtils.setStringSharedPreference(getBaseContext(), Constants.GAADI_GCM_ID, registrationId);

            sendRegIdToServer();

        } catch (Exception e) {
            GCLog.e("GCM Registration exception: " + e.getMessage());
        }
    }


    private void sendRegIdToServer() {
        String phoneModel = android.os.Build.MODEL;
        phoneModel = phoneModel.replaceAll("\\s+", "");
        String androidVersion = android.os.Build.VERSION.RELEASE;
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(
                    getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        }
        String appVersion = pInfo.versionName;

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("device_id", CommonUtils.getStringSharedPreference(this, Constants.ANDROID_ID, ""));
        params.put("gcm_id", CommonUtils.getStringSharedPreference(this, Constants.GAADI_GCM_ID, ""));
        params.put("dealer_id", String.valueOf(CommonUtils.getIntSharedPreference(this, Constants.DEALER_ID, -1)));
        params.put("dealer_email_id", CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_EMAIL, ""));
        params.put("android_ver", androidVersion);
        params.put("app_version", appVersion);
        params.put("phone_model", phoneModel);
        params.put(Constants.METHOD_LABEL, Constants.PUSH_DEALER_REGISTRATION);
        GCLog.e("App Version" + appVersion);

        RetrofitRequest.registerGCMIdRequest(params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, retrofit.client.Response response) {
                if (generalResponse != null) {
                    if ("T".equalsIgnoreCase(generalResponse.getStatus())) {
                        CommonUtils.setBooleanSharedPreference(ApplicationController.getInstance(), Constants.GCM_ID_SENT_TO_SERVER, true);
                        if (CommonUtils.canLog()) {
                            GCLog.e("Inside if After GCM Registration request to server" + generalResponse.toString());
                        }

                    } else {
                        if (CommonUtils.canLog()) {
                            GCLog.e("Inside else After GCM Registration request to server " + generalResponse.toString());
                        }
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (CommonUtils.canLog()) {
                    GCLog.e("Error while registering: " + error.getMessage());
                }
            }
        });

        /*GCMRegisteredUserRequest gcmRegisteredUserRequest = new GCMRegisteredUserRequest(this, Request.Method.POST, params,
                Constants.getWebServiceURL(this),
                new Response.Listener<GeneralResponse>() {
                    @Override
                    public void onResponse(GeneralResponse response) {


                        if (response != null) {
                            if ("T".equalsIgnoreCase(response.getStatus())) {
                                CommonUtils.setBooleanSharedPreference(getBaseContext(), Constants.GCM_ID_SENT_TO_SERVER, true);
                                GCLog.e("Inside if After GCM Registration request to server" + response.toString());

                            } else {
                                GCLog.e("Inside else After GCM Registration request to server " + response.toString());
                            }
                        }

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        GCLog.e("Some problem occurred");

                    }
                });

        gcmRegisteredUserRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        ApplicationController.getInstance().addToRequestQueue(gcmRegisteredUserRequest, Constants.TAG_GCM_REGISTERED_REQUEST, false, this);*/
    }

    @Override
    public void onNoInternetConnection(NetworkEvent networkEvent) {


        GCLog.e("gaadi gcm intent service error in network");
    }
}
