package com.gcloud.gaadi.ui.Finance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.EndlessScroll.RecyclerViewEndlessScrollListener;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.StocksFinanceAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.ApplicationCompleteEvent;
import com.gcloud.gaadi.model.AvailableCarsModel;
import com.gcloud.gaadi.model.CarItemModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.HotFixRecyclerView;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

public class AvailableCarsActivity extends FinanceToolbarActivity implements View.OnClickListener {

    public static long startTime; //Used for GA
    public static long totalTimeOnAvailableCarsActivity;
    public static long leaveTime;
    protected static int check_id_item = R.id.sort_by_available_cars, isAvailable = 1;
    RadioGroup radioGroup;
    EditText editText;
    HashMap<String, String> params = new HashMap<>();
    LinearLayout linearMenuLayout, lay_menu_container;
    boolean flag = false;
    private ProgressBar mProgressBar;
    private HotFixRecyclerView mRecyclerView;
    private StocksFinanceAdapter mAdapter;
    private ArrayList<CarItemModel> mList;
    private LinearLayoutManager layoutManager;
    private LayoutInflater mInflater;
    private boolean checkMenu = false,checkToggle = false;

    @Override
    protected void onResume() {
        super.onResume();
        GCLog.e("register Available");
//        ApplicationController.getEventBus().register(this);
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_FINANCE_INSPECTED_CARS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GCLog.e("register Available");
        ApplicationController.getEventBus().unregister(this);
    }

    @Subscribe
    public void onApplicationCompleted(ApplicationCompleteEvent event) {
        GCLog.e("finish availablecarsactivity");
        finish();
    }

