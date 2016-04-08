package com.gcloud.gaadi.model;

import android.content.Context;

import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.utils.CommonUtils;

/**
 * Created by lakshaygirdhar on 1/10/15.
 */
public class CommonRequestParams {

    protected String apikey;
    protected String ANDROID_ID;
    protected String APP_VERSION;
    protected String username;
    protected String normal_password;
    protected String ucdid;
    protected String SERVICE_EXECUTIVE_ID;


    public CommonRequestParams(Context context) {
        apikey = Constants.API_KEY;
        ANDROID_ID = CommonUtils.getStringSharedPreference(context, Constants.ANDROID_ID, "");
        APP_VERSION = CommonUtils.getStringSharedPreference(context, Constants.APP_VERSION_CODE, "");
        ucdid = String.valueOf(CommonUtils.getIntSharedPreference(context, Constants.UC_DEALER_ID, -1));
        SERVICE_EXECUTIVE_ID = CommonUtils.getStringSharedPreference(context, Constants.SERVICE_EXECUTIVE_ID, "");
        username = CommonUtils.getStringSharedPreference(context, Constants.UC_DEALER_USERNAME, "");
        normal_password = CommonUtils.getStringSharedPreference(context, Constants.UC_DEALER_PASSWORD, "");
    }
}
