package com.gcloud.gaadi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.LeadsAdapter;
import com.gcloud.gaadi.constants.AdapterType;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.LeadDetailModel;
import com.gcloud.gaadi.model.LeadsModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;

import java.net.UnknownHostException;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by ankit on 7/1/15.
 */
public class CarLeadsActivity extends BaseActivity
        implements View.OnClickListener, OnNoInternetConnectionListener, AdapterView.OnItemClickListener {

    int pageNo = 1;
    String carid = "", modelversion = "";
    int make = 0;
    //    private GAHelper mGAHelper;
    //  private ActionBar mActionBar;
    private ListView listView;
    private RelativeLayout leadmakemodel;
    private ImageView makelogo;
    private TextView modelVersion;
    private LeadsAdapter leadsAdapter;
    private LayoutInflater mInflater;
    private View dummyView;
    private Button retry;
    private LinearLayout progressBar;
    private TextView errorMessage;
    private ImageView imageView;
    private FrameLayout layoutContainer, alternativeLayout;
    private HashMap<String, String> params = new HashMap<String, String>();
    private TextView refineFilter;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mGAHelper = new GAHelper(this);
        //   mActionBar = getSupportActionBar();
        //  mActionBar.setDisplayHomeAsUpEnabled(true);
        mInflater = getLayoutInflater();
        Bundle args = getIntent().getExtras();
        if ((args != null) && args.containsKey(Constants.CAR_ID)) {
            carid = args.getString(Constants.CAR_ID);
        }
        if ((args != null) && args.containsKey(Constants.MAKE)) {
            make = args.getInt(Constants.MAKE);
        }
        if ((args != null) && args.containsKey(Constants.MODELVERSION)) {
            modelversion = args.getString(Constants.MODELVERSION);
        }
        getLayoutInflater().inflate(R.layout.activity_place_holder, frameLayout);


        params.put(Constants.PAGE_NO, String.valueOf(pageNo));

        layoutContainer = (FrameLayout) findViewById(R.id.layoutContainer);
        /*listView = (ListView) rootView.findViewById(R.id.list);*/
        setInitialView();
        makeLeadsRequest(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_LEADS_AGAINST_STOCK);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (leadsAdapter != null) {
            leadsAdapter.resetCallInitiated();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_LEADS_AGAINST_STOCK,
                        Constants.CATEGORY_LEADS_AGAINST_STOCK,
                        Constants.ACTION_TAP,
                        Constants.LABEL_BACK_BUTTON,
                        0);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeLeadsRequest(final boolean showFullPageError) {
        params.put(Constants.LEAD_TYPE, "C");
        params.put(Constants.LEAD_SEARCH_STRING, carid);
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
        params.put(Constants.METHOD_LABEL, Constants.LEADS_METHOD);
        params.put(Constants.DEALER_PASSWORD, CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_PASSWORD, ""));
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_USERNAME, ""));
        RetrofitRequest.LeadsRequest(params, new Callback<LeadsModel>() {
            @Override
            public void success(LeadsModel leadsModel, retrofit.client.Response response) {
                if ("T".equalsIgnoreCase(leadsModel.getStatus())) {
                    if (leadsModel.getTotalRecords() > 0) {
                        layoutContainer.removeAllViews();
                        toolbar.setTitle(leadsModel.getTotalRecords() + " Leads");
                        View view = mInflater.inflate(R.layout.fragment_not_yet_called, null, false);
                        layoutContainer.addView(view);
                        lay_sub_tital.setVisibility(View.VISIBLE);
                        // make_logo.setImageResource(ApplicationController.makeLogoMap.get(make));
                        sub_tital.setText(modelversion);
                        listView = (ListView) layoutContainer.findViewById(R.id.list);
                        /* if(make!= 0) {
                        View header = getLayoutInflater().inflate(R.layout.listview_header, null);
                        listView.addHeaderView(header);
                        modelVersion = (TextView) header.findViewById(R.id.stockModelVersion);
                        leadmakemodel = (RelativeLayout) header.findViewById(R.id.leadmakemodel);
                        makelogo = (ImageView) header.findViewById(R.id.makeLogo);
                        modelVersion.setText(modelversion);
                        makelogo.setImageResource(ApplicationController.makeLogoMap.get(make));
                    }*/
                        leadsAdapter = new LeadsAdapter(CarLeadsActivity.this, leadsModel.getLeads(), AdapterType.CAR_LEADS);
                        leadsAdapter.notifyDataSetChanged();
                        listView.setAdapter(leadsAdapter);
                        listView.setOnItemClickListener(CarLeadsActivity.this);
                    } else {
                        showAddLeadLayout(showFullPageError);
                    }

                } else

                {
                    showServerErrorLayout(showFullPageError);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof UnknownHostException) {
                    showNetworkConnectionErrorLayout(showFullPageError);
                } else {
                    showServerErrorLayout(showFullPageError);
                }
            }

        });

    }

      /*  LeadsRequest leadsRequest = new LeadsRequest(this, Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<LeadsModel>() {
                    @Override
                    public void onResponse(LeadsModel response) {
                        GCLog.e("response model: " + response.toString());
                        if ("T".equalsIgnoreCase(response.getStatus())) {
                            if (response.getTotalRecords() > 0) {

                                layoutContainer.removeAllViews();
                                toolbar.setTitle(response.getTotalRecords() + " Leads");
                                View view = mInflater.inflate(R.layout.fragment_not_yet_called, null, false);
                                layoutContainer.addView(view);
                                lay_sub_tital.setVisibility(View.VISIBLE);
                                // make_logo.setImageResource(ApplicationController.makeLogoMap.get(make));
                                sub_tital.setText(modelversion);

                                listView = (ListView) layoutContainer.findViewById(R.id.list);
                                if(make!= 0) {
                                    View header = getLayoutInflater().inflate(R.layout.listview_header, null);
                                    listView.addHeaderView(header);
                                    modelVersion = (TextView) header.findViewById(R.id.stockModelVersion);
                                    leadmakemodel = (RelativeLayout) header.findViewById(R.id.leadmakemodel);
                                    makelogo = (ImageView) header.findViewById(R.id.makeLogo);
                                    modelVersion.setText(modelversion);
                                    makelogo.setImageResource(ApplicationController.makeLogoMap.get(make));
                                }
                                leadsAdapter = new LeadsAdapter(CarLeadsActivity.this, response.getLeads(), AdapterType.CAR_LEADS);
                                leadsAdapter.notifyDataSetChanged();
                                listView.setAdapter(leadsAdapter);
                                listView.setOnItemClickListener(CarLeadsActivity.this);

                            } else {
                                showAddLeadLayout(showFullPageError);
                            }
                        } else {
                            showServerErrorLayout(showFullPageError);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error.getCause() instanceof UnknownHostException) {
                            showNetworkConnectionErrorLayout(showFullPageError);
                        } else {
                            showServerErrorLayout(showFullPageError);
                        }
                    }
                });

        ApplicationController.getInstance().addToRequestQueue(leadsRequest, Constants.TAG_N_LEADS, showFullPageError, this);
    }*/

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


    private void showAddLeadLayout(boolean showFullPageError) {
        setInitialView();
        hideProgressBar();

        if (showFullPageError) {
            View view = mInflater.inflate(R.layout.layout_error, null, false);
            /*alternativeLayout.removeAllViews();*/
            alternativeLayout.addView(view);

            errorMessage = (TextView) view.findViewById(R.id.errorMessage);
            errorMessage.setText(R.string.no_lead_present);
            refineFilter = (TextView) view.findViewById(R.id.checkconnection);
           // refineFilter.setText(R.string.refine_filter);
            refineFilter.setVisibility(View.INVISIBLE);
            imageView = (ImageView) view.findViewById(R.id.no_internet_img);
            imageView.setImageResource(R.drawable.no_lead_icon);
            retry = (Button) view.findViewById(R.id.retry);
            retry.setTag("ADD_LEAD");
            retry.setText(R.string.add_lead);
            retry.setOnClickListener(this);

        } else {
            CommonUtils.showToast(this, getString(R.string.network_error), Toast.LENGTH_SHORT);
        }
    }


    private void showServerErrorLayout(boolean showFullPageError) {
        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                Constants.CATEGORY_VIEW_STOCK,
                Constants.CATEGORY_VIEW_STOCK,
                Constants.ACTION_TAP,
                Constants.LABEL_SERVER_ERROR,
                0);
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

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setInitialView() {
        layoutContainer.removeAllViews();
        dummyView = mInflater.inflate(R.layout.layout_loading_full_page, null, false);
        layoutContainer.addView(dummyView);
        alternativeLayout = (FrameLayout) dummyView.findViewById(R.id.alternativeLayout);
        progressBar = (LinearLayout) dummyView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void onNoInternetConnection(NetworkEvent networkEvent) {
        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                Constants.CATEGORY_VIEW_STOCK,
                Constants.CATEGORY_VIEW_STOCK,
                Constants.ACTION_TAP,
                Constants.LABEL_NO_INTERNET,
                0);
        //  ApplicationController.getInstance().cancelPendingRequests(Constants.TAG_N_LEADS);

        NetworkEvent.NetworkError networkError = networkEvent.getNetworkError();
        if (networkError == NetworkEvent.NetworkError.NO_INTERNET_CONNECTION) {
            showNetworkErrorLayout(networkEvent.isShowFullPageError());
        }
    }

    private void showNetworkErrorLayout(boolean showFullPageError) {
        setInitialView();
        hideProgressBar();

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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.retry:
                makeLeadsRequest(true);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            makeLeadsRequest(true);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle args = new Bundle();
        LeadDetailModel model = (LeadDetailModel) parent.getItemAtPosition(position);
        Intent intent = new Intent(CarLeadsActivity.this, LeadAddActivity.class);
        args.putString(Constants.VIEW_LEAD, Constants.VALUE_VIEWLEAD);
        args.putSerializable(Constants.MODEL_DATA, model);
        args.putString(Constants.CALL_SOURCE, "CL"); // Car Lead Activity
        intent.putExtras(args);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 100);
    }
}
