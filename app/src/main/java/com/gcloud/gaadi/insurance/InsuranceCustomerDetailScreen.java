package com.gcloud.gaadi.insurance;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.ListPopupWindow;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.CityAdapter;
import com.gcloud.gaadi.annotations.Email;
import com.gcloud.gaadi.annotations.NotAllowedChars;
import com.gcloud.gaadi.annotations.RadioGrp;
import com.gcloud.gaadi.annotations.Required;
import com.gcloud.gaadi.annotations.TextRule;
import com.gcloud.gaadi.annotations.rules.Rule;
import com.gcloud.gaadi.annotations.rules.Validator;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.model.CityData;
import com.gcloud.gaadi.model.FinanceCompany;
import com.gcloud.gaadi.model.InsuranceCustomerDetailsModel;
import com.gcloud.gaadi.model.InsuranceCustomerModel;
import com.gcloud.gaadi.model.InsuranceInspectedCarData;
import com.gcloud.gaadi.model.QuoteDetails;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.BaseActivity;
import com.gcloud.gaadi.ui.GCProgressDialog;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class InsuranceCustomerDetailScreen extends BaseActivity
        implements SlideDateTimeListener,View.OnClickListener, Validator.ValidationListener, AdapterView.OnItemClickListener {

    private ArrayList<String> mInsuranceCities;
    private ArrayList<FinanceCompany> financeCompanyList;
    private String mInsuranceCaseID;
    private String mInsurerID;
    private DatePickerDialog mDatePickerDialog;
    private GCProgressDialog mProgressDialog;
    private QuoteDetails mQuoteDetails;
    String processId, carId;
    ImageView iv_actionBack;
  /*  @Required(order = 1, messageResId = R.string.error_customer_salutation_required)
    private MaterialTextView mTitle;*/

    @Required(order = 1, messageResId = R.string.error_customer_first_name_required)
//    @RequiredName(order = 2, messageResId = R.string.error_customer_first_name_contains_special_chars)
    @TextRule(order = 2, minLength = 3, messageResId = R.string.minimum_length_should_be_three_char)
    //@NotAllowedChars(order = 3, notAllowedChars = Constants.NOT_ALLOWED_NAME_CHARS, messageResId = R.string.error_customer_first_name_invalid)
    private MaterialEditText mFirstName;

    @RadioGrp(order = 4, messageResId = R.string.error_vehicle_owner_required)
    private RadioGroup mvehicle_owner;

    RadioButton mIndividual,mCompany;

   /* @NotAllowedChars(order = 4, notAllowedChars = Constants.NOT_ALLOWED_NAME_CHARS, messageResId = R.string.error_customer_middle_name_contains_special_chars)
    private MaterialEditText mMiddleName;*/

  /*  @Required(order = 5, messageResId = R.string.error_customer_last_name_required)
    @TextRule(order = 6, minLength = 3, messageResId = R.string.minimum_length_should_be_three_char)
    @NotAllowedChars(order = 7, notAllowedChars = Constants.NOT_ALLOWED_NAME_CHARS, messageResId = R.string.error_customer_last_name_contains_special_chars)
    private MaterialEditText mLastName;*/

    @Required(order = 5, messageResId = R.string.error_customer_mobile_required)
    @TextRule(order = 6, minLength = 10, maxLength = 10, messageResId = R.string.error_customer_mobile_required)
    private MaterialEditText mMobile;

    @Required(order = 7, messageResId = R.string.error_customer_email_required)
    @Email(order = 8, minLength = 8, messageResId = R.string.error_customer_email_required)
    private MaterialEditText mEmail;

    @Required(order = 9, messageResId = R.string.error_customer_address_required)
    private MaterialEditText mAddress;


    @Required(order = 10, messageResId = R.string.error_customer_city_required)
    private MaterialAutoCompleteTextView mCity;


    @Required(order = 11, messageResId = R.string.error_customer_pincode_required)
    @TextRule(order = 12, minLength = 6, maxLength = 6, messageResId = R.string.error_customer_pincode_invalid)
    private MaterialEditText mPincode;


    @RadioGrp(order = 13, messageResId = R.string.error_customer_gender_required)
    private RadioGroup mGender;

    @RadioGrp(order = 14, messageResId = R.string.error_customer_marital_status_required)
    private RadioGroup mMaritalStatus;


    @Required(order = 15, messageResId = R.string.error_customer_dob_required)
    private MaterialTextView mDateOfBirth;


    // @Required(order = 20, messageResId = R.string.error_customer_occupation_required)
    private MaterialTextView mOccupation;

    // @Required(order = 21, messageResId = R.string.error_customer_income_required)
    private MaterialTextView mIncome;

    @Required(order = 16, messageResId = R.string.error_customer_nominee_name_required)
    @TextRule(order = 17, minLength = 3, messageResId = R.string.minimum_length_should_be_three_char)
    @NotAllowedChars(order = 18, notAllowedChars = Constants.NOT_ALLOWED_NAME_CHARS, messageResId = R.string.error_customer_nominee_name_invalid)
    private MaterialEditText mNomineeName;

    @Required(order = 19, messageResId = R.string.error_customer_nominee_relation_required)
    private MaterialTextView mNomineeRelation;

    @Required(order = 20, messageResId = R.string.error_customer_age_required)
    private MaterialAutoCompleteTextView mNomineeAge;

    @Required(order = 21, messageResId = R.string.error_customer_appointee_name_required)
    @TextRule(order = 22, minLength = 3, messageResId = R.string.minimum_length_should_be_three_char)
    @NotAllowedChars(order = 23, notAllowedChars = Constants.NOT_ALLOWED_NAME_CHARS, messageResId = R.string.error_customer_appointee_name_invalid)
    private MaterialEditText mAppointeeName;

    @Required(order = 24, messageResId = R.string.error_customer_appointee_relation_required)
    private MaterialTextView mAppointeeRelation;

    private Validator mValidator;
//    private GAHelper mGAHelper;

    private ScrollView scrollView;
    // private ActionBar mActionBar;
    private String mAgentId;
    private InsuranceCustomerModel mInsuranceCustomerModel;
    private InsuranceInspectedCarData insuranceInspectedCarData;
    private static final int DATE_PICKER_ID = 11;
    private CityAdapter mCityAdapter;
    private CityData selectedCity;
    private LinearLayout customer_detail_hide;
    private InputFilter[] inputFilters;
    ArrayList<String> temp;
    private boolean isAgeSelected = false, individual = true;
    private String selectedCityItem = "" ;



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CommonUtils.startActivityTransition(InsuranceCustomerDetailScreen.this, Constants.TRANSITION_RIIGHT);
    }

    private FilterQueryProvider cityFilterQueryProvider = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getCityCursor(constraint);
        }
    };


    private Cursor getCityCursor(CharSequence constraint) {
        return ApplicationController.getMakeModelVersionDB().getCityRecords(constraint);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.layout_customer_detail_screenn, frameLayout);

      /*  Toolbar mToolbar = (Toolbar) findViewById(R.id.customerDetailstoolbar);
        setSupportActionBar(mToolbar);
*/
        getSupportActionBar().hide();
        inputFilters = new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c)) // put your condition here
                        sb.append(c);
                    else
                        keepOriginal = false;
                }
                if (keepOriginal)
                    return null;
                else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c) {
                return individual? (Character.isLetter(c) || Character.isSpaceChar(c)): true;
            }
        }};

        mInsuranceCustomerModel = new InsuranceCustomerModel();
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
        mProgressDialog = new GCProgressDialog(this, this);
