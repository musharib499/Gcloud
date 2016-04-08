package com.gcloud.gaadi.retrofit;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.FinanceDB;
import com.gcloud.gaadi.model.FinanceData;
import com.gcloud.gaadi.model.ImageUploadResponse;
import com.gcloud.gaadi.ui.Finance.FinanceCasesStatusActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.FinanceUtils;
import com.gcloud.gaadi.utils.GCLog;
import com.gcloud.gaadi.utils.LogFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.RetrofitError;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class RetrofitImageUploadService extends IntentService {

    public static final String KEY_MAXRETRY = "maxRetry";
    public static final String KEY_MAXCOUNT = "maxCount";
    public static final String KEY_TOTALCOUNT = "totalCount";
    public static final String KEY_FAILCOUNT = "failCount";
    //    public static final int IMAGE_UPLOAD_NOTIFICATION_ID = 101;
    final String TAG = "RetrofitImageUploadService";
    private int maxCount = 0, uploadedCount = 0, failCount = 0;

    private HashMap<String, Integer> applicationIdCountMap;
    private HashMap<String, ArrayList<FinanceData>> applicationIdFinanceDatas;

    //    private boolean notificationOngoing = false;
    private ArrayList<Integer> notificationIdList = new ArrayList<>();

    public RetrofitImageUploadService() {
        super("Image Upload");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        System.gc();
        applicationIdCountMap = new HashMap<>();
        applicationIdFinanceDatas = new HashMap<>();

        resetCounts();
        FinanceDB financeDB = ApplicationController.getFinanceDB();
        ArrayList<FinanceData> financeDatas = financeDB.getImagesForUpload();
//        CommonUtils.setIntSharedPreference(this, KEY_MAXCOUNT, financeDatas.size());

        for (FinanceData financeData : financeDatas) {
            int count = 1;
            if (applicationIdCountMap.containsKey(financeData.getApplicationId())) {
                count = applicationIdCountMap.get(financeData.getApplicationId());
                count++;
            }
            applicationIdCountMap.put(financeData.getApplicationId(), count);

            if (applicationIdFinanceDatas.containsKey(financeData.getApplicationId())) {
                applicationIdFinanceDatas.get(financeData.getApplicationId()).add(financeData);
            } else {
                ArrayList<FinanceData> list = new ArrayList<>();
                list.add(financeData);
                applicationIdFinanceDatas.put(financeData.getApplicationId(), list);
            }
        }

        maxCount = financeDatas.size();
        //financeDB.insertImageUploadCount(maxCount, 0);
        GCLog.e("Number of Images in local DB : " + financeDatas.size());
        JSONObject jsonObject = new JSONObject();
//        for (FinanceData data: financeDatas) {
        for (Map.Entry entry : applicationIdCountMap.entrySet()) {
            maxCount = (Integer) entry.getValue();
            uploadedCount = 0;
            failCount = 0;

            ArrayList<FinanceData> localData = applicationIdFinanceDatas.get(entry.getKey());
            int count = 0;
            while (maxCount > (uploadedCount + failCount)) {
                if (!ApplicationController.checkInternetConnectivity())
                    break;
                FinanceData data = localData.get(count);
                File file = new File(data.getImagePath());
                GCLog.e(file.getPath());
                if (file.exists()) {
                    if (!notificationIdList.contains(Integer.valueOf(data.getApplicationId()))) {
                        notificationIdList.add(Integer.valueOf(data.getApplicationId()));
                        uploadedCount = financeDB.getUploadedImagesCount();
                        maxCount = financeDB.getTotalImagesCount();
                        Log.v("TAG", "Total images count => "+maxCount+" Upload count => "+uploadedCount+" Max Count"+maxCount);
                        financeDB.insertImageUploadCount(financeDB.getTotalImagesCount(), uploadedCount);
                        createNotification(data.getApplicationId(), "Uploading "+((uploadedCount == 0)?1:uploadedCount+1)+" of " + maxCount + " images");
                    }
                    try {
                        jsonObject.put("finance_lead_id", data.getApplicationId());
                        jsonObject.put("file_tag", data.getTagId());
                        jsonObject.put("file_name", data.getRequestName());
                        jsonObject.put("parentId", data.getTagTypeId());
                        LogFile logFile = LogFile.getInstance();
                        logFile.createFileOnDevice(true);
                        logFile.writeToFile("ImageUploadRequest","Application ID : "+data.getApplicationId()+
                        " File Tag : "+data.getTagId()+
                        " File Name : "+data.getRequestName()+
                        " Parent ID : "+data.getTagTypeId());
                    /*RetrofitRequest.makeImageUploadRequest(data.getImagePath(),
                            jsonObject,
                            new BackgroundImageUploadCallback(this,
                                    data));*/
                        ImageUploadResponse response = RetrofitRequest.makeImageUploadRequestSequential(CommonUtils.compressImage(data.getImagePath(), 1200, 1600),
                                data);
                        logFile.writeToFile("ImageUploadResponse","Status :"+response.getStatus()+" :: Message : "+response.getMessage()
                                +" :: Error : "+response.getError());
                        if (response.getStatus().equalsIgnoreCase("T")) {
                            if (financeDB.updateSuccess(data.getId()) != 1) {
                                failCount++;
                                int totalCount = financeDB.getTotalImagesCount();
                                financeDB.insertImageUploadCount(totalCount, uploadedCount);
                            } else {
                                uploadedCount++;
                                int totalCount = financeDB.getTotalImagesCount();
                                financeDB.insertImageUploadCount(totalCount, uploadedCount);
                                File fileToDelete = new File(data.getImagePath());
                                deleteFile(fileToDelete);
                                updateNotification(data.getApplicationId());
                            }
                        } else {
                            failCount++;
                            financeDB.updateFailure(data.getId());
                            int totalCount = financeDB.getTotalImagesCount();
                            financeDB.insertImageUploadCount(totalCount, uploadedCount);
                            cancelNotification("Error in images uploaded, will retry later.", null, data.getApplicationId());
                        }
                    } catch (JSONException exception) {
                        exception.printStackTrace();
                    } catch (RetrofitError error) {
                        error.printStackTrace();
                        failCount++;
                        financeDB.updateFailure(data.getId());
                        int totalCount = financeDB.getTotalImagesCount();
                        financeDB.insertImageUploadCount(totalCount, uploadedCount);
                       // cancelNotification("Error in images uploaded, will retry later.", null, data.getApplicationId());
                    }
                } else {
                    maxCount--; // decreaseMaxCount(data.getId());
                }
                ++count;
            }
            if ((maxCount == (uploadedCount + failCount)) && (failCount == 0)) {
                cancelNotification("All images uploaded", null, (String) entry.getKey());
                financeDB.deleteImageUploadCount();
            }
            if (!ApplicationController.checkInternetConnectivity()) {
                financeDB.insertImageUploadCount(financeDB.getTotalImagesCount(), uploadedCount);
                cancelNotification("Will retry when network is connected", null, (String) entry.getKey());
            }
        }
    }

    private void resetCounts() {
        CommonUtils.setIntSharedPreference(this, KEY_MAXCOUNT, 0);
        CommonUtils.setIntSharedPreference(this, KEY_TOTALCOUNT, 0);
        CommonUtils.setIntSharedPreference(this, KEY_FAILCOUNT, 0);
    }

    private void decreaseMaxCount(int id) {
        int maxCount = CommonUtils.getIntSharedPreference(this, KEY_MAXCOUNT, 0);
        maxCount--;
        CommonUtils.setIntSharedPreference(this, KEY_MAXCOUNT, maxCount);
        ApplicationController.getFinanceDB().updateSuccess(id);
    }

    private void createNotification(String applicationId) {
        CommonUtils.createImageUploadNotification(this, "Loan Application ID: ", applicationId, "Image Upload in Progress", true, null, null);
    }

    private void createNotification(String applicationId, String message) {
        CommonUtils.createImageUploadNotification(this, "Loan Application ID: ", applicationId, message, true, null, null);
    }

    private void deleteFile(File file) {
        try {

            getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Images.Media.DATA
                            + "='"
                            + file.getPath()
                            + "'", null);
        } catch (Exception e) {
            if (e != null) {
                Crashlytics.logException(e.getCause());
            }
            //e.printStackTrace();

        }
    }

    private void updateNotification(String applicationId) {
        CommonUtils.createImageUploadNotification(this, "Loan Application ID: ",
                applicationId,
                "Uploading " +
                        (ApplicationController.getFinanceDB().getUploadedImagesCount() + 1)+ " of " + ApplicationController.getFinanceDB().getTotalImagesCount() + " Images",
                true, null, null);
    }

    private void cancelNotification(String message, String subText, String applicationId) {
        Intent resultIntent = new Intent(ApplicationController.getInstance(), FinanceCasesStatusActivity.class);
        resultIntent.putExtra(Constants.LOAN_CASE_STATUS, FinanceUtils.LoanType.PENDING_CASE);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        ApplicationController.getInstance(),
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        CommonUtils.createImageUploadNotification(ApplicationController.getInstance(), "Loan Application ID: ", applicationId, message,
                false, resultPendingIntent, subText);
    }
}
