package com.gcloud.gaadi.ui;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.ManageLeadDB;
import com.gcloud.gaadi.lms.FollowUpSuccessYes;
import com.gcloud.gaadi.lms.FollowUpUnsuccess;
import com.gcloud.gaadi.lms.LMSInterface;
import com.gcloud.gaadi.lms.LMSNotificationService;
import com.gcloud.gaadi.lms.LMSRemindLater;
import com.gcloud.gaadi.lms.LeadManageIntentService;
import com.gcloud.gaadi.lms.NotYetCalledSuccess;
import com.gcloud.gaadi.lms.NotYetCalledSuccessYes;
import com.gcloud.gaadi.lms.NotYetCalledUnsuccess;
import com.gcloud.gaadi.lms.NotYetCalledUnsuccessCallback;
import com.gcloud.gaadi.lms.NotYetCalledUnsuccessCallbackSchedule;
import com.gcloud.gaadi.lms.WalkInConfirmWalkIn;
import com.gcloud.gaadi.lms.WalkInNo;
import com.gcloud.gaadi.lms.WalkInSuccess;
import com.gcloud.gaadi.lms.WalkInSuccessNo;
import com.gcloud.gaadi.lms.WalkInUnsuccess;
import com.gcloud.gaadi.lms.WalkInYes;
import com.gcloud.gaadi.model.LeadData;
import com.gcloud.gaadi.syncadapter.GenericAccountService;
import com.gcloud.gaadi.syncadapter.SyncUtils;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

import org.joda.time.DateTime;

import java.util.ArrayList;


/**
 * Created by Gaurav on 10-06-2015.
 */
public class LeadFollowUpActivity extends FragmentActivity implements LMSInterface {

    public static final String NOT_YET_CALLED = "Not Yet Called";
    public static final String FOLLOW_UP_SCHEDULE = "Follow Up Schedule";
    public static final String WALK_IN_SCHEDULE = "Walk In Schedule";
    public static final String MARK_CLOSED = "markClosed";
    private static final String TAG = "LeadFollowUpActivity";
    private final Uri LMS_URI = Uri.parse(new StringBuilder().append("content://")
            .append(Constants.LMS_CONTENT_AUTHORITY).append("/").append(ManageLeadDB.TABLE_NAME).toString());

