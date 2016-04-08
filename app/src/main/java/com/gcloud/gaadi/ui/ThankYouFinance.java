package com.gcloud.gaadi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.ApplicationCompleteEvent;
import com.gcloud.gaadi.model.CarItemModel;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.retrofit.FinanceImagesUploadService;
import com.gcloud.gaadi.retrofit.RetrofitImageUploadService;
import com.gcloud.gaadi.retrofit.RetrofitRequest;

import com.gcloud.gaadi.ui.Finance.GaadiFinanceActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.google.gson.Gson;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Lakshay on 27-05-2015.
 */
public class ThankYouFinance extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onPause() {
        super.onPause();
        ApplicationController.getEventBus().unregister(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ApplicationController.getEventBus().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_FINANCE_THANK_YOU);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GCLog.e("oncreate thanks");
        getLayoutInflater().inflate(R.layout.thanks_finance, frameLayout);
        String financeId = CommonUtils.getStringSharedPreference(this, Constants.FINANCE_APP_ID, "");
        findViewById(R.id.tvGoToMain).setOnClickListener(this);
        String carItemJson = CommonUtils.getStringSharedPreference(this, Constants.CAR_DATA, "");
        CarItemModel carItemModel = (new Gson()).fromJson(carItemJson, CarItemModel.class);
                ((TextView) findViewById(R.id.tvApplicationNo)).setText(financeId);
        ((TextView) findViewById(R.id.tvRegNum)).setText(carItemModel.getRegno());
        ((TextView) findViewById(R.id.tv_makeModelVersion)).setText(carItemModel.getMake() + " " + carItemModel.getModel());
        setUpActionBar();

        try {
//            submitApplicationRequest(financeId);
        } catch (Exception e) {

        }

        ApplicationController.getEventBus().post(new ApplicationCompleteEvent());

       // ApplicationController.getFinanceDB().insertPendingApplication(financeId);
        CommonUtils.setIntSharedPreference(this, FinanceImagesUploadService.KEY_MAXRETRY, 5);
       /* int totalImages = ApplicationController.getFinanceDB().getImagesForUpload().size();
        ApplicationController.getFinanceDB().insertImageUploadCount(totalImages, 0);
        startService(new Intent(this, RetrofitImageUploadService.class));*/
        startService(new Intent(this, FinanceImagesUploadService.class));

        setTitleMsg("Loan Application Submitted");

        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_FINANCE_THANK_YOU);
    }

    private void submitApplicationRequest(String financeId) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.METHOD_LABEL, Constants.FINANCE_APP_COMPLETE);
        jsonObject.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_USERNAME, ""));
        jsonObject.put(Constants.DEALER_PASSWORD, CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_PASSWORD, ""));
        jsonObject.put(Constants.FINANCE_LEAD_ID, financeId);

        RetrofitRequest.submitFinanceApplication(jsonObject, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, Response response) {
                GCLog.e("application submitted");

            }

            @Override
            public void failure(RetrofitError error) {
                GCLog.e("application submission error");
            }
        });
    }

    private void setUpActionBar() {
        toolbar.setTitle("Loan Application Submitted");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        ApplicationController.getEventBus().post(new ApplicationCompleteEvent());
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvGoToMain:
                Intent intent = new Intent(this, GaadiFinanceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
        }
    }

}