//        mGAHelper = new GAHelper(this);

      /*  mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);*/

        // View v = findViewById(R.id.toolbarLayout);
        iv_actionBack = (ImageView) findViewById(R.id.iv_actionBack);
        iv_actionBack.setOnClickListener(this);
        findViewById(R.id.btnNextCustomerDetails).setOnClickListener(this);


        scrollView = (ScrollView) findViewById(R.id.scrollView);

        mFirstName = (MaterialEditText) findViewById(R.id.etFirstName);
        mFirstName.setFilters(inputFilters);

        //mMiddleName = (MaterialEditText) findViewById(R.id.etMiddleName);
        // mMiddleName.setFilters(inputFilters);

        //  mLastName = (MaterialEditText) findViewById(R.id.etLastName);
        //  mLastName.setFilters(inputFilters);

        mGender = (RadioGroup) findViewById(R.id.radioGroupGender);

        mMaritalStatus = (RadioGroup) findViewById(R.id.martialStatusGroup);
        mvehicle_owner = (RadioGroup) findViewById(R.id.radioGroupVehicleOwner);

        mNomineeName = (MaterialEditText) findViewById(R.id.etNominee);
        mNomineeName.setFilters(inputFilters);

        mAppointeeName = (MaterialEditText) findViewById(R.id.etAppointeeName);
        mAppointeeName.setFilters(inputFilters);

        customer_detail_hide = (LinearLayout) findViewById(R.id.customer_detail_hide);

        mNomineeRelation = (MaterialTextView) findViewById(R.id.spinnerNomineeRelation);
        mNomineeRelation.setOnClickListener(this);

        mAppointeeRelation = (MaterialTextView) findViewById(R.id.spinnerAppointeeRelation);
        mAppointeeRelation.setOnClickListener(this);

        mNomineeAge = (MaterialAutoCompleteTextView) findViewById(R.id.spinnerNomineeAge);
        mNomineeAge.setSelection(mNomineeAge.getText().length());
        if (mNomineeAge.getText().length() >= 0) {
            mNomineeAge.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.array_age)));
            mNomineeAge.setThreshold(1);
        }
        mNomineeAge.setOnItemClickListener(this);
       /* mNomineeAge.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(!isAgeSelected)
                    {
                        mNomineeAge.setText("");
                    }
                    isAgeSelected = false;
                }
            }
        });*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mNomineeAge.setOnDismissListener(new AutoCompleteTextView.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (!isAgeSelected) {
                        mNomineeAge.setText("");
                    }
                    isAgeSelected = false;
                }
            });
        }

        mOccupation = (MaterialTextView) findViewById(R.id.spinnerOccupation);
        mOccupation.setOnClickListener(this);

        mIncome = (MaterialTextView) findViewById(R.id.spinnerIncome);
        mIncome.setOnClickListener(this);

        mAddress = (MaterialEditText) findViewById(R.id.etCustAddress);

        mCity = (MaterialAutoCompleteTextView) findViewById(R.id.spinnerCity);
        mCity.setSelection(mCity.getText().length());
        mCityAdapter = new CityAdapter(this, null);
        mCity.setAdapter(mCityAdapter);
        mCity.setThreshold(1);
        mCity.setOnItemClickListener(this);
        mCityAdapter.setFilterQueryProvider(cityFilterQueryProvider);

        mPincode = (MaterialEditText) findViewById(R.id.etPin);

        mEmail = (MaterialEditText) findViewById(R.id.etEmail);

        mMobile = (MaterialEditText) findViewById(R.id.etMobile);

        mDateOfBirth = (MaterialTextView) findViewById(R.id.etDateOfBirth);
        //mDateOfBirth.setmActivity(this);

        mDateOfBirth.setOnClickListener(this);

        mvehicle_owner.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                individual = R.id.individual == checkedId;
                if (R.id.individual == checkedId) {
                    customer_detail_hide.setVisibility(View.VISIBLE);
                } else {
                    customer_detail_hide.setVisibility(View.GONE);
                }
            }
        });

        initializeData(savedInstanceState);

    }


    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        if (id == DATE_PICKER_ID) {
            // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        }
    }

    /*public void onCreateDialog() {

                Calendar cal = Calendar.getInstance();
              //  mDatePickerDialog = new DatePickerDialog(this, pickerListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
               // mDatePickerDialog.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
                // Age of person cannot be less than 18

                mDatePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (mDateOfBirth.getText().toString().trim().isEmpty())
                            //mDateOfBirth.removeLabelAnimation();

                       // mDateOfBirth.removeLabelFocusAnimation();
                        mInsuranceCustomerModel.setDob(mDateOfBirth.getText().toString());
                    }
                });

    }
*/
    protected void setMinDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, calendar.getMaximum(Calendar.HOUR_OF_DAY));
        calendar.add(Calendar.MINUTE, calendar.getMaximum(Calendar.MINUTE));
        calendar.add(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND));
        calendar.add(Calendar.MILLISECOND, calendar.getMaximum(Calendar.MILLISECOND));
        calendar.add(Calendar.YEAR, -18);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        long endTime = calendar.getTimeInMillis();

        onDailogs(endTime);
    }
    public void onDailogs(long endTime)
    {
        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                .setShowTimeTab(false)
                .setListener(this)
                .setInitialDate(new Date(endTime))
                .setMaxDate(new Date(endTime))

                .build().show();
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            mDateOfBirth.setText(new StringBuilder().append(selectedDay < 10 ? "0" + selectedDay : selectedDay)
                    .append("/").append(ApplicationController.monthShortMap.get("" + (selectedMonth + 1)))
                    .append("/").append(selectedYear));
        }

    };


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(Constants.INSURANCE_CITIES, mInsuranceCities);
        outState.putSerializable(Constants.FINANCE_COMPANY, financeCompanyList);
        outState.putSerializable(Constants.INSURANCE_INSPECTED_CAR_DATA, insuranceInspectedCarData);
        outState.putString(Constants.INSURANCE_CASE_ID, mInsuranceCaseID);
        outState.putString(Constants.INSURER_ID, mInsurerID);
        outState.putSerializable(Constants.QUOTE_DETAILS, mQuoteDetails);
        {
            mInsuranceCustomerModel.setNomineeRelation(((TextView) findViewById(R.id.spinnerNomineeRelation)).getText().toString());
            //   mInsuranceCustomerModel.setTitle(((TextView) findViewById(R.id.spinnerTitle)).getText().toString());
            mInsuranceCustomerModel.setOccupation(((TextView) findViewById(R.id.spinnerOccupation)).getText().toString());
            mInsuranceCustomerModel.setIncome(((TextView) findViewById(R.id.spinnerIncome)).getText().toString());
        }
        outState.putSerializable(Constants.INSURANCE_CUST_DETAILS, mInsuranceCustomerModel);
        outState.putString(Constants.CAR_ID, carId);
        outState.putString(Constants.PROCESS_ID, processId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mInsuranceCities = savedInstanceState.getStringArrayList(Constants.INSURANCE_CITIES);
        financeCompanyList = (ArrayList<FinanceCompany>) savedInstanceState.getSerializable(Constants.FINANCE_COMPANY);
        insuranceInspectedCarData = (InsuranceInspectedCarData) savedInstanceState.getSerializable(Constants.INSURANCE_INSPECTED_CAR_DATA);
        mInsuranceCaseID = savedInstanceState.getString(Constants.INSURANCE_CASE_ID);
        mInsurerID = savedInstanceState.getString(Constants.INSURER_ID);
        mQuoteDetails = (QuoteDetails) savedInstanceState.getSerializable(Constants.QUOTE_DETAILS);
        mInsuranceCustomerModel = (InsuranceCustomerModel) savedInstanceState.getSerializable(Constants.INSURANCE_CUST_DETAILS);
        processId = savedInstanceState.getString(Constants.PROCESS_ID);
        carId = savedInstanceState.getString(Constants.CAR_ID);
        setDataOnViews();
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


    private void initializeData(Bundle savedInstanceState) {
        Bundle args = getIntent().getExtras();
        if (args == null) {
            if (savedInstanceState != null) {
                mInsuranceCities = savedInstanceState.getStringArrayList(Constants.INSURANCE_CITIES);
                financeCompanyList = (ArrayList<FinanceCompany>) savedInstanceState.getSerializable(Constants.FINANCE_COMPANY);
                insuranceInspectedCarData = (InsuranceInspectedCarData) savedInstanceState.getSerializable(Constants.INSURANCE_INSPECTED_CAR_DATA);
                mInsuranceCaseID = savedInstanceState.getString(Constants.INSURANCE_CASE_ID);
                mInsurerID = savedInstanceState.getString(Constants.INSURER_ID);
                mQuoteDetails = (QuoteDetails) savedInstanceState.getSerializable(Constants.QUOTE_DETAILS);
                mAgentId = savedInstanceState.getString(Constants.AGENT_ID);
                mInsuranceCustomerModel = (InsuranceCustomerModel) savedInstanceState.getSerializable(Constants.INSURANCE_CUST_DETAILS);
                processId = savedInstanceState.getString(Constants.PROCESS_ID);
                carId = savedInstanceState.getString(Constants.CAR_ID);
                GCLog.d("CustomerModel = " + mInsuranceCustomerModel);
                if (mInsuranceCustomerModel != null) {
                    GCLog.d(" Title " + mInsuranceCustomerModel.getTitle());
                }
            }
        } else {
            mInsuranceCities = args.getStringArrayList(Constants.INSURANCE_CITIES);
            financeCompanyList = (ArrayList<FinanceCompany>) args.getSerializable(Constants.FINANCE_COMPANY);
            insuranceInspectedCarData = (InsuranceInspectedCarData) args.getSerializable(Constants.INSURANCE_INSPECTED_CAR_DATA);
            mInsuranceCaseID = args.getString(Constants.INSURANCE_CASE_ID);
            mInsurerID = args.getString(Constants.INSURER_ID);
            mAgentId = args.getString(Constants.AGENT_ID);
            mQuoteDetails = (QuoteDetails) args.getSerializable(Constants.QUOTE_DETAILS);
            mInsuranceCustomerModel = (InsuranceCustomerModel) args.getSerializable(Constants.INSURANCE_CUST_DETAILS);
            processId = args.getString(Constants.PROCESS_ID);
            carId = args.getString(Constants.CAR_ID);
        }

        setDataOnViews();

    }



    private void setDataOnViews() {
        if (mInsuranceCustomerModel != null) {
            if (mInsuranceCustomerModel.getNomineeRelation() != null
                    && !mInsuranceCustomerModel.getNomineeRelation().isEmpty()) {
                mNomineeRelation.startLabelAnimation();
                mNomineeRelation.setText(mInsuranceCustomerModel.getNomineeRelation());
            }
            //  mTitle.startLabelAnimation();
            //  mTitle.setText(mInsuranceCustomerModel.getTitle());
            if (mInsuranceCustomerModel.getOccupation() != null
                    && !mInsuranceCustomerModel.getOccupation().isEmpty()) {
                mOccupation.startLabelAnimation();
                mOccupation.setText(mInsuranceCustomerModel.getOccupation());
            }
           // mDateOfBirth.startLabelAnimation();
            mDateOfBirth.setText(mInsuranceCustomerModel.getDob());
            if (mInsuranceCustomerModel.getIncome() != null
                    && !mInsuranceCustomerModel.getIncome().isEmpty()) {
                mIncome.startLabelAnimation();
                mIncome.setText(mInsuranceCustomerModel.getIncome());
            }
            mCity.setText(mInsuranceCustomerModel.getCity());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mInsuranceCustomerModel == null) {
            mInsuranceCustomerModel = new InsuranceCustomerModel();
        }
    }

    @Override
    public void onClick(View v) {
        MaterialTextView textView;
        switch (v.getId()) {

            case R.id.spinnerNomineeRelation:
                textView = (MaterialTextView) findViewById(R.id.spinnerNomineeRelation);
                showListPopupWindow(textView, R.array.array_nominee_relation);
                break;

            case R.id.spinnerAppointeeRelation:
                textView = (MaterialTextView) findViewById(R.id.spinnerAppointeeRelation);
                showListPopupWindow(textView, R.array.array_nominee_relation);
                break;

            case R.id.spinnerNomineeAge:
                textView = (MaterialTextView) findViewById(R.id.spinnerNomineeAge);
                showListPopupWindow(textView, R.array.array_age);

                break;

          /*  case R.id.spinnerTitle:
                showListPopupWindow((TextView) findViewById(R.id.spinnerTitle), R.array.array_titles);
                break;*/

            case R.id.spinnerOccupation:
                textView = (MaterialTextView) findViewById(R.id.spinnerOccupation);
                showListPopupWindow(textView, R.array.array_occupation);
                break;

            case R.id.spinnerIncome:
                textView = (MaterialTextView) findViewById(R.id.spinnerIncome);
                showListPopupWindow(textView, R.array.array_income);
                break;


            case R.id.spinnerCity:
                if (mInsuranceCities != null) {
                    showCityListPopupWindow(R.id.spinnerCity, mInsuranceCities);
                }
                break;

            case R.id.btnNextCustomerDetails:
                mValidator.validate();

                break;

            case R.id.etDateOfBirth:

                setMinDate();
                  //  mDateOfBirth.onDailogs();
               // mDateOfBirth.afterdismiss(this);
                break;

            case R.id.iv_actionBack:
                onBackPressed();
                break;
        }

    }


    private void showListPopupWindow(final MaterialTextView textView, final int resourseID) {
        final ListPopupWindow listPopupWindow = new ListPopupWindow(
                this);
        listPopupWindow.setAdapter(new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(resourseID)));
        listPopupWindow.setAnchorView(textView);
        listPopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),
                R.drawable.abc_popup_background_mtrl_mult,
                null));
        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);

        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                textView.setText(getResources().getStringArray(resourseID)[position]);
                listPopupWindow.dismiss();
            }
        });
        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (textView.getText().toString().trim().isEmpty())
                    textView.removeLabelAnimation();
                textView.removeLabelFocusAnimation();


            }
        });
        listPopupWindow.show();
    }


    private void showCityListPopupWindow(final int viewID, final ArrayList<String> list) {
        final MaterialTextView textView = ((MaterialTextView) findViewById(viewID));
        final ListPopupWindow listPopupWindow = new ListPopupWindow(
                this);
        listPopupWindow.setAdapter(new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item, list));
        listPopupWindow.setAnchorView(textView);
        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);

        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                textView.setText(list.get(position));
                mInsuranceCustomerModel.setCity(textView.getText().toString());
                listPopupWindow.dismiss();
            }
        });
        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (textView.getText().toString().trim().isEmpty())
                    textView.removeLabelAnimation();
                     textView.removeLabelFocusAnimation();


            }
        });
        listPopupWindow.show();
    }

    private void showAgeListPopupWindow(final MaterialAutoCompleteTextView textView, final ArrayList<String> temp) {
        final ListPopupWindow listPopupWindow = new ListPopupWindow(
                this);
        listPopupWindow.setAdapter(new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item, temp));
        listPopupWindow.setAnchorView(textView);
        listPopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),
                R.drawable.abc_popup_background_mtrl_mult,
                null));
        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);

        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                textView.setText(temp.get(position));
                listPopupWindow.dismiss();
            }
        });
        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
               /* if (textView.getText().toString().trim().isEmpty())
                    textView.removeLabelAnimation();
                textView.removeLabelFocusAnimation();

*/
            }
        });
        listPopupWindow.show();
    }

    @Override
    public void onValidationSucceeded() {
        if (Integer.parseInt(mNomineeAge.getText().toString()) == 0 || mNomineeAge.getText().toString().startsWith("0") || Integer.parseInt(mNomineeAge.getText().toString()) > 100) {
            CommonUtils.showToast(InsuranceCustomerDetailScreen.this, "Please enter a valid age", Toast.LENGTH_LONG);
            mNomineeAge.setText("");
            return;
        }
        if(!selectedCityItem.equals(mCity.getText().toString())) {
            mCity.requestFocus();
            mCity.setError(getResources().getString(R.string.error_customer_city_invalid));
            findViewById(R.id.spinnerCity).setEnabled(true);
            return;
        }
        makeCustomerDetailSubmissionRequest();
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        if (failedView instanceof MaterialEditText) {
            ((MaterialEditText) failedView).setError(failedRule.getFailureMessage());
            failedView.requestFocus();


        } else {
            CommonUtils.showToast(this, failedRule.getFailureMessage(), Toast.LENGTH_SHORT);

        }

        CommonUtils.scrollTo(scrollView, failedView);
        CommonUtils.shakeView(this, failedView);
    }

    @Override
    public void onViewValidationSucceeded(View succeededView) {

    }

    private void makeCustomerDetailSubmissionRequest() {
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        HashMap<String, String> mParams = new HashMap<>();
        mParams.put(Constants.METHOD_LABEL, Constants.ADD_INSURANCE_CUSTOMER_DATA);
        mParams.put(Constants.VEHICLE_OWNER,
                mvehicle_owner.getCheckedRadioButtonId() == R.id.individual ?
                        "Individual" : "Company");

        if(mvehicle_owner.getCheckedRadioButtonId() == R.id.individual ) {

            int selectedId = mGender.getCheckedRadioButtonId();
            RadioButton radioSexButton = (RadioButton) findViewById(selectedId);
            mParams.put(Constants.INSURANCE_CUST_GENDER, radioSexButton.getText().toString());

            int maritalStatus = mMaritalStatus.getCheckedRadioButtonId();
            RadioButton radioMaritalButton = (RadioButton) findViewById(maritalStatus);
            mParams.put(Constants.INSURANCE_MARITAL_STATUS, radioMaritalButton.getText().toString());

            String[] mDate = mDateOfBirth.getText().toString().split("/");
            String mDOB = mDate[2] + "-" + ApplicationController.monthNameMap.get(mDate[1]) + "-" + mDate[0];
            mParams.put(Constants.INSURANCE_CUST_DOB, mDOB);

            mParams.put(Constants.INSURANCE_CUST_OCUUPATION, mOccupation.getText().toString());
            mParams.put(Constants.INSURANCE_CUST_INCOME, mIncome.getText().toString());
        }

        // mParams.put(Constants.INSURANCE_CUST_TITLE, mTitle.getText().toString());
        mParams.put(Constants.INSURANCE_CUST_FNAME, mFirstName.getText().toString().trim());
        // mParams.put(Constants.INSURANCE_CUST_MNAME, mMiddleName.getText().toString().trim());
        //mParams.put(Constants.INSURANCE_CUST_LNAME, mLastName.getText().toString().trim());
        mParams.put(Constants.QUOTE_ID, mQuoteDetails.getQuoteId());
        mParams.put(Constants.CAR_ID, carId);
        mParams.put(Constants.PROCESS_ID, processId);

        mParams.put(Constants.INSURANCE_CUST_NOMINEE_NAME, mNomineeName.getText().toString().trim());
        mParams.put(Constants.INSURANCE_CUST_NOMINEE_RELATION, mNomineeRelation.getText().toString());
        mParams.put(Constants.INSURANCE_CUST_NOMINEE_AGE, mNomineeAge.getText().toString());
        mParams.put(Constants.INSURANCE_CUST_ADDRESS, mAddress.getText().toString().trim());
        mParams.put(Constants.INSURANCE_CUST_CITY, (selectedCity != null) ? selectedCity.getCityName() : mCity.getText().toString());
        mParams.put(Constants.INSURANCE_CUST_PINCODE, mPincode.getText().toString());
        mParams.put(Constants.INSURANCE_CUST_EMAIL, mEmail.getText().toString().trim());
        mParams.put(Constants.INSURANCE_CUST_MOBILE, mMobile.getText().toString().trim());
        mParams.put(Constants.INSURANCE_CUST_APPOINTEE_NAME, mAppointeeName.getText().toString().trim());
        mParams.put(Constants.INSURANCE_CUST_APPOINTEE_RELATION, mAppointeeRelation.getText().toString());


        RetrofitRequest.putCustomerDetailsForInsurance(mParams, new Callback<InsuranceCustomerDetailsModel>() {
            @Override
            public void success(InsuranceCustomerDetailsModel insuranceCustomerDetailsModel, Response response) {
                if (mProgressDialog != null)
                    mProgressDialog.dismiss();


                if (insuranceCustomerDetailsModel.getStatus().equals("T")) {

                    GCLog.e("Customer Detail response: " + insuranceCustomerDetailsModel.toString());
                    Intent intent = new Intent(InsuranceCustomerDetailScreen.this, DocumentScreenActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    intent.putExtra(Constants.INSURANCE_CASE_ID, mInsuranceCaseID);
                    intent.putExtra(Constants.FINANCE_COMPANY, financeCompanyList);
                    intent.putExtra(Constants.QUOTE_DETAILS, mQuoteDetails);
                    intent.putExtra(Constants.PROCESS_ID, processId);
                    intent.putExtra(Constants.SELECTED_CASE, getIntent().getStringExtra(Constants.SELECTED_CASE));
                    intent.putExtra(Constants.CAR_ID, carId);
                    intent.putExtra(Constants.INSURANCE_INSPECTED_CAR_DATA, insuranceInspectedCarData);
                    startActivityForResult(intent, Constants.INSURANCE_CASE_FOR_INSPECTED_CAR);
                    CommonUtils.startActivityTransition(InsuranceCustomerDetailScreen.this, Constants.TRANSITION_LEFT);
                } else {
                    CommonUtils.showToast(InsuranceCustomerDetailScreen.this,
                            "Error: " + insuranceCustomerDetailsModel.getMessage(), Toast.LENGTH_SHORT);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                CommonUtils.showErrorToast(InsuranceCustomerDetailScreen.this, error, Toast.LENGTH_SHORT);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.INSURANCE_CASE_FOR_INSPECTED_CAR:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                    finishActivity(Constants.INSURANCE_CASE_FOR_INSPECTED_CAR);
                }
                break;
        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!(parent.getAdapter() instanceof CityAdapter)) {
            isAgeSelected = true;
            if ((String) parent.getAdapter().getItem(position) != null && Integer.parseInt((String) parent.getAdapter().getItem(position)) < 18) {
                mAppointeeName.setVisibility(View.VISIBLE);
                mAppointeeRelation.setVisibility(View.VISIBLE);
            } else {
                mAppointeeName.setVisibility(View.GONE);
                mAppointeeRelation.setVisibility(View.GONE);
            }
            mNomineeAge.setText((String) parent.getAdapter().getItem(position));
            mNomineeAge.setSelection(mNomineeAge.getText().toString().trim().length());
        } else {
            Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
            selectedCity = new CityData();
            selectedCity.setCityId(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.CITY_ID)));
            selectedCity.setCityName(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.CITY_NAME)));
            selectedCity.setStateId(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.STATE_ID)));
            selectedCity.setRegionId(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.REGION_ID)));

            selectedCityItem=selectedCity.getCityName();
            mCity.setText(selectedCityItem);
            mCity.setSelection(selectedCityItem.length());
        }
    }




    @Override
    public void onDateTimeSet(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        mDateOfBirth.setText(dateFormat.format(date));
        mDateOfBirth.removeLabelFocusAnimation();
    }

    @Override
    public void onDateTimeCancel() {
        if (mDateOfBirth.getText().toString().trim().isEmpty())
            mDateOfBirth.removeLabelAnimation();

        mDateOfBirth.removeLabelFocusAnimation();
        mInsuranceCustomerModel.setDob(mDateOfBirth.getText().toString());
    }
}
