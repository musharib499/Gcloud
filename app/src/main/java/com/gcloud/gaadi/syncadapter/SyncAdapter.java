package com.gcloud.gaadi.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.ManageLeadDB;
import com.gcloud.gaadi.lms.LMSAlarmRescheduleService;
import com.gcloud.gaadi.lms.LMSServerDataPullModel;
import com.gcloud.gaadi.lms.LMSServerDataPushModel;
import com.gcloud.gaadi.model.LeadData;
import com.gcloud.gaadi.model.LeadDetailModel;
import com.gcloud.gaadi.retrofit.RetrofitFieldMap;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.LeadFollowUpActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;

/**
 * Created by Gaurav on 25-08-2015.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private final ContentResolver mContentResolver;
    private final Uri LMS_URI = Uri.parse(new StringBuilder().append("content://")
            .append(Constants.LMS_CONTENT_AUTHORITY).append("/").append(ManageLeadDB.TABLE_NAME).toString());
    private Context context;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
        this.context = context;
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
        this.context = context;
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              SyncResult syncResult) {
        //CommonUtils.showToast(context, "onPerformSync", Toast.LENGTH_SHORT);
        //GCLog.e("gaurav onPerformSync");

        boolean pullSuccess = false;
        int chunkSize = 50;
        ManageLeadDB db = ApplicationController.getManageLeadDB();

        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.METHOD_LABEL, Constants.METHOD_LMS_PULL_DATA);
        params.put(Constants.DEALER_USERNAME,
                CommonUtils.getStringSharedPreference(context, Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.DEALER_PASSWORD,
                CommonUtils.getStringSharedPreference(context, Constants.UC_DEALER_PASSWORD, ""));
        //params.put(Constants.RPP, "20"); // linked to hasNext variable in pull request
        params.put("datetime",
                MillisToSQLTime(CommonUtils.getLongSharedPreference(context,
                        Constants.LMS_LAST_SYNCED_TIME,
                        DateTime.now().getMillis())));
        int page = 0;
        try {
            boolean hasNext;
            do {
                hasNext = false;
                params.put("page", String.valueOf(++page));
                LMSServerDataPullModel response = RetrofitRequest.pullLMSData(context, params);
                if (response.getStatus().equals("T")) {
                    ArrayList<String> mobileNumbers = new ArrayList<>();
                    long latestSyncTime = CommonUtils.getLongSharedPreference(context,
                            Constants.LMS_LAST_SYNCED_TIME,
                            DateTime.now().getMillis());

                    for (LeadDetailModel model : response.getLeadDetailModelArrayList()) {
                        LeadData leadData = getCompleteData(mContentResolver.query(
                                LMS_URI,
                                null, ManageLeadDB.NUMBER + " = ?", new String[]{model.getNumber()}, null));
                        if (leadData != null) {
                            if (leadData.getChangeTime() < SQLTimeToMillis(model.getChangeTime())) {
                                /*GCLog.e("onPerformSync mobile: " + model.getNumber());
                                leadData.setName(model.getName());
                                leadData.setMakeId(model.getMakename());
                                leadData.setModelName(model.getModel());
                                leadData.setApiStatus(model.getLeadStatus());
                                leadData.setStatus(getLMSStatus(model.getLeadStatus()));
                                leadData.setNotificationTime(SQLTimeToMillis(model.getFollowDate()));
                                leadData.setChangeTime(SQLTimeToMillis(model.getChangeTime()));*/
                                ContentValues values = new ContentValues();
                                values.put(ManageLeadDB.NAME, model.getName());
                                values.put(ManageLeadDB.MAKE_ID, model.getMakename());
                                values.put(ManageLeadDB.MODEL_NAME, model.getModel());
                                values.put(ManageLeadDB.NOTIFICATION_TIME, String.valueOf(SQLTimeToMillis(model.getFollowDate())));
                                values.put(ManageLeadDB.LMS_STATUS, getLMSStatus(model.getLeadStatus()));
                                values.put(ManageLeadDB.SERVER_STATUS, model.getLeadStatus());
                                values.put(ManageLeadDB.CURRENT_TIMESTAMP, String.valueOf(SQLTimeToMillis(model.getChangeTime())));
                                values.put(ManageLeadDB.SYNCED, 1);

                                mContentResolver.update(LMS_URI,
                                        values,
                                        ManageLeadDB.NUMBER + " = ?",
                                        new String[]{model.getNumber()});
                                mobileNumbers.add(model.getNumber());
                            }
                        }

                        latestSyncTime = (latestSyncTime < SQLTimeToMillis(model.getChangeTime()))
                                ? SQLTimeToMillis(model.getChangeTime())
                                : latestSyncTime;
                    }
                    CommonUtils.setLongSharedPreference(context, Constants.LMS_LAST_SYNCED_TIME, latestSyncTime);

                    if (!mobileNumbers.isEmpty()) {
                        Intent intent = new Intent(context, LMSAlarmRescheduleService.class);
                        intent.putExtra(Constants.UPDATED_MOBILE_NUMBERS, mobileNumbers);
                        if (context.startService(intent) == null) {
                            GCLog.e("AlarmRescheduleService has not been started");
                        }
                    }
                    //GCLog.e("datetime size: " + response.getLeadDetailModelArrayList().size());
                    hasNext = response.getLeadDetailModelArrayList().size() >= response.getChunkSize(); // linked to rpp param
                    pullSuccess = !hasNext;
                    chunkSize = response.getChunkSize();
                } else {
                    GCLog.e("LMS Pull failed");
                }
            } while (hasNext);
        } catch (RetrofitError error) {
            GCLog.e("LMS Pull failed");
            error.printStackTrace();
        } catch (Exception e) {
            GCLog.e("LMS Pull failed");
            e.printStackTrace();
        }

        if (pullSuccess) {
            try {
                params.put(Constants.METHOD_LABEL, Constants.METHOD_LMS_PUSH_DATA);
                JSONObject jsonParams = new JSONObject();
                jsonParams.put(Constants.DEALER_USERNAME,
                        CommonUtils.getStringSharedPreference(context, Constants.UC_DEALER_USERNAME, ""));
                jsonParams.put(Constants.DEALER_PASSWORD,
                        CommonUtils.getStringSharedPreference(context, Constants.UC_DEALER_PASSWORD, ""));
                JSONObject jsonObject = new JSONObject();
                ArrayList<LeadData> unSyncedData = getUnsyncedData(
                        mContentResolver.query(LMS_URI,
                                null, ManageLeadDB.SYNCED + " = ?", new String[]{String.valueOf(0)}, null));
                //GCLog.e("gaurav unsynced size: "+ unSyncedData.size());
                if (unSyncedData != null && !unSyncedData.isEmpty()) {
                    int count = 0;
                    //for (LeadData leadData : unSyncedData) {
                    do {
                        JSONArray array = new JSONArray();
                        for (int i = count; i < chunkSize + count && i < unSyncedData.size(); i++) {
                            LeadData leadData = unSyncedData.get(i);
                            jsonObject.put("mobileNumber", leadData.getNumber());
                            jsonObject.put("changeTime", MillisToSQLTime(leadData.getChangeTime()));
                            jsonObject.put("followTime", MillisToSQLTime(leadData.getNotificationTime()));
                            jsonObject.put("status", leadData.getApiStatus());
                            array.put(jsonObject);
                        }
                        jsonParams.put("data", array);
                        params.put("data", new RetrofitFieldMap(context, jsonParams).jsonParams());

                        try {
                            LMSServerDataPushModel response1 = RetrofitRequest.pushLMSData(context, params);
                            if (!response1.getStatus().equals("T")) {
                                GCLog.e("LMS Push failed");
                            } else {
                                for (LMSServerDataPushModel.LMSServerDataPushObjectModel model : response1.getSuccessArrayList()) {
                                    if (model.getStatus().equals("T")) {
                                        ContentValues values = new ContentValues();
                                        values.put(ManageLeadDB.SYNCED, 1);
                                        mContentResolver.update(LMS_URI,
                                                values,
                                                ManageLeadDB.NUMBER + " = ?",
                                                new String[]{model.getNumber()});
                                    }
                                }
                            }
                        } catch (RetrofitError error) {
                            GCLog.e("LMS Push failed");
                            error.printStackTrace();
                            break;
                        } catch (Exception e) {
                            GCLog.e("LMS Push failed");
                            e.printStackTrace();
                            break;
                        }
                        array = null;
                        count += chunkSize;
                    } while (count < unSyncedData.size());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<LeadData> getUnsyncedData(Cursor cursor) {
        ArrayList<LeadData> result = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                result.add(getCompleteData(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return result;
    }

    private LeadData getCompleteData(Cursor cursor) {
        LeadData result = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                try {
                    result = new LeadData();
                    result.setRequestCode(cursor.getInt(cursor.getColumnIndex(ManageLeadDB.REQUEST_CODE)));
                    result.setLeadId(cursor.getString(cursor.getColumnIndex(ManageLeadDB.LEAD_ID)));
                    result.setNumber(ManageLeadDB.NUMBER);
                    result.setStatus(cursor.getString(cursor.getColumnIndex(ManageLeadDB.LMS_STATUS)));
                    result.setApiStatus(cursor.getString(cursor.getColumnIndex(ManageLeadDB.SERVER_STATUS)));
                    result.setMakeId(cursor.getString(cursor.getColumnIndex(ManageLeadDB.MAKE_ID)));
                    result.setModelName(cursor.getString(cursor.getColumnIndex(ManageLeadDB.MODEL_NAME)));
                    result.setName(cursor.getString(cursor.getColumnIndex(ManageLeadDB.NAME)));
                    result.setNotificationTime(Long.valueOf(cursor.getString(cursor.getColumnIndex(ManageLeadDB.NOTIFICATION_TIME))));
                    result.setChangeTime(Long.valueOf(cursor.getString(cursor.getColumnIndex(ManageLeadDB.CURRENT_TIMESTAMP))));
                } catch (NullPointerException e) {
                    Crashlytics.logException(e);
                    result = null;
                }
            }
            cursor.close();
        }
        return result;
    }

    private String getLMSStatus(String leadStatus) {
        switch (leadStatus) {
            case "Cold":
            case "Warm":
            case "Hot":
                return LeadFollowUpActivity.FOLLOW_UP_SCHEDULE;
            case "Closed":
            case "Booked":
            case "Converted":
            case "Walked-in":
                return LeadFollowUpActivity.MARK_CLOSED;
            case "WalkInScheduled":
                return LeadFollowUpActivity.WALK_IN_SCHEDULE;
            default:
                return LeadFollowUpActivity.NOT_YET_CALLED;
        }
    }

    private long SQLTimeToMillis(String mySQLTime) {
        //GCLog.e("server time: "+mySQLTime);
        if (mySQLTime == null || mySQLTime.isEmpty()) {
            return 0;
        }
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-d H:m:s");
        DateTime dateTime = DateTime.parse(mySQLTime, formatter);
        return dateTime.getMillis();
    }

    private String MillisToSQLTime(long millis) {
        DateTime dateTime = new DateTime(millis);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-d H:m:s");
        return dateTime.toString(formatter);
    }
}
