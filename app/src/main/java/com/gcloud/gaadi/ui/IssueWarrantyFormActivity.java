package com.gcloud.gaadi.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.BasicListItemAdapter;
import com.gcloud.gaadi.adapter.CityAdapter;
import com.gcloud.gaadi.annotations.FirstCharValidation;
import com.gcloud.gaadi.annotations.Required;
import com.gcloud.gaadi.annotations.TextRule;
import com.gcloud.gaadi.annotations.rules.Rule;
import com.gcloud.gaadi.annotations.rules.Validator;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.DateType;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.BasicListItemModel;
import com.gcloud.gaadi.model.CertificationDetailModel;
import com.gcloud.gaadi.model.CertificationDetailedData;
import com.gcloud.gaadi.model.CertifiedCarData;
import com.gcloud.gaadi.model.CityData;
import com.gcloud.gaadi.model.CityModel;
import com.gcloud.gaadi.model.FollowupDate;
import com.gcloud.gaadi.model.IssueWarrantyModel;
import com.gcloud.gaadi.model.PackageList;
import com.gcloud.gaadi.model.ViewCertificationCarsWarrantyInput;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.fourmob.datetimepicker.date.DatePickerDialog;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

public class IssueWarrantyFormActivity extends BaseActivity implements
        View.OnClickListener, DatePickerDialog.OnDateSetListener,
        OnNoInternetConnectionListener, Validator.ValidationListener, OnItemClickListener {

    public static final String DATEPICKER_TAG = "datepicker";
    ImageView makeLogo;
    // TextView modelVersion;
    String idAndDays, packID;
    Button retry;
    TextView wtstartDate, wtexpiredate/*, wtsaledate*/;
    //TextView wtregnum;
    CertifiedCarData mCertifiedCarData;
    Validator mValidator;
    LinearLayout footerLayout;
    GCProgressDialog progressDialog;
    @TextRule(order = 8, minLength = 1, messageResId = R.string.error_registration_city_required)
    CustomMaterialAutoCompleteTxtVw registrationCity;
    @TextRule(order = 1, minLength = 1, messageResId = R.string.warranty_type_required)
    private EditText wt_warrantytype;
    private CityData selectedCity;
    private boolean warrantyIssued;

    private ProgressBar progressBar;
    private BasicListItemModel selectedPackage;
    private CertificationDetailedData mCerifiedData;
    private BasicListItemAdapter budgetAdapter;
    private ArrayList<BasicListItemModel> pckgList = new ArrayList<BasicListItemModel>();
    private LayoutInflater mInflater;
    private DatePickerDialog datePickerDialog;
    @Required(order = 3, messageResId = R.string.enter_customer_name)
    private EditText cusName;
    private EditText price;
    private CityAdapter cityAdapter;
    private ArrayList<CityData> cityList;
    @Required(order = 2, messageResId = R.string.enter_odometer)
    private EditText odometerReading;
    @TextRule(order = 4, minLength = 10, maxLength = 10, messageResId = R.string.error_customer_mobile_number)
    @FirstCharValidation(order = 5, notAllowedFirstChars = "0,1,2,3,4,5,6", messageResId = R.string.error_mobile_number_invalid)
    private EditText custMobileNumber;
    @Required(order = 6, messageResId = R.string.enter_customer_email)
    private EditText custEmailAddress;
    @Required(order = 7, messageResId = R.string.enter_customer_address)
    private EditText custaddress;
    private FilterQueryProvider cityFilterQueryProvider = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getCityCursor(constraint);
        }
    };

    private android.support.v7.app.AlertDialog confirmationDialog;

    @Override
    public void onBackPressed() {
        if (warrantyIssued) {
            setResult(RESULT_OK);

        } else {
            setResult(RESULT_CANCELED);

        }
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = getLayoutInflater();
        progressDialog = new GCProgressDialog(this, this, getString(R.string.please_wait));

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        getLayoutInflater().inflate(R.layout.issuewarrantyform_layout, frameLayout);
        mCertifiedCarData = (CertifiedCarData) this.getIntent()
                .getSerializableExtra(Constants.MODEL_DATA);
        initializeViews();
        makeIssuedWarrantyRequest(true);
    }

    private void initializeViews() {
        final Calendar calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), false);
        custaddress = (EditText) findViewById(R.id.custaddress);
        custMobileNumber = (EditText) findViewById(R.id.custMobileNumber);
        custEmailAddress = (EditText) findViewById(R.id.custEmailAddress);
        cusName = (EditText) findViewById(R.id.cusName);
        price = (EditText) findViewById(R.id.price);
        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                insertCommaIntoNumber(price, s, "#,##,##,###");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        odometerReading = (EditText) findViewById(R.id.odometerreading);
        odometerReading.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                insertCommaIntoNumber(odometerReading, s, "##,##,###");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        registrationCity = (CustomMaterialAutoCompleteTxtVw) findViewById(R.id.registrationCity);
        // wtregnum = (TextView) findViewById(R.id.wtregnum);
        // wtregnum.setText(mCertifiedCarData.getRegno());

        //setSubTittle(mCertifiedCarData.getModel() + " " + mCertifiedCarData.getCarversion(), "Reg No. " + mCertifiedCarData.getRegno());
        getSupportActionBar().setTitle(mCertifiedCarData.getModel() + " " + mCertifiedCarData.getCarversion());
        getSupportActionBar().setSubtitle("Reg No. " + mCertifiedCarData.getRegno());

        //modelVersion = (TextView) findViewById(R.id.stockModelVersion);
        //  modelVersion.setText(mCertifiedCarData.getModel() + " " + mCertifiedCarData.getCarversion());
        //makeLogo = (ImageView) findViewById(R.id.makeLogo);
        /*makeLogo.setImageResource
                (ApplicationController.makeLogoMap.get(mCertifiedCarData.getMake_id()));
       */
        wt_warrantytype = (EditText) findViewById(R.id.wt_warrantytype);
        wtstartDate = (TextView) findViewById(R.id.wtstartDate);
        wtexpiredate = (TextView) findViewById(R.id.wtexpiredate);
        //wtsaledate = (TextView) findViewById(R.id.wtsaledate);
        footerLayout = (LinearLayout) findViewById(R.id.footerLayout);
        footerLayout.setOnClickListener(this);
        /*wtsaledate.setOnClickListener(this);*/
        setCurrentDate();

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

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_BUYER_LEAD_DETAIL);
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

    private void setExpiryDate(String days) {
        DateTime dateTime = DateTime.now();
        dateTime = dateTime.plusDays(Integer.parseInt(days));
        wtexpiredate.setText(dateTime.dayOfMonth().getAsShortText() + "/"
                + dateTime.monthOfYear().getAsShortText() + "/"
                + dateTime.year().getAsText());
    }

    private void setCurrentDate() {

        DateTime dateTime = DateTime.now();

        wtstartDate.setText(dateTime.dayOfMonth().getAsShortText() + "/" + dateTime.monthOfYear().getAsShortText() + "/" + dateTime.year().getAsString());
        //wtsaledate.setText(dateTime.dayOfMonth().getAsShortText() + "/" + dateTime.monthOfYear().getAsShortText() + "/" + dateTime.year().getAsString());

    }

    private void makeIssuedWarrantyRequest(final boolean showFullPageError) {
        progressDialog.show();
        progressDialog.setCancelable(false);
        HashMap<String, String> params = new HashMap<String, String>();
        ViewCertificationCarsWarrantyInput certcarsWarrantyInput = new ViewCertificationCarsWarrantyInput();
        certcarsWarrantyInput.setApikey(Constants.API_KEY);
        certcarsWarrantyInput.setMethod(Constants.GETCERTIFICATION_METHOD);
        certcarsWarrantyInput.setOutput(Constants.API_RESPONSE_FORMAT);
//		certcarsWarrantyInput.setUsername("aditicars9233@gmail.com");
//		certcarsWarrantyInput.setNormal_password("hijklm");
        certcarsWarrantyInput.setUsername(CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_USERNAME, ""));
        certcarsWarrantyInput.setNormal_password(CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_PASSWORD, ""));
        certcarsWarrantyInput.setCertificateID(mCertifiedCarData
                .getCertificationID());
        Gson gson = new Gson();

        String request_string = gson.toJson(certcarsWarrantyInput,
                ViewCertificationCarsWarrantyInput.class);
        params.put(Constants.EVALUATIONDATA, request_string);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(gson.toJson(certcarsWarrantyInput,
                    ViewCertificationCarsWarrantyInput.class));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RetrofitRequest.certificationDetailRequest(jsonObject, new Callback<CertificationDetailModel>() {


            @Override
            public void success(CertificationDetailModel certificationDetailModel, retrofit.client.Response response) {
                if (certificationDetailModel.getStatus().equals("T")) {
                    mCerifiedData = certificationDetailModel.getCertificationData();
                    getPackageList(mCerifiedData, showFullPageError);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            IssueWarrantyFormActivity.this);
                    AlertDialog alertDialog = builder
                            .setTitle(R.string.sorry)
                            .setMessage(certificationDetailModel.getError())
                            .setPositiveButton(
                                    R.string.retry,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            makeIssuedWarrantyRequest(showFullPageError);
                                        }
                                    })
                            .setNegativeButton(
                                    R.string.issue_warranty_later,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            progressDialog.dismiss();
                                            IssueWarrantyFormActivity.this
                                                    .finish();
                                        }
                                    }).setCancelable(false).create();

                    if (!alertDialog.isShowing()
                            && !IssueWarrantyFormActivity.this
                            .isFinishing()) {
                        alertDialog.show();
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof UnknownHostException) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            IssueWarrantyFormActivity.this);
                    AlertDialog alertDialog = builder
                            .setTitle(R.string.network_connection_error)
                            .setMessage(R.string.network_connection_error_message)
                            .setPositiveButton(R.string.retry,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            makeIssuedWarrantyRequest(showFullPageError);
                                        }
                                    })
                            .setNegativeButton(
                                    R.string.issue_warranty_later,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            progressDialog.dismiss();
                                            IssueWarrantyFormActivity.this
                                                    .finish();
                                        }
                                    }).setCancelable(false).create();

                    if (!alertDialog.isShowing()
                            && !IssueWarrantyFormActivity.this
                            .isFinishing()) {
                        alertDialog.show();
                    }

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            IssueWarrantyFormActivity.this);
                    AlertDialog alertDialog = builder
                            .setTitle(R.string.sorry)
                            .setMessage(R.string.server_error)
                            .setPositiveButton(R.string.retry,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            makeIssuedWarrantyRequest(showFullPageError);
                                        }
                                    })
                            .setNegativeButton(
                                    R.string.issue_warranty_later,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            progressDialog.dismiss();
                                            IssueWarrantyFormActivity.this
                                                    .finish();
                                        }
                                    }).setCancelable(false).create();

                    if (!alertDialog.isShowing()
                            && !IssueWarrantyFormActivity.this
                            .isFinishing()) {
                        alertDialog.show();
                    }
                }
            }
        });

       /* CertificationDetailRequest stocksRequest = new CertificationDetailRequest(
                this,
                Request.Method.POST,
                Constants.getWarrantyWebServiceURL(this),
                params,
                new Response.Listener<CertificationDetailModel>() {
                    @Override
                    public void onResponse(CertificationDetailModel response) {

                        if (response.getStatus().equals("T")) {
                            mCerifiedData = response.getCertificationData();
                            getPackageList(mCerifiedData, showFullPageError);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    IssueWarrantyFormActivity.this);
                            AlertDialog alertDialog = builder
                                    .setTitle(R.string.sorry)
                                    .setMessage(response.getError())
                                    .setPositiveButton(
                                            R.string.retry,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    makeIssuedWarrantyRequest(showFullPageError);
                                                }
                                            })
                                    .setNegativeButton(
                                            R.string.issue_warranty_later,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    progressDialog.dismiss();
                                                    IssueWarrantyFormActivity.this
                                                            .finish();
                                                }
                                            }).setCancelable(false).create();

                            if (!alertDialog.isShowing()
                                    && !IssueWarrantyFormActivity.this
                                    .isFinishing()) {
                                alertDialog.show();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.getCause() instanceof UnknownHostException) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            IssueWarrantyFormActivity.this);
                    AlertDialog alertDialog = builder
                            .setTitle(R.string.network_connection_error)
                            .setMessage(R.string.network_connection_error_message)
                            .setPositiveButton(R.string.retry,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            makeIssuedWarrantyRequest(showFullPageError);
                                        }
                                    })
                            .setNegativeButton(
                                    R.string.issue_warranty_later,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            progressDialog.dismiss();
                                            IssueWarrantyFormActivity.this
                                                    .finish();
                                        }
                                    }).setCancelable(false).create();

                    if (!alertDialog.isShowing()
                            && !IssueWarrantyFormActivity.this
                            .isFinishing()) {
                        alertDialog.show();
                    }

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            IssueWarrantyFormActivity.this);
                    AlertDialog alertDialog = builder
                            .setTitle(R.string.sorry)
                            .setMessage(R.string.server_error)
                            .setPositiveButton(R.string.retry,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            makeIssuedWarrantyRequest(showFullPageError);
                                        }
                                    })
                            .setNegativeButton(
                                    R.string.issue_warranty_later,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            progressDialog.dismiss();
                                            IssueWarrantyFormActivity.this
                                                    .finish();
                                        }
                                    }).setCancelable(false).create();

                    if (!alertDialog.isShowing()
                            && !IssueWarrantyFormActivity.this
                            .isFinishing()) {
                        alertDialog.show();
                    }
                }
            }
        });

        ApplicationController.getInstance().addToRequestQueue(stocksRequest,
                Constants.TAG_STOCKS_LIST, true, this);*/

    }

    protected void getPackageList(
            final CertificationDetailedData rcertfiedmodel,
            final boolean showFullPageError) {

        HashMap<String, String> params = new HashMap<String, String>();
        ViewCertificationCarsWarrantyInput certcarsWarrantyInput = new ViewCertificationCarsWarrantyInput();
        certcarsWarrantyInput.setApikey(Constants.API_KEY);
        certcarsWarrantyInput.setMethod(Constants.GETWARRANTYPACKAGE);
        certcarsWarrantyInput.setOutput(Constants.API_RESPONSE_FORMAT);
//		certcarsWarrantyInput.setUsername("aditicars9233@gmail.com");
//		certcarsWarrantyInput.setNormal_password("hijklm");
        certcarsWarrantyInput.setUsername(CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_USERNAME, ""));
        certcarsWarrantyInput.setNormal_password(CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_PASSWORD, ""));
        certcarsWarrantyInput.setPackageID(rcertfiedmodel
                .getRecommendedPackage());
        Gson gson = new Gson();
        String request_string = gson.toJson(certcarsWarrantyInput,
                ViewCertificationCarsWarrantyInput.class);
        params.put(Constants.EVALUATIONDATA, request_string);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(gson.toJson(certcarsWarrantyInput,
                    ViewCertificationCarsWarrantyInput.class));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("InputString", params + "");
        RetrofitRequest.certificationDetailRequest(jsonObject, new Callback<CertificationDetailModel>() {
            @Override
            public void success(CertificationDetailModel certificationDetailModel, retrofit.client.Response response) {
                if (certificationDetailModel.getStatus().equals("T")) {

                    PackageList[] packageList = certificationDetailModel
                            .getPackageList();
                    setPackageSpinner(packageList);
                    setData(rcertfiedmodel);
                    makeCityRequest(showFullPageError);

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            IssueWarrantyFormActivity.this);
                    AlertDialog alertDialog = builder
                            .setTitle(R.string.sorry)
                            .setMessage(certificationDetailModel.getError())
                            .setPositiveButton(
                                    R.string.retry,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            makeIssuedWarrantyRequest(showFullPageError);
                                        }
                                    })
                            .setNegativeButton(
                                    R.string.issue_warranty_later,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            progressDialog.dismiss();
                                            IssueWarrantyFormActivity.this
                                                    .finish();
                                        }
                                    }).setCancelable(false).create();

                    if (!alertDialog.isShowing()
                            && !IssueWarrantyFormActivity.this
                            .isFinishing()) {
                        alertDialog.show();
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        IssueWarrantyFormActivity.this);
                AlertDialog alertDialog = builder
                        .setTitle(R.string.sorry)
                        .setMessage(R.string.server_error)
                        .setPositiveButton(R.string.retry,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        makeIssuedWarrantyRequest(showFullPageError);
                                    }
                                })
                        .setNegativeButton(
                                R.string.issue_warranty_later,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        progressDialog.dismiss();
                                        IssueWarrantyFormActivity.this
                                                .finish();
                                    }
                                }).setCancelable(false).create();

                if (!alertDialog.isShowing()
                        && !IssueWarrantyFormActivity.this
                        .isFinishing()) {
                    alertDialog.show();
                }

            }
        });
      /*  CertificationDetailRequest stocksRequest = new CertificationDetailRequest(
                this,
                Request.Method.POST,
                Constants.getWarrantyWebServiceURL(this),
                params,
                new Response.Listener<CertificationDetailModel>() {
                    @Override
                    public void onResponse(CertificationDetailModel response) {

                        if (response.getStatus().equals("T")) {

                            PackageList[] packageList = response
                                    .getPackageList();
                            setPackageSpinner(packageList);
                            setData(rcertfiedmodel);
                            makeCityRequest(showFullPageError);

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    IssueWarrantyFormActivity.this);
                            AlertDialog alertDialog = builder
                                    .setTitle(R.string.sorry)
                                    .setMessage(response.getError())
                                    .setPositiveButton(
                                            R.string.retry,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    makeIssuedWarrantyRequest(showFullPageError);
                                                }
                                            })
                                    .setNegativeButton(
                                            R.string.issue_warranty_later,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    progressDialog.dismiss();
                                                    IssueWarrantyFormActivity.this
                                                            .finish();
                                                }
                                            }).setCancelable(false).create();

                            if (!alertDialog.isShowing()
                                    && !IssueWarrantyFormActivity.this
                                    .isFinishing()) {
                                alertDialog.show();
                            }
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        IssueWarrantyFormActivity.this);
                AlertDialog alertDialog = builder
                        .setTitle(R.string.sorry)
                        .setMessage(R.string.server_error)
                        .setPositiveButton(R.string.retry,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        makeIssuedWarrantyRequest(showFullPageError);
                                    }
                                })
                        .setNegativeButton(
                                R.string.issue_warranty_later,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        progressDialog.dismiss();
                                        IssueWarrantyFormActivity.this
                                                .finish();
                                    }
                                }).setCancelable(false).create();

                if (!alertDialog.isShowing()
                        && !IssueWarrantyFormActivity.this
                        .isFinishing()) {
                    alertDialog.show();
                }
            }
        });

        ApplicationController.getInstance().addToRequestQueue(stocksRequest,
                Constants.TAG_STOCKS_LIST, true, this);*/

    }

    protected void makeCityRequest(final boolean showFullPageError) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.METHOD_LABEL, Constants.CITY_METHOD);
        params.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
        RetrofitRequest.makeCityRequest(params, new Callback<CityModel>() {
            @Override
            public void success(CityModel cityModel, retrofit.client.Response response) {
                if (cityModel.getStatus().equalsIgnoreCase("T")) {
                    progressDialog.cancel();
                    cityList = cityModel.getCityList();
                    cityAdapter = new CityAdapter(IssueWarrantyFormActivity.this, null);
                    registrationCity.setAdapter(cityAdapter);
                    registrationCity.setThreshold(1);
                    registrationCity.setOnItemClickListener(IssueWarrantyFormActivity.this);
                    cityAdapter.setFilterQueryProvider(cityFilterQueryProvider);


                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(IssueWarrantyFormActivity.this);
                    AlertDialog alertDialog = builder.setTitle(R.string.sorry)
                            .setMessage(cityModel.getErrorMessage())
                            .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    makeIssuedWarrantyRequest(showFullPageError);
                                }
                            })
                            .setNegativeButton(R.string.issue_warranty_later, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    progressDialog.dismiss();
                                    IssueWarrantyFormActivity.this.finish();
                                }
                            })
                            .setCancelable(false)
                            .create();

                    if (!alertDialog.isShowing() && !IssueWarrantyFormActivity.this.isFinishing()) {
                        alertDialog.show();
                    }
                }
            }


            @Override
            public void failure(RetrofitError error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(IssueWarrantyFormActivity.this);
                AlertDialog alertDialog = builder.setTitle(R.string.sorry)
                        .setMessage(R.string.server_error)
                        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                makeCityRequest(showFullPageError);
                            }
                        })
                        .setNegativeButton(R.string.add_stock_later, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.dismiss();
                                IssueWarrantyFormActivity.this.finish();
                            }
                        })
                        .setCancelable(false)
                        .create();

                if (!alertDialog.isShowing() && !IssueWarrantyFormActivity.this.isFinishing()) {
                    alertDialog.show();
                }

            }

        });

      /*  CityRequest cityRequest = new CityRequest(
                this,
                Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<CityModel>() {
                    @Override
                    public void onResponse(CityModel response) {
                        if (response.getStatus().equalsIgnoreCase("T")) {
                            progressDialog.cancel();
                            cityList = response.getCityList();
                            cityAdapter = new CityAdapter(IssueWarrantyFormActivity.this, null);
                            registrationCity.setAdapter(cityAdapter);
                            registrationCity.setThreshold(1);
                            registrationCity.setOnItemClickListener(IssueWarrantyFormActivity.this);
                            cityAdapter.setFilterQueryProvider(cityFilterQueryProvider);


                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(IssueWarrantyFormActivity.this);
                            AlertDialog alertDialog = builder.setTitle(R.string.sorry)
                                    .setMessage(response.getErrorMessage())
                                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            makeIssuedWarrantyRequest(showFullPageError);
                                        }
                                    })
                                    .setNegativeButton(R.string.issue_warranty_later, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            progressDialog.dismiss();
                                            IssueWarrantyFormActivity.this.finish();
                                        }
                                    })
                                    .setCancelable(false)
                                    .create();

                            if (!alertDialog.isShowing() && !IssueWarrantyFormActivity.this.isFinishing()) {
                                alertDialog.show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(IssueWarrantyFormActivity.this);
                        AlertDialog alertDialog = builder.setTitle(R.string.sorry)
                                .setMessage(R.string.server_error)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        makeCityRequest(showFullPageError);
                                    }
                                })
                                .setNegativeButton(R.string.add_stock_later, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        progressDialog.dismiss();
                                        IssueWarrantyFormActivity.this.finish();
                                    }
                                })
                                .setCancelable(false)
                                .create();

                        if (!alertDialog.isShowing() && !IssueWarrantyFormActivity.this.isFinishing()) {
                            alertDialog.show();
                        }

                    }
                });

        ApplicationController.getInstance().addToRequestQueue(cityRequest, Constants.TAG_CITY_REQUEST, showFullPageError, this);
*/    }

    private Cursor getCityCursor(CharSequence constraint) {
        return ApplicationController.getMakeModelVersionDB().getCityRecords(constraint);
    }


    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    protected void setPackageSpinner(PackageList[] packageList) {
        for (int i = 0; i < packageList.length; i++) {
            BasicListItemModel listItem = new BasicListItemModel(packageList[i].getPack_id() + "~"
                    + packageList[i].getPack_days(), packageList[i].getPack_short_name() + "-"
                    + packageList[i].getPack_short_type());

            pckgList.add(listItem);
        }
        pckgList.trimToSize();
        budgetAdapter = new BasicListItemAdapter(this, pckgList);
        wt_warrantytype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ListPopupWindow packagesPopupWindow = new ListPopupWindow(IssueWarrantyFormActivity.
                        this);
                packagesPopupWindow.setAdapter(budgetAdapter);
                packagesPopupWindow.setModal(true);
                packagesPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
                packagesPopupWindow.setAnchorView(findViewById(R.id.wt_warrantytype));
                packagesPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
                packagesPopupWindow
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent,
                                                    View view, int position, long id) {
                                selectedPackage = (BasicListItemModel) parent
                                        .getAdapter().getItem(position);
                                wt_warrantytype.setText(selectedPackage.getValue());
                                idAndDays = selectedPackage.getId();
                                setExpiryDate(idAndDays.split("~")[1]);
                                packID = idAndDays.split("~")[0];

                                packagesPopupWindow.dismiss();
                            }
                        });
                wt_warrantytype.post(new Runnable() {
                    @Override
                    public void run() {
                        packagesPopupWindow.show();
                    }
                });
            }
        });
    }


    protected void setData(CertificationDetailedData response) {


        getSupportActionBar().setSubtitle("Reg No. " + response.getRegno());
    }

    private void showDatePickerDialog() {
        datePickerDialog.setYearRange(
                Calendar.getInstance().get(Calendar.YEAR), Calendar
                        .getInstance().get(Calendar.YEAR) + 1);
        datePickerDialog.setCloseOnSingleTapDay(false);
        datePickerDialog.setOnDateSetListener(this);
        datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);

    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year,
                          int month, int day) {

        // TODO apply logic to check whether date chosen is greater than today's
        // date.
        FollowupDate followupDate = new FollowupDate(year, month, day);
        /*wtsaledate.setText(day + "/" + followupDate.getMonthName()
                + "/" + year);*/

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
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.wtsaledate:

			showDatePickerDialog();
                break;*/
            case R.id.footerLayout:
                v.setEnabled(false);
                mValidator.validate();
                break;
            case R.id.retry:

                makeIssuedWarrantyRequest(true);
                break;

            case R.id.cancel:
                if (confirmationDialog != null) {
                    confirmationDialog.dismiss();
                }
                findViewById(R.id.footerLayout).setEnabled(true);
                break;

            case R.id.ok:
                if (confirmationDialog != null) {
                    confirmationDialog.dismiss();
                }
                issuewarranty();
                break;
        }
    }

    @Override
    public void onNoInternetConnection(NetworkEvent networkEvent) {
        if (networkEvent.getNetworkError() == NetworkEvent.NetworkError.NO_INTERNET_CONNECTION) {
            showNetworkErrorDialog(networkEvent);
        }
    }

    @Override
    public void onValidationSucceeded() {
        //issuewarranty();

        if (cityAdapter != null
                && cityAdapter.getCursor() != null
                && !(cityAdapter.getCursor().getCount() > 0)) { // Adapter will have data for enlisted city
            onValidationFailed(registrationCity, new Rule<View>(getString(R.string.error_registration_city_required)) {
                @Override
                public boolean isValid(View view) {
                    return false;
                }
            });
            return;
        }


        if (idAndDays == null || idAndDays.isEmpty()) {
            issuewarranty();
        } else {
            createConfirmationDialog(wt_warrantytype.getText().toString().trim(),
                    idAndDays.substring(idAndDays.lastIndexOf("~") + 1) + " days");
        }

    }

    private void createConfirmationDialog(String warrantyType, String numberOfDays) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.fragment_warranty_confirmation, null);
        String message = "You are about to issue\n"
                + warrantyType + "\nwarranty valid for " + numberOfDays + ".\n\n" + "Do you wish to continue?";
        Spannable string = new SpannableString(message);
        string.setSpan(new AbsoluteSizeSpan(16, true),
                message.indexOf(warrantyType),
                (message.indexOf(warrantyType) + warrantyType.length()),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(new StyleSpan(Typeface.BOLD),
                message.indexOf(warrantyType),
                (message.indexOf(warrantyType) + warrantyType.length()),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(new AbsoluteSizeSpan(16, true),
                message.indexOf(numberOfDays),
                (message.indexOf(numberOfDays) + numberOfDays.length()),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(new StyleSpan(Typeface.BOLD),
                message.indexOf(numberOfDays),
                (message.indexOf(numberOfDays) + numberOfDays.length()),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView) dialogLayout.findViewById(R.id.title)).setText(string);
        dialogLayout.findViewById(R.id.cancel).setOnClickListener(this);
        dialogLayout.findViewById(R.id.ok).setOnClickListener(this);
        confirmationDialog = new android.support.v7.app.AlertDialog.Builder(this)
                .setView(dialogLayout)
                .create();
        confirmationDialog.getWindow().getAttributes().windowAnimations = R.style.AnimationDialog;
        confirmationDialog.show();
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        findViewById(R.id.footerLayout).setEnabled(true);
        if (failedView instanceof EditText) {
           /* Toast.makeText(this, failedRule.getFailureMessage(),
                    Toast.LENGTH_SHORT).show();*/
            ((EditText) failedView).setError(failedRule.getFailureMessage());
            failedView.requestFocus();
            CommonUtils.scrollTo((ScrollView) findViewById(R.id.scrollView), failedView);
        }

    }

    @Override
    public void onViewValidationSucceeded(View succeededView) {
        findViewById(R.id.footerLayout).setEnabled(true);
    }

    private void issuewarranty() {
        findViewById(R.id.footerLayout).setEnabled(true);
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            IssueWarrantyModel issueWarrantyModel = new IssueWarrantyModel();
            issueWarrantyModel.setApikey(Constants.API_KEY);
            issueWarrantyModel.setMethod(Constants.ISSUE_CERTIFIESWARRANTY);
            issueWarrantyModel.setOutput(Constants.API_RESPONSE_FORMAT);
            issueWarrantyModel.setUsername(CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_USERNAME, ""));
            issueWarrantyModel.setNormal_password(CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_PASSWORD, ""));

