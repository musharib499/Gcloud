package com.gcloud.gaadi.events;

import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.model.CallLogItem;

import java.io.Serializable;

/**
 * Created by ankit on 22/1/15.
 */
public class CallLogItemSelectedEvent implements Serializable {

    private CallLogItem callLogItem;
    private ShareType shareType;
    private String shareText;
    private String carId;
    private String imageUrl;

    public CallLogItemSelectedEvent(CallLogItem callLogItem, ShareType shareType, String shareText, String carId) {
        this.callLogItem = callLogItem;
        this.shareType = shareType;
        this.shareText = shareText;
        this.carId = carId;

    }

    public CallLogItemSelectedEvent(CallLogItem callLogItem, ShareType shareType, String shareText, String carId, String imageUrl) {
        this.callLogItem = callLogItem;
        this.shareType = shareType;
        this.shareText = shareText;
        this.carId = carId;
        this.imageUrl = imageUrl;

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public CallLogItem getCallLogItem() {
        return callLogItem;
    }

    public void setCallLogItem(CallLogItem callLogItem) {
        this.callLogItem = callLogItem;
    }

    public ShareType getShareType() {
        return shareType;
    }

    public void setShareType(ShareType shareType) {
        this.shareType = shareType;
    }

    public String getShareText() {
        return shareText;
    }

    public void setShareText(String shareText) {
        this.shareText = shareText;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    @Override
    public String toString() {
        return "ContactSelectedEvent{" +
                "callLogItem=" + callLogItem +
                ", shareType=" + shareType +
                ", shareText='" + shareText + '\'' +
                '}';
    }
}
