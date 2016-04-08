package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.BuildConfig;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.DealerData;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.service.GaadiGCMIntentService;
import com.gcloud.gaadi.sfa.SfaCampaignTrackingReceiver;
import com.gcloud.gaadi.ui.customviews.BadgeDrawable;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;


public class MainActivity extends BaseActivity
        implements DealerPickerFragment.OnDealerSelectedListener, OnNoInternetConnectionListener {

    public static final String START_KNOWLARITY_SERVICE = "startKnowlarityService";
    boolean broadcastPush = true;
    private GCProgressDialog progressDialog;

    private MainFragment mainFragment;

    //  private ActionBar mActionBar;

    private int selectedIndex = -1, selectedURLIndex = 0;

    private ContentObserver observer = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            invalidateOptionsMenu();
        }
    };


    public static boolean checkPlayServices(Context context) {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (isAvailable != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
                GCLog.e("user is Recoverable ");
//                GooglePlayServicesUtil.getErrorDialog(isAvailable, context, ).show();
            } else {
                GCLog.e("user is not recoverable.");
                //GooglePlayServicesUtil.getErrorDialog(isAvailable, ((Activity) context), 2000).show();
            }
            return false;
        }
        return true;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        progressDialog = new GCProgressDialog(this, (Activity) this);
        //toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        // setSupportActionBar(toolbar);
        //on toolBar.setNavigationIcon(android.R.drawable.ic_lock_silent_mode);
        //   mActionBar = getSupportActionBar();


        if ("HDFC".equalsIgnoreCase(BuildConfig.CLOUD_OWNER)) {

            toolbar.setTitle("");
            View logo = getLayoutInflater().inflate(R.layout.custom_actionbar_home_page, null);
            toolbar.addView(logo);
            //  mActionBar.setCustomView(R.layout.custom_actionbar_home_page);
            // mActionBar.setDispl
            //
            // ayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        } else {

            //toolBar.setDisplayShowHomeEnabled(true);
            // toolBar.setIcon(R.drawable.gcloud_logo);
            toolbar.setLogo(R.drawable.gcloud_logo);

        }


