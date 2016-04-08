package com.gcloud.gaadi.insurance;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.FinanceCompany;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.GCProgressDialog;
import com.gcloud.gaadi.ui.customviews.PagerContainer;
import com.gcloud.gaadi.utils.CommonUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

public class NewInsuranceDashboard extends AppCompatActivity implements View.OnClickListener {

    private ViewPager pager;
    private String month = "";
    private String year = "";

    private TextView totalPremium, totalCasesCount, issuedCount, cancelledCount, inProcessCount, pendingCasesCount;
    private GCProgressDialog progressDialog;
    private ArrayList<String> mInsuranceCities;
    private ArrayList<FinanceCompany> financeCompanyList;
    private String mAgentId;
    private ArrayList<InsuranceDashboardModel.PendingInsuranceCaseModel> pendingCases;
    private LinearLayout linearLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private NestedScrollView nestedScrollView;
    private RecyclerView.Adapter<ViewHolder> adapter = new RecyclerView.Adapter<ViewHolder>() {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.insurance_pending_tuple, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final InsuranceDashboardModel.PendingInsuranceCaseModel model = pendingCases.get(position);

            holder.requestNo.setText(getString(R.string.request_no, model.getRequestId()));
            holder.reason.setText(model.getPendingReason());
            holder.makeLogo.setImageResource(ApplicationController.makeLogoMap.get(model.getMakeId()));
            holder.makeModelVersion.setText(getString(R.string.insurance_makeModelVersion,
                    new String[]{model.getModel(), model.getVersion(), ""}));
            holder.registrationNumber.setText(model.getRegNo());
            holder.requestDate.setText(model.getRequestDate());
            holder.insurer.setText(model.getInsurer());
            holder.premium.setText(model.getNetPremium());

            holder.uploadNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ApplicationController.checkInternetConnectivity()
                            && CommonUtils.getIntSharedPreference(NewInsuranceDashboard.this,
                            Constants.INSURANCE_PROCESS_ID_IN_PROGRESS, 0) != Integer.parseInt(model.getProcessId())) {
                        startActivityForResult(new Intent(NewInsuranceDashboard.this, InsurancePendingActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                .putExtra("pendingModel", model), 101);
                    } else {
                        CommonUtils.showToast(NewInsuranceDashboard.this, "Image Upload Already in progress", Toast.LENGTH_SHORT);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return pendingCases.size();
        }
    };
    private InsuranceDashboardModel insuranceDashboardModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getLayoutInflater().inflate(R.layout.new_insurance_dashboard, frameLayout);
        setContentView(R.layout.new_insurance_dashboard);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mInsuranceCities = getIntent().getStringArrayListExtra(Constants.INSURANCE_CITIES);
        financeCompanyList = (ArrayList<FinanceCompany>) getIntent().getSerializableExtra(Constants.FINANCE_COMPANY);
        mAgentId = getIntent().getStringExtra(Constants.AGENT_ID);

        totalPremium = (TextView) findViewById(R.id.totalPremium);
        totalCasesCount = (TextView) findViewById(R.id.totalCasesCount);
        issuedCount = (TextView) findViewById(R.id.issuedCount);
        inProcessCount = (TextView) findViewById(R.id.inProcessCount);
        cancelledCount = (TextView) findViewById(R.id.cancelledCount);
        pendingCasesCount = (TextView) findViewById(R.id.pendingCasesCount);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        RecyclerView pendingCasesList = (RecyclerView) findViewById(R.id.pendingCasesList);
        pendingCasesList.setLayoutManager(new LinearLayoutManager(this));
        pendingCasesList.setNestedScrollingEnabled(false);
        pendingCasesList.setHasFixedSize(false);
        /*pendingCasesList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                findViewById(R.id.nestedScrollView).onTouchEvent(event);
                return true;
            }
        });*/
        pendingCasesList.setAdapter(adapter);

        totalCasesCount.setOnClickListener(this);
        findViewById(R.id.renewPolicy).setOnClickListener(this);
        findViewById(R.id.getInspectedCarsListLayout).setOnClickListener(this);

        pendingCases = new ArrayList<>();

