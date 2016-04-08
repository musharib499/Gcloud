package com.gcloud.gaadi.insurance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ListPopupWindow;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.BasicListItemAdapter;
import com.gcloud.gaadi.annotations.NotAllowedChars;
import com.gcloud.gaadi.annotations.Required;
import com.gcloud.gaadi.annotations.RequiredName;
import com.gcloud.gaadi.annotations.TextRule;
import com.gcloud.gaadi.annotations.rules.Rule;
import com.gcloud.gaadi.annotations.rules.Validator;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.BasicListItemModel;
import com.gcloud.gaadi.model.FinanceCompany;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.InsuranceDocumentModel;
import com.gcloud.gaadi.model.InsuranceInspectedCarData;
import com.gcloud.gaadi.model.PreviousPolicyInsurer;
import com.gcloud.gaadi.model.QuoteDetails;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.BaseActivity;
import com.gcloud.gaadi.ui.CustomMaterialAutoCompleteTxtVw;
import com.gcloud.gaadi.ui.GCProgressDialog;
import com.gcloud.gaadi.ui.customviews.CustomCalender;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.MaterialTextView;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class DocumentScreenActivity extends BaseActivity implements CustomCalender.AfterChnageOnDateSet,View.OnClickListener, Validator.ValidationListener
        , RadioGroup.OnCheckedChangeListener {

    private ActionBar mActionBar;
    private Validator mValidator;
    //    private GAHelper mGAHelper;
    private GCProgressDialog mProgressDialog;

    private QuoteDetails mQuoteDetails;



  /*  @Required(order = 1, messageResId = R.string.error_previous_policy_end_date_required)
    MaterialTextView prevPolicyEndDate;*/

    @Required(order = 2, messageResId = R.string.error_previous_policy_insurer_required)
    MaterialTextView prevPolicyInsurer;

    @Required(order = 3, messageResId = R.string.error_previous_policy_number_required)
    @RequiredName(order = 4, messageResId = R.string.error_prev_policy_number_cant_contain_special_chars)
    MaterialEditText prevPolicyNo;

    MaterialTextView _view;

    @Required(order = 5, messageResId = R.string.error_policy_start_date_required)
    CustomCalender policyStartDate;

    @Required(order = 6, messageResId = R.string.error_policy_end_date_required)
    MaterialTextView policyEndDateLabel;

    @Required(order = 7, messageResId = R.string.reg_date_req)
    MaterialTextView tv_regDate;

    @Required(order = 8, messageResId = R.string.error_engine_no_required)
    @TextRule(order = 9, minLength = 8, maxLength = 20, messageResId = R.string.error_engine_no_invalid)
    @NotAllowedChars(order = 10, notAllowedChars = Constants.NOT_ALLOWED_CHARS, messageResId = R.string.error_engine_cant_contain_special_char)
    MaterialEditText engineNo;

    @Required(order = 11, messageResId = R.string.error_chassis_no_required)
    @TextRule(order = 12, minLength = 10, maxLength = 25, messageResId = R.string.error_chassis_no_invalid)
    @NotAllowedChars(order = 13, notAllowedChars = Constants.NOT_ALLOWED_CHARS, messageResId = R.string.error_chassis_no_cant_contain_special_chars)
    MaterialEditText chasisNo;

    @Required(order = 14, messageResId = R.string.bank_value_req)
    CustomMaterialAutoCompleteTxtVw tvFinanceCompany;


    //    TextView nextButton;
    DatePickerDialog dialog;
    public static final int DATE_PICKER_ID_START = 11;
    public static final int DATE_PICKER_ID_END = 12;
    private int year;
    private int month;
    private int day;
    StringBuilder builder;


    String mSelectedCase = "";
    private InsuranceInspectedCarData insuranceInspectedCarData;
    private ArrayList<String> mInsuranceCities, financeCompanyNameList;
    private ArrayList<FinanceCompany> financeCompanyList;


    private ScrollView scrollView;

    boolean mToastShown = false;
    private BasicListItemAdapter financeListAdapter, dateAdapter, previousPolicyInsurerAdptr;
    BasicListItemModel financeListModel, registrationDateItem, prevPolicyInsurerModel;
    private ArrayList<BasicListItemModel> registrationDateList, previousPolicyInsurerList;
    String financeCompanySlug = "", previousPolicyInsurerSlug = "";
    String processId = "", carId = "", isCarFinanced = "0";
    RadioGroup rGrpCarFinanced;
    String bankName = "";


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CommonUtils.startActivityTransition(DocumentScreenActivity.this, Constants.TRANSITION_RIIGHT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(com.gcloud.gaadi.constants.Constants.INSURANCE_CITIES, mInsuranceCities);
        outState.putSerializable(Constants.FINANCE_COMPANY, financeCompanyList);
        outState.putSerializable(Constants.QUOTE_DETAILS, mQuoteDetails);
        outState.putString(Constants.CAR_ID, carId);
        outState.putString(Constants.PROCESS_ID, processId);

        InsuranceDocumentModel model = new InsuranceDocumentModel();
        model.setStartDate(policyStartDate.getText().toString());
        model.setEndDate(policyEndDateLabel.getText().toString());
        if (prevPolicyInsurer.getVisibility() == View.VISIBLE && !prevPolicyInsurer.getText().equals(""))
            model.setPrevPolicyInsurer(prevPolicyInsurer.getText().toString());
       /* if (prevPolicyEndDate.getVisibility() == View.VISIBLE && !prevPolicyEndDate.getText().equals(""))
            model.setPrevPoEndDate(prevPolicyEndDate.getText().toString());*/
        outState.putSerializable(Constants.DOC_ARG, model);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }




    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mInsuranceCities = savedInstanceState.getStringArrayList(com.gcloud.gaadi.constants.Constants.INSURANCE_CITIES);
        financeCompanyList = (ArrayList<FinanceCompany>) savedInstanceState.getSerializable(Constants.FINANCE_COMPANY);
        mQuoteDetails = (QuoteDetails) savedInstanceState.getSerializable(Constants.QUOTE_DETAILS);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_document_screen, frameLayout);


        scrollView = (ScrollView) findViewById(R.id.scrollView);

        Intent intent = getIntent();
        if (intent != null) {
            mQuoteDetails = (QuoteDetails) intent.getSerializableExtra(Constants.QUOTE_DETAILS);
            mInsuranceCities = intent.getStringArrayListExtra(com.gcloud.gaadi.constants.Constants.INSURANCE_CITIES);
            financeCompanyList = (ArrayList<FinanceCompany>) intent.getSerializableExtra(Constants.FINANCE_COMPANY);
            mSelectedCase = intent.getStringExtra(Constants.SELECTED_CASE);


            processId = intent.getStringExtra(Constants.PROCESS_ID);
            carId = intent.getStringExtra(Constants.CAR_ID);
            insuranceInspectedCarData = (InsuranceInspectedCarData) intent.getSerializableExtra(Constants.INSURANCE_INSPECTED_CAR_DATA);
        }

        // API replacement against select on quote screen
        //   quoteDetailSubmissionRequest();


        formPrevPolicyInsurerList();

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
//        mGAHelper = new GAHelper(this);

        mActionBar = getSupportActionBar();
        mActionBar.hide();

        mProgressDialog = new GCProgressDialog(this, this);


        policyStartDate = (CustomCalender) findViewById(R.id.policyStartDate);

        policyStartDate.setmActivity(this);
        policyStartDate.setSimpleDateFormate("dd/MMM/yyyy");
        policyStartDate.setOnClickListener(this);

        // prevPolicyEndDate = (MaterialTextView) findViewById(R.id.prevPolicyEndDate);
        prevPolicyInsurer = (MaterialTextView) findViewById(R.id.policyInsurer);
        tvFinanceCompany = (CustomMaterialAutoCompleteTxtVw) findViewById(R.id.tv_bankList);
        rGrpCarFinanced = (RadioGroup) findViewById(R.id.rGrpCarFinanced);
        tv_regDate = (MaterialTextView) findViewById(R.id.tv_regDate);
        tv_regDate.setOnClickListener(this);

        rGrpCarFinanced.setOnCheckedChangeListener(this);
        prevPolicyInsurer.setOnClickListener(this);
        // tvFinanceCompany.setOnClickListener(this);

        prevPolicyNo = (MaterialEditText) findViewById(R.id.policyNumber);
        //prevOwnerName = (MaterialEditText) findViewById(R.id.ownerName);
        engineNo = (MaterialEditText) findViewById(R.id.engineNo);
        chasisNo = (MaterialEditText) findViewById(R.id.chasisNo);

