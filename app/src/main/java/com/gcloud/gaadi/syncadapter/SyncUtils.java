package com.gcloud.gaadi.syncadapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.gcloud.gaadi.BuildConfig;
import com.gcloud.gaadi.constants.Constants;

/**
 * Created by Gaurav on 25-08-2015.
 */
public class SyncUtils {
    private static final long SYNC_FREQUENCY = 60 * 60;  // 1 hour (in seconds)
    private static final long SYNC_FREQUENCY_DAY = 24 * 60 * 60;  // 1 day (in seconds)
    private static final String CONTENT_AUTHORITY = Constants.LMS_CONTENT_AUTHORITY;
    private static final String SPLASH_CONTENT_AUTHORITY = Constants.SPLASH_CONTENT_AUTHORITY;
    private static final String LEADS_CONTENT_AUTHORITY = Constants.LEADS_CONTENT_AUTHORITY;
    private static final String PREF_SETUP_COMPLETE = "setup_complete";
    public static final String ACCOUNT_TYPE = BuildConfig.ACCOUNT_TYPE;
    private static final String FINANCE_CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".syncadapter.finance";

    /**
     * Create an entry for this application in the system account list, if it isn't already there.
     *
     * @param context Context
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static void CreateSyncAccount(Context context) {
        boolean newAccount = false;
        boolean setupComplete = PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(PREF_SETUP_COMPLETE, false);

        // Create account, if it's missing. (Either first run, or user has deleted account.)
        Account account = GenericAccountService.GetAccount(ACCOUNT_TYPE);
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {

            ContentResolver.setIsSyncable(account, SPLASH_CONTENT_AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(account, SPLASH_CONTENT_AUTHORITY, true);
            ContentResolver.addPeriodicSync(
                    account, SPLASH_CONTENT_AUTHORITY, new Bundle(), SYNC_FREQUENCY_DAY);

            if ("GAADI".equalsIgnoreCase(BuildConfig.CLOUD_OWNER)) {
                ContentResolver.setIsSyncable(account, CONTENT_AUTHORITY, 1);
                ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, true);
                ContentResolver.addPeriodicSync(
                        account, CONTENT_AUTHORITY, new Bundle(), SYNC_FREQUENCY);

                ContentResolver.setIsSyncable(account, LEADS_CONTENT_AUTHORITY, 1);
                ContentResolver.setSyncAutomatically(account, LEADS_CONTENT_AUTHORITY, true);
                ContentResolver.addPeriodicSync(
                        account, LEADS_CONTENT_AUTHORITY, new Bundle(), SYNC_FREQUENCY);

                ContentResolver.setIsSyncable(account, FINANCE_CONTENT_AUTHORITY, 1);
                ContentResolver.setSyncAutomatically(account,FINANCE_CONTENT_AUTHORITY,true);
                ContentResolver.addPeriodicSync(account, FINANCE_CONTENT_AUTHORITY, new Bundle(), SYNC_FREQUENCY_DAY);
            }
            newAccount = true;
        }

        // Schedule an initial sync if we detect problems with either our account or our local
        // data has been deleted. (Note that it's possible to clear app data WITHOUT affecting
        // the account list, so wee need to check both.)
        if (newAccount || !setupComplete) {
            //TriggerRefresh();
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putBoolean(PREF_SETUP_COMPLETE, true).commit();
        }
    }

    /**
     * Helper method to trigger an immediate sync ("refresh").
     * <p/>
     * <p>This should only be used when we need to preempt the normal sync schedule. Typically, this
     * means the user has pressed the "refresh" button.
     * <p/>
     * Note that SYNC_EXTRAS_MANUAL will cause an immediate sync, without any optimization to
     * preserve battery life. If you know new data is available (perhaps via a GCM notification),
     * but the user is not actively waiting for that data, you should omit this flag; this will give
     * the OS additional freedom in scheduling your sync request.
     */
    public static void TriggerRefresh() {
        Bundle b = new Bundle();
        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(
                GenericAccountService.GetAccount(ACCOUNT_TYPE), // Sync account
                Constants.LMS_CONTENT_AUTHORITY,                 // Content authority
                b);                                             // Extras
    }
}
