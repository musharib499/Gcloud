package com.gcloud.gaadi.utils;

import android.content.Context;

import com.gcloud.gaadi.BuildConfig;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

/**
 * Created by ankit on 17/11/14.
 */
public class GAHelper {

    public static final String PROPERTY_ID = BuildConfig.GA_PROPERTY_ID;

    // The following line should be changed to include the correct property id.
    // Dispatch period in seconds.
    private static final int GA_DISPATCH_PERIOD = 30;
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
    private Context mContext;

    public GAHelper(Context context) {
        super();
        mContext = context;
    }

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(mContext);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
                    : analytics.newTracker(R.xml.ecommerce_tracker);
            t.enableAutoActivityTracking(false);
            t.enableExceptionReporting(true);
            t.setAppVersion(CommonUtils.getStringSharedPreference(mContext, Constants.APP_VERSION_NAME, ""));

            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }

    public void sendScreenName(TrackerName trackerId, String screenName) {
        Tracker t = getTracker(trackerId);
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    public void sendEvent(
            TrackerName trackerId,
            String screenName,
            String categoryId,
            String actionId,
            String labelId,
            long valueId) {
        Tracker t = getTracker(trackerId);

        t.setScreenName(screenName);

        boolean isServiceExecutiveLogin =

                CommonUtils.getBooleanSharedPreference(
                        mContext,
                        Constants.SERVICE_EXECUTIVE_LOGIN,
                        false);

        String serviceExecutiveId = "";
        String dealerId = "";

        if (isServiceExecutiveLogin) { // if service executive login

            serviceExecutiveId = CommonUtils.getStringSharedPreference(mContext, Constants.SERVICE_EXECUTIVE_ID, "");
            dealerId = String.valueOf(CommonUtils.getIntSharedPreference(mContext, Constants.UC_DEALER_ID, -1));

            String label = dealerId + "|" + serviceExecutiveId + "|e";

            t.send(new HitBuilders.EventBuilder()
                    .setCategory(categoryId)
                    .setAction(labelId)
                    .setLabel(label)
                    .setValue(valueId)
                    .build());


        } else { // dealer login

            dealerId = String.valueOf(CommonUtils.getIntSharedPreference(mContext, Constants.UC_DEALER_ID, -1));

            String label = dealerId + "|d";

            t.send(new HitBuilders.EventBuilder()
                    .setCategory(categoryId)
                    .setAction(labelId)
                    .setLabel(label)
                    .setValue(valueId)
                    .build());
        }

    /*if (valueId != 0) {
      t.send(new HitBuilders.EventBuilder()
          .setCategory(categoryId)
          .setAction(labelId)
          .setLabel(labelId)
          .setValue(valueId).build());
    } else {
      t.send(new HitBuilders.EventBuilder()
          .setCategory(categoryId)
          .setAction(actionId)
          .setLabel(labelId)
          .build());
    }*/

        t.setScreenName(null);
    }

    public void sendUserTimings(TrackerName trackerName, String category, long time, String name, String label) {

        Tracker t = getTracker(trackerName);

        t.send(new HitBuilders.TimingBuilder().setCategory(category)
                .setValue(time)
                .setLabel(label)
                .setVariable(name).build());
    }

    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     * <p/>
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }
}
