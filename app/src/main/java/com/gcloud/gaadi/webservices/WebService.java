package com.gcloud.gaadi.webservices;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;

public class WebService {
    public static String RECORDS = "http://api.gaadi.com/app/service.php?apikey=U3KqyrewdMuCotTS&method=makemodelversion&output=json";
    public static String LOGIN = "http://api.gaadi.com/app/service.php";
    public static String WEBSERVICE_STOCK = "http://api.gaadi.com/app/service.php";
    public static String WEBSERVICE_DEALER_PLATEFORM = "";
    public static String JSON_RETURN_TYPE_ARRAY = "ARRAY";
    public static String JSON_RETURN_TYPE_OBJECT = "OBJECT";
    private static Activity activity;
    private static FragmentActivity fragmentActivity;


    public static Boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            AlertDialog.Builder alertD = new AlertDialog.Builder(context);
            alertD.setTitle("Warning");
            alertD.setMessage("Please enable data connection to continue");
            alertD.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    //   Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                }
            });

            // Showing Alert Message
            AlertDialog ad = alertD.create();
            if (context instanceof Activity) {
                activity = (Activity) context;
                if (!activity.isFinishing() && !ad.isShowing()) {
                    ad.show();
                }

            } else if (context instanceof FragmentActivity) {
                fragmentActivity = (FragmentActivity) context;
                if (!fragmentActivity.isFinishing() && !ad.isShowing()) {
                    ad.show();
                }

            }

            return false;
        }

    }

    public static boolean checkNetworkConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }

}


