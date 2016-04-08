package com.gcloud.gaadi.model.Finance;

import android.content.Context;

import com.gcloud.gaadi.model.CommonRequestParams;

/**
 * Created by lakshaygirdhar on 6/10/15.
 */
public class FinanceRequest extends CommonRequestParams {

    private String packageName;

    public FinanceRequest(Context context) {
        super(context);
    }
}
