package com.gcloud.gaadi.ui;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.EndlessScroll.EndlessScrollListener;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.ActiveInventoriesAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.events.CallLogItemSelectedEvent;
import com.gcloud.gaadi.events.ContactItemSelectedEvent;
import com.gcloud.gaadi.events.ContactOptionSelectedEvent;
import com.gcloud.gaadi.events.EditActiveInventoryPriceEvent;
import com.gcloud.gaadi.events.FilterEvent;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.events.OpenListItemEvent;
import com.gcloud.gaadi.events.RefreshActiveInventoriesEvent;
import com.gcloud.gaadi.events.RefreshInActiveInventoriesEvent;
import com.gcloud.gaadi.events.RemoveFromD2DEvent;
import com.gcloud.gaadi.events.SetTabTextEvent;
import com.gcloud.gaadi.events.ShareTypeEvent;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.InventoriesModel;
import com.gcloud.gaadi.model.InventoryModel;
import com.gcloud.gaadi.model.UpdatePriceModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.swipelistview.BaseSwipeListViewListener;
import com.gcloud.gaadi.ui.swipelistview.SwipeListView;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.gcloud.gaadi.utils.GShareToUtil;
import com.squareup.otto.Subscribe;

import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by ankit on 24/11/14.
 */
public class DealerActiveInventoryFragment extends Fragment

        implements View.OnClickListener, OnNoInternetConnectionListener {

    int pageNo = 1;
    int selectedIndex = 0;
    private GShareToUtil mGShareToUtil;
    private Bundle args;
    private ShareTypeEvent shareTypeEvent;
    private View rootView;
    private Activity activity;
    private SwipeListView listView;
    private View footerView;
    private ArrayList<InventoryModel> inventoriesData;
    private ActiveInventoriesAdapter inventoriesAdapter;
    private LayoutInflater mInflater;
    private View dummyView;
    private Button retry;
    private LinearLayout progressBar;
    private GAHelper mGAHelper;
    private TextView errorMessage;
    private FrameLayout layoutContainer, alternativeLayout;
    private HashMap<String, String> params = new HashMap<String, String>();
    private boolean smsReceiverRegistered = false;
    private boolean isNextPossible = true;
    private String carId = "";
    private String mobileNumber = "";
    /*private final BroadcastReceiver sendSMSReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    mGAHelper.sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_DEALER_PLATFORM,
                            Constants.CATEGORY_DEALER_PLATFORM,
                            Constants.LABEL_SMS_SENT,
                            String.valueOf(CommonUtils.getIntSharedPreference(getFragmentActivity(), Constants.DEALER_ID, -1)),
                            0);

                    makeServerCallForSharedItem(ShareType.SMS);

                    CommonUtils.showToast(getFragmentActivity(), "SMS sent",
                            Toast.LENGTH_SHORT);
                    break;

                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    CommonUtils.showToast(getFragmentActivity(), "Could not send SMS.",
                            Toast.LENGTH_SHORT);
                    break;

                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    CommonUtils.showToast(getFragmentActivity(), "No service",
                            Toast.LENGTH_SHORT);
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    CommonUtils.showToast(getFragmentActivity(), "PDU is empty",
                            Toast.LENGTH_SHORT);
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    CommonUtils.showToast(getFragmentActivity(), "Radio off",
                            Toast.LENGTH_SHORT);
                    break;
            }
        }
    };*/
    private CallLogsDialogFragment callLogsDialogFragment;
    private ContactOptionSelectedEvent contactOptionSelectedEvent;
    private boolean permissionGranted = false;

    private Activity getFragmentActivity() {
        return activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
//        mGAHelper = new GAHelper(getFragmentActivity());
        mGShareToUtil = new GShareToUtil(getFragmentActivity());
        mInflater = activity.getLayoutInflater();
        footerView = mInflater.inflate(R.layout.listview_footer, null);
        ApplicationController.getEventBus().register(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationController.getEventBus().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GCLog.e("gaurav fragment onactivityresult");
        if (requestCode == Constants.STOCKS_CONTACT_LIST) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactUri = data.getData();

                Cursor cursor = getFragmentActivity().getContentResolver().query(contactUri,
                        new String[]{ContactsContract.Contacts._ID},
                        null, null, null);
                cursor.moveToFirst();

                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                Cursor phones = getFragmentActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

                //phones.moveToFirst();

                if (phones.getCount() >= 1) {
                    while (phones.moveToNext()) {
                        String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //GCLog.e(Constants.TAG, "number: " + number);
                        try {
                            mGShareToUtil.sendSMSHelp(number, shareTypeEvent.getShareText(), shareTypeEvent.getCarId());
                        } catch (Exception e) {
                            CommonUtils.showToast(getFragmentActivity(), "Could not send SMS. Invalid phone number.", Toast.LENGTH_SHORT);
                        }
                        int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        switch (type) {
                            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                // do something with the Home number here...
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                // do something with the Mobile number here...
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                // do something with the Work number here...
                                break;
                        }
                    }
                } else {
                    CommonUtils.showToast(getFragmentActivity(), "Cannot send SMS. No contact number present", Toast.LENGTH_SHORT);

                }
                phones.close();

                //GCLog.e(Constants.TAG, "cursor details: " + cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
                cursor.close();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (contactOptionSelectedEvent != null && permissionGranted) {
            onContactOptionSelectedEvent(contactOptionSelectedEvent);
            permissionGranted = false;
        }
    }

    private void makeActiveInventoriesRequest(final boolean showFullPageError, final int pageNumber) {
        //params.put(Constants.LEAD_TYPE, "N");
        progressBar.setVisibility(View.VISIBLE);
        params.put(Constants.METHOD_LABEL, Constants.D2D_ACTIVE_METHOD);
        params.put(Constants.D2D_TAB, Constants.D2D_ACTIVE_TAB);
        params.put("pageNumber", pageNumber + "");
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(getFragmentActivity(),
                Constants.UC_DEALER_USERNAME, ""));

        RetrofitRequest.InventoriesRequest(params, new Callback<InventoriesModel>() {
            @Override
            public void success(InventoriesModel inventoriesModel, retrofit.client.Response response) {
                if ("T".equalsIgnoreCase(inventoriesModel.getStatus())) {
                    ApplicationController.getEventBus().post(
                            new SetTabTextEvent("Active (" + inventoriesModel.getTotalRecords() + ")", Constants.ACTIVE_DEALER_PLATFORM));
                    if (inventoriesModel.getInventories().size() > 0) {

                        isNextPossible = inventoriesModel.getIsNextPossible();
                        if (pageNumber == 1) {

                            initializeList(inventoriesModel);

                            /*Intent intent = new Intent();
                            intent.putExtra("view", "active");
                            intent.putExtra("count", inventoriesModel.getTotalRecords());
                            ApplicationController.getEventBus().post(intent);*/
                        } else {
                            inventoriesData.addAll(inventoriesModel.getInventories());
                            listView.removeFooterView(footerView);
                            inventoriesAdapter.notifyDataSetChanged();
                        }
                    } else {
                        showAddLeadLayout(showFullPageError);
                    }
                } else {
                    if (inventoriesModel.getTotalRecords() == 0) {
                        ApplicationController.getEventBus().post(
                                new SetTabTextEvent("Active (" + inventoriesModel.getTotalRecords() + ")", Constants.ACTIVE_DEALER_PLATFORM));
                        showAddLeadLayout(showFullPageError);
                    } else {
                        showServerErrorLayout(showFullPageError);
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

       /* InventoriesRequest inventoriesRequest = new InventoriesRequest(getFragmentActivity(), Request.Method.POST,
                Constants.getWebServiceURL(getFragmentActivity()),
                params,
                new Response.Listener<InventoriesModel>() {
                    @Override
                    public void onResponse(InventoriesModel response) {
                        GCLog.e("response model: " + response);
                        if ("T".equalsIgnoreCase(response.getStatus())) {
                            if (response.getInventories().size() > 0) {

                                isNextPossible = response.getIsNextPossible();
                                if (pageNumber == 1) {

                                    ApplicationController.getEventBus().post(
                                            new SetTabTextEvent("Active (" + response.getTotalRecords() + ")", Constants.ACTIVE_DEALER_PLATFORM));

                                    initializeList(response);

                                    Intent intent = new Intent();
                                    intent.putExtra("view", "active");
                                    intent.putExtra("count", response.getTotalRecords());
                                    ApplicationController.getEventBus().post(intent);
                                } else {
                                    inventoriesData.addAll(response.getInventories());
                                    listView.removeFooterView(footerView);
                                    inventoriesAdapter.notifyDataSetChanged();
                                }
                            } else {
                                showAddLeadLayout(showFullPageError);
                            }
                        } else {
                            if (response.getTotalRecords() == 0) {
                                showAddLeadLayout(showFullPageError);
                            } else {
                                showServerErrorLayout(showFullPageError);
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

        ApplicationController.getInstance().addToRequestQueue(inventoriesRequest, Constants.TAG_ACTIVE_INVENTORIES, showFullPageError, this);*/
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


    private void initializeList(InventoriesModel response) {
        inventoriesData = response.getInventories();
        layoutContainer.removeAllViews();
        View view = mInflater.inflate(R.layout.fragment_active_inventories, null, false);
        layoutContainer.addView(view);
        listView = (SwipeListView) layoutContainer.findViewById(R.id.inventoriesList);
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onEndlessScroll(int pageNo) {
                if (isNextPossible) {
                    makeActiveInventoriesRequest(false, pageNo);
                    listView.addFooterView(footerView);
                    listView.setAdapter(inventoriesAdapter);
                }
            }

            @Override
            public void handleOpenedTuple() {
                listView.closeOpenedItems();
            }
        });
        listView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onClickFrontView(int position) {
                /*Intent intent = new Intent(getFragmentActivity(), ViewLeadActivity.class);
                startActivity(intent);*/
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


//        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.active_refresh_layout);
//        swipeRefreshLayout.setOnRefreshListener(DealerActiveInventoryFragment.this);

        listView.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
        listView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
        listView.setOffsetLeft(Constants.LIST_ITEM_LEFT_OFFSET);

        inventoriesAdapter = new ActiveInventoriesAdapter(getFragmentActivity(), response.getInventories());
        listView.setAdapter(inventoriesAdapter);
    }

    private void openAnimate(int position) {
        listView.openAnimate(position);
    }

    private void showAddLeadLayout(boolean showFullPageError) {
        setInitialView();
        hideProgressBar();

        View view = mInflater.inflate(R.layout.layout_error, null, false);
        /*alternativeLayout.removeAllViews();*/
        alternativeLayout.addView(view);

        errorMessage = (TextView) view.findViewById(R.id.errorMessage);
        errorMessage.setText(R.string.no_active_inventories_present);

        ((ImageView) view.findViewById(R.id.no_internet_img)).setImageResource(R.drawable.no_result_icons);
        ((TextView) view.findViewById(R.id.checkconnection)).setText("");

        retry = (Button) view.findViewById(R.id.retry);
        /*retry.setTag("ADD_LEAD");
        retry.setText(R.string.add_lead);
        retry.setOnClickListener(this);*/
        retry.setVisibility(View.GONE);

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
        params.put(Constants.PAGE_NO, String.valueOf(pageNo));

        rootView = getFragmentActivity().getLayoutInflater().inflate(R.layout.activity_place_holder, container, false);
        layoutContainer = (FrameLayout) rootView.findViewById(R.id.layoutContainer);
        /*listView = (ListView) rootView.findViewById(R.id.list);*/
        setInitialView();
        makeActiveInventoriesRequest(true, 1);

        return rootView;
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
      //  ApplicationController.getInstance().cancelPendingRequests(Constants.TAG_ACTIVE_INVENTORIES);

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
                    intent = new Intent(getFragmentActivity(), LeadAddOptionActivity.class);
                    startActivity(intent);
                } else {
                    setInitialView();
                    makeActiveInventoriesRequest(true, 1);
                }
                break;
        }
    }

    @Subscribe
    public void filtersReceived(FilterEvent event) {
        if (event.getCurrentItem() != DealerPlatformActivity.FROM_DEALER_PLATFORM) {
            return;
        }
        //setInitialView();
        params.clear();
        params.putAll(event.getParams());

        makeActiveInventoriesRequest(true, 1);
    }

    @Subscribe
    public void onOpenListItem(OpenListItemEvent event) {
        GCLog.e("position to open: " + event.getPosition());
        int position = event.getPosition();
        if (event.getSource() == Constants.OPENLIST_ACTIVE_INVENTORY) {
            this.openAnimate(position);
        }
    }

    @Subscribe
    public void onActiveInventoryRemoved(RemoveFromD2DEvent event) {
        GCLog.e("Remove inventory: " + event.getCarId());
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.CAR_IDS, event.getCarId());
        params.put(Constants.METHOD_LABEL, Constants.REMOVE_FROM_D2D_METHOD);
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(getContext(), Constants.UC_DEALER_USERNAME, ""));
        RetrofitRequest.RemoveFromD2DRequest(getContext(), params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, retrofit.client.Response response) {
                if ("T".equalsIgnoreCase(generalResponse.getStatus())) {
                    CommonUtils.showToast(getFragmentActivity(), generalResponse.getMessage(), Toast.LENGTH_SHORT);
                    ApplicationController.getEventBus().post(new RefreshInActiveInventoriesEvent(true));
                    setInitialView();
                    makeActiveInventoriesRequest(false, 1);

                } else {
                    CommonUtils.showToast(getFragmentActivity(), "Failed to add on Dealer platform", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                CommonUtils.showToast(getFragmentActivity(), "Some problem occurred with our servers. Please try again later.", Toast.LENGTH_SHORT);
            }
        });

      /*  RemoveFromD2DRequest request = new RemoveFromD2DRequest(
                getFragmentActivity(),
                Request.Method.POST,
                Constants.getWebServiceURL(getFragmentActivity()),
                params,
                new Response.Listener<GeneralResponse>() {
                    @Override
                    public void onResponse(GeneralResponse response) {
                        if ("T".equalsIgnoreCase(response.getStatus())) {
                            CommonUtils.showToast(getFragmentActivity(), response.getMessage(), Toast.LENGTH_SHORT);
                            ApplicationController.getEventBus().post(new RefreshInActiveInventoriesEvent(true));
                            setInitialView();
                            makeActiveInventoriesRequest(false, 1);

                        } else {
                            CommonUtils.showToast(getFragmentActivity(), "Failed to add on Dealer platform", Toast.LENGTH_SHORT);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CommonUtils.showToast(getFragmentActivity(), "Some problem occurred with our servers. Please try again later.", Toast.LENGTH_SHORT);
                    }
                }
        );

        ApplicationController.getInstance().addToRequestQueue(request, Constants.TAG_REMOVE_D2D, false, this);*/
    }

    @Subscribe
    public void onActiveInventoriesRefresh(RefreshActiveInventoriesEvent event) {
        GCLog.e("event for refresh: " + event);
        setInitialView();
        makeActiveInventoriesRequest(false, 1);
    }

    @Subscribe
    public void onPriceUpdateEvent(EditActiveInventoryPriceEvent event) {
        GCLog.e("event for update: " + event);
        showPriceUpdateDialog(event);
    }

    private void showPriceUpdateDialog(final EditActiveInventoryPriceEvent event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getFragmentActivity());
        View view = getFragmentActivity().getLayoutInflater().inflate(R.layout.layout_update_price_dialog, null, false);
        TextView textView = (TextView) view.findViewById(R.id.text);
        final EditText editText = (EditText) view.findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                insertCommaIntoNumber(editText, s, "#,##,##,###");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        textView.setText("Current price: " + event.getCurrentPrice());
        builder.setView(view);

        AlertDialog dialog = builder.setTitle(R.string.update_price)
                .setPositiveButton(R.string.done_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String updatedPrice = editText.getText().toString().trim();
                        if (updatedPrice.length() < 4) {
                            CommonUtils.showToast(getFragmentActivity(), "Please enter valid amount.", Toast.LENGTH_SHORT);
                        } else {
                            makeUpdatePriceRequest(event, updatedPrice);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listView.clearChoices();
                        inventoriesAdapter.notifyDataSetChanged();
                    }
                })
                .create();

        if (!getFragmentActivity().isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }

    }

    protected void insertCommaIntoNumber(EditText etText, CharSequence s, String format) {
        try {
            if (s.toString().length() > 0) {
                String convertedStr = s.toString();
                if (s.toString().contains(".")) {
                    if (chkConvert(s.toString()))
                        convertedStr = customFormat(format, Double.parseDouble(s.toString().replace(",", "")));
                } else {
                    convertedStr = customFormat(format, Double.parseDouble(s.toString().replace(",", "")));
                }


                if (!etText.getText().toString().equals(convertedStr) && convertedStr.length() > 0) {
                    etText.setText(convertedStr);
                    etText.setSelection(etText.getText().length());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String customFormat(String pattern, double value) {
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        String output = myFormatter.format(value);
        return myFormatter.format(value);

    }

    private boolean chkConvert(String s) {
        String tempArray[] = s.split("\\.");
        if (tempArray.length > 1) {
            return Integer.parseInt(tempArray[1]) > 0;
        } else
            return false;
    }

    private void makeUpdatePriceRequest(EditActiveInventoryPriceEvent event, String updatedPrice) {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.CAR_ID, event.getCarId());
        params.put(Constants.UPDATED_DEALER_PRICE, updatedPrice.trim().replace(",", ""));
        params.put(Constants.METHOD_LABEL, Constants.D2D_UPDATE_PRICE_METHOD);
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(getFragmentActivity(), Constants.UC_DEALER_USERNAME, ""));
        RetrofitRequest.EditPriceRequest(params, new Callback<UpdatePriceModel>() {
            @Override
            public void success(UpdatePriceModel updatePriceModel, retrofit.client.Response response) {
                if ("T".equalsIgnoreCase(updatePriceModel.getStatus())) {
                    makeActiveInventoriesRequest(false, 1);

                } else {
                    CommonUtils.showToast(getFragmentActivity(), updatePriceModel.getErrorMessage(), Toast.LENGTH_SHORT);

                }
            }

            @Override
            public void failure(RetrofitError error) {
                CommonUtils.showToast(getFragmentActivity(), "Server Error. Failed to update price", Toast.LENGTH_SHORT);

            }
        });
       /*    EditPriceRequest request = new EditPriceRequest(
             getFragmentActivity(),
                Request.Method.POST,
                Constants.getWebServiceURL(getFragmentActivity()),
                params,
                new Response.Listener<UpdatePriceModel>() {
                    @Override
                    public void onResponse(UpdatePriceModel response) {
                        if ("T".equalsIgnoreCase(response.getStatus())) {
                            makeActiveInventoriesRequest(false, 1);

                        } else {
                            CommonUtils.showToast(getFragmentActivity(), response.getErrorMessage(), Toast.LENGTH_SHORT);

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CommonUtils.showToast(getFragmentActivity(), "Server Error. Failed to update price", Toast.LENGTH_SHORT);
                    }
                }
        );

        ApplicationController.getInstance().addToRequestQueue(request, Constants.TAG_EDIT_PRICE, false, this);*/
    }

    @Subscribe
    public void onShareTypeEvent(ShareTypeEvent event) {
        ContactsPickerFragment contactsPickerFragment;
        Bundle args = new Bundle();
        shareTypeEvent = event;
        switch (event.getShareType()) {
            case SMS:
                GCLog.e("Share type sms");
                contactsPickerFragment = ContactsPickerFragment.newInstance(
                        getFragmentActivity().getString(R.string.select_contact_from),
                        selectedIndex,
                        event.getShareText(),
                        event.getShareType(),
                        event.getCarId(),
                        event.getImageURL()
                );

                contactsPickerFragment.show(getFragmentManager(), "contacts-picker-fragment");
                break;

            case WHATSAPP:
                GCLog.e("Share type whatsapp");
                this.carId = event.getCarId();
                contactsPickerFragment = ContactsPickerFragment.newInstance(
                        getFragmentActivity().getString(R.string.select_contact_from),
                        selectedIndex,
                        event.getShareText(),
                        event.getShareType(),
                        event.getCarId(),
                        event.getImageURL()
                );

                contactsPickerFragment.show(getFragmentManager(), "contacts-picker-fragment");
                break;
        }
    }

    @Subscribe
    public void onCloseListItem(OpenListItemEvent event) {
        GCLog.e("position to open: " + event.getPosition());
        int position = event.getPosition();
        int source = event.getSource();
        if (source == Constants.OPENLIST_ACTIVE_INVENTORY)
            this.closeAnimate(position);
    }

    private void closeAnimate(int position) {
        listView.closeAnimate(position);
    }

  /*  private void makeServerCallForSharedItem(ShareType shareType) {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.CAR_ID, carId);
        params.put(Constants.MOBILE_NUM, mobileNumber);
        params.put(Constants.SHARE_TYPE, shareType.name());
        params.put(Constants.SHARE_SOURCE, Constants.CATEGORY_DEALER_PLATFORM);

        ShareCarsRequest shareCarsRequest = new ShareCarsRequest(getFragmentActivity(),
                Request.Method.POST,
                Constants.getWebServiceURL(getFragmentActivity()),
                params,
                new Response.Listener<GeneralResponse>() {
                    @Override
                    public void onResponse(GeneralResponse response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        ApplicationController.getInstance().addToRequestQueue(shareCarsRequest);
    }*/

    @Subscribe
    public void onContactOptionSelectedEvent(ContactOptionSelectedEvent event) {
        selectedIndex = event.getSelectedIndex();
        contactOptionSelectedEvent = event;
        switch (event.getContactType()) {
            case CALL_LOGS:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !CommonUtils.checkForPermission(activity,
                        new String[]{Manifest.permission.READ_CALL_LOG},
                        Constants.REQUEST_PERMISSION_READ_CALL_LOG, "Phone")) {
                    return;
                }
                mGShareToUtil.showCallLogsDialog(event.getShareText(), event.getShareType(), event.getCarId());
                break;

            case CONTACTS:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !CommonUtils.checkForPermission(activity,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        Constants.REQUEST_PERMISSION_CONTACTS, "Contacts")) {
                    return;
                }
                mGShareToUtil.showContactsDialog(event.getShareText(), event.getShareType(), event.getCarId(), event.getImageUrl());
                break;
            case NEW_CONTACT:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !CommonUtils.checkForPermission(activity,
                        new String[]{Manifest.permission.WRITE_CONTACTS},
                        Constants.REQUEST_PERMISSION_CONTACTS, "Contacts")) {
                    return;
                }
                mGShareToUtil.showAddNameToContactDialog(event.getShareText(), carId);
                break;
            case SEND_TO_NUMBER:
                mGShareToUtil.showAddNewNumToSendSMSDialog(event.getShareText(), carId);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION_READ_CALL_LOG
                || requestCode == Constants.REQUEST_PERMISSION_CONTACTS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //onContactOptionSelectedEvent(contactOptionSelectedEvent);
                permissionGranted = true;
            }
        }
    }

    @Subscribe
    public void OnCallLogContactSelected(CallLogItemSelectedEvent event) {
        mobileNumber = event.getCallLogItem().getGaadiFormatNumber();
        if (event.getShareType() == ShareType.WHATSAPP) {
            if ("null".equalsIgnoreCase(event.getCallLogItem().getName()) || event.getCallLogItem().getName() == null) {
                mGShareToUtil.showAddNameToContactDialog(event.getCallLogItem().getNumber(), event.getShareText(), event.getImageUrl());

            } else {
                mGShareToUtil.sendMessageInWhatsApp(event.getShareText(), event.getImageUrl());
            }

        } else if (event.getShareType() == ShareType.SMS) {
            try {
                mGShareToUtil.sendSMSHelp(event.getCallLogItem().getNumber(), event.getShareText(), event.getCarId());
            } catch (Exception e) {
                CommonUtils.showToast(getFragmentActivity(), "Could not send SMS. Invalid number", Toast.LENGTH_SHORT);
            }
        }
    }

    @Subscribe
    public void OnContactItemSelected(ContactItemSelectedEvent event) {
        mobileNumber = event.getContactListItem().getGaadiFormatNumber();
        if (event.getShareType() == ShareType.WHATSAPP) {
            if ("null".equalsIgnoreCase(event.getContactListItem().getContactName()) || event.getContactListItem().getContactName() == null) {
                mGShareToUtil.showAddNameToContactDialog(
                        event.getContactListItem().getContactNumber(),
                        event.getShareText(), event.getImageUrl());
            } else {
                mGShareToUtil.sendMessageInWhatsApp(event.getShareText(), event.getImageUrl());
            }


        }
    }
}
