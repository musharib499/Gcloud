package com.gcloud.gaadi.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.gcloud.gaadi.constants.Constants;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class DatabaseInsertionService extends IntentService {

    public DatabaseInsertionService() {
        super("DatabaseInsertionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            Bundle extras = intent.getExtras();

            switch (extras.getString(Constants.ACTION)) {
                case "insert":
                    getContentResolver().insert((Uri) extras.get(Constants.PROVIDER_URI),
                            (ContentValues) extras.get(Constants.CONTENT_VALUES));
                    break;

                case "update":
                    getContentResolver().update((Uri) extras.get(Constants.PROVIDER_URI),
                            (ContentValues) extras.get(Constants.CONTENT_VALUES),
                            extras.getString(Constants.SELECTION),
                            extras.getStringArray(Constants.SELECTION_ARGS));
                    break;

                case "delete":
                    getContentResolver().delete((Uri) extras.get(Constants.PROVIDER_URI),
                            extras.getString(Constants.SELECTION),
                            extras.getStringArray(Constants.SELECTION_ARGS));
                    break;
            }
        }
    }
}