//        mGAHelper = new GAHelper(this);
        if (savedInstanceState == null) {
            mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mainFragment)
                    .commit();
        }

        // code to register on GCM through PushWoosh (removing cardekho gcm registration)

        if (!CommonUtils.getBooleanSharedPreference(MainActivity.this, Constants.SERVICE_EXECUTIVE_LOGIN, false)) {
            /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
            {
                registerReceivers();
                final PushManager pushManager = PushManager
                        .getInstance(MainActivity.this);*/

            // Start push manager, this will count app open for Pushwoosh stats
            // as
            // well
            try {
                // perform GAADI GCM Registration
                startGaadiGCMRegistration();
                    /*pushManager.onStartup(MainActivity.this);
                    pushManager.registerForPushNotifications();
                    checkMessage(getIntent());*/
            } catch (Exception e) {
                Log.e(Constants.TAG, "Gcm Exception : " + e.getMessage());
                Crashlytics.log(Log.ERROR, "error initiating gcm registration", e.getMessage());
            }

            /*}*/
        }
    }

    private void startGaadiGCMRegistration() {
        if (checkPlayServices(this)) {
            try {
                initiateGcmRegistration();
            } catch (IOException e) {
                if (CommonUtils.canLog()) {
                    GCLog.e("exception while registering for gcm: " + e.getMessage());
                }
            }
        } else {
            CommonUtils.showToast(this, "Please install/update Google play services to receive push notifications.", Toast.LENGTH_SHORT);
        }
    }

    private void initiateGcmRegistration() throws IOException {

        if (!checkIfRegistered().isEmpty()) {
            GCLog.e(" device already registered ");
            if (!CommonUtils.getBooleanSharedPreference(this, Constants.GCM_ID_SENT_TO_SERVER, false)) {
                sendRegIdToServer();
            }

        } else {
            registerForGcm();
            //sendRegIdToServer();
        }
    }

    private void registerForGcm() {
        Intent intent = new Intent(this, GaadiGCMIntentService.class);
        startService(intent);
    }

    private void sendRegIdToServer() throws IOException {
        String phoneModel = android.os.Build.MODEL;
        phoneModel = phoneModel.replaceAll("\\s+", "");
        String androidVersion = android.os.Build.VERSION.RELEASE;
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(
                    getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        }
        String appVersion = pInfo.versionName;

        /*GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String registrationId = gcm.register(Constants.GCM_SENDER_ID);*/

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("device_id", CommonUtils.getStringSharedPreference(this, Constants.ANDROID_ID, ""));
        params.put("gcm_id", CommonUtils.getStringSharedPreference(this, Constants.GAADI_GCM_ID, ""));
        params.put("dealer_id", String.valueOf(CommonUtils.getIntSharedPreference(this, Constants.DEALER_ID, -1)));
        params.put("dealer_email_id", CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_EMAIL, ""));
        params.put("android_ver", androidVersion);
        params.put("app_version", appVersion);
        params.put("phone_model", phoneModel);
        params.put(Constants.METHOD_LABEL, Constants.PUSH_DEALER_REGISTRATION);
        GCLog.e("App Version" + appVersion);

        RetrofitRequest.registerGCMIdRequest(params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, retrofit.client.Response response) {
                if (generalResponse != null) {
                    if ("T".equalsIgnoreCase(generalResponse.getStatus())) {
                        CommonUtils.setBooleanSharedPreference(MainActivity.this, Constants.GCM_ID_SENT_TO_SERVER, true);
                        if (CommonUtils.canLog()) {
                            GCLog.e("Inside if After GCM Registration request to server" + generalResponse.toString());
                        }

                    } else {
                        if (CommonUtils.canLog()) {
                            GCLog.e("Inside else After GCM Registration request to server " + generalResponse.toString());
                        }
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (CommonUtils.canLog()) {
                    GCLog.e("Error while registering: " + error.getMessage());
                }
            }
        });
        /*GCMRegisteredUserRequest gcmRegisteredUserRequest = new GCMRegisteredUserRequest(this, Request.Method.POST, params,
                Constants.getWebServiceURL(this),
                new Response.Listener<GeneralResponse>() {
                    @Override
                    public void onResponse(GeneralResponse response) {


                        if (response != null) {
                            if ("T".equalsIgnoreCase(response.getStatus())) {
                                CommonUtils.setBooleanSharedPreference(MainActivity.this, Constants.GCM_ID_SENT_TO_SERVER, true);
                                GCLog.e("Inside if After GCM Registration request to server" + response.toString());

                            } else {
                                GCLog.e("Inside else After GCM Registration request to server " + response.toString());
                            }
                        }

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        GCLog.e("Some problem occurred");

                    }
                });

        gcmRegisteredUserRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        ApplicationController.getInstance().addToRequestQueue(gcmRegisteredUserRequest, Constants.TAG_GCM_REGISTERED_REQUEST, false, this);*/
    }

    // Registration receiver

    private String checkIfRegistered() {

        return CommonUtils.getStringSharedPreference(this, Constants.GAADI_GCM_ID, "");

    }


    /*private BroadcastReceiver mReceiver = new com.arellomobile.android.push.BasePushMessageReceiver() {

        @Override
        protected void onMessageReceive(Intent intent) {
            // JSON_DATA_KEY contains JSON payload of push notification.
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
                if (null != intent.getExtras())
                    doOnMessageReceive(intent.getExtras().getString(JSON_DATA_KEY));

            GCLog.e("Gcm message received " );
        }
    };

    // Registration receiver

    BroadcastReceiver mBroadcastReceiver = new RegisterBroadcastReceiver() {

        @Override
        public void onRegisterActionReceive(Context context, Intent intent) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
                checkMessage(intent);
            GCLog.e("GCM Register Receive : ");
        }
    };*/

