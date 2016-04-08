package com.gcloud.gaadi.ui.Finance;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.Fragments.ReuploadDocsFragment;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.FinanceReuploadDocsAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.DocumentInfo;
import com.gcloud.gaadi.model.FinanceData;
import com.gcloud.gaadi.retrofit.FinanceImagesUploadService;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lakshaygirdhar on 16/10/15.
 */
public class FinanceReuploadActivity extends AppCompatActivity implements View.OnClickListener {

    public static String financeId;
    public static String customarId;
    public static String carId;
    ReuploadDocsFragment fragment;
    ArrayList<FinanceData> financeImagesList = new ArrayList<>();
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.finance_reupload_activity);
        findViewById(R.id.bUpload).setOnClickListener(this);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("Reupload Documents");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras().getString("nID") != null) {
            GCLog.e(Constants.TAG, "Log Notification");
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(Integer.parseInt(getIntent().getExtras().getString("nID")));
            CommonUtils.logNotification(this, "0", getClass().getSimpleName(), getIntent().getExtras().getString("notificationMessage"));
            financeId = getIntent().getExtras().getString("aID");
            customarId = getIntent().getExtras().getString("customer_id");
            carId = getIntent().getExtras().getString("car_id");
        } else {

            financeId = getIntent().getExtras().getString(Constants.FINANCE_APP_ID);
            customarId = getIntent().getExtras().getString(Constants.CUSTOMER_ID);
            carId = getIntent().getExtras().getString(Constants.FINANCE_CAR_ID);
        }
        fragment= ReuploadDocsFragment.getInstance(financeId);
        FragmentManager mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().replace(R.id.content_frame,fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GCLog.e(Constants.TAG, "Parent's onactivityResult");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bUpload:
                if (checkIfAnyImagesPresent(FinanceReuploadDocsAdapter.mImages)) {
                    insertImages();
                    int i = 1, size;
                    size = financeImagesList.size();
                    for (FinanceData financeImageData : financeImagesList) {
                        financeImageData.setUploadingSequence(i);
                        financeImageData.setTotalImages(size);
                        ApplicationController.getFinanceDB().insertFinanceImagesRecords(financeImageData);
                        i++;
                    }
                    Intent intent = new Intent(this, FinanceImagesUploadService.class);
                    startService(intent);
                    finish();
                    break;
                }
                else
                {
                    CommonUtils.showToast(FinanceReuploadActivity.this, "Select images to upload", Toast.LENGTH_LONG);
                }
        }
    }

    private boolean checkIfAnyImagesPresent(HashMap<String, HashMap<String, DocumentInfo>> mImages) {
        for (Map.Entry<String, HashMap<String, DocumentInfo>> entry : mImages.entrySet()) {
            if (FinanceReuploadDocsAdapter.checkIfAnyImagePresent(entry.getValue()))
                return true;
        }
        return false;
    }

    private void insertImages() {
        ApplicationController.getFinanceDB().insertPendingApplication(financeId);
        for (Map.Entry<String, HashMap<String, DocumentInfo>> entry : FinanceReuploadDocsAdapter.mImages.entrySet()) {
            HashMap<String, DocumentInfo> map = entry.getValue();
            for (Map.Entry<String, DocumentInfo> entry1 : map.entrySet()) {
                DocumentInfo info = entry1.getValue();
                int i = 0;
                for (String s : info.getImages()) {
                    FinanceData financeData = new FinanceData();
                    financeData.setImagePath(s);
                    financeData.setApplicationId(financeId);
                    financeData.setCustomarId(customarId);
                    financeData.setCarId(carId);
                    financeData.setUploadStatus("");
                    financeData.setTag(info.getTag());
                    financeData.setTagTypeId(info.getParentId() + "");
                    String requestName = financeId + "_" + customarId + "_" + info.getTag() + "_" + i;
                    financeData.setRequestName(requestName);
                /*financeData.setUploadingSequence(sequenceNum++);
                financeData.setTotalImages(totalImages);*/
                    financeImagesList.add(financeData);
                    //ApplicationController.getFinanceDB().insertFinanceImagesRecords(financeData);
                    GCLog.e(Constants.TAG, "Image Inserted : " + requestName + " Image Path : " + financeData.getImagePath());
                    i++;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(fragment.sizeOfDocumentImages==0){
            FinanceReuploadActivity.this.finish();
        } else {
            new AlertDialog.Builder(FinanceReuploadActivity.this)
                    .setTitle("Remove Images")
                    .setMessage("All images will be lost. Are you sure you want to quit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // FinanceDBHelper.deleteImages(CommonUtils.getStringSharedPreference(FinanceCollectImagesActivity.this, Constants.FINANCE_APP_ID, ""));
                            FinanceReuploadActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }

    }

}
