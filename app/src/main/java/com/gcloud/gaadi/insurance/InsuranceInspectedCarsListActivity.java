package com.gcloud.gaadi.insurance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.gcloud.gaadi.events.InsuranceInspectedCarSelectedEvent;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.FinanceCompany;
import com.gcloud.gaadi.model.InspectedCarsInsuranceModel;
import com.gcloud.gaadi.model.InsuranceInspectedCarData;
import com.gcloud.gaadi.model.StockDetailModel;
import com.gcloud.gaadi.model.StocksModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.BaseActivity;
import com.gcloud.gaadi.ui.FilterFragment;
import com.gcloud.gaadi.ui.HotFixRecyclerView;
import com.gcloud.gaadi.ui.StocksActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;

public class InsuranceInspectedCarsListActivity extends BaseActivity
        implements View.OnClickListener, OnNoInternetConnectionListener {

    private static final int FILTER_ACTION = 101;
    int pageNo = 1;
    int selectedIndex = 0;
    private HashMap<String, String> params;
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
    private ArrayList<FinanceCompany> financeCompanyList;
    private String mAgentId = "";
    private InsuranceInspectedCarsAdapter ca;
    private HotFixRecyclerView recList;
    private LinearLayoutManager llm;
    //ActionBar mActionBar;
    private TextView refineFilter;
    private ImageView img;


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
        getLayoutInflater().inflate(R.layout.activity_place_holder, frameLayout);

        StocksActivity.filterOptionMap = new HashMap<>();
        StocksActivity.filterOptionCount = new HashMap<>();
        StocksActivity.filterParams = new HashMap<>();
        StocksActivity.makeHashMap = new LinkedHashMap<>();

        layoutContainer = (FrameLayout) findViewById(R.id.layoutContainer);
        mInflater = getLayoutInflater();

        Intent intent = getIntent();
        mInsuranceCities = intent.getStringArrayListExtra(Constants.INSURANCE_CITIES);
        financeCompanyList = (ArrayList<FinanceCompany>) intent.getSerializableExtra(Constants.FINANCE_COMPANY);
        mAgentId = intent.getStringExtra(Constants.AGENT_ID);
        setInitialView();
        params = new HashMap<>();

        makeInsuranceInspectedCarsRequest(true, 1);
        fab.setOnClickListener(this);
    }


    private void makeInsuranceInspectedCarsRequest(final boolean showFullPageError, final int pageNo) {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        params.put(Constants.METHOD_LABEL, Constants.INSPECTED_CARS_FOR_INSURANCE);
        params.put(Constants.PAGE, String.valueOf(pageNo));
        params.put(Constants.RPP, "10");


        RetrofitRequest.getInspectedCarsForInsurance(
                params, new Callback<InspectedCarsInsuranceModel>() {

                    @Override
                    public void success(InspectedCarsInsuranceModel inspectedCarsInsuranceModel, retrofit.client.Response response) {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }

                        if ("T".equalsIgnoreCase(inspectedCarsInsuranceModel.getStatus())) {
                            if (inspectedCarsInsuranceModel.getTotalRecords() > 0) {
                                toolbar.setTitle("Inspected Cars (" + inspectedCarsInsuranceModel.getTotalRecords() + ")");
                                GCLog.e("response list: " + inspectedCarsInsuranceModel.getInspectedCars());
                                if (pageNo == 1) {
                                    initializeList(inspectedCarsInsuranceModel);
                                    if (inspectedCarsInsuranceModel.getFilters() != null) {
                                        makeFilterRequest(inspectedCarsInsuranceModel.getFilters());
                                    }
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
            CommonUtils.showToast(InsuranceInspectedCarsListActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT);
        }
    }


    private void initializeList(final InspectedCarsInsuranceModel response) {
        insuranceInspectedCars = response.getInspectedCars();
        layoutContainer.removeAllViews();
        View view = mInflater.inflate(R.layout.insurance_inspected_cars_list, null, false);
        layoutContainer.addView(view);
        recList = (HotFixRecyclerView) layoutContainer.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        llm = new LinearLayoutManager(InsuranceInspectedCarsListActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        ca = new InsuranceInspectedCarsAdapter(InsuranceInspectedCarsListActivity.this,
                insuranceInspectedCars, mInsuranceCities, mAgentId);
        recList.setAdapter(ca);

        recList.setOnScrollListener(new RecyclerViewEndlessScrollListener(llm, response.getTotalRecords()) {
            @Override
            public void onLoadMore(int nextPageNo) {
                if (response.getHasNext() == 1) {
                    makeInsuranceInspectedCarsRequest(false, nextPageNo);
                }
            }
        });

    }

    private void makeFilterRequest(StocksModel.Filters filterResponse) {
        if (StocksActivity.filterOptionMap == null) {
            return;
        }
        if (!StocksActivity.filterOptionMap.containsKey("filters")
                || StocksActivity.filterOptionMap.get("filters") == null) {
            //GCLog.e("filters created");
            StocksActivity.filterOptionMap.put("filters", FilterFragment.getInstance(true,
                    InsuranceInspectedCarsFilterActivity.list));
            StocksActivity.filterOptionMap.put("reg no", FilterFragment.getInstance(true, "Search car by Reg no"));
            for (StockDetailModel model : filterResponse.getMake()) {
                StocksActivity.makeHashMap.put(model.getMake(), String.valueOf(model.getMakeid()));
            }
            StocksActivity.filterOptionMap.put("make", FilterFragment.getInstance(false, StocksActivity.makeHashMap, false));
            LinkedHashMap<String, String> modelMap = new LinkedHashMap<>();
            for (StockDetailModel model : filterResponse.getModel()) {
                modelMap.put(model.getModel(), String.valueOf(model.getMakeid()));
            }
            StocksActivity.filterOptionMap.put("model", FilterFragment.getInstance(false, modelMap, false));

            StocksActivity.filterOptionMap.put("fuel type", FilterFragment.getInstance(false, filterResponse.getFuelType()));
            StocksActivity.filterOptionMap.put("year", FilterFragment.getInstance(false, filterResponse.getYear()));

            LinkedHashMap<String, String> priceMap = new LinkedHashMap<>();
            for (StocksModel.Filters.KeyValueModel model : filterResponse.getPrice()) {
                priceMap.put(model.getValue(), model.getKey());
            }
            StocksActivity.filterOptionMap.put("price", FilterFragment.getInstance(false, priceMap, true));
            LinkedHashMap<String, String> kmMap = new LinkedHashMap<>();
            for (StocksModel.Filters.KeyValueModel model : filterResponse.getKm()) {
                kmMap.put(model.getValue(), model.getKey());
            }
            StocksActivity.filterOptionMap.put("km", FilterFragment.getInstance(false, kmMap, true));

            fab.setVisibility(View.VISIBLE);
        }
    }

    private void showNoRecordsMessage(boolean showFullPageError, String message) {
        if (showFullPageError) {
            toolbar.setTitle("Inspected Cars (0)");

            setInitialView();
            hideProgressBar();

            View view = mInflater.inflate(R.layout.layout_error, null, false);

            alternativeLayout.addView(view);
            refineFilter = (TextView) view.findViewById(R.id.checkconnection);
           // refineFilter.setText(R.string.refine_filter);
            refineFilter.setVisibility(View.INVISIBLE);
            img = (ImageView) view.findViewById(R.id.no_internet_img);

            img.setImageResource(R.drawable.no_result_icons);
            errorMessage = (TextView) view.findViewById(R.id.errorMessage);
            errorMessage.setText(message);

            retry = (Button) view.findViewById(R.id.retry);
            retry.setVisibility(View.GONE);

        } else {
            // CommonUtils.showToast(InsuranceInspectedCarsListActivity.this, "No more records found", Toast.LENGTH_SHORT);

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
            CommonUtils.showToast(InsuranceInspectedCarsListActivity.this, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(Constants.INSURANCE_CITIES, mInsuranceCities);
        outState.putSerializable(Constants.FINANCE_COMPANY, financeCompanyList);
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
            CommonUtils.showToast(InsuranceInspectedCarsListActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.retry:

                setInitialView();
                makeInsuranceInspectedCarsRequest(true, 1);

                break;

            case R.id.fab:
                Intent intent = new Intent(this, InsuranceInspectedCarsFilterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, FILTER_ACTION);
                break;
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

    @Subscribe
    public void onInsuranceInspectedCarSelectedEvent(InsuranceInspectedCarSelectedEvent event) {
        GCLog.e("insurance inspected car: " + event.toString());
        Intent intent;
        if (event.getInspectedCarData().getCNGLPGEndorsementinRC().equals("1") && event.getInspectedCarData().getFitmentType().equals(Constants.EXTERNALLY_FITTED)) {
            intent = new Intent(this, InspectedCarsIssuePolicyActivity.class);
        } else {
            intent = new Intent(this, DealerQuoteScreen.class);
            intent.putExtra(Constants.SELECTED_CASE, Constants.INSPECTED_CARS);
        }


        intent.putExtra(Constants.INSURANCE_INSPECTED_CAR_DATA, event.getInspectedCarData());
        intent.putExtra(Constants.INSURANCE_CITIES, mInsuranceCities);
        intent.putExtra(Constants.FINANCE_COMPANY, financeCompanyList);
        intent.putExtra(Constants.AGENT_ID, event.getAgentId());
        startActivityForResult(intent, Constants.INSURANCE_CASE_FOR_INSPECTED_CAR);
        CommonUtils.startActivityTransition(InsuranceInspectedCarsListActivity.this, Constants.TRANSITION_LEFT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        StocksActivity.filterParams = null;
        StocksActivity.filterOptionCount = null;
        StocksActivity.filterOptionMap = null;
        StocksActivity.makeHashMap = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.INSURANCE_CASE_FOR_INSPECTED_CAR:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;

            case FILTER_ACTION:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        boolean applyFilter = extras.getBoolean("applyFilter");
                        filterCarList(applyFilter);
                    }
                }
                break;
        }
    }

    private void filterCarList(boolean applyFilter) {
        params.clear();
        int count = 0;
        for (Map.Entry entry : StocksActivity.filterParams.entrySet()) {
            if (((ArrayList<String>) entry.getValue()).size() > 0) {
                count++;
                StringBuilder values = new StringBuilder();
                for (String value : (ArrayList<String>) entry.getValue()) {
                    if (values.length() > 0) {
                        values.append(",");
                    }
                    values.append(value);
                }
                params.put((String) entry.getKey(), values.toString());
            }
        }
        if ((StocksActivity.filterParams.get("reg no") != null && StocksActivity.filterParams.get("reg no").get(0).isEmpty()) && count > 0) {
            count--;
        }
        setFabCounter(count);
        if (applyFilter) {
            makeInsuranceInspectedCarsRequest(true, 1);
        }

    }
}
