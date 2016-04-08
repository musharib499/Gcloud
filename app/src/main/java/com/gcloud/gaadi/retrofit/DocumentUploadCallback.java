package com.gcloud.gaadi.retrofit;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.insurance.InsuranceDocument;
import com.gcloud.gaadi.interfaces.DocumentUploadComplete;
import com.gcloud.gaadi.model.InsuranceImageUploadModel;
import com.gcloud.gaadi.utils.GCLog;
import com.gcloud.gaadi.utils.ImageItem;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Gaurav on 22-05-2015.
 */
public class DocumentUploadCallback implements Callback<InsuranceImageUploadModel> {

    final String TAG = "DocumentUploadCallback";
    DocumentUploadComplete listener;
    private Context context;
    private ImageItem formItem;
    private ProgressBar progressBar;
    private ImageButton imageView;

    public DocumentUploadCallback(Context context,
                                  ImageItem formItem,
                                  DocumentUploadComplete listener) {
        this.context = context;
        this.formItem = formItem;
        progressBar = this.formItem.getProgressBar();
        imageView = this.formItem.getImage();
        this.progressBar.setVisibility(View.VISIBLE);
        this.formItem.getRetryText().setVisibility(View.GONE);
        formItem.getImage().setEnabled(false);
        this.listener = listener;
    }

    @Override
    public void success(InsuranceImageUploadModel response, Response response1) {
        /**/
        progressBar.setVisibility(View.GONE);
        GCLog.e("success received status: " + response.getStatus());
        if (response.getStatus().equals("T")) {
            if (formItem.getKey().equals(Constants.PREV_POLICY_COPY)) {
                InsuranceDocument.AllValidPush.put(Constants.PREV_POLICY_COPY, true);
            } else if (formItem.getKey().equals(Constants.RC_COPY)) {
                InsuranceDocument.AllValidPush.put(Constants.RC_COPY, true);
            } else if (formItem.getKey().equals(Constants.FORM29_COPY)) {
                InsuranceDocument.AllValidPush.put(Constants.FORM29_COPY, true);
            } else if (formItem.getKey().equals(Constants.FORM30_COPY)) {
                InsuranceDocument.AllValidPush.put(Constants.FORM30_COPY, true);
            } else if (formItem.getKey().equals(Constants.VH_SELLING_COPY)) {
                InsuranceDocument.AllValidPush.put(Constants.VH_SELLING_COPY, true);
            } else if (formItem.getKey().equals(Constants.VH_PURCHASE_COPY)) {
                InsuranceDocument.AllValidPush.put(Constants.VH_PURCHASE_COPY, true);
            } else if (formItem.getKey().equals(Constants.NCB_COPY)) {
                InsuranceDocument.AllValidPush.put(Constants.NCB_COPY, true);
            }

            if (formItem.getRetryText().getVisibility() == View.VISIBLE)
                formItem.getRetryText().setVisibility(View.GONE);
            listener.onImageUploadComplete(formItem);
        } else {
            if (formItem.getRetryText() != null) {
                formItem.getRetryText().setVisibility(View.VISIBLE);
            }
        }
        /*/
        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
        /**/
    }

    @Override
    public void failure(RetrofitError error) {
        InsuranceDocument.moveToNextScreen = false;
        progressBar.setVisibility(View.GONE);
        if (formItem.getRetryText() != null) {
            formItem.getRetryText().setVisibility(View.VISIBLE);
        } else
            GCLog.e("retry null");
        GCLog.e("RetroError: " + error.toString());
    }
}

