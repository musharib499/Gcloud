package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.gcloud.gaadi.adapter.LeadsAdapter;
import com.gcloud.gaadi.constants.AdapterType;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.LeadsOfflineDB;
import com.gcloud.gaadi.events.FilterEvent;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.events.SearchEvent;
import com.gcloud.gaadi.interfaces.OnApplyFilterListener;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.LeadDetailModel;
import com.gcloud.gaadi.model.LeadsModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.syncadapter.GenericAccountService;
import com.gcloud.gaadi.syncadapter.SyncUtils;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by ankit on 6/1/15.
 */
public class NotYetCalledFollowUpLeadsFragment extends Fragment
        implements OnNoInternetConnectionListener, View.OnClickListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, OnApplyFilterListener {

    private final Uri LEADS_URI = Uri.parse(new StringBuilder().append("content://")
            .append(Constants.LEADS_CONTENT_AUTHORITY).append("/").append(LeadsOfflineDB.TABLE_NAME).toString());

    String fromSearchEvent = "";
    int pageNo = 1;
    private View rootView;
    private Activity activity;
    private ListView listView;
    //    private GAHelper mGAHelper;
    private LinearLayout showAllLeads;
    private LeadsAdapter leadsAdapter;
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
    private LeadsModel model = null;
    private String selection = "";
    private ArrayList<String> selectionList = new ArrayList<>();
    private ImageView imageView;
    private TextView refineFilter;
    private boolean isOttoRegistered = false;

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
    }

    private void makeLeadsRequest(final boolean showFullPageError, final int pageNumber) {

        LeadsOfflineDB db = ApplicationController.getLeadsOfflineDB();
        if (pageNumber == 1/* && !ApplicationController.checkInternetConnectivity()*/) {
            model = getLeadsModel(pageNumber, selection, selectionList);
        }
        if ((model == null || model.getLeads().size() == 0)) {
            //GCLog.e("Network Loading");
            if (!ApplicationController.checkInternetConnectivity()) {
                showAddLeadLayout(showFullPageError);
                return;
            }
            ContentResolver.requestSync(GenericAccountService.GetAccount(SyncUtils.ACCOUNT_TYPE),
                    Constants.LEADS_CONTENT_AUTHORITY, new Bundle());

            params.put(Constants.LEAD_TYPE, "N");
            params.put(Constants.PAGE_NO, pageNumber + "");
            params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
            params.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
            params.put(Constants.METHOD_LABEL, Constants.LEADS_METHOD);
            params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(getFragmentActivity(), Constants.UC_DEALER_USERNAME, ""));
            params.put(Constants.DEALER_PASSWORD, CommonUtils.getStringSharedPreference(getFragmentActivity(), Constants.UC_DEALER_PASSWORD, ""));

            params.put("changetime",
                    CommonUtils.MillisToSQLTime(CommonUtils.getLongSharedPreference(getFragmentActivity(),
                            Constants.LEADS_LAST_SYNCED_TIME,
                            (CommonUtils.getLongSharedPreference(getFragmentActivity(), Constants.SERVER_TIME_DIFFERENCE, 0) +
                                    DateTime.now().minusMonths(1).withTimeAtStartOfDay().getMillis()))));
            RetrofitRequest.LeadsRequest(params, new Callback<LeadsModel>() {
                @Override
                public void success(LeadsModel leadsModel, retrofit.client.Response response) {
                    if (leadsModel != null && "T".equalsIgnoreCase(leadsModel.getStatus())) {
                        if (leadsModel.getTotalRecords() > 0) {
                            nextPossible = leadsModel.getNextPossible();
                            if (pageNumber == 1) {
                                initializeViews(leadsModel);
                            } else {
                                leadsData.addAll(leadsModel.getLeads());
                                //listView.removeFooterView(footerView);
                                leadsAdapter.notifyDataSetChanged();

                            }
                            listView.setOnItemClickListener(NotYetCalledFollowUpLeadsFragment.this);

                        } else {
                            showAddLeadLayout(showFullPageError);
                        }
                    } else {
                        showNoRecordsMessage(showFullPageError, leadsModel.getMessage());
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
           /* LeadsRequest leadsRequest = new LeadsRequest(getFragmentActivity(), Request.Method.POST,
                    Constants.getWebServiceURL(getFragmentActivity()),
                    params,
                    new Response.Listener<LeadsModel>() {
                        @Override
                        public void onResponse(LeadsModel response) {
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
                                    listView.setOnItemClickListener(NotYetCalledFollowUpLeadsFragment.this);

                                } else {
                                    showAddLeadLayout(showFullPageError);
                                }
                            } else {
                                showNoRecordsMessage(showFullPageError, response.getMessage());
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
*/
        } else {
            //GCLog.e("Loading from DB");
            model = getLeadsModel(pageNumber,
                    selection,
                    selectionList);
            if (model == null) {
                nextPossible = false;
                //listView.removeFooterView(footerView);
                return;
            }
            /*if (model.getLeads().size() < 10) {
                nextPossible = false;
            } else {
                nextPossible = true;
            }*/
            nextPossible = model.getLeads().size() > 9;
            if (pageNumber == 1) {
                initializeViews(model);
            } else {
                leadsData.addAll(model.getLeads());
                //listView.removeFooterView(footerView);
                leadsAdapter.notifyDataSetChanged();

            }
            listView.setOnItemClickListener(NotYetCalledFollowUpLeadsFragment.this);
        }
    }

    private LeadsModel getLeadsModel(int pageNumber, String select, ArrayList<String> selectList) {
        LeadsModel model = null;
        ArrayList<String> selectionList = new ArrayList<>();
        StringBuilder selection = new StringBuilder();
        if (!select.isEmpty()) {
            selection.append(select).append(" AND ");
            selectionList.addAll(selectList);
        }
        selection.append(LeadsOfflineDB.LEAD_TYPE + " = ? AND " + LeadsOfflineDB.FOLLOW_DATE_ANDROID);
        selectionList.add("Buyer");
        selection.append(" = ?");
        selectionList.add("0");
        selection.append(" AND " + LeadsOfflineDB.LEAD_STATUS + " NOT IN (?, ?)");
        selectionList.add("Closed");
        selectionList.add("Converted");

        String limit = "10 OFFSET " + String.valueOf((pageNumber - 1) * 10);

        Cursor cursor = getFragmentActivity().getContentResolver().query(LEADS_URI,
                new String[]{LeadsOfflineDB.JSON_FORMAT},
                selection.toString(), selectionList.toArray(new String[selectionList.size()]),
                LeadsOfflineDB.CHANGE_TIME + " DESC " + "LIMIT " + limit);

        if (cursor == null) {
            return null;
        }

        if (cursor.moveToFirst()) {
            model = new LeadsModel();
            model.setLeads(new ArrayList<LeadDetailModel>());
            //model = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(JSON_DATA)), LeadsModel.class);
            while (!cursor.isAfterLast()) {
                model.getLeads().add(new Gson().fromJson(
                        cursor.getString(cursor.getColumnIndex(LeadsOfflineDB.JSON_FORMAT)), LeadDetailModel.class));
                cursor.moveToNext();
            }
            cursor.close();
        } else {
            cursor.close();
        }
        return model;
    }

    private void initializeViews(LeadsModel response) {
        layoutContainer.removeAllViews();
        View view = mInflater.inflate(R.layout.fragment_past_leads, null, false);
        layoutContainer.addView(view);
        listView = (ListView) layoutContainer.findViewById(R.id.list);
        showAllLeads = (LinearLayout) layoutContainer.findViewById(R.id.showallleads);
        if (fromSearchEvent.equals("search")) {
            showAllLeads.setVisibility(View.VISIBLE);
        } else {
            showAllLeads.setVisibility(View.GONE);
        }
        /*if (nextPossible)
            listView.addFooterView(footerView, null, false);*/
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onEndlessScroll(int pageNo) {

                if (nextPossible) {
                    //listView.addFooterView(footerView, null, false);
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
                selection = "";
                selectionList.clear();
                showAllLeads.setVisibility(View.GONE);
                makeLeadsRequest(false, 1);
                leadsAdapter.notifyDataSetChanged();
            }
        });
        leadsData = response.getLeads();
        leadsAdapter = new LeadsAdapter(getFragmentActivity(), response.getLeads(), AdapterType.NOT_YET_CALLED);
        listView.setAdapter(leadsAdapter);
        //leadsAdapter.notifyDataSetChanged();
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
            View view = mInflater.inflate(R.layout.layout_error, null, false);

            alternativeLayout.addView(view);

            errorMessage = (TextView) view.findViewById(R.id.errorMessage);
            errorMessage.setText(R.string.network_connection_error_message);

            retry = (Button) view.findViewById(R.id.retry);
            retry.setOnClickListener(this);
            CommonUtils.showToast(getFragmentActivity(), getFragmentActivity().getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
        }
    }

    private void showNoRecordsMessage(boolean showFullPageError, String message) {
        if (showFullPageError) {

            setInitialView();
            hideProgressBar();

            View view = mInflater.inflate(R.layout.layout_error, null, false);

            alternativeLayout.addView(view);

            errorMessage = (TextView) view.findViewById(R.id.errorMessage);
            errorMessage.setText(message);
            refineFilter = (TextView) view.findViewById(R.id.checkconnection);
           // refineFilter.setText(R.string.refine_filter);
            refineFilter.setVisibility(View.INVISIBLE);
            imageView = (ImageView) view.findViewById(R.id.no_internet_img);
            imageView.setImageResource(R.drawable.no_result_icons);
            retry = (Button) view.findViewById(R.id.retry);
            retry.setVisibility(View.GONE);

        } else {
            // CommonUtils.showToast(InsuranceInspectedCarsListActivity.this, "No more records found", Toast.LENGTH_SHORT);

        }
        // retry.setTag("ADD STOCK");

        //retry.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        isOttoRegistered = true;
        ApplicationController.getEventBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        //ApplicationController.getEventBus().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isOttoRegistered) {
            ApplicationController.getEventBus().unregister(this);
        }
    }

    private void showAddLeadLayout(boolean showFullPageError) {
        setInitialView();
        hideProgressBar();

        View view = mInflater.inflate(R.layout.layout_error, null, false);
        /*alternativeLayout.removeAllViews();*/
        alternativeLayout.addView(view);

        errorMessage = (TextView) view.findViewById(R.id.errorMessage);
        refineFilter = (TextView) view.findViewById(R.id.checkconnection);
        refineFilter.setVisibility(View.INVISIBLE);
        //refineFilter.setText(R.string.refine_filter);
        errorMessage.setText(R.string.no_lead_present);
        imageView = (ImageView) view.findViewById(R.id.no_internet_img);
        imageView.setImageResource(R.drawable.no_lead_icon);

        retry = (Button) view.findViewById(R.id.retry);
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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        params.put(Constants.PAGE_NO, String.valueOf(pageNo));

        rootView = getFragmentActivity().getLayoutInflater().inflate(R.layout.activity_place_holder, container, false);
        layoutContainer = (FrameLayout) rootView.findViewById(R.id.layoutContainer);

//        getActivity().findViewById(R.id.resetLeadsFilter).setOnClickListener(this);

        /*listView = (ListView) rootView.findViewById(R.id.list);*/
        setInitialView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                makeLeadsRequest(true, 1);
            }
        }, 200);

        return rootView;
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
    public void onNoInternetConnection(NetworkEvent networkEvent) {
        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                Constants.CATEGORY_SELLER_LEADS,
                Constants.CATEGORY_SELLER_LEADS,
                Constants.ACTION_TAP,
                Constants.LABEL_NO_INTERNET,
                0);
       // ApplicationController.getInstance().cancelPendingRequests(Constants.TAG_N_LEADS);

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
        Intent intent;
        Bundle bundle = new Bundle();
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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle args = new Bundle();
        LeadDetailModel model = (LeadDetailModel) parent.getItemAtPosition(position);
        Intent intent = new Intent(getFragmentActivity(), LeadAddActivity.class);
        args.putString(Constants.VIEW_LEAD, Constants.VALUE_VIEWLEAD);
        args.putInt(Constants.SELECTED_TAB, Constants.NOT_YET_CALLED_FRAG_NO);
        args.putSerializable(Constants.MODEL_DATA, model);
        args.putString(Constants.CALL_SOURCE, "ML"); // Manage Lead
        intent.putExtras(args);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        ApplicationController.getEventBus().post("unregister");

        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        //GCLog.e("Not Yet Called");
        setInitialView();
        makeLeadsRequest(false, 1);
    }

    @Subscribe
    public void onFilterEvent(FilterEvent filterEvent) {
        //GCLog.e("True");
        //if (filterEvent.getCurrentItem() == Constants.NOT_YET_CALLED_FRAG_NO) {

            selection = "";
            selectionList.clear();

            HashMap<String, String> filterMap = filterEvent.getParams();
            String source = filterMap.get(Constants.FILTER_SOURCE);
            String status = filterMap.get(Constants.FILTER_STAUS);
            String budgetFrom = filterMap.get(Constants.FILTER_PRICE);
            String budgetTo = filterMap.get(Constants.FILTER_PRICE_TO);
        String verified = filterMap.get(Constants.VERIFIED);

            StringBuilder select = new StringBuilder();

        if (!CommonUtils.isValidField(source)) {   //if (source == null) {
            source = "";
        } else {
            //GCLog.e("source" + source);
            select.append("lower(").append(LeadsOfflineDB.SOURCE).append(") ")
                    .append(" in (").append(obtainSeparators(source)).append(")");
            selectionList.addAll(rebuildParamsForMultiSelect(source.toLowerCase()));
        }
        if (!CommonUtils.isValidField(status)) {   //if (status == null) {
            status = "";
        } else {
            //GCLog.e("status" + status);
            if (select.length() != 0) {
                select.append(" AND ");
            }
            select.append("lower(").append(LeadsOfflineDB.LEAD_STATUS).append(") ")
                    .append(" in (").append(obtainSeparators(status)).append(")");
            selectionList.addAll(rebuildParamsForMultiSelect(status.toLowerCase()));
        }
        if (!CommonUtils.isValidField(budgetFrom)) {   //if (budgetFrom == null) {
            budgetFrom = "";
        } else {
            if (select.length() != 0) {
                select.append(" AND ");
            }
            select.append("CAST(lower(").append(LeadsOfflineDB.BUDGET_FROM).append(") as INTEGER) ").append(" >= ?")
                    .append(" AND CAST(lower(").append(LeadsOfflineDB.BUDGET_TO).append(") as INTEGER) ").append(" >= ?");
            selectionList.add(budgetFrom.toLowerCase());
            selectionList.add(budgetFrom.toLowerCase());
        }
        if (!CommonUtils.isValidField(budgetTo)) {   //if (budgetTo == null) {
            budgetTo = "";
        } else {
            if (select.length() != 0) {
                select.append(" AND ");
            }
            select.append("CAST(lower(").append(LeadsOfflineDB.BUDGET_TO).append(") as INTEGER) ").append(" <= ?")
                    .append(" AND CAST(lower(").append(LeadsOfflineDB.BUDGET_FROM).append(") as INTEGER) ").append(" <= ?");
            selectionList.add(budgetTo.toLowerCase());
            selectionList.add(budgetTo.toLowerCase());
        }
        if (!CommonUtils.isValidField(verified)) {
            verified = "";
        } else {
            if (select.length() != 0) {
                select.append(" AND ");
            }
            select.append("lower(").append(LeadsOfflineDB.VERIFIED).append(") ").append(" = ?");
            selectionList.add(verified);
        }

            selection = select.toString();

        String filterValue = "," + budgetFrom + "," + budgetTo + "," + status + "," + source + "," + "" + "," + "" + "," + verified;
            params.put(Constants.FILTER_VALUES, filterValue);
            //Log.e("Filter Value", filterValue);
        setInitialView();
            makeLeadsRequest(false, 1);
        //}
    }

    private ArrayList<String> rebuildParamsForMultiSelect(String string) {
        return new ArrayList<>(Arrays.asList(string.split(",")));
    }

    private String obtainSeparators(String string) {
        String[] values = string.split(",");
        StringBuilder separators = new StringBuilder();
        for (String value : values) {
            if (separators.length() > 0) {
                separators.append(",");
            }
            separators.append("?");
        }
        return separators.toString();
    }

    @Subscribe
    public void onSearchEvent(SearchEvent searchEvent) {

        //GCLog.e("True");
        // if(searchEvent.getCurrentItem()==Constants.NOT_YET_CALLED_FRAG_NO) {
        fromSearchEvent = "search";

        selection = "";
        selectionList.clear();
        HashMap<String, String> searchMap = searchEvent.getParams();
        String searchKey = searchMap.get(Constants.LEAD_SEARCH_KEY);
        String leadname = searchMap.get(Constants.LEAD_NAME);
        String leadmobile = searchMap.get(Constants.LEAD_MOBILE);
        if (leadmobile == null) {
            leadmobile = "";
        } else {
            selection = LeadsOfflineDB.NUMBER + " LIKE ?";
            selectionList.add(new StringBuilder().append("%").append(leadmobile).append("%").toString());
        }
        if (leadname == null) {
            leadname = "";
        } else {
            selection = LeadsOfflineDB.NAME + " LIKE ?";
            selectionList.add(new StringBuilder().append("%").append(leadname).append("%").toString());
        }
        String searchvalue = leadname + "," + leadmobile;
        params.put(Constants.FILTER_VALUES, "," + "" + "," + "" + "," + "," + "" + "," + searchvalue);
        //GCLog.e(searchvalue);
        makeLeadsRequest(false, 1);

        //}

    }

    @Override
    public void onFilterApplied() {
        //GCLog.e("True");
    }
}
