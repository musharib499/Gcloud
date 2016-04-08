package com.gcloud.gaadi.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.ImageOrdersThread;
import com.gcloud.gaadi.PhotoUpload;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.BasicListItemAdapter;
import com.gcloud.gaadi.adapter.CityAdapter;
import com.gcloud.gaadi.adapter.ColorAdapter;
import com.gcloud.gaadi.adapter.DealershipAdapter;
import com.gcloud.gaadi.adapter.MakeModelVersionAdapter;
import com.gcloud.gaadi.adapter.ModelVersionAdapter;
import com.gcloud.gaadi.annotations.FirstCharValidation;
import com.gcloud.gaadi.annotations.RadioGrp;
import com.gcloud.gaadi.annotations.Regex;
import com.gcloud.gaadi.annotations.Required;
import com.gcloud.gaadi.annotations.TextRule;
import com.gcloud.gaadi.annotations.rules.Rule;
import com.gcloud.gaadi.annotations.rules.Validator;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.DateType;
import com.gcloud.gaadi.constants.MakeModelType;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.events.CancelMakeEvent;
import com.gcloud.gaadi.events.MakeModelSelectedEvent;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.events.SetMonthYearForPicker;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.BasicListItemModel;
import com.gcloud.gaadi.model.CityData;
import com.gcloud.gaadi.model.ShowroomData;
import com.gcloud.gaadi.model.ShowroomModel;
import com.gcloud.gaadi.model.StockAddModel;
import com.gcloud.gaadi.model.StockDataModel;
import com.gcloud.gaadi.model.VariantColorsModel;
import com.gcloud.gaadi.model.VersionObject;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.service.ImageUploadService;
import com.gcloud.gaadi.service.SyncStocksService;
import com.gcloud.gaadi.ui.fourmob.datetimepicker.date.DatePickerDialog;
import com.gcloud.gaadi.ui.fourmob.datetimepicker.date.MonthYearPicker;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.gcloud.gaadi.utils.OverFlowMenu;
import com.imageuploadlib.Databases.StockImagesDB;
import com.imageuploadlib.Model.StockImageData;
import com.imageuploadlib.Model.StockImageOrderData;
import com.imageuploadlib.PhotosLibrary;
import com.imageuploadlib.Utils.FileInfo;
import com.imageuploadlib.Utils.PhotoParams;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by ankit on 21/11/14.
 */
public class StockAddActivity extends BaseActivity
        implements TextWatcher, AdapterView.OnItemClickListener, View.OnClickListener,
        DatePickerDialog.OnDateSetListener, OnNoInternetConnectionListener,
        CompoundButton.OnCheckedChangeListener, Validator.ValidationListener, RadioGroup.OnCheckedChangeListener {

    public static final int REQUEST_CODE_UPLOAD_PHOTOS = 2004;
    private static final String DATEPICKER_TAG = "DATEPICKER_TAG";
    private static final String TAG = "StockAddActivity";
    public static ArrayList<String> imagesList;
    ImageView color;
    Validator mValidator;
    MonthYearPicker monthYearPickerDialog;
    String mInsuranceType = "Insurance";
    String mTaxType = "Tax";
    @Required(order = 1, messageResId = R.string.error_make_model_required)
    @TextRule(order = 2, minLength = 1, messageResId = R.string.error_make_model_required)
    private CustomMaterialAutoCompleteTxtVw mMakeModel;
    @Required(order = 3, messageResId = R.string.error_model_version_required)
    @TextRule(order = 4, minLength = 1, messageResId = R.string.error_model_version_required)
    private EditText mModelVersion;
    @Required(order = 5, messageResId = R.string.error_stock_color_required)
    @TextRule(order = 6, minLength = 1, messageResId = R.string.error_stock_color_required)
    private EditText stockColor;
    private LinearLayout stockcolorlayout;
    @Required(order = 7, messageResId = R.string.error_stock_color_required)
    @TextRule(order = 8, minLength = 1, messageResId = R.string.error_stock_color_required)
    @Regex(order = 36, pattern = "^[\\p{L} .'-]+$", messageResId = R.string.error_wrong_color)
    private EditText stockOtherColor;
    @Required(order = 9, messageResId = R.string.error_stock_year_required)
    @TextRule(order = 10, minLength = 1, messageResId = R.string.error_stock_year_required)
    private EditText stockYear;
    private EditText insuranceValidYear, insuranceValidMonth;
    @Required(order = 36, messageResId = R.string.error_stock_month_required)
    @TextRule(order = 37, minLength = 1, messageResId = R.string.error_stock_month_required)
    private TextView stMonth;
    private String mStockDates = "", mInsuranceDates = "", mTaxGroup = "Tax", mInsuranceGroup = "Insurance";
    @Required(order = 11, messageResId = R.string.error_kms_driven_rquired)
    @TextRule(order = 12, minLength = 3, messageResId = R.string.error_kms_driven_rquired)
    @FirstCharValidation(order = 13, notAllowedFirstChars = "0", message = "Please remove leading zeroes.")
    private EditText kmsDriven;
    @Required(order = 14, messageResId = R.string.error_num_owners_required)
    @TextRule(order = 15, minLength = 1, messageResId = R.string.error_num_owners_required)
    private EditText numOwners;
    @Required(order = 16, messageResId = R.string.error_registration_city_required)
    @TextRule(order = 17, minLength = 1, messageResId = R.string.error_registration_city_required)
    private CustomAutoCompleteTextView registrationCity;
    @Required(order = 18, messageResId = R.string.error_registration_number_required)
    @TextRule(order = 19, minLength = 2, messageResId = R.string.error_registration_number_required)
    private EditText registrationNumber;
    @Required(order = 20, messageResId = R.string.error_dealership_name_required)
    @TextRule(order = 21, minLength = 1, messageResId = R.string.error_dealership_name_required)
    private EditText dealershipName;
    @Required(order = 22, messageResId = R.string.error_stock_price_required)
    @TextRule(order = 23, minLength = 4, messageResId = R.string.error_invalid_stock_price)
    @FirstCharValidation(order = 24, notAllowedFirstChars = "0", message = "Please remove leading zeroes.")
    private EditText stockPrice;

    private boolean flag = true;

    //    @Required(order = 29, messageResId = R.string.error_dealer_mobile_required)
//    @TextRule(order = 30, minLength = 10, messageResId = R.string.error_incorrect_dealer_mobile)
//    @FirstCharValidation(order = 31, notAllowedFirstChars = "0123456", message = "Please enter a valid dealer mobile number")
//    private EditText dealerMobile;
    private boolean isInsuranceDateClicked = true, isStockDatePickerCLicked = true, isClearMake = false;
    @RadioGrp(order = 25, messageResId = R.string.error_insurance_type_required)
    private RadioGroup insuranceType;
    @Required(order = 26, messageResId = R.string.error_insurance_validity_required)
    @TextRule(order = 27, minLength = 1, messageResId = R.string.error_insurance_validity_required)
    private TextView insuranceValidity;
    @RadioGrp(order = 28, messageResId = R.string.error_tax_type_required)
    private RadioGroup taxType;
    @Required(order = 32, messageResId = R.string.error_dealer_price_required)
    @TextRule(order = 33, minLength = 4, messageResId = R.string.error_invalid_dealer_price)
    @FirstCharValidation(order = 34, notAllowedFirstChars = "0", message = "Please remove leading zeroes.")
    private EditText dealerPrice;
    private  RadioGroup evenOddRadio;
    private MakeModelVersionAdapter makeModelVersionAdapter;
    private VersionObject stock;
    private BasicListItemAdapter numOwnerAdapter;
    private ColorAdapter colorAdapter;
    private DatePickerDialog datePickerDialog;
    private String modelNameBlank = "";
    private TextView tvStockPrice,tvDealerPrice;

    //    private GAHelper mGAHelper;
    private ModelVersionAdapter modelVersionAdapter;
    private CityAdapter cityAdapter;
    private BasicListItemModel selectedNumOwners, selectedColor;
    private CheckBox cngFitted, sellToDealer;
    private TextView fuelType;
    private LinearLayout fuelTypeLayout;
    private LinearLayout uploadOnDealerLayout;
    private RelativeLayout rlPhotosAdded;
    private ArrayList<BasicListItemModel> numOwnersList = new ArrayList<BasicListItemModel>();
    private ArrayList<ShowroomData> showroomList;
    private ArrayList<BasicListItemModel> colorsList = new ArrayList<BasicListItemModel>();
    private ArrayList<CityData> cityList;
    //private ArrayList<ShowroomData> showroomList;
    private ShowroomData selectedShowroom;
    private CityData selectedCity;
    private DealershipAdapter dealershipAdapter;
    private GCProgressDialog progressDialog;
    private ArrayList<FileInfo> imagesPathList;
    private ArrayList<FileInfo> deletedList;
    private LinearLayout footerLayout;
    private ScrollView scrollView;
    private int stYear, stockMonth, stockDay, insuranceYear, insuranceMonth, insuranceDay;
    private  char numberLast;
    private String stockId;
    private String comingFrom = "";
    private Integer[] monthList, yearList, insuranceYearList, insuranceMonthList;
    private String registNo;
    private int selected = -1;
    private LinearLayout radioGroupLayout;
    private FilterQueryProvider cityFilterQueryProvider = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getCityCursor(constraint);
        }
    };
    private FilterQueryProvider mmFilterQueryProvider = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getCursor(constraint);
        }
    };

    public static void sendOrderImages(final Context context, String stockId) {
        ImageOrdersThread thread = new ImageOrdersThread(context, stockId);
        thread.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (comingFrom.equals(""))
            saveStocksData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_ADD_STOCK);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.CITY_LIST, cityList);
        outState.putSerializable(Constants.SHOWROOMS_LIST, showroomList);
        outState.putSerializable(Constants.SELECTED_SHOWROOM, selectedShowroom);
        outState.putSerializable(Constants.COLORS_LIST, colorsList);
        outState.putSerializable(Constants.SELECTED_STOCK, stock);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cityList = (ArrayList<CityData>) savedInstanceState.getSerializable(Constants.CITY_LIST);
        showroomList = (ArrayList<ShowroomData>) savedInstanceState.getSerializable(Constants.SHOWROOMS_LIST);
        selectedShowroom = (ShowroomData) savedInstanceState.getSerializable(Constants.SELECTED_SHOWROOM);
        colorsList = (ArrayList<BasicListItemModel>) savedInstanceState.getSerializable(Constants.COLORS_LIST);
        stock = (VersionObject) savedInstanceState.getSerializable(Constants.SELECTED_STOCK);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog!= null && progressDialog.isShowing())
            progressDialog.dismiss();

        // clearing selected files when activity is destroyed.
        try {
            if (com.imageuploadlib.Utils.ApplicationController.selectedFiles != null) {
                com.imageuploadlib.Utils.ApplicationController.selectedFiles.clear();
            }

            if (imagesList != null)
                imagesList.clear();

        } catch (Exception e) {
            Crashlytics.log(Log.ERROR, Constants.TAG, "exception while clearing selected files list");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GCLog.e("requestCode: " + requestCode);
        if (requestCode == REQUEST_CODE_UPLOAD_PHOTOS) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null) {

                    imagesPathList = (ArrayList<FileInfo>) data
                            .getSerializableExtra(PhotoUpload.KEY_ARRAYLIST_IMAGES);

                    deletedList = (ArrayList<FileInfo>) data.getSerializableExtra(PhotoUpload.KEY_ARRAYLIST_DELETED_IMAGES);

                    if (imagesPathList == null) {
                        return;
                    }
                    int size = imagesPathList.size();
                    if (size > 0) {
                        findViewById(R.id.photosAddedContainer).setVisibility(
                                View.VISIBLE);
                        ((TextView) findViewById(R.id.photoCount))
                                .setText(String.valueOf(size));

                        FileInfo photoInfo = imagesPathList.get(0);
                        ImageView seletedPhotos = (ImageView) findViewById(R.id.selectedImage);

                        GCLog.e(photoInfo.getFilePath());
                        if (photoInfo.getFilePath().startsWith("https://") || photoInfo.getFilePath().startsWith("http://")) {

                            Glide.with(this)
                                    .load(photoInfo.getFilePath())
                                    .placeholder(R.drawable.default_placeholder)
                                    .crossFade()
                                    .into(seletedPhotos);

                        } else {
                            Glide.with(this)
                                    .load("file://" + photoInfo.getFilePath())
                                    .placeholder(R.drawable.default_placeholder)
                                    .crossFade()
                                    .into(seletedPhotos);
                        }

                    } else {
                        findViewById(R.id.photosAddedContainer).setVisibility(
                                View.INVISIBLE);
                    }
                }
            } else if (requestCode == Constants.STOCK_ADD_ADDITIONAL) {
                finish();
            }

        }
    }

    private Cursor getCityCursor(CharSequence constraint) {
        return ApplicationController.getMakeModelVersionDB().getCityRecords(constraint);
    }

    private Cursor getCursor(CharSequence constraint) {
        Cursor cursor;
        MakeModelVersionDB db = ApplicationController.getMakeModelVersionDB();
        cursor = db.getModelRecords(constraint, false);
        return cursor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_add_stock, frameLayout);//setContentView(R.layout.activity_add_stock);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mGAHelper = new GAHelper(this);
        progressDialog = new GCProgressDialog(this, this, getString(R.string.please_wait));
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
        // mActionBar = getSupportActionBar();
        //mActionBar.setDisplayHomeAsUpEnabled(true);

        makeModelVersionAdapter = new MakeModelVersionAdapter(this, null);
        mMakeModel = (CustomMaterialAutoCompleteTxtVw) findViewById(R.id.makeModel);
        mMakeModel.setType(MakeModelType.MAKE);
        mMakeModel.setThreshold(1);
        mMakeModel.setAdapter(makeModelVersionAdapter);
        makeModelVersionAdapter.setFilterQueryProvider(mmFilterQueryProvider);
        mMakeModel.setOnItemClickListener(this);
        mMakeModel.preventFocusReverseAnimation = true;

        insuranceYear = Calendar.getInstance().get(Calendar.YEAR);
        insuranceMonth = Calendar.getInstance().get(Calendar.MONTH);
        stYear = Calendar.getInstance().get(Calendar.YEAR);
        stockMonth = Calendar.getInstance().get(Calendar.MONTH);

        insuranceDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        stockDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        mModelVersion = (EditText) findViewById(R.id.version);
        mModelVersion.setClickable(false);
        mModelVersion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mModelVersion.setError(null);
            }
        });

        TextView addPhotoWidget = (TextView) findViewById(R.id.addPhotoWidget);
        addPhotoWidget.setOnClickListener(this);


        kmsDriven = (EditText) findViewById(R.id.kmsDriven);
        kmsDriven.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                insertCommaIntoNumber(kmsDriven, s, "##,##,###");

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /*kmsDriven.setOnClickListener(this);*/

        numOwners = (EditText) findViewById(R.id.numOwners);
        numOwners.setOnClickListener(this);

        dealershipName = (EditText) findViewById(R.id.dealershipName);

        registrationCity = (CustomAutoCompleteTextView) findViewById(R.id.registrationCity);

        //registrationCity.setOnClickListener(this);
        registrationNumber = (EditText) findViewById(R.id.registrationNumber);
        registrationNumber.setOnFocusChangeListener(new RemoveErrorOnFocus());

        stockYear = (EditText) findViewById(R.id.stockYear);
        /*stockYear.setText(ApplicationController.monthShortMap.get(stockMonth + "") + "/" + stYear);*/
        if (Calendar.getInstance().get(Calendar.MONTH) == 0) {
            stockYear.setText(ApplicationController.monthShortMap.get(12 + "") + "/" + (Calendar.getInstance().get(Calendar.YEAR) - 1));
        } else {
            stockYear.setText(ApplicationController.monthShortMap.get(Calendar.getInstance().get(Calendar.MONTH) + "") + "/" + Calendar.getInstance().get(Calendar.YEAR));
        }
        stockYear.setOnClickListener(this);
        color = (ImageView) findViewById(R.id.Color);
        /*stMonth = (EditText) findViewById(R.id.stockMonth);
        stMonth.setOnClickListener(this);*/
        insuranceValidYear = (EditText) findViewById(R.id.insuranceValidYear);
        insuranceValidYear.setText(ApplicationController.monthShortMap.get(String.valueOf(insuranceMonth + 1)) + "/" + insuranceYear);
        insuranceValidYear.setOnClickListener(this);
        /*insuranceValidMonth = (EditText) findViewById(R.id.insuranceValidMonth);
        insuranceValidMonth.setOnClickListener(this);
        createYearList();
        createInsuranceYearList();*/

        color = (ImageView) findViewById(R.id.Color);
        stockColor = (EditText) findViewById(R.id.stockColor);
        stockcolorlayout = (LinearLayout) findViewById(R.id.stockcolorlayout);
        //stockColor.setEnabled(false);
        stockcolorlayout.setOnClickListener(this);


        stockOtherColor = (EditText) findViewById(R.id.stockOtherColor);

        /*stockColor.setEnabled(false);
        stockColor.setClickable(false);*/
        //stockColor.setOnClickListener(this);
        stockPrice = (EditText) findViewById(R.id.stockPrice);
        tvStockPrice = (TextView) findViewById(R.id.tvStockPrice);
        tvStockPrice.setVisibility(View.INVISIBLE);
        stockPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (stockPrice.getText().toString() == "") {
                    tvStockPrice.setVisibility(View.INVISIBLE);
                    stockPrice.setHint("");
                } else {
                    tvStockPrice.setVisibility(View.VISIBLE);
                    //stockPrice.setHint("StockPrice*");
                }

                insertCommaIntoNumber(stockPrice, s, "#,##,##,###");
            }

            @Override
            public void afterTextChanged(Editable s) {
                stockPrice.setHint("");
               // tvStockPrice.setVisibility(View.INVISIBLE);
            }
        });
        //stockPrice.setOnFocusChangeListener(new RemoveErrorOnFocus());
        stockPrice.setFocusableInTouchMode(true);
        stockPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && ((EditText) v).getText().toString().isEmpty()) {
                    tvStockPrice.setVisibility(View.INVISIBLE);
                    stockPrice.setHint(R.string.enter_price);
                }

            }
        });


        fuelTypeLayout = (LinearLayout) findViewById(R.id.fuelTypeLayout);
        fuelType = (TextView) findViewById(R.id.fuelType);
        cngFitted = (CheckBox) findViewById(R.id.cngFitted);

        insuranceType = (RadioGroup) findViewById(R.id.insuranceType);
        insuranceType.setOnCheckedChangeListener(this);

        sellToDealer = (CheckBox) findViewById(R.id.selltodealer);
        sellToDealer.setOnCheckedChangeListener(this);

        uploadOnDealerLayout = (LinearLayout) findViewById(R.id.dealerInfoLayout);
        //insuranceValidity = (TextView) findViewById(R.id.insuranceValidity);
        //insuranceValidity.setOnClickListener(this);
        taxType = (RadioGroup) findViewById(R.id.taxType);
        taxType.setOnCheckedChangeListener(this);

        radioGroupLayout = (LinearLayout) findViewById(R.id.radioGroupLayout);

        evenOddRadio = (RadioGroup) findViewById(R.id.evenOdd);
        evenOddRadio.setOnCheckedChangeListener(this);

        registrationNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                registNo = registrationNumber.getText().toString().trim();
                findViewById(R.id.even).setEnabled(true);
                findViewById(R.id.odd).setEnabled(true);
                if (!registNo.equals("") && s.length() >= 7) {
                    if(selected != -1 && flag) {
                        //evenOddRadio.check(selected);
                        if (selected == 1) {
                            ((RadioButton) findViewById(R.id.odd)).setChecked(true);
                        } else if(selected == 2) {
                            ((RadioButton) findViewById(R.id.even)).setChecked(true);
                        }
                        flag = false;
                        return;
                    }
                    numberLast = registNo.charAt(s.length() - 1);
                    if(Character.isDigit(numberLast)) {
                        if (numberLast % 2 == 0) {
                            ((RadioButton) findViewById(R.id.even)).setChecked(true);
                            findViewById(R.id.odd).setEnabled(false);
                        } else {
                            ((RadioButton) findViewById(R.id.odd)).setChecked(true);
                            findViewById(R.id.even).setEnabled(false);
                        }
                    }

                }
            }
        });
