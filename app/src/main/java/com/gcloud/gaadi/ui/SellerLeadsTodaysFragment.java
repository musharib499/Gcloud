package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.content.Intent;
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
import com.gcloud.gaadi.adapter.SellerLeadsAdapter;
import com.gcloud.gaadi.constants.AdapterType;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.FilterEvent;
import com.gcloud.gaadi.events.SearchEvent;
import com.gcloud.gaadi.model.LeadDetailModel;
import com.gcloud.gaadi.model.LeadsModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.squareup.otto.Subscribe;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by ankitgarg on 14/08/15.
 */
public class SellerLeadsTodaysFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    String fromSearchEvent = "";
    int pageNo = 1;
    private View rootView;
    private Activity activity;
    private ListView listView;
    //    private GAHelper mGAHelper;
    private LinearLayout showAllLeads;
    private SellerLeadsAdapter leadsAdapter;
    private LayoutInflater mInflater;
    private View dummyView;
    private View footerView;
    private Button retry;
    private LinearLayout progressBar;
    private TextView errorMessage;
    private ArrayList<LeadDetailModel> leadsData;
    private FrameLayout layoutContainer, alternativeLayout;
    private Boolean nextPossible = true;
    private HashMap<String, String> params = new HashMap<String, String>();
    private ImageView imageView;
    private TextView refineFilter;
    private HashMap<String, String> filterParams;

    private Activity getFragmentActivity() {
        return activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
//        mGAHelper = new GAHelper(getFragmentActivity());
        mInflater = activity.getLayoutInflater();
        footerView = mInflater.inflate(R.layout.listview_footer, null);

        filterParams = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        params.put(Constants.PAGE_NO, String.valueOf(pageNo));

        rootView = getFragmentActivity().getLayoutInflater().inflate(R.layout.activity_place_holder, container, false);
        layoutContainer = (FrameLayout) rootView.findViewById(R.id.layoutContainer);

//        getActivity().findViewById(R.id.resetLeadsFilter).setOnClickListener(this);

        /*listView = (ListView) rootView.findViewById(R.id.list);*/
        setInitialView();
        makeLeadsRequest(true, 1);

        return rootView;
    }

    private void initializeViews(LeadsModel response) {
        layoutContainer.removeAllViews();
        View view = mInflater.inflate(R.layout.seller_leads_fragment_layout, null, false);
        layoutContainer.addView(view);
        listView = (ListView) layoutContainer.findViewById(R.id.list);
        showAllLeads = (LinearLayout) layoutContainer.findViewById(R.id.showallleads);
        if (fromSearchEvent.equals("search")) {
            showAllLeads.setVisibility(View.VISIBLE);
        } else {
            showAllLeads.setVisibility(View.GONE);
        }
        if (nextPossible)
            listView.addFooterView(footerView, null, false);
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onEndlessScroll(int pageNo) {

                if (nextPossible) {
                    listView.addFooterView(footerView, null, false);
                    makeLeadsRequest(false, pageNo);
                }
            }

            @Override
            public void handleOpenedTuple() {
            }
        });
        showAllLeads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.clear();
                fromSearchEvent = "";
                showAllLeads.setVisibility(View.GONE);
                makeLeadsRequest(false, 1);
                leadsAdapter.notifyDataSetChanged();
            }
        });
        leadsData = response.getLeads();
        leadsAdapter = new SellerLeadsAdapter(getFragmentActivity(), response.getLeads(), AdapterType.TODAY_FU);
        listView.setAdapter(leadsAdapter);
        leadsAdapter.notifyDataSetChanged();
    }

    private void makeLeadsRequest(final boolean showFullPageError, final int pageNumber) {
        params.clear();
        params.put(Constants.LEAD_TYPE, "N");
        params.put(Constants.SL_PAGE_NO, pageNumber + "");
        params.put(Constants.TAB_VALUE, Constants.SELLER_TODAYS);
        params.put(Constants.METHOD_LABEL, Constants.SELLER_LEADS_METHOD);
        params.put(Constants.SL_DID,
                String.valueOf(CommonUtils.getIntSharedPreference(ApplicationController.getInstance(), Constants.DEALER_ID, -1)));

        params.putAll(filterParams);

        RetrofitRequest.getSellerLeads(params, new Callback<LeadsModel>() {
            @Override
            public void success(LeadsModel response, retrofit.client.Response retrofitResponse) {
                //GCLog.e("response model: " + response.toString());
                if (response != null && "T".equalsIgnoreCase(response.getStatus())) {
                    if (response.getTotalRecords() > 0) {
                        nextPossible = response.getNextPossible();
                        if (pageNumber == 1) {
                            initializeViews(response);
                        } else {
                            leadsData.addAll(response.getLeads());
                            listView.removeFooterView(footerView);
                            leadsAdapter.notifyDataSetChanged();

                        }
                        listView.setOnItemClickListener(SellerLeadsTodaysFragment.this);

                    } else {
                        showAddLeadLayout(showFullPageError);
                    }
                } else {
                    showNoRecordsMessage(showFullPageError, response.getMessage());
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
        /*LeadsRequest leadsRequest = new LeadsRequest(getFragmentActivity(), Request.Method.POST,
                Constants.getWebServiceURL(getFragmentActivity()),
                params,
                new Response.Listener<LeadsModel>() {
                    @Override
                    public void onResponse(LeadsModel response) {
                        GCLog.e("response model: " + response.toString());
                        if ("T".equalsIgnoreCase(response.getStatus())) {
                            if (response.getTotalRecords() > 0) {
                                nextPossible = response.getNextPossible();
                                if (pageNumber == 1) {
                                    initializeViews(response);
                                } else {
                                    leadsData.addAll(response.getLeads());
                                    listView.removeFooterView(footerView);
                                    leadsAdapter.notifyDataSetChanged();

                                }
                                listView.setOnItemClickListener(SellerLeadsNYCFragment.this);

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

        ApplicationController.getInstance().addToRequestQueue(leadsRequest, Constants.TAG_N_LEADS, showFullPageError, this);*/
    }

    private void showNoRecordsMessage(boolean showFullPageError, String message) {
        if (showFullPageError) {

            setInitialView();
            hideProgressBar();

            View view = mInflater.inflate(R.layout.layout_error, null, false);

            alternativeLayout.addView(view);
            refineFilter = (TextView) view.findViewById(R.id.checkconnection);
           // refineFilter.setText(R.string.refine_filter);
            refineFilter.setVisibility(View.INVISIBLE);
            imageView = (ImageView) view.findViewById(R.id.no_internet_img);
            imageView.setImageResource(R.drawable.no_result_icons);
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
            showAllLeads = (LinearLayout) view.findViewById(R.id.showallleads);
            if (fromSearchEvent.equals("search")) {
                showAllLeads.setVisibility(View.VISIBLE);
            } else {
                showAllLeads.setVisibility(View.GONE);
            }
            showAllLeads.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    params.clear();
                    fromSearchEvent = "";
                    showAllLeads.setVisibility(View.GONE);
                    makeLeadsRequest(false, 1);
                    leadsAdapter.notifyDataSetChanged();
                }
            });
        } else {
            CommonUtils.showToast(getFragmentActivity(), getFragmentActivity().getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
        }
    }


    private void showAddLeadLayout(boolean showFullPageError) {
        setInitialView();
        hideProgressBar();

        View view = mInflater.inflate(R.layout.layout_error, null, false);
        /*alternativeLayout.removeAllViews();*/
        alternativeLayout.addView(view);

        errorMessage = (TextView) view.findViewById(R.id.errorMessage);
        errorMessage.setText(R.string.no_lead_present);
        imageView = (ImageView) view.findViewById(R.id.no_internet_img);
        imageView.setImageResource(R.drawable.no_lead_icon);
        retry = (Button) view.findViewById(R.id.retry);
        refineFilter = (TextView) view.findViewById(R.id.checkconnection);
        //refineFilter.setText(R.string.refine_filter);
        refineFilter.setVisibility(View.INVISIBLE);
        /*retry.setTag("ADD_LEAD");
        retry.setText(R.string.add_lead);
        retry.setOnClickListener(this);*/
        retry.setVisibility(View.GONE);
        showAllLeads = (LinearLayout) view.findViewById(R.id.showallleads);
        if (fromSearchEvent.equals("search")) {
            showAllLeads.setVisibility(View.VISIBLE);
        } else {
            showAllLeads.setVisibility(View.GONE);
        }
        showAllLeads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.clear();
                fromSearchEvent = "";
                showAllLeads.setVisibility(View.GONE);
                makeLeadsRequest(false, 1);
                //  leadsAdapter.notifyDataSetChanged();
            }
        });

    }

    private void showServerErrorLayout(boolean showFullPageError) {
        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                Constants.CATEGORY_SELLER_LEADS,
                Constants.CATEGORY_SELLER_LEADS,
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
            showAllLeads = (LinearLayout) view.findViewById(R.id.showallleads);
            if (fromSearchEvent.equals("search")) {
                showAllLeads.setVisibility(View.VISIBLE);
            } else {
                showAllLeads.setVisibility(View.GONE);
            }
            showAllLeads.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    params.clear();
                    fromSearchEvent = "";
                    showAllLeads.setVisibility(View.GONE);
                    makeLeadsRequest(false, 1);
                    leadsAdapter.notifyDataSetChanged();
                }
            });

        } else {
            CommonUtils.showToast(getFragmentActivity(), getFragmentActivity().getString(R.string.server_error), Toast.LENGTH_SHORT);
        }

    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setInitialView() {
        //GCLog.e(" true");
        layoutContainer.removeAllViews();
        dummyView = mInflater.inflate(R.layout.layout_loading_full_page, null, false);
        layoutContainer.addView(dummyView);
        alternativeLayout = (FrameLayout) dummyView.findViewById(R.id.alternativeLayout);
        progressBar = (LinearLayout) dummyView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle args = new Bundle();
        LeadDetailModel model = (LeadDetailModel) parent.getItemAtPosition(position);
        Intent intent = new Intent(getFragmentActivity(), SellerLeadsDetailPageActivity.class);
        args.putString(Constants.VIEW_LEAD, Constants.VALUE_VIEWLEAD);
        args.putInt(Constants.SELECTED_TAB, Constants.TODAY_FOLLOWUP_FRAG_NO);
        args.putSerializable(Constants.MODEL_DATA, model);
        intent.putExtras(args);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        ApplicationController.getEventBus().post("unregister");

        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.retry:
                String tag = (String) v.getTag();
                if ((tag != null) && "ADD_LEAD".equalsIgnoreCase(tag)) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_SELLER_LEADS,
                            Constants.CATEGORY_SELLER_LEADS,
                            Constants.ACTION_TAP,
                            Constants.LABEL_STOCK_DETAIL_ADD_LEAD,
                            0);
                    intent = new Intent(getFragmentActivity(), LeadAddOptionActivity.class);
                    startActivity(intent);
                } else {
                    setInitialView();
                    makeLeadsRequest(true, 1);
                }
                break;
        }
    }

    @Subscribe
    public void onSearchEvent(SearchEvent searchEvent) {

        //GCLog.e("True");
        // if(searchEvent.getCurrentItem()==Constants.NOT_YET_CALLED_FRAG_NO) {
        fromSearchEvent = "search";
        HashMap<String, String> searchMap = searchEvent.getParams();
        String searchKey = searchMap.get(Constants.LEAD_SEARCH_KEY);
        String leadname = searchMap.get(Constants.LEAD_NAME);
        String leadmobile = searchMap.get(Constants.LEAD_MOBILE);
        if (leadmobile == null)
            leadmobile = "";
        if (leadname == null)
            leadname = "";
        String searchvalue = leadname + "," + leadmobile;
        params.put(Constants.FILTER_VALUES, "," + "" + "," + "" + "," + "," + "" + "," + searchvalue);
        //GCLog.e(searchvalue);
        makeLeadsRequest(false, 1);

        //}

    }

    @Subscribe
    public void filterEvent(FilterEvent filterEvent) {
        if (filterEvent.getCurrentItem() != 501) {
            return;
        }
        filterParams.clear();
        filterParams.putAll(filterEvent.getParams());
        setInitialView();
        makeLeadsRequest(true, 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationController.getEventBus().register(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationController.getEventBus().unregister(this);
    }
}
