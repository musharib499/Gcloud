package com.gcloud.gaadi.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.StockImagesAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.ViewCertificationCarsWarrantyInput;
import com.gcloud.gaadi.model.WarrantyDetailModel;
import com.gcloud.gaadi.model.WarrantyDetailsData;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.viewpagerindicator.CirclePageIndicator;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;


public class IssueWarrantyDetailPageActivity extends AppCompatActivity implements
        View.OnClickListener, OnNoInternetConnectionListener {
    CollapsingToolbarLayout collapsingToolbar;
    Toolbar toolbar;
    // private ActionBar mActionBar;
//    private GAHelper gaHelper;
    private int selectedIndex = -1;
    private TextView errorMessage;
    private RelativeLayout sendSMS, sendWhatsapp, sendEmail, addLead;
    private ImageView stockColor, share;
    private CirclePageIndicator circlePageIndicator;
    private String dealerUsername, ucdid, warrantyId;
    private FrameLayout layoutContainer, alternativeLayout;
    private LayoutInflater mInflater;
    private View dummyView;
    private Button retry;
    private LinearLayout progressBar;
    private WarrantyDetailsData warrantyDetailModel;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_place_holder);
        mInflater = getLayoutInflater();

//        gaHelper = new GAHelper(this);
        //  mActionBar = getSupportActionBar();
        //mActionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            warrantyId = extras.getString(Constants.CAR_ID);
        }


        layoutContainer = (FrameLayout) findViewById(R.id.layoutContainer);

        setInitialView();

        makeWarrantyDetailRequest(true);

    }

    private void setInitialView() {
        layoutContainer.removeAllViews();
        dummyView = mInflater.inflate(R.layout.layout_loading_full_page, null, false);
        layoutContainer.addView(dummyView);
        alternativeLayout = (FrameLayout) dummyView.findViewById(R.id.alternativeLayout);
        progressBar = (LinearLayout) dummyView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

    }

    private void showNetworkErrorLayout(boolean showFullPageError) {
        setInitialView();
        hideProgressBar();
        /*alternativeLayout.removeAllViews();*/
        if (showFullPageError) {
            View view = mInflater.inflate(R.layout.layout_error, null, false);

            alternativeLayout.addView(view);

            errorMessage = (TextView) view.findViewById(R.id.errorMessage);
            errorMessage.setText(R.string.network_error);

            retry = (Button) view.findViewById(R.id.retry);
            retry.setOnClickListener(this);

        } else {
            CommonUtils.showToast(this, getString(R.string.network_error), Toast.LENGTH_SHORT);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
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

    private void makeWarrantyDetailRequest(final boolean fullPageError) {

        HashMap<String, String> params = new HashMap<String, String>();
        ViewCertificationCarsWarrantyInput certcarsWarrantyInput = new ViewCertificationCarsWarrantyInput();
        certcarsWarrantyInput.setApikey(Constants.API_KEY);
        certcarsWarrantyInput.setMethod(Constants.GETWARRANTYDETAIL_METHOD);
        certcarsWarrantyInput.setOutput(Constants.API_RESPONSE_FORMAT);
        certcarsWarrantyInput.setUsername(CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_USERNAME, ""));
        certcarsWarrantyInput.setNormal_password(CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_PASSWORD, ""));
        certcarsWarrantyInput.setWarrantyID(warrantyId);
        Gson gson = new Gson();

        params.put(Constants.EVALUATIONDATA, gson.toJson(certcarsWarrantyInput,
                ViewCertificationCarsWarrantyInput.class));
        JSONObject jsonObject = null;
        try {
             jsonObject = new JSONObject(gson.toJson(certcarsWarrantyInput,
                    ViewCertificationCarsWarrantyInput.class));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Inputparameter",params+"");
        RetrofitRequest.WarrantyDetailRequest(jsonObject, new Callback<WarrantyDetailModel>() {
            @Override
            public void success(WarrantyDetailModel viewCertifiedCarModel, retrofit.client.Response response) {

                if (viewCertifiedCarModel.getStatus().equals("T")) {
                    GCLog.e("Stock view response : " + response.toString());
                    layoutContainer.removeAllViews();
                    View view = mInflater.inflate(R.layout.activity_issued_warranty_detail_page, null, false);
                    layoutContainer.addView(view);
                    warrantyDetailModel = viewCertifiedCarModel.getWarrantyData();
                    // String tital=warrantyDetailModel.getModel() + " " + warrantyDetailModel.getVersion();
                    // if (!tital.equals("")) {
                    //toolbar.setLogo(ApplicationController.makeLogoMap.get(warrantyDetailModel.getMake_id()));
                    initializeViews(layoutContainer);
                    toolbar.setTitle(warrantyDetailModel.getModel() + " " + warrantyDetailModel.getVersion());
                    //}


                } else {
                    showServerErrorLayout(fullPageError);
                }
            }

            @Override
            public void failure(RetrofitError error) {

                if (error.getCause() instanceof UnknownHostException) {

                    showNetworkConnectionErrorLayout(fullPageError);

                } else {

                    showServerErrorLayout(fullPageError);
                }

            }

        });
    }
       /* WarrantyDetailRequest stockDetailRequest = new WarrantyDetailRequest(
                this,
                Request.Method.POST,
                Constants.getWarrantyWebServiceURL(this),
                params,
                new Response.Listener<WarrantyDetailModel>() {
                    @Override
                    public void onResponse(WarrantyDetailModel response) {
                        if (response.getStatus().equals("T")) {
                            GCLog.e("Stock view response : " + response.toString());
                            layoutContainer.removeAllViews();
                            View view = mInflater.inflate(R.layout.activity_issued_warranty_detail_page, null, false);
                            layoutContainer.addView(view);
                            warrantyDetailModel = response.getWarrantyData();
                            // String tital=warrantyDetailModel.getModel() + " " + warrantyDetailModel.getVersion();
                            // if (!tital.equals("")) {
                            //toolbar.setLogo(ApplicationController.makeLogoMap.get(warrantyDetailModel.getMake_id()));
                            initializeViews(layoutContainer);
                            toolbar.setTitle(warrantyDetailModel.getModel() + " " + warrantyDetailModel.getVersion());
                            //}


                        } else {
                            showServerErrorLayout(fullPageError);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error.getCause() instanceof UnknownHostException) {

                            showNetworkConnectionErrorLayout(fullPageError);

                        } else {

                            showServerErrorLayout(fullPageError);
                        }
                    }
                });

        ApplicationController.getInstance().addToRequestQueue(stockDetailRequest, Constants.TAG_STOCK_DETAIL, fullPageError, this);
*/



    private void showNetworkConnectionErrorLayout(boolean fullPageError) {
        setInitialView();
        hideProgressBar();
        /*alternativeLayout.removeAllViews();*/
        if (fullPageError) {
            View view = mInflater.inflate(R.layout.layout_error, null, false);

            alternativeLayout.addView(view);

            errorMessage = (TextView) view.findViewById(R.id.errorMessage);
            errorMessage.setText(R.string.network_connection_error_message);

            retry = (Button) view.findViewById(R.id.retry);
            retry.setOnClickListener(this);

        } else {
            CommonUtils.showToast(this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
        }
    }


    private void initializeViews(View parentView) {
        toolbar = (Toolbar) parentView.findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // mToolbarView.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu_back, null));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(warrantyDetailModel.getModel() + " " + warrantyDetailModel.getVersion());

        //  Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), ApplicationController.makeLogoMap.get(warrantyDetailModel.getMake_id()));

       /* View view_toolbar=getLayoutInflater().inflate(R.layout.custom_actionbar_stock_detail,null);
        //mActionBar.setCustomView(R.layout.custom_actionbar_stock_detail);
        //mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ImageView stockMake = (ImageView) view_toolbar.findViewById(R.id.stockMake);
        stockMake.setVisibility(View.VISIBLE);
        stockMake.setImageResource(ApplicationController.makeLogoMap.get(warrantyDetailModel.getMake_id()));
        TextView stockModelVersion = (TextView) view_toolbar.findViewById(R.id.stockModelVersion);
        stockModelVersion.setText(warrantyDetailModel.getModel() + " " + warrantyDetailModel.getVersion());
       toolbar.addView(view_toolbar);
       */ //mActionBar.setDisplayHomeAsUpEnabled(true);
        TextView warrantyId = (TextView) findViewById(R.id.warrantyId);
        warrantyId.setText(warrantyDetailModel.getWarranty_id());

        TextView name = (TextView) findViewById(R.id.name);
        name.setText(warrantyDetailModel.getCust_name());
        TextView reg_no = (TextView) findViewById(R.id.reg_no);
        reg_no.setText("Reg No. " + warrantyDetailModel.getReg_no());

       /* TextView mobile = (TextView) findViewById(R.id.mobile);
        mobile.setText(warrantyDetailModel.getCust_mobile());*/

        TextView email = (TextView) findViewById(R.id.email);
        email.setText(warrantyDetailModel.getCust_email());
        Button btn_call = (Button) findViewById(R.id.btn_callNow);
        btn_call.setOnClickListener(this);

        TextView address = (TextView) findViewById(R.id.address);
        address.setText(warrantyDetailModel.getCust_address());
        TextView warrenty_type = (TextView) findViewById(R.id.warrantyType);
        warrenty_type.setText(warrantyDetailModel.getWarrantyTypeName());
        TextView od_meter = (TextView) findViewById(R.id.odMetter);
        od_meter.setText(warrantyDetailModel.getOdometerReading());

    /*    TextView vehiclesaledate = (TextView) findViewById(R.id.vehiclesaledate);
        vehiclesaledate.setText(getFormattedDate(warrantyDetailModel.getVehicle_sale_date()));*/


        TextView tvwarrantyexpireson = (TextView) findViewById(R.id.tvwarrantyexpireson);
        tvwarrantyexpireson.setText(getWarrantyStartEndDate(warrantyDetailModel.getWarranty_end_date()));

/*
        TextView chasis = (TextView) findViewById(R.id.chasis);
        chasis.setText(warrantyDetailModel.getChassis_no());


        TextView engineno = (TextView) findViewById(R.id.engineno);
        engineno.setText(warrantyDetailModel.getEngine_no());

        TextView regno = (TextView) findViewById(R.id.regno);
        regno.setText(warrantyDetailModel.getReg_no());
*/


        TextView warrantyStartDate = (TextView) findViewById(R.id.warrantyStartDate);
        warrantyStartDate.setText(getWarrantyStartEndDate(warrantyDetailModel.getVehicle_sale_date().split("\\s")[0]));

    /*    TextView warrantyend = (TextView) findViewById(R.id.warrantyend);
        warrantyend.setText(getFormattedDate(warrantyDetailModel.getWarranty_end_date()));*/

        ViewPager imagesPager = (ViewPager) findViewById(R.id.imagesPager);
        StockImagesAdapter imagesAdapter = new StockImagesAdapter(this, warrantyDetailModel.getArrayImages());
        imagesPager.setAdapter(imagesAdapter);
        imagesPager.setOffscreenPageLimit(2);

        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.circlePagerIndicator);
        circlePageIndicator.setViewPager(imagesPager);
    }

    private void showServerErrorLayout(boolean showFullPageError) {
        setInitialView();
        hideProgressBar();

        if (showFullPageError) {
            View view = mInflater.inflate(R.layout.layout_error, null, false);
            /*alternativeLayout.removeAllViews();*/
            alternativeLayout.addView(view);

            errorMessage = (TextView) view.findViewById(R.id.errorMessage);
            errorMessage.setText(R.string.no_internet_connection);

            retry = (Button) view.findViewById(R.id.retry);
            retry.setOnClickListener(this);

        } else {
            CommonUtils.showToast(this, getString(R.string.network_error), Toast.LENGTH_SHORT);
        }
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
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_BUYER_LEAD_DETAIL);
    }

    private String getWarrantyStartEndDate(String date) {
        if(date != null && !(date.equals(""))) {
            String formattedDate = date.split("-")[2] + " " + ApplicationController.monthShortMap.get(date.split("-")[1]) + " " + date.split("-")[0];
            return formattedDate;
        }
        else
            return "";
    }


    private String getFormattedDate(String date) {
        String formattedDate = date.split("-")[2] + ApplicationController.monthShortMap.get(date.split("-")[1]) + "," + date.split("-")[0];


        return formattedDate;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.stock_detail_menu, menu);
        return true;
    }

    private void logUserEvent(String stockId, String customerMobile, ShareType shareType) {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.SHARE_SOURCE, Constants.CATEGORY_ISSUED_WARRANTY);
        params.put(Constants.MOBILE_NUM, customerMobile);
        params.put(Constants.SHARE_TYPE, shareType.name());
        params.put(Constants.CAR_ID, stockId);
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
        params.put(Constants.METHOD_LABEL, Constants.SENT_CARS_METHOD);
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(getApplicationContext(), Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.DEALER_PASSWORD, CommonUtils.getStringSharedPreference(getApplicationContext(), Constants.UC_DEALER_PASSWORD, ""));


        RetrofitRequest.shareCarsRequest(getApplicationContext(), params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, retrofit.client.Response response) {

            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof UnknownHostException) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_ISSUED_WARRANTY,
                            Constants.CATEGORY_ISSUED_WARRANTY,
                            Constants.ACTION_TAP,
                            Constants.LABEL_NO_INTERNET,
                            0);

                } else {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_ISSUED_WARRANTY,
                            Constants.CATEGORY_ISSUED_WARRANTY,
                            Constants.CATEGORY_IO_ERROR,
                            error.getMessage(),
                            0);
                }

            }
        });

     /*   ShareCarsRequest shareCarsRequest = new ShareCarsRequest(this,
                Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<GeneralResponse>() {
                    @Override
                    public void onResponse(GeneralResponse response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getCause() instanceof UnknownHostException) {
                            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                    Constants.CATEGORY_ISSUED_WARRANTY,
                                    Constants.CATEGORY_ISSUED_WARRANTY,
                                    Constants.ACTION_TAP,
                                    Constants.LABEL_NO_INTERNET,
                                    0);

                        } else {
                            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                    Constants.CATEGORY_ISSUED_WARRANTY,
                                    Constants.CATEGORY_ISSUED_WARRANTY,
                                    Constants.CATEGORY_IO_ERROR,
                                    error.getMessage(),
                                    0);
                        }

                    }
                });

        ApplicationController.getInstance().addToRequestQueue(shareCarsRequest);*/
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        Bundle bundle = new Bundle();
        switch (v.getId()) {


            case R.id.retry:
                // TODO can track server error from here with tags.
                setInitialView();
                makeWarrantyDetailRequest(true);
                break;
            case R.id.btn_callNow:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_ISSUED_WARRANTY,
                        Constants.CATEGORY_ISSUED_WARRANTY,
                        Constants.ACTION_TAP,
                        Constants.LABEL_ISSUED_WARRANTY_CALL_BUTTON,
                        0);
                String number = warrantyDetailModel.getCust_mobile();

                logUserEvent(warrantyId, warrantyDetailModel.getCust_mobile(), ShareType.CALL);

                if ((number != null) && !number.isEmpty() && !"null".equalsIgnoreCase(number)) {
                    if (CommonUtils.checkForPermission(this,
                            new String[]{Manifest.permission.CALL_PHONE}, Constants.REQUEST_PERMISSION_CALL, "Phone")) {
                        intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:+91" + number));

                        startActivity(intent);
                    }

                }
                break;

            /*case R.id.share:

                *//*showSharePopup(v);*//*

                SharePopupWindow sharePopupWindow = new SharePopupWindow(this, R.layout.layout_share);
                sharePopupWindow.setShareText(stockDetailModel.getShareText());
                sharePopupWindow.show(mActionBar.getCustomView());

                break;*/
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Intent intent;
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            intent = new Intent(Intent.ACTION_CALL);
        } else {
            intent = new Intent(Intent.ACTION_DIAL);
        }
        intent.setData(Uri.parse("tel:+91" + warrantyDetailModel.getCust_mobile()));
        startActivity(intent);

    }

    @Override
    public void onNoInternetConnection(NetworkEvent networkEvent) {

       // ApplicationController.getInstance().cancelPendingRequests(Constants.TAG_STOCK_DETAIL);

        NetworkEvent.NetworkError networkError = networkEvent.getNetworkError();
        if (networkError == NetworkEvent.NetworkError.NO_INTERNET_CONNECTION) {
            showNetworkErrorLayout(networkEvent.isShowFullPageError());
        }
    }


}
