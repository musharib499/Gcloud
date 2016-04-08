package com.gcloud.gaadi;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.utils.GCLog;

/**
 * Created by Gaurav on 24-04-2015.
 */
public class CallStateListener extends PhoneStateListener {

    final private String TAG = "CallStateListener";

    private Intent intent;
    private Context context;
    //private long startTime = 0;
    private boolean firstIdle = true, success = false;
    private String phoneNumber;
    private TelephonyManager telephonyManager;

    public CallStateListener(Context context, Intent intent, String phoneNumber) {
        this.context = context;
        this.intent = intent;
        this.phoneNumber = phoneNumber;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(this, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        //GCLog.e("oncallstatechanged incoming=" + incomingNumber + " phone=" + phoneNumber);
        switch (state) {
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //GCLog.e("state OFFHOOK");
                firstIdle = false;
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //GCLog.e("state IDLE");
                if (!firstIdle) {
                    //GCLog.e("entered hangup "+incomingNumber.equals(phoneNumber));
                    firstIdle = true;
                    telephonyManager.listen(this, PhoneStateListener.LISTEN_NONE);
                    //if (((System.currentTimeMillis() - startTime) > 10000) && incomingNumber.equals(phoneNumber)) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (hasValidCallEnded(phoneNumber)) {
                                GCLog.e("time completed and numbers matched");
                                intent.putExtra(Constants.CALL_SUCCESS, success);
//                                context.startActivity(intent);
                                /*Intent broadcastIntent = new Intent();
                                broadcastIntent.setAction("com.gaadi.LMS");
                                broadcastIntent.putExtra("intentData", intent.getExtras());
                                context.sendBroadcast(broadcastIntent);*/
                                ApplicationController.getEventBus().post(intent);
                            }
                        }
                    }, 750);
                    /*if (hasValidCallEnded(phoneNumber)) {
                        //GCLog.e(TAG, "time completed and numbers matched");
                        context.startActivity(intent);
                    } else {
                        //GCLog.e(TAG, "validity failed");
                    }*/
                } /*else {
                    firstIdle = false;
                }*/
                break;
        }
    }

    private boolean hasValidCallEnded(String number) {
        String[] mProjection = new String[]{CallLog.Calls.NUMBER, CallLog.Calls.DURATION};
        Uri contacts = CallLog.Calls.CONTENT_URI;
        Cursor managedCursor = context.getContentResolver().query(contacts,
                mProjection,
                null, null,
                CallLog.Calls.DEFAULT_SORT_ORDER);
        int lastCallNumber = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        if (managedCursor.moveToFirst()) {
            if (number.equals(managedCursor.getString(lastCallNumber))) {
                //GCLog.e("hasValidCallEnded number matches "+managedCursor.getInt(duration));
//                if (Integer.parseInt(managedCursor.getString(duration)) > 0) {
                //GCLog.e(TAG, "hasValidCallEnded duration > 0");
                if (managedCursor.getInt(duration) > 11) {
                    success = true;
                    //GCLog.w(TAG, "success: "+success);
                }
                return true;
//                } else {
                //GCLog.e(TAG, "less call duration "+du[ration);
//                }
            } else {
                //GCLog.e("numbers did not match "+number+" "+managedCursor.getString(lastCallNumber)+" "+duration);
            }
            managedCursor.close();
        } else {
            //GCLog.e("movetolast failed");
        }
        return false;
    }

    /*public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }*/
}
