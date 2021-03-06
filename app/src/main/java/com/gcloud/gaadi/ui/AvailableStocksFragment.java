package com.gcloud.gaadi.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FilterQueryProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.StocksAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.db.DBFunction;
import com.gcloud.gaadi.db.ViewStockModel;
import com.gcloud.gaadi.events.ActionEvent;
import com.gcloud.gaadi.events.CallLogItemSelectedEvent;
import com.gcloud.gaadi.events.ContactItemSelectedEvent;
import com.gcloud.gaadi.events.ContactOptionSelectedEvent;
import com.gcloud.gaadi.events.FilterEvent;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.events.OpenListItemEvent;
import com.gcloud.gaadi.events.SetTabTextEvent;
import com.gcloud.gaadi.events.ShareTypeEvent;
import com.gcloud.gaadi.events.StocksRerfreshEvent;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.StockItemModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.service.SyncStocksService;
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

import static com.gcloud.gaadi.providers.ViewStockProvider.CONTENT_URI;


/**
 * Created by priyarawat on 16/9/15.
 */
public class AvailableStocksFragment extends Fragment implements View.OnClickListener, OnNoInternetConnectionListener, LoaderManager.LoaderCallbacks<Cursor> {
    HashMap<String, String> params = new HashMap<String, String>(), filterParams = new HashMap<>(), sortParams = new HashMap<>();
    int checkedItemId;
    private View rootView;
    private FrameLayout layoutContainer, alternativeLayout;
    private LayoutInflater mInflater;
    private View dummyView;
    private Activity activity;
    private LinearLayout progressBar;
    private TextView errorMessage;
    private Button retry;
    private ArrayList<StockItemModel> dataList;
    private SwipeListView stocksList;
    private Boolean nextPossible;
    private StocksAdapter stocksAdapter;
    private GShareToUtil mGShareToUtil;
    private ShareTypeEvent shareTypeEvent;
    private String carId = "";
    private String mobileNumber = "";
    private int selectedIndex = 0;
    private String imageUrl;
    private int pageNo = 1;
    private LinearLayout lay_menu_container;
    private boolean checkMenu = false, checkToggel = false, checkRadioMenu = false;

    private LinearLayout linearMenuLayout;
    private RadioGroup radioGroup;
    private ContactOptionSelectedEvent contactOptionSelectedEvent;
    private boolean permissionGranted = false;
    private StocksActivity.OverFlowMenuListener overFlowMenuListener;
    private ImageView img;
    private TextView refineFilter;
    private CursorLoader cursorLoader;
    private Cursor mCursor;

    private String selection = ViewStockModel.ACTIVE + " = " + " 1 ";
    private ArrayList<String> selectionList = new ArrayList<>();
    private String regNo;
    private String sortByOrder = ViewStockModel.CHANGE_TIME + " DESC ";
    private ProgressBar mProgressBar;
    private View errorLayout;
    private boolean isStockAdd = false;
    private FilterQueryProvider filterQueryProvider = new FilterQueryProvider() {
        public Cursor runQuery(CharSequence constraint) {
            mCursor = getContext().getContentResolver().query(CONTENT_URI, null, selection, null, sortByOrder);
            return mCursor;
        }
    };

    public static AvailableStocksFragment newInstance(StocksActivity.OverFlowMenuListener overFlowMenuListener) {
        AvailableStocksFragment availableStocksFragment = new AvailableStocksFragment();
        availableStocksFragment.overFlowMenuListener = overFlowMenuListener;
        availableStocksFragment.hideMenu();
        // availableStocksFragment.checkRadioButton(StocksActivity.check_id_item);
        return availableStocksFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
//        mGAHelper = new GAHelper(getFragmentActivity());
        setHasOptionsMenu(true);
        mGShareToUtil = new GShareToUtil(activity);
        mInflater = activity.getLayoutInflater();
    }

