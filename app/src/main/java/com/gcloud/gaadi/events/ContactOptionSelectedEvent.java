package com.gcloud.gaadi.events;

import com.gcloud.gaadi.constants.ContactType;
import com.gcloud.gaadi.constants.ShareType;

import java.io.Serializable;

/**
 * Created by ankit on 22/1/15.
 */
public class ContactOptionSelectedEvent implements Serializable {
    private ContactType contactType;
    private int selectedIndex;
    private String shareText;
    private ShareType shareType;
    private String carId;
    private String imageUrl;

    public ContactOptionSelectedEvent(ContactType contactType, int selectedIndex, String shareText, ShareType shareType, String carId) {
        this.contactType = contactType;
        this.selectedIndex = selectedIndex;
        this.shareText = shareText;
        this.shareType = shareType;
        this.carId = carId;

    }

    public ContactOptionSelectedEvent(ContactType contactType, int selectedIndex, String shareText, ShareType shareType, String carId, String imageUrl) {
        this.contactType = contactType;
        this.selectedIndex = selectedIndex;
        this.shareText = shareText;
        this.shareType = shareType;
        this.carId = carId;
        this.imageUrl = imageUrl;

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public ContactType getContactType() {
        return contactType;
    }

    public void setContactType(ContactType contactType) {
        this.contactType = contactType;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
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

    @Override
    public String toString() {
        return "ContactOptionSelectedEvent{" +
                "contactType=" + contactType +
                ", selectedIndex=" + selectedIndex +
                ", shareText='" + shareText + '\'' +
                ", shareType=" + shareType +
                '}';
    }
}
