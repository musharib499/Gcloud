package com.gcloud.gaadi.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.arellomobile.android.push.PushGCMIntentService;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.BuildConfig;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.insurance.AllCasesActivity;
import com.gcloud.gaadi.insurance.InsuranceDashboardModel;
import com.gcloud.gaadi.insurance.NewInsuranceDashboard;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.InsuranceInspectedCarData;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.service.DatabaseInsertionService;
import com.gcloud.gaadi.ui.Finance.FinanceCasesStatusActivity;
import com.gcloud.gaadi.ui.Finance.FinanceReuploadActivity;
import com.gcloud.gaadi.ui.Finance.GaadiFinanceActivity;
import com.gcloud.gaadi.ui.LeadAddActivity;
import com.gcloud.gaadi.ui.LeadsManageActivity;
import com.gcloud.gaadi.ui.MainActivity;
import com.gcloud.gaadi.ui.SellerLeadsActivity;
import com.gcloud.gaadi.ui.SellerLeadsDetailPageActivity;
import com.gcloud.gaadi.ui.SplashActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;
import com.gcloud.gaadi.webservices.JSONParser;
import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import retrofit.Callback;
import retrofit.RetrofitError;

public class GCMIntentService extends PushGCMIntentService implements OnNoInternetConnectionListener {

    private static final String TAG = "GCMIntentService";
    private static final int SELLER_LEAD_CALL_PENDING_REQUEST_CODE = 103;
    private static final int INSURANCE_DOWNLOAD_PENDING_REQUEST_CODE = 104;
    private static final int INSURANCE_SHARE_PENDING_REQUEST_CODE = 105;
    public static int NotificationID = 10786;
    public static String version = "2.2";
    private final int BUYER_LEAD_PENDING_REQUEST_CODE = 101;
    private final int BUYER_LEAD_CALL_PENDING_REQUEST_CODE = 102;
    private final int FINANCE_PENDING_REQUEST_CODE = 106;
    Context ctx;
    private String eventTagName;
    private String deviceId;
    private String regIdStored;
    private UploadTask gcmTask;

    public static void sendDataToServerFromLogin(Context context) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            PreferenceSettings.openDataBase(context);
            if (PreferenceSettings.getDeviceId() == null) {
                PreferenceSettings.setDeviceId(Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
            }
            String dealerID = PreferenceSettings.getDealerId();//"183675";
            String dealerEmail_ID = PreferenceSettings.getDealerEmailId();//"zahid.naqvi@girnarsoft.com";
            Log.e("Test", TAG + " sending GCM ID To Server");
            String phoneModel = android.os.Build.MODEL;
            phoneModel = phoneModel.replaceAll("\\s+", "");
            String androidVersion = android.os.Build.VERSION.RELEASE;
            PackageInfo pInfo = null;
            try {
                pInfo = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), 0);
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //version = pInfo.versionName;
            //if(version==null ||version.isEmpty())
            //	version="2.2";

            String URL_GCM_3 = "http://www.cardekho.com/getEventFeedsDispatchAction.do?parameter=addRegUserAppDetails&requestFrom=dealerApp&authenticateKey=14@89cardekho66feeds&device_id=";


            String response = JSONParser
                    .getJSONFromUrlChat(URL_GCM_3 + PreferenceSettings.getDeviceId()
                                    + "&gcm_id=" + CommonUtils.getStringSharedPreference(ApplicationController.getInstance(), Constants.GAADI_GCM_ID, "")
                                    + "&user_id=" + dealerID
                                    + "&user_email_id=" + dealerEmail_ID
                                    + "&android_ver=" + androidVersion
                                    + "&app_ver=" + version
                                    + "&phone_model=" + phoneModel + "&dm_source=gaadi"
                    );

