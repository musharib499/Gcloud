package com.gcloud.gaadi.ui.Finance;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.FinanceDBHelper;
import com.gcloud.gaadi.model.Finance.FinanceDashboardData;
import com.gcloud.gaadi.model.Finance.FinanceDashboardResponse;
import com.gcloud.gaadi.model.Finance.MonthWiseCount;
import com.gcloud.gaadi.model.Finance.TotalLeadsCount;
import com.gcloud.gaadi.model.FinanceMonthYearModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.BaseActivity;
import com.gcloud.gaadi.ui.customviews.PagerContainer;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.FinanceUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GaadiFinanceActivity extends BaseActivity implements View.OnClickListener {
    ArrayList<MonthWiseCount> monthYrList = new ArrayList<>();
    int total, approvedCases, pendingCases, rejectedCases, total_count_past_days, approved_count_past_days, rejected_count_past_days, pending_count_past_days;
    String month = "0";
    String year = "0";
    int count;
    float steps;
    int sign;
    int step;
    MonthWiseCount prev = null;
    boolean flag = true;
    //    private GAHelper mGAHelper;
    private CardView newCase;
    private TextView tvPendingCasesCount, tvApprovedCasesCount, tvRejectedCasesCount, tvDisbursedCasesCount, tvTotalCasesCount, tvTotalDisbursementAmount;
    private RelativeLayout left_layout_;
   // private TextView tvCasesFiled;
    private TextView tvPercentageChange;
    private ProgressBar mProgressBar;
    //private CircularView mCircularView;
    private LinearLayout llContent;
    private FinanceDashboardData data;
    private PagerContainer mContainer;
    private PagerAdapter adapter;
    private ViewPager pager;
    private int selectedItem = -1;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.finance_dashboard, frameLayout);
        FinanceMonthYearModel financeMonthYrModel;
       /* toolbar.setTitle("Loan Cases Dashboard");
        llContent = (LinearLayout) findViewById(R.id.llContent);
        mProgressBar = (ProgressBar) findViewById(R.id.pbContent);
        setupTablayout();
        left_layout_ = (RelativeLayout) findViewById(R.id.left_layout);
        tvPercentageChange = (TextView) findViewById(R.id.tvPercentageChange);*/
        mProgressBar = (ProgressBar) findViewById(R.id.pbContent);
       /* Calendar calendar = Calendar.getInstance();
        financeMonthYrModel = new FinanceMonthYearModel();

        monthYrList.add(financeMonthYrModel);
        monthYrList.add(financeMonthYrModel);
        while(calendar.get(Calendar.YEAR) >= 2015)
        {
            if(calendar.get(Calendar.MONTH)==4) // in Calendar june is 5th, as month starts from zero
            {
                break;
            }
            financeMonthYrModel = new FinanceMonthYearModel();

            financeMonthYrModel.setMonth(calendar.get(Calendar.MONTH) + 1);
            financeMonthYrModel.setYear(calendar.get(Calendar.YEAR));
            calendar.add(calendar.MONTH, -1);
            monthYrList.add(financeMonthYrModel);

        }
        financeMonthYrModel = new FinanceMonthYearModel();
        monthYrList.add(financeMonthYrModel);
        monthYrList.add(financeMonthYrModel);*/
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(GaadiFinanceActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
       // recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
      //  tvCasesFiled = (TextView) findViewById(R.id.tvCasesFiled);
        mProgressBar = (ProgressBar)findViewById(R.id.pbContent);
        // recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tvPendingCasesCount = (TextView) findViewById(R.id.pendingCasesCount);
        tvApprovedCasesCount = (TextView) findViewById(R.id.approvedCasesCount);
        tvRejectedCasesCount = (TextView) findViewById(R.id.rejectedCasesCount);
        tvDisbursedCasesCount = (TextView) findViewById(R.id.disbursedCasesCount);
        tvTotalDisbursementAmount = (TextView) findViewById(R.id.tvTotalDisbursement);
        tvTotalCasesCount = (TextView) findViewById(R.id.totalCasesCount);
        findViewById(R.id.rlNewCasesLayout).setOnClickListener(this);
        findViewById(R.id.financeCasesLayout).setOnClickListener(this);
        mContainer = (PagerContainer) findViewById(R.id.pager_container);
        pager = mContainer.getViewPager();

      /*  selectedRegionVw = findViewById(R.id.selectedView);
        final int[] location = new int[2];
        selectedRegionVw.getLocationInWindow(location);
        selectedRegionVwXPos = location[0];
        Log.v("Location", "left: " + location[0]);
        Log.v("Location", "top: " + location[1]);*/

     /*   recyclerView.setLayoutManager(linearLayoutManager);

       /* try {
//            if(FinanceDB.getCategories() == null || FinanceDB.getCategories().size() == 0)
                FinanceDBHelper.insertFinanceDocTags();
        } catch (Exception e) {
            GCLog.e(e.getMessage());
        }*/

    }

    public void setViewPagerCal(final ArrayList<MonthWiseCount> monthYrList) {


        final int size = monthYrList.size();

        /*final ArrayList<InsuranceDashboardModel.Dashboard> data = new ArrayList<>();
        data.addAll(dashboardModel.getMonthwise());
        data.add(dashboardModel.getData());
*/
        adapter = new PagerAdapter() {

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {
                TextView view = new TextView(GaadiFinanceActivity.this);
                view.setGravity(Gravity.CENTER);
                view.setTextColor(ContextCompat.getColor(GaadiFinanceActivity.this, R.color.white));

                if (position == getCount() - 1) {
                    view.setText("All");
                } else {

                    view.setText(ApplicationController.monthShortMap.get(monthYrList.get(position).getMonth())+"\n"+monthYrList.get(position).getYear());
                }
                if (!view.getText().toString().equalsIgnoreCase("null")) {
                    container.addView(view);
                }
                return view;
            }

            @Override
            public int getCount() {
                return size;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return (view == object);
            }
        };
        pager.setAdapter(adapter);
        //Necessary or the pager will only have one extra page to show
        // make this at least however many pages you can see
        pager.setOffscreenPageLimit(adapter.getCount());
        //A little space between pages
        pager.setPageMargin(15);

        //If hardware acceleration is enabled, you should also remove
        // clipping on the pager for its children.
        pager.setClipChildren(false);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                month = monthYrList.get(position).getMonth();
                year = monthYrList.get(position).getYear();
                //Toast.makeText(InsuranceDashboard.this, "position: " + position, Toast.LENGTH_SHORT).show();
                resetFinanceData(monthYrList.get(position), prev);
                prev = monthYrList.get(position);
                //prev=monthYrList.get(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void resetFinanceData(final MonthWiseCount monthWiseCount, final MonthWiseCount prev) {
        count = 0;
        step = 0;
        steps = 0;
        final Handler getTotalHandler = new Handler();


        final Handler getApprovedHandler = new Handler();
        getApprovedHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                ++count;
                if (prev == null) {

                    counterOnViewToDisplayData(tvApprovedCasesCount, Integer.parseInt(monthWiseCount.getApproved()), 0);

                } else {

                    counterOnViewToDisplayData(tvApprovedCasesCount, Integer.parseInt(monthWiseCount.getApproved()), Integer.parseInt(prev.getApproved()));
                }

                if ((sign == 1 && (step < Integer.parseInt(monthWiseCount.getApproved()))) || (sign == -1 && (step > Integer.parseInt(monthWiseCount.getApproved())))) {
                    getApprovedHandler.postDelayed(this, 0);
                }

            }
        }, 500);
        final Handler getPendingHandler = new Handler();
        getPendingHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                ++count;
                if (prev == null) {

                    counterOnViewToDisplayData(tvPendingCasesCount, Integer.parseInt(monthWiseCount.getPending()), 0);

                } else {

                    counterOnViewToDisplayData(tvPendingCasesCount, Integer.parseInt(monthWiseCount.getPending()), Integer.parseInt(prev.getPending()));
                }

                if ((sign == 1 && (step < Integer.parseInt(monthWiseCount.getPending()))) || (sign == -1 && (step > Integer.parseInt(monthWiseCount.getPending())))) {
                    getPendingHandler.postDelayed(this, 0);
                }

            }
        }, 500);

        final Handler getRejectedHandler = new Handler();
        getRejectedHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                ++count;
                if (prev == null) {

                    counterOnViewToDisplayData(tvRejectedCasesCount, Integer.parseInt(monthWiseCount.getRejected()), 0);

                } else {
                    counterOnViewToDisplayData(tvRejectedCasesCount, Integer.parseInt(monthWiseCount.getRejected()), Integer.parseInt(prev.getRejected()));
                }

                if ((sign == 1 && (step < Integer.parseInt(monthWiseCount.getRejected()))) || (sign == -1 && (step > Integer.parseInt(monthWiseCount.getRejected())))) {
                    getRejectedHandler.postDelayed(this, 0);
                }

            }
        }, 500);
