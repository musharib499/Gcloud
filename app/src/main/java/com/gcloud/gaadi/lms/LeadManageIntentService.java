package com.gcloud.gaadi.lms;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.ManageLeadDB;
import com.gcloud.gaadi.model.LeadData;
import com.gcloud.gaadi.syncadapter.GenericAccountService;
import com.gcloud.gaadi.syncadapter.SyncUtils;
import com.gcloud.gaadi.ui.LeadFollowUpActivity;
import com.gcloud.gaadi.utils.GCLog;

import org.joda.time.DateTime;

import java.util.Calendar;

/**
 * Created by Gaurav on 10-06-2015.
 */
public class LeadManageIntentService extends IntentService {

    final static public String ACTION_ID = "actionId";
    public static final String LEAD_DATA = "leadData";
    public static final String VALUES = "values";
    public static final String NOTIFICATION_TIME = "notificationTime";
    private static final String TAG = "LeadManageIntentService";
    private final Uri LMS_URI = Uri.parse(new StringBuilder().append("content://")
            .append(Constants.LMS_CONTENT_AUTHORITY).append("/").append(ManageLeadDB.TABLE_NAME).toString());

    public LeadManageIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GCLog.w(TAG, "onHandleIntent");
        if (intent.getExtras() != null) {
            Bundle data = intent.getExtras();
            boolean fromAlarmReset = data.containsKey(LMSResetAlarmService.FROM_ALRAM_RESET);
            LeadData leadData = (LeadData) data.get(LEAD_DATA);
            String currentStatus = leadData.getStatus();
            GCLog.w(TAG, "currentStatus: " + currentStatus);
            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
            switch ((ACTION) data.get(ACTION_ID)) {
                case INSERT:
                    break;
                case UPDATE:
                    if (getStatus(leadData.getNumber()).isEmpty()) {
                        GCLog.w(TAG, "status empty");
                        leadData.setRequestCode((Integer.valueOf(leadData.getLeadId()) * 10));
                        insert(leadData, 0);
                    } else {
                        leadData.setRequestCode(getRequestCode(leadData.getNumber()));
                    }
                    long notificationTime = data.getLong(NOTIFICATION_TIME);
                    //GCLog.w(TAG, "original time: "+notificationTime);

                    while (isSlotAvailable(notificationTime) && !fromAlarmReset) {
                        notificationTime += 60 * 1000;
                    }
                    //GCLog.w(TAG, "Available time: " + notificationTime);

                    if (!fromAlarmReset) {
                        ContentValues values = new ContentValues();
                        values.put(NOTIFICATION_TIME, Long.toString(notificationTime));
                        values.put(ManageLeadDB.LMS_STATUS, leadData.getStatus());
                        values.put(ManageLeadDB.SERVER_STATUS, getApiStatus(leadData.getStatus()));
                        values.put(ManageLeadDB.CURRENT_TIMESTAMP, String.valueOf(DateTime.now().getMillis()));
                        values.put(ManageLeadDB.SYNCED, 0);
                        getContentResolver().update(LMS_URI,
                                values,
                                ManageLeadDB.NUMBER + " = ?",
                                new String[]{leadData.getNumber()});
                        /*ApplicationController.getManageLeadDB().updateNotificationTime(leadData.getNumber(),
                                notificationTime,
                                leadData.getStatus(),
                                getApiStatus(leadData.getStatus()));*/
                        ContentResolver.requestSync(GenericAccountService.GetAccount(SyncUtils.ACCOUNT_TYPE),
                                Constants.LMS_CONTENT_AUTHORITY,
                                new Bundle());
                    }
                    manager.set(AlarmManager.RTC_WAKEUP,
                            notificationTime,
                            getPendingIntent(leadData,
                                    (currentStatus.equals(LeadFollowUpActivity.WALK_IN_SCHEDULE) ?
                                            LMSNotificationService.WalkInState.WALK_IN_CURRENT
                                            : null)
                                    , 0));
                    if (currentStatus.equals(LeadFollowUpActivity.WALK_IN_SCHEDULE)) {
                        GCLog.w(TAG, "entered walk in");
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(notificationTime);
                        if (calendar.get(Calendar.DAY_OF_MONTH) != Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                            calendar.set(Calendar.HOUR_OF_DAY, 8); //notification at 8 am on the day
                            calendar.set(Calendar.MINUTE, 0);
                            manager.set(AlarmManager.RTC_WAKEUP,
                                    calendar.getTimeInMillis(),
                                    getPendingIntent(leadData, LMSNotificationService.WalkInState.WALK_IN_TODAY, 1));
                        }
                        calendar.setTimeInMillis(notificationTime);
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                        long nextDayNotificationTime = calendar.getTimeInMillis() + 1000 + (long) 8 * 60 * 60 * 1000; // 8 am next day morning notification will be received
                        manager.set(AlarmManager.RTC_WAKEUP,
                                nextDayNotificationTime,
                                getPendingIntent(leadData, LMSNotificationService.WalkInState.WALK_IN_YESTERDAY, 2));
                    } else {
                        GCLog.w(TAG, "cant enter walk in");
                    }
                    break;
                case DELETE:
                    break;
            }
        }
    }

    private int getRequestCode(String number) {
        int result = 0;
        Cursor cursor = getContentResolver().query(LMS_URI,
                new String[]{ManageLeadDB.REQUEST_CODE},
                ManageLeadDB.NUMBER + " = ?", new String[]{number},
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result = cursor.getInt(cursor.getColumnIndex(ManageLeadDB.REQUEST_CODE));
                cursor.close();
            }
        }
        return result;
    }

    private void insert(LeadData leadData, int synced) {
        ContentValues values = new ContentValues();
        values.put(ManageLeadDB.LEAD_ID, leadData.getLeadId());
        values.put(ManageLeadDB.NAME, leadData.getName());
        values.put(ManageLeadDB.MAKE_ID, leadData.getMakeId());
        values.put(ManageLeadDB.MODEL_NAME, leadData.getModelName());
//        values.put(LEAD_DATE, leadData.getDate());
        values.put(ManageLeadDB.NOTIFICATION_TIME, "0");
        values.put(ManageLeadDB.LMS_STATUS, leadData.getStatus());
        values.put(ManageLeadDB.SERVER_STATUS, leadData.getApiStatus());
        values.put(ManageLeadDB.NUMBER, leadData.getNumber());
        values.put(ManageLeadDB.CURRENT_TIMESTAMP, String.valueOf(DateTime.now().getMillis()));
        values.put(ManageLeadDB.REQUEST_CODE, leadData.getRequestCode());
        values.put(ManageLeadDB.SYNCED, synced);
        getContentResolver().insert(LMS_URI, values);
    }

    private String getStatus(String number) {
        String status = "";
        Cursor cursor = getContentResolver().query(LMS_URI,
                new String[]{ManageLeadDB.LMS_STATUS},
                ManageLeadDB.NUMBER + " = ?",
                new String[]{number},
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                status = cursor.getString(cursor.getColumnIndex(ManageLeadDB.LMS_STATUS));
            }
            cursor.close();
        }
        return status;
    }

    private boolean isSlotAvailable(long time) {
        boolean result = true;
        Cursor cursor = getContentResolver().query(LMS_URI, new String[]{ManageLeadDB.NOTIFICATION_TIME},
                ManageLeadDB.NOTIFICATION_TIME + " = ?", new String[]{String.valueOf(time)},
                null);
        if (cursor != null) {
            result = cursor.moveToFirst();
            cursor.close();
        }
        return result;
    }

    private String getApiStatus(String status) {
        switch (status) {
            case LeadFollowUpActivity.NOT_YET_CALLED:
                return "";
            case LeadFollowUpActivity.FOLLOW_UP_SCHEDULE:
                return "Hot";
            case LeadFollowUpActivity.WALK_IN_SCHEDULE:
                return "WalkInScheduled";
            case LeadFollowUpActivity.MARK_CLOSED:
                return "Closed";
        }
        return "";
    }

    private PendingIntent getPendingIntent(LeadData data, LMSNotificationService.WalkInState walkInState, int requestCode) {
        Intent intent = new Intent(this, LMSNotificationService.class);
        intent.putExtra(LEAD_DATA, data);
        if (walkInState != null) {
            intent.putExtra(LMSNotificationService.WALK_IN_STATE, walkInState);
        }
        GCLog.w(TAG, "request code: " + (data.getRequestCode() + requestCode));
        return PendingIntent.getService(this, (data.getRequestCode() + requestCode),
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public enum ACTION {INSERT, UPDATE, DELETE}
}