//        dealerMobile = (EditText) findViewById(R.id.dealerMobile);
//        dealerMobile.setText(CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_MOBILE, ""));
        dealerPrice = (EditText) findViewById(R.id.dealerPrice);
        tvDealerPrice = (TextView) findViewById(R.id.tvDealerPrice);
        tvDealerPrice.setVisibility(View.INVISIBLE);
        dealerPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (dealerPrice.getText().toString() == "") {
                    tvDealerPrice.setVisibility(View.INVISIBLE);
                    dealerPrice.setHint("");
                } else {
                    tvDealerPrice.setVisibility(View.VISIBLE);
                   // dealerPrice.setHint(R.string.hint_dealer_price);
                }
                insertCommaIntoNumber(dealerPrice, s, "#,##,##,###");
            }

            @Override
            public void afterTextChanged(Editable s) {
                dealerPrice.setHint("");

            }
        });

        dealerPrice.setFocusableInTouchMode(true);
        dealerPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && ((EditText) v).getText().toString().isEmpty()) {
                    tvDealerPrice.setVisibility(View.INVISIBLE);
                    dealerPrice.setHint(R.string.hint_dealer_price);
                }

            }
        });

        footerLayout = (LinearLayout) findViewById(R.id.footerLayout);
        footerLayout.setOnClickListener(this);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //GCLog.e("event: " + event.toString());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        //GCLog.e("Scroll event.");
                        /*InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);*/
                        // Changed the code to prevent FABRIC issue 806
                        CommonUtils.hideKeyboard(StockAddActivity.this, scrollView);
                        return false;
                }
                return false;
            }
        });

        formLists();
        ((RadioButton) findViewById(R.id.noInsurance)).setChecked(true);
        ((RadioButton) findViewById(R.id.individual)).setChecked(true);
        numOwners.setText(numOwnerAdapter.getItem(0).getValue());
        selectedNumOwners = new BasicListItemModel(numOwnerAdapter.getItem(0).getId(), numOwnerAdapter.getItem(0).getValue());

        cityAdapter = new CityAdapter(StockAddActivity.this, null);
        registrationCity.setAdapter(cityAdapter);
        registrationCity.setThreshold(1);
        registrationCity.setOnItemClickListener(StockAddActivity.this);
        cityAdapter.setFilterQueryProvider(cityFilterQueryProvider);
        selectedCity = new CityData();
        selectedCity.setCityName(CommonUtils.getStringSharedPreference(StockAddActivity.this, Constants.UC_DEALER_CITY, ""));
        selectedCity.setCityId(CommonUtils.getStringSharedPreference(StockAddActivity.this, Constants.UC_DEALER_CITY, ""));
        registrationCity.setText(selectedCity.getCityName());
        if (savedInstanceState != null) {
            cityList = (ArrayList<CityData>) savedInstanceState.getSerializable(Constants.CITY_LIST);
            showroomList = (ArrayList<ShowroomData>) savedInstanceState.getSerializable(Constants.SHOWROOMS_LIST);
        } else {
            // makeShowroomsRequest(false);

        }
        GradientDrawable bgShape = (GradientDrawable) color.getBackground();
        bgShape.setColor(Color.parseColor("#ffffff"));
        formShowroomList();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(Constants.COMING_FROM)) {
            //boolean canEdit = bundle.getBoolean(Constants.CAN_EDIT);
            if ("SV".equalsIgnoreCase(bundle.getString(Constants.COMING_FROM))) {
                comingFrom = "SV";
                stockId = bundle.getString(Constants.STOCK_ID);
                setTitleMsg("Edit Stock");
                if(bundle.getBoolean("fromRemoved"))
                {
                    ((TextView) findViewById(R.id.addGaadi)).setText("Update and Add to Stock");
                }
                else {
                    ((TextView) findViewById(R.id.addGaadi)).setText("Update Stock");
                }
                mMakeModel.setEnabled(false);
                mMakeModel.setClickable(false);
                stockYear.setEnabled(false);
                stockYear.setClickable(false);
              /*  stMonth.setEnabled(false);
                stMonth.setClickable(false);*/

                String ownerNumber = bundle.getString(Constants.NUM_OWNERS);
                String ownerNumberText = ApplicationController.numOwnersMap.get(ownerNumber);
                selectedNumOwners = new BasicListItemModel(ownerNumber, ownerNumberText);
                numOwners.setText(ownerNumberText);

                if (selectedNumOwners.getId().equals("0")) {
                    hideFieldsforUnregisteredCase();

                } else {
                    showFieldsForRegisteredCase();

                    String city = bundle.getString(Constants.CITY_NAME);
                    registrationCity.setText(city);
                    registrationCity.setSelection(city.length());
                    selectedCity = new CityData();
                    selectedCity.setCityName(city);
                    selectedCity.setCityId(city);

                    String regno = bundle.getString(Constants.REGISTRATION_NUMBER);
                    registrationNumber.setText(regno.toUpperCase(Locale.ENGLISH));

                }

                selected = bundle.getInt(Constants.Even_Odd);
                if(selected==1) {
                    ((RadioButton) findViewById(R.id.odd)).setChecked(true);
                } else if(selected == 2) {
                    ((RadioButton) findViewById(R.id.even)).setChecked(true);
                }

                /*insuranceValidYear.setEnabled(false);
                insuranceValidYear.setClickable(false);
                insuranceValidMonth.setEnabled(false);
                insuranceValidMonth.setClickable(false);*/


                String insuranceType = bundle.getString(Constants.INSURANCE_TYPE);
                if ("No Insurance".toLowerCase().equalsIgnoreCase(insuranceType)) {
                    ((RadioButton) findViewById(R.id.noInsurance)).setChecked(true);

                } else if ("Comprehensive".toLowerCase().equalsIgnoreCase(insuranceType)) {
                        insuranceMonth = Integer.parseInt(bundle.getString(Constants.INSURANCE_MONTH));
                    insuranceYear = Integer.parseInt(bundle.getString(Constants.INSURANCE_YEAR));
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    if (currentYear > insuranceYear) {
                        ((RadioButton) findViewById(R.id.noInsurance)).setChecked(true);

                    } else {
                        ((RadioButton) findViewById(R.id.comprehensive)).setChecked(true);
                        insuranceValidYear.setText(ApplicationController.monthShortMap.get(String.valueOf(insuranceMonth)) + "/" + insuranceYear);
                        /*resetInsuranceMonthList(insuranceYear);
                        insuranceValidMonth.setText(ApplicationController.monthShortMap.get(String.valueOf(insuranceMonth)));
                        insuranceValidity.setText(ApplicationController.monthShortMap.get(String.valueOf(insuranceMonth))
                                + "/" + bundle.getString(Constants.INSURANCE_YEAR));*/

                    }

                } else if ("Third Party".toLowerCase().equalsIgnoreCase(insuranceType)) {

                    //insuranceMonth = (bundle.getString(Constants.INSURANCE_MONTH).trim().isEmpty()) ? 1 : Integer.parseInt(bundle.getString(Constants.INSURANCE_MONTH));
                    insuranceYear = Integer.parseInt(bundle.getString(Constants.INSURANCE_YEAR));
                    insuranceMonth = Integer.parseInt(bundle.getString(Constants.INSURANCE_MONTH));
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    if (currentYear > insuranceYear) {
                        ((RadioButton) findViewById(R.id.noInsurance)).setChecked(true);

                    } else {
                        ((RadioButton) findViewById(R.id.thirdparty)).setChecked(true);
                        insuranceValidYear.setText(ApplicationController.monthShortMap.get(String.valueOf(insuranceMonth)) + "/" + insuranceYear);
                        //resetInsuranceMonthList(insuranceYear);
                        //insuranceValidMonth.setText(ApplicationController.monthShortMap.get(String.valueOf(insuranceMonth)));

                        /*insuranceValidity.setText(ApplicationController.monthShortMap.get(String.valueOf(insuranceMonth))
                                + "/" + bundle.getString(Constants.INSURANCE_YEAR));*/

                    }

                }

                String taxType = bundle.getString(Constants.TAX_TYPE);
                if ("Individual".toLowerCase().equalsIgnoreCase(taxType)) {
                    ((RadioButton) findViewById(R.id.individual)).setChecked(true);

                } else if ("Corporate".toLowerCase().equalsIgnoreCase(taxType)) {
                    ((RadioButton) findViewById(R.id.corporate)).setChecked(true);

                }

                String sellToDealer = bundle.getString(Constants.SELL_TO_DEALER);
                if ("0".equals(sellToDealer)) {
                    this.sellToDealer.setChecked(false);

                } else if ("1".equals(sellToDealer)) {
                    this.sellToDealer.setChecked(true);
                    String dealerPrice = bundle.getString(Constants.DEALER_PRICE);
                    this.dealerPrice.setText(dealerPrice);

                }

                stockMonth = Integer.parseInt(bundle.getString(Constants.STOCK_MONTH));
                stYear = Integer.parseInt(bundle.getString(Constants.STOCK_YEAR));
                /*String displayYear = bundle.getString(Constants.STOCK_YEAR);

                String displayMonth = ApplicationController.monthShortMap.get(String.valueOf(stockMonth));*/
                stockYear.setText(ApplicationController.monthShortMap.get(String.valueOf(stockMonth)) + "/" + String.valueOf(stYear));
                /*stMonth.setText(ApplicationController.monthShortMap.get(String.valueOf(stockMonth)));*/


                imagesList = bundle.getStringArrayList(Constants.STOCK_IMAGES);

                int imagesCount = (imagesList != null) ? imagesList.size() : 0;
                if (imagesCount > 0) {
//                    for(int i = 0; i<imagesList.size();i++)
//                    {
//                        Log.e(TAG ,"Image Path : "+ imagesList.get(i));
//                    }
                    rlPhotosAdded = (RelativeLayout) findViewById(R.id.photosAddedContainer);
                    rlPhotosAdded.setVisibility(View.VISIBLE);
                    GCLog.e("" + imagesCount);
                    String url = imagesList.get(0);
                    TextView tvPhotoCount = (TextView) findViewById(R.id.photoCount);
                    tvPhotoCount.setText(imagesList.size() + "");
                    tvPhotoCount.setVisibility(View.VISIBLE);

                    rlPhotosAdded.setOnClickListener(this);
//                PhotoInfo photoInfo = imagesPathList.get(imagesCount - 1);
                    ImageView seletedPhotos = (ImageView) findViewById(R.id.selectedImage);


                    GCLog.e(url);
                    Glide.with(this)
                            .load(url)
                            .placeholder(R.drawable.default_placeholder)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .crossFade()
                            .into(seletedPhotos);

                }

                String make = bundle.getString(Constants.MAKE).trim();
                int makeId = bundle.getInt(Constants.MAKE_ID);
                String model = bundle.getString(Constants.MODEL).trim();
                String version = bundle.getString(Constants.VERSION).trim();
                String makeModelVersion = make + " " + model + " " + version;
                String hexCode = bundle.getString(Constants.HEXCODE);
                if (hexCode != null && !hexCode.equals("")) {
                    //GradientDrawable bcgShape = (GradientDrawable) stockColor.getBackground();
                    bgShape.setColor(Color.parseColor(hexCode));
                } else {
                    //GradientDrawable bcgShape = (GradientDrawable) stockColor.getBackground();
                    bgShape.setColor(Color.parseColor("#ffffff"));
                }
                String cngFitted = bundle.getString(Constants.CNG_FITTED);

                try {
                    Cursor cursor = ApplicationController.getMakeModelVersionDB().getMakeModelVersionRecords(makeModelVersion);
                    //GCLog.e("make id: " + cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MAKEID)));
                    stock = new VersionObject();
                    if (cursor != null) {
                        stock.setMakeId(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MAKEID)));
                        stock.setMake(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MAKENAME)));
                        stock.setModelId(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MODELID)));
                        stock.setModel(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MODELNAME)));
                        stock.setVersionid(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.VERSIONID)));
                        stock.setVersionName(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.VERSIONNAME)));
                        stock.setFuelType(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.FUEL_TYPE)));
                        stock.setTransmission(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.TRANSMISSION_TYPE)));
                        stock.setMakeModel(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MMV_COLUMN_MAKE_MODEL)));
                        stock.setModelVersion(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MMV_COLUMN_MODEL_VERSION)));
                        String fuelType = stock.getFuelType();
                        if ((fuelType != null) && !fuelType.isEmpty()) {
                            fuelTypeLayout.setVisibility(View.VISIBLE);

                            this.fuelType.setText("Fuel Type: " + fuelType);

                            if ("PETROL".equalsIgnoreCase(fuelType)) {
                                this.cngFitted.setVisibility(View.VISIBLE);
                                if ("1".equals(cngFitted)) {
                                    this.cngFitted.setChecked(true);
                                } else {
                                    this.cngFitted.setChecked(false);
                                }

                            } else {
                                this.cngFitted.setVisibility(View.GONE);
                            }
                        } else {
                            fuelTypeLayout.setVisibility(View.GONE);
                        }

                        cursor.close();
                    } else {
                        //GCLog.e("Some problem in fetching make model version data from local db. please sync it.");
                        stock.setMake(make);
                        stock.setMakeId(String.valueOf(makeId));
                        stock.setModel(model);
                        stock.setVersionName(version);
                    }

                    mMakeModel.setText(stock.getModel());
                    modelNameBlank = stock.getModel();
                    mMakeModel.setCompoundDrawablesWithIntrinsicBounds(ApplicationController.makeLogoMap.get(Integer.parseInt(stock.getMakeId())), 0, 0, 0);

                    mModelVersion.setText(stock.getVersionName());
                    mModelVersion.setEnabled(true);
                    mModelVersion.setOnClickListener(this);

                    makeVariantColorsRequest(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String  stockPrice = bundle.getString(Constants.STOCK_PRICE);
                this.stockPrice.setText(stockPrice);
                String kmsDriven = bundle.getString(Constants.KMS_DRIVEN);
                this.kmsDriven.setText(kmsDriven);

                String stockColor = bundle.getString(Constants.STOCK_COLOR);
                this.stockColor.setText(stockColor);
                selectedColor = new BasicListItemModel(stockColor, stockColor);

                String showroomName = bundle.getString(Constants.SHOWROOM_NAME);
                dealershipName.setText(showroomName);
                String showroomid = bundle.getString(Constants.SHOWROOM_ID);

                selectedShowroom = new ShowroomData();
                selectedShowroom.setShowroomId(showroomid);
                selectedShowroom.setShowroomName(showroomName);

            }
        } else {
            if (comingFrom.equals("") && ApplicationController.getStocksDB().getStocksCount() > 0) {

                setSavedStockData();
            }

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

    private void createInsuranceMonthList(int firstMonth) {
        insuranceMonthList = new Integer[12 - firstMonth];
        for (int i = firstMonth; i < 12; i++) {
            insuranceMonthList[i - firstMonth] = i + 1;
        }
    }

   /* public void createInsuranceYearList() {
        insuranceYearList = new Integer[2];
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear; i < currentYear + 2; i++)
            insuranceYearList[i - currentYear] = i;
        createInsuranceMonthList(Calendar.getInstance().get(Calendar.MONTH));
        insuranceValidYear.setText(insuranceYearList[0].toString());
        insuranceValidMonth.setText(ApplicationController.monthShortMap.get(String.valueOf(insuranceMonthList[0])));
    }

    public void resetInsuranceMonthList(int year) {
        if (year == Calendar.getInstance().get(Calendar.YEAR)) {
            createInsuranceMonthList(Calendar.getInstance().get(Calendar.MONTH));
        } else {
            createInsuranceMonthList(0);
        }
    }*/

   /* private void createMonthList(int lastMonth) {
        if (lastMonth == 0) {
            lastMonth = 1;
        } else if (lastMonth == 11) {
            lastMonth = 12;
        }
        monthList = new Integer[lastMonth];
        for (int i = 1; i <= lastMonth; i++) {
            monthList[i - 1] = i;
        }
    }*/

    /*public void createYearList() {
        yearList = new Integer[20];
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        if (currentMonth == 0) {
            currentYear--;
        }
        stYear = currentYear;
        for (int i = currentYear; i > currentYear - 20; i--)
            yearList[currentYear - i] = i;
        createMonthList(currentMonth == 0 ? 12 : currentMonth);
      //  stockYear.setText(yearList[0].toString());
        //  stockMonth = monthList[monthList.length - 1];
        //stMonth.setText(ApplicationController.monthShortMap.get(String.valueOf(monthList[monthList.length - 1])));
    }*/

    public void setSavedStockData() {
        //make version clickable if make is present


        StockDataModel mStockDataModel = ApplicationController.getStocksDB().getStocksData();

        mMakeModel.setText(mStockDataModel.getModel());
        modelNameBlank = mStockDataModel.getModel();
        if (!mStockDataModel.getMake().equals(""))
            mMakeModel.setCompoundDrawablesWithIntrinsicBounds(ApplicationController.makeLogoMap.get(Integer.parseInt(mStockDataModel.getMake())), 0, 0, 0);
        mMakeModel.handleClearButton();
        mModelVersion.setText(mStockDataModel.getVersion());
        mModelVersion.setClickable(true);
        mModelVersion.setEnabled(true);
        mModelVersion.setOnClickListener(this);
        String fuelType = mStockDataModel.getFuelType();
        if ((fuelType != null) && !fuelType.isEmpty()) {
            fuelTypeLayout.setVisibility(View.VISIBLE);

            this.fuelType.setText("Fuel Type: " + fuelType);

            if ("PETROL".equalsIgnoreCase(fuelType)) {
                this.cngFitted.setVisibility(View.VISIBLE);
                if ("1".equals(mStockDataModel.getIsCng())) {
                    this.cngFitted.setChecked(true);
                } else {
                    this.cngFitted.setChecked(false);
                }

            } else {
                this.cngFitted.setVisibility(View.GONE);
            }
        } else {
            fuelTypeLayout.setVisibility(View.GONE);
        }

        if (!mStockDataModel.getMake().equals("") && !mStockDataModel.getVersion().equals("") && !mStockDataModel.getModel().equals("")) {
            makeVariantColorsRequest(true);

        }

        this.stockColor.setText(mStockDataModel.getColor());
        selectedColor = new BasicListItemModel(mStockDataModel.getHexcode(), mStockDataModel.getColor());
        if (mStockDataModel.getHexcode() != null) {
            if (!mStockDataModel.getHexcode().equals("") && !mStockDataModel.getHexcode().trim().equalsIgnoreCase("Other")) {
                GradientDrawable bgShape = (GradientDrawable) color.getBackground();
                bgShape.setColor(Color.parseColor(mStockDataModel.getHexcode()));
            } else {
                GradientDrawable bgShape = (GradientDrawable) color.getBackground();
                bgShape.setColor(Color.parseColor("#ffffff"));
            }
        }
        String kmsDriven = mStockDataModel.getKm();
        this.kmsDriven.setText(kmsDriven);
        String ownerNumber = mStockDataModel.getOwners();
        String ownerNumberText = ApplicationController.numOwnersMap.get(ownerNumber);
        selectedNumOwners = new BasicListItemModel(ownerNumber, ownerNumberText);
        numOwners.setText(ownerNumberText);

        if (selectedNumOwners.getId().equals("0")) {
            hideFieldsforUnregisteredCase();

        } else {
            showFieldsForRegisteredCase();

            String city = mStockDataModel.getReg_city();
            if (city.trim().equals("")) {
                registrationCity.setText(CommonUtils.getStringSharedPreference(StockAddActivity.this, Constants.UC_DEALER_CITY, ""));
                selectedCity = new CityData();
                selectedCity.setCityName(CommonUtils.getStringSharedPreference(StockAddActivity.this, Constants.UC_DEALER_CITY, ""));
                selectedCity.setCityId(CommonUtils.getStringSharedPreference(StockAddActivity.this, Constants.UC_DEALER_CITY, ""));
            } else {
                registrationCity.setText(city);
                selectedCity = new CityData();
                selectedCity.setCityName(city);
                selectedCity.setCityId(city);
            }
            registrationCity.setSelection(city.length());


            String regno = mStockDataModel.getReg_no();
            registrationNumber.setText(regno.toUpperCase(Locale.ENGLISH));

            if (mStockDataModel.getEvenOdd().equals("Even")) {
                ((RadioButton) findViewById(R.id.even)).setChecked(true);
            } else if (mStockDataModel.getEvenOdd().equals("Odd")) {
                ((RadioButton) findViewById(R.id.odd)).setChecked(true);
            }

        }
        if (!mStockDataModel.getDealership().equals("")) {

            if (Constants.isDealerChanged) {
                formShowroomList();
                Constants.isDealerChanged = false;
            } else {
                String mDealerShip[] = mStockDataModel.getDealership().split("~");
                String showroomid = mDealerShip[0];
                String showroomName = mDealerShip[1];
                dealershipName.setText(showroomName);
                selectedShowroom = new ShowroomData();
                selectedShowroom.setShowroomId(showroomid);
                selectedShowroom.setShowroomName(showroomName);
            }

        }
        String stockPrice = mStockDataModel.getStockprice();
        this.stockPrice.setText(stockPrice);
        String insuranceType = mStockDataModel.getInsurancetype();
        if ("No Insurance".toLowerCase().equalsIgnoreCase(insuranceType)) {
            ((RadioButton) findViewById(R.id.noInsurance)).setChecked(true);

        } else if ("Comprehensive".toLowerCase().equalsIgnoreCase(insuranceType)) {

            if (!mStockDataModel.getInsuranceMonth().equals("") && !mStockDataModel.getInsuranceYear().equals("")) {
                insuranceMonth = Integer.parseInt(mStockDataModel.getInsuranceMonth());

                insuranceYear = Integer.parseInt(mStockDataModel.getInsuranceYear());
            }
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            if (currentYear > insuranceYear) {
                ((RadioButton) findViewById(R.id.noInsurance)).setChecked(true);

            } else {
                ((RadioButton) findViewById(R.id.comprehensive)).setChecked(true);
                if (!mStockDataModel.getInsuranceMonth().equals("") || !mStockDataModel.getInsuranceYear().equals("")) {
                    insuranceValidYear.setText(ApplicationController.monthShortMap.get(String.valueOf(insuranceMonth)) + "/" + insuranceYear);
                    // resetInsuranceMonthList(insuranceYear);
                    //insuranceValidMonth.setText(ApplicationController.monthShortMap.get(insuranceMonth));

                }

            }
        } else if ("Third Party".toLowerCase().equalsIgnoreCase(insuranceType)) {

            if (!mStockDataModel.getInsuranceMonth().equals("") || !mStockDataModel.getInsuranceYear().equals("")) {
                insuranceMonth = Integer.parseInt(mStockDataModel.getInsuranceMonth());
                insuranceYear = Integer.parseInt(mStockDataModel.getInsuranceYear());
            }
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            if (currentYear > insuranceYear) {
                ((RadioButton) findViewById(R.id.noInsurance)).setChecked(true);

            } else {
                ((RadioButton) findViewById(R.id.thirdparty)).setChecked(true);
                if (!mStockDataModel.getInsuranceMonth().equals("") || !mStockDataModel.getInsuranceYear().equals("")) {
                    insuranceValidYear.setText(ApplicationController.monthShortMap.get(String.valueOf(insuranceMonth)) + "/" + insuranceYear);
                    //resetInsuranceMonthList(insuranceYear);
                    //insuranceValidMonth.setText(ApplicationController.monthShortMap.get(String.valueOf(insuranceMonth)));

                }

            }

        }

        String taxType = mStockDataModel.getTax();
        if ("Individual".toLowerCase().equalsIgnoreCase(taxType)) {
            ((RadioButton) findViewById(R.id.individual)).setChecked(true);

        } else if ("Corporate".toLowerCase().equalsIgnoreCase(taxType)) {
            ((RadioButton) findViewById(R.id.corporate)).setChecked(true);

        }

        String sellToDealer = mStockDataModel.getSell_to_dealer();
        if ("0".equals(sellToDealer)) {
            this.sellToDealer.setChecked(false);

        } else if ("1".equals(sellToDealer)) {
            this.sellToDealer.setChecked(true);
            String dealerPrice = mStockDataModel.getDealerprice();
            this.dealerPrice.setText(dealerPrice);


        }
        if (!mStockDataModel.getStockYear().equals("")) {
            /* stockMonth = Integer.parseInt(mStockDataModel.getStockMonth());
            stYear = mStockDataModel.getStockYear();
            stockYear.setText();*/
            Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, Integer.parseInt(ApplicationController.getStocksDB().getStocksData().getStockMonth()));
            c.set(Calendar.YEAR, Integer.parseInt(ApplicationController.getStocksDB().getStocksData().getStockYear()));
            long current_mon_year = Calendar.getInstance().getTimeInMillis();
            if (c.getTimeInMillis() > current_mon_year) {
               /* mStockDataModel.setStockMonth(Calendar.getInstance().get(Calendar.MONTH) + "");
                mStockDataModel.setStockMonth(Calendar.getInstance().get(Calendar.YEAR) + "");*/
                if (Calendar.getInstance().get(Calendar.MONTH) == 0) {
                    stockYear.setText(ApplicationController.monthShortMap.get(12 + "") + "/" + (Calendar.getInstance().get(Calendar.YEAR) - 1));
                } else {
                    stockYear.setText(ApplicationController.monthShortMap.get(Calendar.getInstance().get(Calendar.MONTH) + "") + "/" + Calendar.getInstance().get(Calendar.YEAR));
                }
            } else {

                stockYear.setText(ApplicationController.monthShortMap.get(mStockDataModel.getStockMonth()) + "/" + mStockDataModel.getStockYear());
            /*stMonth.setText(ApplicationController.monthShortMap.get(String.valueOf(stockMonth)));*/
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.add_stock_menu, menu);
        if (!"SV".equalsIgnoreCase(comingFrom)) {

            OverFlowMenu.OverFlowMenuText(this, "Reset", 16, menu);


            OverFlowMenu.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (comingFrom.equals("")) {
                        showResetDataAlert();

                    }
                }
            });
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        Intent intent = null;

     /*   if (id == R.id.action_reset) {

            if (comingFrom.equals("")) {
                showResetDataAlert();

            }

            return true;
        } else */if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    private void showResetDataAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(StockAddActivity.this);
        final AlertDialog alertDialog = builder
                .setTitle(R.string.alert)
                .setMessage(R.string.resetdata)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                resetData();

                            }
                        })
                .setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //alertDialog.dismiss();

                            }
                        }).setCancelable(false).create();
        if (!alertDialog.isShowing() && !this.isFinishing()) {
            alertDialog.show();
        }
    }

    private void resetData() {
        ApplicationController.getStocksDB().deletePreviousData();
        stockColor.setEnabled(false);

        if (makeModelVersionAdapter != null) {
            makeModelVersionAdapter = new MakeModelVersionAdapter(this, null);
            makeModelVersionAdapter.notifyDataSetChanged();
            mMakeModel.setText("");
            //mModelVersion.setEnabled(false);
        }

        mModelVersion.setText("");
        if (stock != null) {
            stock.setMakeId("");
            stock.setModel("");
            stock.setVersionid("");
            stock.setVersionName("");
            stock.setFuelType("");
            fuelTypeLayout.setVisibility(View.GONE);
        }
        cngFitted.setChecked(false);
        if (modelVersionAdapter != null) {
            modelVersionAdapter = new ModelVersionAdapter(this, null);
            modelVersionAdapter.notifyDataSetChanged();
            //mModelVersion.setEnabled(false);
        }

        fuelTypeLayout.setVisibility(View.GONE);
        selectedColor = null;
        stockColor.setText("");

        GradientDrawable bgShape = (GradientDrawable) color.getBackground();
        bgShape.setColor(Color.parseColor("#ffffff"));
        findViewById(R.id.otherColorLayout).setVisibility(View.GONE);
        stockOtherColor.setText("");
        insuranceYear = Calendar.getInstance().get(Calendar.YEAR);
        insuranceMonth = Calendar.getInstance().get(Calendar.MONTH);
        stYear = Calendar.getInstance().get(Calendar.YEAR);
        stockMonth = Calendar.getInstance().get(Calendar.MONTH);
        insuranceValidYear.setText(ApplicationController.monthShortMap.get(String.valueOf(insuranceMonth + 1)) + "/" + insuranceYear);
        if (Calendar.getInstance().get(Calendar.MONTH) == 0) {
            stockYear.setText(ApplicationController.monthShortMap.get(12 + "") + "/" + (Calendar.getInstance().get(Calendar.YEAR) - 1));
        } else {
            stockYear.setText(ApplicationController.monthShortMap.get(Calendar.getInstance().get(Calendar.MONTH) + "") + "/" + Calendar.getInstance().get(Calendar.YEAR));
        }
        mStockDates = "";
        mInsuranceDates = "";
        //stockYear.setText("");
        // createYearList();
       /* stMonth.setText(ApplicationController.monthShortMap.get(String.valueOf(monthList[monthList.length - 1])));*/
        // createInsuranceYearList();
        //insuranceValidity.setText("");

        kmsDriven.setText("");

        selectedNumOwners = null;
        numOwners.setText("");
        selectedCity = new CityData();
        selectedCity.setCityName(CommonUtils.getStringSharedPreference(StockAddActivity.this, Constants.UC_DEALER_CITY, ""));
        selectedCity.setCityId(CommonUtils.getStringSharedPreference(StockAddActivity.this, Constants.UC_DEALER_CITY, ""));
        selectedShowroom = null;
        dealershipName.setText("");
        findViewById(R.id.registrationCityLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.registrationNumberLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.radioGroupLayout).setVisibility(View.VISIBLE);
        registrationCity.setText(CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_CITY, ""));
        registrationNumber.setText("");
        stockPrice.setText("");
        stockPrice.setHint(R.string.enter_price);
        tvStockPrice.setVisibility(View.GONE);
        insuranceType.clearCheck();
        taxType.clearCheck();
        if (evenOddRadio.getVisibility() == View.VISIBLE) {
            evenOddRadio.clearCheck();
        }
        mInsuranceGroup = "Insurance";
        mTaxGroup = "Tax";

        findViewById(R.id.insuranceTypeOptions).setVisibility(View.GONE);
        //date set
        sellToDealer.setChecked(false);
        //  dealerMobile.setText(CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_MOBILE, ""));
        dealerPrice.setText("");
        dealerPrice.setHint(R.string.hint_dealer_price);
        uploadOnDealerLayout.setVisibility(View.GONE);
        tvDealerPrice.setVisibility(View.GONE);
        evenOddRadio.setVisibility(View.VISIBLE);

        //to clear the selected photos
        findViewById(R.id.photosAddedContainer).setVisibility(View.INVISIBLE);
        imagesPathList = null;
        deletedList = null;
        com.imageuploadlib.Utils.ApplicationController.selectedFiles.clear();
        ((RadioButton) findViewById(R.id.noInsurance)).setChecked(true);
        ((RadioButton) findViewById(R.id.individual)).setChecked(true);
        numOwners.setText(numOwnerAdapter.getItem(0).getValue());
        registrationCity.setText(CommonUtils.getStringSharedPreference(StockAddActivity.this, Constants.UC_DEALER_CITY, ""));
        selectedNumOwners = new BasicListItemModel(numOwnerAdapter.getItem(0).getId(), numOwnerAdapter.getItem(0).getValue());
        formShowroomList();
    }

    private void makeShowroomsRequest(final boolean showFullPageError) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.UCDID, String.valueOf(CommonUtils.getIntSharedPreference(this, Constants.UC_DEALER_ID, -1)));
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
        params.put(Constants.METHOD_LABEL, Constants.SHOWROOM_METHOD);
        progressDialog.setCancelable(false);
        progressDialog.show();

        RetrofitRequest.showroomRequest(params, new Callback<ShowroomModel>() {
            @Override
            public void success(final ShowroomModel showroomModel, final retrofit.client.Response response) {
                progressDialog.dismiss();
                if (showroomModel.getStatus().equalsIgnoreCase("T")) {
                    showroomList = showroomModel.getShowroomList();
                    if (showroomList.size() == 1) {
                        selectedShowroom = showroomList.get(0);
                        dealershipName.setText(showroomList.get(0).getShowroomName());
                        dealershipName.setClickable(false);
                    } else {
                        dealershipAdapter = new DealershipAdapter(StockAddActivity.this, showroomList);
                        dealershipName.setOnClickListener(StockAddActivity.this);
                    }

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(StockAddActivity.this);
                    AlertDialog alertDialog = builder.setTitle(R.string.sorry)
                            .setMessage(showroomModel.getErrorMessage())
                            .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ApplicationController.getInstance().getGAHelper().sendEvent(
                                            GAHelper.TrackerName.APP_TRACKER,
                                            Constants.CATEGORY_ADD_STOCK,
                                            Constants.CATEGORY_ADD_STOCK,
                                            Constants.SHOWROOM_METHOD + " --- " + Constants.ACTION_SERVER_REQUEST,
                                            showroomModel.getErrorMessage(),
                                            0);
                                    makeShowroomsRequest(showFullPageError);
                                }
                            })
                            .setNegativeButton(R.string.add_stock_later,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            progressDialog.dismiss();
                                            StockAddActivity.this.finish();
                                        }
                                    })
                            .setCancelable(false)
                            .create();

                    if (!alertDialog.isShowing() && !StockAddActivity.this.isFinishing()) {
                        alertDialog.show();
                    }
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof UnknownHostException) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(StockAddActivity.this);
                    AlertDialog alertDialog = builder.setTitle(R.string.network_connection_error)
                            .setMessage(R.string.network_connection_error_message)
                            .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ApplicationController.getInstance().getGAHelper().sendEvent(
                                            GAHelper.TrackerName.APP_TRACKER,
                                            Constants.CATEGORY_ADD_STOCK,
                                            Constants.CATEGORY_ADD_STOCK,
                                            Constants.SHOWROOM_METHOD + " --- Network connection problem",
                                            CommonUtils.getStringSharedPreference(StockAddActivity.this, Constants.UC_DEALER_USERNAME, ""),
                                            0);
                                    makeShowroomsRequest(showFullPageError);
                                }
                            })
                            .setNegativeButton(R.string.add_stock_later,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            progressDialog.dismiss();
                                            StockAddActivity.this.finish();
                                        }
                                    })
                            .setCancelable(false)
                            .create();

                    if (!alertDialog.isShowing() && !StockAddActivity.this.isFinishing()) {
                        alertDialog.show();
                    }

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(StockAddActivity.this);
                    AlertDialog alertDialog = builder.setTitle(R.string.sorry)
                            .setMessage(R.string.server_error)
                            .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ApplicationController.getInstance().getGAHelper().sendEvent(
                                            GAHelper.TrackerName.APP_TRACKER,
                                            Constants.CATEGORY_ADD_STOCK,
                                            Constants.CATEGORY_ADD_STOCK,
                                            Constants.SHOWROOM_METHOD + " --- " + Constants.ACTION_SERVER_REQUEST,
                                            CommonUtils.getStringSharedPreference(StockAddActivity.this, Constants.UC_DEALER_USERNAME, ""),
                                            0);
                                    makeShowroomsRequest(showFullPageError);
                                }
                            })
                            .setNegativeButton(R.string.add_stock_later,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            progressDialog.dismiss();
                                            StockAddActivity.this.finish();
                                        }
                                    })
                            .setCancelable(false)
                            .create();

                    if (!alertDialog.isShowing() && !StockAddActivity.this.isFinishing()) {
                        alertDialog.show();
                    }
                }
            }

        });

      /*  ShowroomRequest showroomRequest = new ShowroomRequest(
                this,
                Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<ShowroomModel>() {
                    @Override
                    public void onResponse(final ShowroomModel response) {
                        progressDialog.dismiss();
                        if (response.getStatus().equalsIgnoreCase("T")) {
                            showroomList = response.getShowroomList();
                            if (showroomList.size() == 1) {
                                selectedShowroom = showroomList.get(0);
                                dealershipName.setText(showroomList.get(0).getShowroomName());
                                dealershipName.setClickable(false);
                            } else {
                                dealershipAdapter = new DealershipAdapter(StockAddActivity.this, showroomList);
                                dealershipName.setOnClickListener(StockAddActivity.this);
                            }

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(StockAddActivity.this);
                            AlertDialog alertDialog = builder.setTitle(R.string.sorry)
                                    .setMessage(response.getErrorMessage())
                                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ApplicationController.getInstance().getGAHelper().sendEvent(
                                                    GAHelper.TrackerName.APP_TRACKER,
                                                    Constants.CATEGORY_ADD_STOCK,
                                                    Constants.CATEGORY_ADD_STOCK,
                                                    Constants.SHOWROOM_METHOD + " --- " + Constants.ACTION_SERVER_REQUEST,
                                                    response.getErrorMessage(),
                                                    0);
                                            makeShowroomsRequest(showFullPageError);
                                        }
                                    })
                                    .setNegativeButton(R.string.add_stock_later,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    progressDialog.dismiss();
                                                    StockAddActivity.this.finish();
                                                }
                                            })
                                    .setCancelable(false)
                                    .create();

                            if (!alertDialog.isShowing() && !StockAddActivity.this.isFinishing()) {
                                alertDialog.show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {

                        if (error.getCause() instanceof UnknownHostException) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(StockAddActivity.this);
                            AlertDialog alertDialog = builder.setTitle(R.string.network_connection_error)
                                    .setMessage(R.string.network_connection_error_message)
                                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ApplicationController.getInstance().getGAHelper().sendEvent(
                                                    GAHelper.TrackerName.APP_TRACKER,
                                                    Constants.CATEGORY_ADD_STOCK,
                                                    Constants.CATEGORY_ADD_STOCK,
                                                    Constants.SHOWROOM_METHOD + " --- Network connection problem",
                                                    CommonUtils.getStringSharedPreference(StockAddActivity.this, Constants.UC_DEALER_USERNAME, ""),
                                                    0);
                                            makeShowroomsRequest(showFullPageError);
                                        }
                                    })
                                    .setNegativeButton(R.string.add_stock_later,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    progressDialog.dismiss();
                                                    StockAddActivity.this.finish();
                                                }
                                            })
                                    .setCancelable(false)
                                    .create();

                            if (!alertDialog.isShowing() && !StockAddActivity.this.isFinishing()) {
                                alertDialog.show();
                            }

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(StockAddActivity.this);
                            AlertDialog alertDialog = builder.setTitle(R.string.sorry)
                                    .setMessage(R.string.server_error)
                                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ApplicationController.getInstance().getGAHelper().sendEvent(
                                                    GAHelper.TrackerName.APP_TRACKER,
                                                    Constants.CATEGORY_ADD_STOCK,
                                                    Constants.CATEGORY_ADD_STOCK,
                                                    Constants.SHOWROOM_METHOD + " --- " + Constants.ACTION_SERVER_REQUEST,
                                                    CommonUtils.getStringSharedPreference(StockAddActivity.this, Constants.UC_DEALER_USERNAME, ""),
                                                    0);
                                            makeShowroomsRequest(showFullPageError);
                                        }
                                    })
                                    .setNegativeButton(R.string.add_stock_later,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    progressDialog.dismiss();
                                                    StockAddActivity.this.finish();
                                                }
                                            })
                                    .setCancelable(false)
                                    .create();

                            if (!alertDialog.isShowing() && !StockAddActivity.this.isFinishing()) {
                                alertDialog.show();
                            }
                        }
                    }
                }
        );

        ApplicationController.getInstance().addToRequestQueue(showroomRequest, Constants.TAG_SHOWROOM_REQUEST, showFullPageError, this);
    */}

    private void formLists() {

        for (Map.Entry<String, String> entry : ApplicationController.numOwnersMap.entrySet()) {
            BasicListItemModel listItem = new BasicListItemModel(entry.getKey(), entry.getValue());
            numOwnersList.add(listItem);
        }

        numOwnersList.trimToSize();

        numOwnerAdapter = new BasicListItemAdapter(this, numOwnersList);


    }

    private void formShowroomList() {
        showroomList = new ArrayList<ShowroomData>();

        ArrayList<ShowroomData> showrooms;
        int dealerID = CommonUtils.getIntSharedPreference(StockAddActivity.this, Constants.DEALER_ID, 0);
        showrooms = ApplicationController.getMakeModelVersionDB().getShowroomsByDealerId(String.valueOf(dealerID));
        if (showrooms != null && showrooms.size() > 0) {
            for (int i = 0; i < showrooms.size(); i++) {
                showroomList.add(showrooms.get(i));
            }

            dealershipAdapter = new DealershipAdapter(StockAddActivity.this, showroomList);
            dealershipName.setOnClickListener(StockAddActivity.this);
            dealershipName.setText(dealershipAdapter.getItem(0).getShowroomName());
            if (showroomList.size() < 2) {
                dealershipName.setVisibility(View.GONE);
                selectedShowroom = showroomList.get(0);
            }
            selectedShowroom = new ShowroomData();
            selectedShowroom.setShowroomId(dealershipAdapter.getItem(0).getShowroomId());
            selectedShowroom.setShowroomName(dealershipAdapter.getItem(0).getShowroomName());
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_ADD_STOCK);
        ApplicationController.getEventBus().register(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (monthYearPickerDialog != null) {
            monthYearPickerDialog.dismiss();
        }
        ApplicationController.getEventBus().unregister(this);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

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

            modelNameBlank = modelName;

            mMakeModel.setText(modelName);
            mMakeModel.setSelection(mMakeModel.getText().length());
            mMakeModel.setCompoundDrawablesWithIntrinsicBounds(
                    ApplicationController.makeLogoMap.get(Integer.parseInt(makeId)),
                    0,
                    R.drawable.close_layer_dark,
                    0);
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mMakeModel.getWindowToken(), 0);
            ApplicationController.getEventBus().post(new MakeModelSelectedEvent(stock));

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

    @Subscribe
    public void onMakeModelSelected(MakeModelSelectedEvent event) {
        fuelTypeLayout.setVisibility(View.GONE);
        stock = event.getModel();
        stock.setVersionName("");
        stock.setVersionid("");
        mModelVersion.setText("");
        /*mModelVersion.handleClearButton();*/
        mModelVersion.setEnabled(true);
        mModelVersion.setOnClickListener(this);
        colorAdapter = null;
        stockColor.setText("");
        selectedColor = null;
        /*mModelVersion.setOnItemClickListener(this);*/
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        Calendar calendar = Calendar.getInstance();
        final ListPopupWindow listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setModal(true);
        listPopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),
                R.drawable.abc_popup_background_mtrl_mult,
                null));
        listPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);

        final ListView insuranceListView = new ListView(this);
        final PopupWindow insurancePopupWindow = new PopupWindow(insuranceListView,
                insuranceValidYear.getWidth(),
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        insurancePopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),
                R.drawable.abc_popup_background_mtrl_mult, null));
        switch (v.getId()) {

            case R.id.footerLayout:
                mValidator.validate();
                break;

            case R.id.dealershipName:
                final ListPopupWindow dealerShipPopupWindow = new ListPopupWindow(this);
                dealerShipPopupWindow.setAdapter(dealershipAdapter);
                /*dealerShipPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));*/
                dealerShipPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
                dealerShipPopupWindow.setModal(true);
                dealerShipPopupWindow.setAnchorView(findViewById(R.id.dealershipName));
                dealerShipPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
                dealerShipPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedShowroom = (ShowroomData) parent.getAdapter().getItem(position);
                        dealershipName.setText(selectedShowroom.getShowroomName());
                        dealerShipPopupWindow.dismiss();
                    }
                });
                dealershipName.post(new Runnable() {
                    @Override
                    public void run() {
                        dealerShipPopupWindow.show();
                    }
                });

                break;

            case R.id.photosAddedContainer:
            case R.id.addPhotoWidget:
                Boolean serviceRunning = CommonUtils.getBooleanSharedPreference(getApplicationContext(), ImageUploadService.SERVICE_RUNNING + stockId, false);
                GCLog.e(ImageUploadService.SERVICE_RUNNING + serviceRunning + "");
                if (serviceRunning && stockId != null) {
                    Toast.makeText(getApplicationContext(), "This stock is already being updated. Please wait while all photos are uploaded", Toast.LENGTH_SHORT).show();
                } else {
                    if ((v.getTag() != null) && String.valueOf(v.getTag()).equals("addPhoto")) {
                        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                Constants.CATEGORY_ADD_STOCK,
                                Constants.CATEGORY_ADD_STOCK,
                                Constants.ACTION_TAP,
                                Constants.LABEL_ADD_PHOTO,
                                0);
                    } else {
                        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                Constants.CATEGORY_ADD_STOCK,
                                Constants.CATEGORY_ADD_STOCK,
                                Constants.ACTION_TAP,
                                Constants.LABEL_ADD_STOCK_VIEW_PHOTOS,
                                0);
                    }
                   /* intent = new Intent(this, PhotoUpload.class);

                    if ((imagesList != null)) {
                        if (imagesList.size() > 0) {
                            intent.putExtra(Constants.STOCK_ID, stockId);
                            if (imagesPathList != null) {
                                intent.putExtra(PhotoUpload.KEY_ARRAYLIST_IMAGES, imagesPathList);
                            } else {
                                intent.putStringArrayListExtra(PhotoUpload.KEY_ARRAYLIST_IMAGES, imagesList);
                            }
                        } else if (imagesPathList != null) {
                            intent.putExtra(PhotoUpload.KEY_ARRAYLIST_IMAGES, imagesPathList);
                        }
                    } else {
                        if (imagesPathList != null) {
                            intent.putExtra(PhotoUpload.KEY_ARRAYLIST_IMAGES, imagesPathList);
                        }
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivityForResult(intent, REQUEST_CODE_UPLOAD_PHOTOS);
                    overridePendingTransition(R.anim.mapview_slide_up, 0);*/
                    PhotoParams params = new PhotoParams(Constants.getImageUploadURL(getApplicationContext()));
                    if ((imagesList != null)) {
                        if (imagesList.size() > 0) {
                            //intent.putExtra(Constants.STOCK_ID, stockId);
                            if (imagesPathList != null) {
                                // intent.putExtra(PhotoUpload.KEY_ARRAYLIST_IMAGES, imagesPathList);
                                params.setImagePathList(imagesPathList);

                            } else {
                                //intent.putStringArrayListExtra(PhotoUpload.KEY_ARRAYLIST_IMAGES, imagesList);
                                params.setImagePathList(imagesList);
                            }
                        } else if (imagesPathList != null) {
                            // intent.putExtra(PhotoUpload.KEY_ARRAYLIST_IMAGES, imagesPathList);
                            params.setImagePathList(imagesPathList);
                        }
                    } else {
                        if (imagesPathList != null) {
                            // intent.putExtra(PhotoUpload.KEY_ARRAYLIST_IMAGES, imagesPathList);
                            params.setImagePathList(imagesPathList);
                        }
                    }

                    params.setMode(PhotoParams.MODE.NEUTRAL);
                    params.setUploadApi(Constants.getImageUploadURL(getApplicationContext()));
                    PhotosLibrary.collectPhotos(StockAddActivity.this, params);

                }
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
                        if (selectedNumOwners.getId().equals("0")) {
                            hideFieldsforUnregisteredCase();
                        } else {
                            showFieldsForRegisteredCase();
                        }
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
                            GradientDrawable bgShape = (GradientDrawable) color.getBackground();
                            bgShape.setColor(Color.parseColor("#ffffff"));
                            stockColor.setText("");

                            selectedColor = null;
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
                                    stockColor.setText("");
                                    GradientDrawable bgShape = (GradientDrawable) color.getBackground();
                                    bgShape.setColor(Color.parseColor("#ffffff"));
                                    selectedColor = null;

                                }
                            }

                        }

                        makeVariantColorsRequest(false);

                        if ((fuelType != null) && !fuelType.isEmpty()) {
                            fuelTypeLayout.setVisibility(View.VISIBLE);

                            StockAddActivity.this.fuelType.setText("Fuel Type: " + fuelType);

                            if ("PETROL".equalsIgnoreCase(fuelType)) {
                                cngFitted.setVisibility(View.VISIBLE);
                                cngFitted.setChecked(false);
                            } else {
                                cngFitted.setVisibility(View.GONE);
                            }
                        } else {
                            fuelTypeLayout.setVisibility(View.GONE);
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


            case R.id.registrationCity:
                break;

            case R.id.stockYear:
                monthYearPickerDialog = new MonthYearPicker(Constants.CODE_FOR_MFG_PICKER_DIALOG);
                monthYearPickerDialog.show(getFragmentManager(), "MonthYearPickerDialog");
                monthYearPickerDialog.setListener(StockAddActivity.this);

                /*listPopupWindow.setAdapter(new ListPopupWindowAdapter(this, yearList, false));
                listPopupWindow.setAnchorView(findViewById(R.id.stockYear));
                listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        stockYear.setText(yearList[position].toString());
                        if (yearList[position] == Calendar.getInstance().get(Calendar.YEAR)) {
                            createMonthList(Calendar.getInstance().get(Calendar.MONTH));
                        } else
                            createMonthList(12);
                        stMonth.setText(ApplicationController.monthShortMap.get(String.valueOf(monthList[0])));
                        stYear = yearList[position];
                        stockMonth = monthList[0];
                        listPopupWindow.dismiss();
                    }
                });
                stockYear.post(new Runnable() {
                    @Override
                    public void run() {
                        listPopupWindow.show();
                    }
                });

                *//*if (comingFrom.equals("") && isStockDatePickerCLicked) {
                    datePickerDialog = DatePickerDialog.newInstance(this,
                            stYear,
                            stockMonth,
                            stockDay,
                            false);
                } else if (comingFrom.equals("") && ApplicationController.getStocksDB().getStocksCount() > 0 &&
                        !ApplicationController.getStocksDB().getStocksData().getStockYear().equals("") &&
                        !ApplicationController.getStocksDB().getStocksData().getStockMonth().equals("")) {
                    datePickerDialog = DatePickerDialog.newInstance(this,
                            Integer.parseInt(ApplicationController.getStocksDB().getStocksData().getStockYear()),
                            Integer.parseInt(ApplicationController.getStocksDB().getStocksData().getStockMonth()) - 1,
                            stockDay,
                            false);
                } else {
                    datePickerDialog = DatePickerDialog.newInstance(this,
                            stYear,
                            stockMonth,
                            stockDay,
                            false);
                }
                if(!datePickerDialog.isVisible()) {
                    datePickerDialog.setYearRange(calendar.get(Calendar.YEAR) - 50, calendar.get(Calendar.YEAR));
                    datePickerDialog.setCloseOnSingleTapDay(false);
                    datePickerDialog.setOnDateSetListener(this, DateType.MODEL_MONTH_YEAR);
                    datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
                }*/
                break;

            /*case R.id.stockMonth:
                *//*final ListPopupWindow stockMonthPopupWindow = new ListPopupWindow(this);
                stockMonthPopupWindow.setAdapter(new ListPopupWindowAdapter(this, monthList, true));
                stockMonthPopupWindow.setModal(true);
                stockMonthPopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.abc_popup_background_mtrl_mult, null));
                stockMonthPopupWindow.setAnchorView(findViewById(R.id.stockMonth));
                stockMonthPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
                stockMonthPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        stMonth.setText(ApplicationController.monthShortMap.get(String.valueOf(monthList[position])));
                        stockMonth = monthList[position];
                        stockMonthPopupWindow.dismiss();
                    }
                });
                stMonth.post(new Runnable() {
                    @Override
                    public void run() {
                        stockMonthPopupWindow.show();
                    }
                });*//*

                listPopupWindow.setAdapter(new ListPopupWindowAdapter(this, monthList, true));
                listPopupWindow.setAnchorView(findViewById(R.id.stockMonth));
                listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                       *//* stMonth.setText(ApplicationController.monthShortMap.get(String.valueOf(monthList[position])));*//*
                        stockMonth = monthList[position];
                        listPopupWindow.dismiss();
                    }
                });
                stMonth.post(new Runnable() {
                    @Override
                    public void run() {
                        listPopupWindow.show();
                    }
                });
                break;*/

            case R.id.stockcolorlayout:
                if (colorAdapter != null) {
                    listPopupWindow.setAdapter(colorAdapter);
                    listPopupWindow.setAnchorView(findViewById(R.id.stockcolorlayout));
                    listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            stockColor.setError(null);
                            selectedColor = (BasicListItemModel) parent.getAdapter().getItem(position);
                            if ("Other".equalsIgnoreCase(selectedColor.getValue())) {
                                findViewById(R.id.otherColorLayout).setVisibility(View.VISIBLE);
                            } else {
                                findViewById(R.id.otherColorLayout).setVisibility(View.GONE);

                            }
                            stockColor.setText(selectedColor.getValue());
                            if (selectedColor.getId() != null && !selectedColor.getId().equals("")) {
                                if (!selectedColor.getId().trim().equalsIgnoreCase("Other")) {
                                    GradientDrawable bgShape = (GradientDrawable) color.getBackground();
                                    bgShape.setColor(Color.parseColor(selectedColor.getId()));
                                } else {
                                    GradientDrawable bgShape = (GradientDrawable) color.getBackground();
                                    bgShape.setColor(Color.parseColor("#ffffff"));
                                }
                            }
                            listPopupWindow.dismiss();
                        }
                    });
                    stockColor.post(new Runnable() {
                        @Override
                        public void run() {
                            listPopupWindow.show();
                        }
                    });
                }
                break;

            case R.id.insuranceValidYear:
                CommonUtils.hideKeyboard(this, insuranceValidYear);
                CommonUtils.scrollTo(scrollView, sellToDealer);
                monthYearPickerDialog = new MonthYearPicker(Constants.CODE_FOR_INSURANCE_PICKER_DIALOG);
                monthYearPickerDialog.show(getFragmentManager(), "MonthYearPicerDialog");
                monthYearPickerDialog.setListener(StockAddActivity.this);
               /* insuranceListView.setAdapter(new ListPopupWindowAdapter(this, insuranceYearList, false));
                insuranceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        insuranceValidYear.setText(insuranceYearList[i].toString());
                        resetInsuranceMonthList(insuranceYearList[i]);
                        insuranceValidMonth.setText(ApplicationController.monthShortMap.get(String.valueOf(insuranceMonthList[0])));
                        insuranceYear = insuranceYearList[i];
                        insuranceMonth = insuranceMonthList[0];
                        insurancePopupWindow.dismiss();
                    }
                });
                insuranceValidYear.post(new Runnable() {
                    @Override
                    public void run() {
                        insurancePopupWindow.showAsDropDown(insuranceValidYear, 0, -15);
                    }
                });*/
                break;
            /*case R.id.insuranceValidMonth:

                CommonUtils.hideKeyboard(this, insuranceValidMonth);
                CommonUtils.scrollTo(scrollView, sellToDealer);
                insuranceListView.setAdapter(new ListPopupWindowAdapter(this, insuranceMonthList, true));
                insuranceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        insuranceValidMonth.setText(ApplicationController.monthShortMap.get(String.valueOf(insuranceMonthList[i])));
                        insuranceMonth = insuranceMonthList[i];
                        insurancePopupWindow.dismiss();
                    }
                });
                insuranceValidMonth.post(new Runnable() {
                    @Override
                    public void run() {
                        insurancePopupWindow.showAsDropDown(insuranceValidMonth, 0, -15);
                    }
                });
                });
                break;*/

            case R.id.stockPrice:
                tvStockPrice.setVisibility(View.VISIBLE);
                break;

            case R.id.dealerPrice:
                tvDealerPrice.setVisibility(View.VISIBLE);
                break;

        }}

    private void hideFieldsforUnregisteredCase() {
        findViewById(R.id.registrationCityLayout).setVisibility(View.GONE);
        findViewById(R.id.registrationNumberLayout).setVisibility(View.GONE);
        findViewById(R.id.radioGroupLayout).setVisibility(View.GONE);
        evenOddRadio.setVisibility(View.GONE);
    }

    private void showFieldsForRegisteredCase() {
        findViewById(R.id.registrationCityLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.registrationNumberLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.radioGroupLayout).setVisibility(View.VISIBLE);
        evenOddRadio.setVisibility(View.VISIBLE);
    }

    private void makeVariantColorsRequest(boolean showFullPageError) {
        if (progressDialog != null) {
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        HashMap<String, String> params = new HashMap<>();

        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
        params.put(Constants.METHOD_LABEL, Constants.MMV_COLORS_METHOD);
        if (stock != null) {
            params.put("make", stock.getMake());
            params.put("model", stock.getModel());
            params.put("version", stock.getVersionName());
        } else {
            if (comingFrom.equals("") && ApplicationController.getStocksDB().getStocksCount() > 0) {
                StockDataModel mStockDatModel = ApplicationController.getStocksDB().getStocksData();
                params.put("make", mStockDatModel.getMakeName());
                params.put("model", mStockDatModel.getModel());
                params.put("version", mStockDatModel.getVersion());

            }
        }

        RetrofitRequest.variantColorsRequest(params, new Callback<VariantColorsModel>() {

            @Override
            public void success(VariantColorsModel response, retrofit.client.Response res) {

                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (response != null && "T".equalsIgnoreCase(response.getStatus() == null ? "" : response.getStatus())) {
                    colorsList.clear();
                    for (int i = 0; i < response.getColors().size(); i++) {
                        String color = response.getColors().get(i);
                        String hexcode = "";
                        if (response.getHexCode() != null) {
                            hexcode = response.getHexCode().get(i);
                        }
                        BasicListItemModel model = new BasicListItemModel(hexcode, response.getColors().get(i));
                        stockColor.setEnabled(true);
                        colorsList.add(model);
                    }
                    colorsList.add(new BasicListItemModel("Other", "Other"));

                    colorAdapter = new ColorAdapter(StockAddActivity.this, colorsList);

                } else {
                    CommonUtils.showToast(StockAddActivity.this, "Error fetching version colors", Toast.LENGTH_SHORT);
                }

            }

            @Override
            public void failure(RetrofitError error) {

                if (StockAddActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                    return;
                }
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }

                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(StockAddActivity.this,
                            getString(R.string.no_internet_connection), Toast.LENGTH_SHORT);
                    return;
                }
                CommonUtils.showToast(StockAddActivity.this, "Server Error. Please try again later.", Toast.LENGTH_SHORT);
            }
        });

       /* VariantColorsRequest request = new VariantColorsRequest(
                this,
                Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<VariantColorsModel>() {
                    @Override
                    public void onResponse(VariantColorsModel response) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        if (response != null && "T".equalsIgnoreCase(response.getStatus() == null ? "" : response.getStatus())) {
                            colorsList.clear();
                            for (int i = 0; i < response.getColors().size(); ++i) {

                                String color = response.getColors().get(i);
                                String hexcode = "";
                                if (response.getHexCode() != null) {
                                    hexcode = response.getHexCode().get(i);
                                }

                                BasicListItemModel model = new BasicListItemModel(hexcode, response.getColors().get(i));
                                stockColor.setEnabled(true);
                                colorsList.add(model);

                            }
                            colorsList.add(new BasicListItemModel("Other", "Other"));

                            colorAdapter = new ColorAdapter(StockAddActivity.this, colorsList);

                        } else {
                            CommonUtils.showToast(StockAddActivity.this, "Error fetching version colors", Toast.LENGTH_SHORT);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        CommonUtils.showToast(StockAddActivity.this, "Server Error. Please try again later.", Toast.LENGTH_SHORT);
                    }
                }
        );

        ApplicationController.getInstance().addToRequestQueue(request, Constants.TAG_COLORS_REQUEST, showFullPageError, this);*/
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {

        //GCLog.e(dateTime.monthOfYear().getAsShortText(Locale.ENGLISH));
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day, DateType dateType) {
        DateTime dateTime = DateTime.parse(year + "-" + (month + 1) + "-" + day);


        switch (dateType) {
            case MODEL_MONTH_YEAR:
                if (dateTime.isBeforeNow()) {
                    mStockDates = "";
                    isStockDatePickerCLicked = true;
                    stYear = year;
                    stockMonth = month;
                    stockDay = day;
                    // stockYear.setText(dateTime.monthOfYear().getAsShortText(Locale.ENGLISH) + "/" + dateTime.year().getAsShortText(Locale.ENGLISH));
                    return;
                }

                CommonUtils.showToast(this, "Please enter a valid model month and year", Toast.LENGTH_SHORT);

                break;

            case INSURANCE_VALIDITY:

                if (dateTime.isAfterNow()) {
                    mInsuranceDates = "";
                    isInsuranceDateClicked = true;
                    insuranceYear = year;
                    insuranceMonth = month;
                    insuranceDay = day;
                    //insuranceValidity.setText(dateTime.monthOfYear().getAsShortText(Locale.ENGLISH) + "/" + dateTime.year().getAsShortText(Locale.ENGLISH));
                    return;
                }
                CommonUtils.showToast(this, "Please enter a valid insurance validity month and year", Toast.LENGTH_SHORT);
                break;
        }
    }

    @Override
    public void onNoInternetConnection(NetworkEvent networkEvent) {
        if (networkEvent.getNetworkError() == NetworkEvent.NetworkError.NO_INTERNET_CONNECTION) {
            showNetworkErrorDialog(networkEvent);
        }
    }

    private void showNetworkErrorDialog(final NetworkEvent networkEvent) {
        if (!networkEvent.isShowFullPageError()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog alertDialog = builder
                    .setTitle(R.string.sorry)
                    .setMessage(R.string.network_error)
                    .setPositiveButton(R.string.retry,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    makeShowroomsRequest(networkEvent.isShowFullPageError());
                                }
                            })
                    .setNegativeButton(R.string.add_stock_later,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    StockAddActivity.this.finish();
                                }
                            })
                    .setCancelable(false)
                    .create();

            if (!alertDialog.isShowing() && !StockAddActivity.this.isFinishing()) {
                alertDialog.show();
            }
        } else {


        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.selltodealer) {
            if (isChecked) {
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_ADD_STOCK,
                        Constants.CATEGORY_ADD_STOCK,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SELL_TO_DEALER,
                        0);
                scrollView.scrollTo(0, scrollView.getBottom());
                scrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                }, 100);
                uploadOnDealerLayout.setVisibility(View.VISIBLE);

            } else {
                uploadOnDealerLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onValidationSucceeded() {

        if (evenOddRadio.getVisibility() == View.VISIBLE
                && (registrationCity.getText().toString().equals("Delhi")
                || registrationCity.getText().toString().equals("New Delhi"))) {
            selected = evenOddRadio.getCheckedRadioButtonId();
            if (!(selected == R.id.even || selected == R.id.odd)) {
                onValidationFailed(evenOddRadio, new Rule<View>(getString(R.string.registration_type_required)) {
                    @Override
                    public boolean isValid(View view) {
                        return false;
                    }
                });
                return;
            }

        }

        if (!(cityAdapter.getCursor().getCount() > 0) ) { // Adapter will have data for enlisted city
            onValidationFailed(registrationCity, new Rule<View>(getString(R.string.error_registration_city_required)) {
                @Override
                public boolean isValid(View view) {
                    return false;
                }
            });
            return;
        }

        if(!modelNameBlank.equals(mMakeModel.getText().toString())) {
            onValidationFailed(mMakeModel, new Rule<View>(getString(R.string.error_make_model_required)) {
                @Override
                public boolean isValid(View view) {
                    return false;
                }
            });
            return;
        }


        if ("SV".equals(comingFrom)) {
            updateStock();
        } else {
            if (ApplicationController.getStocksDB().getStocksCount() > 0)

                initializeAll();
            addStock();

        }

        //Toast.makeText(this, "Succeeded", Toast.LENGTH_SHORT).show();

    }


    public void initializeAll() {
        StockDataModel mStockModel = ApplicationController.getStocksDB().getStocksData();
        if (stock == null) {
            stock = new VersionObject();
            stock.setMakeId(mStockModel.getMake());
            stock.setTransmission(mStockModel.getTransmission());
            stock.setMake(mStockModel.getMakeName());
            stock.setVersionid(mStockModel.getVersionId());
            stock.setVersionName(mStockModel.getVersion());
            stock.setModel(mStockModel.getModel());
            stock.setModelId(mStockModel.getModelID());
            stock.setFuelType(mStockModel.getFuelType());
        }
        if (selectedColor == null) {
            selectedColor = new BasicListItemModel(mStockModel.getColor(), mStockModel.getColor());
        }
        if (selectedCity == null) {
            selectedCity = new CityData();
            selectedCity.setCityName(mStockModel.getReg_city());
            selectedCity.setCityId(mStockModel.getReg_city());
        }
        if (selectedNumOwners == null) {
            selectedNumOwners = new BasicListItemModel(mStockModel.getOwners(), mStockModel.getOwners());
        }
        if (selectedShowroom == null) {
            selectedShowroom = new ShowroomData();
            selectedShowroom.setShowroomId(mStockModel.getDealership().split("~")[0]);
            selectedShowroom.setShowroomName(mStockModel.getDealership().split("~")[1]);
        }
    }


    private void updateStock() {

        if (stock != null) {

            if ((stock.getMakeId() != null) && !stock.getMakeId().isEmpty()) {

                if (stock.getVersionid() != null && !stock.getVersionid().isEmpty()) {

                    if (selectedColor != null) {

                        if ((selectedCity != null) || selectedNumOwners.getId().equals("0")) {

                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            if (deletedList != null && deletedList.size() > 0) {
                                StringBuilder imagePaths = new StringBuilder();
                                for (int x = 0; x < deletedList.size(); x++) {
                                    imagePaths.append(deletedList.get(x).getFilePath()).append(",");
                                }
                                imagePaths.deleteCharAt(imagePaths.lastIndexOf(","));
                                CommonUtils.makeDeleteRequest(getApplicationContext(), imagePaths.toString(), stockId);
                            }
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("fuel", stock.getFuelType());
                            params.put("car_id", stockId);
                            if ("Petrol".equalsIgnoreCase(stock.getFuelType())) {
                                String cngPresent = cngFitted.isChecked() ? "1" : "0";
                                params.put("cngFitted", cngPresent);
                            }
                            params.put("owner", selectedNumOwners.getId());
                            params.put("showroom", selectedShowroom.getShowroomId());
                            params.put("mmonth", ApplicationController.monthNameMap.get(stockYear.getText().toString().split("/")[0]));
                            params.put("myear", stockYear.getText().toString().split("/")[1]);
                            params.put("fuel", stock.getFuelType());
                            params.put("transmission", stock.getTransmission());
                            params.put("pricegaadi", stockPrice.getText().toString().trim().replace(",", ""));
                            if (sellToDealer.isChecked()) {
                                params.put("dealerprice", dealerPrice.getText().toString().trim().replace(",", ""));
                               /* params.put("dealermobile", dealerMobile.getText().toString().trim());
                                CommonUtils.setStringSharedPreference(StockAddActivity.this,
                                        Constants.UC_DEALER_MOBILE,
                                        dealerMobile.getText().toString().trim());*/
                                params.put("addtodealer", "1");

                                int dealerPrice = Integer.parseInt(this.dealerPrice.getText().toString().trim().replace(",", ""));
                                int stockPrice = Integer.parseInt(this.stockPrice.getText().toString().trim().replace(",", ""));
                                if (dealerPrice >= stockPrice) {
                                    CommonUtils.scrollTo(scrollView, this.dealerPrice);
                                    this.dealerPrice.setError("Dealer price should be less than stock price.");
                                    if (progressDialog != null) {
                                        progressDialog.dismiss();
                                    }
                                    return;
                                }
                            } else {
                                params.put("addtodealer", "0");
                            }
                            params.put("make", stock.getMake());
                            params.put("makeID", stock.getMakeId());
                            params.put("model", stock.getModel());
                            params.put("modelID", stock.getModelId());
                            params.put("version", stock.getVersionName());
                            params.put("versionID", stock.getVersionid());
                            params.put("km", kmsDriven.getText().toString().replace(",", ""));
                            if ("Other".equalsIgnoreCase(selectedColor.getValue())) {
                                params.put("colour", stockOtherColor.getText().toString().trim());
                            } else {
                                params.put("colour", selectedColor.getValue());
                            }
                            if (!selectedNumOwners.getId().equals("0")) {
                                params.put("regplace", registrationCity.getText().toString().trim());
                                params.put("regno", registrationNumber.getText().toString().trim().toUpperCase(Locale.ENGLISH));
                            }
                            int insuranceTypeid = insuranceType.getCheckedRadioButtonId();
                            if (insuranceTypeid == R.id.noInsurance) {
                                params.put("insuranceType", "No Insurance");
                            } else {
                                if (insuranceTypeid == R.id.thirdparty) {
                                    params.put("insuranceType", "Third Party");
                                } else {
                                    params.put("insuranceType", "Comprehensive");
                                }

                                //insuranceMonth = (insuranceMonth == 0) ? 1 : insuranceMonth;

                                params.put("month", ApplicationController.monthNameMap.get(insuranceValidYear.getText().toString().split("/")[0]));
                                params.put("year", insuranceValidYear.getText().toString().split("/")[1]);

                            }


                            selected = evenOddRadio.getCheckedRadioButtonId();
                            params.put("odd_even",selected!= -1?findViewById(selected).getTag().toString(): "");

                            int taxTypeId = taxType.getCheckedRadioButtonId();
                            if (taxTypeId == R.id.individual) {
                                params.put("taxType", "Individual");
                            } else {
                                params.put("taxType", "Corporate");
                            }

                            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                    Constants.CATEGORY_UPDATE_STOCK,
                                    Constants.CATEGORY_UPDATE_STOCK,
                                    Constants.ACTION_TAP,
                                    Constants.LABEL_UPDATE_STOCK_CTA,
                                    0
                            );
                            params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
                            params.put(Constants.METHOD_LABEL, Constants.STOCK_ADD_METHOD);
                            params.put(Constants.ACTION, Constants.STOCK_UPDATE);
                            params.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
                            params.put(Constants.DEALER_USERNAME, String.valueOf(CommonUtils.getStringSharedPreference(getApplicationContext(), Constants.UC_DEALER_USERNAME, "")));
                            params.put(Constants.UC_DEALER_USERNAME, CommonUtils.getStringSharedPreference(getApplicationContext(), Constants.UC_DEALER_USERNAME, ""));
                            // Log.d(TAG, "updateStock() called with:  "+ params.toString());
                            RetrofitRequest.stockUpdateRequest(params, new Callback<StockAddModel>() {
                                @Override
                                public void success(StockAddModel stockAddModel, retrofit.client.Response response) {
                                    progressDialog.dismiss();
                                    if ("T".equalsIgnoreCase(stockAddModel.getStatus())) {
                                        CommonUtils.showToast(StockAddActivity.this, "Stock updated successfully.", Toast.LENGTH_SHORT);
                                        //finish();
                                        ApplicationController.getEventBus().post("refresh");
                                        if (!CommonUtils.getBooleanSharedPreference(getApplicationContext(), ImageUploadService.SERVICE_RUNNING + stockId, false))
                                            startImageUpload(stockAddModel.getCarId(), stock.getMake() + " " + stock.getModel() + " " + stock.getVersionName());
                                        StockAddActivity.this.setResult(RESULT_OK);
                                        StockAddActivity.this.finish();
                                        StockAddActivity.this.finish();
                      /*Intent intent = new Intent(StockAddActivity.this, StockAddAdditionalActivity.class);
                      Bundle bundle = new Bundle();
                      bundle.putString(Constants.MAKE_ID, stock.getMakeId());
                      startActivityForResult(intent, Constants.STOCK_ADD_ADDITIONAL);*/
                                    } else {
                                        CommonUtils.showToast(StockAddActivity.this, stockAddModel.getErrorMessage(), Toast.LENGTH_SHORT);
                                    }

                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    progressDialog.dismiss();
                                    if (error.getCause() instanceof UnknownHostException) {
                                        CommonUtils.showToast(StockAddActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                                    } else {
                                        CommonUtils.showToast(StockAddActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                                    }
                                }
                            });



                           /* StockUpdateRequest stockAddRequest = new StockUpdateRequest(
                                    this,
                                    Request.Method.POST,
                                    Constants.getWebServiceURL(this),
                                    params,
                                    new Response.Listener<StockAddModel>() {
                                        @Override
                                        public void onResponse(StockAddModel response) {
                                            progressDialog.dismiss();
                                            if ("T".equalsIgnoreCase(response.getStatus())) {
                                                CommonUtils.showToast(StockAddActivity.this, "Stock updated successfully.", Toast.LENGTH_SHORT);
                                                //finish();
                                                ApplicationController.getEventBus().post("refresh");
                                                if (!CommonUtils.getBooleanSharedPreference(getApplicationContext(), ImageUploadService.SERVICE_RUNNING + stockId, false))
                                                    startImageUpload(response.getCarId(), stock.getMake() + " " + stock.getModel() + " " + stock.getVersionName());
                                                StockAddActivity.this.setResult(RESULT_OK);
                                                StockAddActivity.this.finish();
                      *//*Intent intent = new Intent(StockAddActivity.this, StockAddAdditionalActivity.class);
                      Bundle bundle = new Bundle();
                      bundle.putString(Constants.MAKE_ID, stock.getMakeId());
                      startActivityForResult(intent, Constants.STOCK_ADD_ADDITIONAL);*//*
                                            } else {
                                                CommonUtils.showToast(StockAddActivity.this, response.getErrorMessage(), Toast.LENGTH_SHORT);
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            progressDialog.dismiss();
                                            if (error.getCause() instanceof UnknownHostException) {
                                                CommonUtils.showToast(StockAddActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                                            } else {
                                                CommonUtils.showToast(StockAddActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                                            }
                                        }
                                    }
                            );

                            ApplicationController.getInstance().addToRequestQueue(stockAddRequest, Constants.TAG_ADD_STOCK, false, this);
*/
                        } else {
                            CommonUtils.scrollTo(scrollView, findViewById(R.id.registrationCity));
                            CommonUtils.shakeView(this, findViewById(R.id.registrationCity));
                            CommonUtils.showToast(this, "Please select a city from the list", Toast.LENGTH_SHORT);
                        }

                    } else {
                        CommonUtils.scrollTo(scrollView, findViewById(R.id.stockColor));
                        CommonUtils.shakeView(this, findViewById(R.id.stockColor));
                        CommonUtils.showToast(this, "Please select color from the list", Toast.LENGTH_SHORT);
                    }
                } else {
                    CommonUtils.scrollTo(scrollView, findViewById(R.id.version));
                    CommonUtils.shakeView(this, findViewById(R.id.version));
                    return;
                }

            } else {
                CommonUtils.scrollTo(scrollView, findViewById(R.id.makeModel));
                CommonUtils.shakeView(this, findViewById(R.id.makeModel));
                return;
            }

        } else {
            CommonUtils.scrollTo(scrollView, findViewById(R.id.makeModel));
            CommonUtils.shakeView(this, findViewById(R.id.makeModel));
            return;
        }
    }

    private void addStock() {
        if (stock != null) {

            if ((stock.getMakeId() != null) && !stock.getMakeId().isEmpty()) {

                if (stock.getVersionid() != null && !stock.getVersionid().isEmpty()) {

                    if (selectedColor != null) {

                        if ((selectedCity != null) || selectedNumOwners.getId().equals("0")) {

                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("fuel", stock.getFuelType());
                            if ("Petrol".equalsIgnoreCase(stock.getFuelType())) {
                                String cngPresent = cngFitted.isChecked() ? "1" : "0";
                                params.put("cngFitted", cngPresent);
                            }
                            params.put("owner", selectedNumOwners.getId());
                            params.put("showroom", selectedShowroom.getShowroomId());
                            if (isStockDatePickerCLicked) {
                                params.put("mmonth", ApplicationController.monthNameMap.get(stockYear.getText().toString().split("/")[0]));
                                params.put("myear", stockYear.getText().toString().split("/")[1]);
                            } else if (ApplicationController.getStocksDB().getStocksCount() > 0) {
                                params.put("mmonth", ApplicationController.getStocksDB().getStocksData().getStockMonth());
                                params.put("myear", ApplicationController.getStocksDB().getStocksData().getStockYear());
                            }
                            params.put("fuel", stock.getFuelType());
                            params.put("transmission", stock.getTransmission());
                            params.put("pricegaadi", stockPrice.getText().toString().trim().replace(",", ""));

                            if (sellToDealer.isChecked()) {
                                params.put("dealerprice", dealerPrice.getText().toString().trim().replace(",", ""));
                                /*params.put("dealermobile", dealerMobile.getText().toString().trim());

                                CommonUtils.setStringSharedPreference(StockAddActivity.this,
                                        Constants.UC_DEALER_MOBILE,
                                        dealerMobile.getText().toString().trim());*/

                                params.put("addtodealer", "1");

                                int dealerPrice = Integer.parseInt(this.dealerPrice.getText().toString().trim().replace(",", ""));
                                int stockPrice = Integer.parseInt(this.stockPrice.getText().toString().trim().replace(",", ""));
                                if (dealerPrice >= stockPrice) {
                                    CommonUtils.scrollTo(scrollView, this.dealerPrice);
                                    this.dealerPrice.setError("Dealer price should be less than stock price.");
                                    if (progressDialog != null) {
                                        progressDialog.dismiss();
                                    }
                                    return;
                                }

                            } else {
                                params.put("addtodealer", "0");

                            }

                            params.put("make", stock.getMake());
                            params.put("makeID", stock.getMakeId());
                            params.put("model", stock.getModel());
                            params.put("modelID", stock.getModelId());
                            params.put("version", stock.getVersionName());
                            params.put("versionID", stock.getVersionid());
                            params.put("km", kmsDriven.getText().toString().trim().replace(",", ""));
                            if ("Other".equalsIgnoreCase(selectedColor.getValue())) {
                                params.put("colour", stockOtherColor.getText().toString().trim());
                            } else {
                                params.put("colour", selectedColor.getValue());
                            }
                            if (!selectedNumOwners.getId().equals("0")) {
                                params.put("regplace", selectedCity.getCityName());
                                params.put("regno", registrationNumber.getText().toString().trim().toUpperCase(Locale.ENGLISH));
                            }
                            int insuranceTypeid = insuranceType.getCheckedRadioButtonId();
                            if (insuranceTypeid == R.id.noInsurance) {
                                params.put("insuranceType", "No Insurance");
                            } else {
                                if (insuranceTypeid == R.id.thirdparty) {
                                    params.put("insuranceType", "Third Party");
                                } else {
                                    params.put("insuranceType", "Comprehensive");
                                }
                                if (isInsuranceDateClicked) {
                                    // insuranceMonth = (insuranceMonth == 0) ? 1 : insuranceMonth;
                                    params.put("month", ApplicationController.monthNameMap.get(insuranceValidYear.getText().toString().split("/")[0]));
                                    params.put("year", insuranceValidYear.getText().toString().split("/")[1]);
                                } else if (ApplicationController.getStocksDB().getStocksCount() > 0) {
                                    params.put("month", ApplicationController.getStocksDB().getStocksData().getInsuranceMonth());
                                    params.put("year", ApplicationController.getStocksDB().getStocksData().getInsuranceYear());
                                }
                            }

                            selected = evenOddRadio.getCheckedRadioButtonId();
                            params.put("odd_even", selected != -1 ? findViewById(selected).getTag().toString() : "");
                           /* if (selected == R.id.even) {
                                params.put("odd_even", findViewById(selected).getTag().toString());
                            } else {
                                params.put("odd_even", findViewById(selected).getTag().toString());
                            }*/

                            int taxTypeId = taxType.getCheckedRadioButtonId();
                            if (taxTypeId == R.id.individual) {
                                params.put("taxType", "Individual");
                            } else {
                                params.put("taxType", "Corporate");
                            }


                            //showSaveStocksDataAlertDialog(params.toString());
                            GCLog.e("" + insuranceMonth);

                            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                    Constants.CATEGORY_ADD_STOCK,
                                    Constants.CATEGORY_ADD_STOCK,
                                    Constants.ACTION_TAP,
                                    Constants.LABEL_ADD_STOCK_CTA,
                                    0
                            );
                            params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
                            params.put(Constants.METHOD_LABEL, Constants.STOCK_ADD_METHOD);
                            params.put(Constants.ACTION, Constants.STOCK_ADD);
                            params.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
                            params.put(Constants.DEALER_USERNAME, String.valueOf(CommonUtils.getStringSharedPreference(getApplicationContext(), Constants.UC_DEALER_USERNAME, "")));

                            RetrofitRequest.stockAddRequest(params, new Callback<StockAddModel>() {
                                @Override
                                public void success(StockAddModel stockAddModel, retrofit.client.Response response) {
                                    progressDialog.dismiss();
                                    if ("T".equalsIgnoreCase(stockAddModel.getStatus())) {
                                        ApplicationController.getStocksDB().deletePreviousData();
                                        syncStocksData();
                                        CommonUtils.showToast(StockAddActivity.this, "Stock added successfully.", Toast.LENGTH_SHORT);
                                        //finish();
                                        startImageUpload(stockAddModel.getCarId(), stock.getMake() + " " + stock.getModel() + " " + stock.getVersionName());
                                        //StockAddActivity.this.setResult(RESULT_OK);
                                                /*Intent intent = new Intent(StockAddActivity.this, StockViewActivity.class);
                                                Bundle args = new Bundle();
                                                args.putString()
                                                startActivity(intent);*/
                                        StockAddActivity.this.finish();

                                    } else {
                                        CommonUtils.showToast(StockAddActivity.this, stockAddModel.getErrorMessage(), Toast.LENGTH_SHORT);
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    progressDialog.dismiss();
                                    if (error.getCause() instanceof UnknownHostException) {
                                        CommonUtils.showToast(StockAddActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                                    } else {
                                        CommonUtils.showToast(StockAddActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                                    }
                                }
                            });

                           /* StockAddRequest stockAddRequest = new StockAddRequest(
                                    this,
                                    Request.Method.POST,
                                    Constants.getWebServiceURL(this),
                                    params,
                                    new Response.Listener<StockAddModel>() {
                                        @Override
                                        public void onResponse(StockAddModel response) {
                                            progressDialog.dismiss();
                                            if ("T".equalsIgnoreCase(response.getStatus())) {
                                                ApplicationController.getStocksDB().deletePreviousData();
                                                CommonUtils.showToast(StockAddActivity.this, "Stock added successfully.", Toast.LENGTH_SHORT);
                                                //finish();
                                                startImageUpload(response.getCarId(), stock.getMake() + " " + stock.getModel() + " " + stock.getVersionName());
                                                //StockAddActivity.this.setResult(RESULT_OK);
                                                *//*Intent intent = new Intent(StockAddActivity.this, StockViewActivity.class);
                                                Bundle args = new Bundle();
                                                args.putString()
                                                startActivity(intent);*//*
                                                StockAddActivity.this.finish();

                                            } else {
                                                CommonUtils.showToast(StockAddActivity.this, response.getErrorMessage(), Toast.LENGTH_SHORT);
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            progressDialog.dismiss();
                                            if (error.getCause() instanceof UnknownHostException) {
                                                CommonUtils.showToast(StockAddActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                                            } else {
                                                CommonUtils.showToast(StockAddActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                                            }
                                        }
                                    }
                            );

                            ApplicationController.getInstance().addToRequestQueue(stockAddRequest, Constants.TAG_ADD_STOCK, false, this);

           */             } else {
                            CommonUtils.scrollTo(scrollView, findViewById(R.id.registrationCity));
                            CommonUtils.shakeView(this, findViewById(R.id.registrationCity));
                            CommonUtils.showToast(this, "Please select a city from the list", Toast.LENGTH_SHORT);
                        }

                    } else {
                        CommonUtils.scrollTo(scrollView, findViewById(R.id.stockColor));
                        CommonUtils.shakeView(this, findViewById(R.id.stockColor));
                        CommonUtils.showToast(this, "Please select color from the list", Toast.LENGTH_SHORT);
                    }

                } else {
                    CommonUtils.scrollTo(scrollView, findViewById(R.id.version));
                    CommonUtils.shakeView(this, findViewById(R.id.version));
                    CommonUtils.showToast(this, "Please select a version from given list", Toast.LENGTH_SHORT);
                    return;
                }

            } else {
                CommonUtils.scrollTo(scrollView, findViewById(R.id.makeModel));
                CommonUtils.shakeView(this, findViewById(R.id.makeModel));
                CommonUtils.showToast(this, "Please select a model from the list", Toast.LENGTH_SHORT);
                return;
            }

        } else {
            CommonUtils.scrollTo(scrollView, findViewById(R.id.makeModel));
            CommonUtils.shakeView(this, findViewById(R.id.makeModel));
            return;
        }
    }

    private void syncStocksData() {
        Intent intent = new Intent(getBaseContext(), SyncStocksService.class);
        getBaseContext().stopService(intent);
        getBaseContext().startService(intent);
    }

    private void startImageUpload(String carId, String makeModelVersion) {
        try {
            if (com.imageuploadlib.Utils.ApplicationController.selectedFiles != null)
                com.imageuploadlib.Utils.ApplicationController.selectedFiles.clear();


        } catch (Exception e) {
            Crashlytics.log(Log.ERROR, Constants.TAG, "error clearing selected files " + e.getMessage());

        } finally {
            ApplicationController.orderImages.clear();

        }
        StockImagesDB imagesDB = new StockImagesDB(getApplicationContext());
        imagesDB.deleteStockImageData(carId);
        imagesDB.deleteStockImageOrderData(carId);
        if (imagesPathList != null) {
            try {
                Boolean newImages = false;
                String mapOrder = "";
                if (imagesList == null) {
                    for (int i = 0; i < imagesPathList.size(); ++i) {
                        FileInfo fileInfo = imagesPathList.get(i);
                        ApplicationController.orderImages.add(i, "AA");
                        imagesDB.insertStockImagesRecords(new StockImageData(carId, fileInfo.getFilePath(), "S", makeModelVersion, "", CommonUtils.getIntSharedPreference(getApplicationContext(), Constants.UC_DEALER_ID, -1)));
                        newImages = true;
                        if (i == imagesPathList.size() - 1)
                            mapOrder += i + "";
                        else
                            mapOrder += i + ",";
                    }
                } else {
                    for (int i = 0, j = 0; i < imagesPathList.size(); ++i) {
                        if (!imagesList.contains(imagesPathList.get(i).getFilePath())) {
                            FileInfo fileInfo = imagesPathList.get(i);
                            int dealerId = CommonUtils.getIntSharedPreference(getApplicationContext(), Constants.UC_DEALER_ID, -1);
                            imagesDB.insertStockImagesRecords(new StockImageData(carId, fileInfo.getFilePath(), "S", makeModelVersion, "", dealerId));
                            ApplicationController.orderImages.add(i, "AA");
                            if (i == imagesPathList.size() - 1)
                                mapOrder += i + "";
                            else
                                mapOrder += i + ",";
                            j++;

                            newImages = true;
                        } else {
                            FileInfo fileInfo = imagesPathList.get(i);
                            String filePath = fileInfo.getFilePath();
                            int startImage = filePath.lastIndexOf("/");
                            String imageName = filePath.substring(startImage + 1);
                            ApplicationController.orderImages.add(i, imageName);
                        }
                    }
                }

                saveImagesOrder(carId, mapOrder);
                if (!newImages) {
                    sendOrderImages(getApplicationContext(), carId);
                }
                Intent intent = new Intent(this, ImageUploadService.class);
                intent.putExtra(Constants.CAR_ID, carId);
                startService(intent);
            } catch (Exception e) {
                GCLog.e("Some error occurred while upload photo " + e.getMessage());
            }
        }
    }

    private void saveImagesOrder(String stockId, String mapOrder) {
        String order = "";
        int size = ApplicationController.orderImages.size();
        for (int k = 0; k < size; k++) {
            order += ApplicationController.orderImages.get(k);
            if (k != size - 1) {
                order += ",";
            }
        }
        StockImagesDB imagesDB = new StockImagesDB(getApplicationContext());
        GCLog.e("SaveImagesOrder     Stock Id : " + stockId + " Order : " + order + " Map Order : " + mapOrder);
        imagesDB.insertStockImagesOrder(new StockImageOrderData(stockId, order, mapOrder));
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        View v = (View) failedView.getParent();
        if (failedView instanceof EditText) {
            failedView.requestFocus();
            ((EditText) failedView).setError(failedRule.getFailureMessage());
            //CommonUtils.scrollTo(scrollView, v);
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (group.getId()) {
            case R.id.insuranceType:
                mInsuranceGroup = "";
                if (checkedId == R.id.noInsurance) {
                    findViewById(R.id.insuranceTypeOptions).setVisibility(View.GONE);

                } else {
                    findViewById(R.id.insuranceTypeOptions).setVisibility(View.VISIBLE);
                }
                break;
            case R.id.taxType:
                mTaxGroup = "";
                break;
        }
    }

    private void saveStocksData() {

        StockDataModel stockDataModel = new StockDataModel();
        if (stock != null) {

            if ((stock.getMakeId() != null) && !stock.getMakeId().isEmpty()) {
                stockDataModel.setMake(stock.getMakeId());
                stockDataModel.setModel(stock.getModel());
                stockDataModel.setModelID(stock.getModelId());
                stockDataModel.setMakeName(stock.getMake());
                if (stock.getVersionid() != null && !stock.getVersionid().isEmpty()) {
                    stockDataModel.setVersion(stock.getVersionName());
                    stockDataModel.setVersionId(stock.getVersionid());
                }
                if (stock.getFuelType() != null)
                    stockDataModel.setFuelType(stock.getFuelType());
                if ("Petrol".equalsIgnoreCase(stock.getFuelType())) {
                    String cngPresent = cngFitted.isChecked() ? "1" : "0";
                    stockDataModel.setIsCng(cngPresent);
                }
                if (stock.getTransmission() != null) {
                    stockDataModel.setTransmission(stock.getTransmission());
                }
            }


        } else if (ApplicationController.getStocksDB().getStocksCount() > 0 && !isClearMake) {
            StockDataModel mStockDataModel = ApplicationController.getStocksDB().getStocksData();
            stockDataModel.setMake(mStockDataModel.getMake());
            stockDataModel.setModel(mStockDataModel.getModel());
            stockDataModel.setModelID(mStockDataModel.getModelID());
            stockDataModel.setVersion(mStockDataModel.getVersion());
            stockDataModel.setMakeName(mStockDataModel.getMakeName());
            stockDataModel.setVersionId(mStockDataModel.getVersionId());
            stockDataModel.setFuelType(mStockDataModel.getFuelType());
            stockDataModel.setTransmission(mStockDataModel.getTransmission());
            if ("Petrol".equalsIgnoreCase(mStockDataModel.getFuelType())) {
                String cngPresent = cngFitted.isChecked() ? "1" : "0";
                stockDataModel.setIsCng(cngPresent);
            }
        } else {
            if (ApplicationController.getStocksDB().getStocksCount() > 0) {
                StockDataModel mStockDataModel = ApplicationController.getStocksDB().getStocksData();
                stockDataModel.setMake("");
                stockDataModel.setModel("");
                stockDataModel.setModelID("");
                stockDataModel.setVersion("");
                stockDataModel.setMakeName("");
                stockDataModel.setVersionId("");
                stockDataModel.setFuelType("");
                stockDataModel.setTransmission("");
                stockDataModel.setColor("");
                stockDataModel.setHexcode("");
                if ("Petrol".equalsIgnoreCase(mStockDataModel.getFuelType())) {
                    String cngPresent = cngFitted.isChecked() ? "1" : "0";
                    stockDataModel.setIsCng(cngPresent);
                }

                fuelTypeLayout.setVisibility(View.GONE);
                findViewById(R.id.otherColorLayout).setVisibility(View.GONE);
                if (modelVersionAdapter != null) {
                    modelVersionAdapter = new ModelVersionAdapter(this, null);
                    modelVersionAdapter.notifyDataSetChanged();
                }

                if (colorAdapter != null) {
                    colorAdapter = new ColorAdapter(this, null);
                    colorAdapter.notifyDataSetChanged();
                    stockColor.setText("");
                }
            }
        }


        if (selectedColor != null) {
            if ("Other".equalsIgnoreCase(selectedColor.getValue())) {
                stockDataModel.setColor(stockOtherColor.getText().toString().trim());

            } else {
                stockDataModel.setColor(selectedColor.getValue());
                stockDataModel.setHexcode(selectedColor.getId());
            }
        }
        String str = stockYear.getText().toString();
        if (!str.isEmpty() && mStockDates.equals("") && str.contains("/")) {
            stockDataModel.setStockMonth(ApplicationController.monthNameMap.get(str.split("/")[0]));
            stockDataModel.setStockYear(str.split("/")[1]);

        } else {
            if (ApplicationController.getStocksDB().getStocksCount() > 0) {
                stockDataModel.setStockMonth(ApplicationController.getStocksDB().getStocksData().getStockMonth());
                stockDataModel.setStockYear(ApplicationController.getStocksDB().getStocksData().getStockYear());

            }
        }


        stockDataModel.setKm(kmsDriven.getText().toString().trim());
        if (selectedNumOwners != null) {
            stockDataModel.setOwners(selectedNumOwners.getId());
        }

        if (selectedCity != null) {
            stockDataModel.setReg_city(selectedCity.getCityName());
        }
        if (registrationCity.getText().toString().trim().equals("")) {
            stockDataModel.setReg_city(CommonUtils.getStringSharedPreference(StockAddActivity.this, Constants.UC_DEALER_CITY, ""));
        }

        stockDataModel.setReg_no(registrationNumber.getText().toString().trim());
        if (selectedShowroom != null) {
            stockDataModel.setDealership(selectedShowroom.getShowroomId() + "~" + selectedShowroom.getShowroomName());
        }
        stockDataModel.setStockprice(stockPrice.getText().toString().trim());
        if (mInsuranceGroup.equals("")) {
            int insuranceTypeid = insuranceType.getCheckedRadioButtonId();
            if (insuranceTypeid == R.id.noInsurance) {
                stockDataModel.setInsurancetype("No Insurance");
            } else {
                if (insuranceTypeid == R.id.thirdparty) {
                    stockDataModel.setInsurancetype("Third Party");
                } else {
                    stockDataModel.setInsurancetype("Comprehensive");
                }
                if (mInsuranceDates.equals("") && !insuranceValidYear.getText().toString().isEmpty() && insuranceValidYear.getText().toString().contains("/")) {
                    // insuranceMonth = (insuranceMonth == 0) ? 1 : insuranceMonth;
                    stockDataModel.setInsuranceMonth(ApplicationController.monthNameMap.get(insuranceValidYear.getText().toString().split("/")[0]));
                    stockDataModel.setInsuranceYear(insuranceValidYear.getText().toString().split("/")[1]);
                } else {
                    if (ApplicationController.getStocksDB().getStocksCount() > 0) {
                        stockDataModel.setInsuranceMonth(ApplicationController.getStocksDB().getStocksData().getInsuranceMonth());
                        stockDataModel.setInsuranceYear(ApplicationController.getStocksDB().getStocksData().getInsuranceYear());

                    }
                }

            }
        } else {

            if (ApplicationController.getStocksDB().getStocksCount() > 0) {
                stockDataModel.setInsurancetype(ApplicationController.getStocksDB().getStocksData().getInsurancetype());

            }


        }

        if (mTaxGroup.equals("")) {
            int taxTypeId = taxType.getCheckedRadioButtonId();
            if (taxTypeId == R.id.individual) {
                stockDataModel.setTax("Individual");
            } else {
                stockDataModel.setTax("Corporate");

            }
        } else {

            if (ApplicationController.getStocksDB().getStocksCount() > 0) {
                stockDataModel.setTax(ApplicationController.getStocksDB().getStocksData().getTax());

            }

        }
        if (evenOddRadio.getVisibility() == View.VISIBLE) {
            if (evenOddRadio.getCheckedRadioButtonId() == R.id.even) {
                stockDataModel.setEvenOdd("Even");
            } else if (evenOddRadio.getCheckedRadioButtonId() == R.id.odd) {
                stockDataModel.setEvenOdd("Odd");
            }
        }
        if (sellToDealer.isChecked()) {
            stockDataModel.setSell_to_dealer("1");
            //  stockDataModel.setDealermobile(dealerMobile.getText().toString().trim());
            stockDataModel.setDealerprice(dealerPrice.getText().toString().trim());
        }

        stockDataModel.setAddStockId(1);
        GCLog.e(stockDataModel.toString());
        ApplicationController.getStocksDB().deletePreviousData();
        ApplicationController.getStocksDB().saveStockData(stockDataModel);

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
            stockColor.setEnabled(false);
            fuelTypeLayout.setVisibility(View.GONE);
            findViewById(R.id.otherColorLayout).setVisibility(View.GONE);
            if (modelVersionAdapter != null) {
                modelVersionAdapter = new ModelVersionAdapter(this, null);
                modelVersionAdapter.notifyDataSetChanged();
                //mModelVersion.setEnabled(false);
            }

            if (colorAdapter != null) {
                colorAdapter = new ColorAdapter(this, null);
                colorAdapter.notifyDataSetChanged();
                stockColor.setText("");
                GradientDrawable bgShape = (GradientDrawable) color.getBackground();
                bgShape.setColor(Color.parseColor("#ffffff"));
            }
            selectedColor = null;


        } else if (((comingFrom.equals("") && ApplicationController.getStocksDB().getStocksCount() > 0))) {
            stockColor.setEnabled(false);
            mModelVersion.setClickable(false);
            mModelVersion.setEnabled(false);
            mModelVersion.setText("");
            fuelTypeLayout.setVisibility(View.GONE);
            if (modelVersionAdapter != null) {
                modelVersionAdapter = new ModelVersionAdapter(this, null);
                modelVersionAdapter.notifyDataSetChanged();

            }

            if (colorAdapter != null) {
                colorAdapter = new ColorAdapter(this, null);
                colorAdapter.notifyDataSetChanged();
                stockColor.setText("");
                GradientDrawable bgShape = (GradientDrawable) color.getBackground();
                bgShape.setColor(Color.parseColor("#ffffff"));
            }
            selectedColor = new BasicListItemModel("", "");
            stockColor.setText("");
            GradientDrawable bgShape = (GradientDrawable) color.getBackground();
            bgShape.setColor(Color.parseColor("#ffffff"));
        }

    }

    @Subscribe
    public void setMonthYearForPicker(SetMonthYearForPicker event) {
        if (event.getCurrentItem() == Constants.CODE_FOR_MFG_PICKER_DIALOG) {
            stockYear.setText(event.getText());
        } else {
            insuranceValidYear.setText(event.getText());
        }
    }

    private class RemoveErrorOnFocus implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus && view instanceof EditText) {
                ((EditText) view).setError(null);
            }
        }
    }
}