    @Override
    public void onBackPressed() {
        check_id_item = 0;
        isAvailable = 1;
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.finance_available_cars_menu, menu);
        return true;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_available_cars, myFrameLayout);
        FinanceToolbarActivity.setStepProgress(1);
        mInflater = getLayoutInflater();
        ApplicationController.getEventBus().register(this);

        toolbar.setTitle(Constants.AVAILAIBLE_CARS_TITLE);
        lay_menu_container = (LinearLayout) findViewById(R.id.lay_menu_container);
        lay_menu_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenu();
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mRecyclerView = (HotFixRecyclerView) findViewById(R.id.recyclerView);

        mRecyclerView.setVisibility(View.GONE);

        setTitle("Select Car");
        makeStocksForFinanceRequest(1);
        Calendar calender = Calendar.getInstance();
        startTime = calender.getTimeInMillis();
    }

    private void makeStocksForFinanceRequest(final int pageNo) {

        params.put(Constants.PAGE_NUMBER, pageNo + "");
        params.put(Constants.IS_AVAILABLE, isAvailable+"");

        RetrofitRequest.getFinanceAvailableCars(params, new Callback<AvailableCarsModel>() {
            @Override
            public void success(final AvailableCarsModel availableCarsModel, retrofit.client.Response response) {
                if ("T".equalsIgnoreCase(availableCarsModel.getStatus())) {
                    GCLog.e(Constants.TAG, "Success Available Cars");
                    findViewById(R.id.error_layout).setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
//                    toolbar.setTitle(Constants.AVAILAIBLE_CARS_TITLE + " (" + availableCarsModel.getCarCount() + ")");
                    if (pageNo == 1) {
                        if (availableCarsModel.getStocks().size() == 0) {
                            showNoRecordMessage(true, true);

                        } else {
                            // mList.clear();
                            mList = availableCarsModel.getStocks();
                            checkMenu = true;
                            mAdapter = new StocksFinanceAdapter(availableCarsModel.getStocks());

                            mRecyclerView.setAdapter(mAdapter);
                            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            layoutManager = new LinearLayoutManager(AvailableCarsActivity.this);
                            mRecyclerView.setLayoutManager(layoutManager);
                            mRecyclerView.setOnScrollListener(new RecyclerViewEndlessScrollListener(
                                    layoutManager,
                                    availableCarsModel.getTotalRecords()) {
                                @Override
                                public void onLoadMore(int current_page) {
                                    if (availableCarsModel.isHasNext() && !flag) {
                                        GCLog.e("current page: " + current_page);
                                        mProgressBar.setVisibility(View.VISIBLE);
                                        makeStocksForFinanceRequest(current_page);
                                    }

                                }
                            });
                        }

                    } else {
                        mList.addAll(availableCarsModel.getStocks());
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                showNoRecordMessage(true, false);
//                String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
//                GCLog.e("failure", json.toString());
//                GCLog.e(Constants.TAG, "Retrofit error");
//                if (pageNo == 1 && mList == null)
//                    tvMessage.setText("Some Error !!");
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showNoRecordMessage(boolean showFullPageError, boolean isSuccess) {

        if (showFullPageError == true) {
            View view = findViewById(R.id.error_layout);
            view.setVisibility(View.VISIBLE);
            view.setClickable(true);
            view.setVisibility(View.VISIBLE);
            TextView errorMessage = (TextView) view.findViewById(R.id.errorMessage);
            TextView checkconnection = (TextView) view.findViewById(R.id.checkconnection);
            ImageView imageView = (ImageView) view.findViewById(R.id.no_internet_img);

            Button retry = (Button) view.findViewById(R.id.retry);
            if (isSuccess) {
                retry.setVisibility(View.GONE);
                imageView.setImageResource(R.drawable.no_result_icons);
                checkconnection.setVisibility(View.INVISIBLE);
                errorMessage.setText("No records found");
            } else {
                retry.setVisibility(View.VISIBLE);
                retry.setOnClickListener(this);
                imageView.setImageResource(R.drawable.no_internet_icons);
                checkconnection.setVisibility(View.VISIBLE);
                errorMessage.setText(getResources().getString(R.string.network_error));
            }
        }

    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        //Toast.makeText(this, "Closed", Toast.LENGTH_SHORT).show();
        Calendar calender = Calendar.getInstance();
        leaveTime = calender.getTimeInMillis();
        totalTimeOnAvailableCarsActivity = totalTimeOnAvailableCarsActivity + (leaveTime - startTime);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_filter_menu:
                findViewById(R.id.error_layout).setVisibility(View.GONE);
                menuToggle();
                break;
            case R.id.action_searchByRegNo_menu:
                showSearchByRegNoDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSearchByRegNoDialog() {
        flag = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(AvailableCarsActivity.this);
        final View view = this.getLayoutInflater().inflate(R.layout.layout_search_finance_search_car, null, false);
        editText = (EditText) view.findViewById(R.id.editText);
        builder.setView(view);
        AlertDialog dialog = builder.setPositiveButton(R.string.done_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if (editText.getText().toString().trim().length() == 0) {
                            CommonUtils.showToast(AvailableCarsActivity.this, "Please enter a Registration number to search", Toast.LENGTH_SHORT);

                        } else if (editText.getText().toString().trim().replace(" ", "").matches("[0-9a-zA-Z]+")) {
                            searchByRegNo(editText.getText().toString().trim().replace(" ", ""));

                        } else {
                            CommonUtils.showToast(AvailableCarsActivity.this, "Please enter a valid Registration number to search", Toast.LENGTH_SHORT);
                            flag = true;

                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        if (!this.isFinishing() && !dialog.isShowing()) {
            dialog.show();

        }

    }

    private void searchByRegNo(String regNum) {
        params.put(Constants.REG_NO, regNum);
        if (mProgressBar != null && mProgressBar.getVisibility() == View.GONE)
            mProgressBar.setVisibility(View.VISIBLE);
        makeStocksForFinanceRequest(1);
    }

    public void menuToggle()
    {
        if (checkMenu)
        {
            if (checkToggle)
            {
                hideMenu();

            } else {
                showMenu();
            }
        }
    }

    public void hideMenu()
    {
        if (checkMenu)
        {
            if (lay_menu_container.getVisibility() == View.VISIBLE)
            {
                Animation animation= AnimationUtils.loadAnimation(AvailableCarsActivity.this, R.anim.zoom_in);
                animation.setFillAfter(true);
                mRecyclerView.startAnimation(animation);

                lay_menu_container.setClickable(false);

                Animation animation1= AnimationUtils.loadAnimation(AvailableCarsActivity.this,R.anim.stock_menu_up_translate);
                animation.setFillAfter(true);
                linearMenuLayout.startAnimation(animation1);
                animation1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        lay_menu_container.setVisibility(View.GONE);
                        lay_menu_container.removeAllViews();
                        //    new StocksActivity().fabShow();


                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }
            checkToggle = false;

        }
    }

    public void showMenu()
    {
        if (checkMenu)
        {
            if (lay_menu_container.getVisibility() != View.VISIBLE)
            {

                lay_menu_container.setClickable(true);
                lay_menu_container.setVisibility(View.VISIBLE);

                Animation animation= AnimationUtils.loadAnimation(AvailableCarsActivity.this, R.anim.zoom_out);
                mRecyclerView.startAnimation(animation);



                initializationMenuView();

            }


            checkToggle = true;


        }
    }

    public void initializationMenuView()
    {
        View view = mInflater.inflate(R.layout.finance_filter_menu, null, false);
        linearMenuLayout=(LinearLayout)view.findViewById(R.id.lay_menu_view);

        radioGroup= (RadioGroup) view.findViewById(R.id.radio_menu);
        Animation animation2= AnimationUtils.loadAnimation(AvailableCarsActivity.this, R.anim.stock_menu_down_translate);
        linearMenuLayout.setAnimation(animation2);


        if (AvailableCarsActivity.check_id_item != 0) {
            RadioButton rb = (RadioButton) radioGroup.findViewById(AvailableCarsActivity.check_id_item);
            radioGroup.check(AvailableCarsActivity.check_id_item);
            rb.setTextColor(getResources().getColor(R.color.primary));
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                boolean switchExecuted = false;
                  switch (checkedId) {
                    case R.id.sort_by_available_cars:
                        switchExecuted = true;
                        isAvailable = 1;
                        break;

                    case R.id.sort_by_removed_cars:
                        switchExecuted = true;
                        isAvailable = 0;
                        break;

                }
                AvailableCarsActivity.check_id_item = checkedId;
                if (switchExecuted) {
                    hideMenu();

                    if (mProgressBar != null && mProgressBar.getVisibility() == View.GONE)
                        mProgressBar.setVisibility(View.VISIBLE);
                    params.remove(Constants.REG_NO);
                    makeStocksForFinanceRequest(1);


                }

            }



    }

            );

            lay_menu_container.addView(view);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        hideMenu();

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retry:
                if (mProgressBar != null && mProgressBar.getVisibility() == View.GONE)
                    mProgressBar.setVisibility(View.VISIBLE);
                makeStocksForFinanceRequest(1);
                break;
        }
    }
}
