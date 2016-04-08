package com.gcloud.gaadi.retrofit;

import com.crashlytics.android.Crashlytics;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.Arrays;

import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;

/**
 * Created by ankitgarg on 21/10/15.
 */
public class CustomOKHttpClient extends OkClient {

    public CustomOKHttpClient() {

    }

    public CustomOKHttpClient(OkHttpClient okHttpClient) {
        super(okHttpClient);
    }

    @Override
    public Response execute(Request request) throws IOException {
        Response response = super.execute(request);
        GCLog.e(Constants.TAG, Arrays.toString(Thread.currentThread().getStackTrace()));

        GCLog.e(Constants.TAG, "executed request");
        try {
            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER, request.getMethod(), request.getUrl(), String.valueOf(response.getStatus()), response.getReason(), 0);
        } catch (Exception e) {
            Crashlytics.logException(e.getCause());
        }

        return response;
    }
}
