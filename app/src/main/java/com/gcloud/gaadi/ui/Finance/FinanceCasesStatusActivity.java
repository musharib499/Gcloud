package com.gcloud.gaadi.ui.Finance;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.FinancePagerAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.SetTabTextEvent;
import com.gcloud.gaadi.model.LoanCaseModel;
import com.gcloud.gaadi.ui.BaseActivity;
import com.squareup.otto.Subscribe;

/**
 * @author Lakshay
 */
public class FinanceCasesStatusActivity extends BaseActivity {
    String month;
    String year;

    private int currentTabIndex;
    private ViewPager viewPager;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.layout_loan_cases, frameLayout);
        month = getIntent().getStringExtra(Constants.MONTH);
        year = getIntent().getStringExtra(Constants.YEAR);
        setTabLayout();

       // switchToAppropriateTab(savedInstanceState);
    }


    /*private void switchToAppropriateTab(Bundle savedInstanceState) {

        if (savedInstanceState != null)
            currentTabIndex = savedInstanceState.getInt(Constants.LOAN_CASE_STATUS, 0);
        else if (getIntent() != null) {

            if(getIntent().getExtras().containsKey("nID")){
                GCLog.e(Constants.TAG, "Log Notification finance cases");
                CommonUtils.logNotification(this,"0",getClass().getSimpleName(),getIntent().getExtras().toString());
            }
            FinanceUtils.LoanType
                    loanCaseStatus = (FinanceUtils.LoanType) getIntent().getSerializableExtra(Constants.LOAN_CASE_STATUS);

            switch (loanCaseStatus) {
                case PENDING_CASE:
                    currentTabIndex = 1;
                    break;
                case APPROVED_CASE:
                    currentTabIndex = 0;
                    break;
                default:
                    currentTabIndex = 0;
            }
        }
        viewPager.setCurrentItem(currentTabIndex);
    }*/


    public void setTabLayout() {
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        if (tabLayout == null)
            return;
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(2);

      /*  int pendingLoanCases = getIntent().getIntExtra(Constants.EXTRA_PENDING_LOAN_CASES, 0);
        int rejectedLoanCases = getIntent().getIntExtra(Constants.EXTRA_REJECTED_LOAN_CASES, 0);
        int approvedLoanCases = getIntent().getIntExtra(Constants.EXTRA_APPROVED_LOAN_CASES, 0);*/


        FinancePagerAdapter adapter = new FinancePagerAdapter(getSupportFragmentManager(), month, year);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void setTabText(SetTabTextEvent event) {
        if (event.getCurrentItem() == Constants.ACTIVE_LOAN_STATUS)
            tabLayout.getTabAt(0).setText(event.getText());
        else if (event.getCurrentItem() == Constants.COMPLETED_LOAN_STATUS)
            tabLayout.getTabAt(1).setText(event.getText());

    }

    @Override
    protected void onResume() {
        super.onResume();

        ApplicationController.getEventBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ApplicationController.getEventBus().unregister(this);
    }

    public interface OnLoanCaseContentAvailable {
        void onContentAvailable(LoanCaseModel loanCaseModel, int loanCaseStatus);
    }
}
