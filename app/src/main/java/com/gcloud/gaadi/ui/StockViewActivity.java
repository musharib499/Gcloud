package com.gcloud.gaadi.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gallerylib.interfaces.IPositionHandler;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.StockImagesAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.ContactType;
import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.events.ContactOptionSelectedEvent;
import com.gcloud.gaadi.events.InitialRequestCompleteEvent;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.events.ShareTypeEvent;
import com.gcloud.gaadi.interfaces.CallLogsItemClickInterface;
import com.gcloud.gaadi.interfaces.OnContactSelectedListener;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.CallLogItem;
import com.gcloud.gaadi.model.CertifiedCarData;
import com.gcloud.gaadi.model.ContactListItem;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.MakePremiumModel;
import com.gcloud.gaadi.model.StockDetailModel;
import com.gcloud.gaadi.model.StockItemModel;
import com.gcloud.gaadi.model.UpdatePriceModel;
import com.gcloud.gaadi.model.ViewCertificationCarsWarrantyInput;
import com.gcloud.gaadi.model.ViewCertifiedCarModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.service.SyncStocksService;
import com.gcloud.gaadi.ui.viewpagerindicator.CirclePageIndicator;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.gcloud.gaadi.utils.GShareToUtil;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;

public class StockViewActivity extends AppCompatActivity implements
        View.OnClickListener, OnNoInternetConnectionListener, PopupMenu.OnMenuItemClickListener,
        ContactsPickerFragment.OnContactsOptionSelectedListener,
        CallLogsItemClickInterface, OnContactSelectedListener, IPositionHandler {

    boolean showOverflowMenuItem = false;
    CollapsingToolbarLayout collapsingToolbar;
    FloatingActionButton fab;
    ImageView iv_back;
    Toolbar mToolbarView;
    private Bundle args;
    private ShareTypeEvent shareTypeEvent;
    private ActionBar mActionBar;
    private GAHelper mGAHelper;
    private int selectedIndex = 0;

    /// private RelativeLayout sendSMS, sendWhatsapp, sendEmail, addLead, editStockLayout, addToStockLayout;
    private StockItemModel model;
    private Menu menu;
    private TextView errorMessage;
    private RelativeLayout sendSMS, sendWhatsapp, sendEmail, addLead, edit, addToStock;
    private LinearLayout availableCarsOptionLayout, removedCarsOptionLayout;
    private ImageView stockColor, share;
    private CirclePageIndicator circlePageIndicator;
    private boolean isPremium;
    private boolean certifiedCar;
    private String dealerUsername, ucdid, carId = "";
    private FrameLayout layoutContainer, alternativeLayout;
    private LayoutInflater mInflater;
    private View dummyView;
    private Button retry;
    private LinearLayout progressBar;
    private StockDetailModel stockDetailModel;
    private String mobileNumber = "";
    private boolean shouldReloadList = false;
    private CallLogsDialogFragment callLogsDialogFragment;
    private ViewPager imagesPager;
    private String url;
    private boolean isInspected, fromRemoved = false;
    private GShareToUtil mGShareToUtil;
    private GCProgressDialog progressDialog;
    private ContactOptionSelectedEvent contactOptionSelectedEvent;
    private boolean permissionGranted = false;

    @Override
    public void onBackPressed() {
      //  ApplicationController.getInstance().cancelPendingRequests(Constants.TAG_STOCK_DETAIL);
        if (shouldReloadList) {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGShareToUtil = new GShareToUtil(this);
        setContentView(R.layout.activity_place_holder);
        mInflater = getLayoutInflater();
//        mGAHelper = new GAHelper(this);
     /*   mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);*/

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            carId = extras.getString(Constants.CAR_ID);
            model = (StockItemModel) this.getIntent().getSerializableExtra(Constants.MODEL_DATA);
            fromRemoved = extras.containsKey("fromRemoved");
        }

        progressDialog = new GCProgressDialog(this, this, "Please wait while we complete the task");

        layoutContainer = (FrameLayout) findViewById(R.id.layoutContainer);
        setInitialView();
        makeStockDetailRequest(true);
    }

    private void setInitialView() {
        layoutContainer.removeAllViews();
        dummyView = mInflater.inflate(R.layout.layout_loading_full_page, null, false);
        layoutContainer.addView(dummyView);
        alternativeLayout = (FrameLayout) dummyView.findViewById(R.id.alternativeLayout);
        progressBar = (LinearLayout) dummyView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

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
    protected void onResume() {
        super.onResume();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_VIEW_STOCK_DETAIL);
        ApplicationController.getEventBus().register(this);

        if (contactOptionSelectedEvent != null && permissionGranted) {
            onContactsOptionSelected(null,
                    contactOptionSelectedEvent.getContactType(),
                    contactOptionSelectedEvent.getSelectedIndex(),
                    contactOptionSelectedEvent.getShareText(),
                    contactOptionSelectedEvent.getShareType(),
                    contactOptionSelectedEvent.getCarId(),
                    contactOptionSelectedEvent.getImageUrl());
            permissionGranted = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ApplicationController.getEventBus().unregister(this);
    }

    private void makeStockDetailRequest(final boolean fullPageError) {

        //resetAllViews();
        //showProgressBar();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.UCDID, String.valueOf(CommonUtils.getIntSharedPreference(this, Constants.UC_DEALER_ID, -1)));
        params.put(Constants.CAR_ID, carId);
        params.put(Constants.METHOD_LABEL, Constants.STOCK_DETAIL_METHOD);
        params.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);


        RetrofitRequest.stockViewRequest(params, new Callback<StockDetailModel>() {

            @Override
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
                    syncStockService();
                    //  onPrepareOptionsMenu(StockViewActivity.this.menu);
                } else {
                    showServerErrorLayout(true);
                }

            }

            @Override
            public void failure(RetrofitError error) {

                if (error.getCause() instanceof UnknownHostException) {
                    showNetworkConnectionErrorLayout(fullPageError);
                } else {
                    showServerErrorLayout(fullPageError);
                }
            }
        });

       /* StockDetailRequest stockDetailRequest = new StockDetailRequest(
                this,
                Request.Method.POST,
                Constants.getWebServiceURL(this), params,
                new Response.Listener<StockDetailModel>() {
                    @Override
                    public void onResponse(StockDetailModel response) {

                        if("T".equalsIgnoreCase(response.getStatus())) {
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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error.getCause() instanceof UnknownHostException) {
                            showNetworkConnectionErrorLayout(fullPageError);
                        } else {
                            showServerErrorLayout(fullPageError);
                        }
                    }
                });

        ApplicationController.getInstance().addToRequestQueue(stockDetailRequest, Constants.TAG_STOCK_DETAIL, fullPageError, this);
*/
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
            CommonUtils.showToast(this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
        }
    }

    private void updateMenuText() {
        if (this.menu != null) {
            MenuItem makePremium = this.menu.findItem(R.id.action_one);

            if ("0".equals(stockDetailModel.getPremiumInventory())) {
                makePremium.setTitle("Make Premium");
                isPremium = false;

            } else if ("1".equals(stockDetailModel.getPremiumInventory())) {
                makePremium.setTitle("Remove Premium");
                isPremium = true;

            }

            MenuItem soldWithoutWarranty = this.menu.findItem(R.id.action_three);

            MenuItem soldWithWarranty = this.menu.findItem(R.id.action_four);

            boolean isCertified = !stockDetailModel.getCertificationId().isEmpty(); // Trust mark certified only
            if (isCertified) {
                certifiedCar = true;

                soldWithoutWarranty.setTitle("Sold without Warranty");
                soldWithWarranty.setTitle("Issue Warranty");

            } else {

                soldWithoutWarranty.setTitle("Mark as Sold");
                soldWithWarranty.setTitle("Remove");

            }
        }
    }

    private void initializeViews(View parentView) {
        //invalidateOptionsMenu();

        mToolbarView = (Toolbar) parentView.findViewById(R.id.anim_toolbar);
        setSupportActionBar(mToolbarView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // mToolbarView.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu_back, null));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(
                (stockDetailModel.getModelVersion() == null
                        ? "" : stockDetailModel.getModelVersion()));

      /* NestedScrollView nestedScrollView=(NestedScrollView) findViewById(R.id.nestedScrollVw);
       *//* nestedScrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        nestedScrollView.setFocusable(true);
        nestedScrollView.setFocusableInTouchMode(true);*//*
        nestedScrollView.requestDisallowInterceptTouchEvent(true);*/
/*        nestedScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
               // v.requestFocusFromTouch();
                return true;
            }
        });*/
        //  collapsingToolbar.addView(mToolbarView);
        //  collapsingToolbar.setFitsSystemWindows(true);


      /*  mToolbarView.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/
     /*   iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });*/




     /*   mActionBar.setCustomView(R.layout.custom_actionbar_stock_detail);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);*/

       /* ImageView stockMake = (ImageView) mActionBar.getCustomView().findViewById(R.id.stockMake);
        stockMake.setImageResource(ApplicationController.makeLogoMap.get(stockDetailModel.getMakeid()));

        TextView stockModelVersion = (TextView) mActionBar.getCustomView().findViewById(R.id.stockModelVersion);
        stockModelVersion.setText(stockDetailModel.getModelVersion());
        stockModelVersion.setSelected(true);*/

        if (stockDetailModel.getArrayImages().size() > 0) {
            url = stockDetailModel.getArrayImages().get(0);
            GCLog.e("profile pic url : " + url);
        }

        //mActionBar.setDisplayHomeAsUpEnabled(true);

        TextView stockPrice = (TextView) findViewById(R.id.stockPrice);
        stockPrice.setText(Constants.RUPEES_SYMBOL + " " + stockDetailModel.getStockPrice());

        TextView colorValue = (TextView) findViewById(R.id.stockcolorvalue);
        colorValue.setText(stockDetailModel.getColorValue());

        ImageView trustmark = (ImageView) findViewById(R.id.trustmark);

        if (stockDetailModel.getTrustmarkCertified() == 1) {
            trustmark.setVisibility(View.VISIBLE);
        } else {
            trustmark.setVisibility(View.GONE);
        }


        LinearLayout totalLeadsLayout = (LinearLayout) findViewById(R.id.totalLeadsLayout);

        TextView totalLeads = (TextView) findViewById(R.id.leadCount);
        TextView leadvalue = (TextView) findViewById(R.id.leadvalue);
        String leads = stockDetailModel.getTotalLeads();
        if (leads != null && !leads.isEmpty() && !"null".equalsIgnoreCase(leads) && !"0".equals(leads)) {

            if((Integer.parseInt(leads)== 1)){
                leadvalue.setText("Lead");
            }

            totalLeads.setText((Integer.parseInt(leads) > 99) ? "99+" : leads);


        } else {
            totalLeadsLayout.setTag("NO_LEADS");
            totalLeads.setText("0");

        }
        totalLeadsLayout.setOnClickListener(this);

        //ImageView certifiedBy = (ImageView) findViewById(R.id.certifiedBy);
        /*if ((stockDetailModel.getCertifiedBy() != null)
                && !stockDetailModel.getCertifiedBy().isEmpty()
                && !"null".equalsIgnoreCase(stockDetailModel.getCertifiedBy())) {
            int drawable = ApplicationController.certificationBrandMap.get(Integer.parseInt(stockDetailModel.getCertifiedBy()));
            if (drawable == 0) {
                certifiedBy.setVisibility(View.GONE);
            } else {
                certifiedBy.setImageResource(drawable);
            }
        } else {
            certifiedBy.setVisibility(View.GONE);
        }*/


        TextView kmsDriven = (TextView) findViewById(R.id.kmsDriven);
        kmsDriven.setText(stockDetailModel.getKms());

        kmsDriven.setOnTouchListener(null);

        TextView fuelType = (TextView) findViewById(R.id.stockFuelType);
        fuelType.setText(stockDetailModel.getFuelType());
        fuelType.setOnTouchListener(null);


        TextView modelYear = (TextView) findViewById(R.id.stockYear);
        modelYear.setText(stockDetailModel.getModelYear());
        modelYear.setOnTouchListener(null);

        TextView owner = (TextView) findViewById(R.id.ownership);
        owner.setText(ApplicationController.numOwnersMap.get(stockDetailModel.getOwnerNumber()));
        owner.setOnTouchListener(null);

        TextView registeredCity = (TextView) findViewById(R.id.registrationCity);

        if (stockDetailModel.getOwnerNumber().equals("0")) {
            registeredCity.setText(ApplicationController.numOwnersMap.get("0"));

        } else {
            registeredCity.setText(stockDetailModel.getRegistrationPlace());

        }

        TextView registeredNo = (TextView) findViewById(R.id.registrationNumber);

        if (stockDetailModel.getOwnerNumber().equals("0")) {
            registeredNo.setText(ApplicationController.numOwnersMap.get("0"));

        } else {
            registeredNo.setText(stockDetailModel.getRegistrationNumber());

        }

        TextView d2dPrice = (TextView) findViewById(R.id.d2dPrice);
        d2dPrice.setText(stockDetailModel.getD2dPrice());


        TextView postedOn = (TextView) findViewById(R.id.postedOn);
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime date = dtf.parseDateTime(stockDetailModel.getCreatedDate());

        String formattedDate = date.monthOfYear().getAsShortText(Locale.ENGLISH) + " "
                + date.getDayOfMonth() + ", "
                + date.year().getAsShortText(Locale.ENGLISH);

        postedOn.setText(formattedDate);


        TextView tax = (TextView) findViewById(R.id.taxValue);
        tax.setText(stockDetailModel.getTax());


        TextView insurance = (TextView) findViewById(R.id.insuranceValue);
        insurance.setText(stockDetailModel.getInsurance());

/*
       fabEmail = (FloatingActionButton)findViewById(R.id.fab_email);
        fabSms = (FloatingActionButton)findViewById(R.id.fab_sms);
        fabWhatsApp = (FloatingActionButton)findViewById(R.id.fab_whatsApp);
        fabAddLead = (FloatingActionButton)findViewById(R.id.fab_addLead);

        fabEmail.setOnClickListener(this);
        fabSms.setOnClickListener(this);
        fabWhatsApp.setOnClickListener(this);
        fabAddLead.setOnClickListener(this);*/

        availableCarsOptionLayout = (LinearLayout) findViewById(R.id.available_cars_option_layout);
        removedCarsOptionLayout = (LinearLayout) findViewById(R.id.removed_cars_option_layout);

        if (fromRemoved) {
            availableCarsOptionLayout.setVisibility(View.GONE);
            removedCarsOptionLayout.setVisibility(View.VISIBLE);

            edit = (RelativeLayout) findViewById(R.id.edit);
            edit.setOnClickListener(this);

            addToStock = (RelativeLayout) findViewById(R.id.add_to_stock);
            addToStock.setOnClickListener(this);

        } else {

            addLead = (RelativeLayout) findViewById(R.id.addLead);
            addLead.setOnClickListener(this);

            sendSMS = (RelativeLayout) findViewById(R.id.sendSMS);
            sendSMS.setOnClickListener(this);

            sendWhatsapp = (RelativeLayout) findViewById(R.id.sendWhatsapp);
            sendWhatsapp.setOnClickListener(this);

            sendEmail = (RelativeLayout) findViewById(R.id.sendEmail);
            sendEmail.setOnClickListener(this);
        }

    /*    editStockLayout = (RelativeLayout) findViewById(R.id.editStockLayout);
        editStockLayout.setOnClickListener(this);

        addToStockLayout = (RelativeLayout) findViewById(R.id.addToStockLayout);
        addToStockLayout.setOnClickListener(this);*/
    /*    if(getIntent().getExtras() != null) {
          if (getIntent().getExtras().getInt(Constants.STOCKS_TAB_CLICKED) == Constants.AVAILABLE_STOCKS) {
              findViewById(R.id.footerLayout).setVisibility(View.VISIBLE);
              findViewById(R.id.removedStocksFooterLayout).setVisibility(View.GONE);
          } else {
              findViewById(R.id.footerLayout).setVisibility(View.GONE);
              findViewById(R.id.removedStocksFooterLayout).setVisibility(View.VISIBLE);

          }
      }*/

        stockColor = (ImageView) findViewById(R.id.stockColor);
        if (stockDetailModel.getHexCode() != null && !stockDetailModel.getHexCode().equals("")) {
            GradientDrawable bgShape = (GradientDrawable) stockColor.getBackground();
            bgShape.setColor(Color.parseColor(stockDetailModel.getHexCode()));
        } else {
            GradientDrawable bgShape = (GradientDrawable) stockColor.getBackground();
            bgShape.setColor(Color.parseColor("#ffffff"));
        }
        imagesPager = (ViewPager) findViewById(R.id.imagesPager);
        StockImagesAdapter imagesAdapter = new StockImagesAdapter(
                this,
                stockDetailModel.getArrayImages(),
                stockDetailModel.getMake() + " " + stockDetailModel.getModelVersion());
        imagesPager.setAdapter(imagesAdapter);
        imagesPager.setOffscreenPageLimit(2);

        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.circlePagerIndicator);
        circlePageIndicator.setViewPager(imagesPager);

    }

    private void showServerErrorLayout(boolean showFullPageError) {
        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                Constants.CATEGORY_VIEW_STOCK_DETAIL,
                Constants.CATEGORY_VIEW_STOCK_DETAIL,
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK_DETAIL,
                        Constants.CATEGORY_VIEW_STOCK_DETAIL,
                        Constants.ACTION_TAP,
                        Constants.LABEL_BACK_BUTTON,
                        0);
                onBackPressed();
                return true;

            case R.id.action_two:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK_DETAIL,
                        Constants.CATEGORY_VIEW_STOCK_DETAIL,
                        Constants.ACTION_TAP,
                        Constants.LABEL_STOCK_EDIT,
                        0);
                boolean canEdit = "0".equals(stockDetailModel.getEditable());
