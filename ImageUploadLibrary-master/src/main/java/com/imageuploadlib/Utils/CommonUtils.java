package com.imageuploadlib.Utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.imageuploadlib.R;

import java.util.ArrayList;

/**
 * Created by Lakshay on 17-03-2015.
 */
public class CommonUtils {

    public static void createNotification(Context context, int smallIcon, String title, String content, Intent resultIntent, int imageUploadNotifId) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(smallIcon)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentText(content);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(imageUploadNotifId, mBuilder.build());
    }

    public static String getStringSharedPreference(Context context, String key, String defaultValue) {
        SharedPreferences preferences = context.getApplicationContext().
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

    public static ArrayList<FileInfo> removeFileInfo(ArrayList<FileInfo> source, FileInfo fileInfo) {
        for (FileInfo fileInfo1 : source) {
            if (fileInfo.getFilePath().equals(fileInfo1.getFilePath())) {
                source.remove(fileInfo);
                break;
            }
        }
        return source;
    }

    public static void removeFileInfo(ArrayList<String> source, ArrayList<FileInfo> removeFiles, Boolean flag) {
        if (source == null)
            return;
        for (FileInfo fileInfo : removeFiles) {
            if (source.contains(fileInfo.getFilePath())) {
                source.remove(fileInfo.getFilePath());
            }
        }
    }

    public static void addFileInfo(ArrayList<FileInfo> source, FileInfo fileInfo) {
        Boolean alreadyPresent = false;
        for (FileInfo fileInfo1 : source) {
            if (fileInfo.getFilePath().equals(fileInfo1.getFilePath())) {
                alreadyPresent = true;
                break;
            }
        }
        if (!alreadyPresent)
            source.add(fileInfo);
    }

    public static void removeFileInfo(ArrayList<FileInfo> source, String filePath) {
        for (FileInfo fileInfo : source) {
            if (filePath.equals(fileInfo.getFilePath())) {
                source.remove(fileInfo);
                break;
            }
        }
    }

    public static void removeFileInfo(ArrayList<FileInfo> source, ArrayList<FileInfo> fileInfos) {
        ArrayList<FileInfo> toBeDeleted = new ArrayList<>();
        for (FileInfo fileInfo : source) {
            for (FileInfo fileInfo1 : fileInfos) {
                if (fileInfo1.getFilePath().equals(fileInfo.getFilePath())) {
                    toBeDeleted.add(fileInfo);
                    break;
                }
            }
        }
        source.removeAll(toBeDeleted);
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
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


    public static Uri getImageStoreUri() {
        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
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

    public static boolean checkForPermission(final Context context, final String[] permissions,
                                             final int requestCode, final String requestFor) {
        final ArrayList<String> permissionNeededForList = checkSelfPermission(context, permissions);
        String requestsFor = permissionNeededForList.get(permissionNeededForList.size() - 1);
        permissionNeededForList.remove(permissionNeededForList.size()-1);
        if (permissionNeededForList.isEmpty()) {
            return true;
        }
        if (!requestsFor.isEmpty()) {
            new AlertDialog.Builder(context)
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
                    .create().show();
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
}
