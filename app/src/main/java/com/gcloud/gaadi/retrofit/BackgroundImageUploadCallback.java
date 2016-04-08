package com.gcloud.gaadi.retrofit;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;

import com.crashlytics.android.Crashlytics;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.FinanceDB;
import com.gcloud.gaadi.model.FinanceData;
import com.gcloud.gaadi.model.ImageUploadResponse;
import com.gcloud.gaadi.ui.Finance.FinanceCasesStatusActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

import java.io.File;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Gaurav on 02-06-2015.
 */
public class BackgroundImageUploadCallback implements Callback<ImageUploadResponse> {

    final String TAG = "BackgroundImageUploadCallback";

    private Context context;
    private FinanceData financeData;
    private FinanceDB financeDB;

    public BackgroundImageUploadCallback(Context context,
                                         FinanceData financeData) {
        this.context = context;
        this.financeData = financeData;
        financeDB = ApplicationController.getFinanceDB();
        GCLog.e("retro called");
    }

    @Override
    public void success(ImageUploadResponse imageUploadResponse, Response response) {
        updateCount(RetrofitImageUploadService.KEY_TOTALCOUNT);
        GCLog.e(imageUploadResponse.toString());
        if (imageUploadResponse.getStatus().equals("T")) {
            if (financeDB.updateSuccess(financeData.getId()) != 1)
                updateCount(RetrofitImageUploadService.KEY_FAILCOUNT);
            else {
                File file = new File(financeData.getImagePath());
                deleteFile(file);
                updateNotification();
            }
        } else {
            updateCount(RetrofitImageUploadService.KEY_FAILCOUNT);
            ApplicationController.getFinanceDB().updateFailure(financeData.getId());
            GCLog.e("Error: " + financeData.getImagePath() + " " + imageUploadResponse.getError());
        }
//        updateNotification();
        checkForRetry();
    }

    private void deleteFile(File file) {
        try {

            context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Images.Media.DATA
                            + "='"
                            + file.getPath()
                            + "'", null);
        } catch (Exception e) {
            if (e != null) {
                Crashlytics.logException(e.getCause());
            }

        }
//        file.delete();
//        if (file.exists()) {
//            try {
//                file.getCanonicalFile().delete();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if(file.exists()){
//                context.getApplicationContext().deleteFile(file.getName());
//            }
//        }
    }

    @Override
    public void failure(RetrofitError error) {
        updateCount(RetrofitImageUploadService.KEY_TOTALCOUNT);
        updateCount(RetrofitImageUploadService.KEY_FAILCOUNT);
        ApplicationController.getFinanceDB().updateFailure(financeData.getId());
        GCLog.e("Error: " + financeData.getImagePath() + " " + error.toString());
//        updateNotification();
        checkForRetry();
    }

    private void checkForRetry() {
        String message = "";
        int maxCount = CommonUtils.getIntSharedPreference(context, RetrofitImageUploadService.KEY_MAXCOUNT, 0),
                totalCount = CommonUtils.getIntSharedPreference(context, RetrofitImageUploadService.KEY_TOTALCOUNT, 0),
                failCount = CommonUtils.getIntSharedPreference(context, RetrofitImageUploadService.KEY_FAILCOUNT, 0);
        if (totalCount == maxCount) {
//            GCLog.e(TAG, "All images Uploaded");
            if (failCount > 0) {
//                GCLog.e(TAG, "failCount: " + failCount);
                int maxRetry = CommonUtils.getIntSharedPreference(context, RetrofitImageUploadService.KEY_MAXRETRY, 0);
                if (maxRetry >= 0 && ApplicationController.checkInternetConnectivity()) {
                    maxRetry--;
                    CommonUtils.setIntSharedPreference(context, RetrofitImageUploadService.KEY_MAXRETRY, maxRetry);
                    ApplicationController.getInstance().startService(
                            new Intent(ApplicationController.getInstance(), RetrofitImageUploadService.class));
                } else {
                    message = "Images failed to upload.";
                    cancelNotification(message, "Check Internet Connectivity.");
                }
            } else if (failCount == 0) {
                if (ApplicationController.checkInternetConnectivity()) {
                    financeDB.clearImageDetails();
                    message = "All images uploaded";
                    cancelNotification(message, null);
                }
            }
        }
    }

    private void updateCount(String key) {
        int totalCount = CommonUtils.getIntSharedPreference(context, key, 0);
        totalCount++;
        CommonUtils.setIntSharedPreference(context, key, totalCount);
    }

    /*private void updateNotification() {
        String message = "";
        ArrayList<Integer> list = ApplicationController.getFinanceDB().getSuccessFailCount(financeData.getApplicationId());
        if (list.get(0) == 0) {
            if (list.get(1) == 0) {
                message = "All images uploaded";
                cancelNotification(message);
            } *//*else {
                message = ""+list.get(1)+" failed to upload.";
                cancelNotification(message);
            }*//*
        }
    }*/

    private void cancelNotification(String message, String subText) {
        Intent resultIntent = new Intent(context, FinanceCasesStatusActivity.class);
        resultIntent.putExtra(Constants.LOAN_CASE_STATUS, "0");
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        /*NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("Loan Application Id: "+financeData.getApplicationId())
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher)
                .setProgress(0, 0, false)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Integer.valueOf(financeData.getApplicationId()), mBuilder.build());*/
        CommonUtils.createImageUploadNotification(context, "Loan Application ID: ", financeData.getApplicationId(), message,
                false, resultPendingIntent, subText);
    }

    private void updateNotification() {
        int maxCount = CommonUtils.getIntSharedPreference(context, RetrofitImageUploadService.KEY_MAXCOUNT, 0),
                totalCount = CommonUtils.getIntSharedPreference(context, RetrofitImageUploadService.KEY_TOTALCOUNT, 0),
                failCount = CommonUtils.getIntSharedPreference(context, RetrofitImageUploadService.KEY_FAILCOUNT, 0);
        CommonUtils.createImageUploadNotification(context, "Loan Application ID: ",
                financeData.getApplicationId(),
                "Uploaded " + (totalCount - failCount) + " of " + maxCount + " Images",
                true, null, null);
    }
}
