package com.gcloud.gaadi.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.EndlessScroll.EndlessScrollListener;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.CertifiedCarDataAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.FilterEvent;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.events.OpenListItemEvent;
import com.gcloud.gaadi.events.SetTabTextEvent;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.CertifiedCarData;
import com.gcloud.gaadi.model.FilterCertificationCars;
import com.gcloud.gaadi.model.Paginate;
import com.gcloud.gaadi.model.ViewCertificationCarsWarrantyInput;
import com.gcloud.gaadi.model.ViewCertifiedCarModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

public class CertifiedCarsFragment extends Fragment
        implements View.OnClickListener, OnNoInternetConnectionListener, SwipeRefreshLayout.OnRefreshListener {

    public static boolean shouldReload = false;
    private View rootView;
    private Activity activity;
    private ListView listView;
    private View footerView;
    private ArrayList<CertifiedCarData> carData;
    private CertifiedCarDataAdapter certifiedCarDataAdapter;
    private LayoutInflater mInflater;
    private View dummyView;
    private Button retry;
    private LinearLayout progressBar;
    private TextView errorMessage;
    private FrameLayout layoutContainer, alternativeLayout;
    private HashMap<String, String> params = new HashMap<String, String>();
    private boolean isNextPossible = true;
    private TextView noInternet;
    private ImageView imageView;

    private Activity getFragmentActivity() {
        return activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationController.getEventBus().register(this);

        if (shouldReload) {
            makeCertifiedCarsRequest(false, 1, null);
            shouldReload = false;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        mInflater = activity.getLayoutInflater();
        footerView = mInflater.inflate(R.layout.listview_footer, null);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationController.getEventBus().unregister(this);
    }

    private void makeCertifiedCarsRequest(final boolean showFullPageError, final int pageNumber, String request_string) {
        if (request_string == null) {
            request_string = createViewCertCarsRequest(pageNumber, "", "", "");
        }
        params.put(Constants.EVALUATIONDATA, request_string);
        RetrofitRequest.warrantyCertifiedCars(params, new Callback<ViewCertifiedCarModel>() {

            @Override
            public void success(ViewCertifiedCarModel response, retrofit.client.Response res) {
                if ("T".equalsIgnoreCase(response.getStatus())) {
                    if (pageNumber == 1) {
                        if (response.getFilterKeys() != null) {
                            Intent intent = new Intent(GaadiWarrantyActivity.INITIATE_FILTERS);
                            intent.putExtra("filters", response.getFilterKeys());
                            intent.putExtra(Constants.CALL_SOURCE, "warrantyFilters");
                            ApplicationController.getEventBus().post(intent);
                        }
                    }
                    if (response.getCarCount() != null && response.getCarCount().equals("0")) {
                        ApplicationController.getEventBus().post(
                                new SetTabTextEvent("Certified Cars (" + "0" + ")", Constants.CERTIFIED_CARS));

                        initializeList(response);
                    }
                    if (response.getCarData().size() > 0) {

                        isNextPossible = response.getIsNextPossible();
                        if (pageNumber == 1) {
                            ApplicationController.getEventBus().post(
                                    new SetTabTextEvent("Certified Cars (" + response.getCarCount() + ")", Constants.CERTIFIED_CARS));
                            initializeList(response);

                            /*if (response.getFilterKeys() != null) {
                                Intent intent = new Intent(GaadiWarrantyActivity.INITIATE_FILTERS);
                                intent.putExtra("filters", response.getFilterKeys());
                                intent.putExtra(Constants.CALL_SOURCE, "warrantyFilters");
                                ApplicationController.getEventBus().post(intent);
                            }*/
                        } else {
                            carData.addAll(response.getCarData());
                            listView.removeFooterView(footerView);
                            certifiedCarDataAdapter.notifyDataSetChanged();
                        }

                    } else {
                        showNoCertifiedCarsLayout(showFullPageError);
                    }
                } else {
                    showNoCertifiedCarsLayout(showFullPageError);
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

        /*ViewCertifiedCarRequest certifiedCarRequest = new ViewCertifiedCarRequest(
                getFragmentActivity(),
                Request.Method.POST,
                Constants.getWarrantyWebServiceURL(getFragmentActivity()),
                params,
                new Response.Listener<ViewCertifiedCarModel>() {
                    @Override
                    public void onResponse(ViewCertifiedCarModel response) {
                        Log.d("URL Warranty issue", "" + Constants.getWarrantyWebServiceURL(getFragmentActivity()));
                        GCLog.e("response model: " + response.toString());
                        if ("T".equalsIgnoreCase(response.getStatus())) {
                            if(response.getCarCount() !=null && response.getCarCount().equals("0"))
                            {
                                ApplicationController.getEventBus().post(
                                        new SetTabTextEvent("Certified Cars (" + "0" + ")", Constants.CERTIFIED_CARS));

                                initializeList(response);
                            }
                            if (response.getCarData().size() > 0) {

                                isNextPossible = response.getIsNextPossible();
                                if (pageNumber == 1) {
                                    ApplicationController.getEventBus().post(
                                            new SetTabTextEvent("Certified Cars (" + response.getCarCount() + ")", Constants.CERTIFIED_CARS));
                                    initializeList(response);
                                } else {
                                    carData.addAll(response.getCarData());
                                    listView.removeFooterView(footerView);
                                    certifiedCarDataAdapter.notifyDataSetChanged();
                                }

                            } else {
                                showNoCertifiedCarsLayout(showFullPageError);
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

        ApplicationController.getInstance().addToRequestQueue(certifiedCarRequest, Constants.TAG_ACTIVE_INVENTORIES, showFullPageError, this);*/
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
            CommonUtils.showToast(getFragmentActivity(), getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
        }
    }

    private String createViewCertCarsRequest(int pageNo, String make, String model, String searchKey) {

        String request_string = null;

        ViewCertificationCarsWarrantyInput certcarsWarrantyInput = new ViewCertificationCarsWarrantyInput();
        certcarsWarrantyInput.setApikey(Constants.API_KEY);
        certcarsWarrantyInput.setMethod(Constants.CERTIFIED_CAR_REQUEST_API);
        certcarsWarrantyInput.setOutput(Constants.API_RESPONSE_FORMAT);
        certcarsWarrantyInput.setUsername(CommonUtils.getStringSharedPreference(getFragmentActivity(), Constants.UC_DEALER_USERNAME, ""));
        certcarsWarrantyInput.setNormal_password(CommonUtils.getStringSharedPreference(getFragmentActivity(), Constants.UC_DEALER_PASSWORD, ""));


//		certcarsWarrantyInput.setUsername("aditicars9233@gmail.com");
//		certcarsWarrantyInput.setNormal_password("hijklm");
//		
        Paginate paginate = new Paginate();
        paginate.setPageNo(String.valueOf(pageNo));
        paginate.setPageSize(String.valueOf("10"));

        FilterCertificationCars filterValue = new FilterCertificationCars();
        filterValue.setFromYear("");
        filterValue.setToYear("");
        filterValue.setMake(make);
        filterValue.setModel(model);
        filterValue.setFromKm("");
        filterValue.setToKm("");
        filterValue.setToPrice("");
        filterValue.setFromPrice("");
        filterValue.setRegno("");
        filterValue.setFromDate("");
        filterValue.setToDate("");
        filterValue.setSearchKey(searchKey);

        Gson gson = new Gson();
        certcarsWarrantyInput.setPaginate(paginate);
        certcarsWarrantyInput.setFilterValue(filterValue);

        request_string = gson.toJson(certcarsWarrantyInput,
                ViewCertificationCarsWarrantyInput.class);

        return request_string;
    }


    private void initializeList(ViewCertifiedCarModel response) {
        carData = response.getCarData();
        layoutContainer.removeAllViews();
        View view = mInflater.inflate(R.layout.fragment_certified_cardata, null, false);
        layoutContainer.addView(view);
        listView = (ListView) layoutContainer.findViewById(R.id.certifiedcarslist);
        if (isNextPossible)
            listView.addFooterView(footerView);
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onEndlessScroll(int pageNo) {
                if (isNextPossible) {
                    makeCertifiedCarsRequest(false, pageNo, null);
                    listView.addFooterView(footerView);
                }
            }


            @Override
            public void handleOpenedTuple() {
                // listView.closeOpenedItems();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_CERTIFIED_CARS,
                        Constants.CATEGORY_CERTIFIED_CARS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_CERTIFIED_CARS_ISSUE_WARRANTY,
                        0);
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.CLOSELIST_WARRANTY));
                Intent intent = new Intent(getFragmentActivity(), IssueWarrantyFormActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                Bundle args = new Bundle();

                args.putSerializable(Constants.MODEL_DATA, carData.get(position));
                intent.putExtras(args);
                getFragmentActivity().startActivity(intent);

            }
        });
      /*  listView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onClickFrontView(int position) {
                *//*Intent intent = new Intent(getFragmentActivity(), ViewLeadActivity.class);
                startActivity(intent);*//*
            }

            @Override
            public void onOpened(int position, boolean toRight) {
                if (position != Constants.listOpenedItem)
                    listView.closeAnimate(Constants.listOpenedItem);
                Constants.listOpenedItem = position;
            }

            @Override
            public void onClickBackView(int position) {
                listView.closeAnimate(position);
            }
        });
*/

//        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.active_refresh_layout);
//        swipeRefreshLayout.setOnRefreshListener(DealerActiveInventoryFragment.this);
/*

        listView.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
        listView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
        listView.setOffsetLeft(Constants.LIST_ITEM_LEFT_OFFSET);
*/

        certifiedCarDataAdapter = new CertifiedCarDataAdapter(getFragmentActivity(), activity, response.getCarData());
        listView.setAdapter(certifiedCarDataAdapter);
    }

   /* @Subscribe
    public void onCloseListItem(OpenListItemEvent event) {
        GCLog.e("position to open: " + event.getPosition());
        int position = event.getPosition();
        int source = event.getSource();
        if (source == Constants.CLOSELIST_WARRANTY) {
            this.closeAnimate(position);
        } else if (source == Constants.OPENLIST_CERTIFIED) {
            this.openAnimate(position);
        }
    }*/

    /*private void closeAnimate(int position) {
        listView.closeAnimate(position);
    }

    private void openAnimate(int position) {
        listView.openAnimate(position);
    }
*/
    private void showNoCertifiedCarsLayout(boolean showFullPageError) {
        setInitialView();
        hideProgressBar();

        View view = mInflater.inflate(R.layout.layout_error, null, false);
        alternativeLayout.addView(view);
        errorMessage = (TextView) view.findViewById(R.id.errorMessage);
        noInternet = (TextView) view.findViewById(R.id.checkconnection);
        noInternet.setVisibility(View.GONE);
        imageView = (ImageView) view.findViewById(R.id.no_internet_img);
        imageView.setImageResource(R.drawable.no_result_icons);
        errorMessage.setText(R.string.noresult);
        retry = (Button) view.findViewById(R.id.retry);
        retry.setVisibility(View.GONE);

    }

    private void showServerErrorLayout(boolean showFullPageError) {
        if (!isVisible() || !isAdded()) {
            return;
        }
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
            CommonUtils.showToast(getFragmentActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //    params.put(Constants.PAGE_NO, String.valueOf(pageNo));

        rootView = getFragmentActivity().getLayoutInflater().inflate(R.layout.activity_place_holder, container, false);
        layoutContainer = (FrameLayout) rootView.findViewById(R.id.layoutContainer);
        /*listView = (ListView) rootView.findViewById(R.id.list);*/
        setInitialView();
        makeCertifiedCarsRequest(true, 1, null);

        return rootView;
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
       // ApplicationController.getInstance().cancelPendingRequests(Constants.TAG_ACTIVE_INVENTORIES);

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
            CommonUtils.showToast(getFragmentActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.retry:

                setInitialView();
                makeCertifiedCarsRequest(true, 1, null);
                break;
        }
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent = new Intent(getFragmentActivity(), ViewLeadActivity.class);
//        startActivity(intent);
//    }

    /*  @Subscribe
      public void onOpenListItem(OpenListItemEvent event) {
          GCLog.e("position to open: " + event.getPosition());
          int position = event.getPosition();
          this.openAnimate(position);
      }
  */


    @Subscribe
    public void onFilterEvent(FilterEvent filterEvent) {
        if (filterEvent.getCurrentItem() != GaadiWarrantyActivity.FROM_DEALER_PLATFORM) {
            return;
        }
        if (filterEvent.getParams() == null
                || filterEvent.getParams().size() == 0) {
            makeCertifiedCarsRequest(false, 1, null);
            return;
        }
        setInitialView();
        String make = "", model = "", searchKey  = "";
        HashMap<String, String> params = filterEvent.getParams();
        if (params.containsKey("make")) {
            make = params.get("make");
        }
        if (params.containsKey("model")) {
            model = params.get("model");
        }
        if (params.containsKey("searchKey")) {
            searchKey = params.get("searchKey");
            try {
                Integer.parseInt(params.get("searchKey"));
                searchKey = "";
            } catch (NumberFormatException  e) {
                if (CommonUtils.canLog()) {
                    GCLog.e("registration number received");
                }
            }
        }
        makeCertifiedCarsRequest(false, 1, createViewCertCarsRequest(1, make, model, searchKey));
    }

    @Override
    public void onRefresh() {
        setInitialView();
        makeCertifiedCarsRequest(false, 1, null);
    }
}
