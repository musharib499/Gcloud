package com.gcloud.gaadi.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ankurjha on 30/9/15.
 */
public class FeedbackActivity extends BaseActivity {

    EditText feedbackEdittext;
    Button mSendFeedback, mCallnow;
    private GCProgressDialog progressDialog;
    private ProgressBar progressBar;
    private String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_dealer_feedback);
        getLayoutInflater().inflate(R.layout.activity_dealer_feedback, frameLayout);
        mSendFeedback = (Button) findViewById(R.id.sendfeedback);
        feedbackEdittext = (EditText) findViewById(R.id.feedbackEdittext);
        mCallnow = (Button) findViewById(R.id.callnow);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        /*Boolean isCallButtonActive = getIntent().getExtras().getBoolean("can_call");
        final String phoneNumber = getIntent().getExtras().getString("phone_number");*/
        progressDialog = new GCProgressDialog(this, (Activity) this);
        /*if (isCallButtonActive) {
            mCallnow.setEnabled(true);
        } else {
            mCallnow.setEnabled(false);
        }*/
        mCallnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !CommonUtils.checkForPermission(FeedbackActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        Constants.REQUEST_PERMISSION_CALL, "Phone")) {
                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
            }
        });
        mSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedbackEdittext.getText().toString().trim().isEmpty()) {
                    CommonUtils.showToast(FeedbackActivity.this, "Feedback cannot be left blank", Toast.LENGTH_SHORT);
                } else {
                    sendFeedbackRequest(feedbackEdittext.getText().toString());
                }
            }
        });

        requestFeedbackCallTime();
    }

    public void requestFeedbackCallTime() {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> params = new HashMap<>();
        params.put("can_call", "" + 1);
        params.put(Constants.METHOD_LABEL, Constants.SEND_DEALER_FEEDBACK);
        RetrofitRequest.sendFeedBack(params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse response, retrofit.client.Response res) {
                progressBar.setVisibility(View.GONE);
                if ("T".equalsIgnoreCase(response.getStatus())) {
                    mCallnow.setEnabled(true);
                    phoneNumber = response.getMessage();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void sendFeedbackRequest(String feedback) {
        progressDialog.setCancelable(false);
        progressDialog.show();
        HashMap<String, String> params = new HashMap<>();
        params.put("feedback", feedback);
        params.put(Constants.METHOD_LABEL, Constants.SEND_DEALER_FEEDBACK);
        RetrofitRequest.sendFeedBack(params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse response, Response res) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                if ("T".equalsIgnoreCase(response.getStatus())) {
                    CommonUtils.showToast(FeedbackActivity.this, response.getMessage(), Toast.LENGTH_LONG);
                    finish();
                } else {

                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                CommonUtils.showErrorToast(FeedbackActivity.this, error, Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_BUYER_LEAD_DETAIL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Intent callIntent = null;
        if (requestCode == Constants.REQUEST_PERMISSION_CALL
                && grantResults.length > 1) {
            callIntent = new Intent(Intent.ACTION_CALL);
        } else {
            callIntent = new Intent(Intent.ACTION_DIAL);
        }
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }
}