//
        final Handler getDisbursedHandler = new Handler();
        getDisbursedHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                ++count;
                if (prev == null) {

                    counterOnViewToDisplayData(tvDisbursedCasesCount, Integer.parseInt(monthWiseCount.getDisbursed()), 0);

                } else {
                    counterOnViewToDisplayData(tvDisbursedCasesCount, Integer.parseInt(monthWiseCount.getDisbursed()), Integer.parseInt(prev.getDisbursed()));
                }

                if ((sign == 1 && (step < Integer.parseInt(monthWiseCount.getDisbursed()))) || (sign == -1 && (step > Integer.parseInt(monthWiseCount.getDisbursed())))) {
                    getDisbursedHandler.postDelayed(this, 0);
                }

            }
        }, 500);


        getTotalHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ++count;
                if (prev == null) {
                    counterOnViewToDisplayData(tvTotalCasesCount, monthWiseCount.getTotal().intValue(), 0);
                } else {
                    counterOnViewToDisplayData(tvTotalCasesCount, monthWiseCount.getTotal().intValue(), prev.getTotal().intValue());
                }
                if ((sign == 1 && (step < monthWiseCount.getTotal())) || (sign == -1 && (step > monthWiseCount.getTotal()))) {
                    getTotalHandler.postDelayed(this, 0);
                }

            }
        }, 500);

        final Handler priceHandler = new Handler();
        priceHandler.postDelayed(new Runnable() {
            int count = 0;
            int steps = 0;
            int step = 0;
            int numOfCases = 0;
            int prevCases = 0;

            @Override
            public void run() {
                ++count;
                numOfCases = Integer.parseInt(monthWiseCount.getDisbursedAmount());
                if (prev == null) {
                    prevCases = 0;
                } else {
                    prevCases = Integer.parseInt(prev.getDisbursedAmount());
                }
                steps = (Math.abs(numOfCases - prevCases) < 100 ?
                        1 :
                        Math.abs(numOfCases - prevCases) / 100);

                if (numOfCases == prevCases) {
                    return;
                }
                sign = ((numOfCases - prevCases) / Math.abs(numOfCases - prevCases));
                step = (steps * count * sign) + prevCases;

                if ((sign == -1) && (step <= numOfCases)) {
                    step = numOfCases;
                }

                if ((sign == 1) && (step >= numOfCases)) {
                    step = numOfCases;
                }
                CommonUtils.insertCommaIntoNumber(tvTotalDisbursementAmount, String.valueOf(step), "##,##,###");
                if ((sign == 1 && (step < Integer.parseInt(monthWiseCount.getDisbursedAmount()))) || (sign == -1 && (step > Integer.parseInt(monthWiseCount.getDisbursedAmount())))) {
                    priceHandler.postDelayed(this, 0);
                }
            }
        }, 500);
    }

    private void counterOnViewToDisplayData(TextView textView, int numberOfCases, int prevCount) {

        steps = (Math.abs(numberOfCases - prevCount) < 100 ?
                1 :
                Math.abs(numberOfCases - prevCount) / 100);
        if (numberOfCases == prevCount) {
            return;
        }

        sign = ((numberOfCases - prevCount) / Math.abs(numberOfCases - prevCount));

        step = ((int) steps * count * sign) + prevCount;
        if ((sign == -1) && (step <= numberOfCases)) {
            step = numberOfCases;


        }
        if ((sign == 1) && (step >= numberOfCases)) {
            step = numberOfCases;


        }
        textView.setText(String.valueOf(step));
    }

    /*public void setCircularViewDesing(int approvedCases, int rejectedCases, int pendingCases, int total) {
  /*  public void setCircularViewDesing(int approvedCases, int rejectedCases, int pendingCases, int total) {
        mCircularView = (CircularView) findViewById(R.id.ccv);
        mCircularView.setData(approvedCases, rejectedCases, pendingCases, total);
        mCircularView.setCompletionPercentage(total);
        *//*if(String.valueOf(total).length() == 4)
            mCircularView.setTextSize(60);
        else if(String.valueOf(total).length() == 3)
            mCircularView.setTextSize(80);
        else
            mCircularView.setTextSize(90);*//*
        mCircularView.setStrokeSize(15);
    }*/


 /*   public void setupTablayout() {
        tabLayout.setVisibility(View.VISIBLE);
        if (tabLayout == null)
            return;
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addTab(tabLayout.newTab().setText("Past 30 Days"));
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == 0) {
                    if (left_layout_.getVisibility() == View.GONE) {
                        left_layout_.setVisibility(View.VISIBLE);
                        tvCasesFiled.setText("CASES FILED");
                        setCircularViewDesing(approved_count_past_days, rejected_count_past_days, pending_count_past_days, total_count_past_days);
                        tvApproved.setText(approved_count_past_days + "");
                        tvPending.setText(pending_count_past_days + "");
                        tvRejected.setText(rejected_count_past_days + "");
                    }

                } else if (tab.getPosition() == 1)

                    if (left_layout_.getVisibility() == View.VISIBLE) {
                        left_layout_.setVisibility(View.GONE);
                        tvCasesFiled.setText("TOTAL CASES FILED");
                        setCircularViewDesing(approvedCases, rejectedCases, pendingCases ,total);
                        tvApproved.setText(approvedCases + "");
                        tvPending.setText(pendingCases + "");
                        tvRejected.setText(rejectedCases + "");
                    }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }*/


    @Override
    protected void onStart() {
        super.onStart();
        try {
            getFinanceStats();
        } catch (Exception e) {
            GCLog.e(e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_FINANCE_DASHBOARD);

    }


    private void getFinanceStats() throws Exception {

        RetrofitRequest.getFinanceStats(new Callback<FinanceDashboardResponse>() {
            @Override
            public void success(FinanceDashboardResponse financeStatsModel, Response response) {
                if ("T".equalsIgnoreCase(financeStatsModel.getStatus())) {
                    // llContent.setVisibility(View.VISIBLE);
                    data = financeStatsModel.getData();
                    monthYrList.clear();
                    // monthYrList.add(new MonthWiseCount());
                    // monthYrList.add(new MonthWiseCount());
                    monthYrList.addAll(data.getMonthWiseCount());
                    MonthWiseCount monthWiseCount = new MonthWiseCount();
                    TotalLeadsCount totalLeadsCount;
                    totalLeadsCount = data.getTotalLeadsCount();
                    monthWiseCount.setApproved(totalLeadsCount.getApproved());
                    monthWiseCount.setDisbursed(totalLeadsCount.getDisbursed());
                    monthWiseCount.setDisbursedAmount(totalLeadsCount.getDisbursedAmount());
                    monthWiseCount.setPending(totalLeadsCount.getPending());
                    monthWiseCount.setRejected(totalLeadsCount.getRejected());
                    monthWiseCount.setTotal(Integer.valueOf(totalLeadsCount.getTotal()));
                    monthWiseCount.setMonth("0");
                    monthWiseCount.setYear("0");
                    monthYrList.add(monthWiseCount);
                    // monthYrList.add(new MonthWiseCount());
                    // monthYrList.add(new MonthWiseCount());
                    // recyclerView.setAdapter(new FinanceDashboardRecyclerAdapter(GaadiFinanceActivity.this, monthYrList));
                    setViewPagerCal(monthYrList);
                    if (selectedItem != -1) {
                        pager.setCurrentItem(selectedItem, true);
                    } else {
                        pager.setCurrentItem(adapter.getCount() - 1, true);
                    }
                    // recyclerView.scrollToPosition(monthYrList.size());
                    total = Integer.parseInt(totalLeadsCount.getTotal());
                    approvedCases = Integer.parseInt(totalLeadsCount.getApproved());
                    pendingCases = Integer.parseInt(totalLeadsCount.getPending());
                    rejectedCases = Integer.parseInt(totalLeadsCount.getRejected());
                  /*  tvTotalCasesCount.setText(total + "");


                    //setCircularViewDesing(approved_count_past_days, rejected_count_past_days, pending_count_past_days, total_count_past_days);
                    tvApprovedCasesCount.setText(approvedCases + "");
                    tvPendingCasesCount.setText(pendingCases + "");
                    tvRejectedCasesCount.setText(rejectedCases + "");
                    tvDisbursedCasesCount.setText(Integer.parseInt(totalLeadsCount.getDisbursed()) + "");
                    CommonUtils.insertCommaIntoNumber(tvTotalDisbursementAmount, totalLeadsCount.getDisbursedAmount(), "##,##,###");*/

                    /* if ("1".equals(data.getType())) {
                       tvPercentageChange.setText("Higher than last month");
                    } else {
                        tvPercentageChange.setText("Lesser than last month");
                    }

                    tvPercentageChange.setText(data.getPercentage_val() + " %");
                    left_layout_.setVisibility(View.VISIBLE);

                    if (percentage_val > 0)
                        tvPercentageChange.setTextColor(Color.parseColor("#04B404"));   //Green
                    else
                        tvPercentageChange.setTextColor(Color.parseColor("#ff0000"));*/
                    mProgressBar.setVisibility(View.GONE);

                    if (FinanceDBHelper.getAllBanksCounts() == 0 || FinanceDBHelper.getAllCompanyCounts() == 0 || FinanceDBHelper.getAllEmploymentCounts() == 0 || FinanceDBHelper.getAllIndustriesCounts() == 0)
                        FinanceUtils.performSync();
                } else if ("F".equalsIgnoreCase(financeStatsModel.getStatus()) && financeStatsModel.isForceUpgrade()) {
                    showForceUpgradeDialog();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                CommonUtils.showErrorToast(GaadiFinanceActivity.this, error, Toast.LENGTH_SHORT);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_new_case:
                Intent intent = new Intent(this, FinanceToolbarActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        GCLog.e("view clicked");
        switch (v.getId()) {
          /*  case R.id.rlRejectedCases:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_FINANCE_DASHBOARD, Constants.CATEGORY_FINANCE_DASHBOARD,
                        Constants.ACTION_TAP, Constants.FINANCE_DASHBOARD_REJECTED, 0);
                intent = new Intent(this, FinanceCasesStatusActivity.class);
                intent.putExtra(Constants.LOAN_CASE_STATUS, FinanceUtils.LoanType.REJECTED_CASE);
                break;*/
            case R.id.financeCasesLayout:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_FINANCE_DASHBOARD, Constants.CATEGORY_FINANCE_DASHBOARD,
                        Constants.ACTION_TAP, Constants.FINANCE_DASHBOARD_PENDING, 0);
                if (Integer.parseInt(tvTotalCasesCount.getText().toString()) != 0 && tvTotalCasesCount != null) {
                    intent = new Intent(this, FinanceCasesStatusActivity.class);
                    intent.putExtra(Constants.LOAN_CASE_STATUS, FinanceUtils.LoanType.PENDING_CASE);
                    startActivity(intent);
                }
                intent = new Intent(this, FinanceCasesStatusActivity.class);
                intent.putExtra(Constants.MONTH, month);
                intent.putExtra(Constants.YEAR, year);
                if (pager != null)
                    selectedItem = pager.getCurrentItem();
                break;

          /*  case R.id.rlApprovedFinances:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_FINANCE_DASHBOARD, Constants.CATEGORY_FINANCE_DASHBOARD,
                        Constants.ACTION_TAP, Constants.FINANCE_DASHBOARD_APPROVED, 0);
                intent = new Intent(this, FinanceCasesStatusActivity.class);
                intent.putExtra(Constants.LOAN_CASE_STATUS, FinanceUtils.LoanType.APPROVED_CASE);
                break;

//            case R.id.rlIncompleteCases:
//                GCLog.e(Constants.TAG, "Clicked Incomplete Case ");
//                intent = new Intent(this, FinanceCasesStatusActivity.class);
//                intent.putNExtra(Constants.LOAN_CASE_STATUS,"3");
//                startActivity(intent);
//                break;*/

            case R.id.rlNewCasesLayout:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_FINANCE_DASHBOARD, Constants.CATEGORY_FINANCE_DASHBOARD,
                        Constants.ACTION_TAP, Constants.FINANCE_DASHBOARD_NEW, 0);
//                    intent = new Intent(GaadiFinanceActivity.this, FinanceCollectImagesActivity.class);
                intent = new Intent(GaadiFinanceActivity.this, AvailableCarsActivity.class);
//                intent = new Intent(GaadiFinanceActivity.this, FinanceOffersActivity.class);
                startActivity(intent);
                break;
        }

       /* if(data != null){
            intent.putExtra(Constants.EXTRA_APPROVED_LOAN_CASES, Integer.parseInt(data.getApproved()));
            intent.putExtra(Constants.EXTRA_PENDING_LOAN_CASES, Integer.parseInt(data.getPending()));
            intent.putExtra(Constants.EXTRA_REJECTED_LOAN_CASES, Integer.parseInt(data.getRejected()));
        }*/
        // startActivity(intent);
    }

    private void showForceUpgradeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_upgrade_title);
        builder.setMessage(R.string.app_upgrade_message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.upgrade_now, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_FINANCE_DASHBOARD,
                        Constants.CATEGORY_FINANCE_DASHBOARD,
                        Constants.ACTION_TAP,
                        Constants.LABEL_FORCE_UPGRADE,
                        0);
                navigateToPlayStore(false);
            }
        });
        builder.setNegativeButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_FINANCE_DASHBOARD,
                        Constants.CATEGORY_FINANCE_DASHBOARD,
                        Constants.ACTION_TAP,
                        Constants.LABEL_FORCE_UPGRADE,
                        0);
                finish();
            }
        });

        AlertDialog dialog = builder.create();

        if (!this.isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }
    }

    private void navigateToPlayStore(boolean softUpgrade) {
        try {
           // GCLog.e("PlayStoreurl", "market://details?id=" + CommonUtils.getStringSharedPreference(this, Constants.APP_PACKAGE_NAME, ""));

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
                    + CommonUtils.getStringSharedPreference(this, Constants.APP_PACKAGE_NAME, ""))));
           // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.gcloud.gaadi")));
            finish();

        } catch (ActivityNotFoundException anfe) {
            Crashlytics.logException(anfe);
            CommonUtils.showToast(this, "Play store not found. Please retry.", Toast.LENGTH_LONG);

        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        final View view = findViewById(R.id.calendarCentralView);
        final int leftX = Math.round(view.getX()),
                rightX = Math.round(view.getX() + view.getWidth()),
                width = Math.round(view.getWidth());

        findViewById(R.id.pager_container).setOnTouchListener(new View.OnTouchListener() {
            int x = 0, y = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pager.onTouchEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = Math.round(event.getX());
                        y = Math.round(event.getY());
                        break;

                    case MotionEvent.ACTION_UP:
                        if (fallsInRange(x, Math.round(event.getX()))
                                || fallsInRange(y, Math.round(event.getY()))) {
                            int currentPosition = pager.getCurrentItem();
                            if (x < leftX) {
                                if (x < leftX && x > (leftX - width) && currentPosition > 0) {
                                    pager.setCurrentItem(currentPosition - 1);
                                } else if (currentPosition > 1) {
                                    pager.setCurrentItem(currentPosition - 2);
                                }
                            } else if (x > rightX) {
                                if (x > rightX && x < (rightX + width) && currentPosition < 6) {
                                    pager.setCurrentItem(currentPosition + 1);
                                } else if (currentPosition < 5) {
                                    pager.setCurrentItem(currentPosition + 2);
                                }
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    private boolean fallsInRange(int start, int end) {
        return Math.abs(end - start) <= 2;
    }
}
