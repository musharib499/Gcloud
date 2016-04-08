package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.gcloud.gaadi.EndlessScroll.EndlessScrollListener;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.SimilarCarsAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.ContactType;
import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.events.OpenListItemEvent;
import com.gcloud.gaadi.interfaces.CallLogsItemClickInterface;
import com.gcloud.gaadi.interfaces.OnContactSelectedListener;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.CallLogItem;
import com.gcloud.gaadi.model.ContactListItem;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.InventoriesModel;
import com.gcloud.gaadi.model.InventoryModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.swipelistview.BaseSwipeListViewListener;
import com.gcloud.gaadi.ui.swipelistview.SwipeListView;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.gcloud.gaadi.utils.GShareToUtil;
import com.squareup.otto.Subscribe;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

public class SentCarsFragment extends Fragment implements View.OnClickListener,
        OnNoInternetConnectionListener, SwipeRefreshLayout.OnRefreshListener, ContactsPickerFragment.OnContactsOptionSelectedListener, CallLogsItemClickInterface, OnContactSelectedListener {

    int selectedIndex = 0;
    private View rootView;
    private Activity activity;
    //    private GAHelper mGAHelper;
    private SwipeListView listView;
    private View footerView;
    private ArrayList<InventoryModel> inventoriesData;
    private SimilarCarsAdapter sentcarAdater;
    private LayoutInflater mInflater;
    private View dummyView;
    private Button retry;
    private LinearLayout progressBar;
    private TextView errorMessage;
    private String leadMobilenum = "";
    private String leadName = "";
    private FrameLayout layoutContainer, alternativeLayout;
    private HashMap<String, String> params = new HashMap<String, String>();
    private boolean smsReceiverRegistered = false;
    private boolean isNextPossible = true;
    private String carId = "";
    private String mobileNumber = "";
    private CallLogsDialogFragment callLogsDialogFragment;
    private GShareToUtil mGShareToUtil;
    private ImageView imageView;
    private TextView textView;

    private Activity getFragmentActivity() {
        return activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
//        mGAHelper = new GAHelper(activity);
        mInflater = activity.getLayoutInflater();
        params = (HashMap<String, String>) activity.getIntent().getExtras()
                .get("hashMapKey");
        leadMobilenum = activity.getIntent().getStringExtra(Constants.LEAD_MOBILE);
        leadName = activity.getIntent().getStringExtra(Constants.LEAD_NAME);
        footerView = mInflater.inflate(R.layout.listview_footer, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void makeSentCarsRequest(final boolean showFullPageError,
                                     final int pageNumber) {
        // params.put(Constants.LEAD_TYPE, "N");
        /* params.put(Constants.METHOD_LABEL, Constants.D2D_ACTIVE_METHOD); */
        params.put(Constants.DEALER_USERNAME, CommonUtils
                .getStringSharedPreference(getFragmentActivity(),
                        Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.DEALER_PASSWORD, CommonUtils
                .getStringSharedPreference(getFragmentActivity(),
                        Constants.UC_DEALER_PASSWORD, ""));
        params.put(Constants.D2D_TAB, "send_cars");
        params.put(Constants.PAGE_NO, String.valueOf(pageNumber));
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
        params.put(Constants.METHOD_LABEL, Constants.GETLEADS);
        RetrofitRequest.InventoriesRequest(params, new Callback<InventoriesModel>() {
            @Override
            public void success(InventoriesModel inventoriesModel, retrofit.client.Response response) {
                GCLog.e(
                        "response model: " + response.toString());
                hideProgressBar();
                if ("T".equalsIgnoreCase(inventoriesModel.getStatus())) {
                    if (inventoriesModel.getInventories() != null) {

                        if (inventoriesModel.getInventories().size() > 0) {

                            isNextPossible = inventoriesModel
                                    .getIsNextPossible();
                            if (pageNumber == 1) {
                                initializeList(inventoriesModel);
                            } else {
                                if (isNextPossible) {
                                    inventoriesData.addAll(inventoriesModel
                                            .getInventories());

                                    listView.removeFooterView(footerView);
                                    sentcarAdater
                                            .notifyDataSetChanged();
                                }
                            }
                        } else {
                            showAddLeadLayout(showFullPageError);
                        }

                    } else {
                        showAddLeadLayout(showFullPageError);
                    }
                } else {
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

     /*   SimilarCarRequest inventoriesRequest = new SimilarCarRequest(
                getFragmentActivity(), Request.Method.POST,
                Constants.getWebServiceURL(getFragmentActivity()),
                params,
                new Response.Listener<InventoriesModel>() {
                    @Override
                    public void onResponse(InventoriesModel response) {
                        GCLog.e(
                                "response model: " + response.toString());
                        hideProgressBar();
                        if ("T".equalsIgnoreCase(response.getStatus())) {
                            if (response.getInventories() != null) {

                                if (response.getInventories().size() > 0) {

                                    isNextPossible = response
                                            .getIsNextPossible();
                                    if (pageNumber == 1) {
                                        initializeList(response);
                                    } else {
                                        if (isNextPossible) {
                                            inventoriesData.addAll(response
                                                    .getInventories());

                                            listView.removeFooterView(footerView);
                                            sentcarAdater
                                                    .notifyDataSetChanged();
                                        }
                                    }
                                } else {
                                    showAddLeadLayout(showFullPageError);
                                }

                            } else {
                                showAddLeadLayout(showFullPageError);
                            }
                        } else {
                            showServerErrorLayout(showFullPageError);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.getCause() instanceof UnknownHostException) {
                    showNetworkConnectionErrorLayout(showFullPageError);
                } else {
                    showServerErrorLayout(showFullPageError);
                }
            }
        });

        ApplicationController.getInstance().addToRequestQueue(
                inventoriesRequest, Constants.TAG_ACTIVE_INVENTORIES,
                showFullPageError, this);*/
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

    private void initializeList(InventoriesModel response) {
        inventoriesData = response.getInventories();
        layoutContainer.removeAllViews();
        View view = mInflater.inflate(R.layout.fragment_active_inventories,
                null, false);
        layoutContainer.addView(view);
        listView = (SwipeListView) layoutContainer
                .findViewById(R.id.inventoriesList);
        if (isNextPossible)
            listView.addFooterView(footerView);
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onEndlessScroll(int pageNo) {
                if (isNextPossible) {
                    makeSentCarsRequest(false, pageNo);
                    listView.addFooterView(footerView);
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
/*                Intent intent = new Intent(getFragmentActivity(), ViewLeadActivity.class);
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

        listView.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
        listView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
        listView.setOffsetLeft(Constants.LIST_ITEM_LEFT_OFFSET);

        sentcarAdater = new SimilarCarsAdapter(getFragmentActivity(),
                response.getInventories(), leadMobilenum, leadName, Constants.CLOSELIST_SENTCARS);
        listView.setAdapter(sentcarAdater);
    }

    private void openAnimate(int position) {

        try {
            listView.openAnimate(position);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void closeAnimate(int position) {
        listView.closeAnimate(position);
    }

    private void showAddLeadLayout(boolean showFullPageError) {
        setInitialView();
        hideProgressBar();

        if (showFullPageError) {
            View view = mInflater.inflate(R.layout.layout_error, null, false);
            /* alternativeLayout.removeAllViews(); */
            alternativeLayout.addView(view);
            imageView = (ImageView) view.findViewById(R.id.no_internet_img);
            imageView.setImageResource(R.drawable.no_result_icons);
            textView = (TextView) view.findViewById(R.id.checkconnection);
            textView.setVisibility(View.GONE);

            errorMessage = (TextView) view.findViewById(R.id.errorMessage);
            errorMessage.setText("No Sent Cars");

            retry = (Button) view.findViewById(R.id.retry);
            /*
             * retry.setTag("ADD_LEAD"); retry.setText(R.string.add_lead);
			 * retry.setOnClickListener(this);
			 */
            retry.setVisibility(View.GONE);

        } else {
            CommonUtils.showToast(getFragmentActivity(),
                    getString(R.string.network_error), Toast.LENGTH_SHORT);
        }
    }

    private void showServerErrorLayout(boolean showFullPageError) {
        setInitialView();
        hideProgressBar();

        if (showFullPageError) {
            View view = mInflater.inflate(R.layout.layout_error, null, false);
            /* alternativeLayout.removeAllViews(); */
            alternativeLayout.addView(view);

            errorMessage = (TextView) view.findViewById(R.id.errorMessage);
            errorMessage.setText(R.string.no_internet_connection);

            retry = (Button) view.findViewById(R.id.retry);
            retry.setOnClickListener(this);

        } else {
            CommonUtils.showToast(getFragmentActivity(),
                    getString(R.string.network_error), Toast.LENGTH_SHORT);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = getFragmentActivity().getLayoutInflater().inflate(
                R.layout.activity_place_holder, container, false);
        layoutContainer = (FrameLayout) rootView
                .findViewById(R.id.layoutContainer);
        /* listView = (ListView) rootView.findViewById(R.id.list); */
        setInitialView();
        makeSentCarsRequest(true, 1);

        return rootView;
    }

    private void setInitialView() {
        layoutContainer.removeAllViews();
        dummyView = mInflater.inflate(R.layout.layout_loading_full_page, null,
                false);
        layoutContainer.addView(dummyView);
        alternativeLayout = (FrameLayout) dummyView
                .findViewById(R.id.alternativeLayout);
        progressBar = (LinearLayout) dummyView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void onNoInternetConnection(NetworkEvent networkEvent) {
      /*  ApplicationController.getInstance().cancelPendingRequests(
                Constants.TAG_ACTIVE_INVENTORIES);*/

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
            errorMessage.setText("No Leads");

            retry = (Button) view.findViewById(R.id.retry);
            retry.setVisibility(View.GONE);
            // retry.setOnClickListener(this);

        } else {
            CommonUtils.showToast(getFragmentActivity(),
                    getString(R.string.network_error), Toast.LENGTH_SHORT);
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
                    intent = new Intent(getFragmentActivity(),
                            LeadAddOptionActivity.class);
                    startActivity(intent);
                } else {
                    setInitialView();
                    makeSentCarsRequest(true, 1);
                }
                break;
        }
    }

    // @Override
    // public void onItemClick(AdapterView<?> parent, View view, int position,
    // long id) {
    // Intent intent = new Intent(getFragmentActivity(),
    // ViewLeadActivity.class);
    // startActivity(intent);
    // }

    @Subscribe
    public void onOpenListItem(OpenListItemEvent event) {
        if (event != null) {
            GCLog.e("position to open: " + event.getPosition());
            int position = event.getPosition();
            int source = event.getSource();
            if (source == Constants.CLOSELIST_SENTCARS)
                this.openAnimate(position);
        }
    }


    @Subscribe
    public void onCloseListItem(OpenListItemEvent event) {
        GCLog.e("position to open: " + event.getPosition());
        int position = event.getPosition();
        int source = event.getSource();
        if (source == Constants.CLOSELIST_SENTCARS)
            this.closeAnimate(position);
    }


    @Override
    public void onRefresh() {
        setInitialView();
        makeSentCarsRequest(false, 1);
    }
    /*@Subscribe
    public void onShareTypeSelected(ShareTypeEvent event) {

        ContactsPickerFragment contactsPickerFragment;
        Bundle args = new Bundle();
        int fragmentType=event.getFragmentType();
        if(fragmentType==Constants.OPENLIST_SENTCARS) {
            switch (event.getShareType()) {
                case SMS:
                mGAHelper.sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_SENT_CARS,
                        Constants.CATEGORY_SENT_CARS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SEND_SMS,
                        0);

                    GCLog.e(Constants.TAG, "Share type sms");
                    contactsPickerFragment = ContactsPickerFragment.newInstance(
                            getString(R.string.select_contact_from),
                            selectedIndex,
                            event.getShareText(),
                            event.getShareType(),
                            event.getCarId()
                    );

                    contactsPickerFragment.show(getFragmentManager(), "contacts-picker-fragment");

                    break;

                case WHATSAPP:
                  mGAHelper.sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_SENT_CARS,
                        Constants.CATEGORY_SENT_CARS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SEND_WHATSAPP,
                        0);
                    GCLog.e(Constants.TAG, "Share type whatsapp");
                    contactsPickerFragment = ContactsPickerFragment.newInstance(
                            getString(R.string.select_contact_from),
                            selectedIndex,
                            event.getShareText(),
                            event.getShareType(),
                            event.getCarId()
                    );

                    contactsPickerFragment.show(getFragmentManager(), "contacts-picker-fragment");
                    break;

            }
        }
    }*/


    @Override
    public void onContactsOptionSelected(
            ContactsPickerFragment fragment,
            ContactType contactType,
            int index,
            String shareText,
            ShareType shareType,
            String carId,
            String imageUrl) {
        selectedIndex = index;
        this.carId = carId;

        switch (contactType) {
            case CALL_LOGS:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_SENT_CARS,
                        Constants.CATEGORY_SENT_CARS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_CALL_LOGS,
                        0);
                mGShareToUtil.showCallLogsDialog(shareText, shareType, carId);
                break;

            case CONTACTS:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_SENT_CARS,
                        Constants.CATEGORY_SENT_CARS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_CONTACTS,
                        0);
                mGShareToUtil.showContactsDialog(shareText, shareType, carId);
                break;
        }
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
    public void onCallLogSelected(CallLogItem callLogItem, ShareType shareType, String shareText, String carId, String imageUrl) {
        GCLog.e("call log item: " + callLogItem.toString() + ", sharetype = " + shareType.name() + ", sharetext = " + shareText);
        mobileNumber = callLogItem.getGaadiFormatNumber();
        if (shareType == ShareType.WHATSAPP) {
            if ("null".equalsIgnoreCase(callLogItem.getName()) || (callLogItem.getName() == null)) {
                mGShareToUtil.showAddNameToContactDialog(callLogItem.getNumber(), shareText, carId);

            } else {
                mGShareToUtil.sendMessageInWhatsApp(shareText, carId);
            }

        } else if (shareType == ShareType.SMS) {
            sendSMS(callLogItem.getNumber(), shareText, carId);
        }
    }

    /*private BroadcastReceiver sendSMSReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    mGAHelper.sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_SENT_CARS,
                            Constants.CATEGORY_SENT_CARS,
                            Constants.LABEL_SMS_SENT,
                            mobileNumber,
                            0);

                    makeServerCallForSharedItem();

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

    private void makeServerCallForSharedItem(ShareType shareType) {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.CAR_ID, carId);
        params.put(Constants.MOBILE_NUM, mobileNumber);
        params.put(Constants.SHARE_TYPE, shareType.name());
        params.put(Constants.SHARE_SOURCE, Constants.CATEGORY_SIMILAR_CARS);
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.METHOD_LABEL, Constants.SENT_CARS_METHOD);
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(getContext(), Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.DEALER_PASSWORD, CommonUtils.getStringSharedPreference(getContext(), Constants.UC_DEALER_PASSWORD, ""));

        RetrofitRequest.shareCarsRequest(getContext(), params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, retrofit.client.Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
       /* ShareCarsRequest shareCarsRequest = new ShareCarsRequest(getFragmentActivity(),
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

        ApplicationController.getInstance().addToRequestQueue(shareCarsRequest);*/
    }

    @Override
    public void onContactSelected(ContactListItem contactListItem, String shareText, ShareType shareType, String carId, String imageUrl) {
        mobileNumber = contactListItem.getGaadiFormatNumber();
        if (shareType == ShareType.WHATSAPP) {
            if ("null".equalsIgnoreCase(contactListItem.getContactName()) || (contactListItem.getContactName() == null)) {
                mGShareToUtil.showAddNameToContactDialog(
                        contactListItem.getContactNumber(),
                        shareText,
                        carId);
            } else {
                mGShareToUtil.sendMessageInWhatsApp(shareText, carId);
            }


        } else if (shareType == ShareType.SMS) {
            sendSMS(contactListItem.getContactNumber(), shareText, carId);
        }
    }

    private void sendSMS(String number, String shareText, String carId) {
        /*SmsManager smsManager = SmsManager.getDefault();
        String SENT = number;
        PendingIntent sentPI = PendingIntent.getBroadcast(getFragmentActivity(), 0,
                new Intent(SENT), 0);*/
        this.carId = carId;

        makeServerCallForSharedItem(ShareType.SMS);

        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sendIntent.setData(Uri.parse("smsto:" + number));
        sendIntent.putExtra("address", number);
        sendIntent.putExtra("sms_body", shareText);

        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivity(sendIntent);
    }
}