    private Integer key;
    private Bundle intentData;
    private ArrayList<Integer> fragmentStack;
    private String scheduleStatus;
    private LeadData leadData;
    private String selectedNumberToCall = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lms_lead_follow_up_activity);

        fragmentStack = new ArrayList<>();

        if (getIntent().getExtras() != null) {
            intentData = getIntent().getExtras();

            if (!intentData.containsKey(LMSNotificationService.FROM_NOTIFICATION)) {
                GCLog.e("onHandleIntent not from notification");
                setKey(intentData);
            } else {
                leadData = (LeadData) intentData.get(LeadManageIntentService.LEAD_DATA);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.cancel(Integer.valueOf(leadData.getLeadId()));
                GCLog.e("onHandleIntent from notification action: " + intentData.getString(LMSNotificationService.ACTION));
                switch (intentData.getString(LMSNotificationService.ACTION)) {
                    case LMSNotificationService.PERFORM_CALL:
                        String number = leadData.getNumber();
                        if ((number != null) && !number.isEmpty() && !"null".equalsIgnoreCase(number)) {
                            CommonUtils.activateLeadCallStateListener(this,
                                    LeadFollowUpActivity.class,
                                    "+91" + number,
                                    Constants.VALUE_VIEWLEAD,
                                    Constants.NOT_YET_CALLED_FRAG_NO,
                                    leadData);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                    && !CommonUtils.checkForPermission(this,
                                    new String[]{Manifest.permission.READ_CALL_LOG,
                                    Manifest.permission.CALL_PHONE},
                                    Constants.REQUEST_PERMISSION_CALL, "Phone")) {
                                selectedNumberToCall = number;
                                return;
                            }

                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:+91" + number));

                            startActivity(intent);
                        }
                        break;

                    case LMSNotificationService.REMIND_LATER:
                        key = 90;
                        break;

                    case LMSNotificationService.RESCHEDULE_WALK_IN:
                        key = 6;
                        scheduleStatus = getStatus(leadData.getNumber());
                        break;

                    case LMSNotificationService.WALKIN_YES:
                        key = 100;
                        break;

                    case LMSNotificationService.WALKIN_NO:
                        key = 110;
                        break;
                }
            }
            /*getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, eadManageDialog.getInstance(intentData, key))
                    .commit();*/
            if (intentData.containsKey(LMSNotificationService.FROM_NOTIFICATION)
                    && intentData.getString(LMSNotificationService.ACTION).equals(LMSNotificationService.PERFORM_CALL)) {
                finish();
            } else {
                fragmentStack.add(key);
                if (getFragment(key) != null) {
                    getFragment(key).show(getSupportFragmentManager(), "LMS Dialog");
                } else {
                    GCLog.e("onhandleintent key: " + key + " gives null");
                    finish();
                }
            }
        }
    }

    private DialogFragment getFragment(int key) {
        switch (key) {
            case 0:
                return NotYetCalledUnsuccess.getInstance(intentData, this, this);
            case 1:
                return NotYetCalledUnsuccessCallback.getInstance(intentData, this, this, scheduleStatus);
            case 6:
                return NotYetCalledUnsuccessCallbackSchedule.getInstance(intentData, this, this, scheduleStatus);
            case 11:
                return NotYetCalledSuccess.getInstance(intentData, this, this, NOT_YET_CALLED);
            case 12:
                return NotYetCalledSuccessYes.getInstance(intentData, this, this);
            case 20:
                return FollowUpUnsuccess.getInstance(intentData, this, this);
            case 31:
                return NotYetCalledSuccess.getInstance(intentData, this, this, FOLLOW_UP_SCHEDULE);
            case 32:
                return FollowUpSuccessYes.getInstance(intentData, this, this);
            case 40:
                return WalkInUnsuccess.getInstance(intentData, this, this);
            case 43:
                return WalkInConfirmWalkIn.getInstance(intentData, this, this);
            case 51:
                return WalkInSuccess.getInstance(intentData, this, this);
            case 53:
                return WalkInSuccessNo.getInstance(intentData, this, this);
            case 90:
                return LMSRemindLater.getInstance(intentData, this, this,
                        getStatus(leadData.getNumber()));
            case 100:
                return WalkInYes.getInstance(intentData, this, this);
            case 110:
                return WalkInNo.getInstance(intentData, this, this);
            case Constants.MARK_CLOSED:
                return null;
        }
        return null;
    }

    private String getStatus(String number) {
        String result = "";
        Cursor cursor = getContentResolver().query(LMS_URI,
                new String[]{ManageLeadDB.LMS_STATUS},
                ManageLeadDB.NUMBER + " = ?",
                new String[]{number},
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(ManageLeadDB.LMS_STATUS));
            }
            cursor.close();
        }
        return result;
    }

    private void setKey(Bundle data) {
        boolean callSuccess = data.getBoolean(Constants.CALL_SUCCESS);
        GCLog.e("success: " + callSuccess);
        leadData = (LeadData) data.get(Constants.MODEL_DATA);
        String status = getStatus(leadData.getNumber());
        GCLog.e("status from db: " + status);
        if (status.isEmpty()) {
            switch (leadData.getApiStatus()) {
                case "Cold":
                case "Warm":
                case "Hot":
                    status = FOLLOW_UP_SCHEDULE;
                    break;
                case "Closed":
                case "Booked":
                case "Converted":
                case "Walked-in":
                    status = MARK_CLOSED;
                    break;
                case "WalkInScheduled":
                    status = WALK_IN_SCHEDULE;
                    break;
                default:
                    status = NOT_YET_CALLED;
                    break;
            }
        }
        switch (status) {
            case NOT_YET_CALLED:
                key = callSuccess ? 11 : 0;
                break;
            case FOLLOW_UP_SCHEDULE:
                key = callSuccess ? 31 : 20;
                break;
            case WALK_IN_SCHEDULE:
                key = callSuccess ? 51 : 40;
                break;
            case MARK_CLOSED:
                key = Constants.MARK_CLOSED;
                break;
        }
    }

    @Override
    public void onFragmentDismiss(int key) {
        switch (key) {
            case Constants.MARK_CLOSED:
                if (getStatus(leadData.getNumber()).isEmpty()) {
                    leadData.setRequestCode(Integer.valueOf(leadData.getLeadId()) * 10);
                    leadData.setStatus(MARK_CLOSED);
                    insert(leadData, 0);
                } else {
                    update(LeadFollowUpActivity.MARK_CLOSED, leadData.getNumber());
                }
                ContentResolver.requestSync(GenericAccountService.GetAccount(SyncUtils.ACCOUNT_TYPE),
                        Constants.LMS_CONTENT_AUTHORITY,
                        new Bundle());
                CommonUtils.showToast(this, "Lead " + leadData.getName() + " is marked Close", Toast.LENGTH_SHORT);
                finish();
                break;
            case Constants.BACK_PRESSED:
                if (fragmentStack.size() < 2) {
                    finish();
                } else {
                    fragmentStack.remove(fragmentStack.size() - 1);
                    getFragment(fragmentStack.get(fragmentStack.size() - 1)).show(getSupportFragmentManager(), "LMS Dialog");
                }
                break;
            case Constants.OUTSIDE_TOUCHED:
                finish();
                break;
            default:
                switch (key) {
                    case 6:
                        scheduleStatus = NOT_YET_CALLED;
                        break;
                    case 13:    // NotYetCalled->Success->Yes->Schedule FollowUp
                    case 23:    //FollowUp->Unsuccess->Schedule FollowUp
                    case 34:    //FollowUp->Success->Yes->Schedule FollowUp
                        scheduleStatus = FOLLOW_UP_SCHEDULE;
                        key = 6;
                        break;
                    case 14:    //NotYetCalled->Success->Yes->Schedule WalkIn
                    case 22:    //FollowUp->Unsuccess->Schedule WalkIn
                    case 33:    //FollowUp->Success->Yes->Schedule WalkIn
                    case 42:    //WalkIn->UnSuccess->Schedule WalkIn
                    case 44:    //WalkIn->UnSuccess->Callback->Schedule
                    case 54:    //WalkIn->Success->No->Schedule WalkIn
                    case 102:   //WalkIn Yesterday Notification ->Yes->Schedule WalkIn
                    case 111:   //WalkIn Yesterday Notification ->No->Schedule WalkIn
                        scheduleStatus = WALK_IN_SCHEDULE;
                        key = 6;
                        break;
                    case 1:
                        scheduleStatus = NOT_YET_CALLED;
                        break;
                    case 21:    //FollowUp->UnSuccess->CallBack
                        scheduleStatus = FOLLOW_UP_SCHEDULE;
                        key = 1;
                        break;
                    case 41:    // WalkIn->UnSuccess->CallBack
                        scheduleStatus = WALK_IN_SCHEDULE;
                        key = 1;
                        break;

                }
                fragmentStack.add(key);
                getFragment(key).show(getSupportFragmentManager(), "LMS Dialog");
                break;
        }
    }

    private void update(String status, String number) {
        ContentValues values = new ContentValues();
        values.put(ManageLeadDB.LMS_STATUS, status);
        values.put(ManageLeadDB.SERVER_STATUS, "Closed");
        values.put(ManageLeadDB.CURRENT_TIMESTAMP, String.valueOf(DateTime.now().getMillis()));
        values.put(ManageLeadDB.SYNCED, 0);

        getContentResolver().update(LMS_URI, values, ManageLeadDB.NUMBER + " = ?", new String[]{number});
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION_CALL) {
            Intent intent;
            if (grantResults.length > 1
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //Uri uri = intent.getData();
                //String uriString = uri.toString();
                //String phoneNumber = uriString.substring(7);
                    CommonUtils.activateLeadCallStateListener(this,
                            LeadFollowUpActivity.class,
                            "+91" + /**/selectedNumberToCall /*/"9560619309"/**/,
                            Constants.VALUE_VIEWLEAD,
                            Constants.NOT_YET_CALLED_FRAG_NO,
                            leadData);
            }
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //intent.removeExtra("lead_data");
                intent = new Intent(Intent.ACTION_CALL);
            } else {
                intent = new Intent(Intent.ACTION_DIAL);
            }
            intent.setData(Uri.parse("tel:+91" + selectedNumberToCall));
            startActivity(intent);
        }
    }
}
