package com.gcloud.gaadi.events;

import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.model.ContactListItem;

import java.io.Serializable;

/**
 * Created by ankit on 22/1/15.
 */
public class ContactItemSelectedEvent implements Serializable {
    private ContactListItem contactListItem;
    private ShareType shareType;
    private String shareText;
    private String carId;
    private String imageUrl;

    public ContactItemSelectedEvent(ContactListItem contactListItem, ShareType shareType, String shareText, String carId) {
        this.contactListItem = contactListItem;
        this.shareType = shareType;
        this.shareText = shareText;
        this.carId = carId;
    }

    public ContactItemSelectedEvent(ContactListItem contactListItem, ShareType shareType, String shareText, String carId, String imageUrl) {
        this.contactListItem = contactListItem;
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

    public ContactListItem getContactListItem() {
        return contactListItem;
    }

    public void setContactListItem(ContactListItem contactListItem) {
        this.contactListItem = contactListItem;
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
        return "ContactItemSelectedEvent{" +
                "contactListItem=" + contactListItem +
                ", shareType=" + shareType +
                ", shareText='" + shareText + '\'' +
                '}';
    }
}
