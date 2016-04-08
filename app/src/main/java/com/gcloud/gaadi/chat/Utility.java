package com.gcloud.gaadi.chat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utility {
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                return true;
            } else if (connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) {
                return true;
            } else
                return false;
        } catch (Exception e) {
            return false;
        }

    }

    enum CHATLIST_MESSAGE_TYPE {
        CONVERSATION_LIST,
        DEALER_CHAT,
        EXPERT_CHAT
    }
}
