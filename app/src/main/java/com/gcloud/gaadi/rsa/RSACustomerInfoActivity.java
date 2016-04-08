package com.gcloud.gaadi.rsa;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.BasicListItemAdapter;
import com.gcloud.gaadi.adapter.CityAdapter;
import com.gcloud.gaadi.adapter.MakeModelVersionAdapter;
import com.gcloud.gaadi.adapter.ModelVersionAdapter;
import com.gcloud.gaadi.annotations.Email;
import com.gcloud.gaadi.annotations.MobileNumber;
import com.gcloud.gaadi.annotations.Required;
import com.gcloud.gaadi.annotations.TextRule;
import com.gcloud.gaadi.annotations.rules.Rule;
import com.gcloud.gaadi.annotations.rules.Validator;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.DateType;
import com.gcloud.gaadi.constants.MakeModelType;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.events.CancelMakeEvent;
import com.gcloud.gaadi.events.SetMonthYearForPicker;
import com.gcloud.gaadi.model.BasicListItemModel;
import com.gcloud.gaadi.model.CityData;
import com.gcloud.gaadi.model.RSACustomerInfoResponseModel;
import com.gcloud.gaadi.model.VersionObject;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.rsa.RSAModel.RSACarDetailsModel;
import com.gcloud.gaadi.rsa.RSAModel.RSAPackage;
import com.gcloud.gaadi.ui.BaseActivity;
import com.gcloud.gaadi.ui.CustomAutoCompleteTextView;
import com.gcloud.gaadi.ui.CustomMaterialAutoCompleteTxtVw;
import com.gcloud.gaadi.ui.GCProgressDialog;
import com.gcloud.gaadi.ui.fourmob.datetimepicker.date.DatePickerDialog;
import com.gcloud.gaadi.ui.fourmob.datetimepicker.date.MonthYearPicker;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.gcloud.gaadi.utils.OverFlowMenu;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ankitgarg on 21/05/15.
 */
