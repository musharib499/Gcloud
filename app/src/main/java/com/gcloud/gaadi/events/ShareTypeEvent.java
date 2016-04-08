package com.gcloud.gaadi.events;

import com.gcloud.gaadi.constants.ShareType;

import java.io.Serializable;

/**
 * Created by ankit on 8/1/15.
 */
public class ShareTypeEvent implements Serializable {

    int fragmentType;
    private ShareType shareType;
    private String carId;
    private String shareText;
    private String extraText;
    private String mobileNumber;
    private String leadName;
    private String imageURL;


    public ShareTypeEvent(ShareType shareType, String shareText, String carId) {
        this.shareType = shareType;
        this.shareText = shareText;
        this.carId = carId;
    }

    public ShareTypeEvent(
            ShareType shareType,
            String shareText,
            String carId,
            String imageURL) {

    }

    public ShareTypeEvent(
            ShareType shareType,
            String shareText,
            String carId,
            int fragmentType) {
        this.shareType = shareType;
        this.shareText = shareText;
        this.carId = carId;
        this.fragmentType = fragmentType;
    }

    public ShareTypeEvent(
            ShareType shareType,
            String shareText,
            String carId,
            String imageURL,
            int fragmentType) {
        this.shareType = shareType;
        this.shareText = shareText;
        this.carId = carId;
        this.imageURL = imageURL;
        this.fragmentType = fragmentType;
    }

    public ShareTypeEvent(
            ShareType shareType,
            String shareText,
            String carId,
            int fragmentType,
            String mobileNumber,
            String leadName) {
        this.shareType = shareType;
        this.shareText = shareText;
        this.carId = carId;
        this.fragmentType = fragmentType;
        this.mobileNumber = mobileNumber;
        this.leadName = leadName;
    }

    public ShareTypeEvent(
            ShareType shareType,
            String shareText,
            String carId,
            String mobileNumber,
            String leadName) {
        this.shareType = shareType;
        this.shareText = shareText;
        this.carId = carId;
        this.mobileNumber = mobileNumber;
        this.leadName = leadName;
    }

    public ShareTypeEvent(
            ShareType shareType,
            String shareText,
            String carId,
            String mobileNumber,
            String leadName,
            String imageURL) {

    }

    public String getLeadName() {
        return leadName;
    }

    public void setLeadName(String leadName) {
        this.leadName = leadName;
    }

    public int getFragmentType() {
        return fragmentType;
    }

    public void setFragmentType(int fragmentType) {
        this.fragmentType = fragmentType;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getExtraText() {
        return extraText;
    }

    public void setExtraText(String extraText) {
        this.extraText = extraText;
    }

    public String getShareText() {
        return shareText;
    }

    public void setShareText(String shareText) {
        this.shareText = shareText;
    }

    public ShareType getShareType() {
        return shareType;
    }

    public void setShareType(ShareType shareType) {
        this.shareType = shareType;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }
}
