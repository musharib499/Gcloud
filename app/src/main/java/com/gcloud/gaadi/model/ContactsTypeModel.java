package com.gcloud.gaadi.model;

import com.gcloud.gaadi.constants.ContactType;

import java.io.Serializable;

/**
 * Created by ankit on 8/1/15.
 */
public class ContactsTypeModel implements Serializable {

    private int id;
    private ContactType contactType;

    public ContactsTypeModel(int id, ContactType contactType) {
        this.id = id;
        this.contactType = contactType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ContactType getContactType() {
        return contactType;
    }

    public void setContactType(ContactType contactType) {
        this.contactType = contactType;
    }

    @Override
    public String toString() {
        return "ContactsTypeModel{" +
                "id=" + id +
                ", contactType='" + contactType + '\'' +
                '}';
    }
}
