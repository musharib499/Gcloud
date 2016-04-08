package com.gcloud.gaadi.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.annotations.Email;
import com.gcloud.gaadi.annotations.FirstCharValidation;
import com.gcloud.gaadi.annotations.MobileNumber;
import com.gcloud.gaadi.annotations.NotAllowedChars;
import com.gcloud.gaadi.annotations.RadioGrp;
import com.gcloud.gaadi.annotations.Required;
import com.gcloud.gaadi.annotations.rules.Rule;
import com.gcloud.gaadi.annotations.rules.Validator;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.DateType;
import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.constants.TimeType;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.interfaces.CallLogsItemClickInterface;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.AddLeadModel;
import com.gcloud.gaadi.model.CallLogItem;
import com.gcloud.gaadi.model.FollowupDate;
import com.gcloud.gaadi.model.FollowupTime;
import com.gcloud.gaadi.model.StockDetailModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.fourmob.datetimepicker.date.DatePickerDialog;
import com.gcloud.gaadi.ui.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.gcloud.gaadi.ui.sleepbot.datetimepicker.time.TimePickerDialog;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

public class StockAddLeadActivity extends BaseActivity implements
        OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
        Validator.ValidationListener, OnCheckedChangeListener,
        OnNoInternetConnectionListener, CallLogsItemClickInterface {

    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    private static final String TAG = "StockAddLeadActvity";
    RadioButton radioButton;
    String formatdate = "";
    GCProgressDialog progressBar;
    ImageView callog;
    StockDetailModel model;
    ImageView stockImage;
    TextView stockPrice, modelVersion, modelYear, colorValue, stockLeads,
            kmsDriven;
    ImageView moreOptions, make, color;
    String name = "", mobilenum = "", email = "", source = "", status = "",
            date = "", time = "";
    // private ActionBar mActionBar;
//    private GAHelper mGAHelper;
    private Validator mValidator;
    @Required(order = 1, messageResId = R.string.enter_lead_name)
    @NotAllowedChars(order = 2, notAllowedChars = Constants.NOT_ALLOWED_NAME_CHARS, messageResId = R.string.error_lead_name_invalid_chars)
    private EditText leadName;
    @Required(order = 3, messageResId = R.string.error_lead_mobile_number)
    @FirstCharValidation(order = 4, notAllowedFirstChars = "0123456", messageResId = R.string.error_mobile_number_invalid)
    @MobileNumber(order = 5, minLength = 10, maxLength = 10, messageResId = R.string.error_mobile_number_invalid)
    private EditText leadMobileNumber;
    @Email(order = 6, minLength = 0, messageResId = R.string.error_lead_email_address)
    private EditText leadEmailAddress;
    @RadioGrp(order = 7, messageResId = R.string.error_provide_lead_source)
    private RadioGroup leadSource;
    private RadioGroup leadStatus;
    private TextView dateView;
    private TextView timeView;
    private EditText comments;
    private TextView leadAdd;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private boolean permissionGranted;
    private CallLogsDialogFragment callLogsDialogFragment;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.stock_add_lead, frameLayout);
