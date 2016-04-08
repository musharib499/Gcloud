package com.gcloud.gaadi.retrofit;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.BuildConfig;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.lms.LMSResetAlarmService;
import com.gcloud.gaadi.service.PendingImagesService;
import com.gcloud.gaadi.syncadapter.GenericAccountService;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

public class ImageUploadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        throw new UnsupportedOperationException("Not yet implemented");
        if (intent.getAction().toString().equals(Intent.ACTION_BOOT_COMPLETED)) {
            GCLog.w("Boot complete", "received");
//            context.startService(new Intent(context, LMSResetAlarmService.class));
            context.startService(new Intent(context, LMSResetAlarmService.class));
            CommonUtils.uploadPendingImages(context);
        } else {
            if (ApplicationController.checkInternetConnectivity()) {

                ContentResolver.requestSync(GenericAccountService.GetAccount(BuildConfig.ACCOUNT_TYPE),
                        Constants.LEADS_CONTENT_AUTHORITY, new Bundle());

                CommonUtils.setIntSharedPreference(ApplicationController.getInstance(), RetrofitImageUploadService.KEY_MAXRETRY, 5);
                Intent startImageUpload = new Intent(ApplicationController.getInstance(), PendingImagesService.class);
                ApplicationController.getInstance().startService(startImageUpload);
            }
        }
    }
}
