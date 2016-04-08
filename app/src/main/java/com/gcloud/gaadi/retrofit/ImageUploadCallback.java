package com.gcloud.gaadi.retrofit;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.gcloud.gaadi.interfaces.ImageUploadComplete;
import com.gcloud.gaadi.model.FormItem;
import com.gcloud.gaadi.model.ImageUploadResponse;
import com.gcloud.gaadi.ui.customviews.ImageUploadImageView;
import com.gcloud.gaadi.utils.GCLog;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Gaurav on 22-05-2015.
 */
public class ImageUploadCallback implements Callback<ImageUploadResponse> {

    final String TAG = "ImageUploadCallback";

    private Context context;
    private FormItem formItem;
    private ProgressBar progressBar;
    private ImageUploadImageView imageView;

    private ImageUploadComplete listener;

    public ImageUploadCallback(Context context,
                               FormItem formItem,
                               ImageUploadComplete listener) {
        this.context = context;
        this.formItem = formItem;
        progressBar = formItem.getImageProgress();
        imageView = formItem.getIvIcon();
        this.listener = listener;
        this.progressBar.setVisibility(View.VISIBLE);
        this.imageView.changeStatus(ImageUploadImageView.UPLOADING);
        formItem.setClickable(false);
    }

    @Override
    public void success(ImageUploadResponse imageUploadResponse, Response response) {
        /**/
        progressBar.setVisibility(View.GONE);
        GCLog.e("success received status: " + imageUploadResponse.getStatus());
        if (imageUploadResponse.getStatus().equals("T")) {
            imageView.changeStatus(ImageUploadImageView.UPLOADED);
            listener.onImageUploadComplete(formItem);
        } else {
            if (formItem.getRetry() != null) {
                formItem.getRetry().setVisibility(View.VISIBLE);
            } else
                GCLog.e("retry null");
            formItem.setClickable(true);
        }
        /*/
        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
        /**/
    }

    @Override
    public void failure(RetrofitError error) {
        progressBar.setVisibility(View.GONE);
        if (formItem.getRetry() != null) {
            formItem.getRetry().setVisibility(View.VISIBLE);
        }
        /*Toast.makeText(context, "RetroError", Toast.LENGTH_SHORT).show();
        formItem.setClickable(true);*/
        GCLog.e("RetroError: " + error.toString());
    }
}