//        nextButton = (TextView) findViewById(R.id.nextButton);
        findViewById(R.id.nextButton).setOnClickListener(this);
        policyEndDateLabel = (MaterialTextView) findViewById(R.id.policyEndDateText);
        policyEndDateLabel.setClickable(false);
        policyEndDateLabel.setEnabled(false);
        policyEndDateLabel.setOnClickListener(this);


        //prevPolicyEndDate.setOnClickListener(this);


//        nextButton.setOnClickListener(this);

        if (mSelectedCase.equalsIgnoreCase(Constants.OTHER_CARS)) {
            findViewById(R.id.previousPolicyLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.engineNumChasisNumLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.reg_dateLayout).setVisibility(View.GONE);
            engineNo.setText(insuranceInspectedCarData.getEngineNo());
            chasisNo.setText(insuranceInspectedCarData.getChassisNo());
            setPolicyStartDateForOtherCars();

        } else {
            findViewById(R.id.previousPolicyLayout).setVisibility(View.GONE);
            findViewById(R.id.engineNumChasisNumLayout).setVisibility(View.GONE);
            findViewById(R.id.reg_dateLayout).setVisibility(View.VISIBLE);
            TextView regMonth = (TextView) findViewById(R.id.registrationMonth);
            // regMonth.setText(CommonUtils.getReplacementString(this, R.string.month, ApplicationController.monthShortMap.get(insuranceInspectedCarData.getRegMonth() + "/" + insuranceInspectedCarData.getRegYear())));
            regMonth.setText(ApplicationController.monthShortMap.get(insuranceInspectedCarData.getRegMonth()) + " / " + insuranceInspectedCarData.getRegYear());
           /* TextView regYear = (TextView) findViewById(R.id.registrationYear);
            regYear.setText(CommonUtils.getReplacementString(this, R.string.registrationYear, insuranceInspectedCarData.getRegYear()));*/

            formList();
        }

        if (financeCompanyList != null) {
            ArrayList<BasicListItemModel> financeBasicItemList = new ArrayList<BasicListItemModel>();
            financeCompanyNameList = new ArrayList<String>();
            for (FinanceCompany financeCompanyDetails : financeCompanyList) {
                BasicListItemModel listItem = new BasicListItemModel(
                        financeCompanyDetails.getFinanceCompanySlug(), financeCompanyDetails.getFinanceCompanyName());
                financeBasicItemList.add(listItem);
                financeCompanyNameList.add(financeCompanyDetails.getFinanceCompanyName());
            }
            financeBasicItemList.trimToSize();
            financeListAdapter = new BasicListItemAdapter(this, financeBasicItemList);
            tvFinanceCompany.setThreshold(1);
            tvFinanceCompany.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, financeCompanyNameList));
            tvFinanceCompany.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    //financeListModel = (BasicListItemModel) adapterView.getAdapter().getItem(position);
                    bankName  =  tvFinanceCompany.getText().toString();

                    /*tvFinanceCompany.setText(financeCompanyList.get(position).getFinanceCompanyName());
                    financeCompanySlug = financeCompanyList.get(position).getFinanceCompanySlug();*/
                    financeCompanySlug = financeCompanyList.get(financeCompanyNameList.indexOf(tvFinanceCompany.getText().toString()))
                            .getFinanceCompanySlug();

                }
            });
        }

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

