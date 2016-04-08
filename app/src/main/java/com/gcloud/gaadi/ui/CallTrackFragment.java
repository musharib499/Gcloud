package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.CallTrackAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.interfaces.CallTrackClickInterface;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.CallTrackModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.utils.CommonUtils;

import java.net.UnknownHostException;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

public class CallTrackFragment extends Fragment implements OnNoInternetConnectionListener {

    private static CallTrackFragment mInstance;
    CallTrackModel callTrackModel;
    private CallTrackAdapter callTrackAdapter;
    private ListView listView;
    private CallTrackClickInterface mListener;
    private Activity activity;
    private String callSource = "";
    private boolean shown;
    private ProgressBar progressBar;


    public CallTrackFragment() {

    }

    public static CallTrackFragment getInstance() {
        if (mInstance == null) {
            mInstance = new CallTrackFragment();
        }

        return mInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity != null) {
//			getLoaderManager().initLoader(Constants.CALL_LOGS_LOADER, null,
//					this);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.CALL_TRACKER_LIST, callTrackModel);
        outState.putString(Constants.CALL_SOURCE, callSource);
    }

    private Activity getFragmentActivity() {
        return activity;
    }

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater inflater = getFragmentActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.call_log, container, false);
        listView = (ListView) view.findViewById(R.id.logsList);

        //mCursor = getCallLogsCursor();
//		callTrackAdapter = new CallTrackAdapter(getActivity(), mCursor);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
//		
        /*Bundle args = getArguments();
        if (args != null) {
            callTrackModel = (CallTrackModel)
                    args.getSerializable(Constants.MODEL_DATA);
            callSource = args.getString(Constants.CALL_SOURCE);

        }

        if (savedInstanceState != null) {
            callTrackModel = (CallTrackModel) savedInstanceState.getSerializable(Constants.CALL_TRACKER_LIST);
            callSource = savedInstanceState.getString(Constants.CALL_SOURCE);
        }*/

        //callTrackAdapter = new CallTrackAdapter(getActivity(), callTrackModel.getCustomers());
        //listView.setAdapter(callTrackAdapter);
        //listView.setOnItemClickListener(this);

        /*AlertDialog.Builder builder = new AlertDialog.Builder(
                getFragmentActivity());
        builder.setView(view);
        builder.setTitle("Call Tracker Leads");

        if (getFragmentActivity() instanceof CallLogsItemClickInterface) {
            mListener = (CallTrackClickInterface) getFragmentActivity();
        }*/

        return view;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        makeCallTrackApiCall();
    }

    private void makeCallTrackApiCall() {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.DEALER_USERNAME, CommonUtils
                .getStringSharedPreference(activity, Constants.UC_DEALER_USERNAME,
                        ""));
        params.put(Constants.DEALER_PASSWORD, CommonUtils
                .getStringSharedPreference(activity, Constants.UC_DEALER_PASSWORD,
                        ""));
        params.put(Constants.UCDID, String.valueOf(CommonUtils
                .getIntSharedPreference(activity, Constants.UC_DEALER_ID, -1)));

        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
        params.put(Constants.METHOD_LABEL, Constants.CALL_TRACK_API);


        RetrofitRequest.callTrackRequest(params, new Callback<CallTrackModel>() {

          /*  @Override
            public void success(StockDetailModel response, retrofit.client.Response res) {

                if ("T".equalsIgnoreCase(response.getStatus())) {
                    layoutContainer.removeAllViews();
                    View view = mInflater.inflate(R.layout.activity_view_stock_detail_page, null, false);
                    layoutContainer.addView(view);
                    stockDetailModel = response;
                    initializeViews(layoutContainer);
                    showOverflowMenuItem = true;
                    // invalidateOptionsMenu();
                    updateMenuText();
                    //  onPrepareOptionsMenu(StockViewActivity.this.menu);
                } else {
                    showServerErrorLayout(true);
                }

            }*/

            @Override
            public void success(CallTrackModel callTrackModel, retrofit.client.Response response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                if (callTrackModel.getStatus().equals("T")) {
                    Log.i("ankur", "onResponse T");
                    if (callTrackModel.getCustomers() != null) {
                        if (callTrackModel.getCustomers().size() > 0) {
                            //showCallTrackerDialog(response);
                            showList(callTrackModel);
                        }
                    } else {
                        CommonUtils.showToast(activity,
                                callTrackModel.getMessage(),
                                Toast.LENGTH_SHORT);
                    }

                } else {
                    CommonUtils.showToast(activity,
                            callTrackModel.getErrorMessage(),
                            Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void failure(RetrofitError error) {

                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                if (error.getCause() instanceof UnknownHostException && activity != null && isAdded()) {
                    CommonUtils.showToast(activity, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                } else {
                    if (activity != null && isAdded()) {
                        CommonUtils.showToast(activity,
                                getString(R.string.server_error_message), Toast.LENGTH_LONG);
                    }
                }
            }

        });


        /*CallTrackRequest leadAddRequest = new CallTrackRequest(
                activity,
                Request.Method.POST,
                Constants.getWebServiceURL(activity),
                params,
                new Response.Listener<CallTrackModel>() {
                    @Override
                    public void onResponse(CallTrackModel response) {
                        Log.i("ankur","onResponse ");
                        if (progressBar != null) {
                            progressBar.dismiss();
                        }

                        if (response.getStatus().equals("T")) {
                            Log.i("ankur","onResponse T");
                            if (response.getCustomers() != null) {
                                if (response.getCustomers().size() > 0) {
                                    //showCallTrackerDialog(response);
                                    showList(response);
                                }
                            } else {
                                CommonUtils.showToast(activity,
                                        response.getMessage(),
                                        Toast.LENGTH_SHORT);
                            }

                        } else {
                            CommonUtils.showToast(activity,
                                    response.getErrorMessage(),
                                    Toast.LENGTH_SHORT);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ankur","onErrorResponse ");
                if (progressBar != null) {
                    progressBar.dismiss();
                }

                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(activity, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                } else {
                    CommonUtils.showToast(activity,
                            getString(R.string.server_error_message), Toast.LENGTH_LONG);
                }
            }
        });

        ApplicationController.getInstance().addToRequestQueue(leadAddRequest,
                Constants.TAG_STOCKS_LIST, false, this);*/
    }

    public void showList(CallTrackModel callTrackModel) {
        if (getFragmentActivity() != null && !getFragmentActivity().isFinishing()) {
            callTrackAdapter = new CallTrackAdapter(getFragmentActivity(), callTrackModel.getCustomers());
            listView.setAdapter(callTrackAdapter);
        }

    }

    @Override
    public void onNoInternetConnection(NetworkEvent networkEvent) {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        CommonUtils.showToast(activity, getString(R.string.network_error), Toast.LENGTH_LONG);
    }


 /*   @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        CustomerModel cmodel = (CustomerModel) parent.getAdapter().getItem(position);


        CustomerModel cm = new CustomerModel(cmodel.getMobile(), cmodel.getName(), cmodel.getStatus(), cmodel.getDate(), callSource);


        dismiss();

        if (mListener != null) {
            mListener.onCallTrackkSelected(cm);
        }

    }

    public void show(FragmentManager manager, String tag) {
        if (shown) return;

        super.show(manager, tag);
        shown = true;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        shown = false;
        super.onDismiss(dialog);
    }

    public boolean isShowing() {
        return shown;
    }*/

}