//			issueWarrantyModel.setUsername("aditicars9233@gmail.com");
//			issueWarrantyModel.setNormal_password("hijklm");


            issueWarrantyModel.setCertificationID(mCerifiedData
                    .getCertificationID());
            issueWarrantyModel.setCustName(cusName.getText().toString().trim());
            issueWarrantyModel.setCustAddress(custaddress.getText().toString()
                    .trim());
            issueWarrantyModel.setCustMobile(custMobileNumber.getText()
                    .toString().trim());

            issueWarrantyModel.setCustTelePhone("");

            ///

            issueWarrantyModel.setPrice(price.getText().toString().trim());
            issueWarrantyModel.setOdometerreading(odometerReading.getText().toString().trim().replace(",", ""));
            issueWarrantyModel.setCity(registrationCity.getText().toString());

            ///
            issueWarrantyModel
                    .setWarrantySaleDate(getFormattedDate(wtstartDate));
            issueWarrantyModel.setVehicleSaleDate(getFormattedDate(wtstartDate));

            issueWarrantyModel.setDealerID(mCerifiedData.getDealer_id());
            issueWarrantyModel.setChasisNo(mCerifiedData.getChassis_no());

            issueWarrantyModel.setEngineNo(mCerifiedData.getEngine_number());
            issueWarrantyModel.setCarMakeModelVersion(mCerifiedData.getMake()
                    + " " + mCerifiedData.getModel() + " "
                    + mCerifiedData.getCarversion());
            issueWarrantyModel.setCarMfgYear(mCerifiedData.getMyear());
            issueWarrantyModel.setCarRegNo(mCerifiedData.getRegno());
            issueWarrantyModel.setCarRegYear(mCerifiedData.getYear());
            issueWarrantyModel.setCarRegMonth(mCerifiedData.getMm());
            issueWarrantyModel
                    .setWarrantyStartDate(getFormattedDate(wtstartDate));
            issueWarrantyModel
                    .setWarrantyEndDate(getFormattedDate(wtexpiredate));
            issueWarrantyModel.setWarrantyType(packID);
            issueWarrantyModel.setCarID(mCerifiedData.getUsedCarID());
            issueWarrantyModel.setCustEmailId(custEmailAddress.getText()
                    .toString().trim());

            //issueWarrantyModel.setPackageName(CommonUtils.getStringSharedPreference(this, Constants.APP_PACKAGE_NAME, ""));
            //issueWarrantyModel.setSource(BuildConfig.APP_SOURCE);

            Gson gson = new Gson();
            String request_string = gson.toJson(issueWarrantyModel,
                    IssueWarrantyModel.class);
            params.put(Constants.EVALUATIONDATA, request_string);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(gson.toJson(issueWarrantyModel,
                        IssueWarrantyModel.class));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.show();
            RetrofitRequest.certificationDetailRequest(jsonObject, new Callback<CertificationDetailModel>() {
                @Override
                public void success(CertificationDetailModel certificationDetailModel, retrofit.client.Response response) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    if (certificationDetailModel.getStatus().equals("T")) {

                        if (CommonUtils.getBooleanSharedPreference(IssueWarrantyFormActivity.this, Constants.RSA_DEALER, false)) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(IssueWarrantyFormActivity.this);
                            Dialog dialog = builder.setTitle(R.string.warranty_issued_title)
                                    .setMessage(CommonUtils.getReplacementString(IssueWarrantyFormActivity.this, R.string.warranty_rsa, certificationDetailModel.getWarrantyID(), certificationDetailModel.getRSADetails().getRsaId()))
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            warrantyIssued = true;

                                            CertifiedCarsFragment.shouldReload = true;
                                            IssuedWarrantyFragment.shouldReload = true;
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    })
                                    .setCancelable(false)
                                    .create();

                            if (!IssueWarrantyFormActivity.this.isFinishing() && !dialog.isShowing()) {
                                dialog.show();
                            }
                        } else {

                            AlertDialog.Builder builder = new AlertDialog.Builder(IssueWarrantyFormActivity.this);
                            Dialog dialog = builder.setTitle(R.string.warranty_issued_title)
                                    .setMessage(CommonUtils.getReplacementString(IssueWarrantyFormActivity.this, R.string.warranty_message, certificationDetailModel.getWarrantyID()))
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            warrantyIssued = true;

                                            CertifiedCarsFragment.shouldReload = true;
                                            IssuedWarrantyFragment.shouldReload = true;
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    })
                                    .create();

                            if (!IssueWarrantyFormActivity.this.isFinishing() && !dialog.isShowing()) {
                                dialog.show();
                            }

                        }


                    } else {
                        CommonUtils.showToast(IssueWarrantyFormActivity.this,
                                certificationDetailModel.getError(), Toast.LENGTH_SHORT);
                        setResult(RESULT_OK);
                        finish();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    CommonUtils.showToast(IssueWarrantyFormActivity.this,
                            "Some problem occurred", Toast.LENGTH_SHORT);

                }
            });
           /* CertificationDetailRequest stocksRequest = new CertificationDetailRequest(
                    this,
                    Request.Method.POST,
                    Constants.getWarrantyWebServiceURL(this),
                    params,
                    new Response.Listener<CertificationDetailModel>() {
                        @Override
                        public void onResponse(CertificationDetailModel response) {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            if (response.getStatus().equals("T")) {

                                if (CommonUtils.getBooleanSharedPreference(IssueWarrantyFormActivity.this, Constants.RSA_DEALER, false)) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(IssueWarrantyFormActivity.this);
                                    Dialog dialog = builder.setTitle(R.string.warranty_issued_title)
                                            .setMessage(CommonUtils.getReplacementString(IssueWarrantyFormActivity.this, R.string.warranty_rsa, response.getWarrantyID(), response.getRSADetails().getRsaId()))
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    warrantyIssued = true;

                                                    CertifiedCarsFragment.shouldReload = true;
                                                    IssuedWarrantyFragment.shouldReload = true;
                                                    setResult(RESULT_OK);
                                                    finish();
                                                }
                                            })
                                            .setCancelable(false)
                                            .create();

                                    if (!IssueWarrantyFormActivity.this.isFinishing() && !dialog.isShowing()) {
                                        dialog.show();
                                    }
                                } else {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(IssueWarrantyFormActivity.this);
                                    Dialog dialog = builder.setTitle(R.string.warranty_issued_title)
                                            .setMessage(CommonUtils.getReplacementString(IssueWarrantyFormActivity.this, R.string.warranty_message, response.getWarrantyID()))
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    warrantyIssued = true;

                                                    CertifiedCarsFragment.shouldReload = true;
                                                    IssuedWarrantyFragment.shouldReload = true;
                                                    setResult(RESULT_OK);
                                                    finish();
                                                }
                                            })
                                            .create();

                                    if (!IssueWarrantyFormActivity.this.isFinishing() && !dialog.isShowing()) {
                                        dialog.show();
                                    }

                                }


                            } else {
                                CommonUtils.showToast(IssueWarrantyFormActivity.this,
                                        response.getError(), Toast.LENGTH_SHORT);
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    CommonUtils.showToast(IssueWarrantyFormActivity.this,
                            "Some problem occurred", Toast.LENGTH_SHORT);

                }
            });

            ApplicationController.getInstance().addToRequestQueue(
                    stocksRequest, Constants.TAG_STOCKS_LIST, true, this);
       */ } catch (Exception e) {
            GCLog.e("exception: " + e.getMessage());
        }

    }

    private void showNetworkErrorDialog(final NetworkEvent networkEvent) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder
                .setTitle(R.string.sorry)
                .setMessage(R.string.network_error)
                .setPositiveButton(R.string.retry,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                makeIssuedWarrantyRequest(networkEvent
                                        .isShowFullPageError());
                            }
                        })
                .setNegativeButton(R.string.issue_warranty_later,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                progressDialog.dismiss();
                                IssueWarrantyFormActivity.this.finish();
                            }
                        }).setCancelable(false).create();

        if (!alertDialog.isShowing()
                && !IssueWarrantyFormActivity.this.isFinishing()) {
            alertDialog.show();
        }

    }

    private String getFormattedDate(TextView dateView) {
//		String warrantyDates = dateView.getText().toString().trim();
//		String[] formatdate = warrantyDates.split("Date: ")[1].split("/");
        String warrantyDates = dateView.getText().toString().trim();
        String[] formatdate = warrantyDates.split("/");
        warrantyDates = formatdate[2].trim() + "-"
                + ApplicationController.monthNameMap.get(formatdate[1].trim())
                + "-" + formatdate[0];

        return warrantyDates;
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year,
                          int month, int day, DateType dateType) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
        if (parent.getAdapter() instanceof CityAdapter) {
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

}
