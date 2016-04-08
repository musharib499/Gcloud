package com.gcloud.gaadi.lms;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.ManageLeadDB;
import com.gcloud.gaadi.model.LeadData;
import com.gcloud.gaadi.utils.GCLog;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by Gaurav on 01-07-2015.
 */
public class LMSResetAlarmService extends IntentService {
    public static final String FROM_ALRAM_RESET = "fromAlarmReset";
    private final String TAG = "LMSResetAlarmService";

    public LMSResetAlarmService() {
        super("LMSResetAlarmService");
    }

    private final Uri LMS_URI = Uri.parse(new StringBuilder().append("content://")
            .append(Constants.LMS_CONTENT_AUTHORITY).append("/").append(ManageLeadDB.TABLE_NAME).toString());

    @Override
    protected void onHandleIntent(Intent intent) {
        GCLog.w(TAG, "onhandleintent");
        ArrayList<LeadData> leadDatas = getCompleteTable();
        GCLog.w(TAG, "count: " + leadDatas.size());
        for (LeadData leadData : leadDatas) {
            GCLog.w(TAG, "notification time: " + Long.toString(leadData.getNotificationTime()));
            Intent newIntent = new Intent(this, LeadManageIntentService.class);
            newIntent.putExtra(LeadManageIntentService.LEAD_DATA, leadData);
            newIntent.putExtra(LeadManageIntentService.ACTION_ID, LeadManageIntentService.ACTION.UPDATE);
            newIntent.putExtra(LeadManageIntentService.NOTIFICATION_TIME, leadData.getNotificationTime());
            newIntent.putExtra(FROM_ALRAM_RESET, true);
            startService(newIntent);
        }
    }

    private ArrayList<LeadData> getCompleteTable() {
        ArrayList<LeadData> result = new ArrayList<>();
        Cursor cursor = getContentResolver().query(LMS_URI, null,
                ManageLeadDB.NOTIFICATION_TIME + " > ?",
                new String[]{Long.toString(DateTime.now().getMillis() + 60000)}, // get data for which notification time
                null);                                                        // is 1 minute from now
        if (cursor.moveToFirst()) {
//            GCLog.w("Gaurav", "entered move to first count: "+cursor.getCount());
            while (!cursor.isAfterLast()) {
//                GCLog.w("Gaurav", "entered while");
                result.add(getLeadData(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return result;
    }

    private LeadData getLeadData(Cursor cursor) {
        LeadData leadData = new LeadData();
        leadData.setStatus(cursor.getString(cursor.getColumnIndex(ManageLeadDB.LMS_STATUS)));
        leadData.setName(cursor.getString(cursor.getColumnIndex(ManageLeadDB.NAME)));
        leadData.setNumber(cursor.getString(cursor.getColumnIndex(ManageLeadDB.NUMBER)));
        leadData.setLeadId(cursor.getString(cursor.getColumnIndex(ManageLeadDB.LEAD_ID)));
        leadData.setMakeId(cursor.getString(cursor.getColumnIndex(ManageLeadDB.MAKE_ID)));
        leadData.setModelName(cursor.getString(cursor.getColumnIndex(ManageLeadDB.MODEL_NAME)));
        leadData.setNotificationTime(Long.valueOf(cursor.getString(cursor.getColumnIndex(ManageLeadDB.NOTIFICATION_TIME))));
        leadData.setChangeTime(Long.valueOf(cursor.getString(cursor.getColumnIndex(ManageLeadDB.CURRENT_TIMESTAMP))));
        leadData.setApiStatus(cursor.getString(cursor.getColumnIndex(ManageLeadDB.SERVER_STATUS)));
        return leadData;
    }
}
