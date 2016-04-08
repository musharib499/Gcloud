package com.gcloud.gaadi.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.net.Uri;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.LeadsOfflineDB;
import com.gcloud.gaadi.model.AddLeadModel;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.LeadDetailModel;
import com.gcloud.gaadi.model.LeadsModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.service.SyncStocksService;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.HashMap;

import retrofit.RetrofitError;

/**
 * Created by gauravkumar on 17/9/15.
 */
public class LeadsSyncAdapter extends AbstractThreadedSyncAdapter {

    private final Uri LEADS_URI = Uri.parse("content://"
            + Constants.LEADS_CONTENT_AUTHORITY + "/" + LeadsOfflineDB.TABLE_NAME);
    private final Uri LEADS_CALL_LOG_URI = Uri.parse("content://"
            + Constants.LEADS_CONTENT_AUTHORITY + "/" + LeadsOfflineDB.CALL_LOG_TABLE_NAME);
    private final Uri LEADS_OFFLINE_UPDATE_URI = Uri.parse("content://"
            + Constants.LEADS_CONTENT_AUTHORITY + "/" + LeadsOfflineDB.TABLE_OFFLINE_UPDATE);
    private Context context;

    public LeadsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
    }

    public LeadsSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        this.context = context;
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              SyncResult syncResult) {

        context.startService(new Intent(context, SyncStocksService.class));

        boolean hasNext = false, pullSuccess = false;
        int pageNumber = 0, chunkSize = 50;

        LeadsOfflineDB db = ApplicationController.getLeadsOfflineDB();

        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.METHOD_LABEL, Constants.LEADS_METHOD);
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(context, Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.DEALER_PASSWORD, CommonUtils.getStringSharedPreference(context, Constants.UC_DEALER_PASSWORD, ""));
        //params.put(Constants.RPP, "50");

        {
            // update the offline update saved data
            Cursor cursor = getOfflineData();
            Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        try {
                            final int rowId = cursor.getInt(cursor.getColumnIndex(LeadsOfflineDB.COLUMN_ID));
                            HashMap<String, String> localParams = new Gson()
                                    .fromJson(cursor.getString(cursor.getColumnIndex(LeadsOfflineDB.COLUMN_JSON)),
                                            type);

                            AddLeadModel model = RetrofitRequest.leadEditRequest(localParams);
                            if ("T".equalsIgnoreCase(model.getStatus())) {
                                deleteUpdatedRow(rowId);
                            }
                        } catch (RetrofitError | IllegalStateException error) {
                            Crashlytics.logException(error);
                        } finally {
                            cursor.moveToNext();
                        }
                    } while (!cursor.isAfterLast());
                }
                cursor.close();
            }
        }

        params.put("changetime",
                CommonUtils.MillisToSQLTime(CommonUtils.getLongSharedPreference(context,
                        Constants.LEADS_LAST_SYNCED_TIME,
                        (CommonUtils.getLongSharedPreference(context, Constants.SERVER_TIME_DIFFERENCE, 0) +
                                DateTime.now().minusMonths(1).withTimeAtStartOfDay().getMillis()))));

        {
            // Buyer
            do {
                params.put("pageNumber", String.valueOf(++pageNumber));
                try {
                    LeadsModel model = RetrofitRequest.getBuyerLeads(params);
                    long latestSyncTime = CommonUtils.getLongSharedPreference(context,
                            Constants.LEADS_LAST_SYNCED_TIME,
                            (CommonUtils.getLongSharedPreference(context, Constants.SERVER_TIME_DIFFERENCE, 0) +
                                    DateTime.now().minusMonths(1).withTimeAtStartOfDay().getMillis()));

                    if (model == null
                            || !model.getStatus().equalsIgnoreCase("T")
                            || model.getLeads().size() == 0) {
                        if (hasNext) {
                            latestSyncTime += 1000;
                            CommonUtils.setLongSharedPreference(context, Constants.LEADS_LAST_SYNCED_TIME, latestSyncTime);
                        }
                        hasNext = false;
                    } else {
                        //hasNext = model.getNextPossible();

                        for (LeadDetailModel detailModel : model.getLeads()) {
                            insertLeadDetailModel("Buyer", detailModel);

                            latestSyncTime = (latestSyncTime < CommonUtils.SQLTimeToMillis(detailModel.getChangeTime()))
                                    ? CommonUtils.SQLTimeToMillis(detailModel.getChangeTime())
                                    : latestSyncTime;
                        }

                        if (!model.getNextPossible()) {
                            latestSyncTime += 1000;
                        }
                        CommonUtils.setLongSharedPreference(context, Constants.LEADS_LAST_SYNCED_TIME, latestSyncTime);

                        hasNext = model.getLeads().size() >= model.getPageSize();
                        pullSuccess = !hasNext;
                        chunkSize = model.getPageSize();
                    }
                } catch (RetrofitError error) {
                    Crashlytics.logException(error.getCause());
                    hasNext = false;
                } catch (SQLiteDatabaseLockedException e) {
                    Crashlytics.logException(e.getCause());
                    hasNext = false;
                }
            } while (hasNext);
        }

        {
            // Buyer Today block

        }

        {
            // Buyer Past block

        }

        {
            // Buyer Upcoming block

        }

        {
            // Seller Not Yet Called block

        }

        {
            // Seller Today block

        }

        {
            // Seller Past block

        }

        {
            // Update call log
            Cursor cursor = getCallLogs();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    params.put(Constants.METHOD_LABEL, Constants.SENT_CARS_METHOD);
                    while (!cursor.isAfterLast()) {
                        try {
                        params.put(Constants.SHARE_SOURCE, cursor.getString(cursor.getColumnIndex(Constants.SHARE_SOURCE)));
                        params.put(Constants.SHARE_TYPE, cursor.getString(cursor.getColumnIndex(Constants.SHARE_TYPE)));
                        params.put(Constants.MOBILE_NUM, cursor.getString(cursor.getColumnIndex(Constants.MOBILE_NUM)));
                        params.put(Constants.CAR_ID, cursor.getString(cursor.getColumnIndex(Constants.CAR_ID)));
                        params.put("offline_datetime", cursor.getString(cursor.getColumnIndex(LeadsOfflineDB.CALL_LOG_TIME)));
                            GeneralResponse response = RetrofitRequest.uploadeventTracking(params);
                            if ("T".equals(response.getStatus())) {
                                deleteCallLog(cursor.getInt(cursor.getColumnIndex(LeadsOfflineDB.CALL_LOG_ID)));
                            }
                        } catch (RetrofitError error) {
                            GCLog.e(error.getMessage());
                        } catch (IllegalStateException e) {
                            Crashlytics.logException(e);
                        } finally {
                            cursor.moveToNext();
                        }
                    }
                }
                cursor.close();
            }

        }

        deleteOldData();
    }

    private Cursor getOfflineData() {
        return context.getContentResolver().query(LEADS_OFFLINE_UPDATE_URI, null, null, null, LeadsOfflineDB.COLUMN_ID);
    }

    private void deleteUpdatedRow(int rowId) {
        context.getContentResolver().delete(LEADS_OFFLINE_UPDATE_URI,
                LeadsOfflineDB.COLUMN_ID + " = ?", new String[]{String.valueOf(rowId)});
    }

    private Cursor getCallLogs() {
        return context.getContentResolver().query(LEADS_CALL_LOG_URI, null, null, null, null);
    }

    private void deleteCallLog(int callLogId) {
        context.getContentResolver().delete(LEADS_CALL_LOG_URI,
                LeadsOfflineDB.CALL_LOG_ID + " = ?", new String[]{String.valueOf(callLogId)});
    }

    private void deleteOldData() {
        String oldTime = String.valueOf(DateTime.now().minusMonths(1).withTimeAtStartOfDay().getMillis());
        context.getContentResolver().delete(LEADS_URI, LeadsOfflineDB.CHANGE_TIME + " < ?", new String[]{oldTime});
    }

    private void insertLeadDetailModel(String buyer, LeadDetailModel model) {
        ContentValues values = new ContentValues();
        values.put(LeadsOfflineDB.LEAD_TYPE, buyer);
        values.put(LeadsOfflineDB.FOLLOW_DATE_ANDROID, String.valueOf(CommonUtils.SQLTimeToMillis(model.getFollowDate())));
        values.put(LeadsOfflineDB.CHANGE_TIME, String.valueOf(CommonUtils.SQLTimeToMillis(model.getChangeTime())));
        values.put(LeadsOfflineDB.NAME, model.getName());
        values.put(LeadsOfflineDB.NUMBER, model.getNumber());
        values.put(LeadsOfflineDB.BUDGET_FROM, model.getBudgetfrom());
        values.put(LeadsOfflineDB.BUDGET_TO, model.getBudgetto());
        values.put(LeadsOfflineDB.SOURCE, model.getSource());
        values.put(LeadsOfflineDB.LEAD_STATUS, model.getLeadStatus());
        values.put(LeadsOfflineDB.VERIFIED, model.getVerified());
        values.put(LeadsOfflineDB.JSON_FORMAT, new Gson().toJson(model));

        context.getContentResolver().insert(LEADS_URI, values);
    }
}