//        mGAHelper = new GAHelper(this);

        permissionGranted = false;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            model = (StockDetailModel) this.getIntent().getSerializableExtra(
                    Constants.MODEL_DATA);
            initializeViews();
        }
        progressBar = new GCProgressDialog(this, this);

        // mActionBar = getSupportActionBar();
        //mActionBar.setDisplayHomeAsUpEnabled(true);
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        final Calendar calendar = Calendar.getInstance();

        datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), false);
        timePickerDialog = TimePickerDialog.newInstance(this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE) + 5, false, false);

        dateView = (TextView) findViewById(R.id.followupDate);
        dateView.setOnClickListener(this);

        timeView = (TextView) findViewById(R.id.followupTime);
        timeView.setOnClickListener(this);

        leadAdd = (TextView) findViewById(R.id.addLead);
        leadAdd.setOnClickListener(this);

        leadSource = (RadioGroup) findViewById(R.id.leadSource);
        leadSource.setOnCheckedChangeListener(this);

        leadStatus = (RadioGroup) findViewById(R.id.leadStatus);
        leadStatus.setOnCheckedChangeListener(this);

        leadName = (EditText) findViewById(R.id.leadName);
        leadMobileNumber = (EditText) findViewById(R.id.leadcontactNumber);
        leadEmailAddress = (EditText) findViewById(R.id.leadEmailAddress);
        comments = (EditText) findViewById(R.id.comments);
        comments.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent event) {

                if (view.getId() == R.id.comments) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
        callog = (ImageView) findViewById(R.id.callog);
        callog.setOnClickListener(this);
        //    viewcommnts = (TextView) findViewById(R.id.viewcommnts);
//		viewcommnts.setOnClickListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent = null;

        // noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                    Constants.CATEGORY_ADD_LEAD_AGAINST_STOCK,
                    Constants.CATEGORY_ADD_LEAD_AGAINST_STOCK,
                    Constants.ACTION_TAP,
                    Constants.LABEL_BACK_BUTTON,
                    0);
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initializeViews() {

        stockImage = (ImageView) findViewById(R.id.stockImage);
        if ((model.getArrayImages() != null) && (!model.getArrayImages().isEmpty()) && !model.getArrayImages().get(0).isEmpty()) {
            Glide.with(this)
                    .load(model.getArrayImages().get(0))
                    .crossFade()
                    .centerCrop()
                    .placeholder(R.drawable.image_load_default_small)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(stockImage);

        } else {
            Glide.with(this)
                    .load(R.drawable.no_image_default_small)
                    .centerCrop()
                    .into(stockImage);

        }

        stockPrice = (TextView) findViewById(R.id.stockPrice);
        stockPrice.setText(Constants.RUPEES_SYMBOL + " " + model.getStockPrice());

        make = (ImageView) findViewById(R.id.makeLogo);
        make.setImageResource(ApplicationController.makeLogoMap.get(model
                .getMakeid()));

        modelVersion = (TextView) findViewById(R.id.stockModelVersion);
        modelVersion.setText(model.getModelVersion());

        boolean certifiedCar = !model.getCertificationId().isEmpty();
        if (certifiedCar) {
            findViewById(R.id.trustmark).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.trustmark).setVisibility(View.GONE);
        }

        moreOptions = (ImageView) findViewById(R.id.moreOptions);
        moreOptions.setVisibility(View.INVISIBLE);

        modelYear = (TextView) findViewById(R.id.modelYear);
        modelYear.setText(model.getModelYear());

        colorValue = (TextView) findViewById(R.id.colorValue);
        colorValue.setText(model.getColorValue());

        color = (ImageView) findViewById(R.id.color);
        if (model.getHexCode() != null && !model.getHexCode().equals("")) {
            GradientDrawable bgShape = (GradientDrawable) color.getBackground();
            bgShape.setColor(Color.parseColor(model.getHexCode()));
        } else {
            GradientDrawable bgShape = (GradientDrawable) color.getBackground();
            bgShape.setColor(Color.parseColor("#ffffff"));
        }

        stockLeads = (TextView) findViewById(R.id.stockLeads);
        int totalLeads = Integer.parseInt(model.getTotalLeads());
        String totalLeadsText = "";
        if (totalLeads == 0) {
            findViewById(R.id.leadsLayout).setVisibility(View.GONE);
        } else if (totalLeads == 1) {
            totalLeadsText = "1 Lead";
        } else if (totalLeads < 99) {
            totalLeadsText = totalLeads + " Leads";
        } else if (totalLeads > 99) {
            totalLeadsText = "99+ Leads";
        }
        stockLeads.setText(totalLeadsText);

        kmsDriven = (TextView) findViewById(R.id.kmsDriven);
        kmsDriven.setText(model.getKms());

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (permissionGranted) {
            showCallLogsDialog();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_ADD_LEAD_AGAINST_STOCK);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.followupDate:
                if (!datePickerDialog.isVisible())
                    showDatePickerDialog();
                break;

            case R.id.followupTime:
                if (!timePickerDialog.isVisible())
                    showTimePickerDialog();
                break;

            case R.id.addLead:
                clearErrors();
                mValidator.validate();
                break;
            case R.id.callog:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !CommonUtils.checkForPermission(this,
                        new String[]{Manifest.permission.READ_CALL_LOG},
                        Constants.REQUEST_PERMISSION_READ_CALL_LOG, "Phone")) {
                    return;
                }
                showCallLogsDialog();

           /* case R.id.viewcommnts:
                //showCallLogsDialog();
                break;*/
        }
    }

    private void clearErrors() {
        leadEmailAddress.setError(null);
        leadMobileNumber.setError(null);
        leadName.setError(null);
    }

    private void showTimePickerDialog() {
        timePickerDialog.setStartTime(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE) + 5);
        timePickerDialog.setCloseOnSingleTapMinute(false);
        timePickerDialog.setOnTimeSetListener(this);
        timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);

    }

    private void showDatePickerDialog() {
        datePickerDialog.setYearRange(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.YEAR) + 1);
        datePickerDialog.setCloseOnSingleTapDay(false);
        datePickerDialog.setOnDateSetListener(this);
        datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);

    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        //Toast.makeText(this, "Date set to " + day + ", " + month + ", " + year, Toast.LENGTH_SHORT).show();

        // TODO apply logic to check whether date chosen is greater than today's
        // date.
        FollowupDate followupDate = new FollowupDate(year, month, day);
        formatdate = Integer.toString(year) + "-" + Integer.toString(month + 1)
                + "-" + Integer.toString(day);
