package com.gcloud.gaadi.insurance;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.InsuranceDB;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.utils.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.RetrofitError;

/**
 * Created by gaurav on 2/3/16.
 */
public class InsuranceBackgroundImageUploadService extends IntentService {

    private final Uri INSURANCE_IMAGE_URI = Uri.parse("content://"
            + Constants.INSURANCE_CONTENT_AUTHORITY + "/" + InsuranceDB.TABLE_IMAGES);

    public InsuranceBackgroundImageUploadService() {
        super("InsuranceBackgroundImageUploadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Cursor cursor = getContentResolver().query(INSURANCE_IMAGE_URI, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                HashMap<Integer, ArrayList<UploadDataModel>> uploadMap = new HashMap<>();
                while (!cursor.isAfterLast()) {
                    int carId = cursor.getInt(cursor.getColumnIndex(InsuranceDB.COLUMN_CAR_ID)),
                            processId = cursor.getInt(cursor.getColumnIndex(InsuranceDB.COLUMN_PROCESS_ID)),
                            policyId = cursor.getInt(cursor.getColumnIndex(InsuranceDB.COLUMN_POLICY_ID));
                    String docName = cursor.getString(cursor.getColumnIndex(InsuranceDB.COLUMN_DOC_NAME)),
                            imagePath = cursor.getString(cursor.getColumnIndex(InsuranceDB.COLUMN_IMAGE_PATH));

                    if (!uploadMap.containsKey(processId)) {
                        uploadMap.put(processId, new ArrayList<UploadDataModel>());
                    }

                    uploadMap.get(processId).add(new UploadDataModel(docName, imagePath, carId, processId, policyId));

                    cursor.moveToNext();
                }

                JSONObject params = new JSONObject();
                for (Map.Entry entry : uploadMap.entrySet()) {
                    ArrayList<UploadDataModel> models = (ArrayList<UploadDataModel>) entry.getValue();
                    if (models == null || models.size() < 1) {
                        continue;
                    }
                    CommonUtils.setIntSharedPreference(this, Constants.INSURANCE_PROCESS_ID_IN_PROGRESS, models.get(0).processId);
                    int max = models.size(), success = 0, failed = 0;
                    for (UploadDataModel model : models) {
                        try {
                            if (!new File(model.imagePath).exists())
                                throw new FileNotFoundException();
                            params.put(Constants.METHOD_LABEL, "uploadInsuranceDocuments");
                            params.put("process_id", model.processId);
                            params.put("car_id", model.carId);
                            params.put("document_name", model.docName);
                            GeneralResponse response = RetrofitRequest.uploadInsuranceDocument(model.imagePath, params);
                            if ("T".equalsIgnoreCase(response.getStatus())) {
                                ++success;
                                getContentResolver().delete(INSURANCE_IMAGE_URI,
                                        InsuranceDB.COLUMN_PROCESS_ID + " = ? AND " + InsuranceDB.COLUMN_DOC_NAME + " = ?",
                                        new String[]{String.valueOf(model.processId), model.docName});
                            } else {
                                ++failed;
                            }
                        } catch (RetrofitError | JSONException ex) {
                            ++failed;
                        } catch (FileNotFoundException ex) {
                            --max;
                            getContentResolver().delete(INSURANCE_IMAGE_URI,
                                    InsuranceDB.COLUMN_PROCESS_ID + " = ? AND " + InsuranceDB.COLUMN_DOC_NAME + " = ?",
                                    new String[]{String.valueOf(model.processId), model.docName});
                        }
                        StringBuilder message = new StringBuilder();
                        if ((success + failed) == max) {
                            if (failed == 0) {
                                message.append("All images uploaded");
                            } else {
                                message.append(failed + " failed to upload");
                            }
                        } else {
                            message.append("Uploading " + (success + 1) + " of " + max);
                            if (failed > 0) {
                                message.append(", " + failed + " failed");
                            }
                        }
                        CommonUtils.createImageUploadNotification(this,
                                "Insurance ID: ", String.valueOf((model.policyId != 0) ? model.policyId : model.processId),
                                message.toString(), false, null, "");
                    }
                    CommonUtils.setIntSharedPreference(this, Constants.INSURANCE_PROCESS_ID_IN_PROGRESS, 0);
                }
            }
            cursor.close();
        }
    }

    private class UploadDataModel {
        public String docName, imagePath;
        public int carId, processId, policyId;

        public UploadDataModel(String docName, String imagePath, int carId, int processId, int policyId) {
            this.docName = docName;
            this.imagePath = imagePath;
            this.carId = carId;
            this.processId = processId;
            this.policyId = policyId;
        }
    }
}
