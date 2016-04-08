package com.gcloud.gaadi.sfa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.CampaignTrackingReceiver;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by vinodtakhar on 12/1/16.
 */
public class SfaCampaignTrackingReceiver extends BroadcastReceiver {
    public static final String ACTION_GCLOUD_LOGIN="com.gcloud.gaadi.ACTION_SFA_LOGIN";
    public static final String EXTRA_LOGIN_TOKEN="login_token";
    public static final String EXTRA_ACCESS_RIGHTS = "extra_access_rights";

    public static final String UTM_CAMPAIGN = "utm_campaign";
    public static final String UTM_SOURCE = "utm_source";
    public static final String UTM_MEDIUM = "utm_medium";
    public static final String UTM_TERM = "utm_term";
    public static final String UTM_CONTENT = "utm_content";

    public static Map<String, String> getHashMapFromQuery(String query)
            throws UnsupportedEncodingException {

        Map<String, String> query_pairs = new LinkedHashMap<String, String>();

        query = URLDecoder.decode(query, "UTF-8");

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                    URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Pass the intent to other receivers.
        String referrerString =  intent.getStringExtra("referrer");

        try {
            Map<String, String> getParams = getHashMapFromQuery(referrerString);

            //get the login token and send
            Intent sfaIntent=new Intent(ACTION_GCLOUD_LOGIN);
            Bundle bundle=new Bundle();
            bundle.putString(EXTRA_LOGIN_TOKEN,getParams.get(UTM_CONTENT));
            bundle.putString(EXTRA_ACCESS_RIGHTS, getParams.get(UTM_TERM));
            sfaIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(sfaIntent);

        } catch (UnsupportedEncodingException e) {
            if (e != null) {
                Crashlytics.logException(e.getCause());
            }
        }

        // When you're done, pass the intent to the Google Analytics receiver.
        new CampaignTrackingReceiver().onReceive(context, intent);
    }
}
