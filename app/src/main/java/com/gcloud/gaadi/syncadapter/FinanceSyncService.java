package com.gcloud.gaadi.syncadapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.utils.GCLog;

/**
 * Created by lakshaygirdhar on 6/10/15.
 */
public class FinanceSyncService extends Service {

    FinanceSyncAdapter mAdapter = null;
    private Object mLockObject = new Object();

    @Override
    public void onCreate() {
        super.onCreate();
        GCLog.e(Constants.TAG, "Finance Service created");
        synchronized (mLockObject) {
            if (mAdapter == null) {
                mAdapter = new FinanceSyncAdapter(this, true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAdapter.getSyncAdapterBinder();
    }
}
