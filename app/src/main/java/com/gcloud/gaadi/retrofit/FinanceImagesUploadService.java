package com.gcloud.gaadi.retrofit;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
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
 * Created by priyarawat on 20/1/16.
 */
public class FinanceImagesUploadService extends IntentService {
    public static final String KEY_MAXRETRY = "maxRetry";
    final String TAG = "FinanceImageUploadService";
    // private HashMap<String, Integer> applicationIdCountMap;
    private HashMap<String, ArrayList<FinanceData>> applicationIdFinanceDatas;
    private ArrayList<Integer> notificationIdList = new ArrayList<>();
    private int maxCount = 0, uploadedCount = 0, totalImagesForAppId = 0;

    public FinanceImagesUploadService()
    {
        super("Images Upload");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        //applicationIdCountMap = new HashMap<>();
        applicationIdFinanceDatas = new HashMap<>();
        FinanceDB financeDB = ApplicationController.getFinanceDB();
        ArrayList<FinanceData> financeDatas = financeDB.getImagesForUpload();
        for (FinanceData financeImageData:financeDatas) {

            if (applicationIdFinanceDatas.containsKey(financeImageData.getApplicationId())) {
                applicationIdFinanceDatas.get(financeImageData.getApplicationId()).add(financeImageData);
            } else {
                ArrayList<FinanceData> list = new ArrayList<>();
                list.add(financeImageData);
                applicationIdFinanceDatas.put(financeImageData.getApplicationId(), list);
            }
        }
        maxCount = financeDatas.size();
        GCLog.e("Number of Images in local DB : " + financeDatas.size());
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry entry:applicationIdFinanceDatas.entrySet() ){

            ArrayList<FinanceData> financeImagesList = (ArrayList<FinanceData>) entry.getValue();
            String financeId = financeImagesList.get(0).getApplicationId();

            maxCount = financeImagesList.get(0).getTotalImages();
            totalImagesForAppId = financeImagesList.size();
            int failedCount = 0;
            int fileNotExist = 0;
            int j;
            for (j = 0; j < totalImagesForAppId; j++) {

                if (!ApplicationController.checkInternetConnectivity()) {
                    cancelNotification("Will retry when network is connected", null, financeId);
                    break;
                }
                FinanceData financeImageData = financeImagesList.get(j);
                uploadedCount = financeImageData.getUploadingSequence();
                File file = new File(financeImageData.getImagePath());
                if(file.exists())
                {
                    if (!notificationIdList.contains(Integer.valueOf(financeId))) {
                        notificationIdList.add(Integer.valueOf(financeId));

                        Log.v("TAG", "Total images count => " + maxCount + " Upload count => " + uploadedCount + " Max Count" + maxCount);

                        createNotification(financeId, "Uploading "+((uploadedCount == 0)?1:uploadedCount)+" of " + maxCount + " images");
                    }
                    else
                    {
                        updateNotification(financeId);
                    }
                    try {
                        jsonObject.put("finance_lead_id", financeId);
                        jsonObject.put("file_tag", financeImageData.getTagId());
                        jsonObject.put("file_name", financeImageData.getRequestName());
                        jsonObject.put("parentId", financeImageData.getTagTypeId());
                        LogFile logFile = LogFile.getInstance();
                        logFile.createFileOnDevice(true);
                        logFile.writeToFile("ImageUploadRequest","Application ID : "+financeImageData.getApplicationId()+
                                " File Tag : "+financeImageData.getTagId()+
                                " File Name : "+financeImageData.getRequestName()+
                                " Parent ID : "+financeImageData.getTagTypeId());

                        ImageUploadResponse response = RetrofitRequest.makeImageUploadRequestSequential(CommonUtils.compressImage(financeImageData.getImagePath(), 1200, 1600),
                                financeImageData);
                        logFile.writeToFile("ImageUploadResponse","Status :"+response.getStatus()+" :: Message : "+response.getMessage()
                                +" :: Error : "+response.getError());
                        if (response.getStatus().equalsIgnoreCase("T")) {
                          financeDB.updateSuccess(financeImageData.getId());
                            File fileToDelete = new File(financeImageData.getImagePath());
                                deleteFile(fileToDelete);
                               // updateNotification(financeImageData.getApplicationId());

                        } else {
                            failedCount++;
                            financeDB.updateFailure(financeImageData.getId());

                            cancelNotification("Error in images uploaded, will retry later.", null, financeImageData.getApplicationId());
                        }
                    } catch (JSONException exception) {
                        //exception.printStackTrace();
                    } catch (RetrofitError error) {
                        //error.printStackTrace();
                        failedCount++;
                        financeDB.updateFailure(financeImageData.getId());

                    }
                }
                else
                {
                    fileNotExist++;
                    financeDB.updateSuccess(financeImageData.getId());
                }
            }

            if(failedCount == 0 && fileNotExist == 0)
            {
                cancelNotification("All images uploaded", null, financeId);
                break;
            }
            else if(failedCount != 0 && fileNotExist != 0)
            {
                cancelNotification(getString(R.string.error_in_images_retry_later), null, financeId);
            }
            else if(fileNotExist != 0)
            {
                cancelNotification(getString(R.string.file_not_exist), null, financeId);
            }
            else {
                //cancelNotification(String.format(getString(R.string.images_failure),failedCount),null,financeId);
                cancelNotification(getString(R.string.error_in_images_retry_later), null, financeId);
            }

           /* if(j!=0 && j == maxCount)
            {
                cancelNotification("All images uploaded", null, financeId);

            }*/


        }
    }
    private void createNotification(String applicationId, String message) {
        CommonUtils.createImageUploadNotification(this, "Loan Application ID: ", applicationId, message, true, null, null);
    }
    private void updateNotification(String applicationId) {
        CommonUtils.createImageUploadNotification(this, "Loan Application ID: ",
                applicationId,
                "Uploading " +
                        uploadedCount+ " of " + maxCount + " Images",
                true, null, null);
    }
    private void deleteFile(File file) {
        try {

            getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Images.Media.DATA
                            + "='"
                            + file.getPath()
                            + "'", null);
        } catch (Exception e) {
            e.printStackTrace();

        }
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
        CommonUtils.createImageUploadNotification(ApplicationController.getInstance(), "Loan Application ID: ",
                applicationId, message,
                false, resultPendingIntent, subText);
    }

}
