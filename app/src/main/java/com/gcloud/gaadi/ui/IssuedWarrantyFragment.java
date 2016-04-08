package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.gcloud.gaadi.adapter.IssueWarrantyAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.FilterEvent;
import com.gcloud.gaadi.events.NetworkEvent;
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


public class IssuedWarrantyFragment extends Fragment
        implements OnNoInternetConnectionListener,
        View.OnClickListener,
        AdapterView.OnItemClickListener {

    public static boolean shouldReload = false;
    private View rootView;
    private Activity activity;
    private ListView listView;
    private IssueWarrantyAdapter issueWarrntyAdapater;
    private LayoutInflater mInflater;
    private View dummyView;
    private View footerView;
    private Button retry;
    private LinearLayout progressBar;
    private TextView errorMessage;
    private ArrayList<CertifiedCarData> certifiesCarData;
    private FrameLayout layoutContainer, alternativeLayout;
    private Boolean nextPossible = true;
    private HashMap<String, String> params = new HashMap<String, String>();
    private TextView noInternet;
    private ImageView imageView;
//    private GAHelper mGAHelper;

    private Activity getFragmentActivity() {
        return activity;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        mInflater = activity.getLayoutInflater();
//        mGAHelper = new GAHelper(getFragmentActivity());
        footerView = mInflater.inflate(R.layout.listview_footer, null);
    }

    private void makeIssuedWarrantyRequest(final boolean showFullPageError, final int pageNumber, String request_string) {
        if (request_string == null) {
            request_string = createViewCertCarsRequest(pageNumber, "", "", "", "", "");
        }
        params.put(Constants.EVALUATIONDATA, request_string);

        RetrofitRequest.warrantyIssuedRequest(params, new Callback<ViewCertifiedCarModel>() {

            @Override
            public void success(ViewCertifiedCarModel response, retrofit.client.Response res) {
                GCLog.e("response model: " + response.toString());
                if ("T".equalsIgnoreCase(response.getStatus())) {
                    if (response.getCarCount() == null || response.getCarCount().equals("0")) {
                        ApplicationController.getEventBus().post(
                                new SetTabTextEvent("Issued Warranty (" + "0" + ")", Constants.ISSUED_WARRANTY));

                        initializeList(response);
                    }

                    if (response.getCarData().size() > 0) {
                        nextPossible = response.getIsNextPossible();

                        if (pageNumber == 1) {
                            ApplicationController.getEventBus().post(
                                    new SetTabTextEvent("Issued Warranty (" + response.getCarCount() + ")", Constants.ISSUED_WARRANTY));
                            GCLog.e("position to open: " + "Certified Cars (" + response.getCarCount() + ")", Constants.ISSUED_WARRANTY + ")"
                                    + " ");
                            initializeList(response);
                        } else {
                            certifiesCarData.addAll(response.getCarData());

                            listView.removeFooterView(footerView);
                            issueWarrntyAdapater.notifyDataSetChanged();
                        }
                        listView.setOnItemClickListener(IssuedWarrantyFragment.this);

                    } else {
                        showNoWarrantyIssuedLayout(showFullPageError);
                    }
                } else {
                    showNoWarrantyIssuedLayout(showFullPageError);
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

        /*ViewCertifiedCarRequest issueWarrantyReuest = new ViewCertifiedCarRequest(
                getFragmentActivity(),
                Request.Method.POST,
                Constants.getWarrantyWebServiceURL(getFragmentActivity()),
                params,
                new Response.Listener<ViewCertifiedCarModel>() {
                    @Override
                    public void onResponse(ViewCertifiedCarModel response) {
                        GCLog.e("response model: " + response.toString());
                        if ("T".equalsIgnoreCase(response.getStatus())) {
                            if(response.getCarCount() !=null && response.getCarCount().equals("0"))
                            {
                                ApplicationController.getEventBus().post(
                                        new SetTabTextEvent("Certified Cars (" + "0" + ")", Constants.ISSUED_WARRANTY));

                                initializeList(response);
                            }

                            if (response.getCarData().size() > 0) {
                                nextPossible = response.getIsNextPossible();

                                if (pageNumber == 1) {
                                    ApplicationController.getEventBus().post(
                                            new SetTabTextEvent("Issued Warranty (" + response.getCarCount() + ")", Constants.ISSUED_WARRANTY));
                                    GCLog.e("position to open: " + "Certified Cars (" + response.getCarCount() + ")", Constants.ISSUED_WARRANTY + ")"
                                            + " ");
                                    initializeList(response);
                                } else {
                                    certifiesCarData.addAll(response.getCarData());

                                    listView.removeFooterView(footerView);
                                    issueWarrntyAdapater.notifyDataSetChanged();
                                }
                                listView.setOnItemClickListener(IssuedWarrantyFragment.this);

                            } else {
                                showNoWarrantyIssuedLayout(showFullPageError);
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

        ApplicationController.getInstance().addToRequestQueue(issueWarrantyReuest, Constants.TAG_N_LEADS, showFullPageError, this);*/
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


    private void initializeList(ViewCertifiedCarModel response) {
        certifiesCarData = response.getCarData();
        layoutContainer.removeAllViews();
        View view = mInflater.inflate(R.layout.fragment_issuewarranty_cardata, null, false);
        layoutContainer.addView(view);
        listView = (ListView) layoutContainer.findViewById(R.id.issuewarrantycarslist);
        if (nextPossible)
            listView.addFooterView(footerView);
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onEndlessScroll(int pageNo) {
                if (nextPossible) {
                    makeIssuedWarrantyRequest(false, pageNo, null);
                    listView.addFooterView(footerView);
                }
            }

            @Override
            public void handleOpenedTuple() {
            }
        });

        issueWarrntyAdapater = new IssueWarrantyAdapter(getFragmentActivity(), getFragmentActivity(), response.getCarData());
        listView.setAdapter(issueWarrntyAdapater);
    }


    private String createViewCertCarsRequest(int pageNumber,
                                             String make, String model, String warrantyType, String warrantyStatus, String searchKey) {


        String request_string = null;

        ViewCertificationCarsWarrantyInput certcarsWarrantyInput = new ViewCertificationCarsWarrantyInput();
        certcarsWarrantyInput.setApikey(Constants.API_KEY);
        certcarsWarrantyInput.setMethod(Constants.ISSUEWARRANTY_CAR_REQUEST_API);
        certcarsWarrantyInput.setOutput(Constants.API_RESPONSE_FORMAT);
        certcarsWarrantyInput.setUsername(CommonUtils.getStringSharedPreference(getFragmentActivity(), Constants.UC_DEALER_USERNAME, ""));
        certcarsWarrantyInput.setNormal_password(CommonUtils.getStringSharedPreference(getFragmentActivity(), Constants.UC_DEALER_PASSWORD, ""));

        Paginate paginate = new Paginate();
        paginate.setPageNo(String.valueOf(pageNumber));
        paginate.setPageSize(String.valueOf("10"));

        FilterCertificationCars filterValue = new FilterCertificationCars();
        filterValue.setFromYear("");
        filterValue.setToYear("");
        filterValue.setMake(make);
        filterValue.setModel(model);
        filterValue.setWarrantyType(warrantyType);
        filterValue.setWarrantyStatus(warrantyStatus);
        filterValue.setSearchKey(searchKey);
        filterValue.setFromKm("");
        filterValue.setToKm("");
        filterValue.setToPrice("");
        filterValue.setFromPrice("");
        filterValue.setRegno("");
        filterValue.setFromDate("");
        filterValue.setToDate("");

        certcarsWarrantyInput.setPaginate(paginate);
        certcarsWarrantyInput.setFilterValue(filterValue);
        Gson gson = new Gson();
        request_string = gson.toJson(certcarsWarrantyInput,
                ViewCertificationCarsWarrantyInput.class);

        GCLog.e(" request string: " + request_string);
        return request_string;
    }


    @Override
    public void onResume() {
        super.onResume();
        ApplicationController.getEventBus().register(this);

        if (shouldReload) {
            makeIssuedWarrantyRequest(false, 1, null);
            shouldReload = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationController.getEventBus().unregister(this);
    }

    private void showNoWarrantyIssuedLayout(boolean showFullPageError) {
        setInitialView();
        hideProgressBar();

      /*  if (showFullPageError) {*/
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

       /* } else {
            CommonUtils.showToast(getFragmentActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT);
        }*/
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


        rootView = getFragmentActivity().getLayoutInflater().inflate(R.layout.activity_place_holder, container, false);
        layoutContainer = (FrameLayout) rootView.findViewById(R.id.layoutContainer);
        /*listView = (ListView) rootView.findViewById(R.id.list);*/
        setInitialView();
        makeIssuedWarrantyRequest(true, 1, null);

        return rootView;
    }

    private void setInitialView() {
        GCLog.e(" true");
        layoutContainer.removeAllViews();
        dummyView = mInflater.inflate(R.layout.layout_loading_full_page, null, false);
        layoutContainer.addView(dummyView);
        alternativeLayout = (FrameLayout) dummyView.findViewById(R.id.alternativeLayout);
        progressBar = (LinearLayout) dummyView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void onNoInternetConnection(NetworkEvent networkEvent) {
        // ApplicationController.getInstance().cancelPendingRequests(Constants.TAG_N_LEADS);

        NetworkEvent.NetworkError networkError = networkEvent.getNetworkError();
        if (networkError == NetworkEvent.NetworkError.NO_INTERNET_CONNECTION) {
            showNetworkErrorLayout(networkEvent.isShowFullPageError());
        }
    }

    @Subscribe
    public void onFilterEvent(FilterEvent filterEvent) {
        if (filterEvent.getCurrentItem() != GaadiWarrantyActivity.FROM_DEALER_PLATFORM) {
            return;
        }
        if (filterEvent.getParams() == null
                || filterEvent.getParams().size() == 0) {
            makeIssuedWarrantyRequest(false, 1, null);
            return;
        }
        setInitialView();
        String make = "", model = "", warrantyType = "", warrantyStatus = "", searchKey = "";
        HashMap<String, String> params = filterEvent.getParams();
        if (params.containsKey("make")) {
            make = params.get("make");
        }
        if (params.containsKey("model")) {
            model = params.get("model");
        }
        if (params.containsKey("warrantyType")) {
            warrantyType = params.get("warrantyType");
        }
        if (params.containsKey("warrantyStatus")) {
            warrantyStatus = params.get("warrantyStatus");
        }
        if (params.containsKey("searchKey")) {
            searchKey = params.get("searchKey");
        }
        makeIssuedWarrantyRequest(false, 1, createViewCertCarsRequest(1, make, model, warrantyType, warrantyStatus, searchKey));
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
                String tag = (String) v.getTag();
                if ((tag != null) && "ADD_LEAD".equalsIgnoreCase(tag)) {
                    intent = new Intent(getFragmentActivity(), LeadAddOptionActivity.class);
                    startActivity(intent);
                } else {
                    setInitialView();
                    makeIssuedWarrantyRequest(true, 1, null);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Intent intent;
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            intent = new Intent(Intent.ACTION_CALL);
        } else {
            intent = new Intent(Intent.ACTION_DIAL);
        }
        intent.setData(Uri.parse("tel:+91" + issueWarrntyAdapater.getNumberToCall()));
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                Constants.CATEGORY_ISSUED_WARRANTY,
                Constants.CATEGORY_ISSUED_WARRANTY,
                Constants.ACTION_TAP,
                Constants.LABEL_ISSUED_WARRANTY_VIEW_CUSTOMER_DETAILS,
                0);

        CertifiedCarData model = (CertifiedCarData) listView.getAdapter()
                .getItem(position);
        Bundle args = new Bundle();
        Intent intent = new Intent(getFragmentActivity(), IssueWarrantyDetailPageActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        args.putString(Constants.CAR_ID, model.getWarranty_id());
        intent.putExtras(args);
        startActivity(intent);
    }


}
