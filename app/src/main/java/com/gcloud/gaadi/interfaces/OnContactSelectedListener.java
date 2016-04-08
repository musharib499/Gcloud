package com.gcloud.gaadi.interfaces;

import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.model.ContactListItem;

/**
 * Created by ankit on 9/1/15.
 */
public interface OnContactSelectedListener {
    public void onContactSelected(ContactListItem contactListItem, String shareText, ShareType shareType, String carId, String imageUrl);
}