/*
        prevPolicyEndDate.startLabelAnimation();
        prevPolicyEndDate.setText(new StringBuilder().append(year+1)
                .append("-").append(month).append("-").append(day)
                .append(" "));*/

        if (savedInstanceState != null) {
            InsuranceDocumentModel model = (InsuranceDocumentModel) savedInstanceState.getSerializable(Constants.DOC_ARG);
            if (model != null) {
                policyStartDate.setText(model.getStartDate());
               // policyStartDate.startLabelAnimation();
                policyEndDateLabel.setText(model.getEndDate());
                policyEndDateLabel.startLabelAnimation();
                if (prevPolicyInsurer.getVisibility() == View.VISIBLE) {
                    if (model.getPrevPolicyInsurer() != null) {
                        prevPolicyInsurer.setText(model.getPrevPolicyInsurer());
                        prevPolicyInsurer.startLabelAnimation();
                    }
                }

             /*   if (prevPolicyEndDate.getVisibility() == View.VISIBLE) {
                    if (model.getPrevPoEndDate() != null) {
                        prevPolicyEndDate.setText(model.getPrevPoEndDate());
                        prevPolicyEndDate.startLabelAnimation();
                    }
                }*/
            }

        }

    }

    private void formPrevPolicyInsurerList() {
        previousPolicyInsurerList = new ArrayList<BasicListItemModel>();
        String str = CommonUtils.getStringSharedPreference(DocumentScreenActivity.this, Constants.PREV_POLICY_INSURER_LIST, "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<PreviousPolicyInsurer>>() {
        }.getType();
        List<PreviousPolicyInsurer> list = gson.fromJson(str, type);
        for (PreviousPolicyInsurer prevPolicyInsurerObj : list) {
            previousPolicyInsurerList.add(new BasicListItemModel(prevPolicyInsurerObj.getPrevPolicyInsurerSlug(), prevPolicyInsurerObj.getPrevPolicyInsurerName()));
        }
        previousPolicyInsurerList.trimToSize();
        previousPolicyInsurerAdptr = new BasicListItemAdapter(DocumentScreenActivity.this, previousPolicyInsurerList);
    }

    private void formList() {
        DateTime dateTime = new DateTime(Integer.parseInt(insuranceInspectedCarData.getRegYear()),
                Integer.parseInt(insuranceInspectedCarData.getRegMonth()), 1, 0, 0);

        registrationDateList = new ArrayList<BasicListItemModel>();
        for (int i = dateTime.dayOfMonth().getMinimumValue(); i <= dateTime.dayOfMonth().getMaximumValue(); ++i) {
            registrationDateList.add(new BasicListItemModel(String.valueOf(i), String.valueOf(i)));
        }
        registrationDateList.trimToSize();
        dateAdapter = new BasicListItemAdapter(this, registrationDateList);
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


    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.policyInsurer:
                final android.widget.ListPopupWindow listpopupWindow = new android.widget.ListPopupWindow(this);
                listpopupWindow.setAdapter(previousPolicyInsurerAdptr);
                listpopupWindow.setModal(true);
                listpopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
                listpopupWindow.setAnchorView(findViewById(R.id.policyInsurer));
                listpopupWindow.setWidth(android.widget.ListPopupWindow.WRAP_CONTENT);
                listpopupWindow
                        .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent,
                                                    View view, int position, long id) {

                                prevPolicyInsurerModel = (BasicListItemModel) parent.getAdapter().getItem(position);

                                prevPolicyInsurer.setText(prevPolicyInsurerModel.getValue());
                                previousPolicyInsurerSlug = prevPolicyInsurerModel.getId();
                                listpopupWindow.dismiss();


                            }
                        });
                listpopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (prevPolicyInsurer.getText().toString().trim().isEmpty())
                            prevPolicyInsurer.removeLabelAnimation();
                        prevPolicyInsurer.removeLabelFocusAnimation();
                    }
                });
                prevPolicyInsurer.post(new Runnable() {
                    @Override
                    public void run() {
                        listpopupWindow.show();
                    }
                });

                break;

           /* case R.id.tv_bankList:
                final android.widget.ListPopupWindow financeListpopupWindow = new android.widget.ListPopupWindow(this);
                financeListpopupWindow.setAdapter(financeListAdapter);
                financeListpopupWindow.setModal(true);
                financeListpopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
                financeListpopupWindow.setAnchorView(findViewById(R.id.tv_bankList));
                financeListpopupWindow.setWidth(android.widget.ListPopupWindow.WRAP_CONTENT);
                financeListpopupWindow
                        .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent,
                                                    View view, int position, long id) {

                                financeListModel = (BasicListItemModel) parent.getAdapter().getItem(position);

                                tvFinanceCompany.setText(financeListModel.getValue());
                                financeCompanySlug = financeListModel.getId();
                                financeListpopupWindow.dismiss();


                            }
                        });
                financeListpopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (tvFinanceCompany.getText().toString().trim().isEmpty())
                            tvFinanceCompany.removeLabelAnimation();
                        tvFinanceCompany.removeLabelFocusAnimation();
                    }
                });
                tvFinanceCompany.post(new Runnable() {
                    @Override
                    public void run() {
                        financeListpopupWindow.show();
                    }
                });

                break;
*/
            case R.id.tv_regDate:
              /*  if (!datePickerDialog.isVisible()) {
                    showDatePickerDialog();
                }*/

                final android.widget.ListPopupWindow regDatePopupWindow = new android.widget.ListPopupWindow(this);

                regDatePopupWindow.setAdapter(dateAdapter);
                regDatePopupWindow.setModal(true);
                regDatePopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
                regDatePopupWindow.setAnchorView(findViewById(R.id.tv_regDate));
                regDatePopupWindow.setWidth(android.widget.ListPopupWindow.WRAP_CONTENT);
                regDatePopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                        tv_regDate.setText(registrationDateList.get(position).getValue());
                        registrationDateItem = registrationDateList.get(position);
                        regDatePopupWindow.dismiss();


                    }
                });
                regDatePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (tv_regDate.getText().toString().trim().isEmpty())
                            tv_regDate.removeLabelAnimation();
                        tv_regDate.removeLabelFocusAnimation();
                    }
                });
                tv_regDate.post(new Runnable() {
                    @Override
                    public void run() {
                        regDatePopupWindow.show();
                    }
                });

                /*showDialog(DATE_DIALOG_ID);*/
                break;

            case R.id.policyStartDate:


                policyStartDate.afterchnage(this);
                setPolicyStartDate(policyStartDate);
                policyStartDate.onDailogs();
                policyEndDateLabel.setVisibility(View.VISIBLE);



           /*     if (mSelectedCase.equalsIgnoreCase(Constants.OTHER_CARS)) {
                    policyStartDate.re
                    return;
                }

               showDialog(DATE_PICKER_ID_START);*/
                break;

            case R.id.policyEndDateText:
                policyEndDateLabel.removeLabelFocusAnimation();

                break;

            case R.id.nextButton:
                mToastShown = false;
                mValidator.validate();
                break;

            case R.id.iv_actionBack:
                onBackPressed();
                break;
        }
    }

    private void validateField() {
        View view = null;
        String msg = null;
        if (prevPolicyInsurer.getText().toString().equals("")) {
            view = prevPolicyInsurer;
            msg = "please enter previous policy insurer";
            mToastShown = true;
        } else if (prevPolicyNo.getText().toString().equals("")) {
            view = prevPolicyNo;
            msg = "please enter previous policy number";
            mToastShown = true;
        }
        if (mToastShown) {
            CommonUtils.showToast(this, msg, Toast.LENGTH_SHORT);
            CommonUtils.scrollTo(scrollView, view);
            CommonUtils.shakeView(this, view);
        }
    }


    private void showListPopupWindow(final MaterialTextView textView, final int resourseID) {
        final ListPopupWindow listPopupWindow = new ListPopupWindow(this);

        listPopupWindow.setAdapter(new ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(resourseID)));

        listPopupWindow.setAnchorView(textView);
        listPopupWindow.setWidth(300);
        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);

        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPopupWindow.dismiss();
                textView.setText(getResources().getStringArray(resourseID)[position]);
            }
        });
        listPopupWindow.show();
    }

    protected void setPolicyStartDate(CustomCalender dialog) {


        if (mSelectedCase.equalsIgnoreCase(Constants.INSPECTED_CARS)) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH,1);
            long startTime = calendar.getTimeInMillis() - 1000;
            dialog.setMinDate(new Date(startTime));

            calendar.add(Calendar.DAY_OF_MONTH, 30);
            long endTime = calendar.getTimeInMillis();
            dialog.setMaxDate(new Date(endTime));


        }


    }


    // For other cars policyStartDate should be previousPolicyEndDate + 1
    void setPolicyStartDateForOtherCars() throws ArrayIndexOutOfBoundsException {
        policyStartDate.setClickable(false);
        policyStartDate.setEnabled(false);
        //policyStartDate.setHideUnderline(true);
        policyStartDate.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        String dateStr = CommonUtils.getStringSharedPreference(DocumentScreenActivity.this, Constants.PREVIOUS_POLICY_END_DATE, "");
        String[] mDates = dateStr.split("/");
        /*Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long milliseconds = date.getTime();*/
        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(milliseconds);
        cal.set(Integer.valueOf(mDates[2]),
                Integer.valueOf(ApplicationController.monthNameMap.get(mDates[1])) - 1,
                Integer.valueOf(mDates[0]));
        cal.add(Calendar.DAY_OF_MONTH, 1);
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(cal.getTime());*/

        policyStartDate.setText((cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/"
                + ApplicationController.monthShortMap.get("" + (cal.get(Calendar.MONTH) + 1)) + "/"
                + cal.get(Calendar.YEAR));
        //
        // policyStartDate.startLabelAnimation();
        try {
            setPolicyEndDate();
        } catch (Exception e) {
            GCLog.e("exception while setting policy end date: " + e.getMessage());
        }
    }

    protected void setPrevPolicyEndDate(DatePickerDialog dialog) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DAY_OF_MONTH, 44);

        long endTime = calendar1.getTimeInMillis();
        dialog.getDatePicker().setMaxDate(endTime);

        Calendar calendar = Calendar.getInstance();


        long startTime = calendar.getTimeInMillis() - 1000;
        dialog.getDatePicker().setMinDate(startTime);

    }

    // New Policy End Date Value should be {New Policy Start Date + 1 Year} – 1 Day
    protected void setPolicyEndDate() throws Exception {

        String dateStr = policyStartDate.getText().toString().trim();
        /*Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long milliseconds = date.getTime();*/
        String[] mDate = dateStr.split("/");
        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(milliseconds);
        cal.set(Integer.valueOf(mDate[2]),
                Integer.valueOf(ApplicationController.monthNameMap.get(mDate[1])) - 1,
                Integer.valueOf(mDate[0]));
        cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.add(Calendar.YEAR, 1);
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(cal.getTime());*/

        policyEndDateLabel.startLabelAnimation();
        // policyEndDateLabel.startLabelFocusAnimation();
        policyEndDateLabel.setText((cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" +
                ApplicationController.monthShortMap.get("" + (cal.get(Calendar.MONTH) + 1)) + "/" +
                cal.get(Calendar.YEAR));
    }

   /* protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID_START:
                dialog = new DatePickerDialog(this, pickerListener, year, month, day);
                dialog.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
                setPolicyStartDate(dialog);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (_view.getText().toString().trim().isEmpty())
                            _view.removeLabelAnimation();
                        _view.removeLabelFocusAnimation();

                    }
                });
                return dialog;
            case DATE_PICKER_ID_END:
                dialog = new DatePickerDialog(this, pickerListener, year, month, day);
                dialog.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
                setPrevPolicyEndDate(dialog);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (_view.getText().toString().trim().isEmpty())
                            _view.removeLabelAnimation();
                        _view.removeLabelFocusAnimation();

                    }
                });
                return dialog;
        }
        return null;
    }*/


    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // Show selected date
            _view.setText(new StringBuilder().append(day < 10 ? "0" + day : day)
                    .append("/").append(ApplicationController.monthShortMap.get("" + (month + 1)))
                    .append("/").append(year));
           /* if(_view.getId() == R.id.prevPolicyEndDate)
            {
                setPolicyStartDateForOtherCars();
            }*/
            if (_view.getId() == R.id.policyStartDate) {
                policyEndDateLabel.setVisibility(View.VISIBLE);
                try {
                    setPolicyEndDate();
                } catch (Exception e) {
                    GCLog.e("exception while setting policy end date: " + e.getMessage());
                }
            }
        }

    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case Constants.INSURANCE_CASE_FOR_INSPECTED_CAR:
                    setResult(RESULT_OK);
                    finish();
                    finishActivity(Constants.INSURANCE_CASE_FOR_INSPECTED_CAR);
                    break;

            }
        }
    }

    private void launchInsuranceDocumentScreen() {
        Intent intent = new Intent(this, InsuranceDocument.class);
        intent.putStringArrayListExtra(com.gcloud.gaadi.constants.Constants.INSURANCE_CITIES, mInsuranceCities);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Constants.PROCESS_ID, processId);
        intent.putExtra(Constants.CAR_ID, carId);
        intent.putExtra(Constants.QUOTE_DETAILS, mQuoteDetails);

        intent.putExtra(Constants.SELECTED_CASE, getIntent().getStringExtra(Constants.SELECTED_CASE));

        intent.putExtra(Constants.INSURANCE_INSPECTED_CAR_DATA, insuranceInspectedCarData);
        startActivityForResult(intent, com.gcloud.gaadi.constants.Constants.INSURANCE_CASE_FOR_INSPECTED_CAR);
        CommonUtils.startActivityTransition(DocumentScreenActivity.this, Constants.TRANSITION_LEFT);
    }

    @Override
    public void onValidationSucceeded() {

        if(!bankName.equals(tvFinanceCompany.getText().toString())){
            tvFinanceCompany.requestFocus();
            tvFinanceCompany.setError(getResources().getString(R.string.bank_value_req));
            return;
        }
        makeDocumentDataSubmissionRequest();
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


    private void makeDocumentDataSubmissionRequest() {
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        HashMap<String, String> mParams = new HashMap<>();
        mParams.put(Constants.METHOD_LABEL, Constants.ADD_INSURANCE_MORE_DETAILS);

        // mParams.put(Constants.INSURANCE_CASE_ID, mInsuranceCaseId);
        // mParams.put(Constants.INSURER_ID_API, mInsurerID);
        //  mParams.put(Constants.AGENT_ID_API, mAgentID);

        String[] mDate = policyStartDate.getText().toString().split("/");
        String policyDate = mDate[2] + "-" + ApplicationController.monthNameMap.get(mDate[1]) + "-" + mDate[0];
        mParams.put(Constants.START_DATE, policyDate);

        mDate = policyEndDateLabel.getText().toString().split("/");
        policyDate = mDate[2] + "-" + ApplicationController.monthNameMap.get(mDate[1]) + "-" + mDate[0];
        mParams.put(Constants.END_DATE, policyDate);
        mParams.put(Constants.POLICY_INSURER, ((MaterialTextView) findViewById(R.id.policyInsurer)).getText().toString());
        mParams.put(Constants.PREV_POLICY_NUMBER, prevPolicyNo.getText().toString());
        // mParams.put(Constants.OWNER_NAME, prevOwnerName.getText().toString());
        mParams.put(Constants.ENGINE_NUMBER, engineNo.getText().toString());
        mParams.put(Constants.CHASIS_NUMBER, chasisNo.getText().toString());
        mParams.put("finance_company_slug", financeCompanySlug);
        mParams.put("car_financed", isCarFinanced);
        //   mParams.put("prev_policy_end_date", prevPolicyEndDate.getText().toString());
        mParams.put("reg_date", tv_regDate.getText().toString());
        mParams.put(Constants.PROCESS_ID, processId);
        mParams.put(Constants.CAR_ID, carId);

        RetrofitRequest.putInsuranceQuoteDetails(mParams, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, Response response) {
                if (mProgressDialog != null)
                    mProgressDialog.dismiss();
                if ("T".equalsIgnoreCase(generalResponse.getStatus())) {
                    launchInsuranceDocumentScreen();
                } else {
                    CommonUtils.showToast(DocumentScreenActivity.this, generalResponse.getError(), Toast.LENGTH_SHORT);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if (mProgressDialog != null)
                    mProgressDialog.dismiss();
                CommonUtils.showErrorToast(DocumentScreenActivity.this, error, Toast.LENGTH_SHORT);
            }
        });

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (radioGroup.getId()) {
            case R.id.rGrpCarFinanced:

                if (checkedId == R.id.rb_carFinancedYes) {
                    tvFinanceCompany.setVisibility(View.VISIBLE);
                    tvFinanceCompany.requestFocus();
                    isCarFinanced = "1";

                } else {
                    CommonUtils.hideKeyboard(getBaseContext(),tvFinanceCompany);
                    tvFinanceCompany.setText("");
                    bankName = "";
                    tvFinanceCompany.setVisibility(View.GONE);
                    isCarFinanced = "0";
                }
                break;
        }
    }

    @Override
    public void onAfterChange() {
        try {
            setPolicyEndDate();
        } catch (Exception e) {
            GCLog.e("exception while setting policy end date: " + e.getMessage());
        }
    }

    @Override
    public void onAfterDismiss() {

    }
}