package com.gcloud.gaadi.model.Finance;

import android.content.Context;

import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.CommonRequestParams;

import java.io.Serializable;

/**
 * Created by lakshaygirdhar on 1/10/15.
 */
public class FinanceDashboardRequest extends CommonRequestParams implements Serializable {

    private String packageName;

    public FinanceDashboardRequest(Context context) {
        super(context);
        packageName = Constants.PACKAGE_FINANCE;
    }
}