    private Activity getFragmentActivity() {
        return activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationController.getEventBus().register(this);
        /*android.os.Handler handler = getFragmentActivity().getWindow().getDecorView().getHandler();
        handler.post(new Runnable() {
            @Override
            public void run() {*/
                getLoaderManager().restartLoader(Constants.AVAILABLE_STOCKS_ID, null, AvailableStocksFragment.this);
            /*}
        });*/
        if (contactOptionSelectedEvent != null && permissionGranted) {
            onContactOptionSelectedEvent(contactOptionSelectedEvent);
            permissionGranted = false;
        }
    }

    @Override
    public void onDestroy() {
        ApplicationController.getEventBus().unregister(this);
        super.onDestroy();
        params.clear();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = getFragmentActivity().getLayoutInflater().inflate(R.layout.stocks_fragment_layout, container, false);
        initializeViews(rootView);
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

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private Cursor getAvailCurosr() {

        Cursor cursor = getContext().getContentResolver().query(CONTENT_URI, null, selection,null, sortByOrder);

        cursor.moveToFirst();

        return cursor;
    }

    private void showNoRecordMessage(boolean showFullPageError, boolean isSuccess) {

        if (showFullPageError == true) {
            if(progressBar != null){
                progressBar.setVisibility(View.GONE);
            }
            errorLayout.setVisibility(View.VISIBLE);
            errorLayout.setClickable(true);
            errorLayout.setVisibility(View.VISIBLE);
            TextView errorMessage = (TextView) errorLayout.findViewById(R.id.errorMessage);
            TextView checkconnection = (TextView) errorLayout.findViewById(R.id.checkconnection);
            ImageView imageView = (ImageView) errorLayout.findViewById(R.id.no_internet_img);

            Button retry = (Button) errorLayout.findViewById(R.id.retry);
            if (isSuccess) {
                retry.setVisibility(View.GONE);
                imageView.setImageResource(R.drawable.no_result_icons);
                checkconnection.setVisibility(View.INVISIBLE);
                errorMessage.setText("No records found");
            } else {
                retry.setVisibility(View.VISIBLE);
                retry.setOnClickListener(this);
                imageView.setImageResource(R.drawable.no_internet_icons);
                checkconnection.setVisibility(View.VISIBLE);
                errorMessage.setText(getResources().getString(R.string.network_error));
            }
        }

    }

    private void showNoRecordsMessage(String message) {
        setInitialView();
        hideProgressBar();

        View view = mInflater.inflate(R.layout.layout_error, null, false);

        alternativeLayout.addView(view);

        img = (ImageView) view.findViewById(R.id.no_internet_img);

        img.setImageResource(R.drawable.no_result_icons);
        errorMessage = (TextView) view.findViewById(R.id.errorMessage);
        refineFilter = (TextView) view.findViewById(R.id.checkconnection);
        //refineFilter.setText(R.string.refine_filter);
        refineFilter.setVisibility(View.INVISIBLE);
        errorMessage.setText(message);

        retry = (Button) view.findViewById(R.id.retry);
        retry.setTag("ADD STOCK");
        retry.setText(R.string.add_stock);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reteyClick();

            }
        });

    }

    @Override
    public void onClick(View v) {

        if (!StocksActivity.checkChangeListenerCalled && StocksActivity.previousCheckedId == v.getId()) {
            radioGroup.clearCheck();
            StocksActivity.previousCheckedId = -1;
            sortParams.clear();
            StocksActivity.check_id_item = 0;
            ApplicationController.getEventBus().post(new StocksRerfreshEvent(sortParams, Constants.REMOVED_STOCKS));
            getLoaderManager().restartLoader(Constants.AVAILABLE_STOCKS_ID, null, this);
        } else {
            StocksActivity.checkChangeListenerCalled = false;
        }

    }

    private void openAddStockActivity() {
        Intent intent = new Intent(activity, StockAddActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        activity.finish();
    }

    @Override
    public void onNoInternetConnection(NetworkEvent networkEvent) {
        // ApplicationController.getInstance().cancelPendingRequests(Constants.TAG_STOCK_DETAIL);

        if (!isVisible() || !isAdded()) {
            return;
        }
        NetworkEvent.NetworkError networkError = networkEvent.getNetworkError();
        if (networkError == NetworkEvent.NetworkError.NO_INTERNET_CONNECTION) {
            showNetworkErrorLayout(networkEvent.isShowFullPageError());
        } else if (networkError == NetworkEvent.NetworkError.SLOW_CONNECTION) {

        }
    }

    private void showNetworkErrorLayout(boolean showFullPageError) {
        if (!isVisible() || !isAdded()) {
            return;
        }
        setInitialView();
        hideProgressBar();
        /*alternativeLayout.removeAllViews();*/
        if (showFullPageError) {
            View view = mInflater.inflate(R.layout.layout_error, null, false);
            alternativeLayout.addView(view);
            errorMessage = (TextView) view.findViewById(R.id.errorMessage);
            errorMessage.setText(R.string.network_error);
            retry = (Button) view.findViewById(R.id.retry);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reteyClick();
                }
            });

        } else {
            CommonUtils.showToast(activity, getString(R.string.network_error), Toast.LENGTH_SHORT);
        }
    }

    public void reteyClick() {
        String tag = (String) retry.getTag();
        if ((tag != null) && !tag.isEmpty() && tag.equalsIgnoreCase(getString(R.string.add_stock))) {
            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                    Constants.CATEGORY_VIEW_STOCK,
                    Constants.CATEGORY_VIEW_STOCK,
                    Constants.ACTION_TAP,
                    Constants.LABEL_ADD_STOCK,
                    0);
            openAddStockActivity();
        } else {

            getLoaderManager().restartLoader(Constants.AVAILABLE_STOCKS_ID, null, this);
//            makeStockListRequest(true, 1);
        }
    }

    private void showNetworkConnectionErrorLayout(boolean fullPageError) {

        if (!isVisible() || !isAdded()) {
            return;
        }
        hideProgressBar();
        /*alternativeLayout.removeAllViews();*/
        if (fullPageError) {
            setInitialView();
            View view = mInflater.inflate(R.layout.layout_error, null, false);

            alternativeLayout.addView(view);

            errorMessage = (TextView) view.findViewById(R.id.errorMessage);
            errorMessage.setText(R.string.network_connection_error_message);

            retry = (Button) view.findViewById(R.id.retry);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reteyClick();
                }
            });

        } else {
            CommonUtils.showToast(activity, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
        }
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
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reteyClick();
                }
            });

        } else {
            if (activity != null && this.isAdded()) {
                CommonUtils.showToast(activity, getString(R.string.network_error), Toast.LENGTH_SHORT);
            }
        }
    }

    public void showMenu() {
        if (checkMenu) {
            if (lay_menu_container.getVisibility() != View.VISIBLE) {

                lay_menu_container.setClickable(true);
                lay_menu_container.setVisibility(View.VISIBLE);

                Animation animation = AnimationUtils.loadAnimation(getFragmentActivity(), R.anim.zoom_out);
                stocksList.startAnimation(animation);

                if (overFlowMenuListener != null) {
                    overFlowMenuListener.onOpen();
                }

                initializationMenuView();

            }


            checkToggel = true;


        }
    }

    public void initializationMenuView() {
        View view = mInflater.inflate(R.layout.stock_menu, null, false);
        linearMenuLayout = (LinearLayout) view.findViewById(R.id.lay_menu_view);
        view.findViewById(R.id.sort_by_relevance).setOnClickListener(this);
        view.findViewById(R.id.sort_by_km_asc).setOnClickListener(this);
        view.findViewById(R.id.sort_by_km_desc).setOnClickListener(this);
        view.findViewById(R.id.sort_by_price_asc).setOnClickListener(this);
        view.findViewById(R.id.sort_by_price_desc).setOnClickListener(this);
        view.findViewById(R.id.sort_by_year_asc).setOnClickListener(this);
        view.findViewById(R.id.sort_by_year_desc).setOnClickListener(this);
        radioGroup = (RadioGroup) view.findViewById(R.id.radio_menu);
        Animation animation2 = AnimationUtils.loadAnimation(getFragmentActivity(), R.anim.stock_menu_down_translate);
        linearMenuLayout.setAnimation(animation2);
        // new StocksActivity().fabHide();
        if (StocksActivity.check_id_item != 0) {
            RadioButton rb = (RadioButton) radioGroup.findViewById(StocksActivity.check_id_item);
            radioGroup.check(StocksActivity.check_id_item);
            rb.setTextColor(getResources().getColor(R.color.primary));
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                boolean switchExecuted = false;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);


                switch (checkedId) {
                    case R.id.sort_by_km_asc:

                        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                Constants.CATEGORY_VIEW_STOCK,
                                Constants.CATEGORY_VIEW_STOCK,
                                Constants.ACTION_TAP,
                                Constants.LABEL_SORT_BY_KM,
                                0);
                        StocksActivity.check_id_item = checkedId;
                        switchExecuted = true;
                        sortByOrder = ViewStockModel.KM_SORT;
                        //return true;
                        break;

                    case R.id.sort_by_km_desc:
                        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                Constants.CATEGORY_VIEW_STOCK,
                                Constants.CATEGORY_VIEW_STOCK,
                                Constants.ACTION_TAP,
                                Constants.LABEL_SORT_BY_KM,
                                0);
                        StocksActivity.check_id_item = checkedId;
                        switchExecuted = true;
                        sortByOrder = ViewStockModel.KM_SORT + " DESC ";
                        //return true;
                        break;

                    case R.id.sort_by_price_asc:
                        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                Constants.CATEGORY_VIEW_STOCK,
                                Constants.CATEGORY_VIEW_STOCK,
                                Constants.ACTION_TAP,
                                Constants.LABEL_SORT_BY_PRICE,
                                0);
                        switchExecuted = true;
                        StocksActivity.check_id_item = checkedId;
                        sortByOrder = ViewStockModel.PRICE_SORT;
                        //return true;
                        break;

                    case R.id.sort_by_price_desc:

                        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                Constants.CATEGORY_VIEW_STOCK,
                                Constants.CATEGORY_VIEW_STOCK,
                                Constants.ACTION_TAP,
                                Constants.LABEL_SORT_BY_PRICE,
                                0);
                        switchExecuted = true;
                        StocksActivity.check_id_item = checkedId;
                        sortByOrder = ViewStockModel.PRICE_SORT + " DESC ";
                        //return true;

                        break;

                    case R.id.sort_by_year_asc:

                        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                Constants.CATEGORY_VIEW_STOCK,
                                Constants.CATEGORY_VIEW_STOCK,
                                Constants.ACTION_TAP,
                                Constants.LABEL_SORT_BY_YEAR,
                                0);
                        switchExecuted = true;
                        StocksActivity.check_id_item = checkedId;
                        sortByOrder = ViewStockModel.MODEL_YEAR;
                        //return true;
                        break;

                    case R.id.sort_by_year_desc:

                        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                Constants.CATEGORY_VIEW_STOCK,
                                Constants.CATEGORY_VIEW_STOCK,
                                Constants.ACTION_TAP,
                                Constants.LABEL_SORT_BY_YEAR,
                                0);
                        switchExecuted = true;
                        StocksActivity.check_id_item = checkedId;
                        sortByOrder = ViewStockModel.MODEL_YEAR + " DESC ";
                        //return true;
                        break;

                    case R.id.sort_by_relevance:

                        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                Constants.CATEGORY_VIEW_STOCK,
                                Constants.CATEGORY_VIEW_STOCK,
                                Constants.ACTION_TAP,
                                Constants.LABEL_SORT_BY_RELEVANCE,
                                0);
                        switchExecuted = true;
                        StocksActivity.check_id_item = checkedId;
                        sortByOrder = ViewStockModel.CHANGE_TIME + " DESC ";
                        //return true;
                        break;
                }

                if (checkedId > -1) {
                    StocksActivity.checkChangeListenerCalled = true;
                    StocksActivity.previousCheckedId = checkedId;
                }

                if (switchExecuted) {
                    stocksList.setSelection(0);
                    hideMenu();
                    hideProgressBar();
                    sortParams.put("sortBy", sortByOrder);
                    ApplicationController.getEventBus().post(new StocksRerfreshEvent(sortParams, Constants.REMOVED_STOCKS));
                    getLoaderManager().restartLoader(Constants.AVAILABLE_STOCKS_ID, null, AvailableStocksFragment.this);
                    if (overFlowMenuListener != null) {
                        overFlowMenuListener.onClose();
                    }

                }

            }


        });
        lay_menu_container.addView(view);
    }


    @Subscribe
    public void refreshStocksList(StocksRerfreshEvent stocksRefreshEvent) {
        if (Constants.AVAILABLE_STOCKS == stocksRefreshEvent.getFragmentType()) {
            sortParams = stocksRefreshEvent.getParams();
            sortByOrder = sortParams.get("sortBy");
            getLoaderManager().restartLoader(Constants.AVAILABLE_STOCKS_ID, null, this);
        }
    }


    @Override
    public void onPause() {
        hideMenu();
        super.onPause();
    }

    public void hideMenu() {
        if (checkMenu) {
            if (lay_menu_container.getVisibility() == View.VISIBLE) {
                Animation animation = AnimationUtils.loadAnimation(getFragmentActivity(), R.anim.zoom_in);
                animation.setFillAfter(true);
                stocksList.startAnimation(animation);

                lay_menu_container.setClickable(false);

                Animation animation1 = AnimationUtils.loadAnimation(getFragmentActivity(), R.anim.stock_menu_up_translate);
                animation.setFillAfter(true);
                linearMenuLayout.startAnimation(animation1);
                animation1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        lay_menu_container.setVisibility(View.GONE);
                        lay_menu_container.removeAllViews();
                        //    new StocksActivity().fabShow();
                        if (overFlowMenuListener != null) {
                            overFlowMenuListener.onClose();
                        }

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }
            checkToggel = false;

        }
    }

    public void menuToggle() {
        if (checkMenu) {
            if (checkToggel) {
                hideMenu();

            } else {
                showMenu();
            }
        }
    }

    private void initializeViews(View view) {
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        stocksList = (SwipeListView) view.findViewById(R.id.stocksList);
        stocksList.setVisibility(View.GONE);
        errorLayout = view.findViewById(R.id.error_layout);
        lay_menu_container = (LinearLayout) view.findViewById(R.id.lay_menu_container);
        lay_menu_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenu();
            }
        });
        checkMenu = true;
        stocksList.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onClickFrontView(int position) {
                GCLog.e("onClickFrontView " + position);
                Bundle args = new Bundle();
                Cursor c = (Cursor) stocksAdapter.getItem(position);
                Intent intent = new Intent(activity, StockViewActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra(Constants.STOCKS_TAB_CLICKED, Constants.AVAILABLE_STOCKS);
                args.putString(Constants.CAR_ID, c.getString(c.getColumnIndex(ViewStockModel.STOCK_ID)));
                args.putSerializable(Constants.MODEL_DATA, DBFunction.getStockModel(c));

                intent.putExtras(args);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getFragmentActivity().startActivityForResult(intent, Constants.STOCK_VIEW_ACTIVITY);
            }

            @Override
            public void onOpened(int position, boolean toRight) {
                if (position != Constants.listOpenedItem)
                    stocksList.closeAnimate(Constants.listOpenedItem);
                Constants.listOpenedItem = position;
            }

            @Override
            public void onClickBackView(int position) {
                stocksList.closeAnimate(position);
            }
        });
        stocksAdapter = new StocksAdapter(getContext(), null);
        stocksList.setAnimationTime(300);
        stocksList.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
        stocksList.setOffsetLeft(Constants.LIST_ITEM_LEFT_OFFSET);
        stocksList.setAdapter(stocksAdapter);
    }

    public void openAnimate(int position) {
        Log.e("OpenANimate", " StocksActivity");
        stocksList.openAnimate(position);
    }

    public void closeAnimate(int position) {
        stocksList.closeAnimate(position);
    }

    @Subscribe
    public void onCloseListItem(OpenListItemEvent event) {
        GCLog.e("position to open: " + event.getPosition());
        int position = event.getPosition();
        int source = event.getSource();
        if (source == Constants.AVAILABLE_STOCKS)
            this.closeAnimate(position);
    }

    @Subscribe
    public void onOpenListItem(OpenListItemEvent event) {
        GCLog.e("position to open: " + event.getPosition());
        int position = event.getPosition();
        if (event.getSource() == Constants.AVAILABLE_STOCKS) {
            this.openAnimate(position);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.STOCK_VIEW_ACTIVITY) {
            if (resultCode != Activity.RESULT_CANCELED) {
                synStocksData();
                getLoaderManager().restartLoader(Constants.AVAILABLE_STOCKS_ID, null, this);
            }
        } else if (requestCode == Constants.STOCKS_CONTACT_LIST) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactUri = data.getData();

                Cursor cursor = activity.getContentResolver().query(contactUri,
                        new String[]{ContactsContract.Contacts._ID},
                        null, null, null);
                cursor.moveToFirst();

                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                Cursor phones = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

                //phones.moveToFirst();

                if (phones.getCount() >= 1) {
                    while (phones.moveToNext()) {
                        String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //GCLog.e(Constants.TAG, "number: " + number);
                        try {
                            mGShareToUtil.sendSMSHelp(number, shareTypeEvent.getShareText(), shareTypeEvent.getCarId());
                        } catch (Exception e) {
                            CommonUtils.showToast(activity, "Could not send SMS. Invalid phone number.", Toast.LENGTH_SHORT);
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
                    CommonUtils.showToast(activity, "Cannot send SMS. No contact number present", Toast.LENGTH_SHORT);

                }
                phones.close();

                //GCLog.e(Constants.TAG, "cursor details: " + cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
                cursor.close();
            }
        }
    }

    private void synStocksData() {
        Intent intent = new Intent(getContext(), SyncStocksService.class);
        getContext().stopService(intent);
        getContext().startService(intent);
    }

    @Subscribe
    public void onShareTypeSelected(ShareTypeEvent event) {
        if (!isResumed()) {
            return;
        }

        this.shareTypeEvent = event;
        this.carId = event.getCarId();
        ContactsPickerFragment contactsPickerFragment;
        Bundle args = new Bundle();
        switch (event.getShareType()) {
            case SMS:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SEND_IN_SMS,
                        0);

                GCLog.e("Share type sms");
                contactsPickerFragment = ContactsPickerFragment.newInstance(
                        getString(R.string.select_contact_from),
                        selectedIndex,
                        event.getShareText(),
                        event.getShareType(),
                        event.getCarId(),
                        event.getImageURL()
                );

                contactsPickerFragment.show(getFragmentManager(), "contacts-picker-fragment");

                break;

            case WHATSAPP:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SEND_WHATSAPP,
                        0);
                GCLog.e("Share type whatsapp");
                mGShareToUtil.sendWhatsapp(getString(R.string.select_contact_from), selectedIndex, event);
                /*contactsPickerFragment = ContactsPickerFragment.newInstance(
                        getString(R.string.select_contact_from),
                        selectedIndex,
                        event.getShareText(),
                        event.getShareType(),
                        event.getCarId(),
                        event.getImageURL()
                );

                contactsPickerFragment.show(getSupportFragmentManager(), "contacts-picker-fragment");*/
                //sendMessageInWhatsApp(event.getShareText(), event.getCarId());
                break;

            case EMAIL:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SEND_EMAIL,
                        0);
                GCLog.e("Share type email");
                mGShareToUtil.showSendEmailDialog(event);
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
                CommonUtils.showToast(activity, "Could not send SMS. Invalid number", Toast.LENGTH_SHORT);
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


    @Subscribe
    public void onActionEvent(ActionEvent event) {
        switch (event.getActionType()) {
            case BRING_TOP:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.ACTION_TAP,
                        Constants.LABEL_BRING_TO_TOP,
                        0);
                if (CommonUtils.getBooleanSharedPreference(activity, Constants.SERVICE_EXECUTIVE_LOGIN, false)) {
                    CommonUtils.showToast(activity, getString(R.string.bring_to_top_not_service_executive), Toast.LENGTH_SHORT);

                } else if (CommonUtils.getStringSharedPreference(activity, Constants.UC_CARDEKHO_INVENTORY, "0").equals("1")) {
                    makeBringToTopRequest(event);

                } else {
                    CommonUtils.showToast(activity, getString(R.string.no_premium_package), Toast.LENGTH_SHORT);

                }

                break;
        }
    }

    private void makeBringToTopRequest(ActionEvent event) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.CAR_ID, event.getStockItem().getStockId());
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.METHOD_LABEL, Constants.BRING_TOP_METHOD);
        params.put(Constants.UC_DEALER_USERNAME, CommonUtils.getStringSharedPreference(getContext(), Constants.UC_DEALER_USERNAME, ""));
        RetrofitRequest.bringTopRequest(getContext(), params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, retrofit.client.Response response) {
                if ("T".equalsIgnoreCase(generalResponse.getStatus())) {
                    CommonUtils.showToast(activity, generalResponse.getMessage(), Toast.LENGTH_SHORT);
//                    canShowProgressDialog();
//                    makeStockListRequest(false, 1);

                } else {
                    CommonUtils.showToast(activity, generalResponse.getError(), Toast.LENGTH_SHORT);

                }

            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(activity, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                } else {
                    CommonUtils.showToast(activity, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                }
            }
        });
        getLoaderManager().restartLoader(Constants.AVAILABLE_STOCKS_ID, null, this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        hideMenu();
        if (checkedItemId != 0)
            menu.findItem(checkedItemId).setChecked(true);
        super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean switchExecuted = false;

        switch (item.getItemId()) {
            case R.id.action_menu:
                menuToggle();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onFilterEvent(FilterEvent filterEvent) {
        GCLog.e("True");
        filterParams.clear();
        HashMap<String, String> filterMap = filterEvent.getParams();

        String regNo = filterMap.get("reg no");
        String model = filterMap.get("model");
        String make = filterMap.get("make");
        String fueltype = filterMap.get("fuel type");
        String km = filterMap.get("km");
        String price = filterMap.get("price");
        String certify = filterMap.get("trustmark_certified");
        String year = filterMap.get("year");
//        make := Aston Martin,Bentley , model =: Arnage,DB9 reg no := null ,Fuel Type := Diesel,LPG ,km := 4-6 ,price := 2-3,5-8 ,certify := null


        if (!CommonUtils.isValidField(regNo)) {   //if (source == null) {
            regNo = "";
        } else {
            regNo = " AND " + ViewStockModel.REG_NO + " LIKE '" + regNo.trim() + "%' ";
        }

        if (!CommonUtils.isValidField(model)) {   //if (source == null) {
            model = "";
        } else {
            model = CommonUtils.addSingleQuotesInString(model);
            model = " AND " + ViewStockModel.MODEL_NAME + " IN " + " ( " + model + " ) ";
        }

        if (!CommonUtils.isValidField(make)) {   //if (status == null) {
            make = "";
        } else {
            make = CommonUtils.addSingleQuotesInString(make);
            make = " AND " + ViewStockModel.MAKE_NAME + " IN " + " ( " + make + " ) ";
        }
        if (!CommonUtils.isValidField(fueltype)) {   //if (status == null) {
            fueltype = "";
        } else {
            fueltype = CommonUtils.addSingleQuotesInString(fueltype);
            fueltype = " AND " + ViewStockModel.FUEL_TYPE + " IN ( " + fueltype + " )";
        }

        if (!CommonUtils.isValidField(km)) {   //if (status == null) {
            km = "";
        } else {
            ArrayList<Integer> data = CommonUtils.optimisefilterInString(km,10000);
            km = " AND " + ViewStockModel.KM_SORT + " >=  '" + data.get(0) + "' AND " + ViewStockModel.KM_SORT + " < " + data.get(data.size() - 1) + "";
        }

        if (!CommonUtils.isValidField(certify)) {   //if (status == null) {
            certify = "";
        } else {
            certify = " AND " + ViewStockModel.TRUST_MARK_CERTIFY + " = '" + certify + "' ";
        }

        if (!CommonUtils.isValidField(price)) {   //if (status == null) {
            price = "";
        } else {
            ArrayList<Integer> data = CommonUtils.optimisefilterInString(price,100000);
            price = " AND " + ViewStockModel.PRICE_SORT + " >=  '" + data.get(0) + "' AND " + ViewStockModel.PRICE_SORT + " < " + data.get(data.size() - 1) + "";
        }

        if (!CommonUtils.isValidField(year)) {   //if (status == null) {
            year = "";
        } else {
            year = " AND " + ViewStockModel.MODEL_YEAR + " IN ( '" + year + "' )";
        }
        String select = regNo + "" + fueltype + "" + price + "" + km + "" + certify + "" + "" + make + "" + model + "" + year;

        selection = ViewStockModel.ACTIVE + " = 1 " + select;
       /* stocksAdapter.setFilterQueryProvider(filterQueryProvider);
        stocksAdapter.swapCursor(getContext().getContentResolver().query(CONTENT_URI, null, selection, null, sortByOrder));
        stocksAdapter.notifyDataSetChanged();*/
        stocksList.setSelection(0);
        android.os.Handler handler = getFragmentActivity().getWindow().getDecorView().getHandler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                getLoaderManager().restartLoader(Constants.AVAILABLE_STOCKS_ID, null, AvailableStocksFragment.this);
            }
        });
    }

    @Subscribe
    public void addToStockCalled(String order) {
        if ("refresh".equals(order)) {
            hideProgressBar();
//            setInitialView();
//            makeStockListRequest(true, 1);
            isStockAdd = true;
            getLoaderManager().restartLoader(Constants.AVAILABLE_STOCKS_ID, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        cursorLoader = new CursorLoader(getFragmentActivity(),
                CONTENT_URI, null, selection, null, sortByOrder);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(CommonUtils.getBooleanSharedPreference(ApplicationController.getInstance(),Constants.IS_ACTIVE_RUNNING_STOCK_SERVICE,false)
                &&  !CommonUtils.getBooleanSharedPreference(ApplicationController.getInstance(),Constants.IS_ERROR_STOCK_SERVICE,false)
                && (cursor == null || (cursor != null && cursor.getCount() == 0 ))){
            if (mProgressBar != null && mProgressBar.getVisibility() == View.GONE) {
                mProgressBar.setVisibility(View.VISIBLE);
                GCLog.e("close dialog");
            }
        }
        else {
            if (mProgressBar != null && mProgressBar.getVisibility() == View.VISIBLE) {
                mProgressBar.setVisibility(View.GONE);
                GCLog.e("close dialog");
            }
            if (cursor != null && cursor.getCount() > 0 ) {
                stocksList.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                stocksAdapter.changeCursor(cursor);
                if(isStockAdd) {
                    stocksList.post(new Runnable() {

                        @Override
                        public void run() {
                            stocksList.setSelection(0);
                            isStockAdd = false;
                        }
                    });
                }
            } else if(cursor.getCount() == 0 ){
                showNoRecordMessage(true,true);
            }
        }
        ApplicationController.getEventBus().post(
                new SetTabTextEvent("Available (" + cursor.getCount() + ")", Constants.AVAILABLE_STOCKS));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (stocksAdapter != null)
            stocksAdapter.changeCursor(null);
    }
}


