package com.gcloud.gaadi.model;

import java.io.Serializable;

/**
 * Created by ankit on 9/1/15.
 */
public class ContactListItem implements Serializable {
    private String contactName;
    private String contactNumber;

    public ContactListItem(String contactName, String contactNumber) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getGaadiFormatNumber() {
        return this.contactNumber.substring(Math.max(0, this.contactNumber.length() - 10));
    }

    @Override
    public String toString() {
        return "ContactListItem{" +
                "contactName='" + contactName + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                '}';
    }
}
