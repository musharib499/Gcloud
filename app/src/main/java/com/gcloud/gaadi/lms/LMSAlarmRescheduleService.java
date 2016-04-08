package com.gcloud.gaadi.lms;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.ManageLeadDB;
import com.gcloud.gaadi.model.LeadData;
import com.gcloud.gaadi.utils.GCLog;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Gaurav on 27-08-2015.
 * This will take input from SyncAdapter as ArrayList<String>
 * Will process all mobile Numbers in ArrayList
 * Cancel current alarms with invalid notification time and update rest
 */
public class LMSAlarmRescheduleService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    private final Uri LMS_URI = Uri.parse(new StringBuilder().append("content://")
            .append(Constants.LMS_CONTENT_AUTHORITY).append("/").append(ManageLeadDB.TABLE_NAME).toString());

    public LMSAlarmRescheduleService() {
        super("LMSAlarmRescheduleService");
    }

    @Override
    protected void onHandleIntent(Intent intent1) {
        Bundle data = intent1.getExtras();
        if (data != null) {
            ArrayList<String> mobileNumbers = (ArrayList<String>) data.get(Constants.UPDATED_MOBILE_NUMBERS);
            if (mobileNumbers != null) {
                ManageLeadDB db = ApplicationController.getManageLeadDB();
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                for (String mobileNumber : mobileNumbers) {
                    LeadData leadData = getCompleteData(mobileNumber);

                    alarmManager.cancel(PendingIntent.getService(this,
                            leadData.getRequestCode(),
                            new Intent(this, LMSNotificationService.class),
                            PendingIntent.FLAG_CANCEL_CURRENT));
                    if (leadData.getNotificationTime() > (Calendar.getInstance().getTimeInMillis() + 60 * 1000)) {
                        Intent intent = new Intent(this, LeadManageIntentService.class);
                        intent.putExtra(LeadManageIntentService.LEAD_DATA, leadData);
                        intent.putExtra(LeadManageIntentService.ACTION_ID, LeadManageIntentService.ACTION.UPDATE);
                        intent.putExtra(LeadManageIntentService.NOTIFICATION_TIME, leadData.getNotificationTime());
                        if (startService(intent) == null) {
                            GCLog.w("LMSAlarmRescheduleService", "start Service failed");
                        }
                    }
                }
            }
        }
    }

    private LeadData getCompleteData(String number) {
        LeadData result = new LeadData();
        Cursor cursor = getContentResolver().query(LMS_URI, null, ManageLeadDB.NUMBER + " = ?",
                new String[]{number}, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result.setRequestCode(cursor.getInt(cursor.getColumnIndex(ManageLeadDB.REQUEST_CODE)));
                result.setLeadId(cursor.getString(cursor.getColumnIndex(ManageLeadDB.LEAD_ID)));
                result.setNumber(number);
                result.setStatus(cursor.getString(cursor.getColumnIndex(ManageLeadDB.LMS_STATUS)));
                result.setApiStatus(cursor.getString(cursor.getColumnIndex(ManageLeadDB.SERVER_STATUS)));
                result.setMakeId(cursor.getString(cursor.getColumnIndex(ManageLeadDB.MAKE_ID)));
                result.setModelName(cursor.getString(cursor.getColumnIndex(ManageLeadDB.MODEL_NAME)));
                result.setName(cursor.getString(cursor.getColumnIndex(ManageLeadDB.NAME)));
                result.setNotificationTime(Long.valueOf(cursor.getString(cursor.getColumnIndex(ManageLeadDB.NOTIFICATION_TIME))));
                result.setChangeTime(Long.valueOf(cursor.getString(cursor.getColumnIndex(ManageLeadDB.CURRENT_TIMESTAMP))));
            } else {
                result = null;
            }
            cursor.close();
        } else {
            result = null;
        }
        return result;
    }
}
