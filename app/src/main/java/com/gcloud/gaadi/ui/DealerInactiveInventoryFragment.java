package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.gcloud.gaadi.adapter.InActiveInventoriesAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.AddOnD2DEvent;
import com.gcloud.gaadi.events.EditInactiveInventoryPriceEvent;
import com.gcloud.gaadi.events.FilterEvent;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.events.OpenListItemEvent;
import com.gcloud.gaadi.events.RefreshActiveInventoriesEvent;
import com.gcloud.gaadi.events.RefreshInActiveInventoriesEvent;
import com.gcloud.gaadi.events.SetTabTextEvent;
import com.gcloud.gaadi.interfaces.MoreActionsClickListener;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.InventoriesModel;
import com.gcloud.gaadi.model.InventoryModel;
import com.gcloud.gaadi.model.UpdatePriceModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.swipelistview.BaseSwipeListViewListener;
import com.gcloud.gaadi.ui.swipelistview.SwipeListView;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;
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
public class DealerInactiveInventoryFragment extends Fragment implements View.OnClickListener, OnNoInternetConnectionListener, MoreActionsClickListener, SwipeRefreshLayout.OnRefreshListener {

    int pageNo = 1;
    private View rootView;
    private Activity activity;
    private SwipeListView listView;
    private InActiveInventoriesAdapter inventoriesAdapter;
    private LayoutInflater mInflater;
    private View dummyView;
    private Button retry;
    private View footerView;
    private LinearLayout progressBar;
    private TextView errorMessage;
    private ArrayList<InventoryModel> listData;
    private FrameLayout layoutContainer, alternativeLayout;
    private HashMap<String, String> params = new HashMap<String, String>();
    private boolean nextPossible = true;

