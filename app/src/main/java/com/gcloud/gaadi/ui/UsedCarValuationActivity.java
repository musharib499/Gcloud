package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListPopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.BasicListItemAdapter;
import com.gcloud.gaadi.adapter.CityAdapter;
import com.gcloud.gaadi.adapter.ListPopupWindowAdapter;
import com.gcloud.gaadi.adapter.MakeModelVersionAdapter;
import com.gcloud.gaadi.adapter.ModelVersionAdapter;
import com.gcloud.gaadi.annotations.FirstCharValidation;
import com.gcloud.gaadi.annotations.Required;
import com.gcloud.gaadi.annotations.TextRule;
import com.gcloud.gaadi.annotations.rules.Rule;
import com.gcloud.gaadi.annotations.rules.Validator;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.MakeModelType;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.events.CancelMakeEvent;
import com.gcloud.gaadi.model.BasicListItemModel;
import com.gcloud.gaadi.model.CityData;
import com.gcloud.gaadi.model.StockDataModel;
import com.gcloud.gaadi.model.UsedCarDetailsModel;
import com.gcloud.gaadi.model.UsedCarValuationModel;
import com.gcloud.gaadi.model.VersionObject;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.gcloud.gaadi.utils.OverFlowMenu;
import com.squareup.otto.Subscribe;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class UsedCarValuationActivity extends BaseActivity implements View.OnClickListener, Validator.ValidationListener,
        AdapterView.OnItemClickListener {

    protected static boolean mRadioGroup = false;
    Validator mValidator;
    private Activity mActivity;
    @Required(order = 1, messageResId = R.string.error_make_model_required)
    @TextRule(order = 2, minLength = 1, messageResId = R.string.error_make_model_required)
    private CustomMaterialAutoCompleteTxtVw mMakeModel;
    @Required(order = 3, messageResId = R.string.error_model_version_required)
    @TextRule(order = 4, minLength = 1, messageResId = R.string.error_model_version_required)
    private EditText mModelVersion;
    @Required(order = 5, messageResId = R.string.error_registration_city_required)
    @TextRule(order = 6, minLength = 1, messageResId = R.string.error_registration_city_required)
    private AutoCompleteTextView registrationCity;
    @Required(order = 7, messageResId = R.string.error_stock_year_required)
    @TextRule(order = 8, minLength = 1, messageResId = R.string.error_stock_year_required)
    private EditText manufacturingfYear;
    @Required(order = 9, messageResId = R.string.error_kms_driven_rquired)
    @TextRule(order = 10, minLength = 3, messageResId = R.string.error_kms_driven_rquired)
    @FirstCharValidation(order = 11, notAllowedFirstChars = "0", message = "Please remove leading zeroes.")
    private EditText kmsDriven;
    @Required(order = 12, messageResId = R.string.error_num_owners_required)
    @TextRule(order = 13, minLength = 1, messageResId = R.string.error_num_owners_required)
    private EditText numOwners;
    private boolean  isClearMake = false;
    private Button mSubmitBtn;
    private ScrollView scrollView;
    private BasicListItemModel selectedNumOwners;
    private ArrayList<BasicListItemModel> numOwnersList = new ArrayList<BasicListItemModel>();
    private BasicListItemAdapter numOwnerAdapter;
    private CityAdapter cityAdapter;
    private CityData selectedCity;
    private ArrayList<CityData> cityList;
    private int stYear;
    private MakeModelVersionAdapter makeModelVersionAdapter;
    private Integer[] yearList;
    private VersionObject stock;
    private String comingFrom = "";
    private ModelVersionAdapter modelVersionAdapter;
    private GCProgressDialog gcProgressDialog;
    private RadioGroup isSellerOrBuyerRgrp;
    private RadioButton buyer;
    private boolean checkRadiobutton = false;



    private FilterQueryProvider mmFilterQueryProvider = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getCursor(constraint);
        }
    };
    private FilterQueryProvider cityFilterQueryProvider = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getCityCursor(constraint);
        }
    };

    private Cursor getCursor(CharSequence constraint) {
        Cursor cursor;
        MakeModelVersionDB db = ApplicationController.getMakeModelVersionDB();
        cursor = db.getModelRecords(constraint, false);
        return cursor;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ApplicationController.getEventBus().unregister(UsedCarValuationActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationController.getEventBus().register(UsedCarValuationActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.used_cars_valuation, frameLayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);


        gcProgressDialog = new GCProgressDialog(this, this, getString(R.string.please_wait));
        mSubmitBtn = (Button) findViewById(R.id.submit);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        registrationCity = (AutoCompleteTextView) findViewById(R.id.city);
        manufacturingfYear = (EditText) findViewById(R.id.mfgYear);
        isSellerOrBuyerRgrp = (RadioGroup) findViewById(R.id.isSellerOrBuyerRgrp);
        isSellerOrBuyerRgrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.buyer:
                        checkRadiobutton = false;
                        break;
                    case R.id.seller:
                        checkRadiobutton = true;
                        break;

                }
            }
        });

       // String radiovaluetype = ((RadioButton)findViewById(isSellerOrBuyerRgrp.getCheckedRadioButtonId())).getText().toString();

        manufacturingfYear.setEnabled(false);   // UCV requirement by Udit
        manufacturingfYear.setOnClickListener(this);
        kmsDriven = (EditText) findViewById(R.id.kmsDriven);

        numOwners = (EditText) findViewById(R.id.numOwners);
        numOwners.setOnClickListener(this);

        mSubmitBtn.setOnClickListener(this);
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        makeModelVersionAdapter = new MakeModelVersionAdapter(this, null);
        mMakeModel = (CustomMaterialAutoCompleteTxtVw) findViewById(R.id.makeModel);
        mMakeModel.setType(MakeModelType.MAKE);
        mMakeModel.setThreshold(1);
        mMakeModel.setAdapter(makeModelVersionAdapter);
        makeModelVersionAdapter.setFilterQueryProvider(mmFilterQueryProvider);
        mMakeModel.setOnItemClickListener(this);
        mMakeModel.preventFocusReverseAnimation = true;
        buyer=(RadioButton)findViewById(R.id.buyer);

        mModelVersion = (EditText) findViewById(R.id.version);
        mModelVersion.setClickable(false);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //GCLog.e("event: " + event.toString());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        //GCLog.e("Scroll event.");
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                        return false;
                }
                return false;
            }
        });

        formLists();
        //createYearList();
        numOwners.setText(numOwnerAdapter.getItem(0).getValue());
        selectedNumOwners = new BasicListItemModel(numOwnerAdapter.getItem(0).getId(), numOwnerAdapter.getItem(0).getValue());

        cityAdapter = new CityAdapter(UsedCarValuationActivity.this, null);
        registrationCity.setAdapter(cityAdapter);
        registrationCity.setThreshold(1);
        registrationCity.setOnItemClickListener(this);
        cityAdapter.setFilterQueryProvider(cityFilterQueryProvider);
        selectedCity = new CityData();
        selectedCity.setCityName(CommonUtils.getStringSharedPreference(UsedCarValuationActivity.this, Constants.UC_DEALER_CITY, ""));
        selectedCity.setCityId(CommonUtils.getStringSharedPreference(UsedCarValuationActivity.this, Constants.UC_DEALER_CITY, ""));
        registrationCity.setText(selectedCity.getCityName());
        //  manufacturingfYear.setText("" + stYear);
        if (savedInstanceState != null) {
            cityList = (ArrayList<CityData>) savedInstanceState.getSerializable(Constants.CITY_LIST);
        }

        kmsDriven.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CommonUtils.insertCommaIntoNumber(kmsDriven, s, "##,##,###");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_USED_CAR_VALUATION);

    }

    @Override
    public void onClick(View v) {
        final ListPopupWindow listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setModal(true);
        listPopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),
                R.drawable.abc_popup_background_mtrl_mult,
                null));
        listPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
        switch (v.getId()) {

            case R.id.submit:
                mValidator.validate();
                break;

            case R.id.mfgYear:
                listPopupWindow.setAdapter(new ListPopupWindowAdapter(this, yearList, false));
                listPopupWindow.setAnchorView(findViewById(R.id.mfgYear));
                listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        manufacturingfYear.setText(yearList[position].toString());

                        stYear = yearList[position];
                        listPopupWindow.dismiss();
                    }
                });
                manufacturingfYear.post(new Runnable() {
                    @Override
                    public void run() {
                        listPopupWindow.show();
                    }
                });
                break;

            case R.id.numOwners:
                final ListPopupWindow numOwnersPopupWindow = new ListPopupWindow(this);
                numOwnersPopupWindow.setAdapter(numOwnerAdapter);
                numOwnersPopupWindow.setModal(true);
                numOwnersPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
                /*numOwnersPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));*/
                numOwnersPopupWindow.setAnchorView(findViewById(R.id.numOwners));
                numOwnersPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
                numOwnersPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedNumOwners = (BasicListItemModel) parent.getAdapter().getItem(position);
                        numOwners.setText(selectedNumOwners.getValue());

                       /* if (selectedNumOwners.getId().equals("0")) {
                            hideFieldsforUnregisteredCase();
                        } else {
                            showFieldsForRegisteredCase();
                        }*/
                        numOwnersPopupWindow.dismiss();
                    }
                });
                numOwners.post(new Runnable() {
                    @Override
                    public void run() {
                        numOwnersPopupWindow.show();
                    }
                });
                break;

            case R.id.version:
                final ListPopupWindow popupWindow = new ListPopupWindow(this);
                mModelVersion.setError(null);
                Cursor cursor1 = null;
                if (stock != null) {
                    cursor1 = ApplicationController.getMakeModelVersionDB().getVersionForMakeModel(stock);
                } else {
                    if (comingFrom.equals("") && ApplicationController.getStocksDB().getStocksCount() > 0) {
                        StockDataModel mStockDatModel = ApplicationController.getStocksDB().getStocksData();
                        if (!mStockDatModel.getMake().equals("") && !mStockDatModel.getModelID().equals("")) {
                            cursor1 = ApplicationController.getMakeModelVersionDB().
                                    getVersionForMakeModelfromDB(mStockDatModel.getMake(), mStockDatModel.getModelID());
                        }

                    }
                }
                modelVersionAdapter = new ModelVersionAdapter(this, cursor1);
                popupWindow.setModal(true);
                popupWindow.setAnchorView(findViewById(R.id.version));
                popupWindow.setAdapter(modelVersionAdapter);

                popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
                        String version = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.VERSIONNAME));
                        String versionId = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.VERSIONID));
                        String fuelType = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.FUEL_TYPE));
                        String transmissionType = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.TRANSMISSION_TYPE));
                        String modelVersion = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MMV_COLUMN_MODEL_VERSION));
                        String makeModelVersion = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MMV_COLUMN_MAKE_MODEL_VERSION));

                        if (stock != null) {
                            stock.setVersionid(versionId);
                            stock.setVersionName(version);
                            stock.setTransmission(transmissionType);
                            stock.setFuelType(fuelType);
                            stock.setModelVersion(modelVersion);
                            stock.setMakeModelVersion(makeModelVersion);
                            popupWindow.dismiss();
                            mModelVersion.setText(version);

                            makeYearListRequest();

                        } else {
                            if (comingFrom.equals("") && ApplicationController.getStocksDB().getStocksCount() > 0) {
                                StockDataModel mStockDatModel = ApplicationController.getStocksDB().getStocksData();
                                if (!mStockDatModel.getMake().equals("") && !mStockDatModel.getModelID().equals("")) {
                                    stock = new VersionObject();
                                    stock.setMake(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MAKENAME)));
                                    stock.setMakeId(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MAKEID)));
                                    stock.setModel(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MODELNAME)));
                                    stock.setModelId(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MODELID)));
                                    stock.setVersionid(versionId);
                                    stock.setVersionName(version);
                                    stock.setTransmission(transmissionType);
                                    stock.setFuelType(fuelType);
                                    stock.setModelVersion(modelVersion);
                                    stock.setMakeModelVersion(makeModelVersion);
                                    popupWindow.dismiss();
                                    mModelVersion.setText(version);


                                }
                            }

                        }

                    }


                });

                mModelVersion.post(new Runnable() {
                    @Override
                    public void run() {
                        popupWindow.show();
                    }
                });


                break;




        }

    }

    private void makeYearListRequest() {
        if (gcProgressDialog != null && !gcProgressDialog.isShowing()) {
            gcProgressDialog.show();
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.METHOD_LABEL, Constants.TRUPRICE_YEAR_REQUEST);
        params.put(Constants.MAKEW, stock.getMakeId());
        params.put(Constants.MODELW, stock.getModelId());
        params.put(Constants.CAR_VERSION, stock.getVersionid());
        RetrofitRequest.sendUsedCarValuationData(params, new Callback<UsedCarValuationModel>() {
            @Override
            public void success(UsedCarValuationModel response, Response res) {
                if (gcProgressDialog != null) {
                    gcProgressDialog.dismiss();
                }
                createYearList(Integer.valueOf(response.getUsedCarData().getStart_year()),
                        Integer.valueOf(response.getUsedCarData().getEnd_year()));
                manufacturingfYear.setEnabled(true);
            }

            @Override
            public void failure(RetrofitError error) {
                if (gcProgressDialog != null) {
                    gcProgressDialog.dismiss();
                }
                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(UsedCarValuationActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                } else {
                    CommonUtils.showToast(UsedCarValuationActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    public void onValidationSucceeded() {

        makeFormSubmissionRequest();
    }

    private void makeFormSubmissionRequest() {
        gcProgressDialog.setCancelable(false);
        gcProgressDialog.show();
        HashMap<String, String> mParams = new HashMap<String, String>();
        mParams.put(Constants.METHOD_LABEL, Constants.USED_CAR_VALUATION_METHOD);


        mParams.put(Constants.SELLER_TYPE, isSellerOrBuyerRgrp.getCheckedRadioButtonId() == R.id.buyer ? "buyer" : "seller");

        mParams.put(Constants.UCV_MAKE, stock.getMakeId());
        mParams.put(Constants.UCV_MODEL, stock.getModelId());
        mParams.put(Constants.UCV_VARIANT,stock.getVersionid());
        mParams.put(Constants.UCV_YEAR, manufacturingfYear.getText().toString());
        mParams.put(Constants.UCV_KMSDRIVEN, kmsDriven.getText().toString().trim().replace(",",""));
        mParams.put(Constants.UCV_CITY, registrationCity.getText().toString());
        mParams.put(Constants.UCV_COLOR, "1");
        mParams.put(Constants.UCV_OWNER, selectedNumOwners.getId());

        RetrofitRequest.sendUsedCarValuationData(mParams, new Callback<UsedCarValuationModel>() {
            @Override
            public void success(UsedCarValuationModel usedCarValuationModel, Response response) {
                if (gcProgressDialog != null)
                    gcProgressDialog.dismiss();

                if ("T".equalsIgnoreCase(usedCarValuationModel.getStatus())) {
                    if ("Not Available".equalsIgnoreCase(usedCarValuationModel.getUsedCarData().getD2IExcellent())) {
                        CommonUtils.showToast(UsedCarValuationActivity.this,
                                "We do not currently have enough transaction data for the variant of your choice",
                                Toast.LENGTH_LONG);
                    } else {
                        mRadioGroup = checkRadiobutton;
                        Intent intent = new Intent(UsedCarValuationActivity.this, UCVFeedbackActivity.class);
                        intent.putExtra(Constants.USED_CAR_VALUATION_MODEL, usedCarValuationModel.getUsedCarData());
                        //startActivity(intent);

                        //Intent intent=new Intent();
                        UsedCarDetailsModel value = new UsedCarDetailsModel();
                        value.setMake(mMakeModel.getText().toString());
                        value.setVersion(mModelVersion.getText().toString());
                        value.setRegyear(manufacturingfYear.getText().toString());

                        value.setCarkm(kmsDriven.getText().toString());
                        value.setOwner(numOwners.getText().toString());
                        value.setBuyer(isSellerOrBuyerRgrp.getCheckedRadioButtonId() == R.id.buyer ? "Buyer" : "Seller");
                        value.setCity(registrationCity.getText().toString());

                        intent.putExtra("value", value);
                        startActivity(intent);
                    }
                } else {
                    CommonUtils.showToast(UsedCarValuationActivity.this, usedCarValuationModel.getError(), Toast.LENGTH_LONG);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (gcProgressDialog != null)
                    gcProgressDialog.dismiss();

                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(UsedCarValuationActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                } else {
                    CommonUtils.showToast(UsedCarValuationActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                }
            }
        });



    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        View v = (View) failedView.getParent();
        if (failedView instanceof EditText) {
            failedView.requestFocus();
            ((EditText) failedView).setError(failedRule.getFailureMessage());
            CommonUtils.scrollTo(scrollView, v);
            CommonUtils.shakeView(this, failedView);
            return;
        }

        CommonUtils.showToast(this, failedRule.getFailureMessage(), Toast.LENGTH_SHORT);
        if (v == null) {
            CommonUtils.scrollTo(scrollView, failedView);
        } else if (v.getParent() != findViewById(R.id.makeModelVersionLayout)) {
            CommonUtils.scrollTo(scrollView, (View) v.getParent());
            CommonUtils.shakeView(this, v);
            return;
        } else
            CommonUtils.scrollTo(scrollView, v);
        CommonUtils.shakeView(this, failedView);
    }

    @Override
    public void onViewValidationSucceeded(View succeededView) {

    }

    private void formLists() {

        for (Map.Entry<String, String> entry : ApplicationController.numOwnersMapUsedCarValuation.entrySet()) {
            BasicListItemModel listItem = new BasicListItemModel(entry.getKey(), entry.getValue());
            numOwnersList.add(listItem);
        }

        numOwnersList.trimToSize();

        numOwnerAdapter = new BasicListItemAdapter(this, numOwnersList);
    }

    private Cursor getCityCursor(CharSequence constraint) {
        return ApplicationController.getMakeModelVersionDB().getCityRecords(constraint);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getAdapter() instanceof MakeModelVersionAdapter) {

            Cursor cursor = (Cursor) mMakeModel.getAdapter().getItem(position);

            int rowid = cursor.getInt(cursor.getColumnIndex(MakeModelVersionDB.ID));
            String makeId = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MAKEID));
            String make = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MAKENAME));
            String modelId = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MODELID));
            String modelName = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MODELNAME));
            String makeModel = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MMV_COLUMN_MAKE_MODEL));


            stock = new VersionObject();
            stock.setId(rowid);
            stock.setMakeId(makeId);
            stock.setMake(make);
            stock.setModelId(modelId);
            stock.setModel(modelName);
            stock.setMakeModel(makeModel);

            GCLog.e("makeid = " + makeId);

            mMakeModel.setText(modelName);
            mMakeModel.setSelection(mMakeModel.getText().length());
            mMakeModel.setCompoundDrawablesWithIntrinsicBounds(
                    ApplicationController.makeLogoMap.get(Integer.parseInt(makeId)),
                    0,
                    R.drawable.close_layer_dark,
                    0);
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mMakeModel.getWindowToken(), 0);
            mModelVersion.setEnabled(true);
            mModelVersion.setOnClickListener(this);

        } else if (parent.getAdapter() instanceof CityAdapter) {
            Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
            selectedCity = new CityData();
            selectedCity.setCityId(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.CITY_ID)));
            selectedCity.setCityName(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.CITY_NAME)));
            selectedCity.setStateId(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.STATE_ID)));
            selectedCity.setRegionId(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.REGION_ID)));

            registrationCity.setText(selectedCity.getCityName());
            registrationCity.setSelection(registrationCity.getText().length());
            GCLog.e("selected City: " + selectedCity.toString());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.CITY_LIST, cityList);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cityList = (ArrayList<CityData>) savedInstanceState.getSerializable(Constants.CITY_LIST);
    }

    public void createYearList(int start, int end) {
        //int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearList = new Integer[(end - start) + 1];
        for (int i = end; i >= start; i--)
            yearList[end - i] = i;
        manufacturingfYear.setText("");
    }

    @Subscribe
    public void onMakeCancelEvent(CancelMakeEvent event) {
        isClearMake = true;
        if (stock != null) {
            stock.setMakeId("");
            stock.setModel("");
            mModelVersion.setClickable(false);
            mModelVersion.setEnabled(false);
            mModelVersion.setText("");
            stock.setVersionid("");
            stock.setVersionName("");

         //   findViewById(R.id.otherColorLayout).setVisibility(View.GONE);
            if (modelVersionAdapter != null) {
                modelVersionAdapter = new ModelVersionAdapter(this, null);
                modelVersionAdapter.notifyDataSetChanged();
                //mModelVersion.setEnabled(false);
            }
        }
        manufacturingfYear.setText("");
        manufacturingfYear.setEnabled(false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                Constants.CATEGORY_USED_CAR_VALUATION,
                Constants.CATEGORY_USED_CAR_VALUATION,
                Constants.ACTION_TAP,
                Constants.LABEL_ADD_PHOTO,
                0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*getMenuInflater().inflate(R.menu.menu_filter,menu);

        return super.onCreateOptionsMenu(menu);*/

        OverFlowMenu.OverFlowMenuText(this, "Reset", 16, menu);
        OverFlowMenu.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyer.setChecked(true);
                mMakeModel.setText("");
                mModelVersion.setText("");
                kmsDriven.setText("");
                manufacturingfYear.setEnabled(false);
                manufacturingfYear.setText("");
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_reset:
                buyer.setChecked(true);
                mMakeModel.setText("");
                mModelVersion.setText("");
                kmsDriven.setText("");
                manufacturingfYear.setEnabled(false);
                manufacturingfYear.setText("");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}


