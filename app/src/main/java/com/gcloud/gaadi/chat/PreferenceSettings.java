package com.gcloud.gaadi.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

public class PreferenceSettings {
    private static SharedPreferences myPrefs;
    private static SharedPreferences.Editor prefsEditor;

    public static void openDataBase(Context context) {
        context = context.getApplicationContext();

        if (myPrefs == null) {
            try {
                myPrefs = context.getSharedPreferences("GaadiChatAppPrefsData", Build.VERSION.SDK_INT >= 11 ? 4 : Context.MODE_PRIVATE);
                prefsEditor = myPrefs.edit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void forceOpen(Context context) {
        if (myPrefs == null)
            openDataBase(context);
        else
            synchronized (myPrefs) {
                myPrefs = null;
                openDataBase(context);
            }
    }

    public static void save() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
            prefsEditor.apply();
        else
            prefsEditor.commit();
    }

    public static void reset() {
        prefsEditor.clear();
        prefsEditor.commit();
    }

    public static String getGCM() {
        String i = myPrefs.getString("KEY_GCM_KEYWORD", null);
        return i;
    }

    public static void setGCM(String j) {
        prefsEditor.putString("KEY_GCM_KEYWORD", j);
        save();
    }

    public static boolean getIsLogout() {
        boolean i = myPrefs.getBoolean("KEY_Is_Logout", false);
        return i;
    }

    public static void setIsLogout(boolean j) {
        prefsEditor.putBoolean("KEY_Is_Logout", j);
        save();

    }

    public static String getList() {
        String i = myPrefs.getString("KEY_List", null);
        return i;
    }

    public static void setList(String j) {
        prefsEditor.putString("KEY_List", j);
        save();
    }

    public static String getDeviceId() {
        String i = myPrefs.getString("KEY_Device_Id", null);
        return i;
    }

    public static void setDeviceId(String j) {
        prefsEditor.putString("KEY_Device_Id", j);
        save();
    }

    public static String getDealerId() {
        String i = myPrefs.getString("KEY_Dealer_Id", "");
        return i;
    }

    public static void setDealerId(String j) {
        prefsEditor.putString("KEY_Dealer_Id", j);
        save();
    }

    public static String getDealerEmailId() {
        String i = myPrefs.getString("KEY_Dealer_Email_Id", null);
        return i;
    }

    public static void setDealerEmailId(String j) {
        prefsEditor.putString("KEY_Dealer_Email_Id", j);
        save();
    }

    public static void ClearAll() {
        prefsEditor.clear();
        save();
    }
}
