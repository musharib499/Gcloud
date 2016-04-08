package com.gcloud.gaadi;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.gcloud.gaadi.service.GaadiGCMIntentService;
import com.gcloud.gaadi.utils.GCLog;

/**
 * Created by ankitgarg on 01/04/15.
 */
public class GaadiGCMBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        GCLog.e("received notification " + intent.getExtras().toString());
        ComponentName componentName = new ComponentName(context.getPackageName(), GaadiGCMIntentService.class.getName());
        startWakefulService(context, intent.setComponent(componentName));
        setResultCode(Activity.RESULT_OK);
    }
}