    private Activity getFragmentActivity() {
        return activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        mInflater = activity.getLayoutInflater();
        footerView = mInflater.inflate(R.layout.listview_footer, null);
        ApplicationController.getEventBus().register(this);
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

    private void makeInActiveInventoriesRequest(final boolean showFullPageError, final int pageNumber) {
        //params.put(Constants.LEAD_TYPE, "N");

        params.put(Constants.METHOD_LABEL, Constants.D2D_INACTIVE_METHOD);
        params.put(Constants.D2D_TAB, Constants.D2D_INACTIVE_TAB);
        params.put(Constants.PAGE_NO, pageNumber + "");
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(getFragmentActivity(), Constants.UC_DEALER_USERNAME, ""));

        RetrofitRequest.InactiveInventoriesRequest(params, new Callback<InventoriesModel>() {
            @Override
            public void success(InventoriesModel inventoriesModel, retrofit.client.Response response) {
                if ("T".equalsIgnoreCase(inventoriesModel.getStatus())) {
                    ApplicationController.getEventBus().post(
                            new SetTabTextEvent("Inactive (" + inventoriesModel.getTotalRecords() + ")", Constants.INACTIVE_DEALER_PLATFORM));
                    if (inventoriesModel.getInventories().size() > 0) {

                        nextPossible = inventoriesModel.getIsNextPossible();
                        if (pageNumber == 1) {
                            listData = inventoriesModel.getInventories();
                            initializeList(inventoriesModel);
                            /*Intent intent = new Intent();
                            intent.putExtra("view", "inactive");
                            intent.putExtra("count", inventoriesModel.getTotalRecords());
                            ApplicationController.getEventBus().post(intent);*/

                            Intent intent = new Intent(DealerPlatformActivity.INITIATE_FILTERS);
                            intent.putExtra(Constants.CALL_SOURCE, "dealerPlatform");
                            intent.putExtra("filterResponse", inventoriesModel.getFilters());
                            ApplicationController.getEventBus().post(intent);
                        } else {
                            listData.addAll(inventoriesModel.getInventories());
                            listView.removeFooterView(footerView);
                            inventoriesAdapter.notifyDataSetChanged();
                        }
                    } else {
                        showAddLeadLayout(showFullPageError);
                    }
                } else {
                    if (inventoriesModel.getTotalRecords() == 0) {
                        ApplicationController.getEventBus().post(
                                new SetTabTextEvent("Inactive (" + inventoriesModel.getTotalRecords() + ")", Constants.INACTIVE_DEALER_PLATFORM));
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

       /*InventoriesRequest inventoriesRequest = new InventoriesRequest(getFragmentActivity(), Request.Method.POST,
                Constants.getWebServiceURL(getFragmentActivity()),
                params,
                new Response.Listener<InventoriesModel>() {
                    @Override
                    public void onResponse(InventoriesModel response) {
                        GCLog.e("response model: " + response.toString());
                        if ("T".equalsIgnoreCase(response.getStatus())) {
                            if (response.getInventories().size() > 0) {

                                nextPossible = response.getIsNextPossible();
                                if (pageNumber == 1) {
                                    listData = response.getInventories();
                                    ApplicationController.getEventBus().post(
                                            new SetTabTextEvent("Inactive (" + response.getTotalRecords() + ")", Constants.INACTIVE_DEALER_PLATFORM));
                                    initializeList(response);
                                    Intent intent = new Intent();
                                    intent.putExtra("view", "inactive");
                                    intent.putExtra("count", response.getTotalRecords());
                                    ApplicationController.getEventBus().post(intent);
                                } else {
                                    listData.addAll(response.getInventories());
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
        layoutContainer.removeAllViews();
        View view = mInflater.inflate(R.layout.fragment_inactive_inventories, null, false);
        layoutContainer.addView(view);
        listView = (SwipeListView) layoutContainer.findViewById(R.id.inventoriesList);
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onEndlessScroll(int pageNo) {
                if (nextPossible) {
                    listView.addFooterView(footerView);
                    listView.setAdapter(inventoriesAdapter);
                    makeInActiveInventoriesRequest(false, pageNo);
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
                                                  //super.onClickBackView(position);
                                                  listView.closeAnimate(position);
                                              }
                                          }
        );
        /*SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.inactive_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);*/
        listView.setOffsetLeft(Constants.LIST_ITEM_LEFT_OFFSET);
        listView.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
        listView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
        inventoriesAdapter = new InActiveInventoriesAdapter(getFragmentActivity(), response.getInventories(), this);
        listView.setAdapter(inventoriesAdapter);
//        listView.setOnItemClickListener(DealerInactiveInventoryFragment.this);
    }

    private void showAddLeadLayout(boolean showFullPageError) {
        setInitialView();
        hideProgressBar();

        View view = mInflater.inflate(R.layout.layout_error, null, false);
        /*alternativeLayout.removeAllViews();*/
        alternativeLayout.addView(view);

        errorMessage = (TextView) view.findViewById(R.id.errorMessage);
        errorMessage.setText(R.string.no_inactive_inventories_present);

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
            CommonUtils.showToast(getFragmentActivity(), getFragmentActivity().getString(R.string.network_error), Toast.LENGTH_SHORT);
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
        makeInActiveInventoriesRequest(true, 1);

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

    @Subscribe
    public void filtersReceived(FilterEvent event) {
        if (event.getCurrentItem() != DealerPlatformActivity.FROM_DEALER_PLATFORM) {
            return;
        }
        //setInitialView();
        params.clear();
        params.putAll(event.getParams());

        makeInActiveInventoriesRequest(true, 1);
    }

    @Subscribe
    public void onCloseListItem(OpenListItemEvent event) {
        GCLog.e("position to open: " + event.getPosition());
        int position = event.getPosition();
        int source = event.getSource();
        if (source == Constants.CLOSELIST_DEALERINACTIVE)
            this.closeAnimate(position);
    }

    private void closeAnimate(int position) {
        listView.closeAnimate(position);
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
                    makeInActiveInventoriesRequest(true, 1);
                }
                break;
        }
    }

    @Override
    public void clickMoreOptions(int position) {
        openAnimate(position);
    }

    public void openAnimate(int position) {
//        listView.closeAnimate(Constants.listOpenedItem);
//        Constants.listOpenedItem = position;
        listView.openAnimate(position);
    }

    @Override
    public void onRefresh() {
        setInitialView();
        makeInActiveInventoriesRequest(false, 1);
    }


    @Subscribe
    public void onRemoveEvent(AddOnD2DEvent event) {
        GCLog.e("car id to be removed: " + event.getCarId());
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.CAR_IDS, event.getCarId());
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.METHOD_LABEL, Constants.ADD_ON_D2D_METHOD);
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(getFragmentActivity(), Constants.UC_DEALER_USERNAME, ""));
        RetrofitRequest.addOnD2DRequest(getContext(), params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, retrofit.client.Response response) {
                if ("T".equalsIgnoreCase(generalResponse.getStatus())) {

                    CommonUtils.showToast(getFragmentActivity(), generalResponse.getMessage(), Toast.LENGTH_SHORT);
                    ApplicationController.getEventBus().post(new RefreshActiveInventoriesEvent(true));
                    setInitialView();
                    makeInActiveInventoriesRequest(false, 1);

                } else {
                    CommonUtils.showToast(getFragmentActivity(), "Failed to add on Dealer platform", Toast.LENGTH_SHORT);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                CommonUtils.showToast(getFragmentActivity(), "Some problem occurred with our servers. Please try again later.", Toast.LENGTH_SHORT);

            }
        });

        //By me Dipanshu garg
        /*AddOnD2DRequest addOnD2DRequest = new AddOnD2DRequest(
                getFragmentActivity(),
                Request.Method.POST,
                Constants.getWebServiceURL(getFragmentActivity()),
                params,
                new Response.Listener<GeneralResponse>() {
                    @Override
                    public void onResponse(GeneralResponse response) {
                        if ("T".equalsIgnoreCase(response.getStatus())) {

                            CommonUtils.showToast(getFragmentActivity(), response.getMessage(), Toast.LENGTH_SHORT);
                            ApplicationController.getEventBus().post(new RefreshActiveInventoriesEvent(true));
                            setInitialView();
                            makeInActiveInventoriesRequest(false, 1);

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

        ApplicationController.getInstance().addToRequestQueue(addOnD2DRequest, Constants.TAG_ADD_D2D, false, this);*/
    }