public class RSACustomerInfoActivity extends BaseActivity
        implements Validator.ValidationListener, View.OnClickListener, AdapterView.OnItemClickListener, DatePickerDialog.OnDateSetListener {

    //private RSACarDetailsModel carModel =new RSACarDetailsModel();
    BasicListItemModel selectedPackage;
    BasicListItemAdapter packageAdapter;
    LinearLayout cardViewLayout;
    boolean flag;
    HashMap<String, String> params;
    MonthYearPicker monthYearPickerDialog;
    @Required(order = 10, messageResId = R.string.error_customer_name_required)
    @TextRule(order = 11, minLength = 3, messageResId = R.string.error_customer_name_invalid)
    private EditText customerName;
    @Required(order = 5, messageResId = R.string.rsa_package)
    @TextRule(order = 6, messageResId = R.string.rsa_package)
    private EditText rsaPackage;
    @Required(order = 1, messageResId = R.string.error_make_model_required)
    @TextRule(order = 2, minLength = 1, messageResId = R.string.error_make_model_required)
    private CustomMaterialAutoCompleteTxtVw mMakeModel;
    @Required(order = 3, messageResId = R.string.error_model_version_required)
    @TextRule(order = 4, minLength = 1, messageResId = R.string.error_model_version_required)
    private EditText mModelVersion;
    @Required(order = 7, messageResId = R.string.error_model_version_required)
    private EditText regMonYear;
    @Required(order = 8, messageResId = R.string.error_registration_number_required)
    @TextRule(order = 9, minLength = 7, messageResId = R.string.error_registration_number_length)
    private EditText registrationNumber;
    @Required(order = 12, messageResId = R.string.error_customer_mobile_required)
    @MobileNumber(order = 13, minLength = 10, messageResId = R.string.error_customer_mobile_invalid)
    private EditText customerMobile;
    @Required(order = 14, messageResId = R.string.error_customer_email_required)
    @Email(order = 15, messageResId = R.string.error_customer_email_invalid)
    private EditText customerEmail;
    @Required(order = 16, messageResId = R.string.error_customer_city_required)
    @TextRule(order = 17, minLength = 3, messageResId = R.string.error_customer_city_invalid)
    private CustomAutoCompleteTextView customerCity;
    private CityAdapter mCityAdapter;
    /*private TextView submit;*/
    private Validator mValidator;
    private ScrollView scrollView;
    private RSACarDetailsModel carData;
    private GCProgressDialog progressDialog;
    private String source = "";
    private VersionObject stock;
    private CityData selectedCity;
    private ArrayList<RSAPackage> packageList;
    private MakeModelVersionAdapter makeModelVersionAdapter;
    private FilterQueryProvider cityFilterQueryProvider = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getCityCursor(constraint);
        }
    };
    private ModelVersionAdapter modelVersionAdapter;
    private String makeId;
    private String modelId;
    private String versionId;
    private String modelNameBlank = "";
    private String cityNameBlank = "";
    private FilterQueryProvider mmFilterQueryProvider = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getCursor(constraint);
        }
    };

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
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_RSA_CUSTOMER_INFO);

    }

    @Override
    protected void onResume() {
        super.onResume();
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_rsa_customer_info, frameLayout);
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        customerName = (EditText) findViewById(R.id.customerName);
        rsaPackage = (EditText) findViewById(R.id.rsaPackage);
        flag = getIntent().getBooleanExtra(Constants.RSA_FLAG, true);
        mMakeModel = (CustomMaterialAutoCompleteTxtVw) findViewById(R.id.makeModel);
        mModelVersion = (EditText) findViewById(R.id.version);
        regMonYear = (EditText) findViewById(R.id.regMonYear);
        registrationNumber = (EditText) findViewById(R.id.registrationNumber);
        customerMobile = (EditText) findViewById(R.id.mobileNumber);
        customerEmail = (EditText) findViewById(R.id.email);
        customerCity = (CustomAutoCompleteTextView) findViewById(R.id.registrationCity);
        cardViewLayout = (LinearLayout) findViewById(R.id.cardView);
        carData = (RSACarDetailsModel) getIntent().getSerializableExtra(Constants.MODEL_DATA);
        customerEmail.setOnClickListener(this);
        customerMobile.setOnClickListener(this);
        customerName.setOnClickListener(this);
        customerEmail.setOnClickListener(this);
        rsaPackage.setClickable(true);

        if (carData != null) {
            mMakeModel.setVisibility(View.GONE);
            mModelVersion.setVisibility(View.GONE);
            regMonYear.setVisibility(View.GONE);
            registrationNumber.setVisibility(View.GONE);
            cardViewLayout.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.stockModelVersion)).setText(carData.getModelName() + " " + carData.getCarVersion());

            ((TextView) findViewById(R.id.stockPrice)).setText(getString(R.string.inr, carData.getStockPrice()));
            if (carData.getRegistrationNumber() != null) {
                ((TextView) findViewById(R.id.reg_no)).setText("Reg no. - " + carData.getRegistrationNumber());
            }
            if (ApplicationController.makeLogoMap.get(Integer.parseInt(carData.getMakeid())) != 0) {
                ((ImageView) findViewById(R.id.makeLogo)).setImageDrawable(ContextCompat.getDrawable(RSACustomerInfoActivity.this, ApplicationController.makeLogoMap.get(Integer.parseInt(carData.getMakeid()))));
            } else {
                findViewById(R.id.makeLogo).setVisibility(View.INVISIBLE);
            }
            int colorCode = Color.parseColor("#FFFFFF");
            if (carData != null && carData.getHexCode() != null && !carData.getHexCode().isEmpty()) {
                try {
                    colorCode = Color.parseColor(carData.getHexCode());
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } finally {
                    findViewById(R.id.color).setBackgroundColor(colorCode);
                }
            } else {
                findViewById(R.id.color).setBackgroundColor(colorCode);
            }

        } else {
            mMakeModel.setOnClickListener(this);
            mModelVersion.setClickable(false);
            mModelVersion.setOnClickListener(this);
            makeModelVersionAdapter = new MakeModelVersionAdapter(this, null);
            mMakeModel.setType(MakeModelType.MAKE);
            mMakeModel.setThreshold(1);
            mMakeModel.setAdapter(makeModelVersionAdapter);
            makeModelVersionAdapter.setFilterQueryProvider(mmFilterQueryProvider);
            mMakeModel.setOnItemClickListener(this);
            mMakeModel.preventFocusReverseAnimation = true;
            // regMonYear.setClickable(true);
            regMonYear.setOnClickListener(this);
            regMonYear.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    regMonYear.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    regMonYear.setError(null);
                }
            });
            customerName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    customerName.setError(null);

                }

                @Override
                public void afterTextChanged(Editable s) {
                    customerName.setError(null);

                }
            });
            registrationNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    registrationNumber.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    registrationNumber.setError(null);

                }
            });
            mMakeModel.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mModelVersion.setText("");
                }

                @Override
                public void afterTextChanged(Editable s) {
                    mModelVersion.setText("");
                    mMakeModel.setError(null);

                }
            });
            rsaPackage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    rsaPackage.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    rsaPackage.setError(null);

                }
            });
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

            customerMobile.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    customerMobile.setError(null);

                }
            });
            customerCity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    customerCity.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    customerCity.setError(null);

                }
            });
        }
        mCityAdapter = new CityAdapter(this, null);
        customerCity.setAdapter(mCityAdapter);
        customerCity.setThreshold(1);
        customerCity.setOnItemClickListener(this);
        mCityAdapter.setFilterQueryProvider(cityFilterQueryProvider);

        source = getIntent().getStringExtra(Constants.SOURCE);
        packageList = (ArrayList<RSAPackage>) getIntent().getExtras().getSerializable(Constants.RSA_PACKAGE);
        ArrayList<BasicListItemModel> modelList = new ArrayList<>();
        for (RSAPackage entry : packageList) {
            BasicListItemModel listItem = new BasicListItemModel(entry.getPackageID() + "", entry.getPackageName());
            modelList.add(listItem);
        }
        modelList.trimToSize();
        rsaPackage.setOnClickListener(this);

        packageAdapter = new BasicListItemAdapter(this, modelList);
        if (savedInstanceState != null) {
            source = savedInstanceState.getString(Constants.SOURCE);
            selectedCity = (CityData) savedInstanceState.getSerializable(Constants.CITY_NAME);
        }
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                        return false;
                }
                return false;
            }
        });


    }

    @Override
    public void onBackPressed() {

        if (carData != null) {
            if (!customerName.getText().toString().equals("")
                    || !customerEmail.getText().toString().equals("")
                    || !customerMobile.getText().toString().equals("")
                    || !rsaPackage.getText().toString().equals("")
                    || !customerCity.getText().toString().equals("")) {
                alertDialogOnBackPress();
            } else {
                setResult(RESULT_CANCELED);
                finish();
                super.onBackPressed();
            }
        } else {
            if (!customerName.getText().toString().equals("")
                    || !customerEmail.getText().toString().equals("")
                    || !customerMobile.getText().toString().equals("")
                    || !rsaPackage.getText().toString().equals("")
                    || !customerCity.getText().toString().equals("")
                    || !registrationNumber.getText().toString().equals("")
                    || !regMonYear.getText().toString().equals("")
                    || !mMakeModel.getText().toString().equals("")
                    || !mModelVersion.getText().toString().equals("")) {
                alertDialogOnBackPress();
            } else {
                setResult(RESULT_CANCELED);
                finish();
                super.onBackPressed();
            }
        }
    }

    private void alertDialogOnBackPress() {
        new android.support.v7.app.AlertDialog.Builder(RSACustomerInfoActivity.this)
                .setTitle("RSA Form Data")
                .setCancelable(false)
                .setMessage(R.string.rsa_back_press_message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // FinanceDBHelper.deleteImages(CommonUtils.getStringSharedPreference(FinanceCollectImagesActivity.this, Constants.FINANCE_APP_ID, ""));
                        RSACustomerInfoActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onValidationSucceeded() {

        if (carData == null) {
            if (!modelNameBlank.equals(mMakeModel.getText().toString())) {
                onValidationFailed(mMakeModel, new Rule<View>(getString(R.string.error_make_model_invalid)) {
                    @Override
                    public boolean isValid(View view) {
                        return false;
                    }
                });
                return;
            }
            if (!(registrationNumber.getText().toString().trim().replace(" ", "").matches("[0-9a-zA-Z]+"))) {
                onValidationFailed(registrationNumber, new Rule<View>(getString(R.string.error_registration_number_special_characters)) {
                    @Override
                    public boolean isValid(View view) {
                        return false;
                    }
                });
                return;
            }
        }

        if (!(customerName.getText().toString().trim().replace(" ", "").matches("[a-zA-Z]+"))) {
            onValidationFailed(customerName, new Rule<View>(getString(R.string.error_customer_name_special_characters)) {
                @Override
                public boolean isValid(View view) {
                    return false;
                }
            });
            return;
        }

        if (!(customerMobile.getText().toString().startsWith("9", 0) || customerMobile.getText().toString().startsWith("8", 0) || customerMobile.getText().toString().startsWith("7", 0))) {
            onValidationFailed(customerMobile, new Rule<View>(getString(R.string.error_customer_mobile_invalid)) {
                @Override
                public boolean isValid(View view) {
                    return false;
                }
            });
            return;
        }
        if (selectedCity == null) {
            customerCity.setError(getString(R.string.error_customer_city_from_list));
            CommonUtils.scrollTo(scrollView, customerCity);
            //CommonUtils.shakeView(this, customerCity);
            return;
        }
        if (!cityNameBlank.equals(customerCity.getText().toString())) {
            onValidationFailed(customerCity, new Rule<View>(getString(R.string.error_customer_city_invalid)) {
                @Override
                public boolean isValid(View view) {
                    return false;
                }
            });
            return;
        }


            sendCustomerInformation();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        OverFlowMenu.OverFlowMenuText(this, "Issue RSA", 16, menu);
        OverFlowMenu.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.hideKeyboard(RSACustomerInfoActivity.this, view);
                CommonUtils.scrollTo(scrollView, customerCity);
                mValidator.validate();
            }
        });
        return true;
    }


    private void sendCustomerInformation() {
        progressDialog = new GCProgressDialog(this, this);
        progressDialog.setCancelable(false);
        progressDialog.show();
        params = new HashMap<>();


        if (carData != null) {

            params.put(Constants.REG_NO, carData.getRegistrationNumber());
            params.put(Constants.CAR_ID, carData.getStockId());
            params.put(Constants.RSA_MONTH, carData.getModelMonth());
            params.put(Constants.LABEL_SORT_BY_YEAR, carData.getModelYear());
            params.put(Constants.CAR_MAKE_ID, carData.getMakeid());
            params.put(Constants.MODEL_ID, carData.getModel());
            params.put(Constants.VERSION_ID, carData.getVersion());


        } else {
            params.put(Constants.CAR_ID, "");
            params.put(Constants.REG_NO, registrationNumber.getText().toString().trim().replace(" ", ""));
            params.put(Constants.RSA_MONTH, ApplicationController.monthNameMap.get(regMonYear.getText().toString().split("/")[0]));
            params.put(Constants.LABEL_SORT_BY_YEAR, regMonYear.getText().toString().split("/")[1]);
            params.put(Constants.CAR_MAKE_ID, makeId);
            params.put(Constants.MODEL_ID, modelId);
            params.put(Constants.VERSION_ID, versionId);
        }

        params.put(Constants.LEAD_NAME, customerName.getText().toString());
        params.put(Constants.MOBILE_NUM, customerMobile.getText().toString().trim().replace(" ", ""));
        params.put(Constants.EMAIL, customerEmail.getText().toString().trim().replace(" ", ""));
        params.put(Constants.RSA_PACKAGE_NAME, rsaPackage.getText().toString());
        params.put(Constants.UCV_CITY, customerCity.getText().toString());
        params.put(Constants.METHOD_LABEL, Constants.ISSUE_RSA);

        RetrofitRequest.postRSACustomerData(params, new Callback<RSACustomerInfoResponseModel>() {
            @Override
            public void success(RSACustomerInfoResponseModel rsaCustomerInfoResponseModel, Response response) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if ("T".equalsIgnoreCase(rsaCustomerInfoResponseModel.getStatus())) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    ApplicationController.getInstance().getGAHelper().sendEvent(
                            GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_ISSUE_RSA,
                            Constants.CATEGORY_ISSUE_RSA,
                            Constants.ACTION_TAP,
                            Constants.LABEL_ISSUE_RSA_COMPLETED + " - " + source,
                            0
                    );
                    new android.support.v7.app.AlertDialog.Builder(RSACustomerInfoActivity.this)
                            .setTitle("RSA ID Issued")
                            .setCancelable(false)
                            .setMessage("RSA is issued successfully.Your RSA ID is " + rsaCustomerInfoResponseModel.getRsaId())
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                    // if(carData == null) {
                                       /* Intent intent = new Intent(RSACustomerInfoActivity.this, RSADashboardActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        startActivity(intent);*/
                                    setResult(RESULT_OK);
                                    RSACustomerInfoActivity.this.finish();

                                }
                            }).create().show();


                } else {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }

                    CommonUtils.showToast(RSACustomerInfoActivity.this,
                            rsaCustomerInfoResponseModel.getMessage(), Toast.LENGTH_SHORT);

                }


            }

            @Override
            public void failure(RetrofitError error) {

                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                CommonUtils.showErrorToast(RSACustomerInfoActivity.this, error, Toast.LENGTH_SHORT);

            }
        });

    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {

        if (failedView instanceof EditText) {

            failedView.requestFocus();
            ((EditText) failedView).setError(failedRule.getFailureMessage());

        } else if (failedView instanceof TextView) {
            failedView.requestFocus();
            ((TextView) failedView).setError(failedRule.getFailureMessage());

        } else if (failedView instanceof MaterialAutoCompleteTextView) {

            failedView.requestFocus();
            ((MaterialAutoCompleteTextView) failedView).setError(failedRule.getFailureMessage());

        } /*else {
            CommonUtils.showToast(this, failedRule.getFailureMessage(), Toast.LENGTH_SHORT);
        }*/

        CommonUtils.scrollTo(scrollView, failedView);
        // CommonUtils.shakeView(this, failedView);

    }

    @Override
    public void onViewValidationSucceeded(View succeededView) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rsaPackage:
                formList();
                break;
            case R.id.version:

                final ListPopupWindow popupWindow = new ListPopupWindow(this);
                Cursor cursor1 = null;
               /* if (ApplicationController.getStocksDB().getStocksCount() > 0) {*/
                if (makeId != null && modelId != null) {
                    if (!("").equals(makeId) && !("").equals(modelId)) {
                        cursor1 = ApplicationController.getMakeModelVersionDB().
                                getVersionForMakeModelfromDB(makeId, modelId);
                    }
                } else {
                    mModelVersion.setClickable(false);
                    mModelVersion.setEnabled(false);
                }

                // }


                modelVersionAdapter = new ModelVersionAdapter(this, cursor1);
                popupWindow.setModal(true);
                popupWindow.setAnchorView(findViewById(R.id.version));
                popupWindow.setAdapter(modelVersionAdapter);
                popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
                        String version = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.VERSIONNAME));
                        versionId = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.VERSIONID));
                        String fuelType = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.FUEL_TYPE));
                        String transmissionType = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.TRANSMISSION_TYPE));
                        String modelVersion = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MMV_COLUMN_MODEL_VERSION));
                        String makeModelVersion = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MMV_COLUMN_MAKE_MODEL_VERSION));
                        mModelVersion.setText(version);
                        popupWindow.dismiss();
                    }
                });
                mModelVersion.post(new Runnable() {
                    @Override
                    public void run() {
                        popupWindow.show();
                    }
                });


                break;

            case R.id.regMonYear:
                CommonUtils.hideKeyboard(this, regMonYear);
                CommonUtils.scrollTo(scrollView, regMonYear);
                monthYearPickerDialog = new MonthYearPicker(Constants.CODE_FOR_RSA_PICKER_DIALOG);
                monthYearPickerDialog.show(getFragmentManager(), "MonthYearPickerDialog");
                monthYearPickerDialog.setListener(RSACustomerInfoActivity.this);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getAdapter() instanceof MakeModelVersionAdapter) {


            Cursor cursor = (Cursor) mMakeModel.getAdapter().getItem(position);

            int rowid = cursor.getInt(cursor.getColumnIndex(MakeModelVersionDB.ID));
            makeId = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MAKEID));
         /*String make = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MAKENAME));
*/
            modelId = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MODELID));
            String modelName = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MODELNAME));
            //String makeModel = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MMV_COLUMN_MAKE_MODEL));
            modelNameBlank = modelName;
            mMakeModel.setText(modelName);

            mModelVersion.setClickable(true);
            mModelVersion.setEnabled(true);
            mMakeModel.setSelection(mMakeModel.getText().length());
            mMakeModel.setCompoundDrawablesWithIntrinsicBounds(
                    ApplicationController.makeLogoMap.get(Integer.parseInt(makeId)),
                    0,
                    R.drawable.close_layer_dark,
                    0);
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mMakeModel.getWindowToken(), 0);


        } else if (parent.getAdapter() instanceof CityAdapter) {
            Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
            selectedCity = new CityData();
            selectedCity.setCityId(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.CITY_ID)));
            selectedCity.setCityName(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.CITY_NAME)));
            selectedCity.setStateId(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.STATE_ID)));
            selectedCity.setRegionId(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.REGION_ID)));
            cityNameBlank = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.CITY_NAME));

            customerCity.setText(selectedCity.getCityName());

            customerCity.setSelection(customerCity.getText().length());
            GCLog.e("selected City: " + selectedCity.toString());

        }
    }

    @Subscribe
    public void setMonthYearVal(SetMonthYearForPicker event) {
        if (event.getCurrentItem() == Constants.CODE_FOR_RSA_PICKER_DIALOG) {
            regMonYear.setText(event.getText());
        }

    }

    private void formList() {
        CommonUtils.hideKeyboard(RSACustomerInfoActivity.this, rsaPackage);
        CommonUtils.scrollTo(scrollView, rsaPackage);

       /* InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mMakeModel.getWindowToken(), 0);*/


        final ListPopupWindow rsaPackagePopupWindow = new ListPopupWindow(this);
        rsaPackagePopupWindow.setAdapter(packageAdapter);
        rsaPackagePopupWindow.setModal(true);
        rsaPackagePopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
        rsaPackagePopupWindow.setAnchorView(findViewById(R.id.rsaPackage));
        rsaPackagePopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
        rsaPackagePopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                rsaPackage.setError(null);
                selectedPackage = (BasicListItemModel) parent.getAdapter().getItem(position);
                rsaPackage.setText(selectedPackage.getValue());
                rsaPackagePopupWindow.dismiss();
            }
        });
        rsaPackage.post(new Runnable() {
            @Override
            public void run() {
                rsaPackagePopupWindow.show();

            }
        });


    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {

    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day, DateType dateType) {

    }

    @Subscribe
    public void onMakeCancelEvent(CancelMakeEvent event) {
        boolean isClearMake = true;

        mModelVersion.setClickable(false);
        mModelVersion.setEnabled(false);
        mModelVersion.setText("");

        if (modelVersionAdapter != null) {
            modelVersionAdapter = new ModelVersionAdapter(this, null);
            modelVersionAdapter.notifyDataSetChanged();


        }
    }

}
