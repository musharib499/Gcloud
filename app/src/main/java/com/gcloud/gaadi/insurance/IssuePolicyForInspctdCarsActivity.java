package com.gcloud.gaadi.insurance;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.BasicListItemAdapter;
import com.gcloud.gaadi.annotations.Required;
import com.gcloud.gaadi.annotations.ValueChecker;
import com.gcloud.gaadi.annotations.rules.Rule;
import com.gcloud.gaadi.annotations.rules.Validator;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.model.BasicListItemModel;
import com.gcloud.gaadi.model.InsuranceInspectedCarData;
import com.gcloud.gaadi.model.NewInsuranceCaseModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.GCProgressDialog;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.MaterialTextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;

public class IssuePolicyForInspctdCarsActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, Validator.ValidationListener {

    private static final int DATE_DIALOG_ID = 1;


    private ActionBar mActionBar;
    private DatePickerDialog datePickerDialog;
    // GCProgressDialog progressDialog;
    Validator mValidator;
    RadioGroup rGrpCngLpgType, rg_kitType, rGrpAccessories, rGrpInsurePassengers;
    private ScrollView scrollView;
    String formatdate = "";
    public static final String DATEPICKER_TAG = "datepicker";

    @Required(order = 1, messageResId = R.string.reg_date_req)
    MaterialTextView tv_regDate;

    @Required(order = 2, messageResId = R.string.city_req)
    MaterialTextView tv_regCity;

    @Required(order = 3, messageResId = R.string.error_expiring_idv_required)
    @ValueChecker(order = 4, minValue = 100000, replaceChars = ",.", messageResId = R.string.min_value_is_100000)
    MaterialEditText et_expiringIDV;

    @Required(order = 5, messageResId = R.string.error_request_type_required)
    MaterialTextView tv_requesttype;

    @Required(order = 6, messageResId = R.string.kit_value_required)
    @ValueChecker(order = 7, maxValue = 50000, replaceChars = ",.", messageResId = R.string.max_value_50000)
    MaterialEditText et_CngLpgKitValue;

    @Required(order = 8, messageResId = R.string.accessories_value_req)
    @ValueChecker(order = 9, maxValue = 100000, replaceChars = ",.", messageResId = R.string.max_value_100000)
    MaterialEditText et_accessoriesValue;

    @Required(order = 10, messageResId = R.string.ncb_valie_req)
    MaterialTextView tv_NCB;