//        GCLog.e(TAG, "OnDateSet date: "+formatdate);
        dateView.setText(day + "/" + followupDate.getMonthName()
                + "/" + year);
        if (!(status.equalsIgnoreCase("Closed") || status.equalsIgnoreCase("Booked") || status.equalsIgnoreCase("Walked-in") || status.equalsIgnoreCase("Converted")))
            if (compareDate(formatdate)) {
                timeView.performClick();
            }
      /*  if(compareDateAndTime(true)) {
            dateView.setText("Date: " + day + "/" + followupDate.getMonthName()
                    + "/" + year);
            timeView.performClick();
        }*/
    }

    private boolean compareDate(String selectedDateTime) {
        try {
            final Calendar calendar = Calendar.getInstance();
            String currentDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
            DateTime currentFormattedDate = dtf.parseDateTime(currentDate);
            DateTime selectedDate = dtf.parseDateTime(formatdate);
            if (selectedDate.isBefore(currentFormattedDate)) {
                dateView.setText("");
                timeView.setText("");
                formatdate = "";

                CommonUtils.showToast(StockAddLeadActivity.this,
                        "Selected Date is before Current Date", Toast.LENGTH_SHORT);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day, DateType dateType) {

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        // Toast.makeText(this, "Time set to " + hourOfDay + ", " + minute,
        // Toast.LENGTH_SHORT).show();
        FollowupTime followupTime = new FollowupTime(hourOfDay, minute);
        if (minute >= 60) {
            minute = 59;
        }
        StringBuilder timeStringBuilder = new StringBuilder();
        timeStringBuilder.append((minute < 10) ? ("0" + minute) : minute);
        timeView.setText(hourOfDay + "-" + timeStringBuilder);
        String date = "";
        if (formatdate.equals("")) {
            if (!dateView.getText().toString().trim().equals("")) {
                String fDate = dateView.getText().toString().trim();
                String[] formatdatearr = fDate.split("Date: ")[1].split("/");
                date = formatdatearr[2].trim()
                        + "-"
                        + ApplicationController.monthNameMap
                        .get(formatdatearr[1].trim()) + "-"
                        + formatdatearr[0];
                formatdate = date + " " + Integer.toString(hourOfDay) + ":"
                        + timeStringBuilder.toString();
                if (!(status.equalsIgnoreCase("Closed") || status.equalsIgnoreCase("Booked") || status.equalsIgnoreCase("Walked-in") || status.equalsIgnoreCase("Converted")))
                    compareDateAndTime(false);
            } else {
                formatdate = "";
                timeView.setText("");
            }

        } else {

            formatdate = formatdate.split("\\s+")[0] + " "
                    + Integer.toString(hourOfDay) + ":" + minute;
            if (!(status.equalsIgnoreCase("Closed") || status.equalsIgnoreCase("Booked") || status.equalsIgnoreCase("Walked-in") || status.equalsIgnoreCase("Converted")))
                compareDateAndTime(false);
        }
    }


    private boolean compareDateAndTime(boolean isDate) {
        try {
            DateTimeFormatter dtf;
            DateTime selectedDate;
            boolean isBefore = false;
            if (isDate) {
//                GCLog.e(TAG, "date called");
                dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                String currentDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
                selectedDate = dtf.parseDateTime(formatdate);
//                GCLog.e(TAG, "current date: "+currentDate);
                if (selectedDate.isBefore(dtf.parseDateTime(currentDate))) {
                    isBefore = true;
//                    GCLog.e(TAG, "date isbefore");
                }
            } else {
//                GCLog.e(TAG, "time called");
                dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
                selectedDate = dtf.parseDateTime(formatdate);
                if (selectedDate.isBeforeNow())
                    isBefore = true;
            }
//            GCLog.e(TAG, "date: "+formatdate);
            if (isBefore) {
                dateView.setText("");
                timeView.setText("");
                formatdate = "";
                CommonUtils.showToast(StockAddLeadActivity.this,
                        "Selected Date is before Current Date", Toast.LENGTH_SHORT);
                return false;
            } else
                return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /*private boolean compareDate( String selectedDateTime) {
        try {
            final Calendar calendar = Calendar.getInstance();
            String currentDate=calendar.get(Calendar.YEAR)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
            DateTime currentFormattedDate=dtf.parseDateTime(currentDate);
            DateTime selectedDate = dtf.parseDateTime(formatdate);
            if (selectedDate.isBefore(currentFormattedDate)) {
                dateView.setText("");
                timeView.setText("");
                formatdate = "";
                CommonUtils.showToast(StockAddLeadActivity.this,
                        "Selected Date is before Current Date", Toast.LENGTH_SHORT);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void onValidationSucceeded() {
        if (!dateView.getText().toString().isEmpty() && timeView.getText().toString().isEmpty()) {
            CommonUtils.showToast(this, "Time field is Empty", Toast.LENGTH_SHORT);
            return;
        }
        progressBar.show();
        addLead(true);
    }

    private void addLead(final boolean showFullPageError) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.DEALER_USERNAME, CommonUtils
                .getStringSharedPreference(this, Constants.UC_DEALER_USERNAME,
                        ""));
        params.put(Constants.DEALER_PASSWORD, CommonUtils
                .getStringSharedPreference(this, Constants.UC_DEALER_PASSWORD,
                        ""));
        params.put(Constants.UCDID, String.valueOf(CommonUtils
                .getIntSharedPreference(this, Constants.UC_DEALER_ID, -1)));
        params.put(Constants.LEAD_NAME, leadName.getText().toString().trim());
        params.put(Constants.LEAD_MOBILE, leadMobileNumber.getText().toString()
                .trim());
        params.put(Constants.EMAIL, leadEmailAddress.getText().toString()
                .trim());
        params.put(Constants.CALL_SOURCE, "AAS");
        if (formatdate != null) {
            params.put(Constants.NEXT_FOLLOW, formatdate);
        }
        params.put(Constants.LEAD_STATUS, status);
        params.put(Constants.SOURCE, source);
        params.put(Constants.CAR_ID, model.getStockId());
        params.put(Constants.COMMENT, comments.getText().toString().trim());
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
        params.put(Constants.METHOD_LABEL, Constants.ADD_LEAD_API);

        RetrofitRequest.leadAddRequest(params, new Callback<AddLeadModel>() {
            @Override
            public void success(AddLeadModel addLeadModel, retrofit.client.Response response) {
                if (progressBar != null) {
                    progressBar.dismiss();
                }
                if (addLeadModel.getStatus().equals("T")) {
                    CommonUtils.showToast(StockAddLeadActivity.this,
                            "Lead Added Successfully.",
                            Toast.LENGTH_SHORT);

                    setResult(RESULT_OK);
                    finish();
                } else {
                    CommonUtils.showToast(StockAddLeadActivity.this,
                            addLeadModel.getError(), Toast.LENGTH_LONG);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(StockAddLeadActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                } else {
                    CommonUtils.showToast(StockAddLeadActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                }
            }
        });
       /* LeadAddRequest leadAddRequest = new LeadAddRequest(this, Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<AddLeadModel>() {
                    @Override
                    public void onResponse(AddLeadModel response) {
                        if (progressBar != null) {
                            progressBar.dismiss();
                        }
                        if (response.getStatus().equals("T")) {
                            CommonUtils.showToast(StockAddLeadActivity.this,
                                    "Lead Added Successfully.",
                                    Toast.LENGTH_SHORT);

                            setResult(RESULT_OK);
                            finish();
                        } else {
                            CommonUtils.showToast(StockAddLeadActivity.this,
                                    response.getError(), Toast.LENGTH_LONG);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(StockAddLeadActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                } else {
                    CommonUtils.showToast(StockAddLeadActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                }
            }
        });

        ApplicationController.getInstance().addToRequestQueue(leadAddRequest,
                Constants.TAG_STOCKS_LIST, showFullPageError, this);*/
    }

    private void showCallLogsDialog() {

        if (callLogsDialogFragment == null) {

            callLogsDialogFragment = CallLogsDialogFragment.getInstance();
            Bundle args = new Bundle();

        }

        callLogsDialogFragment.show(getSupportFragmentManager(), "call-logs-dialog");

    }

    @Override
    public void onCallLogSelected(CallLogItem callLogItem, ShareType shareType,
                                  String shareText, String carId, String imageUrl) {

        leadMobileNumber.setText(callLogItem.getGaadiFormatNumber());
        if ((callLogItem.getName() != null) && (!callLogItem.getName()
                .equals("<Unknown>"))) {
            leadName.setText(callLogItem.getName());
        } else {
            leadName.setText("");
        }

    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        if (failedView instanceof EditText) {
            ((EditText) failedView).setError(failedRule.getFailureMessage());
            return;
        }

        CommonUtils.showToast(this, failedRule.getFailureMessage(), Toast.LENGTH_SHORT);

    }

    @Override
    public void onViewValidationSucceeded(View succeededView) {

    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute,
                          TimeType timeType) {

    }

    @Override
    public void onCheckedChanged(RadioGroup radiogroup, int checkedId) {
        switch (radiogroup.getId()) {
            case R.id.leadSource:

                radioButton = (RadioButton) findViewById(checkedId);
                source = radioButton.getText().toString().trim();
                break;

            case R.id.leadStatus:

                radioButton = (RadioButton) findViewById(checkedId);
                if (checkedId == R.id.statusWalkinScheduled) {
                    status = "WalkInScheduled";
                } else {
                    status = radioButton.getText().toString().trim();
                }
                // status =CommonUtils.getLeadStatus(status);
                break;


        }

    }


    @Override
    public void onNoInternetConnection(NetworkEvent networkEvent) {


        if (networkEvent.getNetworkError() == NetworkEvent.NetworkError.NO_INTERNET_CONNECTION) {
            if (progressBar != null) {
                progressBar.dismiss();
            }
            CommonUtils.showToast(StockAddLeadActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION_READ_CALL_LOG) {
            permissionGranted = grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
    }
}