            GCLog.e(TAG + " response  GCM ID To Server " + response);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     *
     * @param context
     * @param message
     */
    private static void generateNotification(Context context, String message) {

        JSONObject data = null;
        String messageBody = null;
        int notificationId = 0;
        try {
            JSONObject jobj = new JSONObject(message.toString().trim());
            data = jobj.getJSONObject("gcmNotificationItems");

            if (data.has("notificationMessage"))
                messageBody = URLDecoder.decode(data.getString("notificationMessage"), "UTF-8");

            if (data.has("notificationId"))
                notificationId = data.getInt("notificationId");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.chat_notification_icon)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(messageBody)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(new long[]{0})
                .setAutoCancel(true);

        mBuilder.setSound(uri);

        Intent notificationIntent = new Intent(context, SplashActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        mBuilder.setContentIntent(pendingIntent);


        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(notificationId, mBuilder.build());

    }

    /**
     * Method called on device registered
     */
    @Override
    protected void onRegistered(Context context, String registrationId) {
        GCLog.e(TAG + " onRegistered " + registrationId);

        try {
            ctx = context;
            String regid = registrationId;
            if (Utility.isNetworkAvailable(context)) {

                //PreferenceSettings.openDataBase(context);

                deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
                regIdStored = CommonUtils.getStringSharedPreference(this, Constants.GAADI_GCM_ID, "");
                boolean isGCMIDChanged = false;


                GCLog.e(" Save GCM ID  " + regIdStored);

                if (null != registrationId) {
                    if (regIdStored == null) {
                        regIdStored = regid;
                        CommonUtils.setStringSharedPreference(this, Constants.GAADI_GCM_ID, regIdStored);
                        CommonUtils.setStringSharedPreference(this, Constants.ANDROID_ID, deviceId);
                        /*PreferenceSettings.setGCM(regid);
                        PreferenceSettings.setDeviceId(deviceId);*/
                        isGCMIDChanged = true;
                    } else if (!regIdStored.equals(regid)) {
                        regIdStored = regid;
                        CommonUtils.setStringSharedPreference(this, Constants.GAADI_GCM_ID, regIdStored);
                        CommonUtils.setStringSharedPreference(this, Constants.GAADI_GCM_ID, regIdStored);
                        /*PreferenceSettings.setGCM(regid);
                        PreferenceSettings.setDeviceId(deviceId);*/
                        isGCMIDChanged = true;
                    }
                }

                CommonUtils.setStringSharedPreference(context, Constants.GAADI_GCM_ID, regIdStored);
                //PreferenceSettings.setGCM(regIdStored);


                if (isGCMIDChanged) {
                    // start service to send register id\
                    Log.e("Test", "executeUploadTask start service to send register id ");
                    executeUploadTask();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void executeUploadTask() {
        if (gcmTask != null)
            gcmTask.cancel(true);

        gcmTask = new UploadTask();
        gcmTask.execute();
        GCLog.e("Test", "gcmTask.getStatus() ");
    }

    /**
     * Method called on device un registred
     */
    @Override
    protected void onUnregistered(Context context, String registrationId) {

//        displayMessage(context, getString(R.string.gcm_unregistered));
//        ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message
     */
    @Override
    protected void onMessage(Context context, Intent intent) {
        GCLog.e("messsage: " + intent.getExtras().toString());
        Log.e(TAG, "  onMessage   " + intent.getExtras().getString("u"));
        Log.e(TAG, "  onMessage   " + intent.getExtras().getString("u"));
        GCLog.e("notification: " + intent.getExtras().toString());

        //PreferenceSettings.openDataBase(context);
        //Log.e(TAG, "  onMessage  PreferenceSettings.getIsLogout()  " + PreferenceSettings.getIsLogout());

        GCLog.e("push notification message: " + intent.getExtras().getString("cloud_owner"));
        //if (PreferenceSettings.getIsLogout()) return;
        try {
            parseLive(context, intent);
        } catch (Exception e) {
            if (CommonUtils.canLog()) {
                GCLog.e("exception occurred while reading notifications: " + e.getMessage());
            }
        }


    }

    /**
     * Method called on receiving a deleted message
     */
    @Override
    protected void onDeletedMessages(Context context, int total) {/*

		String message = getString(R.string.gcm_deleted, total);
		// displayMessage(context, message);
		// notifies user
		generateNotification(context, message, null);

	*/
    }

    /**
     * Method called on Error
     */
    @Override
    public void onError(Context context, String errorId) {

        // displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message

        // displayMessage(context, getString(R.string.gcm_recoverable_error,
        // errorId));
        return super.onRecoverableError(context, errorId);
    }

    void parseLive(final Context context, Intent intent) throws Exception {

        final Bundle extras = new Bundle();

        try {

            if (intent.getExtras().getString("u") != null) {
                String message = "";
                message = intent.getExtras().getString("u");
                GCLog.e("GCM", " parseLive Received  " + message);

                JSONObject jobj = new JSONObject(message.trim());
                JSONObject data = jobj.getJSONObject("gcmNotificationItems");
                eventTagName = data.getString("eventTagName");
                GCLog.e("GCM", " eventTagName  " + eventTagName);

                if (eventTagName.equalsIgnoreCase("DEALER_CHAT_REQUEST_NOTIFICATION")) {
                    doWithGcmMesssgae(true, intent, message, data);
                } else if (eventTagName.equalsIgnoreCase("BUYER_LEAD")) {
                    String cloud_owner = data.getString("cloud_owner");
                    //if (BuildConfig.CLOUD_OWNER.equalsIgnoreCase(cloud_owner)) {
                        extras.putString("UCDID", data.getString("UCDID"));
                        extras.putString("notificationMessage", data.getString("notificationMessage"));
                        extras.putString("nID", data.getString("nID"));
                        extras.putString("nType", data.getString("nType"));
                        extras.putString("aID", data.getString("aID"));
                        extras.putString("cloud_owner", cloud_owner);
                        extras.putString("leadId", data.getString("leadId"));
                        extras.putString("title", data.getString("title"));
                    extras.putString(Constants.VIEW_LEAD, Constants.VALUE_VIEWLEAD);

                        final NotificationCompat.Action[] actions = new NotificationCompat.Action[]{
                                new NotificationCompat.Action(R.drawable.call_icon, "Call Now",
                                        PendingIntent.getActivities(context, BUYER_LEAD_CALL_PENDING_REQUEST_CODE,
                                                new Intent[]{
                                                        new Intent(context, MainActivity.class)
                                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                                        | Intent.FLAG_ACTIVITY_NEW_TASK),
                                                        new Intent(context, LeadsManageActivity.class),
                                                        new Intent(context, LeadAddActivity.class)
                                                                .putExtras(extras)
                                                                .putExtra(Constants.VIEW_LEAD, Constants.VALUE_VIEWLEAD)
                                                                .putExtra(Constants.PERFORM_CALL, true)
                                                                .putExtra(Constants.FROM_NOTIFICATION, true)
                                                },
                                                PendingIntent.FLAG_UPDATE_CURRENT))
                        };

                        if (!data.has("image_url") || data.getString("image_url") == null || data.getString("image_url").isEmpty()) {
                            generateGCloudNotification(context, extras, null, actions, data);
                        } else {
                            extras.putString("image_url", data.getString("image_url"));
                            try {
                                Bitmap image = Glide.with(context)
                                        .load(data.getString("image_url"))
                                        .asBitmap()
                                        .into(354, 256)
                                        .get();
                                generateGCloudNotification(context, extras, image, actions, data);
                            } catch (ExecutionException | InterruptedException | OutOfMemoryError ex) {
                                generateGCloudNotification(context, extras, null, actions, data);
                            }
                        }
                    //}

                } else if (eventTagName.equalsIgnoreCase("APP_LOGOUT")) {
                    String responseCode = "0";
                    if (!CommonUtils.getBooleanSharedPreference(context, Constants.SERVICE_EXECUTIVE_LOGIN, false)) {
                        if (Integer.valueOf(data.getString("UCDID"))
                                == CommonUtils.getIntSharedPreference(context, Constants.UC_DEALER_ID, -1)) {
                            CommonUtils.logoutUser(getApplicationContext());
                            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();
                            responseCode = "1";
                            getApplicationContext().startActivity(new Intent(getApplicationContext(),
                                    SplashActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }
                        CommonUtils.logNotification(getApplicationContext(),
                                responseCode, getClass().getSimpleName(), data.toString());
                    }
                }
                else if (eventTagName.equalsIgnoreCase("SELLER_LEAD")) {
                    String cloud_owner = data.getString("cloud_owner");
                    //if (BuildConfig.CLOUD_OWNER.equalsIgnoreCase(cloud_owner)) {
                        extras.putString("UCDID", data.getString("UCDID"));
                        extras.putString("notificationMessage", data.getString("notificationMessage"));
                        extras.putString("nID", data.getString("nID"));
                        extras.putString("nType", data.getString("nType"));
                        extras.putString("aID", data.getString("aID"));
                        extras.putString("cloud_owner", cloud_owner);
                        extras.putString("leadId", data.getString("leadId"));
                        extras.putString("leadname", data.getString("name"));
                        extras.putString("title",data.getString("title"));

                        final NotificationCompat.Action[] actions = new NotificationCompat.Action[]{
                                new NotificationCompat.Action(R.drawable.call_icon, "Call Now",
                                        PendingIntent.getActivities(context, SELLER_LEAD_CALL_PENDING_REQUEST_CODE,
                                                new Intent[]{
                                                        new Intent(context, MainActivity.class)
                                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                                        | Intent.FLAG_ACTIVITY_NEW_TASK),
                                                        new Intent(context, SellerLeadsActivity.class),
                                                        new Intent(context, SellerLeadsDetailPageActivity.class)
                                                                .putExtras(extras)
                                                                .putExtra(Constants.PERFORM_CALL, true)
                                                                .putExtra(Constants.FROM_NOTIFICATION, true)
                                                },
                                                PendingIntent.FLAG_UPDATE_CURRENT))
                        };

                    generateGCloudNotification(context, extras, null, actions, data);
                    //}

                } else if (eventTagName.equalsIgnoreCase("INSURANCE")) {
                    String cloud_owner = data.getString("cloud_owner");
                    //if (BuildConfig.CLOUD_OWNER.equalsIgnoreCase(cloud_owner)) {
                        extras.putString("UCDID", data.getString("UCDID"));
                        extras.putString("notificationMessage", data.getString("notificationMessage"));
                        extras.putString("nID", data.getString("nID"));
                        extras.putString("nType", data.getString("nType"));
                        extras.putString("aID", data.getString("aID"));
                        extras.putString("cloud_owner", cloud_owner);
                        extras.putString("request_id", data.getString("request_id"));
                        extras.putString("title",data.getString("title"));

                        refreshInsuranceDashboardData(context);

                        InsuranceInspectedCarData insuranceData = null;
                    boolean showActions = false;
                        try {
                            if (data.has("insurance_data") && data.getString("insurance_data") != null
                                    && !data.getString("insurance_data").isEmpty()) {
                                insuranceData = new Gson().fromJson(data.getString("insurance_data"),
                                        InsuranceInspectedCarData.class);
                                showActions = insuranceData.getPolicyDocUrl() != null
                                        && !insuranceData.getPolicyDocUrl().isEmpty();
                            }
                        } catch (Exception ex) {

                        }
                    if (showActions) {
                        extras.putString("insurance_data", data.getString("insurance_data"));
                        final NotificationCompat.Action[] actions = new NotificationCompat.Action[]{
                                new NotificationCompat.Action(android.R.drawable.stat_sys_download, "Download",
                                        PendingIntent.getActivities(context, INSURANCE_DOWNLOAD_PENDING_REQUEST_CODE,
                                                new Intent[]{
                                                        new Intent(context, MainActivity.class)
                                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                                        | Intent.FLAG_ACTIVITY_NEW_TASK),
                                                        new Intent(context, AllCasesActivity.class)
                                                                .putExtras(extras)
                                                                .putExtra(Constants.FROM_NOTIFICATION, true)
                                                                .putExtra(Constants.ACTION, "download")
                                                                .putExtra(Constants.INSURANCE_INSPECTED_CAR_DATA, insuranceData)
                                                },
                                                PendingIntent.FLAG_UPDATE_CURRENT)),
                                new NotificationCompat.Action(android.R.drawable.ic_menu_share, "Share",
                                        PendingIntent.getActivities(context, INSURANCE_SHARE_PENDING_REQUEST_CODE,
                                                new Intent[]{
                                                        new Intent(context, MainActivity.class)
                                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                                        | Intent.FLAG_ACTIVITY_NEW_TASK),
                                                        new Intent(context, AllCasesActivity.class)
                                                                .putExtras(extras)
                                                                .putExtra(Constants.FROM_NOTIFICATION, true)
                                                                .putExtra(Constants.ACTION, "share")
                                                                .putExtra(Constants.INSURANCE_INSPECTED_CAR_DATA, insuranceData)
                                                },
                                                PendingIntent.FLAG_UPDATE_CURRENT))
                        };
                        generateGCloudNotification(context, extras, null, actions, data);
                    } else {
                        generateGCloudNotification(context, extras, null, null, data);
                    }
                    //}

                }
                else if (eventTagName.equalsIgnoreCase(Constants.FINANCE_NOTIFICATION_TAG)) {

                    GCLog.e(TAG + "finance aId : " + data.getString("aID"));
                    GCLog.e(TAG + "finance nID : " + data.getString("nID"));
                    GCLog.e(TAG + "finance nType : " + data.getString("nType"));
                    GCLog.e(TAG + "finance title : " + data.getString("title"));

                    extras.putString("UCDID", data.getString("UCDID"));
                    extras.putString("notificationMessage", data.getString("notificationMessage"));
                    extras.putString("nID", data.getString("nID"));
                    extras.putString("nType", data.getString("nType"));
                    extras.putString("aID", data.getString("aID"));
                    extras.putString("title",data.getString("title"));
                    extras.putString("type",data.getString("type"));
                    extras.putString("carId",data.getString("car_id"));
                    extras.putString("loanId",data.getString("loan_id"));
                    extras.putString("customerId",data.getString("customer_id"));


                    if (extras.getString("type").equalsIgnoreCase("finance_action_required")) {

                        final NotificationCompat.Action[] actions = new NotificationCompat.Action[]{
                                new NotificationCompat.Action(R.drawable.ic_menu_camera, "Tap to reupload",
                                        PendingIntent.getActivities(context, FINANCE_PENDING_REQUEST_CODE, new Intent[]{
                                                new Intent(context, MainActivity.class)
                                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK),
                                                new Intent(context, GaadiFinanceActivity.class),
                                                new Intent(context, FinanceReuploadActivity.class)
                                                        .putExtras(extras)}, PendingIntent.FLAG_UPDATE_CURRENT))};
                        generateGCloudNotification(this, extras, null, actions, data);
                    } else {
                        generateGCloudNotification(this, extras, null, null, data);
                    }


                } else {
                    doWithGcmMesssgae(false, intent, message, data);
                }

            } else {
                if (intent.getExtras().getString("cloud_owner") != null) {
                    String cloud_owner = extras.getString("cloud_owner");
                    if (BuildConfig.CLOUD_OWNER.equalsIgnoreCase(cloud_owner)) {
                        GCLog.e(extras.getString("msg"));
                        generateGCloudNotification(context, extras, null, null, null);

                    }

                }
            }
        } catch (Exception e) {
            if (e != null) {
                Crashlytics.logException(e.getCause());
                GCLog.e("exception : " + e.getMessage());
            }
        }
    }

    private void refreshInsuranceDashboardData(final Context context) {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.METHOD_LABEL, Constants.GET_INSURANCE_CASES);
        params.put(Constants.PAGE_NO, String.valueOf(1));
        params.put("type", "count");
        params.put("records", "all");

        RetrofitRequest.getInsuranceDashboard(
                params,
                new Callback<InsuranceDashboardModel>() {

                    @Override
                    public void success(InsuranceDashboardModel response, retrofit.client.Response res) {
                        if ("T".equalsIgnoreCase(response.getStatus())) {
                            CommonUtils.setStringSharedPreference(context,
                                    Constants.INSURANCE_DASHBOARD_OFFLINE_DATA,
                                    new Gson().toJson(response));
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
    }

    private void generateGCloudNotification(Context context, Bundle extras,
                                            Bitmap image, NotificationCompat.Action[] actions,
                                            JSONObject data) throws Exception {
        int pendingRequestCode = 0;

        int dealerId = Integer.valueOf(extras.getString("UCDID"));


        if (!CommonUtils.getBooleanSharedPreference(context, Constants.SERVICE_EXECUTIVE_LOGIN, false)) {

            if (dealerId == CommonUtils.getIntSharedPreference(context, Constants.UC_DEALER_ID, -1)) {

                long rowId = 0;

                if (data != null && Integer.parseInt(extras.getString("nType")) != 2) {
                    Intent databaseIntent = new Intent(context, DatabaseInsertionService.class);
                    databaseIntent.putExtra(Constants.PROVIDER_URI, Uri.parse("content://"
                            + Constants.NOTIFICATION_CONTENT_AUTHORITY + "/" + MakeModelVersionDB.TABLE_NOTIFICATION));
                    databaseIntent.putExtra(Constants.ACTION, "delete");
                    databaseIntent.putExtra(Constants.SELECTION, MakeModelVersionDB.COLUMN_TIME + " < ?");
                    databaseIntent.putExtra(Constants.SELECTION_ARGS,
                            new String[]{String.valueOf(new DateTime().minusDays(15).getMillis())});
                    // Remove all entries older than 15 days
                    startService(databaseIntent);

                    Uri uriWithId = getContentResolver().insert(Uri.parse("content://"
                                    + Constants.NOTIFICATION_CONTENT_AUTHORITY + "/" + MakeModelVersionDB.TABLE_NOTIFICATION),
                            getContentValues(data));

                    rowId = ContentUris.parseId(uriWithId);
                }

                GCLog.e("extras: " + extras.toString());
                String messageBody = extras.getString("notificationMessage");
                String title = extras.getString("title");
                int notificationId = Integer.parseInt(extras.getString("nID"));
                int notificationType = Integer.parseInt(extras.getString("nType")); // notification type
                int autoincrementId = Integer.parseInt(extras.getString("aID")); // notification autoincrement id
                String type = extras.getString("type");

                //Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                NotificationCompat.Builder mBuilder;
                Notification notification;

                try {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                        mBuilder = new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_notification)
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                                .setContentText(messageBody)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                                .setPriority(Notification.PRIORITY_HIGH)
                                .setVibrate(new long[]{0})
                                .setDefaults(Notification.DEFAULT_SOUND)
                                .setAutoCancel(true);

                        if ("".equals(title) || title == null) {
                            mBuilder.setContentTitle(context.getResources().getString(R.string.app_name));
                        } else {
                            mBuilder.setContentTitle(title);
                        }

                        if (actions != null && actions.length > 0) {
                            for (NotificationCompat.Action action : actions) {
                                mBuilder.addAction(action);
                            }
                        }

                        notification = mBuilder.build();

                        if (image != null) {
                            int[] actionIds = new int[]{R.id.action3, R.id.action2, R.id.action1};
                            int[] separatorIds = new int[]{R.id.separator2, R.id.separator1};

                            RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.big_notification);
                            remoteView.setTextViewText(R.id.title,
                                    ("".equals(title) || title == null) ? getString(R.string.app_name) : title);
                            remoteView.setTextViewText(R.id.time, DateTimeFormat.forPattern("hh:mm a").print(new DateTime()));
                            remoteView.setTextViewText(R.id.message, messageBody);
                            remoteView.setImageViewBitmap(R.id.imageView, image);

                            if (actions != null && actions.length > 0) {
                                remoteView.setViewVisibility(R.id.actionLayout, View.VISIBLE);

                                for (int i = 0; i < actions.length; i++) {
                                    remoteView.setViewVisibility(actionIds[i], View.VISIBLE);
                                    remoteView.setTextViewText(actionIds[i], actions[i].getTitle());
                                    remoteView.setTextViewCompoundDrawables(actionIds[i], actions[i].getIcon(), 0, 0, 0);
                                    remoteView.setOnClickPendingIntent(actionIds[i], actions[i].getActionIntent());

                                    if (i > 0) {
                                        remoteView.setViewVisibility(separatorIds[i - 1], View.VISIBLE);
                                    }
                                }
                            }

                            notification.bigContentView = remoteView;
                        }

                        //mBuilder.setSound(uri);
                    } else {
                        mBuilder = new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.gcloud_logo)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                                .setContentText(messageBody)
                                .setDefaults(Notification.DEFAULT_SOUND)
                                .setAutoCancel(true);

                        if ("".equals(title) || title == null) {
                            mBuilder.setContentTitle(context.getResources().getString(R.string.app_name));
                        } else {
                            mBuilder.setContentTitle(title);
                        }

                        //mBuilder.setSound(uri);
                        notification = mBuilder.build();
                    }
                    LinkedList<Intent> intents = new LinkedList<>();
                    intents.add(new Intent(context, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    Intent notificationIntent = null;

                    extras.putBoolean(Constants.FROM_NOTIFICATION, true);
                    if (CommonUtils.getBooleanSharedPreference(context, Constants.USER_LOGGEDIN, false)) {
                        GCLog.e("notification type: " + notificationType);
                        switch (notificationType) {
                            case 1: // Leads notification
                                intents.add(new Intent(context, LeadsManageActivity.class));
                                notificationIntent = new Intent(context, LeadAddActivity.class);
                                //  extras.putString(Constants.CALL_SOURCE, "ML");

                                pendingRequestCode = BUYER_LEAD_PENDING_REQUEST_CODE;
                                break;

                            case 2:
                                GCLog.e(Constants.TAG, "nType is " + notificationType);
                                intents.add(new Intent(context, GaadiFinanceActivity.class));
                                notificationIntent = new Intent(context, FinanceCasesStatusActivity.class);
                              /*  if (type.equalsIgnoreCase("finance_approved")) {
                                    notificationIntent = new Intent(context, FinanceCasesStatusActivity.class);
                                    GCLog.e(Constants.TAG, "Approved");
                                    notificationIntent.putExtra(Constants.LOAN_CASE_STATUS, FinanceUtils.LoanType.APPROVED_CASE);
                                } else if (type.equalsIgnoreCase("finance_rejected")) {
                                    notificationIntent = new Intent(context, FinanceCasesStatusActivity.class);
                                    notificationIntent.putExtra(Constants.LOAN_CASE_STATUS, FinanceUtils.LoanType.REJECTED_CASE);
                                    GCLog.e(Constants.TAG, "Rejected");
                                } else if (type.equalsIgnoreCase("finance_filed")) {
                                    notificationIntent = new Intent(context, FinanceCasesStatusActivity.class);
                                    notificationIntent.putExtra(Constants.LOAN_CASE_STATUS, FinanceUtils.LoanType.PENDING_CASE);
                                    GCLog.e(Constants.TAG, "Pending");
                                }else if (type.equalsIgnoreCase("finance_disbursed")){

                                    notificationIntent.putExtra(Constants.LOAN_CASE_STATUS, FinanceUtils.LoanType.PENDING_CASE);
                                    GCLog.e(Constants.TAG, "Disbursed");
                                }else if(type.equalsIgnoreCase("finance_action_required") *//*&& extras.getBoolean("reuploadImages")*//*){
                                    notificationIntent = new Intent(context, FinanceCasesStatusActivity.class);
                                    GCLog.e(Constants.TAG, "Action Required");
                                    GCLog.e(Constants.TAG, "App Id : "+ extras.getString("loanId"));
                                    GCLog.e(Constants.TAG, "App Id : "+ extras.getString("carId"));
                                    GCLog.e(Constants.TAG, "Customer Id "+ extras.getString("customerId"));
                                    *//*notificationIntent.putExtra(Constants.FINANCE_APP_ID, extras.getString("loanId"));
                                    notificationIntent.putExtra(Constants.FINANCE_CAR_ID, extras.getString("carId"));
                                    notificationIntent.putExtra(Constants.CUSTOMER_ID, extras.getString("customerId"));*//*
                                }*/
                                break;

                            case 6:
                                intents.add(new Intent(context, SellerLeadsActivity.class));
                                notificationIntent = new Intent(context, SellerLeadsDetailPageActivity.class);
                                break;
                            case 7:
                                intents.add(new Intent(context, NewInsuranceDashboard.class));
                                notificationIntent = new Intent(context, AllCasesActivity.class);
                                break;

                            default:
                                notificationIntent = new Intent(context, SplashActivity.class);
                                break;
                        }
                    } else {

                        notificationIntent = new Intent(context, SplashActivity.class);
                    }

                    extras.putLong("rowId", rowId);
                    notificationIntent.putExtras(extras);
                    // set intent so it does not start a new activity
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intents.add(notificationIntent);
                    PendingIntent pendingIntent =
                            PendingIntent.getActivities(context, pendingRequestCode,
                                    intents.toArray(new Intent[intents.size()]),
                                    PendingIntent.FLAG_ONE_SHOT);
                    //mBuilder.setContentIntent(pendingIntent);
                    notification.contentIntent = pendingIntent;


                    NotificationManager nm = (NotificationManager) context
                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.notify(notificationId, notification);


                } catch (Exception e) {
                    CommonUtils.generateNotificationWithoutVibrate(context,
                            notificationId,
                            null,
                            messageBody,
                            notificationType,
                            extras);

                }

            } else {
                CommonUtils.logNotification(getApplicationContext(), "1", getClass().getSimpleName(), extras.toString());
            }
        }

    }

    private ContentValues getContentValues(JSONObject extras) {
        ContentValues values = new ContentValues();
        values.put(MakeModelVersionDB.COLUMN_TYPE, 1);
        values.put(MakeModelVersionDB.COLUMN_TIME, String.valueOf(new DateTime().getMillis()));
        values.put(MakeModelVersionDB.COLUMN_JSON, extras.toString());
        return values;
    }

    private void generateParentStackNotification(Context context, Bundle extras) {


    }

    void doWithGcmMesssgae(boolean isChat, Intent intent1, String message, JSONObject data) {
        if (isChat) {
            try {
                String conversationId = null, chat_with = null, variantId = null, km = null, offerprice = null, price = null,
                        chatsessionid = null, imageurl = null, modelyear = null, title = null, messagebody = null, userchatid = null, name = null;

                if (data.has("messageTitle"))
                    title = URLDecoder.decode(data.getString("messageTitle"), "UTF-8");

                if (data.has("notificationMessage"))
                    messagebody = URLDecoder.decode(data.getString("notificationMessage"), "UTF-8");

                if (data.has("conversationId"))
                    conversationId = data.getString("conversationId");

                if (data.has("chat_with"))
                    chat_with = data.getString("chat_with");

                if (data.has("variantId"))
                    variantId = data.getString("variantId");

                if (data.has("km"))
                    km = data.getString("km");

                if (data.has("modelyear"))
                    modelyear = data.getString("modelyear");

                if (data.has("offerprice"))
                    offerprice = data.getString("offerprice");

                if (data.has("userChatId"))
                    userchatid = data.getString("userChatId");

                if (data.has("price"))
                    price = data.getString("price");

                if (data.has("name"))
                    name = data.getString("name");

                if (data.has("chatsessionid"))
                    chatsessionid = data.getString("chatsessionid");

                if (data.has("imageurl"))
                    imageurl = data.getString("imageurl");
                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.chat_notification_icon)
                        .setContentTitle(title)
                        .setContentText(messagebody)
                        .setAutoCancel(true);

                mBuilder.setSound(uri);

                Intent intent = new Intent(getApplicationContext(), DealerChatActivity.class);
                intent.putExtra("conversationid", conversationId);
                intent.putExtra("chat_with", chat_with);
                intent.putExtra("variantId", variantId);
                intent.putExtra("km", km);
                intent.putExtra("modelyear", modelyear);

                intent.putExtra("offerprice", offerprice);
                intent.putExtra("userchatid", userchatid);
                intent.putExtra("price", price);
                intent.putExtra("name", name);
                intent.putExtra("chatsessionid", chatsessionid);
                intent.putExtra("imageurl", imageurl);
                intent.setAction("com.girnar.cardekho.activity" + System.currentTimeMillis());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);

                mBuilder.setContentIntent(pendingIntent);


                NotificationManager nm = (NotificationManager) getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(conversationId, NotificationID, mBuilder.build());
            } catch (Exception e) {
                //e.printStackTrace();
                if (e != null) {
                    Crashlytics.logException(e.getCause());
                }
            }
        }

    }

    @Override
    public void onNoInternetConnection(NetworkEvent networkEvent) {

    }

    private void sendDataToServer() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            PreferenceSettings.openDataBase(ctx);

            String dealerID = PreferenceSettings.getDealerId();//"183675";
            String dealerEmail_ID = PreferenceSettings.getDealerEmailId();//"zahid.naqvi@girnarsoft.com";
            Log.e("Test", TAG + " sending GCM ID To Server");
            String phoneModel = android.os.Build.MODEL;
            phoneModel = phoneModel.replaceAll("\\s+", "");
            String androidVersion = android.os.Build.VERSION.RELEASE;
            PackageInfo pInfo = null;
            try {
                pInfo = getPackageManager().getPackageInfo(
                        getPackageName(), 0);
            } catch (NameNotFoundException e) {
                if (e != null) {
                    Crashlytics.logException(e.getCause());
                }
            }
            //version = pInfo.versionName;
            //if(version==null ||version.isEmpty())
            //	version="2.2";

            String URL_GCM_3 = "http://www.cardekho.com/getEventFeedsDispatchAction.do?parameter=addRegUserAppDetails&requestFrom=dealerApp&authenticateKey=14@89cardekho66feeds&device_id=";


            String response = JSONParser
                    .getJSONFromUrlChat(URL_GCM_3 + deviceId
                            + "&gcm_id=" + CommonUtils.getStringSharedPreference(ApplicationController.getInstance(), Constants.GAADI_GCM_ID, "")
                            + "&user_id=" + dealerID
                            + "&user_email_id=" + dealerEmail_ID
                            + "&android_ver=" + androidVersion
                            + "&app_ver=" + version
                            + "&phone_model=" + phoneModel + "&dm_source=gaadi");

            GCLog.e(TAG + " response  GCM ID To Server " + response);
            //sendGCMRegisteredUserDataToServer();

        } catch (Exception e) {
            if (e != null) {
                Crashlytics.logException(e.getCause());
            }
        }
    }

