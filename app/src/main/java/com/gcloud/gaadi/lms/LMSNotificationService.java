package com.gcloud.gaadi.lms;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.ManageLeadDB;
import com.gcloud.gaadi.model.LeadData;
import com.gcloud.gaadi.ui.LeadFollowUpActivity;
import com.gcloud.gaadi.utils.GCLog;

/**
 * Created by Gaurav on 24-06-2015.
 */
public class LMSNotificationService extends IntentService {

    private static final String TAG = "LMSNotificationService";
    public static final String ACTION = "action";
    final public static String PERFORM_CALL = "performCall";
    public static final String REMIND_LATER = "remindLater";
    public static final String RESCHEDULE_WALK_IN = "reScheduleWalkIn";
    public static final String WALK_IN_STATE = "walkInState";
    public static final String WALKIN_YES = "walkInYes";
    public static final String WALKIN_NO = "walkInNo";
    public static final String FROM_NOTIFICATION = "fromNotification";
    private final Uri LMS_URI = Uri.parse(new StringBuilder().append("content://")
            .append(Constants.LMS_CONTENT_AUTHORITY).append("/").append(ManageLeadDB.TABLE_NAME).toString());

    public static enum WalkInState {WALK_IN_TODAY, WALK_IN_CURRENT, WALK_IN_YESTERDAY}

    ;

    public LMSNotificationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GCLog.w(TAG, "onHandleIntent");

        Context context = ApplicationController.getInstance();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            LeadData leadData = (LeadData) extras.get(LeadManageIntentService.LEAD_DATA);
            leadData = getCompleteData(leadData.getNumber());

            if (leadData == null) {
                return;
            }

