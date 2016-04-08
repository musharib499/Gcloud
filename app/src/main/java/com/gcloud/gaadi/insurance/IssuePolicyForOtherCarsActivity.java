package com.gcloud.gaadi.insurance;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.BasicListItemAdapter;
import com.gcloud.gaadi.adapter.MakeModelVersionAdapter;
import com.gcloud.gaadi.adapter.ModelVersionAdapter;
import com.gcloud.gaadi.annotations.Required;
import com.gcloud.gaadi.annotations.TextRule;
import com.gcloud.gaadi.annotations.ValueChecker;
import com.gcloud.gaadi.annotations.rules.Rule;
import com.gcloud.gaadi.annotations.rules.Validator;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.MakeModelType;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.events.CancelMakeEvent;
import com.gcloud.gaadi.events.MakeModelSelectedEvent;
import com.gcloud.gaadi.model.BasicListItemModel;
import com.gcloud.gaadi.model.FinanceCompany;
import com.gcloud.gaadi.model.InsuranceInspectedCarData;
import com.gcloud.gaadi.model.NewInsuranceCaseModel;
import com.gcloud.gaadi.model.RTOModel;
import com.gcloud.gaadi.model.VersionObject;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.BaseActivity;
import com.gcloud.gaadi.ui.CustomMaterialAutoCompleteTxtVw;
import com.gcloud.gaadi.ui.GCProgressDialog;
import com.gcloud.gaadi.ui.customviews.CustomCalender;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.MaterialTextView;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class IssuePolicyForOtherCarsActivity extends BaseActivity
        implements AdapterView.OnItemClickListener, View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, Validator.ValidationListener {
    public static final String DATEPICKER_TAG = "datepicker";
    public static final int DATE_PICKER_ID_END = 12;
    private static final int DATE_DIALOG_ID = 1;
    GCProgressDialog progressDialog;
    Validator mValidator;
    RadioGroup rGrpCngLpgType, rg_kitType, rGrpClaimedCurrentPolicy;
    String formatdate = "";
    @Required(order = 8, messageResId = R.string.reg_num_req)
    MaterialEditText et_regNum;
    MaterialTextView tv_regCity;
    /*  @Required(order = 9, messageResId = R.string.bank_valie_req)
      MaterialTextView tv_bankList;*/
    @Required(order = 9, messageResId = R.string.ncb_valie_req)
    MaterialTextView tv_NCB;
    @Required(order = 10, messageResId = R.string.kit_value_required)
    @ValueChecker(order = 11, maxValue = 50000, replaceChars = ",.", messageResId = R.string.max_value_50000)
    MaterialEditText et_CngLpgKitValue;
    BasicListItemModel requestTypeModel;
    android.app.DatePickerDialog.OnDateSetListener myDateSetListener;
    HashMap<String, String> mParams;
    boolean brandNewCar;
    String makeId = "", model = "", make = "", version = "", city = "", fuelType = "";

   /* @Required(order = 5, messageResId = R.string.error_expiring_idv_required)
    @ValueChecker(order = 6, minValue = 100000, replaceChars = ",.", messageResId = R.string.min_value_is_100000)
    MaterialEditText et_expiringIDV;*/
   String isCNGLPG = "0", kitType = "", isPolicyClaimed = "0";
    String ncbValue = "", regDateValue = "";
    String[] banksList;
    @Required(order = 1, messageResId = R.string.error_make_model_required)
    @TextRule(order = 2, minLength = 1, messageResId = R.string.error_make_model_required)
    private CustomMaterialAutoCompleteTxtVw mMakeModel;
    @Required(order = 3, messageResId = R.string.error_model_version_required)
    @TextRule(order = 4, minLength = 1, messageResId = R.string.error_model_version_required)
    private MaterialTextView mModelVersion;
    // MaterialTextView tv_requesttype;
    private MakeModelVersionAdapter makeModelVersionAdapter;

   /* @Required(order = 12, messageResId = R.string.accessories_value_req)
    @ValueChecker(order = 13, maxValue = 100000, replaceChars = ",.", messageResId = R.string.max_value_100000)
    MaterialEditText et_accessoriesValue;*/
   private ModelVersionAdapter modelVersionAdapter;
    private VersionObject stock;
    private DatePickerDialog datePickerDialog;
    private ScrollView scrollView;
    private boolean isClearMake = false;
    private InsuranceInspectedCarData insuranceInspectedCarData;
    private ArrayList<FinanceCompany> financeCompanyList;
    @Required(order = 7, messageResId = R.string.reg_date_req)
    private MaterialTextView tv_regDate;
    @Required(order = 12, messageResId = R.string.error_previous_policy_end_date_required)
    private MaterialTextView prevPolicyEndDate;
    // LinearLayout footerLayout;
    private BasicListItemAdapter requestTypeAdapter;
    private ArrayList<BasicListItemModel> requestTypeList = new ArrayList<BasicListItemModel>();
    private int year;
    private int month;
    private int day;
    private String currentDate = "", requestTypeId = "Rollover", finalModelName = "";
    private ArrayList<String> mInsuranceCities;
    private String mAgentId = "";
    private DatePickerDialog dialog;
    private FilterQueryProvider mmFilterQueryProvider = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getCursor(constraint);
        }
    };
    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // Show selected date
            prevPolicyEndDate.setText(new StringBuilder().append(day < 10 ? "0" + day : day)
                    .append("/").append(ApplicationController.monthShortMap.get("" + (month + 1)))
                    .append("/").append(year));

            CommonUtils.setStringSharedPreference(IssuePolicyForOtherCarsActivity.this,
                    Constants.PREVIOUS_POLICY_END_DATE,
                    /*("" + year + "-" + (month + 1) + "-" + day)*/
                    prevPolicyEndDate.getText().toString());
        }

    };
    private String modelId = "-1", versionId = "-1";

    /**
     * @return current Date from Calendar in dd/MM/yyyy format
     * adding 1 into month because Calendar month starts from zero
     */
    public static String getDate(Calendar cal) {
        return "" + cal.get(Calendar.DATE) + "/" +
                (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
    }

    /**
     * @return current Date from Calendar in HH:mm:SS format
     */
    public static String getTime(Calendar cal) {
        return "" + cal.get(Calendar.HOUR_OF_DAY) + ":" +
                (cal.get(Calendar.MINUTE)) + ":" + cal.get(Calendar.SECOND);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(Constants.INSURANCE_CITIES, mInsuranceCities);
        outState.putString(Constants.AGENT_ID, mAgentId);
        outState.putSerializable(Constants.REQUEST_TYPE_MODEL, requestTypeModel);

        outState.putString(Constants.CAR_REG_DATE, regDateValue);
        outState.putSerializable(Constants.REQUEST_TYPE, requestTypeModel);
        outState.putString(Constants.NCB, ncbValue);
        outState.putString(Constants.CITY_NAME, city);
        outState.putSerializable(Constants.FINANCE_COMPANY, financeCompanyList);
        outState.putSerializable(Constants.MODEL_DATA, stock);
        outState.putString(Constants.REQUEST_TYPE_ID, requestTypeId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mInsuranceCities = savedInstanceState.getStringArrayList(Constants.INSURANCE_CITIES);
        mAgentId = savedInstanceState.getString(Constants.AGENT_ID);
        requestTypeModel = (BasicListItemModel) savedInstanceState.getSerializable(Constants.REQUEST_TYPE_MODEL);
        stock = (VersionObject) savedInstanceState.getSerializable(Constants.MODEL_DATA);
        regDateValue = savedInstanceState.getString(Constants.CAR_REG_DATE);
        financeCompanyList = (ArrayList<FinanceCompany>) savedInstanceState.getSerializable(Constants.FINANCE_COMPANY);
        ncbValue = savedInstanceState.getString(Constants.NCB);

        city = savedInstanceState.getString(Constants.CITY_NAME);

        requestTypeId = savedInstanceState.getString(Constants.REQUEST_TYPE_ID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.issue_policy_for_other_cars, frameLayout);
        progressDialog = new GCProgressDialog(this, this);
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
        //   mActionBar = getSupportActionBar();
        toolbar.setVisibility(View.GONE);
        // mActionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        financeCompanyList = (ArrayList<FinanceCompany>) intent.getSerializableExtra(Constants.FINANCE_COMPANY);
        mInsuranceCities = intent.getStringArrayListExtra(Constants.INSURANCE_CITIES);
        mAgentId = intent.getStringExtra(Constants.AGENT_ID);

        if (savedInstanceState != null) {
            city = savedInstanceState.getString(Constants.CITY_NAME);
            ncbValue = savedInstanceState.getString(Constants.NCB);

            regDateValue = savedInstanceState.getString(Constants.CAR_REG_DATE);

            requestTypeId = savedInstanceState.getString(Constants.REQUEST_TYPE_ID);
            stock = (VersionObject) savedInstanceState.getSerializable(Constants.MODEL_DATA);

        }


        scrollView = (ScrollView) findViewById(R.id.scrollView);
        formLists();




        myDateSetListener = new android.app.DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int i, int j, int k) {

                year = i;
                month = j;
                day = k;
                updateDisplay();
                tv_regDate.setText(currentDate);
            }
        };


        et_regNum = (MaterialEditText) findViewById(R.id.et_RegNum);
        et_regNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String str = (String.valueOf(charSequence)).replace(" ", "");

                if (str.length() == 0) {
                    tv_regCity.setText("");
                }
                if (str.length() == 4) {
                    makeRTORequest(str);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        tv_regCity = (MaterialTextView) findViewById(R.id.tvRTO);


        makeModelVersionAdapter = new MakeModelVersionAdapter(this, null);
        mMakeModel = (CustomMaterialAutoCompleteTxtVw) findViewById(R.id.makeModel);
        mMakeModel.setType(MakeModelType.MAKE);
        mMakeModel.setThreshold(1);
        mMakeModel.setAdapter(makeModelVersionAdapter);
        makeModelVersionAdapter.setFilterQueryProvider(mmFilterQueryProvider);
        mMakeModel.setOnItemClickListener(this);



        findViewById(R.id.tv_next).setOnClickListener(this);

        mModelVersion = (MaterialTextView) findViewById(R.id.version);

        if (stock != null && stock.getModel() != null) {

            mModelVersion.setClickable(true);
            mModelVersion.setEnabled(true);
            mModelVersion.setText(stock.getVersionName());
            mModelVersion.startLabelAnimation();
            mMakeModel.setCompoundDrawablesWithIntrinsicBounds(
                    ApplicationController.makeLogoMap.get(Integer.parseInt(stock.getMakeId())),
                    0,
                    R.drawable.close_layer_dark,
                    0);
            mMakeModel.setText(stock.getModel());
            makeId = stock.getMakeId();
            make = stock.getMake();
            model = stock.getModel();
            modelId = stock.getModelId();
            version = stock.getVersionName();
            versionId = stock.getVersionid();
            fuelType = stock.getFuelType();
        } else {
            mModelVersion.setClickable(false);
            mModelVersion.setEnabled(false);

        }

        rGrpClaimedCurrentPolicy = (RadioGroup) findViewById(R.id.rGrpClaimedCurrentPolicy);
        rGrpClaimedCurrentPolicy.setOnCheckedChangeListener(this);

        tv_regDate = (MaterialTextView) findViewById(R.id.tv_regDate);

        /*tv_regDate.setmActivity(this);
        tv_regDate.setSimpleDateFormate("dd/MMM/yyyy");*/

        tv_regDate.setOnClickListener(this);

        if (!regDateValue.equals("")) {
            tv_regDate.setText(regDateValue);
           // tv_regDate.startLabelAnimation();
        }

        tv_NCB = (MaterialTextView) findViewById(R.id.tv_NCB);
        tv_NCB.setOnClickListener(this);

        if (!ncbValue.equals("")) {
            tv_NCB.setText(ncbValue);
            tv_NCB.startLabelAnimation();
        }

        rGrpCngLpgType = (RadioGroup) findViewById(R.id.rGrpcngLpgType);
        rGrpCngLpgType.setOnCheckedChangeListener(this);

        rg_kitType = (RadioGroup) findViewById(R.id.rg_kitType);
        rg_kitType.setOnCheckedChangeListener(this);
        prevPolicyEndDate = (MaterialTextView) findViewById(R.id.prevPolicyEndDate);
        /*prevPolicyEndDate.setmActivity(this);
        prevPolicyEndDate.setSimpleDateFormate("dd/MMM/yyyy");*/
        prevPolicyEndDate.setOnClickListener(this);
      /*  rGrpAccessories = (RadioGroup) findViewById(R.id.rGrpAccessories);
        rGrpAccessories.setOnCheckedChangeListener(this);
*/


        et_CngLpgKitValue = (MaterialEditText) findViewById(R.id.et_CngLpgKitValue);
        // et_accessoriesValue = (MaterialEditText) findViewById(R.id.et_accessoriesValue);

        et_CngLpgKitValue.addTextChangedListener(new GenericTextWatcher(et_CngLpgKitValue));
        //  et_accessoriesValue.addTextChangedListener(new GenericTextWatcher(et_accessoriesValue));
    }

    private void makeRTORequest(String str) {
        findViewById(R.id.rtoProgressBar).setVisibility(View.VISIBLE);
        HashMap<String, String> rtoParams = new HashMap<>();

        rtoParams.put(Constants.METHOD_LABEL, Constants.GET_RTO);
        rtoParams.put(Constants.REG_NO, str);
        RetrofitRequest.makeRTORequest(IssuePolicyForOtherCarsActivity.this, rtoParams, new Callback<RTOModel>() {
            @Override
            public void success(RTOModel rtoResponse, Response response) {
              /*  if (progressDialog != null)
                    progressDialog.dismiss();*/
                findViewById(R.id.rtoProgressBar).setVisibility(View.GONE);
                if (rtoResponse.getStatus().equals("T")) {
                    if (rtoResponse.getRto().equals("")) {
                        CommonUtils.showToast(IssuePolicyForOtherCarsActivity.this, " RTO not available ", Toast.LENGTH_SHORT);
                        return;
                    }

                    tv_regCity.setText(rtoResponse.getRto());

                    tv_regCity.removeLabelFocusAnimation();
                    // tv_regCity.removeLabelFocusAnimation();
                } else {
                    CommonUtils.showToast(IssuePolicyForOtherCarsActivity.this, rtoResponse.getMessage(), Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void failure(RetrofitError error) {
               /* if (progressDialog != null)
                    progressDialog.dismiss();*/
                findViewById(R.id.rtoProgressBar).setVisibility(View.GONE);
                CommonUtils.showErrorToast(IssuePolicyForOtherCarsActivity.this, error, Toast.LENGTH_SHORT);
            }
        });
    }

    private void updateDisplay() {
        currentDate = "" + day + "/" + ApplicationController.monthShortMap.get("" + (month + 1)) + "/" + year;/*new StringBuilder().append(day).append("/")
                .append(ApplicationController.monthShortMap.get(month+!)).append("/").append(year).toString();*/
        regDateValue = currentDate;
        Log.i("DATE", currentDate);
    }

    private void formLists() {

        for (Map.Entry<String, String> entry : ApplicationController.otherCarsReqType
                .entrySet()) {
            BasicListItemModel listItem = new BasicListItemModel(
                    entry.getKey(), entry.getValue());
            requestTypeList.add(listItem);
        }
        requestTypeList.trimToSize();
        requestTypeAdapter = new BasicListItemAdapter(this, requestTypeList);

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
        CommonUtils.startActivityTransition(IssuePolicyForOtherCarsActivity.this, Constants.TRANSITION_RIIGHT);
    }

    private Cursor getCursor(CharSequence constraint) {
        Cursor cursor;
        MakeModelVersionDB db = ApplicationController.getMakeModelVersionDB();
        cursor = db.getModelRecords(constraint, false);
        return cursor;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getAdapter() instanceof MakeModelVersionAdapter) {


            Cursor cursor = (Cursor) mMakeModel.getAdapter().getItem(position);

            int rowid = cursor.getInt(cursor.getColumnIndex(MakeModelVersionDB.ID));
            makeId = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MAKEID));
            make = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MAKENAME));
            modelId = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MODELID));
            String modelName = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MODELNAME));
            model = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MODELNAME));
            String makeModel = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MMV_COLUMN_MAKE_MODEL));


            stock = new VersionObject();
            stock.setId(rowid);
            stock.setMakeId(makeId);
            stock.setMake(make);
            stock.setModelId(modelId);
            stock.setModel(modelName);
            stock.setMakeModel(makeModel);

            finalModelName = modelName;
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

        }
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

    @Subscribe
    public void onMakeModelSelected(MakeModelSelectedEvent event) {

        stock = event.getModel();
        stock.setVersionName("");
        stock.setVersionid("");
        mModelVersion.setText("");
        mModelVersion.setEnabled(true);
        mModelVersion.setClickable(true);
        mModelVersion.setOnClickListener(this);

        /*mModelVersion.setOnItemClickListener(this);*/
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.version:
                final ListPopupWindow popupWindow = new ListPopupWindow(this);
                Cursor cursor1 = null;
                if (stock != null) {
                    cursor1 = ApplicationController.getMakeModelVersionDB().getVersionForMakeModel(stock);
                }
                modelVersionAdapter = new ModelVersionAdapter(this, cursor1);
                popupWindow.setModal(true);
                popupWindow.setAnchorView(findViewById(R.id.version));
                popupWindow.setAdapter(modelVersionAdapter);
                popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
                        version = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.VERSIONNAME));
                        String version = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.VERSIONNAME));
                        versionId = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.VERSIONID));
                        fuelType = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.FUEL_TYPE));
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
                            mModelVersion.setText(version);
                            popupWindow.dismiss();


                        }
                        if (mModelVersion.getText().toString().trim().isEmpty()) {
                            mModelVersion.removeLabelAnimation();
                        }
                        mModelVersion.removeLabelFocusAnimation();
                    }


                });
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                       if (mModelVersion.getText().toString().trim().isEmpty()) {
                           mModelVersion.removeLabelAnimation();
                       }
                        mModelVersion.removeLabelFocusAnimation();

                    }
                });
                mModelVersion.post(new Runnable() {
                    @Override
                    public void run() {
                        popupWindow.show();
                    }
                });
                break;

            case R.id.tv_regDate:

              //  showDialog(DATE_DIALOG_ID);
                Calendar cal = Calendar.getInstance();
                /*cal.setTimeZone(TimeZone.getTimeZone("GMT"));

                cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
                cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
                cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));*/
                cal.add(Calendar.YEAR, -9);
                Date minDate = new Date(cal.getTimeInMillis());
                cal = Calendar.getInstance();
                /*cal.setTimeZone(TimeZone.getTimeZone("GMT"));
                cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
                cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
                cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));*/
                cal.add(Calendar.MONTH, -9);
                Date maxDate = new Date(cal.getTimeInMillis());
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setShowTimeTab(false)
                        .setMinDate(minDate)
                        .setMaxDate(maxDate)
                        .setListener(new SlideDateTimeListener() {
                            @Override
                            public void onDateTimeSet(Date date) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
                                tv_regDate.setText(dateFormat.format(date));
                                tv_regDate.removeLabelFocusAnimation();
                            }

                            @Override
                            public void onDateTimeCancel() {
                                if (tv_regDate.getText().toString().trim().isEmpty()) {
                                    tv_regDate.removeLabelAnimation();
                                }
                                tv_regDate.removeLabelFocusAnimation();
                            }
                        })
                        .build().show();
                /*onPrepareDialog(tv_regDate);

                tv_regDate.onDailogs();*/

                break;
            case R.id.prevPolicyEndDate:

                SlideDateTimePicker.Builder builder = new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setShowTimeTab(false);

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, 44);

                builder.setMaxDate(new Date(calendar.getTimeInMillis()));

                calendar = Calendar.getInstance();
                builder.setMinDate(new Date(calendar.getTimeInMillis() - 1000));

                builder.setListener(new SlideDateTimeListener() {
                    @Override
                    public void onDateTimeSet(Date date) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
                        prevPolicyEndDate.setText(dateFormat.format(date));
                        prevPolicyEndDate.removeLabelFocusAnimation();
                        CommonUtils.setStringSharedPreference(IssuePolicyForOtherCarsActivity.this,
                                Constants.PREVIOUS_POLICY_END_DATE,
                    /*("" + year + "-" + (month + 1) + "-" + day)*/
                                prevPolicyEndDate.getText().toString());
                    }

                    @Override
                    public void onDateTimeCancel() {
                        if (prevPolicyEndDate.getText().toString().trim().isEmpty()) {
                            prevPolicyEndDate.removeLabelAnimation();
                        }
                        prevPolicyEndDate.removeLabelFocusAnimation();
                    }
                })
                        .build().show();

                /*prevPolicyEndDate.afterchnage(this);
                setPrevPolicyEndDate(prevPolicyEndDate);
                prevPolicyEndDate.onDailogs();*/


                break;

            case R.id.tv_next:
                mValidator.validate();
                break;

            case R.id.iv_actionBack:
                onBackPressed();
                break;



         /*   case R.id.tvRTO:
                final ListPopupWindow cityPopUpWindow = new ListPopupWindow(
                        this);
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, mInsuranceCities);
                ;
                cityPopUpWindow.setAdapter(cityAdapter);
                cityPopUpWindow.setModal(true);
                cityPopUpWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
                cityPopUpWindow.setAnchorView(findViewById(R.id.tvRTO));
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
*/
            case R.id.tv_NCB:
              /*  if (tv_regDate.getText().toString().trim().equals("")) {
                    CommonUtils.showToast(IssuePolicyForOtherCarsActivity.this, "Select a registration date first", Toast.LENGTH_SHORT);
                    return;
                }*/
                final ListPopupWindow ncbPolicyPopUpWindow = new ListPopupWindow(
                        this);

               /* String regDate;
                int currentYr, regDateYr;
                regDate = tv_regDate.getText().toString();
                String[] mDate = regDate.split("/");
                *//*SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy");
                Date formattedRegDate = null;
                try {
                    formattedRegDate = parser.parse(regDate);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                GCLog.e("Reg Date", regDate);
                GCLog.e("Formatted Reg Date", formattedRegDate.toString());
                calendar.setTime(formattedRegDate);*//*
                Calendar calendar = Calendar.getInstance();
                calendar.set(Integer.valueOf(mDate[2]),
                        Integer.valueOf(ApplicationController.monthNameMap.get(mDate[1])),
                        Integer.valueOf(mDate[0]));
                calendar.get(Calendar.DAY_OF_MONTH); //Day of the month :)
                regDateYr = calendar.get(Calendar.YEAR);
                Calendar cal = Calendar.getInstance();
                currentYr = cal.get(Calendar.YEAR);


                int numOfYrs = 1;
                numOfYrs = currentYr - regDateYr;*/
                String[] str = null;
                str = ApplicationController.otherCarsNcbPolicy.get("5");
              /*  switch (numOfYrs) {
                    case 1:
                        str = ApplicationController.otherCarsNcbPolicy.get("1");
                        break;
                    case 2:
                        str = ApplicationController.otherCarsNcbPolicy.get("2");
                        break;
                    case 3:
                        str = ApplicationController.otherCarsNcbPolicy.get("3");
                        break;
                    case 4:
                        str = ApplicationController.otherCarsNcbPolicy.get("4");
                        break;

                    default:
                        str = ApplicationController.otherCarsNcbPolicy.get("5");
                        break;
                }*/
                final ArrayAdapter<String> arr = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, str);
                ncbPolicyPopUpWindow.setAdapter(arr);
                ncbPolicyPopUpWindow.setModal(true);
                ncbPolicyPopUpWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
                ncbPolicyPopUpWindow.setAnchorView(findViewById(R.id.tv_NCB));
                ncbPolicyPopUpWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
                ncbPolicyPopUpWindow
                        .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent,
                                                    View view, int position, long id) {

                                /*ncbPolicyModel = (BasicListItemModel) parent
                                        .getAdapter().getItem(position);
*/
                                tv_NCB.setText(arr.getItem(position));
                                ncbValue = arr.getItem(position);
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
          /*  case R.id.tv_bankList:
                final ArrayAdapter<String> bankArray = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, banksList);
                final ListPopupWindow bankPopUpWindow = new ListPopupWindow(
                        this);
                bankPopUpWindow.setAdapter(bankArray);
                bankPopUpWindow.setModal(true);
                bankPopUpWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
                bankPopUpWindow.setAnchorView(findViewById(R.id.tv_bankList));
                bankPopUpWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
                bankPopUpWindow
                        .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent,
                                                    View view, int position, long id) {

                                *//*ncbPolicyModel = (BasicListItemModel) parent
                                        .getAdapter().getItem(position);
*//*
                                tv_bankList.setText(bankArray.getItem(position));

                                bankPopUpWindow.dismiss();


                            }
                        });


                bankPopUpWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (tv_bankList.getText().toString().trim().isEmpty())
                            tv_bankList.removeLabelAnimation();
                        tv_bankList.removeLabelFocusAnimation();
                    }
                });
                tv_bankList.post(new Runnable() {
                    @Override
                    public void run() {
                        bankPopUpWindow.show();
                    }
                });
                break;
*/
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

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (radioGroup.getId()) {
            case R.id.rGrpcngLpgType:

                if (checkedId == R.id.yesCngLpg) {
                    findViewById(R.id.kitTypeLayout).setVisibility(View.VISIBLE);
                    ((RadioButton) findViewById(R.id.rb_externallyFitted)).setChecked(true);
                    isCNGLPG = "1";
                    kitType = Constants.EXTERNALLY_FITTED;

                } else {
                    findViewById(R.id.kitTypeLayout).setVisibility(View.GONE);
                    isCNGLPG = "0";
                    kitType = "";
                    // et_CngLpgKitValue.setText("");
                }
                break;

          /*  case R.id.rGrpCarFinanced:

                if (checkedId == R.id.rb_carFinancedYes) {
                    tv_bankList.setVisibility(View.VISIBLE);
                    isCarFinanced="1";


                } else {
                    tv_bankList.setVisibility(View.GONE);
                    isCarFinanced="0";

                }
                break;*/

            case R.id.rg_kitType:
                if (checkedId == R.id.rb_externallyFitted) {
                    findViewById(R.id.et_CngLpgKitValue).setVisibility(View.VISIBLE);
                    kitType = Constants.EXTERNALLY_FITTED;

                } else {
                    findViewById(R.id.et_CngLpgKitValue).setVisibility(View.GONE);
                    kitType = Constants.COMPNY_FITTED;
                    // et_CngLpgKitValue.setText("");
                }
                break;

            case R.id.rGrpClaimedCurrentPolicy:
                if (checkedId == R.id.rb_ClaimPolicyNo) {

                    tv_NCB.setVisibility(View.VISIBLE);

                    isPolicyClaimed = "0";
                } else {
                    tv_NCB.setVisibility(View.GONE);
                    isPolicyClaimed = "1";
                }
                break;

        }
    }

    @Override
    public void onValidationSucceeded() {
        if (!finalModelName.equals(mMakeModel.getText().toString())) {
            onValidationFailed(mMakeModel, new Rule<View>(getString(R.string.incorrect_entry)) {
                @Override
                public boolean isValid(View view) {
                    return false;
                }
            });
            return;
        }
        makeHashMap();
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

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, myDateSetListener, year, month,
                        day);
                datePickerDialog.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
                return datePickerDialog;
            case DATE_PICKER_ID_END:
                dialog = new DatePickerDialog(this, pickerListener, year, month, day);
                dialog.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
                // Previous Policy End Date Range : From Today to {Today + 44 }
                //setPrevPolicyEndDate(dialog);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (prevPolicyEndDate.getText().toString().trim().isEmpty()) {
                            //  prevPolicyEndDate.removeLabelAnimation();
                            // prevPolicyEndDate.removeLabelFocusAnimation();
                        }
                    }
                });
                return dialog;
        }
        return null;
    }

    private void setPrevPolicyEndDate(CustomCalender dialog) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DAY_OF_MONTH, 44);

        long endTime = calendar1.getTimeInMillis();
        dialog.setMaxDate(new Date(endTime));

        Calendar calendar = Calendar.getInstance();


        long startTime = calendar.getTimeInMillis() - 1000;
        dialog.setMinDate(new Date(startTime));

    }

    public void onPrepareDialog(CustomCalender dialog) {

              /*  return new DatePickerDialog(this, myDateSetListener, year, month,
                        day);*/
                // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
                Calendar cal;



                if (requestTypeId.equals(Constants.BRAND_NEW_CAR)) {
                    String dateString;
                    cal = Calendar.getInstance();
                    cal.setTimeZone(TimeZone.getTimeZone("GMT"));
                    cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
                    cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
                    cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
                    cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 10);
                    cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));


                    dateString = getDate(cal);
                    GCLog.e(dateString);
                    dialog.setMaxDate(new Date(cal.getTimeInMillis() - 1000));

                    cal = Calendar.getInstance();

                    cal.setTimeZone(TimeZone.getTimeZone("GMT"));
                    //  ((DatePickerDialog)dialog).getDatePicker().setMaxDate(cal.getTimeInMillis()-1000);

                    cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
                    cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
                    cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
                    cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));

                    cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 10);

                    dialog.setMinDate(new Date(cal.getTimeInMillis() - 1000));


                } else {
                    cal = Calendar.getInstance();
                    cal.setTimeZone(TimeZone.getTimeZone("GMT"));

                    cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
                    cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
                    cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
                    cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
                    cal.add(Calendar.YEAR, -9);
                    dialog.setMinDate(new Date(cal.getTimeInMillis() - 1000));
                    cal = Calendar.getInstance();
                    cal.setTimeZone(TimeZone.getTimeZone("GMT"));
                    cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
                    cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
                    cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
                    cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
                    cal.add(Calendar.MONTH, -9);
                    dialog.setMaxDate(new Date(cal.getTimeInMillis() - 1000));

                }
                dialog.onDateTimeCancel();




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

            if (modelVersionAdapter != null) {
                modelVersionAdapter = new ModelVersionAdapter(this, null);
                modelVersionAdapter.notifyDataSetChanged();
                //mModelVersion.setEnabled(false);
            }
            mModelVersion.removeLabelAnimation();
            mMakeModel.setFloatingLabelAnimating(false);

        }

    }

    void makeHashMap() {
        mParams = new HashMap<>();
        insuranceInspectedCarData = new InsuranceInspectedCarData();
        insuranceInspectedCarData.setMake(make);
        insuranceInspectedCarData.setMakeId(Integer.parseInt(makeId));
        insuranceInspectedCarData.setModel(model);
        insuranceInspectedCarData.setVersion(version);
        insuranceInspectedCarData.setRegNo(et_regNum.getText().toString());
        insuranceInspectedCarData.setFuelType(fuelType);
        //String[] year = currentDate.split("/");
        String regYear = currentDate.substring(currentDate.lastIndexOf("/")+1);
        insuranceInspectedCarData.setRegYear(regYear);

        mParams.put(Constants.CAR_MAKE_ID, makeId);
        mParams.put(Constants.INSURANCE_CAR_MAKE_ID, makeId);
        mParams.put(Constants.FILTER_MAKE, make);
        mParams.put(Constants.INSURANCE_CAR_MODEL_ID, modelId);
        mParams.put(Constants.FILTER_MODEL, model);
        mParams.put(Constants.INSURANCE_CAR_VERSION_ID, versionId);
        mParams.put(Constants.CAR_VERSION, version);
        String[] mDate = tv_regDate.getText().toString().split("/");
        String mRegDate = mDate[2] + "-" + ApplicationController.monthNameMap.get(mDate[1]) + "-" + mDate[0];
        GCLog.e("format for api: " + mRegDate);
        mParams.put(Constants.CAR_REG_DATE, mRegDate);
        mParams.put("reg_no", et_regNum.getText().toString());

        mParams.put(Constants.IS_CNG_LPG, isCNGLPG);
        mParams.put(Constants.CNG_LPG_KIT_VALUE, et_CngLpgKitValue.getVisibility() == View.VISIBLE ? et_CngLpgKitValue.getText().toString().trim().replace(",", "") : "");
        mParams.put(Constants.KIT_TYPE, kitType);
        mParams.put("rto", tv_regCity.getText().toString());
        mParams.put(Constants.CLAIMED_CURRENT_POLICY, isPolicyClaimed);
        mDate = prevPolicyEndDate.getText().toString().split("/");
        mRegDate = mDate[2] + "-" + ApplicationController.monthNameMap.get(mDate[1]) + "-" + mDate[0];
        GCLog.e("format for api: " + mRegDate);
        mParams.put(Constants.PREVIOUS_POLICY_END_DATE, mRegDate);

        mParams.put(Constants.NCB, tv_NCB.getVisibility() == View.VISIBLE ? tv_NCB.getText().toString() : "0");

        makeInsuranceCasesRequest(true, 1);
    }

    private void makeInsuranceCasesRequest(final boolean showFullPageError, final int pageNo) {
        progressDialog.setCancelable(false);
        progressDialog.show();
        //  HashMap<String, String> params = new HashMap<>();
        // params.put(Constants.INSURANCE_AGENT_ID, "4");
        mParams.put(Constants.METHOD_LABEL, Constants.SAVE_OTHER_CARS_DETAILS);
        // mParams.put(Constants.PAGE_NO, String.valueOf(pageNo));


        RetrofitRequest.putNewInsuranceCase(
                mParams, new Callback<NewInsuranceCaseModel>() {

                    @Override
                    public void success(NewInsuranceCaseModel insuranceCaseStatusModel, retrofit.client.Response response) {
                        if (progressDialog != null)
                            progressDialog.dismiss();

                        if ("T".equalsIgnoreCase(insuranceCaseStatusModel.getStatus())) {
                            Intent intent = new Intent(IssuePolicyForOtherCarsActivity.this, DealerQuoteScreen.class);
                            intent.putExtra(Constants.PROCESS_ID, insuranceCaseStatusModel.getProcessId());
                            intent.putExtra(Constants.INSURANCE_CITIES, mInsuranceCities);
                            intent.putExtra(Constants.INSURANCE_IS_NEW_CAR, brandNewCar);
                            intent.putExtra(Constants.SELECTED_CASE, Constants.OTHER_CARS);
                            intent.putExtra(Constants.INSURANCE_REQUEST_TYPE, requestTypeId);
                            intent.putExtra(Constants.FINANCE_COMPANY, financeCompanyList);
                            intent.putExtra(Constants.INSURANCE_INSPECTED_CAR_DATA, insuranceInspectedCarData);
                            startActivityForResult(intent, Constants.INSURANCE_CASE_FOR_INSPECTED_CAR);
                            CommonUtils.startActivityTransition(IssuePolicyForOtherCarsActivity.this, Constants.TRANSITION_LEFT);
                        } else { // status false
                            CommonUtils.showToast(IssuePolicyForOtherCarsActivity.this, "Error: " + insuranceCaseStatusModel.getMessage(), Toast.LENGTH_SHORT);

                        }


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        CommonUtils.showErrorToast(IssuePolicyForOtherCarsActivity.this, error, Toast.LENGTH_SHORT);

                    }
                });

    }
}
