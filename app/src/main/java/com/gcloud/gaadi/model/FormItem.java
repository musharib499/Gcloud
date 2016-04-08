package com.gcloud.gaadi.model;

import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gcloud.gaadi.ui.customviews.ImageUploadImageView;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Lakshay on 22-05-2015.
 */
public class FormItem implements Serializable {

    String imagePath;
    String itemText;

    ProgressBar imageProgress;
    ImageUploadImageView ivIcon;
    ImageButton remove = null;
    TextView retry = null;
    int tag;
    String applicationID;
    private JSONObject jsonObject;
    private Boolean showRetry = false, showProgressBar = false;
    private boolean isClickable = true;

    public FormItem(String imagePath, String itemText, ProgressBar imageProgress, TextView retry) {
        this.imagePath = imagePath;
        this.itemText = itemText;
        this.imageProgress = imageProgress;
        this.retry = retry;
    }

    public FormItem() {
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

    public ImageUploadImageView getIvIcon() {
        return ivIcon;
    }

    public void setIvIcon(ImageUploadImageView ivIcon) {
        this.ivIcon = ivIcon;
    }

    public ProgressBar getImageProgress() {
        return imageProgress;
    }

    public void setImageProgress(ProgressBar imageProgress) {
        this.imageProgress = imageProgress;
    }

    public boolean getClickable() {
        return isClickable;
    }

    public void setClickable(boolean status) {
        isClickable = status;
    }

    public ImageButton getRemove() {
        return remove;
    }

    public void setRemove(ImageButton remove) {
        this.remove = remove;
    }

    public TextView getRetry() {
        return retry;
    }

    public void setRetry(TextView textView) {
        retry = textView;
    }

    public Boolean getShowRetry() {
        return showRetry;
    }

    public void setShowRetry(Boolean showRetry) {
        this.showRetry = showRetry;
    }

    public Boolean getShowProgressBar() {
        return showProgressBar;
    }

    public void setShowProgressBar(Boolean showProgressBar) {
        this.showProgressBar = showProgressBar;
    }

    public void clear() {
        this.imagePath = null;
        isClickable = true;
    }
}