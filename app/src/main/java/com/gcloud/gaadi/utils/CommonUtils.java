package com.gcloud.gaadi.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.crashlytics.android.Crashlytics;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.BuildConfig;
import com.gcloud.gaadi.CallStateListener;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.DBFunction;
import com.gcloud.gaadi.db.InsuranceDB;
import com.gcloud.gaadi.db.LeadsOfflineDB;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.db.ManageLeadDB;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.ImageUploadResponse;
import com.gcloud.gaadi.model.InventoryImageDeleteResponse;
import com.gcloud.gaadi.model.LeadData;
import com.gcloud.gaadi.model.RSALogEventModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.service.ImageUploadService;
import com.gcloud.gaadi.service.PendingImagesService;
import com.gcloud.gaadi.ui.LeadsManageActivity;
import com.gcloud.gaadi.ui.SplashActivity;
import com.gcloud.gaadi.ui.StockAddActivity;
import com.imageuploadlib.Databases.StockImagesDB;
import com.imageuploadlib.Model.StockImageData;
import com.imageuploadlib.Model.StockImageOrderData;
import com.imageuploadlib.Utils.FileInfo;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by ankit on 17/11/14.
 */
public class CommonUtils {

    private static final String TAG = "CommonUtils";

    public static void activateLeadCallStateListener(Context context,
                                                     Class nextClass,
                                                     String phoneNumber,
                                                     String arg1,
                                                     int arg2,
                                                     LeadData arg3) {
        Bundle args = new Bundle();
        Intent intent = new Intent(context, nextClass);
        args.putString(Constants.VIEW_LEAD, arg1);
        args.putInt(Constants.SELECTED_TAB, arg2);
        args.putSerializable(Constants.MODEL_DATA, arg3);
        intent.putExtras(args);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        /*CallStateListener callStateListener = */
        new CallStateListener(context, intent, phoneNumber);
        //callStateListener.setPhoneNumber(phoneNumber);
    }


    //Do not use it for general purposes it is written specifically for Finance reupload items
    public static void expand(final View v, Context context, int heightPx) {
        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightPx, context.getResources().getDisplayMetrics());
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = height;

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? height
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void hideKeyboard(Context context, View view) {
        if (context == null || view == null) {
            return;
        }
        InputMethodManager inputManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public static String getMonthShortForm(int month) {
        switch (month) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
        }
        return "";
    }

    public static String camelCase(String input) {
        StringBuilder camelCase = new StringBuilder();
        boolean change = true;
        for (int i = 0; i < input.length(); i++) {
            String atIndex = String.valueOf(input.charAt(i));
            if (change) {
                camelCase.append(atIndex.toUpperCase());
                change = false;
            } else {
                camelCase.append(atIndex.equalsIgnoreCase(" ") || atIndex.equalsIgnoreCase("-") ? atIndex : atIndex.toLowerCase());
            }
            if (atIndex.equalsIgnoreCase(" ") || atIndex.equalsIgnoreCase("-")) {
                change = true;
            }
        }
        return camelCase.toString();
    }

    public static String getPolicyName(int month) {
        switch (month) {
            case Constants.policyCode:
                return Constants.PREV_POLICY_COPY;
            case Constants.rcCode:
                return Constants.RC_COPY;
            case Constants.form29Code:
                return Constants.FORM29_COPY;
            case Constants.form30Code:
                return Constants.FORM30_COPY;
            case Constants.sellCode:
                return Constants.VH_SELLING_COPY;
            case Constants.purchaseCode:
                return Constants.VH_PURCHASE_COPY;
            case Constants.ncbCode:
                return Constants.NCB_COPY;
        }
        return "";
    }

    public static boolean isValidField(String data) {
        return data != null && !data.isEmpty();
    }

    public static boolean isValidField(ArrayList<?> data) {
        return data != null && !data.isEmpty();
    }

