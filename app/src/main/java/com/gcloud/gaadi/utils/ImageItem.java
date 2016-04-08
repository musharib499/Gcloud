package com.gcloud.gaadi.utils;

import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Vaibhav on 5/30/2015.
 */
public class ImageItem {

    ImageButton imageButton;
    ProgressBar progressBar;
    TextView retryText;
    String imagePath;
    String key;

    public ImageItem(ImageButton button, ProgressBar bar, TextView view) {
        this.imageButton = button;
        this.progressBar = bar;
        this.retryText = view;
    }

    public void setImageButton(ImageButton button) {
        this.imageButton = button;
    }

    public ImageButton getImage() {
        return imageButton;
    }

    public ProgressBar getProgressBar() {
        return this.progressBar;
    }

    public void setProgressBar(ProgressBar bar) {
        this.progressBar = bar;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String path) {
        this.imagePath = path;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public TextView getRetryText() {
        return this.retryText;
    }

    public void setRetryText(TextView view) {
        this.retryText = view;
    }
}