  /*  public void startOrStopChatNotification(Context context, int isActive) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            PreferenceSettings.openDataBase(context);
            if (PreferenceSettings.getDeviceId() == null) {
                PreferenceSettings.setDeviceId(Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
            }
            String URL_REMOVE_GCM_KEY = "http://www.cardekho.com/getEventFeedsDispatchAction.do?parameter=activateDeactivatePushNotification&authenticateKey=14@89cardekho66feeds&";
            String response = JSONParser.getJSONFromUrlChat(URL_REMOVE_GCM_KEY + "&device_id=" + PreferenceSettings.getDeviceId() + "&isactive=" + isActive + "&user_id="
                    + PreferenceSettings.getDealerId());

            GCLog.e(TAG + " isActive " + isActive + " response  stopChatNotification " + response);
            sendStart_StopChatNotificatnRequestToServer(isActive);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /**
     * Method to notify the server that user has been registered with GCM
     *//*
    private void sendGCMRegisteredUserDataToServer() {
        String phoneModel = android.os.Build.MODEL;
        phoneModel = phoneModel.replaceAll("\\s+", "");
        String androidVersion = android.os.Build.VERSION.RELEASE;
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(
                    getPackageName(), 0);
        } catch (NameNotFoundException e) {

            e.printStackTrace();
        }
        String appVersion = pInfo.versionName;

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("device_id", PreferenceSettings.getDeviceId());
        params.put("gcm_id", PreferenceSettings.getGCM());
        params.put("dealer_id", PreferenceSettings.getDealerId());
        params.put("dealer_email_id", PreferenceSettings.getDealerEmailId());
        params.put("android_ver", androidVersion);
        params.put("app_version", appVersion);
        params.put("phone_model", phoneModel);
        GCLog.e("App Version" + appVersion);
        GCMRegisteredUserRequest gcmRegisteredUserRequest = new GCMRegisteredUserRequest(this, Request.Method.POST, params,
                Constants.getWebServiceURL(this),
                new Response.Listener<GeneralResponse>() {
                    @Override
                    public void onResponse(GeneralResponse response) {


                        if (response != null) {
                            if ("T".equalsIgnoreCase(response.getStatus())) {
                                GCLog.e(Constants.TAG, "Inside if After GCM Registration request to server" + response.toString());

                            } else {
                                GCLog.e(Constants.TAG, "Inside else After GCM Registration request to server " + response.toString());
                            }
                        }

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        GCLog.e(Constants.TAG, "Some problem occurred");

                    }
                });

        gcmRegisteredUserRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        ApplicationController.getInstance().addToRequestQueue(gcmRegisteredUserRequest, Constants.TAG_GCM_REGISTERED_REQUEST, false, this);
    }*/

