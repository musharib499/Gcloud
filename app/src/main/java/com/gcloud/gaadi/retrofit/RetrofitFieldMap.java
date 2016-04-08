package com.gcloud.gaadi.retrofit;

import android.content.Context;

import com.gcloud.gaadi.BuildConfig;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ankit on 23/1/15.
 */
public class RetrofitFieldMap {

    //private Response.Listener<UpgradeCheckModel> mListener;
    //private Response.ErrorListener mErrorErrorListener;
    private Context mContext;
    private HashMap<String, String> mParams;
    private JSONObject jsonObject;

    public RetrofitFieldMap(Context context,
                            HashMap<String, String> params) {
        mContext = context;
        if (params == null) {
            mParams = new HashMap<>();
        } else {
            mParams = params;
        }

        if (CommonUtils.getIntSharedPreference(context, Constants.SFA_USER_ID, 0) != 0) {
            mParams.put("sfa_user_id",
                    String.valueOf(CommonUtils.getIntSharedPreference(context, Constants.SFA_USER_ID, 0)));
        }

    }

    public RetrofitFieldMap(Context context, JSONObject jsonObject) {
        mContext = context;

        if (jsonObject == null) {
            this.jsonObject = new JSONObject();
        } else {
            this.jsonObject = jsonObject;
        }
    }


    public Map<String, String> getParams() {
        mParams.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        mParams.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
        // mParams.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(mContext, Constants.UC_DEALER_USERNAME, ""));
        mParams.put(Constants.UCDID, String.valueOf(CommonUtils.getIntSharedPreference(mContext, Constants.UC_DEALER_ID, -1)));
        mParams.put(Constants.OWNER, BuildConfig.CLOUD_OWNER);
        mParams.put(Constants.APP_NAME, CommonUtils.getStringSharedPreference(mContext, Constants.APP_PACKAGE_NAME, ""));
        mParams.put(Constants.PLATFORM_SOURCE, "ANDROID_APP");
        mParams.put(Constants.SERVICE_EXECUTIVE_ID, CommonUtils.getStringSharedPreference(mContext, Constants.SERVICE_EXECUTIVE_ID, ""));
        mParams.put(Constants.ANDROID_ID, CommonUtils.getStringSharedPreference(mContext, Constants.ANDROID_ID, ""));
        mParams.put(Constants.UC_DEALER_USERNAME, CommonUtils.getStringSharedPreference(mContext, Constants.UC_DEALER_USERNAME, ""));
        mParams.put(Constants.SERVICE_EXECUTIVE_LOGIN, String.valueOf(CommonUtils.getBooleanSharedPreference(mContext, Constants.SERVICE_EXECUTIVE_LOGIN, false)));
        mParams.put(Constants.APP_VERSION, CommonUtils.getStringSharedPreference(mContext, Constants.APP_VERSION_CODE, ""));
        return mParams;
    }

    public Map<String, String> getTruPriceParams() {
        mParams.put(Constants.API_KEY_LABEL, Constants.TRU_PRICE_API_KEY);
        mParams.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
        // mParams.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(mContext, Constants.UC_DEALER_USERNAME, ""));
        mParams.put(Constants.UCDID, String.valueOf(CommonUtils.getIntSharedPreference(mContext, Constants.UC_DEALER_ID, -1)));
        mParams.put(Constants.OWNER, BuildConfig.CLOUD_OWNER);
        mParams.put("api_source","gcloud");
        mParams.put("api_sub_source", "android");
        mParams.put(Constants.APP_NAME, CommonUtils.getStringSharedPreference(mContext, Constants.APP_PACKAGE_NAME, ""));
        mParams.put(Constants.PLATFORM_SOURCE, "ANDROID_APP");
        mParams.put(Constants.SERVICE_EXECUTIVE_ID, CommonUtils.getStringSharedPreference(mContext, Constants.SERVICE_EXECUTIVE_ID, ""));
        mParams.put(Constants.ANDROID_ID, CommonUtils.getStringSharedPreference(mContext, Constants.ANDROID_ID, ""));
        mParams.put(Constants.UC_DEALER_USERNAME, CommonUtils.getStringSharedPreference(mContext, Constants.UC_DEALER_USERNAME, ""));
        mParams.put(Constants.SERVICE_EXECUTIVE_LOGIN, String.valueOf(CommonUtils.getBooleanSharedPreference(mContext, Constants.SERVICE_EXECUTIVE_LOGIN, false)));
        mParams.put(Constants.APP_VERSION, CommonUtils.getStringSharedPreference(mContext, Constants.APP_VERSION_CODE, ""));
        return mParams;
    }


    public Map<String, String> getSellerLeadParams() {
        mParams.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        mParams.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
        mParams.put(Constants.UCDID, String.valueOf(CommonUtils.getIntSharedPreference(mContext, Constants.UC_DEALER_ID, -1)));
        // mParams.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(mContext, Constants.UC_DEALER_USERNAME, ""));

        mParams.put(Constants.OWNER, BuildConfig.CLOUD_OWNER);

        mParams.put(Constants.APP_NAME, CommonUtils.getStringSharedPreference(mContext, Constants.APP_PACKAGE_NAME, ""));

        mParams.put(Constants.SERVICE_EXECUTIVE_ID, CommonUtils.getStringSharedPreference(mContext, Constants.SERVICE_EXECUTIVE_ID, ""));
        mParams.put(Constants.ANDROID_ID, CommonUtils.getStringSharedPreference(mContext, Constants.ANDROID_ID, ""));
        mParams.put(Constants.UC_DEALER_USERNAME, CommonUtils.getStringSharedPreference(mContext, Constants.UC_DEALER_USERNAME, ""));
        mParams.put(Constants.SERVICE_EXECUTIVE_LOGIN, String.valueOf(CommonUtils.getBooleanSharedPreference(mContext, Constants.SERVICE_EXECUTIVE_LOGIN, false)));
        mParams.put(Constants.APP_VERSION, CommonUtils.getStringSharedPreference(mContext, Constants.APP_VERSION_CODE, ""));
        return mParams;
    }

