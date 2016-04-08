package com.gcloud.gaadi.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.utils.FinanceUtils;
import com.gcloud.gaadi.utils.GCLog;

/**
 * Created by lakshaygirdhar on 6/10/15.
 */
public class FinanceSyncAdapter extends AbstractThreadedSyncAdapter {

    public FinanceSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public FinanceSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        GCLog.e(Constants.TAG, "Perform Sync finance");
        FinanceUtils.performSync();
//        FinanceUtils.getCompaniesData();
//        FinanceUtils.getEmploymentData();
    }
}
