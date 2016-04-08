package com.gcloud.gaadi.sfa;

import android.os.Bundle;

import com.gcloud.gaadi.ui.SplashActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

/**
 * Created by vinodtakhar on 12/1/16.
 */
public class SfaGcloudLoginActivity extends SplashActivity {
    @Override
    public void onCreate(Bundle bundle) {
        GCLog.e("Received SFA intent");
        CommonUtils.logoutUser(this);
        token = getIntent().getStringExtra(SfaCampaignTrackingReceiver.EXTRA_LOGIN_TOKEN);
        mUserRights = getIntent().getStringExtra(SfaCampaignTrackingReceiver.EXTRA_ACCESS_RIGHTS);

        CommonUtils.setStringSharedPreference(getApplicationContext(), SfaCampaignTrackingReceiver.EXTRA_ACCESS_RIGHTS, mUserRights);
        super.onCreate(bundle);

    }

    @Override
    public void showLoginForm() {

        if (!token.isEmpty()) {
            loginUser("", "", true);
        } else {
            finish();
        }
    }
/*
    @Override
    public void loginFailedError(){
        this.finish();
    }*/
}