//        GCLog.e(Constants.TAG, "Stock detail model: " + stockDetailModel.toString());
                if (canEdit) {

                    Intent intent = new Intent(this, StockAddActivity.class);
                    Bundle args = new Bundle();
                    args.putString(Constants.COMING_FROM, "SV");
                    args.putBoolean(Constants.CAN_EDIT, canEdit && CommonUtils.getBooleanSharedPreference(getApplicationContext(), Constants.CAN_EDIT, true));
                    args.putString(Constants.STOCK_ID, stockDetailModel.getStockId());
                    args.putString(Constants.CITY_NAME, stockDetailModel.getRegistrationPlace());
                    args.putString(Constants.REGISTRATION_NUMBER, stockDetailModel.getRegistrationNumber());
                    args.putString(Constants.NUM_OWNERS, stockDetailModel.getOwnerNumber());
                    args.putString(Constants.INSURANCE_TYPE, stockDetailModel.getInsurance());
                    args.putString(Constants.TAX_TYPE, stockDetailModel.getTax());
                    args.putString(Constants.SELL_TO_DEALER, stockDetailModel.getSellToDealer());
                    args.putString(Constants.DEALER_PRICE, stockDetailModel.getEditDealerPrice());
                    args.putString(Constants.STOCK_MONTH, stockDetailModel.getModelMonth());
                    args.putString(Constants.STOCK_YEAR, stockDetailModel.getModelYear());
                    args.putString(Constants.MAKE, stockDetailModel.getMake());
                    args.putInt(Constants.MAKE_ID, stockDetailModel.getMakeid());
                    args.putString(Constants.MODEL, stockDetailModel.getModel());
                    args.putString(Constants.VERSION, stockDetailModel.getVersion());
                    args.putString(Constants.KMS_DRIVEN, stockDetailModel.getEditKm());
                    args.putString(Constants.STOCK_PRICE, stockDetailModel.getEditPrice());
                    args.putString(Constants.STOCK_COLOR, stockDetailModel.getColorValue());
                    args.putString(Constants.INSURANCE_MONTH, stockDetailModel.getInsuranceMonth());
                    args.putString(Constants.INSURANCE_YEAR, stockDetailModel.getYear());
                    args.putString(Constants.CNG_FITTED, stockDetailModel.getCngFitted());
                    args.putString(Constants.SHOWROOM_NAME, stockDetailModel.getShowroom());
                    args.putString(Constants.HEXCODE, stockDetailModel.getHexCode());
                    args.putString(Constants.SHOWROOM_ID, stockDetailModel.getShowroomId());
                    args.putStringArrayList(Constants.STOCK_IMAGES, stockDetailModel.getArrayImages());
                    args.putInt(Constants.Even_Odd, stockDetailModel.getEvenOdd());
                    args.putBoolean("fromRemoved", fromRemoved);
                    intent.putExtras(args);

                    startActivityForResult(intent, Constants.ACTION_EDIT);

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    View view = getLayoutInflater().inflate(R.layout.layout_update_price_dialog, null);
                    TextView textView = (TextView) view.findViewById(R.id.text);
                    final EditText editText = (EditText) view.findViewById(R.id.editText);
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            CommonUtils.insertCommaIntoNumber(editText, s, "#,##,##,###");
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    textView.setText("This stock has been marked certified and cannot be edited. You can however update the price.\n\nCurrent Price: " + stockDetailModel.getStockPrice());
                    builder.setView(view);
                    builder.setPositiveButton(R.string.done_label, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String updatedPrice = editText.getText().toString().trim();
                            if (updatedPrice.length() < 4) {
                                CommonUtils.showToast(StockViewActivity.this, "Please enter valid amount.", Toast.LENGTH_SHORT);
                            } else {
                                if (Integer.parseInt(updatedPrice) > Integer.parseInt(stockDetailModel.getEditDealerPrice())) {
                                    makeUpdatePriceRequest(updatedPrice);
                                } else {
                                    CommonUtils.showToast(StockViewActivity.this, "Car price should be greater than dealer price", Toast.LENGTH_SHORT);
                                }
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });


                    AlertDialog dialog = builder.create();

                    if (!StockViewActivity.this.isFinishing() && !dialog.isShowing()) {
                        dialog.show();
                    }

                    //CommonUtils.showToast(this, "Stock cannot be edited.", Toast.LENGTH_SHORT);
                }
                return true;

            case R.id.action_one:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK_DETAIL,
                        Constants.CATEGORY_VIEW_STOCK_DETAIL,
                        Constants.ACTION_TAP,
                        Constants.LABEL_MAKE_PREMIUM,
                        0);
                if (CommonUtils.getStringSharedPreference(this, Constants.UC_CARDEKHO_INVENTORY, "0").equals("1")) {
                    makePremium();
                } else {
                    CommonUtils.showToast(this, getString(R.string.no_premium_package), Toast.LENGTH_SHORT);
                }
                return true;

            case R.id.action_three:


                if (certifiedCar) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_VIEW_STOCK_DETAIL,
                            Constants.CATEGORY_VIEW_STOCK_DETAIL,
                            Constants.ACTION_TAP,
                            Constants.LABEL_SOLD_WITHOUT_WARRANTY,
                            0);

                    // Done for RSA project.
                    //showRSADialog(InitialRequestCompleteEvent.RequestType.SOLD_WITHOUT_WARRANTY);

                    showConfirmationDialog(InitialRequestCompleteEvent.RequestType.SOLD_WITHOUT_WARRANTY);

                } else if (isInspected) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_VIEW_STOCK_DETAIL,
                            Constants.CATEGORY_VIEW_STOCK_DETAIL,
                            Constants.ACTION_TAP,
                            Constants.LABEL_SOLD,
                            0);

                    showConfirmationDialog(InitialRequestCompleteEvent.RequestType.MARK_SOLD_INVENTORY);

                } else {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_VIEW_STOCK_DETAIL,
                            Constants.CATEGORY_VIEW_STOCK_DETAIL,
                            Constants.ACTION_TAP,
                            Constants.LABEL_SOLD,
                            0);

                    showConfirmationDialog(InitialRequestCompleteEvent.RequestType.MARK_SOLD_INVENTORY);
                }

                return true;

            case R.id.action_four:


                if (certifiedCar) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_VIEW_STOCK_DETAIL,
                            Constants.CATEGORY_VIEW_STOCK_DETAIL,
                            Constants.ACTION_TAP,
                            Constants.LABEL_ISSUE_WARRANTY,
                            0);

                    showConfirmationDialog(InitialRequestCompleteEvent.RequestType.ISSUE_WARRANTY);

                } else if (isInspected) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_VIEW_STOCK_DETAIL,
                            Constants.CATEGORY_VIEW_STOCK_DETAIL,
                            Constants.ACTION_TAP,
                            Constants.LABEL_REMOVE,
                            0);

                    showConfirmationDialog(InitialRequestCompleteEvent.RequestType.REMOVE_INVENTORY);

                } else {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_VIEW_STOCK_DETAIL,
                            Constants.CATEGORY_VIEW_STOCK_DETAIL,
                            Constants.ACTION_TAP,
                            Constants.LABEL_REMOVE,
                            0);

                    showConfirmationDialog(InitialRequestCompleteEvent.RequestType.REMOVE_INVENTORY);
                }
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    /*private void showRSADialog(final InitialRequestCompleteEvent.RequestType requestType) {

        if (CommonUtils.getBooleanSharedPreference(this, Constants.RSA_DEALER, false)) {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.layout_rsa_view);
            dialog.setTitle(R.string.issue_rsa);

            Button yes = (Button) dialog.findViewById(R.id.yes);
            Button no = (Button) dialog.findViewById(R.id.no);

            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    ApplicationController.getInstance().getGAHelper().sendEvent(
                            GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_VIEW_STOCK_DETAIL,
                            Constants.CATEGORY_VIEW_STOCK_DETAIL,
                            Constants.ACTION_TAP,
                            Constants.LABEL_ISSUE_RSA_STARTED + " - " + Constants.STOCK_DETAIL,
                            0
                    );

                    try {
                        CommonUtils.logRSAEvent(
                                stockDetailModel.getStockId(),
                                Constants.STOCK_DETAIL,
                                Constants.LABEL_ISSUE_RSA_STARTED);
                    } catch (Exception e) {
                        GCLog.e("exception: " + e.getMessage());
                    }
                    Intent intent = new Intent(StockViewActivity.this, RSACustomerInfoActivity.class);
                    intent.putExtra(Constants.SOURCE, Constants.STOCK_DETAIL);
                    intent.putExtra(Constants.MODEL_DATA, getModelDataForCar());
                    startActivityForResult(intent, Constants.RSA_CAR_SELECTED_REQUEST_CODE);
                }
            });

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    showConfirmationDialog(requestType);
                }
            });

            if (!dialog.isShowing() && !this.isFinishing()) {
                dialog.show();
            }
        } else {
            showConfirmationDialog(requestType);
        }
    }*/

    public void syncStockService(){
        Intent intent = new Intent(getBaseContext(), SyncStocksService.class);
        getBaseContext().stopService(intent);
        getBaseContext().startService(intent);
    }

    private void makeUpdatePriceRequest(String updatedPrice) {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.CAR_ID, stockDetailModel.getStockId());
        params.put(Constants.UPDATED_STOCK_PRICE, updatedPrice);
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
        params.put(Constants.METHOD_LABEL, Constants.UPDATE_PRICE_METHOD);
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(getApplicationContext(), Constants.UC_DEALER_USERNAME, ""));
        RetrofitRequest.EditPriceRequest(params, new Callback<UpdatePriceModel>() {
            @Override
            public void success(UpdatePriceModel updatePriceModel, retrofit.client.Response response) {
                if ("T".equalsIgnoreCase(updatePriceModel.getStatus())) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_VIEW_STOCK_DETAIL,
                            Constants.CATEGORY_VIEW_STOCK_DETAIL,
                            Constants.ACTION_TAP,
                            Constants.LABEL_UPDATE_PRICE,
                            0);
                    syncStockService();
                    CommonUtils.showToast(StockViewActivity.this, updatePriceModel.getMessage(), Toast.LENGTH_SHORT);
                    if (fromRemoved) {
                        onActivityResult(Constants.ACTION_EDIT, Activity.RESULT_OK, null);
                    } else {
                        makeStockDetailRequest(false);
                    }
                } else {
                    CommonUtils.showToast(StockViewActivity.this, updatePriceModel.getErrorMessage(), Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(StockViewActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                } else {
                    CommonUtils.showToast(StockViewActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                }
            }
        });
       /* EditStockPriceRequest editPriceRequest = new EditStockPriceRequest(this,
                Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<UpdatePriceModel>() {
                    @Override
                    public void onResponse(UpdatePriceModel response) {
                        if ("T".equalsIgnoreCase(response.getStatus())) {
                            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                    Constants.CATEGORY_VIEW_STOCK_DETAIL,
                                    Constants.CATEGORY_VIEW_STOCK_DETAIL,
                                    Constants.ACTION_TAP,
                                    Constants.LABEL_UPDATE_PRICE,
                                    0);
                            CommonUtils.showToast(StockViewActivity.this, response.getMessage(), Toast.LENGTH_SHORT);
                            if (fromRemoved) {
                                onActivityResult(Constants.ACTION_EDIT, Activity.RESULT_OK, null);
                            } else {
                                makeStockDetailRequest(false);
                            }
                        } else {
                            CommonUtils.showToast(StockViewActivity.this, response.getErrorMessage(), Toast.LENGTH_SHORT);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getCause() instanceof UnknownHostException) {
                            CommonUtils.showToast(StockViewActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                        } else {
                            CommonUtils.showToast(StockViewActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                        }
                    }
                });

        ApplicationController.getInstance().addToRequestQueue(editPriceRequest, Constants.TAG_EDIT_PRICE, false, this);*/

    }

    private void makePremium() {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.CAR_ID, carId);
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.METHOD_LABEL, Constants.MAKE_PREMIUM_METHOD);
        params.put(Constants.UC_DEALER_USERNAME, CommonUtils.getStringSharedPreference(getApplicationContext(), Constants.UC_DEALER_USERNAME, ""));

        if (isPremium) {
            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                    Constants.CATEGORY_VIEW_STOCK_DETAIL,
                    Constants.CATEGORY_VIEW_STOCK_DETAIL,
                    Constants.ACTION_TAP,
                    Constants.LABEL_REMOVE_PREMIUM,
                    0);
            params.put("type", "remove");
        } else {
            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                    Constants.CATEGORY_VIEW_STOCK_DETAIL,
                    Constants.CATEGORY_VIEW_STOCK_DETAIL,
                    Constants.ACTION_TAP,
                    Constants.LABEL_ADD_PREMIUM,
                    0);
            params.put("type", "add");
        }

        RetrofitRequest.MakePremiumRequest(params, new Callback<MakePremiumModel>() {
            @Override
            public void success(MakePremiumModel makePremiumModel, retrofit.client.Response response) {
                if ("T".equalsIgnoreCase(makePremiumModel.getStatus())) {
                    CommonUtils.showToast(StockViewActivity.this, makePremiumModel.getMessage(), Toast.LENGTH_SHORT);
                    showOverflowMenuItem = false;
                    // invalidateOptionsMenu();
                    setInitialView();
                    makeStockDetailRequest(true);

                } else {
                    CommonUtils.showToast(StockViewActivity.this, makePremiumModel.getErrorMessage(), Toast.LENGTH_SHORT);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(StockViewActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                } else {
                    CommonUtils.showToast(StockViewActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                }
            }
        });

        /*MakePremiumRequest request = new MakePremiumRequest(
                this,
                Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<MakePremiumModel>() {
                    @Override
                    public void onResponse(MakePremiumModel response) {
                        if ("T".equalsIgnoreCase(response.getStatus())) {
                            CommonUtils.showToast(StockViewActivity.this, response.getMessage(), Toast.LENGTH_SHORT);
                            showOverflowMenuItem = false;
                            // invalidateOptionsMenu();
                            setInitialView();
                            makeStockDetailRequest(true);

                        } else {
                            CommonUtils.showToast(StockViewActivity.this, response.getErrorMessage(), Toast.LENGTH_SHORT);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getCause() instanceof UnknownHostException) {
                            CommonUtils.showToast(StockViewActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                        } else {
                            CommonUtils.showToast(StockViewActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                        }

                    }
                }
        );

        ApplicationController.getInstance().addToRequestQueue(request, Constants.TAG_MAKE_PREMIUM, false, this);*/
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!fromRemoved && menu != null && menu.size() > 0) {
            if (showOverflowMenuItem) {
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setVisible(true);
                menu.getItem(2).setVisible(true);
                menu.getItem(3).setVisible(true);

                if (stockDetailModel != null) {
                    boolean isCertified = (stockDetailModel.getTrustmarkCertified() == 1);
                    isInspected = (stockDetailModel.getInspected() == 1);

                    if (isCertified) {
                        certifiedCar = true;
                        menu.getItem(2).setTitle("Sold without Warranty");
                        menu.getItem(3).setTitle("Issue Warranty");
                    } else {
                        certifiedCar = false;
                        menu.getItem(2).setTitle("Mark as Sold");
                        menu.getItem(3).setTitle("Remove");
                    }

                    if ("1".equals(stockDetailModel.getPremiumInventory())) {
                        isPremium = true;
                        menu.getItem(0).setTitle("Remove Premium");
                    }
                }
            } else {
                menu.getItem(0).setVisible(false);
                menu.getItem(1).setVisible(false);
                menu.getItem(2).setVisible(false);
                menu.getItem(3).setVisible(false);
            }
        }
        return true;
    }

    private void showConfirmationDialog(final InitialRequestCompleteEvent.RequestType requestType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (requestType == InitialRequestCompleteEvent.RequestType.REMOVE_INVENTORY) {

            builder.setTitle(R.string.title_remove_inventory);
            builder.setMessage("Are you sure to remove this inventory?");

        } else if (requestType == InitialRequestCompleteEvent.RequestType.MARK_SOLD_INVENTORY) {

            builder.setTitle(R.string.title_mark_sold_inventory);
            builder.setMessage("Are you sure to mark this inventory as sold?");

        } else if (requestType == InitialRequestCompleteEvent.RequestType.SOLD_WITHOUT_WARRANTY) {

            builder.setTitle("Sold without warranty");
            builder.setMessage("Are you sure to mark this inventory as sold without warranty?");

        } else if (requestType == InitialRequestCompleteEvent.RequestType.SOLD_WITH_WARRANTY) {

            builder.setTitle("Sold with warranty");
            builder.setMessage("Are you sure to mark this inventory as sold with warranty?");

        } else if (requestType == InitialRequestCompleteEvent.RequestType.ISSUE_WARRANTY) {

            builder.setTitle("Issue warranty");
            builder.setMessage("Are you sure to issue warranty for this inventory?");

        }

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (requestType == InitialRequestCompleteEvent.RequestType.REMOVE_INVENTORY) {

                    makeStockRemoveRequest(false);

                } else if (requestType == InitialRequestCompleteEvent.RequestType.MARK_SOLD_INVENTORY) {

                    makeInventorySoldRequest(false);

                } else if (requestType == InitialRequestCompleteEvent.RequestType.SOLD_WITHOUT_WARRANTY) {
                    makeStockSoldWithoutWarrantyRequest(false);
//                    showSoldWithWarrantyScreen(Constants.ISSUE_WARRANTY);
                } else if (requestType == InitialRequestCompleteEvent.RequestType.ISSUE_WARRANTY) {
                    showSoldWithWarrantyScreen(Constants.ISSUE_WARRANTY);
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();

        if (!this.isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }
    }

    private CertifiedCarData getModelDataForCar() {
        CertifiedCarData certifiedCarData = new CertifiedCarData();
        certifiedCarData.setUsedCarID(stockDetailModel.getStockId());
        certifiedCarData.setPricefrom(stockDetailModel.getStockPrice());
        certifiedCarData.setRegno(stockDetailModel.getRegistrationNumber());
        certifiedCarData.setCertificationID(stockDetailModel.getCertificationId());
        certifiedCarData.setMake_id(stockDetailModel.getMakeid());
        certifiedCarData.setModel(stockDetailModel.getModel());
        certifiedCarData.setCarversion(stockDetailModel.getVersion());

        return certifiedCarData;
    }

    private void showSoldWithWarrantyScreen(int warrantyCode) {
        CertifiedCarData certifiedCarData = getModelDataForCar();

        Intent intent = new Intent(this, IssueWarrantyFormActivity.class);
        intent.putExtra(Constants.MODEL_DATA, certifiedCarData);
        startActivityForResult(intent, warrantyCode);
        finish();
    }

    private void makeStockSoldWithoutWarrantyRequest(boolean showFullPageError) {
        HashMap<String, String> params = new HashMap<String, String>();
        ViewCertificationCarsWarrantyInput certcarsWarrantyInput = new ViewCertificationCarsWarrantyInput();
        certcarsWarrantyInput.setApikey(Constants.API_KEY);
        certcarsWarrantyInput
                .setMethod(Constants.WARRANTY_UPDATECAR_STOCKSTATUS_METHOD);
        certcarsWarrantyInput.setOutput(Constants.API_RESPONSE_FORMAT);
        certcarsWarrantyInput.setUsername(CommonUtils.getStringSharedPreference(this,
                Constants.UC_DEALER_USERNAME, ""));
        certcarsWarrantyInput.setNormal_password(CommonUtils.getStringSharedPreference(this,
                Constants.UC_DEALER_PASSWORD, ""));
//
//		certcarsWarrantyInput.setUsername("aditicars9233@gmail.com");
//		certcarsWarrantyInput.setNormal_password("hijklm");
        certcarsWarrantyInput.setCertificateID(stockDetailModel.getCertificationId());
        certcarsWarrantyInput.setCarStatus("2");
        certcarsWarrantyInput.setRemarks("");
        Gson gson = new Gson();
        String request_string = gson.toJson(certcarsWarrantyInput,
                ViewCertificationCarsWarrantyInput.class);
        params.put(Constants.EVALUATIONDATA, request_string);
        //progressDialog.show();

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(gson.toJson(certcarsWarrantyInput,
                    ViewCertificationCarsWarrantyInput.class));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RetrofitRequest.stocksRequest(jsonObject, new Callback<ViewCertifiedCarModel>() {
            @Override
            public void success(ViewCertifiedCarModel viewCertifiedCarModel, retrofit.client.Response response) {
                if (viewCertifiedCarModel.getStatus().equals("T")) {
                    CommonUtils.showToast(StockViewActivity.this, viewCertifiedCarModel.getMsg(),
                            Toast.LENGTH_SHORT);
                    syncStockService();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    CommonUtils.showToast(StockViewActivity.this,
                            viewCertifiedCarModel.getError(), Toast.LENGTH_SHORT);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(StockViewActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);

                } else {
                    CommonUtils.showToast(StockViewActivity.this, "Server Error. Please try again later.",
                            Toast.LENGTH_SHORT);

                }
            }
        });

      /*  ViewCertifiedCarRequest stocksRequest = new ViewCertifiedCarRequest(
                this,
                Request.Method.POST,
                Constants.getWarrantyWebServiceURL(this),
                params,
                new Response.Listener<ViewCertifiedCarModel>() {
                    @Override
                    public void onResponse(ViewCertifiedCarModel response) {

                        if (response.getStatus().equals("T")) {
                            CommonUtils.showToast(StockViewActivity.this, response.getMsg(),
                                    Toast.LENGTH_SHORT);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            CommonUtils.showToast(StockViewActivity.this,
                                    response.getError(), Toast.LENGTH_SHORT);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(StockViewActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);

                } else {
                    CommonUtils.showToast(StockViewActivity.this, "Server Error. Please try again later.",
                            Toast.LENGTH_SHORT);

                }

            }
        });

        ApplicationController.getInstance().addToRequestQueue(stocksRequest,
                Constants.TAG_STOCKS_LIST, true, this);*/
    }

    private void makeStockRemoveRequest(boolean showFullPageError) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.CAR_IDS, carId);
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.METHOD_LABEL, Constants.STOCK_REMOVE_METHOD);
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(getApplicationContext(), Constants.UC_DEALER_USERNAME, ""));

        RetrofitRequest.makeStockRemoveRequest(getApplicationContext(), params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, retrofit.client.Response response) {
                if ("T".equalsIgnoreCase(generalResponse.getStatus())) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_VIEW_STOCK_DETAIL,
                            Constants.CATEGORY_VIEW_STOCK_DETAIL,
                            Constants.ACTION_TAP,
                            Constants.LABEL_REMOVE,
                            0);
                    CommonUtils.showToast(StockViewActivity.this, "Stock removed successfully.", Toast.LENGTH_SHORT);
                    syncStockService();
                    setResult(RESULT_OK);
                    finish();

                } else {
                    CommonUtils.showToast(StockViewActivity.this, "Some problem occurred while removing stock. Please try again later.", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(StockViewActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                } else {
                    CommonUtils.showToast(StockViewActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                }
            }
        });

      /*  StockRemoveRequest stockRemoveRequest = new StockRemoveRequest(
                this,
                Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<GeneralResponse>() {
                    @Override
                    public void onResponse(GeneralResponse response) {
                        if ("T".equalsIgnoreCase(response.getStatus())) {
                            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                    Constants.CATEGORY_VIEW_STOCK_DETAIL,
                                    Constants.CATEGORY_VIEW_STOCK_DETAIL,
                                    Constants.ACTION_TAP,
                                    Constants.LABEL_REMOVE,
                                    0);
                            CommonUtils.showToast(StockViewActivity.this, "Stock removed successfully.", Toast.LENGTH_SHORT);
                            setResult(RESULT_OK);
                            finish();

                        } else {
                            CommonUtils.showToast(StockViewActivity.this, "Some problem occurred while removing stock. Please try again later.", Toast.LENGTH_LONG);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getCause() instanceof UnknownHostException) {
                            CommonUtils.showToast(StockViewActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                        } else {
                            CommonUtils.showToast(StockViewActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                        }
                    }
                }
        );

        ApplicationController.getInstance().addToRequestQueue(stockRemoveRequest, Constants.TAG_REMOVE_STOCK, showFullPageError, this);
   */ }


    private void makeInventorySoldRequest(boolean showFullPageError) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.CAR_IDS, carId);
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.METHOD_LABEL, Constants.STOCK_SOLD_METHOD);
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(getApplicationContext(), Constants.UC_DEALER_USERNAME, ""));

        RetrofitRequest.markInventorySoldRequest(getApplicationContext(), params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, retrofit.client.Response response) {
                if (!"T".equalsIgnoreCase(generalResponse.getStatus())) {

                    CommonUtils.showToast(StockViewActivity.this, generalResponse.getError(), Toast.LENGTH_LONG);
                } else {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_VIEW_STOCK_DETAIL,
                            Constants.CATEGORY_VIEW_STOCK_DETAIL,
                            Constants.ACTION_TAP,
                            Constants.LABEL_SOLD,
                            0);
                    CommonUtils.showToast(StockViewActivity.this, "Stock marked as sold successfully.", Toast.LENGTH_SHORT);
                    syncStockService();
                    setResult(RESULT_FIRST_USER);
                    finish();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(StockViewActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                } else {
                    CommonUtils.showToast(StockViewActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                }
            }
        });

        /*MarkSoldInventoryRequest markInventorySoldRequest = new MarkSoldInventoryRequest(
                this,
                Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<GeneralResponse>() {
                    @Override
                    public void onResponse(GeneralResponse response) {
                        if (!"T".equalsIgnoreCase(response.getStatus())) {

                            CommonUtils.showToast(StockViewActivity.this, response.getError(), Toast.LENGTH_LONG);
                        } else {
                            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                    Constants.CATEGORY_VIEW_STOCK_DETAIL,
                                    Constants.CATEGORY_VIEW_STOCK_DETAIL,
                                    Constants.ACTION_TAP,
                                    Constants.LABEL_SOLD,
                                    0);
                            CommonUtils.showToast(StockViewActivity.this, "Stock marked as sold successfully.", Toast.LENGTH_SHORT);
                            setResult(RESULT_FIRST_USER);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getCause() instanceof UnknownHostException) {
                            CommonUtils.showToast(StockViewActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                        } else {
                            CommonUtils.showToast(StockViewActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                        }
                    }
                }
        );

        ApplicationController.getInstance().addToRequestQueue(markInventorySoldRequest, Constants.TAG_MARK_SOLD, false, this);
   */ }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.ADD_LEAD:
            case Constants.ACTION_EDIT:
                if (resultCode == RESULT_OK) {
                    if (fromRemoved) {
                        makeAddToStockRequest();
                    } else {
                        showOverflowMenuItem = false;
                        shouldReloadList = true;
                        setResult(RESULT_OK);
                        invalidateOptionsMenu();
                        //setInitialView();
                        makeStockDetailRequest(true);
                    }
                }
                break;

            case Constants.STOCKS_CONTACT_LIST:
                if (resultCode == RESULT_OK) {
                    Uri contactUri = data.getData();

                    Cursor cursor = getContentResolver().query(contactUri,
                            new String[]{ContactsContract.Contacts._ID},
                            null, null, null);
                    cursor.moveToFirst();

                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

                    //phones.moveToFirst();

                    if (phones.getCount() >= 1) {
                        while (phones.moveToNext()) {
                            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            //GCLog.e(Constants.TAG, "number: " + number);
                            try {
                                this.carId = shareTypeEvent.getCarId();
                                mGShareToUtil.sendSMSHelp(number, shareTypeEvent.getShareText(), carId);
                            } catch (Exception e) {
                                CommonUtils.showToast(this, "Could not send SMS. Invalid phone number.", Toast.LENGTH_SHORT);
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
                        CommonUtils.showToast(this, "Cannot send SMS. No contact number present", Toast.LENGTH_SHORT);

                    }
                    phones.close();

                    //GCLog.e(Constants.TAG, "cursor details: " + cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
                    cursor.close();
                }
                break;

            case Constants.ISSUE_WARRANTY:
            case Constants.SOLD_WITH_WARRANTY:
                if (resultCode == RESULT_OK) {
                    syncStockService();
                    shouldReloadList = true;
                    onBackPressed();
                }
                break;

            case Constants.RSA_CAR_SELECTED_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    syncStockService();
                    setResult(RESULT_OK);
                    finish();
                    finishActivity(Constants.RSA_CAR_SELECTED_REQUEST_CODE);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (!fromRemoved && mToolbarView != null) {
            mToolbarView.inflateMenu(R.menu.stock_detail_menu);
            this.menu = menu;
        }

        return true;
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        Bundle bundle = new Bundle();
        switch (v.getId()) {


            case R.id.edit:
                onOptionsItemSelected(getMenuItem());
                break;

            case R.id.add_to_stock:
                makeAddToStockRequest();
                break;

            case R.id.sendEmail:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK_DETAIL,
                        Constants.CATEGORY_VIEW_STOCK_DETAIL,
                        Constants.ACTION_TAP,
                        Constants.LABEL_STOCK_DETAIL_SEND_EMAIL,
                        0);
                ShareTypeEvent shareTypeEvent = new ShareTypeEvent(ShareType.EMAIL, stockDetailModel.getShareText(), stockDetailModel.getStockId(), Constants.STOCKS);
                shareTypeEvent.setExtraText(carId);
                ApplicationController.getEventBus().post(shareTypeEvent);
                break;


            case R.id.addLead:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK_DETAIL,
                        Constants.CATEGORY_VIEW_STOCK_DETAIL,
                        Constants.ACTION_TAP,
                        Constants.LABEL_STOCK_DETAIL_ADD_LEAD,
                        0);

                String warrantyOnlyDealer = CommonUtils.getStringSharedPreference(this, Constants.UC_WARRANTY_ONLY_DEALER, "0");

                if ("0".equals(warrantyOnlyDealer)) {
                    intent = new Intent(this, StockAddLeadActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable(Constants.MODEL_DATA, stockDetailModel);
                    intent.putExtras(args);
                    startActivityForResult(intent, Constants.ADD_LEAD);

                } else {
                    CommonUtils.showToast(this, getString(R.string.warranty_only_dealer_message), Toast.LENGTH_SHORT);

                }
                break;


            case R.id.sendSMS:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK_DETAIL,
                        Constants.CATEGORY_VIEW_STOCK_DETAIL,
                        Constants.ACTION_TAP,
                        Constants.LABEL_STOCK_DETAIL_SEND_SMS,
                        0);
                ApplicationController.getEventBus().post(new ShareTypeEvent(ShareType.SMS, stockDetailModel.getShareText(), stockDetailModel.getStockId(), Constants.STOCKS));
                break;


            case R.id.sendWhatsapp:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK_DETAIL,
                        Constants.CATEGORY_VIEW_STOCK_DETAIL,
                        Constants.ACTION_TAP,
                        Constants.LABEL_STOCK_DETAIL_SEND_WHATSAPP,
                        0);

                ApplicationController.getEventBus().post(new ShareTypeEvent(ShareType.WHATSAPP,
                        stockDetailModel.getShareText(),
                        stockDetailModel.getStockId(),
                        (stockDetailModel.getArrayImages() != null && stockDetailModel.getArrayImages().size() > 0)
                                ? stockDetailModel.getArrayImages().get(0) : "",
                        Constants.STOCKS));
                break;

            case R.id.totalLeadsLayout:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK_DETAIL,
                        Constants.CATEGORY_VIEW_STOCK_DETAIL,
                        Constants.ACTION_TAP,
                        Constants.LABEL_STOCK_DETAIL_VIEW_LEADS,
                        0);

                if (CommonUtils.getStringSharedPreference(this, Constants.UC_WARRANTY_ONLY_DEALER, "0").equals("0")) {
                    String tag = (String) v.getTag();
                    if ((tag != null) && !tag.isEmpty() && tag.equalsIgnoreCase("NO_LEADS")) {
                        CommonUtils.showToast(this, "No leads for this stock.", Toast.LENGTH_SHORT);
                    } else {
                        intent = new Intent(this, CarLeadsActivity.class);
                        bundle.putString(Constants.CAR_ID, carId);
                        bundle.putInt(Constants.MAKE, stockDetailModel.getMakeid());
                        bundle.putString(Constants.MODELVERSION, stockDetailModel.getModelVersion());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } else {
                    CommonUtils.showToast(this, getString(R.string.warranty_only_dealer_message), Toast.LENGTH_SHORT);
                }
                break;

            case R.id.retry:
                setInitialView();
                makeStockDetailRequest(true);
                break;

            /*case R.id.share:

                *//*showSharePopup(v);*//*

                SharePopupWindow sharePopupWindow = new SharePopupWindow(this, R.layout.layout_share);
                sharePopupWindow.setShareText(stockDetailModel.getShareText());
                sharePopupWindow.show(mActionBar.getCustomView());

                break;*/
        }
    }

    private void showSharePopup(View anchorView) {
        final PopupMenu popupMenu = new PopupMenu(this, anchorView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.stock_sorting_menu);
        anchorView.post(new Runnable() {
            @Override
            public void run() {
                popupMenu.show();
            }
        });
        //popupMenu.show();
    }

    @Override
    public void onNoInternetConnection(NetworkEvent networkEvent) {

        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                Constants.CATEGORY_CAR_DETAILS,
                Constants.CATEGORY_CAR_DETAILS,
                Constants.ACTION_TAP,
                Constants.LABEL_NO_INTERNET,
                0);
      //  ApplicationController.getInstance().cancelPendingRequests(Constants.TAG_STOCK_DETAIL);

        NetworkEvent.NetworkError networkError = networkEvent.getNetworkError();
        if (networkError == NetworkEvent.NetworkError.NO_INTERNET_CONNECTION) {
            showNetworkErrorLayout(networkEvent.isShowFullPageError());
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sendEmail:
                CommonUtils.showToast(this, stockDetailModel.getShareText(), Toast.LENGTH_SHORT);
                return true;

            case R.id.sendSMS:
                CommonUtils.showToast(this, stockDetailModel.getShareText(), Toast.LENGTH_SHORT);
                return true;

            case R.id.sendWhatsapp:
                CommonUtils.showToast(this, stockDetailModel.getShareText(), Toast.LENGTH_SHORT);
                return true;
        }
        return false;
    }

    @Subscribe
    public void onShareTypeSelected(ShareTypeEvent event) {
        shareTypeEvent = event;
        ContactsPickerFragment contactsPickerFragment;
        switch (event.getShareType()) {
            case SMS:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_CAR_DETAILS,
                        Constants.CATEGORY_CAR_DETAILS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SEND_IN_SMS,
                        0);
//        GCLog.e(Constants.TAG, "Share type sms");
                mGShareToUtil.sendSMS(getString(R.string.select_contact_from), selectedIndex, event);
/*                contactsPickerFragment = ContactsPickerFragment.newInstance(
                        getString(R.string.select_contact_from),
                        selectedIndex,
                        event.getShareText(),
                        event.getShareType(),
                        event.getCarId(),
                        event.getImageURL()
                );
*/

                break;

            case WHATSAPP:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_CAR_DETAILS,
                        Constants.CATEGORY_CAR_DETAILS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SEND_WHATSAPP,
                        0);
                GCLog.e(Constants.TAG, "Share type whatsapp");
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

        /*this.carId = event.getCarId();
        sendMessageInWhatsApp(event.getShareText(), event.getCarId());*/
                break;

            case EMAIL:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_CAR_DETAILS,
                        Constants.CATEGORY_CAR_DETAILS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SEND_EMAIL,
                        0);