            if (leadData.getStatus().equals(LeadFollowUpActivity.MARK_CLOSED)) {
                //Stop notification for Mark Closed Lead
                return;
            }
            int pendingRequestCode = leadData.getRequestCode();
            Intent callNow = new Intent(context, LeadFollowUpActivity.class);
            callNow.putExtra(LeadManageIntentService.LEAD_DATA, leadData);
            callNow.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            callNow.putExtra(ACTION, PERFORM_CALL);
            callNow.putExtra(FROM_NOTIFICATION, true);
            PendingIntent callNowPending = PendingIntent.getActivity(context,
                    (pendingRequestCode),
                    callNow,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            Intent remindLater = new Intent(context, LeadFollowUpActivity.class);
            remindLater.putExtra(LeadManageIntentService.LEAD_DATA, leadData);
            remindLater.putExtra(ACTION, REMIND_LATER);
            remindLater.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            remindLater.putExtra(FROM_NOTIFICATION, true);
            PendingIntent remindLaterPending = PendingIntent.getActivity(context,
                    (pendingRequestCode + 1),
                    remindLater,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true);
//            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.lms_notyetcalled_callback_custom_notification);
            switch (leadData.getStatus()) {
                case LeadFollowUpActivity.NOT_YET_CALLED:
                case LeadFollowUpActivity.FOLLOW_UP_SCHEDULE:
                    /*mBuilder.setContentTitle("Gcloud LMS")
                            .setContentText("Call " + leadData.getName() + " interested in " + leadData.getModelName())
                            .setAutoCancel(true)
                            .setTicker("Call "+leadData.getName()+ " interested in "+leadData.getModelName())
                            .addAction(R.drawable.call, "Call", callNowPending)
                            .setPriority(Notification.PRIORITY_MAX)
                            .setDefaults(Notification.DEFAULT_SOUND);*/
                    /*contentView.setImageViewResource(R.id.carPicture, R.drawable.ic_launcher);
                    contentView.setTextViewText(R.id.title, "Gcloud LMS");
                    contentView.setTextViewText(R.id.message, "Call " + leadData.getName() + " interested in "
                            + leadData.getM?akeId() + " " + leadData.getModelName());
                    contentView.setOnClickPendingIntent(R.id.call, callNowPending);
                    contentView.setOnClickPendingIntent(R.id.remindLater, remindLaterPending);*/
                    mBuilder/**/
                            .setContentTitle("Gcloud LMS")
                            .setContentText("Call " + leadData.getName() + " interested in "
                                    + leadData.getMakeId() + " " + leadData.getModelName())
                            .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle("Gcloud LMS").bigText("Call " + leadData.getName() + " interested in "
                                    + leadData.getMakeId() + " " + leadData.getModelName()))
                            .addAction(android.R.drawable.sym_action_call, "Call Now", callNowPending)
                            .addAction(0, "Remind Later", remindLaterPending)
                            .setPriority(Notification.PRIORITY_MAX)/*/
                            .setContent(contentView)/**/;
                    break;

                case LeadFollowUpActivity.WALK_IN_SCHEDULE:
                    WalkInState walkInState = (WalkInState) extras.get(WALK_IN_STATE);
                    switch (walkInState) {
                        case WALK_IN_TODAY:
                            /*contentView.setImageViewResource(R.id.carPicture, R.drawable.cadillac);
                            contentView.setTextViewText(R.id.title, "Gcloud LMS");
                            contentView.setTextViewText(R.id.message, leadData.getName() + " is going to visit your showroom today");
                            contentView.setTextViewText(R.id.call, "Call and\nConfirm");
                            contentView.setOnClickPendingIntent(R.id.call, callNowPending);
                            contentView.setOnClickPendingIntent(R.id.remindLater, remindLaterPending);
                            contentView.setViewVisibility(R.id.reScheduleWalkIn, View.VISIBLE);*/
                            Intent reScheduleWalkIn = new Intent(context, LeadFollowUpActivity.class);
                            reScheduleWalkIn.putExtra(LeadManageIntentService.LEAD_DATA, leadData);
                            reScheduleWalkIn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            reScheduleWalkIn.putExtra(ACTION, RESCHEDULE_WALK_IN);
                            reScheduleWalkIn.putExtra(FROM_NOTIFICATION, true);
                            PendingIntent reScheduleWalkInPending = PendingIntent.getActivity(context,
                                    (pendingRequestCode + 2),
                                    reScheduleWalkIn,
                                    PendingIntent.FLAG_CANCEL_CURRENT);
//                            contentView.setOnClickPendingIntent(R.id.reScheduleWalkIn, reScheduleWalkInPending);
                            mBuilder/**/
                                    .setContentTitle("Gcloud LMS")
                                    .setContentText(leadData.getName() + " is going to visit your showroom today")
                                    .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle("Gcloud LMS")
                                            .bigText(leadData.getName() + " is going to visit your showroom today"))
                                    .addAction(0, "Schedule", reScheduleWalkInPending)
                                    .addAction(R.drawable.call, "", callNowPending)
                                    .addAction(0, "Later", remindLaterPending)
                                    .setPriority(Notification.PRIORITY_MAX)/*/
                                    .setContent(contentView)/**/;
                            break;
                        case WALK_IN_CURRENT:
                            /*contentView.setImageViewResource(R.id.carPicture, R.drawable.cadillac);
                            contentView.setTextViewText(R.id.title, "Gcloud LMS");
                            contentView.setTextViewText(R.id.message, "Call " + leadData.getName() + " interested in "
                                    + leadData.getMakeId() + " " + leadData.getModelName());
                            contentView.setOnClickPendingIntent(R.id.call, callNowPending);
                            contentView.setOnClickPendingIntent(R.id.remindLater, remindLaterPending);*/
                            mBuilder/**/
                                    .setContentTitle("Gcloud LMS")
                                    .setContentText("Call " + leadData.getName() + " interested in "
                                            + leadData.getMakeId() + " " + leadData.getModelName())
                                    .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle("Gcloud LMS")
                                            .bigText("Call " + leadData.getName() + " interested in "
                                                    + leadData.getMakeId() + " " + leadData.getModelName()))
                                    .addAction(R.drawable.call, "Call Now", callNowPending)
                                    .addAction(0, "Remind Later", remindLaterPending)
                                    .setPriority(Notification.PRIORITY_MAX)/*/
                                    .setContent(contentView)/**/;
                            break;
                        case WALK_IN_YESTERDAY:
                            if (!leadData.getStatus().equals(LeadFollowUpActivity.MARK_CLOSED)) {
                                Intent yes = new Intent(this, LeadFollowUpActivity.class);
                                yes.putExtra(LeadManageIntentService.LEAD_DATA, leadData);
                                yes.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                yes.putExtra(ACTION, WALKIN_YES);
                                yes.putExtra(FROM_NOTIFICATION, true);
                                PendingIntent yesPending = PendingIntent.getActivity(context,
                                        (pendingRequestCode + 3),
                                        callNow,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                                Intent no = new Intent(context, LeadFollowUpActivity.class);
                                no.putExtra(LeadManageIntentService.LEAD_DATA, leadData);
                                no.putExtra(ACTION, WALKIN_NO);
                                no.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                no.putExtra(FROM_NOTIFICATION, true);
                                PendingIntent noPending = PendingIntent.getActivity(context,
                                        (pendingRequestCode + 4),
                                        remindLater,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                                /*contentView.setImageViewResource(R.id.carPicture, R.drawable.cadillac);
                                contentView.setTextViewText(R.id.title, "Gcloud LMS");
                                contentView.setTextViewText(R.id.message, "Did " + leadData.getName() + " interested in "
                                        + leadData.getMakeId() + " " + leadData.getModelName() + " visit showroom yesterday?");
                                contentView.setTextViewText(R.id.call, "Yes");
                                contentView.setTextViewText(R.id.remindLater, "No");
                                contentView.setOnClickPendingIntent(R.id.call, yesPending);
                                contentView.setOnClickPendingIntent(R.id.remindLater, noPending);*/
                                mBuilder/**/
                                        .setContentTitle("Gcloud LMS")
                                        .setContentText("Call " + leadData.getName() + " interested in "
                                                + leadData.getMakeId() + " " + leadData.getModelName())
                                        .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle("Gcloud LMS")
                                                .bigText("Call " + leadData.getName() + " interested in "
                                                        + leadData.getMakeId() + " " + leadData.getModelName()))
                                        .addAction(0, "Yes", yesPending)
                                        .addAction(0, "No", noPending)
                                        .setPriority(Notification.PRIORITY_MAX)/*/
                                        .setContent(contentView)/**/;
                            }
                            break;
                    }
                    break;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                        .setSmallIcon(R.drawable.ic_notification)
                        .setColor(getResources().getColor(R.color.gaadi_blue));
            } else {
                mBuilder.setSmallIcon(R.drawable.ic_launcher);
            }
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Integer.valueOf(leadData.getLeadId()), mBuilder.build());
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