// Registration of the receivers

    /*public void registerReceivers() {
        IntentFilter intentFilter = new IntentFilter(getPackageName()
                + ".action.PUSH_MESSAGE_RECEIVE");

        if (broadcastPush)
            registerReceiver(mReceiver, intentFilter);

        registerReceiver(
                mBroadcastReceiver,
                new IntentFilter(
                        getPackageName()
                                + "."
                                + PushManager.REGISTER_BROAD_CAST_ACTION));
    }*/

    /*public void unregisterReceivers() { // Unregister receivers on pause
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) { // pass.

        }

        try {
            unregisterReceiver(mBroadcastReceiver);
        } catch (Exception e) {
            // pass through
        }
    }*/

    /*private void checkMessage(Intent intent) {
        if (null != intent) {
            if (intent
                    .hasExtra(com.arellomobile.android.push.PushManager.PUSH_RECEIVE_EVENT)) {
                doOnMessageReceive(intent
                        .getExtras()
                        .getString(
                                com.arellomobile.android.push.PushManager.PUSH_RECEIVE_EVENT));
            }

            resetIntentValues();
        }
    }*/

    /*private void resetIntentValues() {
        Intent mainAppIntent = getIntent();

        if (mainAppIntent
                .hasExtra(com.arellomobile.android.push.PushManager.PUSH_RECEIVE_EVENT)) {
            mainAppIntent
                    .removeExtra(com.arellomobile.android.push.PushManager.PUSH_RECEIVE_EVENT);
        } else if (mainAppIntent
                .hasExtra(com.arellomobile.android.push.PushManager.REGISTER_EVENT)) {
            mainAppIntent
                    .removeExtra(com.arellomobile.android.push.PushManager.REGISTER_EVENT);
        } else if (mainAppIntent
                .hasExtra(com.arellomobile.android.push.PushManager.UNREGISTER_EVENT)) {
            mainAppIntent
                    .removeExtra(com.arellomobile.android.push.PushManager.UNREGISTER_EVENT);
        } else if (mainAppIntent
                .hasExtra(com.arellomobile.android.push.PushManager.REGISTER_ERROR_EVENT)) {
            mainAppIntent
                    .removeExtra(com.arellomobile.android.push.PushManager.REGISTER_ERROR_EVENT);
        } else if (mainAppIntent
                .hasExtra(com.arellomobile.android.push.PushManager.UNREGISTER_ERROR_EVENT)) {
            mainAppIntent
                    .removeExtra(com.arellomobile.android.push.PushManager.UNREGISTER_ERROR_EVENT);
        }

        setIntent(mainAppIntent);
    }*/

    /*public void doOnMessageReceive(String message) {
        if (message != null && !message.equalsIgnoreCase("")) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this,
                    getResources().getString(R.string.error_msg),  Toast.LENGTH_LONG)
                    .show();

        }
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        boolean isServiceExecutiveLogin = CommonUtils.getBooleanSharedPreference(this, Constants.SERVICE_EXECUTIVE_LOGIN, false);

        if (BuildConfig.IS_DEV_BUILD) {
            if (isServiceExecutiveLogin) {
                getMenuInflater().inflate(R.menu.menu_se_main_dev, menu);
            } else {
                getMenuInflater().inflate(R.menu.menu_main_dev, menu);
            }
        } else {
            if (isServiceExecutiveLogin) {
                getMenuInflater().inflate(R.menu.menu_se_main, menu);
            } else {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.menu_main, menu);
            }
        }

        try {
            Cursor cursor = getContentResolver().query(Uri.parse("content://"
                            + Constants.NOTIFICATION_CONTENT_AUTHORITY + "/" + MakeModelVersionDB.TABLE_NOTIFICATION),
                    new String[]{MakeModelVersionDB.COLUMN_ID},
                    MakeModelVersionDB.COLUMN_TYPE + " = ?",
                    new String[]{"1"}, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    MenuItem itemCart = menu.findItem(R.id.action_notification);
                    LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
                    BadgeDrawable.setBadgeCount(this, icon, String.valueOf(cursor.getCount()), R.id.action_notification);
                }
                cursor.close();
            }
        } catch (Exception ex) {
            Crashlytics.logException(ex);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent = null;

        switch (id) {
            case R.id.action_notification:
                startActivity(new Intent(this, NotificationActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                break;

            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.changeDealer:

                if (!(MainActivity.this.isFinishing()) && CommonUtils.getBooleanSharedPreference(this, Constants.SERVICE_EXECUTIVE_LOGIN, false)) {
                    selectedIndex = CommonUtils.getIntSharedPreference(this, Constants.SELECTED_DEALER_INDEX, -1);
                    DealerPickerFragment dealerPickerFragment = DealerPickerFragment.newInstance(getString(R.string.select_dealer),
                            selectedIndex);
                    dealerPickerFragment.show(getSupportFragmentManager(), "dealer-picker-dialog");
                }
                return true;

            case R.id.action_change_url:
                selectedURLIndex = CommonUtils.getIntSharedPreference(this, Constants.SELECTED_URL_INDEX, 0);

                URLPickerFragment urlPickerFragment = URLPickerFragment.newInstance(R.string.change_host_url,
                        selectedURLIndex);
                urlPickerFragment.show(getSupportFragmentManager(), "url-picker-dialog");

                return true;

            case R.id.action_logout:
                CommonUtils.logoutUser(this);
                CommonUtils.setStringSharedPreference(getApplicationContext(),
                        SfaCampaignTrackingReceiver.EXTRA_ACCESS_RIGHTS, "");
                //GCMIntentService service=new GCMIntentService();
                //service.startOrStopChatNotification(this,1);
                // GCMIntentService.startOrStopChatNotification(this, 1);
                NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                nMgr.cancelAll();
                //PreferenceSettings.openDataBase(this);
                //PreferenceSettings.ClearAll();
                intent = new Intent(this, SplashActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;

            case R.id.action_feedback:
                /*if (ApplicationController.checkInternetConnectivity()) {
                    requestFeedbackCallTime();
                } else {
                    CommonUtils.showToast(this, getString(R.string.network_error), Toast.LENGTH_SHORT);
                }*/
                startActivity(new Intent(MainActivity.this, FeedbackActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
            unregisterReceivers();*/

        getContentResolver().unregisterContentObserver(observer);
    }


    @Override
    protected void onResume() {
        super.onResume();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_HOME_PAGE);
        //registerReceivers();
        invalidateOptionsMenu();

        getContentResolver().registerContentObserver(Uri.parse("content://"
                        + Constants.NOTIFICATION_CONTENT_AUTHORITY + "/" + MakeModelVersionDB.TABLE_NOTIFICATION),
                true, observer);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (CommonUtils.getIntSharedPreference(this, Constants.SFA_USER_ID, 0) != 0) {
            CommonUtils.logoutUser(this);
            CommonUtils.setStringSharedPreference(getApplicationContext(),
                    SfaCampaignTrackingReceiver.EXTRA_ACCESS_RIGHTS, "");
        }

    }

    @Override
    public void onDealerSelected(DealerPickerFragment fragment, DealerData dealer, int index) {
        selectedIndex = index;

        GCProgressDialog progressDialog = new GCProgressDialog(this, this,
                "Please Wait. We are resetting the privileges.");
        progressDialog.setCancelable(false);
        mainFragment = new MainFragment().setProgressDialog(progressDialog);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mainFragment)
                    .commit();

        if (!this.isFinishing()) {
            progressDialog.show();
        }
    }

    @Override
    public void onNoInternetConnection(NetworkEvent networkEvent) {
        CommonUtils.showToast(this, getString(R.string.network_error), Toast.LENGTH_SHORT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mainFragment != null && mainFragment.isAdded()) {
            mainFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
