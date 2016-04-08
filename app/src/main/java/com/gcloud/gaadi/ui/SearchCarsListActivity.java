package com.gcloud.gaadi.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.EndlessScroll.EndlessScrollListener;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.SearchedCarsListAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.MakeOfferEvent;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.StockItemModel;
import com.gcloud.gaadi.model.StocksModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.swipelistview.BaseSwipeListViewListener;
import com.gcloud.gaadi.ui.swipelistview.SwipeListView;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.squareup.otto.Subscribe;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;


/**
 * Created by ankit on 29/12/14.
 */
public class SearchCarsListActivity extends BaseActivity
        implements OnNoInternetConnectionListener,
        View.OnClickListener,
        AdapterView.OnItemClickListener {

    // private ActionBar mActionBar;
//    private GAHelper mGAHelper;

    String cityName = "", model = "", make = "", dealerShipId = "", priceMax = "", priceMin = "";
    private FrameLayout layoutContainer, alternativeLayout;
    private View dummyView;
    private Button retry;
    private LinearLayout progressBar;
    private TextView errorMessage;
    private Boolean nextPossible;
    private View footerView;
    private ArrayList<StockItemModel> dataList;
    private HashMap<String, String> params = new HashMap<String, String>();
    private LayoutInflater mInflater;
    private SearchedCarsListAdapter searchedCarsListAdapter;
    private SwipeListView searchedCarsList;
    private TextView refineFilter;
    private ImageView img;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mGAHelper = new GAHelper(this);
        //   mActionBar = getSupportActionBar();
        // mActionBar.setDisplayHomeAsUpEnabled(true);

        mInflater = getLayoutInflater();
        mInflater.inflate(R.layout.search_cars_list_activity, frameLayout);
        layoutContainer = (FrameLayout) findViewById(R.id.layoutContainer);
        setInitialView();
        if (getIntent().getExtras() != null) {
            cityName = getIntent().getExtras().getString(Constants.CITY_NAME);
            make = getIntent().getExtras().getString(Constants.MAKE);
            model = getIntent().getExtras().getString(Constants.MODEL);
            dealerShipId = getIntent().getExtras().getString(Constants.DEALER_ID);
            priceMax = getIntent().getExtras().getString(Constants.PRICE_MAX);
            priceMin = getIntent().getExtras().getString(Constants.PRICE_MIN);

        }
        searchCarsListRequest(true, 1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_DEALER_PLATFORM);
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

    private void searchCarsListRequest(final boolean showFullPageError, final int pageNo) {
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.UCDID, String.valueOf(CommonUtils.getIntSharedPreference(this, Constants.UC_DEALER_ID, -1)));
        params.put(Constants.PAGE_NO, String.valueOf(pageNo));
        params.put(Constants.D2D_TAB, Constants.D2D_SEARCH_TAB);
        params.put(Constants.FILTER_MAKE, make);
        params.put(Constants.FILTER_MODEL, model);
        params.put(Constants.CITY_METHOD, cityName);
        params.put(Constants.PRICE_MAX, priceMax);
        params.put(Constants.PRICE_MIN, priceMin);
        params.put(Constants.DEALERSHIP_IDS, dealerShipId);
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
        params.put(Constants.METHOD_LABEL, Constants.D2D_METHOD);

        RetrofitRequest.stocksRequest(params, new Callback<StocksModel>() {
            @Override
            public void success(StocksModel stocksModel, retrofit.client.Response response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                if (stocksModel.getTotalRecords() == 0) {
                    //hide button and show message
                    showNoRecordsMessage(stocksModel.getMessage());
                } else {
                    GCLog.e("SearchedCars List response: " + response.toString());
                    nextPossible = stocksModel.isHasNext();
                    GCLog.d(nextPossible + " ");
                    if (pageNo == 1) {
                        layoutContainer.removeAllViews();
                        View view = mInflater.inflate(R.layout.activity_stocks, null, false);
                       // FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
                     //   fab.setVisibility(View.GONE);
                        layoutContainer.addView(view);
                        initializeViews(stocksModel);

                    } else {
                        searchedCarsList.removeFooterView(footerView);
                        dataList.addAll(stocksModel.getStocks());

                        searchedCarsListAdapter.notifyDataSetChanged();
                    }
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
      /*  SearchCarsRequest stocksRequest = new SearchCarsRequest(
                this,
                Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<StocksModel>() {
                    @Override
                    public void onResponse(StocksModel response) {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }

                        if (response.getTotalRecords() == 0) {
                            //hide button and show message
                            showNoRecordsMessage(response.getMessage());
                        } else {
                            GCLog.e("SearchedCars List response: " + response.toString());
                            nextPossible = response.isHasNext();
                            GCLog.d(nextPossible + " ");
                            if (pageNo == 1) {
                                layoutContainer.removeAllViews();
                                View view = mInflater.inflate(R.layout.activity_stocks, null, false);
                                FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
                                fab.setVisibility(View.GONE);
                                layoutContainer.addView(view);
                                initializeViews(response);

                            } else {
                                searchedCarsList.removeFooterView(footerView);
                                dataList.addAll(response.getStocks());

                                searchedCarsListAdapter.notifyDataSetChanged();
                            }
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

        ApplicationController.getInstance().addToRequestQueue(stocksRequest, Constants.TAG_SEARCH_CARS_LIST, showFullPageError, this);
    */}


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

    private void showNoRecordsMessage(String message) {
        setInitialView();
        hideProgressBar();

        View view = mInflater.inflate(R.layout.layout_error, null, false);

        alternativeLayout.addView(view);

        errorMessage = (TextView) view.findViewById(R.id.errorMessage);
        errorMessage.setText(message);
        refineFilter = (TextView) view.findViewById(R.id.checkconnection);
       // refineFilter.setText(R.string.refine_filter);
        refineFilter.setVisibility(View.INVISIBLE);
        img = (ImageView) view.findViewById(R.id.no_internet_img);
        img.setImageResource(R.drawable.no_result_icons);

        retry = (Button) view.findViewById(R.id.retry);
        // retry.setTag("ADD STOCK");

        retry.setOnClickListener(this);

    }

    private void showServerErrorLayout(boolean showFullPageError) {
        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                Constants.CATEGORY_DEALER_PLATFORM,
                Constants.CATEGORY_DEALER_PLATFORM,
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

    private void initializeViews(StocksModel response) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerView = inflater.inflate(R.layout.listview_footer, null);
        //   mActionBar.setDisplayHomeAsUpEnabled(true);

        searchedCarsList = (SwipeListView) findViewById(R.id.stocksList);
        if (nextPossible)
            searchedCarsList.addFooterView(footerView);
        searchedCarsList.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onEndlessScroll(int pageNo) {
                if (nextPossible) {
                    searchedCarsList.addFooterView(footerView);
                    searchCarsListRequest(false, pageNo);
                }
            }

            @Override
            public void handleOpenedTuple() {
                searchedCarsList.closeOpenedItems();
            }
        });
        searchedCarsListAdapter = new SearchedCarsListAdapter(this, response.getStocks());

        dataList = response.getStocks();
        searchedCarsList.setAdapter(searchedCarsListAdapter);
        searchedCarsList.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onClickFrontView(int position) {
                GCLog.e("onClickFrontView " + position);
            }

            @Override
            public void onOpened(int position, boolean toRight) {
                if (position != Constants.listOpenedItem)
                    searchedCarsList.closeAnimate(Constants.listOpenedItem);
                Constants.listOpenedItem = position;
            }

            @Override
            public void onClickBackView(int position) {
                searchedCarsList.closeAnimate(position);
            }
        });

        searchedCarsList.setAnimationTime(300);
        searchedCarsList.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);

        searchedCarsList.setOffsetLeft(Constants.LIST_ITEM_LEFT_OFFSET);


        searchedCarsListAdapter.notifyDataSetChanged();

    }

    public void openAnimate(int position) {
        Log.e("OpenANimate", " StocksActivity");
        searchedCarsList.openAnimate(position);
    }


    public void closeAnimate(int position) {
        searchedCarsList.closeAnimate(position);
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
                Constants.CATEGORY_DEALER_PLATFORM,
                Constants.CATEGORY_DEALER_PLATFORM,
                Constants.ACTION_TAP,
                Constants.LABEL_SERVER_ERROR,
                0);
       // ApplicationController.getInstance().cancelPendingRequests(Constants.TAG_STOCK_DETAIL);

        NetworkEvent.NetworkError networkError = networkEvent.getNetworkError();
        if (networkError == NetworkEvent.NetworkError.NO_INTERNET_CONNECTION) {
            showNetworkErrorLayout(networkEvent.isShowFullPageError());
        } else if (networkError == NetworkEvent.NetworkError.SLOW_CONNECTION) {

        }
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
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance();
        switch (v.getId()) {
            case R.id.retry:
               /* String tag = (String) retry.getTag();
                if ((tag != null) && !tag.isEmpty() && tag.equalsIgnoreCase(getString(R.string.add_stock))) {
                    openAddStockActivity();
                } else {*/
                searchCarsListRequest(true, 1);

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.STOCK_VIEW_ACTIVITY) {
            if (resultCode != RESULT_CANCELED) {
                setInitialView();
                searchCarsListRequest(true, 1);
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//        if(parent.getAdapter())
    }


    @Subscribe
    public void onMakeOfferEvent(MakeOfferEvent event) {
        GCLog.e("make offer event: " + event.toString());
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:" + event.getOtherDealerMobile()));
        sendIntent.putExtra("sms_body", "\n--\n" + event.getDealerEmail());
        startActivity(sendIntent);
        closeAnimate(Constants.listOpenedItem);
    }
}