    public String jsonParams() {
        try {
            if (CommonUtils.getIntSharedPreference(mContext, Constants.SFA_USER_ID, 0) != 0) {
                jsonObject.put("sfa_user_id",
                        CommonUtils.getIntSharedPreference(mContext, Constants.SFA_USER_ID, 0));
            }
            jsonObject.put(Constants.API_KEY_LABEL, Constants.API_KEY);
            jsonObject.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
            jsonObject.put(Constants.OWNER, BuildConfig.CLOUD_OWNER);
            jsonObject.put(Constants.UCDID, CommonUtils.getIntSharedPreference(mContext, Constants.UC_DEALER_ID, -1));
            jsonObject.put(Constants.PLATFORM_SOURCE, "ANDROID_APP");
            jsonObject.put(Constants.APP_NAME, CommonUtils.getStringSharedPreference(mContext, Constants.APP_PACKAGE_NAME, ""));
            jsonObject.put(Constants.SERVICE_EXECUTIVE_ID, CommonUtils.getStringSharedPreference(mContext, Constants.SERVICE_EXECUTIVE_ID, ""));
            jsonObject.put(Constants.ANDROID_ID, CommonUtils.getStringSharedPreference(mContext, Constants.ANDROID_ID, ""));
            jsonObject.put(Constants.UC_DEALER_USERNAME, CommonUtils.getStringSharedPreference(mContext, Constants.UC_DEALER_USERNAME, ""));
            jsonObject.put(Constants.SERVICE_EXECUTIVE_LOGIN, String.valueOf(CommonUtils.getBooleanSharedPreference(mContext, Constants.SERVICE_EXECUTIVE_LOGIN, false)));
            jsonObject.put(Constants.APP_VERSION, CommonUtils.getStringSharedPreference(mContext, Constants.APP_VERSION_CODE, ""));

        } catch (Exception e) {

            GCLog.e("exception: " + e.getMessage());
        }
        GCLog.e("json: " + jsonObject.toString());
        return jsonObject.toString();
    }

    public Map<String, String> getInsuranceParams() {
        mParams.put(Constants.API_KEY_LABEL_INSURANCE, Constants.INSURANCE_API_KEY);
        mParams.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);

        mParams.put(Constants.UCDID, String.valueOf(CommonUtils.getIntSharedPreference(mContext, Constants.UC_DEALER_ID, -1)));

        //mParams.put(Constants.UCDID, "1125");
        mParams.put(Constants.OWNER, BuildConfig.CLOUD_OWNER);

        mParams.put(Constants.APP_NAME, CommonUtils.getStringSharedPreference(mContext, Constants.APP_PACKAGE_NAME, ""));
        mParams.put(Constants.PLATFORM_SOURCE, "ANDROID_APP");
        mParams.put(Constants.SERVICE_EXECUTIVE_ID, CommonUtils.getStringSharedPreference(mContext, Constants.SERVICE_EXECUTIVE_ID, ""));
        mParams.put(Constants.ANDROID_ID, CommonUtils.getStringSharedPreference(mContext, Constants.ANDROID_ID, ""));
        mParams.put(Constants.UC_DEALER_USERNAME, CommonUtils.getStringSharedPreference(mContext, Constants.UC_DEALER_USERNAME, ""));
        mParams.put(Constants.SERVICE_EXECUTIVE_LOGIN, String.valueOf(CommonUtils.getBooleanSharedPreference(mContext, Constants.SERVICE_EXECUTIVE_LOGIN, false)));
        mParams.put(Constants.APP_VERSION, CommonUtils.getStringSharedPreference(mContext, Constants.APP_VERSION_CODE, ""));
        return mParams;

    }

    public Map<String, String> stockImagejsonParams() {
        try {
            mParams.put(Constants.API_KEY_LABEL, Constants.API_KEY);
            mParams.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
            mParams.put(Constants.OWNER, BuildConfig.CLOUD_OWNER);
            mParams.put(Constants.UCDID, String.valueOf(CommonUtils.getIntSharedPreference(mContext, Constants.UC_DEALER_ID, -1)));
            //jsonObject.put(Constants.OWNER, BuildConfig.CLOUD_OWNER);
            mParams.put(Constants.PLATFORM_SOURCE, "Android GCloud App");
            mParams.put(Constants.APP_NAME, CommonUtils.getStringSharedPreference(mContext, Constants.APP_PACKAGE_NAME, ""));
            mParams.put(Constants.SERVICE_EXECUTIVE_ID, CommonUtils.getStringSharedPreference(mContext, Constants.SERVICE_EXECUTIVE_ID, ""));
            mParams.put(Constants.ANDROID_ID, CommonUtils.getStringSharedPreference(mContext, Constants.ANDROID_ID, ""));
            mParams.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(mContext, Constants.UC_DEALER_USERNAME, ""));
            mParams.put(Constants.SERVICE_EXECUTIVE_LOGIN, String.valueOf(CommonUtils.getBooleanSharedPreference(mContext, Constants.SERVICE_EXECUTIVE_LOGIN, false)));
            mParams.put(Constants.APP_VERSION, CommonUtils.getStringSharedPreference(mContext, Constants.APP_VERSION_CODE, ""));

        } catch (Exception e) {

            GCLog.e("exception: " + e.getMessage());
        }

        return mParams;
    }

}