    public static void setStringSharedPreference(Context context, String key, String value) {
        SharedPreferences preferences = context.getApplicationContext().
                getSharedPreferences(
                        Constants.APP_SHARED_PREFERENCE,
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if ((key != null) && !key.isEmpty()) {
            editor.putString(key, value);
            editor.apply();
        }
    }

    public static void setIntSharedPreference(Context context, String key, int value) {
        SharedPreferences preferences = context.getApplicationContext()
                .getSharedPreferences(
                        Constants.APP_SHARED_PREFERENCE,
                        Context.MODE_PRIVATE
                );
        SharedPreferences.Editor editor = preferences.edit();

        if ((key != null) && !key.isEmpty()) {
            editor.putInt(key, value);
            editor.apply();
        }

    }

    public static void logNotification(Context context , String responseCode, String screenName, String message){
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.GCM_RESPONSE_CODE, responseCode); // for notifications that are delivered but not destined for this dealer
        params.put(Constants.SCREEN_NAME, screenName);
        params.put(Constants.GCM_MESSAGE, message);
        params.put(Constants.METHOD_LABEL, Constants.LOG_NOTIFICATION_METHOD);

        RetrofitRequest.logNotificationRequest(context, params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, retrofit.client.Response response) {
                GCLog.e("notification logged");
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

       /* LogNotificationRequest logNotificationRequest = new LogNotificationRequest(
                context,
                Request.Method.POST,
                Constants.getWebServiceURL(context),
                params,
                new Response.Listener<GeneralResponse>() {
                    @Override
                    public void onResponse(GeneralResponse response) {
                        GCLog.e("notification logged");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        ApplicationController.getInstance().addToRequestQueue(logNotificationRequest);*/
    }

    public static void setBooleanSharedPreference(Context context, String key, boolean value) {
        SharedPreferences preferences = context.getApplicationContext().
                getSharedPreferences(
                        Constants.APP_SHARED_PREFERENCE,
                        Context.MODE_PRIVATE
                );
        SharedPreferences.Editor editor = preferences.edit();
        if ((key != null) && !key.isEmpty()) {
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    public static String getLeadStatus(String selectedStatus) {
        String status = "";

        if (selectedStatus.equals("Hot")) {
            status = "H";
        } else if (selectedStatus.equals("Cold")) {
            status = "C";
        } else if (selectedStatus.equals("Warm")) {
            status = "W";
        } else if (selectedStatus.equals("Close")) {
            status = "Cl";
        }
        return status;
    }

    public static String getStringSharedPreference(Context context, String key, String defaultValue) {
        SharedPreferences preferences = context.
                getSharedPreferences(
                        Constants.APP_SHARED_PREFERENCE,
                        Context.MODE_PRIVATE
                );

        if (preferences.contains(key)) {
            return preferences.getString(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    // To get IPv4 address: getIPAddress("ipv4")
    // To get IPv6 address: getIPAddress("ipv6")
    public static String getIPAddress(String type) {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (type.equalsIgnoreCase("ipv4")
                                && inetAddress instanceof Inet4Address) {
                            return inetAddress.getHostAddress();
                        } else if (type.equalsIgnoreCase("ipv6")
                                && inetAddress instanceof Inet6Address) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            GCLog.e("IP Address", ex.toString());
        }
        return "";
    }

    public static boolean getBooleanSharedPreference(Context context, String key, boolean defaultValue) {
        SharedPreferences preferences = context.getApplicationContext().
                getSharedPreferences(
                        Constants.APP_SHARED_PREFERENCE,
                        Context.MODE_PRIVATE
                );

        if (preferences.contains(key)) {
            return preferences.getBoolean(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public static void makeDeleteRequest(Context context, final String imagePath, String carId) {
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.CAR_ID, carId);
        params.put(Constants.IMAGE_NAME, imagePath);
        params.put(Constants.METHOD_LABEL, Constants.DELETE_PHOTO_METHOD);

        RetrofitRequest.photoRequest(params, new Callback<InventoryImageDeleteResponse>() {
            @Override
            public void success(InventoryImageDeleteResponse inventoryImageDeleteResponse, retrofit.client.Response response) {
                if (response != null && inventoryImageDeleteResponse.getResponses() != null && inventoryImageDeleteResponse.getResponses().size() > 0) {
                    //GCLog.e(response.toString());
                    GCLog.e("Dipanshu Delete Reques", params.toString());
                    for (GeneralResponse res : inventoryImageDeleteResponse.getResponses()) {
                        if ("T".equals(res.getStatus())) {
                            StockAddActivity.imagesList.remove(res.getMessage());
                        }
                    }
                } else {
                    GCLog.e("Response Null");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                GCLog.e("Photo Delete Error");
            }
        });

        /*DeletePhotoRequest photoRequest = new DeletePhotoRequest(
                context,
                Request.Method.POST, Constants.getWebServiceURL(context),
                new Response.Listener<InventoryImageDeleteResponse>() {
                    @Override
                    public void onResponse(InventoryImageDeleteResponse response) {

                        if (response != null && response.getResponses() != null && response.getResponses().size() > 0) {
                            //GCLog.e(response.toString());
                            for (GeneralResponse res : response.getResponses()) {
                                if ("T".equals(res.getStatus())) {
                                    StockAddActivity.imagesList.remove(res.getMessage());
                                }
                            }
                        } else {
                            GCLog.e("Response Null");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        GCLog.e("Photo Delete Error");
                    }
                }, params);
        ApplicationController.getInstance().addToRequestQueue(photoRequest);*/
    }

    public static double calculateLoanEmi(int principal, double roi, int noOfMonths) {
        double r = roi / 1200;
        double powerCoefficient = Math.pow(1 + r, noOfMonths);
        double emi = (principal * r * powerCoefficient) / (powerCoefficient - 1);
        return emi;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static int getIntSharedPreference(Context context, String key, int defaultValue) {
        SharedPreferences preferences = context.getApplicationContext()
                .getSharedPreferences(
                        Constants.APP_SHARED_PREFERENCE,
                        Context.MODE_PRIVATE
                );

        if (preferences.contains(key)) {
            return preferences.getInt(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public static void publishResult(Context context, String carId, String status, int count, int size, boolean start, String makeModelVersion) {
        try {
            Intent intent = new Intent();
            if ("T".equalsIgnoreCase(status)) {
                CommonUtils.createNotificationTile(
                        context,
                        Integer.parseInt(carId),
                        R.drawable.ic_launcher,
                        makeModelVersion, "Uploaded " + (count + 1) + " of " + size + " photos.",
                        size,
                        count,
                        intent, makeModelVersion);
            } else if ("C".equalsIgnoreCase(status)) {
                CommonUtils.createNotificationTile(
                        context,
                        Integer.parseInt(carId),
                        R.drawable.ic_launcher,
                        makeModelVersion, "All photos uploaded successfully.\nYour photos will be visible in 5-10 minutes.",
                        size,
                        0,
                        intent, makeModelVersion);
                StockAddActivity.sendOrderImages(context, carId);
            } else {
                if (size > 0) {
                    CommonUtils.createNotificationTile(
                            context,
                            Integer.parseInt(carId),
                            R.drawable.ic_launcher,
                            makeModelVersion, "Failed to upload " + (size - count) + " out of " + size + " photos",
                            size,
                            0,
                            intent, makeModelVersion);
                    StockAddActivity.sendOrderImages(context, carId);
                }
            }
        } catch (Exception e) {
            GCLog.e("Some exception occurred. " + e.getMessage());
        }
    }

    public static void setLongSharedPreference(Context context, String key, long value) {
        SharedPreferences preferences = context.getApplicationContext().
                getSharedPreferences(
                        Constants.APP_SHARED_PREFERENCE,
                        Context.MODE_PRIVATE
                );
        SharedPreferences.Editor editor = preferences.edit();
        if ((key != null) && !key.isEmpty()) {
            editor.putLong(key, value);
            editor.apply();
        }
    }

    public static ArrayList<String> getProofGroupMaps() {
        ArrayList<String> grorupLists = new ArrayList<>();
        grorupLists.add(0, Constants.ID_PROOFS_MEMBERS);
        grorupLists.add(1, Constants.ADDRESS_PROOFS_MEMBERS);
        grorupLists.add(2, Constants.PAN_CARD_MEMBERS);
        grorupLists.add(3, Constants.INCOME_PROOFS_MEMBERS);
        grorupLists.add(4, Constants.OTHERS_MEMBERS);
        return grorupLists;
    }

    public static long getLongSharedPreference(Context context, String key, long defaultValue) {
        SharedPreferences preferences = context.getApplicationContext().
                getSharedPreferences(
                        Constants.APP_SHARED_PREFERENCE,
                        Context.MODE_PRIVATE
                );

        if (preferences.contains(key)) {
            return preferences.getLong(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static void shakeView(Context context, View failedView) {
        Animation anim = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.shake);
        failedView.setAnimation(anim);
        failedView.startAnimation(anim);
    }

    //public static void translateViewUp(View view, )

    public static void showToast(Context context, String toastMessage, int toastDuration) {
        try {
            Toast.makeText(context, toastMessage, toastDuration).show();
        } catch (IllegalStateException e) {
            // No need to show toast
            // Added the changes in order to prevent Fabric 797
        }
    }

    public static boolean checkForPermission(final Context context, final String[] permissions,
                                             final int requestCode, final String requestFor) {
        final ArrayList<String> permissionNeededForList = checkSelfPermission(context, permissions);
        String requestsFor = permissionNeededForList.get(permissionNeededForList.size() - 1);
        permissionNeededForList.remove(permissionNeededForList.size()-1);
        if (permissionNeededForList.isEmpty()) {
            return true;
        }
        if (!requestsFor.isEmpty()) {
            Dialog dialog = new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.permission_error))
                    .setMessage(context.getString(R.string.you_need_to_allow_access_to,
                            new String[]{requestFor, requestFor}))
                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermission(context,
                                    permissionNeededForList.toArray(new String[permissionNeededForList.size()]),
                                    requestCode);
                        }
                    })
                    .setNegativeButton(context.getString(R.string.go_to_app_info), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                            intent.setData(uri);
                            ((Activity) context).startActivityForResult(intent, 10);
                        }
                    })
                    .create();

            if (!((Activity) context).isFinishing() && !dialog.isShowing()) {
                dialog.show();
            }
        }

        requestPermission(context,
                permissionNeededForList.toArray(new String[permissionNeededForList.size()]),
                requestCode);

        return false;
    }

    private static ArrayList<String> checkSelfPermission(Context context, String[] permissions) {
        ArrayList<String> list = new ArrayList<>();
        StringBuilder requestsFor = new StringBuilder();
        for (String permission: permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                list.add(permission);
                if (getBooleanSharedPreference(context, permission, false)) {
                // Check if permission has been called previously, true if called previously
                    if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                        if (requestsFor.length() > 0) {
                            requestsFor.append(", ");
                        }
                        requestsFor.append(permission.substring(permission.lastIndexOf(".") + 1));
                    }
                } else {
                    setBooleanSharedPreference(context, permission, true);
                }
            }
        }
        if (requestsFor.length() > 0)
            list.add(requestsFor.toString());
        else
            list.add("");
        return list;
    }

    private static void requestPermission(Context context, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions((Activity) context, permissions, requestCode);
    }

    public static void logoutUser(Context context) {
        /*SharedPreferences preferences = context.getSharedPreferences(Constants.APP_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        preferences.edit().clear().apply();*/
        CommonUtils.setBooleanSharedPreference(context, Constants.USER_LOGGEDIN, false);
        CommonUtils.setIntSharedPreference(context, Constants.UC_DEALER_ID, -1);
        CommonUtils.setStringSharedPreference(context, Constants.UC_DEALER_EMAIL, "");
        CommonUtils.setStringSharedPreference(context, Constants.UC_DEALER_MOBILE, "");
        CommonUtils.setStringSharedPreference(context, Constants.UC_DEALER_NAME, "");
        CommonUtils.setIntSharedPreference(context, Constants.UC_DEALER_NUM_INVENTORIES_TO_LIST, -1);
        CommonUtils.setStringSharedPreference(context, Constants.UC_DEALER_USERNAME, "");
        CommonUtils.setBooleanSharedPreference(context, Constants.SERVICE_EXECUTIVE_LOGIN, false);
        CommonUtils.setStringSharedPreference(context, Constants.SERVICE_EXECUTIVE_ID, "");
        CommonUtils.setStringSharedPreference(context, Constants.UC_DEALER_PASSWORD, "");
        CommonUtils.setStringSharedPreference(context, Constants.UC_WARRANTY_ONLY_DEALER, "");
        CommonUtils.setStringSharedPreference(context, Constants.UC_CARDEKHO_INVENTORY, "");
        CommonUtils.setStringSharedPreference(context, Constants.GAADI_GCM_ID, "");
        CommonUtils.setBooleanSharedPreference(context, Constants.RSA_DEALER, false);
        CommonUtils.setBooleanSharedPreference(context, Constants.INSURANCE_DEALER, false);
        CommonUtils.setBooleanSharedPreference(context, Constants.FINANCE_DEALER, false);
        CommonUtils.setIntSharedPreference(context, Constants.IS_SELLER, 0);
        CommonUtils.setIntSharedPreference(context, Constants.IS_LMS, 0);
        CommonUtils.removeSharedPreference(context, Constants.LMS_LAST_SYNCED_TIME);
        CommonUtils.removeSharedPreference(context, Constants.LEADS_LAST_SYNCED_TIME);
        CommonUtils.removeSharedPreference(context, Constants.INSURANCE_DASHBOARD_OFFLINE_DATA);
        CommonUtils.removeSharedPreference(context, Constants.SFA_USER_ID);
        CommonUtils.removeSharedPreference(context, Constants.STOCK_CHANGE_TIME);
        CommonUtils.removeSharedPreference(context, Constants.IS_ACTIVE_RUNNING_STOCK_SERVICE);
        CommonUtils.removeSharedPreference(context, Constants.IS_INACTIVE_RUNNING_STOCK_SERVICE);
        CommonUtils.removeSharedPreference(context, Constants.IS_ERROR_STOCK_SERVICE);
        DBFunction.clearStocksTable();
        /*//remove this before making live.
        CommonUtils.setBooleanSharedPreference(context, Constants.RE_SYNCED, false);*/
        ApplicationController.getStocksDB().deletePreviousData();
        ApplicationController.getManageLeadDB().getWritableDatabase().delete(ManageLeadDB.TABLE_NAME, null, null);
        ApplicationController.getLeadsOfflineDB().getWritableDatabase().delete(LeadsOfflineDB.TABLE_NAME, null, null);
        ApplicationController.getInsuranceDB().getWritableDatabase().delete(InsuranceDB.TABLE_IMAGES, null, null);
        ApplicationController.getMakeModelVersionDB().getWritableDatabase()
                .delete(MakeModelVersionDB.TABLE_NOTIFICATION, null, null);

    }

    public static void removeSharedPreference(Context context, String key) {
        SharedPreferences preferences = context.getApplicationContext()
                .getSharedPreferences(
                        Constants.APP_SHARED_PREFERENCE,
                        Context.MODE_PRIVATE
                );
        SharedPreferences.Editor editor = preferences.edit();

        if ((key != null) && !key.isEmpty()) {
            editor.remove(key);
            editor.apply();
        }
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static void scrollTo(final ScrollView scroll, final View view) {
        long time = 500;

        final long interval = 20;

        final int currY = scroll.getScrollY();
        final int viewYPos = view.getTop();
        final int diffHeight = currY - viewYPos;
        GCLog.e("" + viewYPos);

        GCLog.e("currY: " + currY + ", diffHeight: " + diffHeight);

        if (diffHeight > 0) { // scroll up

            GCLog.e("Scroll up");

            new CountDownTimer(time, interval) {

                int i = 0;

                int y = currY;

                int stepSize = getStepSize(diffHeight, interval);

                @Override
                public void onTick(long millisUntilFinished) {

                    if ((stepSize > 0) && (y > viewYPos)) {

                        y -= ++i * stepSize;

                        scroll.scrollTo(0, y);
                    } else {

                        scroll.scrollTo(0, viewYPos);
                    }

                }

                @Override
                public void onFinish() {
                    i = 0;
                    // view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                    // R.anim.zoom_out));

                }
            }.start();
        } else { // scroll down

            GCLog.e("scroll down");

            new CountDownTimer(time, interval) {

                int i = 0;

                int y = currY;

                int stepSize = getStepSize(-diffHeight, interval);

                @Override
                public void onTick(long millisUntilFinished) {

                    if ((stepSize > 0) && (y < viewYPos)) {

                        y += ++i * stepSize;

                        scroll.scrollTo(0, y);
                    } else {

                        scroll.scrollTo(0, viewYPos);

                    }

                }

                @Override
                public void onFinish() {
                    i = 0;

                }
            }.start();
        }
    }

    public static void uploadPendingImages(Context context) {
        GCLog.e("UploadPendingImages");
        Intent intent = new Intent(context, PendingImagesService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), BuildConfig.RETRY_IMAGES_DURATION * 1000, pendingIntent);
    }

    public static long SQLTimeToMillis(String mySQLTime) {
        //GCLog.e("server time: "+mySQLTime);
        if (mySQLTime == null || mySQLTime.isEmpty() || ("0000-00-00 00:00:00").equals(mySQLTime)) {
            return 0;
        }
        try {
            String[] dateTime = mySQLTime.split(" ");
            if (dateTime.length > 1) {
                String[] times = dateTime[1].split(":");
                if (times.length < 3) {
                    mySQLTime += ":00";
                }
            }
        } catch (Exception e) {
            return 0;
        }
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-d H:m:s");
        try {
            DateTime dateTime = DateTime.parse(mySQLTime, formatter);
            return dateTime.getMillis();
        } catch (org.joda.time.IllegalFieldValueException exception) {
            exception.printStackTrace();
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String MillisToSQLTime(long millis) {
        DateTime dateTime = new DateTime(millis);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-d H:m:s");
        return dateTime.toString(formatter);
    }

    protected static int getStepSize(int diffHeight, long interval) {
        return (int) (diffHeight / interval);
    }

    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void uploadImages(Context context, ArrayList<StockImageData> imageData) {

        HashSet<String> carIdsToUploadPhotosFor = new HashSet<>();
        int pendingSuccessfullUploadCount = 0;
        String makeModelVersion = "";
        Intent intent = new Intent();
        if (imageData != null && imageData.size() > 0) {


//            Boolean pendingImageUploadSuccessfull = false;

            Boolean differentDealerId = false;

            String carId = "";

//            makeModelVersion = imageData.get(0).getMakeModelVersion();
//            carId = stockImageData.getCarId();
//            int notId = Integer.parseInt(carId);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancelAll();


            for (int i = 0; i < imageData.size(); i++) {

                StockImageData stockImageData = imageData.get(i);
                GCLog.e("Stock Object Dealer Id : " + stockImageData.getDealerId() + "");
                GCLog.e("" + "Prefs Dealer Id : " + CommonUtils.getIntSharedPreference(context, Constants.UC_DEALER_ID, -1) + "");
                if (stockImageData.getDealerId() != CommonUtils.getIntSharedPreference(context, Constants.UC_DEALER_ID, -1) && !CommonUtils.getBooleanSharedPreference(context, Constants.SERVICE_EXECUTIVE_LOGIN, false)) {
                    continue;
                }
                if (i == 0)
                    CommonUtils.createNotificationTile(context, Constants.RESUME_UPLOAD_NOTIF, R.drawable.ic_launcher, Constants.RESUME_NOTIF_NAME, "Resuming photo upload", imageData.size(), 0, intent, Constants.RESUME_NOTIF_NAME);

                differentDealerId = true;

                GCLog.e(" " + stockImageData.toString());
                String imagePath = stockImageData.getImagePath();

                try {
                    GCLog.e("image path : " + imagePath);
                  /*  HttpClient httpClient = new DefaultHttpClient();
                    HttpContext httpContext = new BasicHttpContext();
                    HttpPost httpPost = new HttpPost(Constants.getImageUploadURL(context));
                    MultipartEntity multipartContent = new MultipartEntity();
                    FileBody fileBody = new FileBody(new File(stockImageData.getImagePath()));
                    multipartContent.addPart("source", new StringBody("Android GCloud App"));*/
//                    String extension = imagePath.substring(imagePath.lastIndexOf("."));
//                    String name = "used_car_" + i + "_" + carId + extension;
                    String name = stockImageData.getRequestImageName();
                    //StringBody fileName = new StringBody(name);
                    carId = stockImageData.getCarId();

                    carIdsToUploadPhotosFor.add(carId);
                    CommonUtils.setBooleanSharedPreference(context, ImageUploadService.SERVICE_RUNNING + carId, true);
                    StockImagesDB imagesDB = new StockImagesDB(context);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("file_name", name);
                    params.put("car_id", carId);

                  /*  multipartContent.addPart("file_name", fileName); // ,"image/jpeg"
                    multipartContent.addPart("stockImg", fileBody);
                    multipartContent.addPart("car_id", new StringBody(carId));
                    multipartContent.addPart(Constants.APP_NAME, new StringBody(CommonUtils.getStringSharedPreference(context, Constants.APP_PACKAGE_NAME, "")));
                    multipartContent.addPart(Constants.OWNER, new StringBody(BuildConfig.CLOUD_OWNER));
                    multipartContent.addPart(Constants.SERVICE_EXECUTIVE_ID, new StringBody(CommonUtils.getStringSharedPreference(context, Constants.SERVICE_EXECUTIVE_ID, "")));
                    multipartContent.addPart(Constants.API_KEY_LABEL, new StringBody(Constants.API_KEY));
                    multipartContent.addPart(Constants.ANDROID_ID, new StringBody(CommonUtils.getStringSharedPreference(context, Constants.ANDROID_ID, "")));
                    multipartContent.addPart(Constants.UCDID,
                            new StringBody(String.valueOf(CommonUtils.getIntSharedPreference(context, Constants.UC_DEALER_ID, -1))));
                    multipartContent.addPart(Constants.DEALER_USERNAME,
                            new StringBody(CommonUtils.getStringSharedPreference(context, Constants.UC_DEALER_USERNAME, "")));
                    multipartContent.addPart(Constants.SERVICE_EXECUTIVE_LOGIN, new StringBody(String.valueOf(CommonUtils.getBooleanSharedPreference(context, Constants.SERVICE_EXECUTIVE_LOGIN, false))));
                    multipartContent.addPart(Constants.APP_VERSION, new StringBody(CommonUtils.getStringSharedPreference(context, Constants.APP_VERSION_CODE, "")));

                    GCLog.e(multipartContent.toString());
                    GCLog.e(new StringBody(String.valueOf(CommonUtils.getIntSharedPreference(context, Constants.UC_DEALER_ID, -1))) + "," + new StringBody(CommonUtils.getStringSharedPreference(context, Constants.UC_DEALER_USERNAME, "")) + "," + new StringBody(String.valueOf(CommonUtils.getBooleanSharedPreference(context, Constants.SERVICE_EXECUTIVE_LOGIN, false))) + "," + new StringBody(CommonUtils.getStringSharedPreference(context, Constants.APP_VERSION_CODE, "")));
                    httpPost.setEntity(multipartContent);
                    HttpResponse response = httpClient.execute(httpPost, httpContext);
                    String responses = EntityUtils.toString(response.getEntity());*/
//                    CommonUtils.publishResult(context, notificationId+"", "T", i, imageData.size(), false, Constants.RESUME_NOTIF_NAME);
                    ImageUploadResponse response = RetrofitRequest.stockImagesUploadRequest(stockImageData.getImagePath(),
                            params);
                    if (response.getStatus().equalsIgnoreCase("T")) {
                       /* String requestImageName = object.getString(ImageUploadService.REQUEST_IMAGE_NAME);
                        String responseImageName = object.getString(ImageUploadService.RESPONSE_IMAGE_NAME);*/
                        String requestImageName = response.getRequest_image_name();
                        String responseImageName = response.getResponse_image_name();
                        //Update Response name corresponding to request image name
                        imagesDB.updateStockImageData(requestImageName, responseImageName, "T", carId);

                        //update the new order by replacing request name to response name
                        StockImageOrderData order = imagesDB.getStockImageOrderDataById(carId);
                        String oldOrder = order.getImageUploadOrder();
                        String latestOrder = oldOrder.replace(requestImageName, responseImageName);
                        imagesDB.updateStockImageOrderData(latestOrder, carId);

                        ++pendingSuccessfullUploadCount;
                        int notificationId = Constants.RESUME_UPLOAD_NOTIF;
                        CommonUtils.createNotificationTile(context, notificationId, R.drawable.ic_launcher, Constants.RESUME_NOTIF_NAME, "Uploaded " + pendingSuccessfullUploadCount + " of " + imageData.size() + " photos", imageData.size(), pendingSuccessfullUploadCount, intent, Constants.RESUME_NOTIF_NAME);
//                        publishImageResult("T", i, imageData.size(), false, makeModelVersion);
                    }


                } catch (Exception e) {
                    GCLog.e(e.getMessage());
                }
            }
            if (pendingSuccessfullUploadCount == imageData.size() && pendingSuccessfullUploadCount != 0) {
                CommonUtils.createNotificationTile(context, Constants.RESUME_UPLOAD_NOTIF, R.drawable.ic_launcher, Constants.RESUME_NOTIF_NAME, "Successfully uploaded all photos", imageData.size(), pendingSuccessfullUploadCount, intent, Constants.RESUME_NOTIF_NAME);
                for (String singleCarId : carIdsToUploadPhotosFor) {
                    StockAddActivity.sendOrderImages(context, singleCarId);
                    CommonUtils.setBooleanSharedPreference(context, ImageUploadService.SERVICE_RUNNING + singleCarId, false);
                }
            } else {
                int countFailed = imageData.size() - pendingSuccessfullUploadCount;
                if (differentDealerId)
                    CommonUtils.createNotificationTile(context, Constants.RESUME_UPLOAD_NOTIF, R.drawable.ic_launcher, Constants.RESUME_NOTIF_NAME, "Failed to upload " + countFailed + " photos", imageData.size(), pendingSuccessfullUploadCount, intent, Constants.RESUME_NOTIF_NAME);
                else {
                    GCLog.e("Different dealer Id's photos pending ");
                }
                for (String singleCarId : carIdsToUploadPhotosFor) {
                    CommonUtils.setBooleanSharedPreference(context, ImageUploadService.SERVICE_RUNNING + singleCarId, false);
                }
            }
            CommonUtils.setBooleanSharedPreference(context, PendingImagesService.PENDING_IMAGES_SERVICE, false);
        }
    }

    /*public static void generateNotificationWithoutVibrate(Context context , int notificationId, Uri uri , String messageBody)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.chat_notification_icon)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(messageBody)
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
    }*/

    public static void generateNotificationWithoutVibrate(Context context, int notificationId,
                                                          Uri uri, String messageBody,
                                                          int notificationType, Bundle extras) {
        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.gcloud_logo)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setAutoCancel(true);

        //mBuilder.setSound(uri);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);

        Intent notificationIntent;

        if (CommonUtils.getBooleanSharedPreference(context, Constants.USER_LOGGEDIN, false)) {
            GCLog.e("notification type: " + notificationType);
            switch (notificationType) {
                case 1: // Leads notification
                    notificationIntent = new Intent(context, LeadsManageActivity.class);
                    break;

                default:
                    notificationIntent = new Intent(context, SplashActivity.class);
                    break;
            }
        } else {

            notificationIntent = new Intent(context, SplashActivity.class);
        }

        notificationIntent.putExtras(extras);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pendingIntent);


        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(notificationId, mBuilder.build());

    }

    public static void createNotificationTile(Context context,
                                              int imageUploadNotifId,
                                              int drawable,
                                              String title,
                                              String content,
                                              int total,
                                              int count,
                                              Intent resultIntent, String makeModelVersion) {

        NotificationCompat.Builder mBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder = new NotificationCompat.Builder(context)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVibrate(new long[0])
                    .setSmallIcon(drawable)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                    .setTicker(makeModelVersion)
                    .setContentText(content).setAutoCancel(true);

        } else {
            mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(drawable)
                    .setContentTitle(title)
                    .setTicker(makeModelVersion)
                    .setContentText(content)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                    .setAutoCancel(true);
        }

        try {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            // Adds the back stack for the Intent (but not the Intent itself)
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // mId allows you to update the notification later on.
            mNotificationManager.notify(imageUploadNotifId, mBuilder.build());
        } catch (Exception e) {
            mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(drawable)
                    .setContentTitle(title)
                    .setTicker(makeModelVersion)
                    .setContentText(content).setAutoCancel(true);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            // Adds the back stack for the Intent (but not the Intent itself)
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // mId allows you to update the notification later on.
            mNotificationManager.notify(imageUploadNotifId, mBuilder.build());
        }


    }

    public static void createImageUploadNotification(Context context,
                                                     String title,
                                                     String applicationId,
                                                     String message,
                                                     boolean progress,
                                                     PendingIntent pendingIntent,
                                                     String subText) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(title + applicationId)
                .setContentText(message)
                .setProgress(0, 0, progress)
                .setAutoCancel(!progress)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
//                .setOngoing(progress)
                .setTicker(message);

        if (pendingIntent != null)
            mBuilder.setContentIntent(pendingIntent);

        if (!progress && subText != null)
            mBuilder.setSubText(subText);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setColor(context.getResources().getColor(R.color.gaadi_blue));
        } else {
            mBuilder.setSmallIcon(R.drawable.ic_launcher);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Integer.valueOf(applicationId), mBuilder.build());
    }

   /* public static String getUnzippedResponseString(NetworkResponse networkResponse) throws IOException {
        StringBuilder output = new StringBuilder();
        String encoding = networkResponse.headers.get("Content-Encoding");
        if (encoding != null && encoding.equals("gzip")) {
            //GCLog.e(Constants.TAG, "gzipped response");
            GZIPInputStream gStream = new GZIPInputStream(new ByteArrayInputStream(networkResponse.data));
            InputStreamReader reader = new InputStreamReader(gStream);
            //GCLog.e(Constants.TAG, "response gzipped size: " + networkResponse.data.length);
            BufferedReader in = new BufferedReader(reader, 16384);
            String read;

            while ((read = in.readLine()) != null) {
                output.append(read);
            }

            //GCLog.e(Constants.TAG, "response read: " + output);
            reader.close();
            in.close();
            gStream.close();
            return new String(output.toString().getBytes(), HttpHeaderParser.parseCharset(networkResponse.headers));
        } else {
            //GCLog.e(Constants.TAG, "Response unzipped size: " + networkResponse.data.length);
            return new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
        }
    *//*return new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));*//*
    }*/

    public static boolean writeToStorage(Context context, Drawable drawable, String imageName) {
        try {
            OutputStream fOut = null;
            File file = new File(Environment.getExternalStorageDirectory(), "/Gaadi Gcloud");
            if (!file.exists())
                if (!file.mkdirs())
                    return false;

            Bitmap bitmap = ((GlideBitmapDrawable) drawable).getBitmap();
            file = new File(Environment.getExternalStorageDirectory(), "/Gaadi Gcloud/" + imageName + ".png");
            file.createNewFile();
            GCLog.e("path for share png: " + file.getAbsolutePath());
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
            return true;
        } catch (OutOfMemoryError | Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static JSONObject mergedJSONObjects(JSONObject... jsonObjects) throws JSONException {
        JSONObject mergedJSON = new JSONObject();
        for (JSONObject obj : jsonObjects) {
            Iterator it = obj.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                mergedJSON.put(key, obj.get(key));
            }
        }

        return mergedJSON;
    }

    public static void showErrorToast(Context context, RetrofitError retrofitError, int duration) {
//        GAHelper gaHelper = new GAHelper(context);
        ApplicationController.getInstance().getGAHelper().sendEvent(
                GAHelper.TrackerName.APP_TRACKER,
                Constants.CATEGORY_ERROR,
                Constants.CATEGORY_ERROR,
                retrofitError.getKind().name(),
                retrofitError.getMessage(),
                0
        );

        //GCLog.e(retrofitError.toString());
        if (context == null)  {
            context = ApplicationController.getInstance();
        }
        switch (retrofitError.getKind()) {
            case NETWORK: // all network error
                Toast.makeText(context, context.getString(R.string.network_connection_error), duration).show();
                break;

            case CONVERSION: // unable to parse json data (invalid deserialization)
                Toast.makeText(context, context.getString(R.string.application_error), duration).show();
                break;

            case UNEXPECTED: // An internal error occurred while attempting to execute a request
                Toast.makeText(context, context.getString(R.string.server_error), duration).show();
                break;

            case HTTP: // A non-200 HTTP status code was received from the server
                Toast.makeText(context, context.getString(R.string.server_error), duration).show();
                break;
        }
    }

    public static String getErrorMessage(Context context, RetrofitError retrofitError) {
//        GAHelper gaHelper = new GAHelper(context);
        ApplicationController.getInstance().getGAHelper().sendEvent(
                GAHelper.TrackerName.APP_TRACKER,
                Constants.CATEGORY_ERROR,
                Constants.CATEGORY_ERROR,
                retrofitError.getKind().name(),
                retrofitError.getMessage(),
                0
        );

        if(context == null){
            context = ApplicationController.getInstance();
        }

        GCLog.e(retrofitError.toString());
        switch (retrofitError.getKind()) {
            case NETWORK: // all network error
                return context.getString(R.string.network_connection_error);

            case CONVERSION: // unable to parse json data (invalid deserialization)
                return context.getString(R.string.application_error);

            case UNEXPECTED: // An internal error occurred while attempting to execute a request
                return  context.getString(R.string.server_error);

            case HTTP: // A non-200 HTTP status code was received from the server
                return context.getString(R.string.server_error);

            default:
                return "";
        }
    }

    public static String getReplacementString(Context context, int resId, String... replacement) {
        return context.getString(resId, replacement);
    }

    //To be called for activity transition. Use Constants.TRANSITION_LEFT to launch an activity and Constants.TRANSITION_RIGHT to be back from an activity.
    public static void startActivityTransition(Activity activity, String direction) {
        if (direction.equals(Constants.TRANSITION_LEFT)) {
            activity.overridePendingTransition(R.anim.activity_in_left, R.anim.activity_out_left);
            return;
        }

        if (direction.equals(Constants.TRANSITION_RIIGHT)) {
            activity.overridePendingTransition(R.anim.activity_in_right, R.anim.activity_out_right);
        }
    }

    public static void logRSAEvent(String stockId, String screenSource, String eventLabel) throws Exception {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.METHOD_LABEL, Constants.RSA_TRACK_METHOD);
        jsonObject.put("screen_source", screenSource);
        jsonObject.put("rsa_event", eventLabel);
        jsonObject.put("stock_id", stockId);

        RetrofitRequest.logRSAEvent(jsonObject, new Callback<RSALogEventModel>() {
            @Override
            public void success(RSALogEventModel rsaLogEventModel, retrofit.client.Response response) {


            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public static ArrayList<String> getFinanceEmploymentType() {
        ArrayList<String> employmentType = new ArrayList<>();
        employmentType.add("--Select--");
        employmentType.add(Constants.FINANCE_EMP_TYPE_SALARIED);
        employmentType.add(Constants.FINANCE_EMP_TYPE_BUSINESS);
        employmentType.add(Constants.FINANCE_EMP_TYPE_PROFESS);
        employmentType.add(Constants.FINANCE_EMP_TYPE_OTHERS);
        return employmentType;
    }

    public static ArrayList<String> getSalaryCreditType() {
        ArrayList<String> employmentType = new ArrayList<>();
        employmentType.add("--Select--");
        employmentType.add("Direct Deposit");
        employmentType.add("Cash");
        employmentType.add("Cheque");

        return employmentType;
    }

    public static ArrayList<String> getOfficeStUpType() {
        ArrayList<String> officeSetUpType = new ArrayList<>();
        officeSetUpType.add("--Select--");
        officeSetUpType.add("Resi-cum Office");
        officeSetUpType.add("Own Office");
        officeSetUpType.add("Out of Client Site");

        return officeSetUpType;
    }

    public static HashMap<String, Integer> getFinanceReviewIconsMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put(Constants.FINANCE_APPLICATION_FORM, R.drawable.application_form);
        map.put(Constants.ADDRESS_PROOFS, R.drawable.location);
        map.put(Constants.INCOME_PROOFS, R.drawable.money);
        map.put(Constants.OTHER_DOCS, R.drawable.otherdoc);
        map.put(Constants.PAN_CARD, R.drawable.idproof);
        map.put(Constants.ID_PROOFS, R.drawable.idproof);
        map.put(Constants.RC_COPY, R.drawable.idproof);
        map.put(Constants.RTO_DOCUMENTS, R.drawable.otherdoc);
        map.put(Constants.SUPPORTING_DOCUMENTS, R.drawable.otherdoc);
        return map;
    }


    public static ArrayList<String> getResidenceType() {
        ArrayList<String> residenceType = new ArrayList<>();
        residenceType.add("--Select--");
        residenceType.add(Constants.FINANCE_RESIDENCE_TYPE_SELF);
        residenceType.add(Constants.FINANCE_RESIDENCE_TYPE_PARENTS);
        residenceType.add(Constants.FINANCE_RESIDENCE_TYPE_FAMILY);
        residenceType.add(Constants.FINANCE_RESIDENCE_TYPE_FRIENDS);
        residenceType.add(Constants.FINANCE_RESIDENCE_TYPE_ALONE);
        residenceType.add(Constants.FINANCE_RESIDENCE_TYPE_GUEST);
        residenceType.add(Constants.FINANCE_RESIDENCE_TYPE_HOSTEL);
        residenceType.add(Constants.FINANCE_RESIDENCE_TYPE_GOV);
        return residenceType;
    }


    public static ArrayList<String> getEmploymentStatusType() {
        ArrayList<String> employmentStatusType = new ArrayList<>();
        employmentStatusType.add("--Select--");
        employmentStatusType.add(Constants.FINANCE_EMP_STATUS_TYPE1);
        employmentStatusType.add(Constants.FINANCE_EMP_STATUS_TYPE2);
        employmentStatusType.add(Constants.FINANCE_EMP_STATUS_TYPE3);
        employmentStatusType.add(Constants.FINANCE_EMP_STATUS_TYPE4);
        return employmentStatusType;
    }

    public static boolean canLog() {
        return !"PROD".equalsIgnoreCase(BuildConfig.ENVIRONMENT);
    }

    public static ArrayList<String> getImagesFromInfos(ArrayList<FileInfo> infos) {
        ArrayList<String> images = new ArrayList<>();
        for (FileInfo info: infos){
            images.add(info.getFilePath());
        }
        return images;
    }

    public static String getNetworkType() {
        ConnectivityManager cm = (ConnectivityManager) ApplicationController.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info==null || !info.isConnected())
            return "-"; //not connected
        if(info.getType() == ConnectivityManager.TYPE_WIFI)
            return "WIFI";
        if(info.getType() == ConnectivityManager.TYPE_MOBILE){
            int networkType = info.getSubtype();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                    return "4G";
                default:
                    return "?";
            }
        }
        return "?";
    }

    public static void insertCommaIntoNumber(TextView etText, String s, String format) {
        try {
            if (s.length() > 0) {
                String convertedStr = s;
                if (s.contains(".")) {
                    if (chkConvert(s))
                        convertedStr = customFormat(format, Double.parseDouble(s.replace(",", "")));
                } else {
                    convertedStr = customFormat(format, Double.parseDouble(s.replace(",", "")));
                }


                if (!etText.getText().toString().equals(convertedStr) && convertedStr.length() > 0) {
                    etText.setText(CommonUtils.getReplacementString(ApplicationController.getInstance(), R.string.inr, convertedStr));
                    //  TextView.setSelection(etText.getText().length());
                }
            }

        } catch (Exception e) {
            Crashlytics.logException(e.getCause());
            //e.printStackTrace();
        }
    }

    public static String convertCommaIntoNumber(String s, String format) {
        String convertedStr = s;
        try {
            if (s.length() > 0) {

                if (s.contains(".")) {
                    if (chkConvert(s))
                        convertedStr = customFormat(format, Double.parseDouble(s.replace(",", "")));
                } else {
                    convertedStr = customFormat(format, Double.parseDouble(s.replace(",", "")));
                }

            }

        } catch (Exception e) {
            Crashlytics.logException(e.getCause());
            //e.printStackTrace();
        }

        return convertedStr;
    }

    private static boolean chkConvert(String s) {
        String tempArray[] = s.split("\\.");
        if (tempArray.length > 1) {
            return Integer.parseInt(tempArray[1]) > 0;
        } else
            return false;
    }

    private static String customFormat(String pattern, double value) {
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        return myFormatter.format(value);

    }


    public static void insertCommaIntoNumber(EditText etText, CharSequence s, String format) {
        try {
            if (s.toString().length() > 0) {
                String convertedStr = s.toString();
                if (s.toString().contains(".")) {
                    if (chkConvert(s.toString()))
                        convertedStr = customFormat(format, Double.parseDouble(s.toString().replace(",", "")));
                } else {
                    convertedStr = customFormat(format, Double.parseDouble(s.toString().replace(",", "")));
                }
                if (!etText.getText().toString().equals(convertedStr) && convertedStr.length() > 0) {
                    etText.setText(convertedStr);
                    etText.setSelection(etText.getText().length());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String compressImage(String path,int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilies.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilies.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilies.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilies.ScalingLogic.FIT);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            // Store to tmp file

            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/TMMFOLDER");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

            String s = "tmp.png";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }
    public static ArrayList<Integer> optimisefilterInString( String str,int multiple) {
        ArrayList<Integer> data = new ArrayList<>();
        for (String retval : str.split(",")) {
            String[] parts = retval.split("-");
            int index = retval.indexOf("-");
            data.add(Integer.parseInt(retval.substring(0,index))*multiple);
            int a = Integer.parseInt(retval.substring(index + 1, retval.length()));
            if(a == 0){
                data.add(5000*multiple);
            }else {
                data.add(a*multiple);
            }
        }
        Collections.sort(data);
        return data;
    }


    public static String addSingleQuotesInString( String str) {
        List<String> elephantList = Arrays.asList(str.split(","));
        String newString = "";
        for (int i = 0; i < elephantList.size(); i++) {
            newString += "'" + elephantList.get(i) + "',";
        }
        return newString.substring(0, newString.length() - 1);
    }

//    //To be called while launching an activity that should get in to screen from left and current activity will shift right (Mostly from on back pressed).
//    public static void moveActivityRightAnim(Activity activity) {
//        activity.overridePendingTransition(R.anim.activity_in_right, R.anim.activity_out_right);
//    }

}
