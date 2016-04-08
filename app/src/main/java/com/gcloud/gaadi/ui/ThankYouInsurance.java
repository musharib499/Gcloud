package com.gcloud.gaadi.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.utils.CommonUtils;

/**
 * Created by Ambujesh on 6/1/2015.
 */
public class ThankYouInsurance extends BaseActivity implements View.OnClickListener {

    private int mPolicyID;
    private ActionBar mActionBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.layout_thanku_insurance, frameLayout);
        findViewById(R.id.btnBackToMain).setOnClickListener(this);

        /*mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);*/

        Bundle intentData = getIntent().getExtras();
        if (intentData != null) {
            mPolicyID = intentData.getInt(Constants.INSURANCE_POLICY_ID, 0);
        } else if (savedInstanceState != null) {
            mPolicyID = savedInstanceState.getInt(Constants.INSURANCE_POLICY_ID);
        }

        String message = "Your request for issuing policy has been taken. Please quote REQUEST ID "
                + mPolicyID + " in all future communications. Please do not turn off the internet connection before all Insurance documents are uploaded.";
        Spannable string = new SpannableString(message);
        String subText = "REQUEST ID";
        int start = message.indexOf(subText);
        string.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.insurance_inspected_bg)),
                start,
                (start + subText.length() + 1 + String.valueOf(mPolicyID).length()),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(new AbsoluteSizeSpan(20, true),
                start,
                (start + subText.length() + 1 + String.valueOf(mPolicyID).length()),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView) findViewById(R.id.tvThankyouMessage)).setText(string);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                launchMainScreen();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackToMain:
                launchMainScreen();
                break;
        }
    }

    private void launchMainScreen() {
        setResult(RESULT_OK);
        finish();
        finishActivity(Constants.INSURANCE_CASE_FOR_INSPECTED_CAR);
        CommonUtils.startActivityTransition(ThankYouInsurance.this, Constants.TRANSITION_RIIGHT);
    }
}
