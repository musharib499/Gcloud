package com.gcloud.gaadi.interfaces;

import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.model.CallLogItem;

/**
 * Created by ankit on 26/12/14.
 */
public interface CallLogsItemClickInterface {
    public void onCallLogSelected(CallLogItem callLogItem, ShareType shareType, String shareText, String carId, String imageUrl);
}
