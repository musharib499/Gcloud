package com.gcloud.gaadi.ui.Finance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.Fragments.FinanceIncomeInformationFragment;
import com.gcloud.gaadi.Fragments.FinanceLoadingFragment;
import com.gcloud.gaadi.Fragments.PersonalDetailsFragment;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.ApplicationCompleteEvent;
import com.gcloud.gaadi.model.CarItemModel;
import com.gcloud.gaadi.model.Finance.FinanceFormsRequestData;
import com.gcloud.gaadi.model.FinanceIncomeDetailsObj;
import com.gcloud.gaadi.model.FinancePersonalDetails;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.squareup.otto.Subscribe;

import java.util.Calendar;

/**
 * Created by Lakshay on 06-08-2015.
 */
public class FinanceFormsActivity extends FinanceToolbarActivity implements PersonalDetailsFragment.PersonalDetailsActionListener, FinanceIncomeInformationFragment.FinanceIncomeActionsListener, FinanceLoadingFragment.FinanceLoadingListener {

    private FragmentManager mFragmentManager;
    private String car_id = "";
    public static FinanceFormsRequestData mRequestData = new FinanceFormsRequestData();
    private CarItemModel model;
    public static long startTime;
    public static long leaveTime;
    public static long totalTimeOnFinanceFormsActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.finance_forms_activity, myFrameLayout);

        ApplicationController.getEventBus().register(FinanceFormsActivity.this);

        setToolBar();

        model = (CarItemModel) getIntent().getExtras().getSerializable(Constants.MODEL_DATA);
        car_id = model.getUsedCarID();
        CommonUtils.setStringSharedPreference(this, Constants.FINANCE_CAR_ID, car_id);

        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_FINANCE_PERSONAL_FORM);
        PersonalDetailsFragment fragment = PersonalDetailsFragment.getInstance(this, this);
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().replace(R.id.content_layout, fragment).commit();
        Calendar calendar = Calendar.getInstance();
        startTime = calendar.getTimeInMillis();
    }

    private void setToolBar() {
//        getSupportActionBar().setTitle(Constants.FINANCE_FORMS);
        toolbar.setTitle(Constants.FINANCE_FORMS);
        FinanceToolbarActivity.setStepProgress(2);
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Subscribe
    public void onApplicationCompleted(ApplicationCompleteEvent event) {
        GCLog.e(Constants.TAG, "finish availablecarsactivity");
        finish();
    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationController.getEventBus().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(Constants.FINANCE_FORMS);

    }

    @Override
    public void onPersonalDetailsFilled(FinancePersonalDetails detailsObj) {
        detailsObj.setCarId(car_id);
        mRequestData.setPersonalDetails(detailsObj);
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_FINANCE_INCOME_PROOFS);
        FinanceIncomeInformationFragment fragment = FinanceIncomeInformationFragment.getInstance(this);
       // mFragmentManager.beginTransaction().addToBackStack(Constants.FINANCE_INCOME_FORM).replace(R.id.content_layout, fragment).commit();
        mFragmentManager.beginTransaction().replace(R.id.content_layout, fragment).commit();

    }

    @Override
    public void onIncomeFilled(FinanceIncomeDetailsObj obj) {
        mRequestData.setIncomeDetailsObj(obj);
//        if (!obj.getEmploymentType().equalsIgnoreCase(Constants.FINANCE_EMP_TYPE_OTHERS_SERVER)) {
            /*String backStackFragmentName = mFragmentManager.getBackStackEntryAt(mFragmentManager.getBackStackEntryCount() - 1).getName();
            Toast.makeText(this, backStackFragmentName , Toast.LENGTH_SHORT).show();
            if(backStackFragmentName.equals(Constants.FINANCE_EMP_TYPE_SALARIED)
                    || backStackFragmentName.equals(Constants.FINANCE_EMP_TYPE_BUSINESS)
                    || backStackFragmentName.equals(Constants.FINANCE_EMP_TYPE_PROFESS))
            {
                FinanceLoadingFragment fragment = FinanceLoadingFragment.getInstance(this);
                mFragmentManager.beginTransaction().replace(R.id.content_layout, fragment).commitAllowingStateLoss();
            }
            else
            {
                FinanceLoadingFragment fragment = FinanceLoadingFragment.getInstance(this);
                mFragmentManager.beginTransaction().replace(R.id.content_layout, fragment).commitAllowingStateLoss();
            }*/
            Intent intent = new Intent(this, FinanceLoadingActivity.class);
            intent.putExtra(Constants.CAR_MODEL, model);
            startActivity(intent);

//        } else {
//            Intent intent = new Intent(this, FinanceOthersActivity.class);
//            startActivity(intent);
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_FINANCE_PERSONAL_FORM);
        mFragmentManager.popBackStack(Constants.LOADING_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onOthersOffers() {
        Intent intent = new Intent(this, FinanceOthersActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        //Toast.makeText(this, "Closed", Toast.LENGTH_SHORT).show();
        Calendar calender = Calendar.getInstance();
        leaveTime = calender.getTimeInMillis();
        totalTimeOnFinanceFormsActivity = totalTimeOnFinanceFormsActivity + (leaveTime - startTime);
    }
}
