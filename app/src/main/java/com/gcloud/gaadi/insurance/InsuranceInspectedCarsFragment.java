package com.gcloud.gaadi.insurance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.EndlessScroll.RecyclerViewEndlessScrollListener;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.InsuranceInspectedCarsAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.InspectedCarsInsuranceModel;
import com.gcloud.gaadi.model.InsuranceInspectedCarData;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.HotFixRecyclerView;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

public class InsuranceInspectedCarsFragment extends Fragment
        implements View.OnClickListener, OnNoInternetConnectionListener {

    int pageNo = 1;
    int selectedIndex = 0;
    private View rootView;
    private Activity activity;
    private LayoutInflater mInflater;
    private View dummyView;
    private Button retry;
    private LinearLayout progressBar;
    //    private GAHelper mGAHelper;
    private TextView errorMessage;
    private FrameLayout layoutContainer, alternativeLayout;
    private ArrayList<InsuranceInspectedCarData> insuranceInspectedCars;
    private ArrayList<String> mInsuranceCities;
    private String mAgentId = "";
    private InsuranceInspectedCarsAdapter ca;
    private HotFixRecyclerView recList;
    private LinearLayoutManager llm;
    private TextView refineFilter;
    private ImageView img;

    private Activity getFragmentActivity() {
        return activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationController.getEventBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ApplicationController.getEventBus().unregister(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
//        mGAHelper = new GAHelper(getFragmentActivity());
        mInflater = activity.getLayoutInflater();
    }


    private void makeInsuranceInspectedCarsRequest(final boolean showFullPageError, final int pageNo) {

        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.METHOD_LABEL, Constants.INSPECTED_CARS_FOR_INSURANCE);
        params.put(Constants.PAGE_NO, String.valueOf(pageNo));


        RetrofitRequest.getInspectedCarsForInsurance(
                params, new Callback<InspectedCarsInsuranceModel>() {

                    @Override
                    public void success(InspectedCarsInsuranceModel inspectedCarsInsuranceModel, retrofit.client.Response response) {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }

                        if ("T".equalsIgnoreCase(inspectedCarsInsuranceModel.getStatus())) {
                            if (inspectedCarsInsuranceModel.getTotalRecords() > 0) {
                                GCLog.e("response list: " + inspectedCarsInsuranceModel.getInspectedCars());
                                if (pageNo == 1) {
                                    initializeList(inspectedCarsInsuranceModel);

                                } else {
                                    insuranceInspectedCars.addAll(inspectedCarsInsuranceModel.getInspectedCars());
                                    ca.notifyDataSetChanged();
                                }


                            } else {
                                // count mismatch for total records
                                showNoRecordsMessage(showFullPageError, inspectedCarsInsuranceModel.getError());
                            }
                        } else {
                            showNoRecordsMessage(showFullPageError, inspectedCarsInsuranceModel.getError());

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        switch (error.getKind()) {
                            case NETWORK:
                                showNetworkConnectionErrorLayout(showFullPageError);
                                break;

                            case UNEXPECTED:
                                showServerErrorLayout(showFullPageError);
                                break;

                            case HTTP:
                                showServerErrorLayout(showFullPageError);
                                break;

                            case CONVERSION:
                                showNoRecordsMessage(showFullPageError, "Application Error.");
                                break;
                        }
                        //CommonUtils.showErrorToast(getFragmentActivity(), error, Toast.LENGTH_SHORT);

                    }
                });

    }

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
            CommonUtils.showToast(getFragmentActivity(), getFragmentActivity().getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
        }
    }


    private void initializeList(InspectedCarsInsuranceModel response) {
        insuranceInspectedCars = response.getInspectedCars();
        layoutContainer.removeAllViews();
        View view = mInflater.inflate(R.layout.insurance_inspected_cars, null, false);
        layoutContainer.addView(view);
        recList = (HotFixRecyclerView) layoutContainer.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        llm = new LinearLayoutManager(getFragmentActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        ca = new InsuranceInspectedCarsAdapter(getFragmentActivity(),
                insuranceInspectedCars, mInsuranceCities, mAgentId);
        recList.setAdapter(ca);

        recList.setOnScrollListener(new RecyclerViewEndlessScrollListener(llm, response.getTotalRecords()) {
            @Override
            public void onLoadMore(int nextPageNo) {
                makeInsuranceInspectedCarsRequest(false, nextPageNo);
            }
        });

    }


    private void showNoRecordsMessage(boolean showFullPageError, String message) {
        if (showFullPageError) {

            setInitialView();
            hideProgressBar();

            View view = mInflater.inflate(R.layout.layout_error, null, false);

            refineFilter = (TextView) view.findViewById(R.id.checkconnection);
          //  refineFilter.setText(R.string.refine_filter);
            refineFilter.setVisibility(View.INVISIBLE);
            img = (ImageView) view.findViewById(R.id.no_internet_img);

            img.setImageResource(R.drawable.no_result_icons);

            alternativeLayout.addView(view);

            errorMessage = (TextView) view.findViewById(R.id.errorMessage);
            errorMessage.setText(message);

            retry = (Button) view.findViewById(R.id.retry);
            retry.setVisibility(View.GONE);

        } else {
            CommonUtils.showToast(getFragmentActivity(), "No more records found", Toast.LENGTH_SHORT);

        }
        // retry.setTag("ADD STOCK");

        //retry.setOnClickListener(this);

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
            CommonUtils.showToast(getFragmentActivity(), getFragmentActivity().getString(R.string.server_error), Toast.LENGTH_SHORT);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //params.put(Constants.PAGE_NO, String.valueOf(pageNo));

        rootView = getFragmentActivity().getLayoutInflater().inflate(R.layout.activity_place_holder, container, false);
        layoutContainer = (FrameLayout) rootView.findViewById(R.id.layoutContainer);

        Bundle args = getArguments();
        if (args == null) {
            if (savedInstanceState != null) {
                mInsuranceCities = savedInstanceState.getStringArrayList(Constants.INSURANCE_CITIES);
                mAgentId = savedInstanceState.getString(Constants.AGENT_ID);
            }

        } else {
            mInsuranceCities = args.getStringArrayList(Constants.INSURANCE_CITIES);
            mAgentId = args.getString(Constants.AGENT_ID);

        }

        setInitialView();
        makeInsuranceInspectedCarsRequest(true, 1);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(Constants.INSURANCE_CITIES, mInsuranceCities);
        outState.putString(Constants.AGENT_ID, mAgentId);

    }

    private void setInitialView() {
        layoutContainer.removeAllViews();
        dummyView = mInflater.inflate(R.layout.layout_loading_full_page, null, false);
        layoutContainer.addView(dummyView);
        alternativeLayout = (FrameLayout) dummyView.findViewById(R.id.alternativeLayout);
        progressBar = (LinearLayout) dummyView.findViewById(R.id.progressBar);
        if (progressBar.getVisibility() != View.VISIBLE)
            progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void onNoInternetConnection(NetworkEvent networkEvent) {
       // ApplicationController.getInstance().cancelPendingRequests(Constants.LABEL_INSURANCE_POLICY);

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
            CommonUtils.showToast(getFragmentActivity(), getFragmentActivity().getString(R.string.network_error), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.retry:

                setInitialView();
                makeInsuranceInspectedCarsRequest(true, 1);

                break;
        }
    }


    public void onUpdateScreenEvent() {
        GCLog.e("insurance inspected cars list");
        makeInsuranceInspectedCarsRequest(false, 1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        onUpdateScreenEvent();

    }


}