    /**
     * Method that sends request to server to start or stop the chat notification
     *
     */

  /*  private void sendStart_StopChatNotificatnRequestToServer(int isActive) {
        HashMap<String, String> params = new HashMap<String, String>();
        String phoneModel = android.os.Build.MODEL;
        phoneModel = phoneModel.replaceAll("\\s+", "");
        String androidVersion = android.os.Build.VERSION.RELEASE;


        params.put("device_id", PreferenceSettings.getDeviceId());
        params.put("dealer_id", PreferenceSettings.getDealerId());
        params.put("android_ver", androidVersion);
        params.put("app_version", version);
        if (isActive == 0)
            params.put("send_notification", "1");   // 1 to start notification and 0 to stop notification
        else
            params.put("send_notification", "0");
        ChatNotificationRequest start_stopChatNotificationRequest = new ChatNotificationRequest(this, Request.Method.POST, params,
                Constants.getWebServiceURL(this),
                new Response.Listener<StartOrStopChatNotificationModel>() {
                    @Override
                    public void onResponse(StartOrStopChatNotificationModel response) {


                        if (response != null) {
                            if ("T".equalsIgnoreCase(response.getStatus())) {
                                GCLog.e("Inside if Start/stop chat request to server" + response.toString());

                            } else {
                                GCLog.e("Inside else  Start/stop chat request to server " + response.toString());
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

        start_stopChatNotificationRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        ApplicationController.getInstance().addToRequestQueue(start_stopChatNotificationRequest, Constants.TAG_START_STOP_CHAT_NOTIFICATION, false, this);
    }
*/

    private class UploadTask extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            sendDataToServer();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

}