    @Subscribe
    public void onInactiveListRefresh(RefreshInActiveInventoriesEvent event) {
        if (event.isRefresh()) {
            setInitialView();
            makeInActiveInventoriesRequest(false, 1);
        }
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent = new Intent(getFragmentActivity(), ViewLeadActivity.class);
//        startActivity(intent);
//    }

    @Subscribe
    public void onPriceUpdateEvent(EditInactiveInventoryPriceEvent event) {
        GCLog.e("Inactive inventory price update event: " + event.toString());
        showPriceUpdateDialog(event);

    }

    private void showPriceUpdateDialog(final EditInactiveInventoryPriceEvent event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getFragmentActivity());
        View view = getFragmentActivity().getLayoutInflater().inflate(R.layout.layout_update_price_dialog, null, false);
        final TextView textView = (TextView) view.findViewById(R.id.text);
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

    private void makeUpdatePriceRequest(final EditInactiveInventoryPriceEvent event, String updatedPrice) {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.CAR_ID, event.getCarId());
        params.put(Constants.UPDATED_DEALER_PRICE, updatedPrice.trim().replace(",", ""));
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
        params.put(Constants.METHOD_LABEL, Constants.D2D_UPDATE_PRICE_METHOD);
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(getFragmentActivity(), Constants.UC_DEALER_USERNAME, ""));
        RetrofitRequest.EditPriceRequestInactive(params, new Callback<UpdatePriceModel>() {
            @Override
            public void success(UpdatePriceModel updatePriceModel, retrofit.client.Response response) {
                if ("T".equalsIgnoreCase(updatePriceModel.getStatus())) {

                    if (event.getAction().equals("UpdatePriceAndAdd")) {
                        ApplicationController.getEventBus().post(new AddOnD2DEvent(event.getCarId()));
                    } else {
                        makeInActiveInventoriesRequest(false, 1);
                    }

                } else {
                    CommonUtils.showToast(getFragmentActivity(), updatePriceModel.getErrorMessage(), Toast.LENGTH_SHORT);

                }
            }

            @Override
            public void failure(RetrofitError error) {
                CommonUtils.showToast(getFragmentActivity(), "Server Error. Failed to update price", Toast.LENGTH_SHORT);
            }
        });
        /*EditPriceRequest request = new EditPriceRequest(
                getFragmentActivity(),
                Request.Method.POST,
                Constants.getWebServiceURL(getFragmentActivity()),
                params,
                new Response.Listener<UpdatePriceModel>() {
                    @Override
                    public void onResponse(UpdatePriceModel response) {
                        if ("T".equalsIgnoreCase(response.getStatus())) {

                            if (event.getAction().equals("UpdatePriceAndAdd")) {
                                ApplicationController.getEventBus().post(new AddOnD2DEvent(event.getCarId()));
                            } else {
                                makeInActiveInventoriesRequest(false, 1);
                            }

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


}