        progressDialog = new GCProgressDialog(this, this, getString(R.string.please_wait));

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            CollapsingToolbarLayout.LayoutParams layoutParams = (CollapsingToolbarLayout.LayoutParams) linearLayout.getLayoutParams();
            int margin = actionBarHeight + getResources().getDimensionPixelSize(R.dimen.insurance_title_top_margin);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                margin += 15;
            } else {
                margin -= 15;
            }
            layoutParams.setMargins(0,
                    margin,
                    0, 0);
            linearLayout.setLayoutParams(layoutParams);
        }

        /*findViewById(R.id.insuranceCasesLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pager.onTouchEvent(event);
                return true;
            }
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestToGetInsuranceCases(false, 1);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case 101:
                pendingCases.remove(data.getSerializableExtra("pendingModel"));
                changeScrollingState();
                adapter.notifyDataSetChanged();
                pendingCasesCount.setText(pendingCases.size() == 0 ? "" : getString(R.string.pending_cases, pendingCases.size()));
                ((AppBarLayout) findViewById(R.id.appbar)).setExpanded(true, true);
                requestToGetInsuranceCases(true, 1);
                break;
        }
    }

    public void setViewPagerCal(InsuranceDashboardModel response) {
        nestedScrollView.smoothScrollTo(0, 0);
        insuranceDashboardModel = response;
        PagerContainer mContainer = (PagerContainer) findViewById(R.id.pager_container);

        //final int size = monthYrList.size();
        pendingCases = response.getPendingInsuranceCaseModels();
        if (pendingCases == null) {
            pendingCases = new ArrayList<>();
        }
        pendingCasesCount.setText(pendingCases.size() == 0 ? "" : getString(R.string.pending_cases, pendingCases.size()));

        changeScrollingState();
        adapter.notifyDataSetChanged();

        final ArrayList<InsuranceDashboardModel.Dashboard> data = new ArrayList<>();
        data.addAll(response.getMonthwise());
        data.add(response.getData());

        pager = mContainer.getViewPager();
        pager.setAdapter(new PagerAdapter() {

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {
                LinearLayout view = new LinearLayout(NewInsuranceDashboard.this);
                view.setOrientation(LinearLayout.VERTICAL);
                view.setGravity(Gravity.CENTER);

                TextView month = new TextView(NewInsuranceDashboard.this);
                month.setTextColor(ContextCompat.getColor(NewInsuranceDashboard.this, android.R.color.white));
                month.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                month.setGravity(Gravity.CENTER_HORIZONTAL);

                TextView year = new TextView(NewInsuranceDashboard.this);
                year.setTextColor(ContextCompat.getColor(NewInsuranceDashboard.this, R.color.insurance_light_gray));
                year.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                year.setPadding(0, 5, 0, 0);
                year.setGravity(Gravity.CENTER_HORIZONTAL);

                view.addView(month);
                view.addView(year);

                if (position == getCount() - 1) {
                    month.setText("All");
                } else {
                    month.setText(ApplicationController.monthShortMap.get(data.get(position).getMonth()));
                    year.setText(data.get(position).getYear());
                }

                container.addView(view);
                return view;
            }

            @Override
            public int getCount() {
                return data.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return (view == object);
            }
        });
        //Necessary or the pager will only have one extra page to show
        // make this at least however many pages you can see
        pager.setOffscreenPageLimit(pager.getAdapter().getCount());
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
                month = data.get(position).getMonth();
                year = data.get(position).getYear();
                resetInsuranceData(data.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void changeScrollingState() {
        AppBarLayout.LayoutParams collapseParams = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
        collapseParams.setScrollFlags(pendingCases.size() > 0 ?
                AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP : 0);
        collapsingToolbarLayout.setLayoutParams(collapseParams);
    }

    private boolean fallsInRange(int start, int end) {
        return Math.abs(end - start) <= 2;
    }

    private void resetInsuranceData(InsuranceDashboardModel.Dashboard dashboard) {
        totalCasesCount.setText(dashboard.getAllCount());
        issuedCount.setText(dashboard.getBookedCount());
        inProcessCount.setText(dashboard.getUnbookedCount());
        cancelledCount.setText(dashboard.getCancelledCount());
        totalPremium.setText(getString(R.string.inr, dashboard.getTotalCommissionEarned()));
    }

    private void requestToGetInsuranceCases(final boolean showFullPageError, final int pageNo) {
        final String insuranceDashboardDataInJson = CommonUtils
                .getStringSharedPreference(this, Constants.INSURANCE_DASHBOARD_OFFLINE_DATA, "");
        if (!showFullPageError) {
            if (!insuranceDashboardDataInJson.isEmpty()) {
                reloadData(new Gson().fromJson(insuranceDashboardDataInJson, InsuranceDashboardModel.class));
            }
        }
        if (!this.isFinishing() && progressDialog != null &&
                !progressDialog.isShowing() && insuranceDashboardDataInJson.isEmpty()) {
            progressDialog.show();
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.METHOD_LABEL, Constants.GET_INSURANCE_CASES);
        params.put(Constants.PAGE_NO, String.valueOf(pageNo));
        params.put("type", "count");
        params.put("records", "all");

        RetrofitRequest.getInsuranceDashboard(
                params,
                new Callback<InsuranceDashboardModel>() {

                    @Override
                    public void success(InsuranceDashboardModel response, retrofit.client.Response res) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        if ("T".equalsIgnoreCase(response.getStatus())) {
                            CommonUtils.setStringSharedPreference(NewInsuranceDashboard.this,
                                    Constants.INSURANCE_DASHBOARD_OFFLINE_DATA,
                                    new Gson().toJson(response));
                            reloadData(response);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        CommonUtils.showErrorToast(NewInsuranceDashboard.this, error, Toast.LENGTH_SHORT);
                    }
                });

    }

    private void reloadData(InsuranceDashboardModel response) {
        setViewPagerCal(response);
        pager.setCurrentItem(pager.getAdapter().getCount() - 2, true);
    }

    public void getInspectedCarList() {
        Intent intent = new Intent(NewInsuranceDashboard.this, InsuranceInspectedCarsListActivity.class);
        intent.putExtra(Constants.AGENT_ID, mAgentId);
        intent.putStringArrayListExtra(Constants.INSURANCE_CITIES, mInsuranceCities);
        intent.putExtra(Constants.FINANCE_COMPANY, financeCompanyList);
        startActivity(intent);
        CommonUtils.startActivityTransition(NewInsuranceDashboard.this, Constants.TRANSITION_LEFT);
    }

    public void startRenewalCase() {
        Intent otherCarsintent = new Intent(NewInsuranceDashboard.this, IssuePolicyForOtherCarsActivity.class);
        otherCarsintent.putExtra(Constants.AGENT_ID, mAgentId);
        otherCarsintent.putStringArrayListExtra(Constants.INSURANCE_CITIES, mInsuranceCities);
        otherCarsintent.putExtra(Constants.FINANCE_COMPANY, financeCompanyList);
        startActivity(otherCarsintent);
        CommonUtils.startActivityTransition(NewInsuranceDashboard.this, Constants.TRANSITION_LEFT);
    }

    public void openTotalCases() {
        if (Integer.parseInt(totalCasesCount.getText().toString()) == 0) {
            return;
        }
        startActivity(new Intent(this, AllCasesActivity.class)
                .putExtra("month", month)
                .putExtra("year", year));
        CommonUtils.startActivityTransition(NewInsuranceDashboard.this, Constants.TRANSITION_LEFT);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.totalCasesCount:
                openTotalCases();
                break;

            case R.id.getInspectedCarsListLayout:
                getInspectedCarList();
                break;

            case R.id.renewPolicy:
                startRenewalCase();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_insurance_dashboard, menu);
        TextView callHelpline = (TextView) menu.findItem(R.id.action_call_helpline)
                .getActionView().findViewById(R.id.call_helpline);
        callHelpline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (insuranceDashboardModel != null
                        && CommonUtils.isValidField(insuranceDashboardModel.getHelplineNumber())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && !CommonUtils.checkForPermission(NewInsuranceDashboard.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            101, "Phone")) {
                        return;
                    }
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:+91" + /**/insuranceDashboardModel.getHelplineNumber() /*/"9560619309"/**/));
                    startActivity(intent);
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            Intent intent;
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                intent = new Intent(Intent.ACTION_CALL);
            } else {
                intent = new Intent(Intent.ACTION_DIAL);
            }
            intent.setData(Uri.parse("tel:+91" + /**/insuranceDashboardModel.getHelplineNumber() /*/"9560619309"/**/));
            startActivity(intent);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        public TextView requestNo, reason, makeModelVersion, registrationNumber,
                insuranceType, requestDate, insurer, premium, uploadNow;
        public ImageView makeLogo;

        public ViewHolder(View itemView) {
            super(itemView);

            requestNo = (TextView) itemView.findViewById(R.id.requestNo);
            reason = (TextView) itemView.findViewById(R.id.reason);
            makeModelVersion = (TextView) itemView.findViewById(R.id.makeModelVersion);
            registrationNumber = (TextView) itemView.findViewById(R.id.registrationNumber);
            insuranceType = (TextView) itemView.findViewById(R.id.insuranceType);
            requestDate = (TextView) itemView.findViewById(R.id.requestDate);
            insurer = (TextView) itemView.findViewById(R.id.insurer);
            premium = (TextView) itemView.findViewById(R.id.premium);
            makeLogo = (ImageView) itemView.findViewById(R.id.makeLogo);
            uploadNow = (TextView) itemView.findViewById(R.id.uploadNow);
        }
    }
}
