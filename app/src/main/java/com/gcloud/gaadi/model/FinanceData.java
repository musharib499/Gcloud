package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Lakshay on 25-05-2015.
 */
public class FinanceData extends DocumentInfo implements Serializable {

    int id;
    String imagePath;
    String carId;
    @SerializedName("file_name")
    String requestName;
    String responseName;
    String uploadStatus, timestamp;
    @SerializedName("finance_lead_id")
    String applicationId;
    String customarId;
    @SerializedName("file_tag")
    String tagId;
    @SerializedName("tag_id")
    String tagTypeId;
    int uploadingSequence;
    int totalImages;

    public String getTagTypeId() {
        return tagTypeId;
    }

    public void setTagTypeId(String tagTypeId) {
        this.tagTypeId = tagTypeId;
    }

    public FinanceData(int id, String imagePath, String requestName, String applicationId, String customarId, String tagId, String tagTypeId, int uploadingSequence, int totalImages) {
        this.id = id;
        this.imagePath = imagePath;
        this.requestName = requestName;
        this.applicationId = applicationId;
        this.customarId = customarId;
        this.tagId = tagId;
        this.tagTypeId = tagTypeId;
        this.parentId = Integer.parseInt(tagTypeId);
        this.uploadingSequence = uploadingSequence;
        this.totalImages = totalImages;
    }

    public FinanceData() {
    }

    public int getUploadingSequence() {
        return uploadingSequence;
    }

    public void setUploadingSequence(int uploadingSequence) {
        this.uploadingSequence = uploadingSequence;
    }

    public int getTotalImages() {
        return totalImages;
    }

    public void setTotalImages(int totalImages) {
        this.totalImages = totalImages;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getCustomarId() {
        return customarId;
    }

    public void setCustomarId(String customarId) {
        this.customarId = customarId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getResponseName() {
        return responseName;
    }

    public void setResponseName(String responseName) {
        this.responseName = responseName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FinanceData{");
        sb.append("id=").append(id);
        sb.append(", imagePath='").append(imagePath).append('\'');
        sb.append(", carId='").append(carId).append('\'');
        sb.append(", requestName='").append(requestName).append('\'');
        sb.append(", responseName='").append(responseName).append('\'');
        sb.append(", uploadStatus='").append(uploadStatus).append('\'');
        sb.append(", timestamp='").append(timestamp).append('\'');
        sb.append(", applicationId='").append(applicationId).append('\'');
        sb.append(", customarId='").append(customarId).append('\'');
        sb.append(", tagId='").append(tagId).append('\'');
        sb.append(", tagTypeId='").append(tagTypeId).append('\'');
        sb.append(", uploadingSequence=").append(uploadingSequence);
        sb.append(", totalImages=").append(totalImages);
        sb.append('}');
        return sb.toString();
    }
}