    HashMap<String, String> mParams;
    LinearLayout footerLayout;
    private BasicListItemAdapter requestTypeAdapter, ncbPolicyAdapter, dateAdapter;
    BasicListItemModel requestTypeModel, ncbPolicyModel;
    private ArrayList<BasicListItemModel> requestTypeList = new ArrayList<BasicListItemModel>();
    private ArrayList<BasicListItemModel> ncbPolicyList = new ArrayList<BasicListItemModel>();
    private int year;
    private int month;
    private int day;
    DatePickerDialog.OnDateSetListener myDateSetListener;
    private String currentDate;
    private ArrayList<String> mInsuranceCities;
    private String mAgentId = "", requestTypeId = "";
    private InsuranceInspectedCarData insuranceInspectedCarData;
    String makeId = "", model = "", make = "", version = "", dealerId = "", carId = "", city = "", seatCapacity = "", power = "", reportUrl = "", fuelType = "", fitmentType = "", rcUrl = "";
    String isCNGLPG = "0", kitType = Constants.EXTERNALLY_FITTED, isAccessories = "0", isPolicyClaimed = "", isPassengerInsured = "0";
    GCProgressDialog progressDialog;
    boolean showSeatingCapacity;
    RelativeLayout insurePassengersLayout;
    private ArrayList<BasicListItemModel> registrationDateList;
    RadioButton rb_yesCngLpg, rb_noCngLpg, rb_companyFitted, rb_externallyFitted;
    BasicListItemModel registrationDateItem, cityItem;
    String ncbValue = "", requestValue = "", regDateValue = "";


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(Constants.INSURANCE_CITIES, mInsuranceCities);
        outState.putString(Constants.AGENT_ID, mAgentId);
        outState.putSerializable(Constants.INSURANCE_INSPECTED_CAR_DATA, insuranceInspectedCarData);
        outState.putSerializable(Constants.REGISTRATION_DATE, registrationDateList);
        outState.putSerializable(Constants.NCB_POLICY_LIST, ncbPolicyList);
        outState.putSerializable(Constants.REQUEST_TYPE_LIST, requestTypeList);
        outState.putSerializable(Constants.CAR_REG_DATE, registrationDateItem);
        outState.putSerializable(Constants.REQUEST_TYPE, requestTypeModel);
        outState.putSerializable(Constants.NCB, ncbPolicyModel);
        outState.putString(Constants.CITY_NAME, city);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mInsuranceCities = savedInstanceState.getStringArrayList(Constants.INSURANCE_CITIES);
        mAgentId = savedInstanceState.getString(Constants.AGENT_ID);
        insuranceInspectedCarData = (InsuranceInspectedCarData) savedInstanceState.getSerializable(Constants.INSURANCE_INSPECTED_CAR_DATA);
        registrationDateList = (ArrayList<BasicListItemModel>) savedInstanceState.getSerializable(Constants.REGISTRATION_DATE);
        ncbPolicyList = (ArrayList<BasicListItemModel>) savedInstanceState.getSerializable(Constants.NCB_POLICY_LIST);
        requestTypeList = (ArrayList<BasicListItemModel>) savedInstanceState.getSerializable(Constants.REQUEST_TYPE_LIST);
        registrationDateItem = (BasicListItemModel) savedInstanceState.getSerializable(Constants.CAR_REG_DATE);
        requestTypeModel = (BasicListItemModel) savedInstanceState.getSerializable(Constants.REQUEST_TYPE);
        ncbPolicyModel = (BasicListItemModel) savedInstanceState.getSerializable(Constants.NCB);
        city = savedInstanceState.getString(Constants.CITY_NAME);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issue_policy_for_inspctd_cars);
        progressDialog = new GCProgressDialog(this, this);
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        // progressDialog = new GCProgressDialog(this, this);

        Intent intent = getIntent();
        mInsuranceCities = intent.getStringArrayListExtra(Constants.INSURANCE_CITIES);
        mAgentId = intent.getStringExtra(Constants.AGENT_ID);
        insuranceInspectedCarData = (InsuranceInspectedCarData) intent.getSerializableExtra(Constants.INSURANCE_INSPECTED_CAR_DATA);
        insuranceInspectedCarData = (InsuranceInspectedCarData) intent.getSerializableExtra(Constants.INSURANCE_INSPECTED_CAR_DATA);

        /*
        * code used for getting data passed using intent
         */
        if (insuranceInspectedCarData != null) {
            carId = insuranceInspectedCarData.getCarId();
            make = insuranceInspectedCarData.getMake();
            makeId = Integer.toString(insuranceInspectedCarData.getMakeId());
            model = insuranceInspectedCarData.getModel();
            version = insuranceInspectedCarData.getVersion();
            seatCapacity = insuranceInspectedCarData.getPassengerSeats();
            power = insuranceInspectedCarData.getPower();
            fuelType = insuranceInspectedCarData.getFuelType();
            reportUrl = insuranceInspectedCarData.getCertificationReportURL();
            city = insuranceInspectedCarData.getRegistrationCity();
            showSeatingCapacity = insuranceInspectedCarData.isShowSeatingCapacity();
            fitmentType = insuranceInspectedCarData.getFitmentType();
            rcUrl = insuranceInspectedCarData.getRcUrl();
        }

        if (savedInstanceState != null) {
            city = savedInstanceState.getString(Constants.CITY_NAME);
            if (savedInstanceState.containsKey(Constants.NCB) && (savedInstanceState.getSerializable(Constants.NCB) != null)) {
                ncbValue = ((BasicListItemModel) savedInstanceState.getSerializable(Constants.NCB)).getValue();
            }
            if (savedInstanceState.containsKey(Constants.REQUEST_TYPE) && (savedInstanceState.getSerializable(Constants.REQUEST_TYPE) != null)) {
                requestValue = ((BasicListItemModel) savedInstanceState.getSerializable(Constants.REQUEST_TYPE)).getValue();
                requestTypeId = ((BasicListItemModel) savedInstanceState.getSerializable(Constants.REQUEST_TYPE)).getId();
            }
            if (savedInstanceState.containsKey(Constants.CAR_REG_DATE) && (savedInstanceState.getSerializable(Constants.CAR_REG_DATE) != null)) {
                regDateValue = ((BasicListItemModel) savedInstanceState.getSerializable(Constants.CAR_REG_DATE)).getValue();
            }
        }


        scrollView = (ScrollView) findViewById(R.id.scrollView);
        formLists();

        tv_regCity = (MaterialTextView) findViewById(R.id.tv_regCity);
        if (!city.equals("")) {
            tv_regCity.setText(city);
            tv_regCity.startLabelAnimation();
        }
        tv_regCity.setOnClickListener(this);

        tv_NCB = (MaterialTextView) findViewById(R.id.tv_NCB);
        tv_NCB.setOnClickListener(this);
        if (!ncbValue.equals("")) {
            tv_NCB.setText(ncbValue);
            tv_NCB.startLabelAnimation();
        }

        tv_requesttype = (MaterialTextView) findViewById(R.id.tv_requesttype);
        tv_requesttype.setOnClickListener(this);
        if (!requestValue.equals("")) {
            tv_requesttype.setText(requestValue);
            tv_requesttype.startLabelAnimation();
            if (requestTypeId.equals(Constants.POLICY_NOT_EXPIRED_NCB_TRANSFER)) {
                tv_NCB.setVisibility(View.VISIBLE);
            } else
                tv_NCB.setVisibility(View.GONE);
        }

        footerLayout = (LinearLayout) findViewById(R.id.footerLayout);
        footerLayout.setOnClickListener(this);


        tv_regDate = (MaterialTextView) findViewById(R.id.tv_regDate);
        tv_regDate.setOnClickListener(this);
        if (!regDateValue.equals("")) {
            tv_regDate.setText(regDateValue);
            tv_regDate.startLabelAnimation();
        }
        TextView regMonth = (TextView) findViewById(R.id.registrationMonth);
        regMonth.setText(CommonUtils.getReplacementString(this, R.string.month, ApplicationController.monthShortMap.get(insuranceInspectedCarData.getRegMonth())));

        TextView regYear = (TextView) findViewById(R.id.registrationYear);
        regYear.setText(CommonUtils.getReplacementString(this, R.string.registrationYear, insuranceInspectedCarData.getModelYear()));


        rGrpCngLpgType = (RadioGroup) findViewById(R.id.rGrpcngLpgType);
        rGrpCngLpgType.setOnCheckedChangeListener(this);

        rb_yesCngLpg = (RadioButton) findViewById(R.id.yesCngLpg);
        rb_noCngLpg = (RadioButton) findViewById(R.id.noCngLpg);
        rb_companyFitted = (RadioButton) findViewById(R.id.rb_companyFitted);
        rb_externallyFitted = (RadioButton) findViewById(R.id.rb_externallyFitted);

        rg_kitType = (RadioGroup) findViewById(R.id.rg_kitType);
        rg_kitType.setOnCheckedChangeListener(this);

        et_CngLpgKitValue = (MaterialEditText) findViewById(R.id.et_CngLpgKitValue);


        if (fitmentType.equalsIgnoreCase("NA")) {
            rb_noCngLpg.setChecked(true);
            findViewById(R.id.kitTypeLayout).setVisibility(View.GONE);
            enableRadioGroups(false);

        } else {
            if (fitmentType.equalsIgnoreCase(Constants.FITMENT_COMPANY_FITTED)) {
                findViewById(R.id.kitTypeLayout).setVisibility(View.VISIBLE);
                rb_companyFitted.setChecked(true);
                rb_yesCngLpg.setChecked(true);
                et_CngLpgKitValue.setVisibility(View.GONE);
                enableRadioGroups(false);

            } else if (fitmentType.equalsIgnoreCase(Constants.FITMENT_OUTSIDE)) {
                findViewById(R.id.kitTypeLayout).setVisibility(View.VISIBLE);
                rb_externallyFitted.setChecked(true);
                rb_yesCngLpg.setChecked(true);
                et_CngLpgKitValue.setVisibility(View.VISIBLE);
                enableRadioGroups(false);

            } else {

                enableRadioGroups(true);
            }
        }

        rGrpAccessories = (RadioGroup) findViewById(R.id.rGrpAccessories);
        rGrpAccessories.setOnCheckedChangeListener(this);

        insurePassengersLayout = (RelativeLayout) findViewById(R.id.insurePassengersLayout);
        rGrpInsurePassengers = (RadioGroup) findViewById(R.id.rGrpInsurePassengers);
        rGrpInsurePassengers.setOnCheckedChangeListener(this);

        if (showSeatingCapacity) {
            insurePassengersLayout.setVisibility(View.VISIBLE);
        } else {
            insurePassengersLayout.setVisibility(View.GONE);
        }

        et_accessoriesValue = (MaterialEditText) findViewById(R.id.et_accessoriesValue);


        et_expiringIDV = (MaterialEditText) findViewById(R.id.et_expiringIDV);
        et_expiringIDV.addTextChangedListener(new GenericTextWatcher(et_expiringIDV));
        et_CngLpgKitValue.addTextChangedListener(new GenericTextWatcher(et_CngLpgKitValue));
        et_accessoriesValue.addTextChangedListener(new GenericTextWatcher(et_accessoriesValue));
        // tv_requesttype.setText(requestTypeList.get(0).getValue());


    }

    void enableRadioGroups(boolean value) {
        rb_yesCngLpg.setEnabled(value);
        rb_noCngLpg.setEnabled(value);
        rb_companyFitted.setEnabled(value);
        rb_externallyFitted.setEnabled(value);

    }

    private void updateDisplay() {
        currentDate = new StringBuilder().append(year).append("-")
                .append(month + 1).append("-").append(day).toString();

        Log.i("DATE", currentDate);
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
            if (Integer.parseInt(tempArray[1]) > 0) {
                return true;
            } else
                return false;
        } else
            return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CommonUtils.startActivityTransition(IssuePolicyForInspctdCarsActivity.this, Constants.TRANSITION_RIIGHT);
    }

    private FilterQueryProvider mmFilterQueryProvider = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getCursor(constraint);
        }
    };

    private Cursor getCursor(CharSequence constraint) {
        Cursor cursor;
        MakeModelVersionDB db = ApplicationController.getMakeModelVersionDB();
        cursor = db.getModelRecords(constraint, false);
        return cursor;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    @Override
    public void onClick(View view) {


        switch (view.getId()) {


            case R.id.tv_regDate:
              /*  if (!datePickerDialog.isVisible()) {
                    showDatePickerDialog();
                }*/

                final ListPopupWindow regDatePopupWindow = new ListPopupWindow(this);

                regDatePopupWindow.setAdapter(dateAdapter);
                regDatePopupWindow.setModal(true);
                regDatePopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
                regDatePopupWindow.setAnchorView(findViewById(R.id.tv_regDate));
                regDatePopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
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

            case R.id.footerLayout:

                mValidator.validate();
                break;

            case R.id.tv_requesttype:
                final ListPopupWindow reqTypePopupWindow = new ListPopupWindow(this);
                reqTypePopupWindow.setAdapter(requestTypeAdapter);
                reqTypePopupWindow.setModal(true);
                reqTypePopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
                reqTypePopupWindow.setAnchorView(findViewById(R.id.tv_requesttype));
                reqTypePopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
                reqTypePopupWindow
                        .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent,
                                                    View view, int position, long id) {

                                requestTypeModel = (BasicListItemModel) parent.getAdapter().getItem(position);
                                if (requestTypeModel.getId().equals(Constants.POLICY_NOT_EXPIRED_NCB_TRANSFER)) {
                                    tv_NCB.setVisibility(View.VISIBLE);
                                    isPolicyClaimed = "0";
                                } else {
                                    tv_NCB.setVisibility(View.GONE);
                                    isPolicyClaimed = "1";
                                }
                                tv_requesttype.setText(requestTypeModel.getValue());
                                requestTypeId = requestTypeModel.getId();
                                reqTypePopupWindow.dismiss();


                            }
                        });
                reqTypePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (tv_requesttype.getText().toString().trim().isEmpty())
                            tv_requesttype.removeLabelAnimation();
                        tv_requesttype.removeLabelFocusAnimation();
                    }
                });
                tv_requesttype.post(new Runnable() {
                    @Override
                    public void run() {
                        reqTypePopupWindow.show();
                    }
                });
                break;

            case R.id.tv_NCB:
                final ListPopupWindow ncbPolicyPopUpWindow = new ListPopupWindow(
                        this);
                ncbPolicyPopUpWindow.setAdapter(ncbPolicyAdapter);
                ncbPolicyPopUpWindow.setModal(true);
                ncbPolicyPopUpWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
                ncbPolicyPopUpWindow.setAnchorView(findViewById(R.id.tv_NCB));
                ncbPolicyPopUpWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
                ncbPolicyPopUpWindow
                        .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent,
                                                    View view, int position, long id) {

                                ncbPolicyModel = (BasicListItemModel) parent
                                        .getAdapter().getItem(position);

                                tv_NCB.setText(ncbPolicyModel.getValue());
                                ncbPolicyPopUpWindow.dismiss();


                            }
                        });


                ncbPolicyPopUpWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (tv_NCB.getText().toString().trim().isEmpty())
                            tv_NCB.removeLabelAnimation();
                        tv_NCB.removeLabelFocusAnimation();
                    }
                });
                tv_NCB.post(new Runnable() {
                    @Override
                    public void run() {
                        ncbPolicyPopUpWindow.show();
                    }
                });
                break;


            case R.id.tv_regCity:
                final ListPopupWindow cityPopUpWindow = new ListPopupWindow(
                        this);
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, mInsuranceCities);
                ;
                cityPopUpWindow.setAdapter(cityAdapter);
                cityPopUpWindow.setModal(true);
                cityPopUpWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
                cityPopUpWindow.setAnchorView(findViewById(R.id.tv_regCity));
                cityPopUpWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
                cityPopUpWindow
                        .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent,
                                                    View view, int position, long id) {


                                tv_regCity.setText(mInsuranceCities.get(position));
                                city = mInsuranceCities.get(position);
                                cityPopUpWindow.dismiss();


                            }
                        });


                cityPopUpWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (tv_regCity.getText().toString().trim().isEmpty())
                            tv_regCity.removeLabelAnimation();
                        tv_regCity.removeLabelFocusAnimation();
                    }
                });
                tv_regCity.post(new Runnable() {
                    @Override
                    public void run() {
                        cityPopUpWindow.show();
                    }
                });
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // mGAHelper.sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_ADD_STOCK);
        ApplicationController.getEventBus().register(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        ApplicationController.getEventBus().unregister(this);
    }

  /*  private void showDatePickerDialog() {
        datePickerDialog.setYearRange(
                Calendar.getInstance().get(Calendar.YEAR), Calendar
                        .getInstance().get(Calendar.YEAR) + 1);
        datePickerDialog.setCloseOnSingleTapDay(false);
        datePickerDialog.setOnDateSetListener(this);
        datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);

    }*/

    /*   @Override
       public void onDateSet(DatePickerDialog datePickerDialog, int year,
                             int month, int day) {
           // Toast.makeText(this, "Date set to " + day + ", " + month + ", " +
           // year, Toast.LENGTH_SHORT).show();


           FollowupDate followupDate = new FollowupDate(year, month, day);
           tv_regDate.setText("Date: " + day + "/" + followupDate.getMonthName()
                   + "/" + year);
           formatdate = Integer.toString(year) + "-" + Integer.toString(month + 1)
                   + "-" + Integer.toString(day);
           compareDate(formatdate);

       }

       @Override
       public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day, DateType dateType) {

       }
   */
    private boolean compareDate(String selectedDateTime) {
        try {
            final Calendar calendar = Calendar.getInstance();
            String currentDate = calendar.get(Calendar.YEAR) + "-" + ((calendar.get(Calendar.MONTH)) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
            GCLog.e("" + currentDate);
            GCLog.e("" + selectedDateTime);
            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
            DateTime currentFormattedDate = dtf.parseDateTime(currentDate);
            DateTime selectedDate = dtf.parseDateTime(formatdate);
            if (!selectedDate.isBefore(currentFormattedDate)) {
                tv_regDate.setText("");

                formatdate = "";

                CommonUtils.showToast(IssuePolicyForInspctdCarsActivity.this,
                        "Selected Date should be before Current Date", Toast.LENGTH_SHORT);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (radioGroup.getId()) {
            case R.id.rGrpcngLpgType:

                if (checkedId == R.id.yesCngLpg) {
                    findViewById(R.id.kitTypeLayout).setVisibility(View.VISIBLE);
                    isCNGLPG = "1";

                } else {
                    findViewById(R.id.kitTypeLayout).setVisibility(View.GONE);
                    isCNGLPG = "0";
                }
                break;
            case R.id.rg_kitType:
                if (checkedId == R.id.rb_externallyFitted) {
                    et_CngLpgKitValue.setVisibility(View.VISIBLE);
                    kitType = Constants.EXTERNALLY_FITTED;

                } else {
                    et_CngLpgKitValue.setVisibility(View.GONE);
                    kitType = Constants.COMPNY_FITTED;
                }
                break;
            case R.id.rGrpAccessories:
                if (checkedId == R.id.rb_accessoriesYes) {
                    findViewById(R.id.et_accessoriesValue).setVisibility(View.VISIBLE);
                    isAccessories = "1";

                } else {
                    findViewById(R.id.et_accessoriesValue).setVisibility(View.GONE);
                    isAccessories = "0";
                }


                break;
            case R.id.rGrpInsurePassengers:
                if (checkedId == R.id.rb_insurePassengersYes) {

                    isPassengerInsured = "1";

                } else {
                    isPassengerInsured = "0";

                }


                break;


        }
    }

    @Override
    public void onValidationSucceeded() {
        makeHashMap();
    }

    void makeHashMap() {
        mParams = new HashMap<String, String>();
        mParams.put(Constants.INSURANCE_AGENT_ID, mAgentId);
        mParams.put(Constants.CAR_ID, carId);
        mParams.put(Constants.CAR_DEALER_ID, "" + CommonUtils.getIntSharedPreference(IssuePolicyForInspctdCarsActivity.this, Constants.UC_DEALER_ID, -1));
        mParams.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        mParams.put(Constants.REQUEST_TYPE, requestTypeId);
        mParams.put(Constants.ACTION, Constants.PUT_NEW_INSURANCE_CASE);
        mParams.put(Constants.CAR_MAKE_ID, makeId);
        mParams.put(Constants.FILTER_MAKE, make);
        mParams.put(Constants.FILTER_MODEL, model);
        mParams.put(Constants.CAR_VERSION, version);
        mParams.put(Constants.CAR_REG_DATE, tv_regDate.getText().toString()
                + "-" + insuranceInspectedCarData.getRegMonth()
                + "-" + insuranceInspectedCarData.getModelYear());
        mParams.put(Constants.CITY_METHOD, city);
        mParams.put(Constants.IS_CNG_LPG, isCNGLPG);
        mParams.put(Constants.CNG_PNG_VALUE, et_CngLpgKitValue.getText().toString().trim().replace(",", ""));
        mParams.put(Constants.KIT_TYPE, rGrpCngLpgType.getCheckedRadioButtonId() == R.id.yesCngLpg ? kitType : "");
        mParams.put(Constants.CAR_ACCESSORIES, isAccessories);
        mParams.put(Constants.ACCESSORIES_VALUE, et_accessoriesValue.getText().toString().trim().replace(",", ""));
        mParams.put(Constants.EXPIRING_IDV, et_expiringIDV.getText().toString().trim().replace(",", ""));
        mParams.put(Constants.CLAIMED_CURRENT_POLICY, isPolicyClaimed);

        mParams.put(Constants.NCB, tv_NCB.getVisibility() == View.VISIBLE ? tv_NCB.getText().toString() : "0");
        mParams.put(Constants.INSURED_PASSENGERS, isPassengerInsured);
        mParams.put(Constants.SEAT_CAPACITY, seatCapacity);
        mParams.put(Constants.POWER, power);
        mParams.put(Constants.FUEL_TYPE, fuelType);
        mParams.put(Constants.REPORT_URL, reportUrl);
        mParams.put(Constants.RC_URL, rcUrl);
        makeInsuranceCasesRequest(true, 1);
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {

        if (failedView instanceof EditText) {
            failedView.requestFocus();
            ((EditText) failedView).setError(failedRule.getFailureMessage());
            CommonUtils.shakeView(this, failedView);
            return;
        }

        View v = (View) failedView.getParent();
        CommonUtils.showToast(this, failedRule.getFailureMessage(), Toast.LENGTH_SHORT);
        if (v != null)
            CommonUtils.scrollTo(scrollView, v);
        else
            CommonUtils.scrollTo(scrollView, failedView);
        CommonUtils.shakeView(this, failedView);
    }

    @Override
    public void onViewValidationSucceeded(View succeededView) {

    }

    private void formLists() {

        for (Map.Entry<String, String> entry : ApplicationController.inspectedCarsReqType
                .entrySet()) {
            BasicListItemModel listItem = new BasicListItemModel(
                    entry.getKey(), entry.getValue());
            requestTypeList.add(listItem);
        }
        requestTypeList.trimToSize();
        requestTypeAdapter = new BasicListItemAdapter(this, requestTypeList);


        for (Map.Entry<String, String> entry : ApplicationController.inspectedCarsNcbPolicy
                .entrySet()) {
            BasicListItemModel listItem = new BasicListItemModel(
                    entry.getKey(), entry.getValue());
            ncbPolicyList.add(listItem);
        }
        ncbPolicyList.trimToSize();
        ncbPolicyAdapter = new BasicListItemAdapter(this, ncbPolicyList);

        DateTime dateTime = new DateTime(Integer.parseInt(insuranceInspectedCarData.getModelYear()),
                Integer.parseInt(insuranceInspectedCarData.getRegMonth()), 1, 0, 0);

        registrationDateList = new ArrayList<>();
        for (int i = dateTime.dayOfMonth().getMinimumValue(); i <= dateTime.dayOfMonth().getMaximumValue(); ++i) {
            registrationDateList.add(new BasicListItemModel(String.valueOf(i), String.valueOf(i)));
        }
        registrationDateList.trimToSize();
        dateAdapter = new BasicListItemAdapter(this, registrationDateList);


    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
                if (insuranceInspectedCarData.getRegMonth() != null && !insuranceInspectedCarData.getRegMonth().trim().equals("")) {
                    Calendar cal = Calendar.getInstance();
                    DatePickerDialog dialog = new DatePickerDialog(this, myDateSetListener, year, month,
                            day);
                    cal.set(Calendar.MONTH, Integer.parseInt(insuranceInspectedCarData.getRegMonth()) - 1);

                    cal.set(Calendar.YEAR, Integer.parseInt(insuranceInspectedCarData.getRegYear()));

                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    dialog.getDatePicker().setMinDate(cal.getTimeInMillis() - 1000);

                    // code to set max date
                    cal = Calendar.getInstance();

                    cal.set(Calendar.MONTH, Integer.parseInt(insuranceInspectedCarData.getRegMonth()) - 1);

                    cal.set(Calendar.YEAR, Integer.parseInt(insuranceInspectedCarData.getRegYear()));
                    cal.set(Calendar.DAY_OF_MONTH, cal.getMaximum(Calendar.DAY_OF_MONTH));
                    dialog.getDatePicker().setMaxDate(cal.getTimeInMillis() - 1000);
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if (tv_regDate.getText().toString().trim().isEmpty())
                                tv_regDate.removeLabelAnimation();
                            tv_regDate.removeLabelFocusAnimation();
                        }
                    });
                    return dialog;
                } else {
                    Calendar cal = Calendar.getInstance();
                    DatePickerDialog dialog = new DatePickerDialog(this, myDateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH));
                    return dialog;
                }

        }
        return null;
    }

 /*   @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case DATE_DIALOG_ID:

                Calendar cal=Calendar.getInstance();

                DatePickerDialog dialog = new DatePickerDialog(this, myDateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                //  dialog.getDatePicker().setMinDate(new Date("Sat Feb 17 2012").getTime());
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (tv_regDate.getText().toString().trim().isEmpty())
                            tv_regDate.removeLabelAnimation();
                        tv_regDate.removeLabelFocusAnimation();
                    }
                });

        }
    }*/

    private void makeInsuranceCasesRequest(final boolean showFullPageError, final int pageNo) {
        progressDialog.setCancelable(false);
        progressDialog.show();
        //  HashMap<String, String> params = new HashMap<>();
        // params.put(Constants.INSURANCE_AGENT_ID, "4");
        mParams.put(Constants.METHOD_LABEL, Constants.INSURANCE_CASE);
        // mParams.put(Constants.PAGE_NO, String.valueOf(pageNo));


        RetrofitRequest.putNewInsuranceCase(
                mParams, new Callback<NewInsuranceCaseModel>() {

                    @Override
                    public void success(NewInsuranceCaseModel insuranceCaseStatusModel, retrofit.client.Response response) {
                        if (progressDialog != null)
                            progressDialog.dismiss();

                        if ("T".equalsIgnoreCase(insuranceCaseStatusModel.getStatus())) {
                            Intent intent = new Intent(IssuePolicyForInspctdCarsActivity.this, DealerQuoteScreen.class);
                            // intent.putExtra(Constants.INSURANCE_CASE_ID, insuranceCaseStatusModel.getInsuranceCaseId());
                            intent.putExtra(Constants.INSURANCE_CITIES, mInsuranceCities);
                            intent.putExtra(Constants.INSURANCE_IS_NEW_CAR, false);
                            intent.putExtra(Constants.INSURANCE_REQUEST_TYPE, requestTypeId);
                            intent.putExtra(Constants.INSURANCE_INSPECTED_CAR_DATA, insuranceInspectedCarData);
                            intent.putExtra(Constants.AGENT_ID, mAgentId);
                            startActivityForResult(intent, Constants.INSURANCE_CASE_FOR_INSPECTED_CAR);
                            CommonUtils.startActivityTransition(IssuePolicyForInspctdCarsActivity.this, Constants.TRANSITION_LEFT);
                        } else { // status false
                            CommonUtils.showToast(IssuePolicyForInspctdCarsActivity.this, "Error: " + insuranceCaseStatusModel.getMessage(), Toast.LENGTH_SHORT);

                        }


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (progressDialog != null)
                            progressDialog.dismiss();

                        CommonUtils.showErrorToast(IssuePolicyForInspctdCarsActivity.this, error, Toast.LENGTH_SHORT);

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
                }
                break;
        }
    }
}
