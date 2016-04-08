package com.gcloud.gaadi.syncadapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.gcloud.gaadi.utils.GCLog;

/**
 * Created by Gaurav on 25-08-2015.
 */
public class SplashSyncService extends Service {
    private static final String TAG = "SplashSyncService";

    private static final Object sSyncAdapterLock = new Object();
    private static SplashSyncAdapter sSyncAdapter = null;

    /**
     * Thread-safe constructor, creates static {@link SyncAdapter} instance.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        GCLog.e(TAG + "Service created");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SplashSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    /**
     * Logging-only destructor.
     */
    public void onDestroy() {
        super.onDestroy();
        GCLog.e(TAG + "Service destroyed");
    }

    /**
     * Return Binder handle for IPC communication with {@link SyncAdapter}.
     * <p/>
     * <p>New sync requests will be sent directly to the SyncAdapter using this channel.
     *
     * @param intent Calling intent
     * @return Binder handle for {@link SyncAdapter}
     */
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