//        GCLog.e(Constants.TAG, "Share type email");
                mGShareToUtil.showSendEmailDialog(event);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_VIEW_STOCK);

    }

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

        contactOptionSelectedEvent = new ContactOptionSelectedEvent(
                contactType, index, shareText, shareType, carId, imageUrl);

        switch (contactType) {
            case CALL_LOGS:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_CAR_DETAILS,
                        Constants.CATEGORY_CAR_DETAILS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_CALL_LOGS,
                        0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !CommonUtils.checkForPermission(this,
                        new String[]{Manifest.permission.READ_CALL_LOG},
                        Constants.REQUEST_PERMISSION_READ_CALL_LOG, "Phone")) {
                    return;
                }
                mGShareToUtil.showCallLogsDialog(shareText, shareType, carId, imageUrl);
                break;

            case CONTACTS:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_CAR_DETAILS,
                        Constants.CATEGORY_CAR_DETAILS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_CONTACTS,
                        0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !CommonUtils.checkForPermission(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        Constants.REQUEST_PERMISSION_CONTACTS, "Contacts")) {
                    return;
                }
                mGShareToUtil.showContactsDialog(shareText, shareType, carId, imageUrl);
                break;

            case NEW_CONTACT:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !CommonUtils.checkForPermission(this,
                        new String[]{Manifest.permission.WRITE_CONTACTS},
                        Constants.REQUEST_PERMISSION_CONTACTS, "Contacts")) {
                    return;
                }
                mGShareToUtil.showAddNameToContactDialog(shareText, carId);
                break;

            case SEND_TO_NUMBER:
                mGShareToUtil.showAddNewNumToSendSMSDialog(shareText, carId);
                break;
        }
    }

    @Override
    public void onCallLogSelected(CallLogItem callLogItem, ShareType shareType, String shareText, String carId, String imageUrl) {
        mobileNumber = callLogItem.getGaadiFormatNumber();
        if (shareType == ShareType.WHATSAPP) {
            if ("null".equalsIgnoreCase(callLogItem.getName()) || (callLogItem.getName() == null)) {
                mGShareToUtil.showAddNameToContactDialog(callLogItem.getNumber(), shareText, carId);

            } else {
                mGShareToUtil.sendMessageInWhatsApp(shareText, carId);
            }

        } else if (shareType == ShareType.SMS) {
            try {
                mGShareToUtil.sendSMSHelp(callLogItem.getNumber(), shareText, carId);
            } catch (Exception e) {
                CommonUtils.showToast(this, "Could not send SMS. Invalid number", Toast.LENGTH_SHORT);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION_READ_CALL_LOG
                || requestCode == Constants.REQUEST_PERMISSION_CONTACTS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true;
            }
        }
    }

  /*  private void makeServerCallForSharedItem(ShareType shareType) {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.CAR_ID, carId);
        params.put(Constants.MOBILE_NUM, mobileNumber);
        params.put(Constants.SHARE_TYPE, shareType.name());
        params.put(Constants.SHARE_SOURCE, Constants.CATEGORY_VIEW_STOCK_DETAIL);

        ShareCarsRequest shareCarsRequest = new ShareCarsRequest(StockViewActivity.this,
                Request.Method.POST,
                Constants.getWebServiceURL(this),
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

    /*private BroadcastReceiver smsSentReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            switch (getResultCode())
            {
                case Activity.RESULT_OK:
                    mGAHelper.sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_CAR_DETAILS,
                            Constants.CATEGORY_CAR_DETAILS,
                            Constants.LABEL_SMS_SENT,
                            String.valueOf(CommonUtils.getIntSharedPreference(StockViewActivity.this, Constants.DEALER_ID, -1)),
                            0);

                    makeServerCallForSharedItem(ShareType.SMS);
                    CommonUtils.showToast(getBaseContext(), "SMS sent",
                            Toast.LENGTH_SHORT);
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    CommonUtils.showToast(getBaseContext(), "Could not send SMS",
                            Toast.LENGTH_SHORT);
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    CommonUtils.showToast(getBaseContext(), "No service",
                            Toast.LENGTH_SHORT);
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    CommonUtils.showToast(getBaseContext(), "PDU is empty",
                            Toast.LENGTH_SHORT);
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    CommonUtils.showToast(getBaseContext(), "Radio off",
                            Toast.LENGTH_SHORT);
                    break;
            }
        }
    };*/

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


        }/* else if (shareType == ShareType.SMS) {
      sendSMS(contactListItem.getContactNumber(), shareText);
    }*/
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();

        *//*if (smsReceiverRegistered) {
            unregisterReceiver(smsSentReceiver);
        }*//*
    }*/

    @Override
    public void positionHandlerCallBack(int curPosSelected) {
        int currentPos = curPosSelected;
        imagesPager.setCurrentItem(currentPos);
    }

    private void makeAddToStockRequest() {
        if (progressDialog != null && !this.isFinishing()) {
            progressDialog.show();
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.CAR_ID, stockDetailModel.getStockId());
        params.put(Constants.METHOD_LABEL, Constants.METHOD_ADD_TO_STOCK);

        RetrofitRequest.addToStock(params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, retrofit.client.Response response) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if ("T".equalsIgnoreCase(generalResponse.getStatus())) {
                    syncStockService();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    CommonUtils.showToast(StockViewActivity.this, generalResponse.getError(), Toast.LENGTH_LONG);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                GCLog.e(error.getMessage());
            }
        });
    }

    private MenuItem getMenuItem() {
        return new MenuItem() {
            @Override
            public int getItemId() {
                return R.id.action_two;
            }

            @Override
            public int getGroupId() {
                return 0;
            }

            @Override
            public int getOrder() {
                return 0;
            }

            @Override
            public MenuItem setTitle(CharSequence title) {
                return null;
            }

            @Override
            public MenuItem setTitle(int title) {
                return null;
            }

            @Override
            public CharSequence getTitle() {
                return "removed";
            }

            @Override
            public MenuItem setTitleCondensed(CharSequence title) {
                return null;
            }

            @Override
            public CharSequence getTitleCondensed() {
                return null;
            }

            @Override
            public MenuItem setIcon(Drawable icon) {
                return null;
            }

            @Override
            public MenuItem setIcon(int iconRes) {
                return null;
            }

            @Override
            public Drawable getIcon() {
                return null;
            }

            @Override
            public MenuItem setIntent(Intent intent) {
                return null;
            }

            @Override
            public Intent getIntent() {
                return null;
            }

            @Override
            public MenuItem setShortcut(char numericChar, char alphaChar) {
                return null;
            }

            @Override
            public MenuItem setNumericShortcut(char numericChar) {
                return null;
            }

            @Override
            public char getNumericShortcut() {
                return 0;
            }

            @Override
            public MenuItem setAlphabeticShortcut(char alphaChar) {
                return null;
            }

            @Override
            public char getAlphabeticShortcut() {
                return 0;
            }

            @Override
            public MenuItem setCheckable(boolean checkable) {
                return null;
            }

            @Override
            public boolean isCheckable() {
                return false;
            }

            @Override
            public MenuItem setChecked(boolean checked) {
                return null;
            }

            @Override
            public boolean isChecked() {
                return false;
            }

            @Override
            public MenuItem setVisible(boolean visible) {
                return null;
            }

            @Override
            public boolean isVisible() {
                return false;
            }

            @Override
            public MenuItem setEnabled(boolean enabled) {
                return null;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }

            @Override
            public boolean hasSubMenu() {
                return false;
            }

            @Override
            public SubMenu getSubMenu() {
                return null;
            }

            @Override
            public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
                return null;
            }

            @Override
            public ContextMenu.ContextMenuInfo getMenuInfo() {
                return null;
            }

            @Override
            public void setShowAsAction(int actionEnum) {

            }

            @Override
            public MenuItem setShowAsActionFlags(int actionEnum) {
                return null;
            }

            @Override
            public MenuItem setActionView(View view) {
                return null;
            }

            @Override
            public MenuItem setActionView(int resId) {
                return null;
            }

            @Override
            public View getActionView() {
                return null;
            }

            @Override
            public MenuItem setActionProvider(ActionProvider actionProvider) {
                return null;
            }

            @Override
            public ActionProvider getActionProvider() {
                return null;
            }

            @Override
            public boolean expandActionView() {
                return false;
            }

            @Override
            public boolean collapseActionView() {
                return false;
            }

            @Override
            public boolean isActionViewExpanded() {
                return false;
            }

            @Override
            public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
                return null;
            }
        };
    }
}
