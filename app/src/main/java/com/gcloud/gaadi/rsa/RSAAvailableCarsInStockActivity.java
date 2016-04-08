package com.gcloud.gaadi.rsa;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.EndlessScroll.RecyclerViewEndlessScrollListener;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.rsa.RSAAdapters.RSACarsAdapter;
import com.gcloud.gaadi.rsa.RSAModel.RSACarDetailsModel;
import com.gcloud.gaadi.rsa.RSAModel.RSACarsModel;
import com.gcloud.gaadi.rsa.RSAModel.RSAPackage;
import com.gcloud.gaadi.ui.BaseActivity;
import com.gcloud.gaadi.ui.HotFixRecyclerView;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.squareup.otto.Subscribe;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ankitgarg on 20/05/15.
 */
public class RSAAvailableCarsInStockActivity extends BaseActivity implements View.OnClickListener {

    HashMap<String, String> params = new HashMap<>();
    private HotFixRecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private RelativeLayout errorLayout;
    private RSACarsAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<RSACarDetailsModel> mList;
    private ArrayList<RSAPackage> packageList;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_rsa, frameLayout);
//        mGAHelper = new GAHelper(this);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mRecyclerView = (HotFixRecyclerView) findViewById(R.id.recyclerView);
        errorLayout = (RelativeLayout) findViewById(R.id.errorLayout);
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        packageList = (ArrayList<RSAPackage>) getIntent().getExtras().getSerializable(Constants.RSA_PACKAGE);
        mProgressBar.setVisibility(View.VISIBLE);
        makeStocksForRSARequest(true, 1, String.valueOf(10));


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

    private void makeStocksForRSARequest(final boolean showFullPageError, final int pageNo, final String rpp) {
        params.put(Constants.PAGE_NO, pageNo + "");
        params.put(Constants.METHOD_LABEL, Constants.GET_RSA_STOCK_LIST_METHOD);
        params.put(Constants.RPP, rpp);
        RetrofitRequest.getRSAAvailableCars(params, new Callback<RSACarsModel>() {
            @Override
            public void success(final RSACarsModel rsaCarsModel, Response response) {
                if (null != rsaCarsModel) {
                    if ("T".equalsIgnoreCase(rsaCarsModel.getStatus())) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        errorLayout.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.GONE);
                        if (rsaCarsModel.getTotalRecords() != null && rsaCarsModel.getTotalRecords() != "") {
                            if (pageNo == 1) {
                                mList = rsaCarsModel.getStockData();
                                mAdapter = new RSACarsAdapter(RSAAvailableCarsInStockActivity.this, mList);
                                layoutManager = new LinearLayoutManager(RSAAvailableCarsInStockActivity.this);
                                mRecyclerView.setLayoutManager(layoutManager);
                                mRecyclerView.setAdapter(mAdapter);
                                //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                toolbar.setTitle(rsaCarsModel.getTotalRecords() + " Cars");
                                mRecyclerView.setOnScrollListener(new RecyclerViewEndlessScrollListener(
                                        layoutManager,
                                        Integer.parseInt(rsaCarsModel.getTotalRecords())) {
                                    @Override
                                    public void onLoadMore(int current_page) {
                                        if (rsaCarsModel.isHasNext()) {
                                            GCLog.e("current page: " + current_page);
                                            mProgressBar.setVisibility(View.VISIBLE);
                                            makeStocksForRSARequest(false, current_page, "10");
                                        }
                                    }
                                });
                            } else {
                                mList.addAll(rsaCarsModel.getStockData());
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {

                        if (pageNo == 1) {
                            mProgressBar.setVisibility(View.GONE);
                            errorLayout.setVisibility(View.VISIBLE);
                            errorLayout.findViewById(R.id.retry).setVisibility(View.GONE);
                            ((TextView) errorLayout.findViewById(R.id.errorMessage)).setText("No records found");

                        } else {
                            mProgressBar.setVisibility(View.GONE);
                        }

                    }
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayout.findViewById(R.id.retry).setVisibility(View.GONE);
                    ((TextView) errorLayout.findViewById(R.id.errorMessage)).setText("No records found");

                }


            }

            @Override
                public void failure(RetrofitError error) {

                    mProgressBar.setVisibility(View.GONE);
                    if (showFullPageError) {
                        errorLayout.setVisibility(View.VISIBLE);
                        if (error.getCause() instanceof UnknownHostException) {
                            //showNetworkConnectionErrorDialog();
                            ((TextView) errorLayout.findViewById(R.id.errorMessage))
                                    .setText(getString(R.string.network_connection_error_message));
                            findViewById(R.id.checkconnection).setVisibility(View.VISIBLE);

                            errorLayout.findViewById(R.id.retry).setTag(showFullPageError);
                            errorLayout.findViewById(R.id.retry).setOnClickListener(RSAAvailableCarsInStockActivity.this);
                        } else {

                            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                    Constants.CATEGORY_ISSUE_RSA,
                                    Constants.CATEGORY_ISSUE_RSA,
                                    error.getKind().name(),
                                    error.getMessage(),
                                    0);


                            ((TextView) errorLayout.findViewById(R.id.errorMessage))
                                    .setText(getString(R.string.server_error_message));

                            errorLayout.findViewById(R.id.retry).setTag(showFullPageError);
                            errorLayout.findViewById(R.id.retry).setOnClickListener(RSAAvailableCarsInStockActivity.this);


                        }

                        //showServerErrorDialog();

                    } else {

                        CommonUtils.showErrorToast(RSAAvailableCarsInStockActivity.this, error, Toast.LENGTH_SHORT);

                    }
                    //GCLog.e(Constants.TAG, "error: " + error.getMessage());
                }
            });

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.retry:
                //boolean showFullPageError = (boolean) v.getTag();
                makeStocksForRSARequest(true, 1, "10");
                break;
        }
    }


    @Subscribe
    public void onCarSelectedEvent(RSACarSelectedEvent event) {

        ApplicationController.getInstance().getGAHelper().sendEvent(
                GAHelper.TrackerName.APP_TRACKER,
                Constants.CATEGORY_RSA_INSPECTED_CARS,
                Constants.CATEGORY_RSA_INSPECTED_CARS,
                Constants.ACTION_TAP,
                Constants.LABEL_ISSUE_RSA_STARTED + " - " + Constants.RSA_TAB,
                0
        );

        try {
            CommonUtils.logRSAEvent(
                    event.getCarData().getStockId(),
                    Constants.RSA_TAB,
                    Constants.LABEL_ISSUE_RSA_STARTED);

        } catch (Exception e) {
            GCLog.e("exception: " + e.getMessage());
        }

        Intent intent = new Intent(this, RSACustomerInfoActivity.class);
        intent.putExtra(Constants.SOURCE, Constants.RSA_TAB);
        intent.putExtra(Constants.RSA_PACKAGE, packageList);
        intent.putExtra(Constants.MODEL_DATA, event.getCarData());
        intent.putExtra(Constants.RSA_FLAG, false);
        startActivityForResult(intent, Constants.RSA_CAR_SELECTED_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.RSA_CAR_SELECTED_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    RSAAvailableCarsInStockActivity.this.finish();

                }
                break;
        }

    }
    @Override
    protected void onStart() {

        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_BUYER_LEAD_DETAIL);
    }
}
