package com.gcloud.gaadi.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ankitgarg on 21/07/15.
 */
public class DeeplinkingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Uri data = getIntent().getData();
        Intent intent = null;

        if (data != null) {
            GCLog.e(data.toString());
            String scheme = data.getScheme();
            String host = data.getHost();
            //String queryParameter = data.getQueryParameter(Constants.LEAD_ID);
            List<String> pathSegments = data.getPathSegments();

            GCLog.e("leadType: " + pathSegments.get(0).substring(0, 1));
            LinkedList<Intent> stack = new LinkedList<>();

            if (CommonUtils.getBooleanSharedPreference(this, Constants.APP_INITIALIZATION, false)) {

                if (CommonUtils.getBooleanSharedPreference(this, Constants.USER_LOGGEDIN, false)) {
                    stack.add(new Intent(this, MainActivity.class));
                    String leadType = pathSegments.get(0).substring(0, 1);
                    switch (leadType) {
                        case "b":
                            stack.add(new Intent(this, LeadsManageActivity.class));
                            intent = new Intent(this, LeadAddActivity.class);
                            break;
                        case "s":
                            if (CommonUtils.getIntSharedPreference(this, Constants.IS_SELLER, 0) == 1) {
                                stack.add(new Intent(this, SellerLeadsActivity.class));
                                intent = new Intent(this, SellerLeadsDetailPageActivity.class);
                            } else {
                                CommonUtils.showToast(this, getString(R.string.warranty_only_dealer_message), Toast.LENGTH_LONG);
                            }
                            break;
                    }

                } else {
                    intent = new Intent(this, SplashActivity.class);
                }

            } else {
                intent = new Intent(this, SplashActivity.class);
            }
            if (intent != null) {
                intent.putExtra(Constants.TOKEN, pathSegments.get(0));
                intent.putExtra(Constants.FROM_DEEPLINK, true);
                stack.add(intent);
                //startActivity(intent);
            }
            startActivities(stack.toArray(new Intent[stack.size()]));
            finish();

            /*if (intent == null) {
                intent = new Intent(this, SplashActivity.class);
                startActivity(intent);
                finish();
            }*/

            GCLog.e("path segments: " + data.getPathSegments());
            GCLog.e("SCHEME: " + scheme + ", HOST: " + host);
        }


    }
    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_BUYER_LEAD_DETAIL);
    }
}
