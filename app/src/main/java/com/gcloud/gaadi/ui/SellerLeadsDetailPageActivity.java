package com.gcloud.gaadi.ui;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.SellerLeadsCommentsAdapter;
import com.gcloud.gaadi.adapter.SellerLeadsRecyclerAdapter;
import com.gcloud.gaadi.annotations.Email;
import com.gcloud.gaadi.annotations.NotAllowedChars;
import com.gcloud.gaadi.annotations.Required;
import com.gcloud.gaadi.annotations.rules.Rule;
import com.gcloud.gaadi.annotations.rules.Validator;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.DateType;
import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.constants.TimeType;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.model.AddLeadModel;
import com.gcloud.gaadi.model.FollowupDate;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.LeadData;
import com.gcloud.gaadi.model.LeadDetailModel;
import com.gcloud.gaadi.model.SellerLeadsDetailModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.service.DatabaseInsertionService;
import com.gcloud.gaadi.ui.fourmob.datetimepicker.date.DatePickerDialog;
import com.gcloud.gaadi.ui.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.gcloud.gaadi.ui.sleepbot.datetimepicker.time.TimePickerDialog;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.MaterialTextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

public class SellerLeadsDetailPageActivity extends AppCompatActivity implements Validator.ValidationListener, RadioGroup.OnCheckedChangeListener, View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    @Required(order = 1, messageResId = R.string.enter_lead_name)
    @NotAllowedChars(order = 2, notAllowedChars = Constants.NOT_ALLOWED_NAME_CHARS, messageResId = R.string.error_lead_name_invalid_chars)
    MaterialEditText et_fullName;
    @Email(order = 3, messageResId = R.string.error_lead_email_address)
    MaterialEditText et_emailId;
    TextView tv_email, tv_contactNum, tv_totalCarsSold;
    Button btn_callNow;
    RadioGroup leadStatus;
    RadioButton radioButton;
    ListView previousCommentsListVw;
    MaterialEditText tv_followUpTime;
    MaterialEditText tv_followUpDate;
    // ImageView iv_back, iv_editLead;
    RecyclerView leadsRecyclerVw;
    //CollapsingToolbarLayout collapsingToolBarLayout;
    AppBarLayout appBarLayout;
    MaterialEditText et_comments;
    MaterialTextView tv_mobilenum;
    LinearLayout updateLeadbtn;
    String status = "", token = "", selectedNumberToCall = "";
    int hour = 0;
    int min = 0;
    boolean isLeadEdit = false;
    GCProgressDialog progressDialog;
    LeadDetailModel leadDetailModel;
    Toolbar toolBar;
    String sellerLeadId = "", leadName = "";
    private HorizontalScrollView statusscrollview;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Validator mValidator;
    private View callItem = null;
    private boolean performCall = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_leads_detail_page_layout);
        Calendar calendar = Calendar.getInstance();
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
        progressDialog = new GCProgressDialog(this, this, getString(R.string.please_wait));
        datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), false);
        timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
        toolBar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //collapsingToolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        /*getSupportActionBar().setTitle(((LeadDetailModel) getIntent().getSerializableExtra(Constants.MODEL_DATA)).getName());
        setSupportActionBar(toolBar);*/
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_contactNum = (TextView) findViewById(R.id.tv_contactNum);
        et_fullName = (MaterialEditText) findViewById(R.id.et_fullName);
        et_emailId = (MaterialEditText) findViewById(R.id.et_email);
        tv_mobilenum = (MaterialTextView) findViewById(R.id.et_mobileNum);
        tv_contactNum = (TextView) findViewById(R.id.tv_contactNum);
        et_comments = (MaterialEditText) findViewById(R.id.comments);
        updateLeadbtn = (LinearLayout) findViewById(R.id.footerLayout);
        updateLeadbtn.setOnClickListener(this);
        btn_callNow = (Button) findViewById(R.id.btn_callNow);
        btn_callNow.setOnClickListener(this);
        if (!ApplicationController.checkInternetConnectivity()) {
            btn_callNow.setVisibility(View.INVISIBLE);
        }
        tv_totalCarsSold = (TextView) findViewById(R.id.tv_totalCarsSold);
        leadsRecyclerVw = (RecyclerView) findViewById(R.id.leadsRecyclerView);
        leadsRecyclerVw.setLayoutManager(new LinearLayoutManager(SellerLeadsDetailPageActivity.this, LinearLayoutManager.HORIZONTAL, false));
        statusscrollview = (HorizontalScrollView) findViewById(R.id.statusscrollview);
       /* iv_back = (ImageView)findViewById(R.id.iv_back);
        iv_editLead = (ImageView)findViewById(R.id.iv_edit);
        iv_back.setOnClickListener(this);
        iv_editLead.setOnClickListener(this);*/
        final CoordinatorLayout rootView = (CoordinatorLayout) findViewById(R.id.rootView);
        final NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.nestedScrollVw);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
                if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
            }
        });

        leadStatus = (RadioGroup) findViewById(R.id.leadStatus);
        leadStatus.setOnCheckedChangeListener(this);
        previousCommentsListVw = (ListView) findViewById(R.id.previousCommentsList);
        previousCommentsListVw.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent event) {

                if (view.getId() == R.id.previousCommentsList) {
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
        tv_followUpDate = (MaterialEditText) findViewById(R.id.followupDate);
        tv_followUpTime = (MaterialEditText) findViewById(R.id.followupTime);
        tv_followUpDate.setOnClickListener(this);
        tv_followUpTime.setOnClickListener(this);

        // code to avoid scrollview from scrolling to edittext
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        scrollView.setFocusable(true);
        scrollView.setFocusableInTouchMode(true);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                return false;
            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            GCLog.e("extras are not null");
            if (extras.containsKey(Constants.FROM_NOTIFICATION)) {
                sellerLeadId = extras.getString("leadId");
                leadName = extras.getString("leadname");
                getSupportActionBar().setTitle(leadName);
                setSupportActionBar(toolBar);

                if (extras.containsKey("rowId")) {
                    ContentValues values = new ContentValues();
                    values.put(MakeModelVersionDB.COLUMN_TYPE, 2);

                    startService(new Intent(this, DatabaseInsertionService.class)
                            .putExtra(Constants.ACTION, "update")
                            .putExtra(Constants.PROVIDER_URI, Uri.parse("content://"
                                    + Constants.NOTIFICATION_CONTENT_AUTHORITY + "/" + MakeModelVersionDB.TABLE_NOTIFICATION))
                            .putExtra(Constants.CONTENT_VALUES, values)
                            .putExtra(Constants.SELECTION, MakeModelVersionDB.COLUMN_ID + " = ?")
                            .putExtra(Constants.SELECTION_ARGS, new String[]{String.valueOf(extras.getLong("rowId", 0))}));
                }


                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.cancel(Integer.parseInt(extras.getString("nID")));

                performCall = extras.containsKey(Constants.PERFORM_CALL) && extras.getBoolean(Constants.PERFORM_CALL);

                // send this to server to track.
                GCLog.e("notification opened: " + extras.toString());

                if (!extras.containsKey("read")) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put(Constants.GCM_RESPONSE_CODE, "0"); // notification was opened and was destined for this dealer.
                    params.put(Constants.SCREEN_NAME, getClass().getSimpleName());
                    params.put(Constants.GCM_MESSAGE, extras.toString());
                    params.put(Constants.METHOD_LABEL, Constants.LOG_NOTIFICATION_METHOD);

                    RetrofitRequest.makeLogNotificationRequest(this, params, new Callback<GeneralResponse>() {
                        @Override
                        public void success(GeneralResponse generalResponse, retrofit.client.Response response) {

                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                }
            } else if (extras.containsKey(Constants.FROM_DEEPLINK)) {
                token = extras.getString(Constants.TOKEN);
                if (token == null) {
                    token = "";
                }
            } else {
                leadName = ((LeadDetailModel) getIntent().getSerializableExtra(Constants.MODEL_DATA)).getName();
                getSupportActionBar().setTitle(leadName);
                setSupportActionBar(toolBar);
                sellerLeadId = ((LeadDetailModel) getIntent().getSerializableExtra(Constants.MODEL_DATA)).getLeadId();
            }
        }
        requestSellerLeadsDetails(true, 1, !token.isEmpty());
    }


    private void requestSellerLeadsDetails(final boolean showFullPageError, final int pageNumber, final boolean fromDeepLink) {
        if (!ApplicationController.checkInternetConnectivity()) {
            CommonUtils.showToast(this, getString(R.string.no_internet_msg), Toast.LENGTH_SHORT);
            return;
        }
        if (!progressDialog.isShowing())
            progressDialog.show();
        HashMap<String, String> params = new HashMap<>();

        params.put(Constants.METHOD_LABEL, Constants.SELLER_LEADS_DETAIL_METHOD);
        params.put(Constants.SL_DID,
                String.valueOf(CommonUtils.getIntSharedPreference(ApplicationController.getInstance(), Constants.DEALER_ID, -1)));
        if (fromDeepLink) {
            params.put(Constants.TOKEN, token);
        } else {
            params.put(Constants.SELLER_ID, sellerLeadId);
        }

        RetrofitRequest.getSellerLeadsDetails(params, new Callback<SellerLeadsDetailModel>() {
            @Override
            public void success(SellerLeadsDetailModel response, retrofit.client.Response retrofitResponse) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                GCLog.e("response model: " + response.toString());
                if ("T".equalsIgnoreCase(response.getStatus())) {

                    initializeViews(response.getLeadDetails());
                    if (performCall) {
                        btn_callNow.performClick();
                    }

                } else {
                    // showNoRecordsMessage(showFullPageError, response.getMessage());
                    if (fromDeepLink) {
                        new AlertDialog.Builder(SellerLeadsDetailPageActivity.this)
                                .setTitle(getString(R.string.information))
                                .setMessage(response.getMessage())
                                .setPositiveButton(getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        finish();
                                    }
                                })
                                .create().show();
                    } else {
                        CommonUtils.showToast(SellerLeadsDetailPageActivity.this, response.getError(), Toast.LENGTH_SHORT);
                    }
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                if (error.getCause() instanceof UnknownHostException) {
                    //  showNetworkConnectionErrorLayout(showFullPageError);

                } else {
                    // showServerErrorLayout(showFullPageError);

                }
            }
        });

    }

    private void initializeViews(LeadDetailModel leadDetailsObj) {
        leadDetailModel = leadDetailsObj;
        tv_email.setText(leadDetailsObj.getEmailID());
        tv_contactNum.setText(leadDetailsObj.getNumber());
        tv_mobilenum.setText(leadDetailsObj.getNumber());
        tv_mobilenum.startLabelAnimation();
        ((TextView) findViewById(R.id.lead_source)).setText(leadDetailModel.getSource());
        setLeadStatus(leadDetailsObj.getLeadStatus());
        if (!leadDetailsObj.getformattedfollowDate().trim().equals("")) {
            String formattedFollowDate = leadDetailsObj.getformattedfollowDate();
            String[] formattedDate = formattedFollowDate.split("\\s+");
            tv_followUpDate.setText("" + formattedDate[0]);
            tv_followUpTime.setText("" + formattedDate[1]);


        } else {
            tv_followUpDate.setText("");
            tv_followUpTime.setText("");

        }
        if (leadDetailsObj.getCommentsArrayList().size() > 0) {
            findViewById(R.id.comments_header).setVisibility(View.VISIBLE);
            previousCommentsListVw.setVisibility(View.VISIBLE);
            SellerLeadsCommentsAdapter adapter = new SellerLeadsCommentsAdapter(SellerLeadsDetailPageActivity.this, leadDetailsObj.getCommentsArrayList());
            previousCommentsListVw.setAdapter(adapter);
        }
        if (leadDetailsObj.getCarsList().size() > 0) {
            tv_totalCarsSold.setVisibility(View.VISIBLE);
            leadsRecyclerVw.setVisibility(View.VISIBLE);
            leadsRecyclerVw.setAdapter(new SellerLeadsRecyclerAdapter(SellerLeadsDetailPageActivity.this, leadDetailsObj.getCarsList()));
            tv_totalCarsSold.setText(leadName + " is selling " + leadDetailsObj.getCarsList().size() + " cars");
        }

        if (leadDetailModel.getVerified() != null
                && !leadDetailModel.getVerified().isEmpty()
                && leadDetailModel.getVerified().equalsIgnoreCase("1")) {
            findViewById(R.id.verified_sign).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.verified_sign).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_seller_leads_detail_page, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.iv_edit:
                if (isLeadEdit) {
                    findViewById(R.id.nestedScrollVw).scrollTo(0, 0);
                    Animation viewHide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.view_hide);
                    Animation viewShow = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.activity_in_right);
                    viewShow.setDuration(1000);

                    findViewById(R.id.editLeadLayout).setAnimation(viewHide);
                    findViewById(R.id.addLeadLayout).setAnimation(viewShow);
                    findViewById(R.id.editLeadLayout).setVisibility(View.GONE);
                    findViewById(R.id.addLeadLayout).setVisibility(View.VISIBLE);

                    isLeadEdit = false;
                    invalidateOptionsMenu();
                    initializeViews(leadDetailModel);
                } else {
                    isLeadEdit = true;
                    findViewById(R.id.editLeadLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.addLeadLayout).setVisibility(View.GONE);
                    getSupportActionBar().setTitle("Edit Lead");

                    invalidateOptionsMenu();
                    findViewById(R.id.nestedScrollVw).scrollTo(0, 0);
                    Animation viewHide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.activity_out_left);
                    Animation viewShow = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.view_show);
                    findViewById(R.id.editLeadLayout).setAnimation(viewShow);
                    findViewById(R.id.addLeadLayout).setAnimation(viewHide);
                    et_fullName.setText(leadDetailModel.getName());
                    et_emailId.setText(leadDetailModel.getEmailID());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem iv_edit = menu.findItem(R.id.iv_edit);
        if (isLeadEdit) {
            iv_edit.setIcon(R.drawable.toolbar_close);
            getSupportActionBar().setTitle("Edit Lead");
        } else {
            iv_edit.setIcon(R.drawable.edit_seller_leads);
            getSupportActionBar().setTitle(leadName);
        }

        return true;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (radioGroup.getId()) {
            case R.id.leadStatus:
                if (radioButton != null && checkedId == radioButton.getId()) {
                    return;
                }
                if (checkedId == -1) {
                    status = "";
                    radioButton.setOnClickListener(null);
                    radioButton = null;
                    return;
                }
                radioButton = (RadioButton) findViewById(checkedId);
               /* if(checkedId==R.id.statusWalkinScheduled) {
                    status = "WalkInScheduled";
                }
                else {*/
                status = radioButton.getText().toString().trim();
                //  }
                autoSmoothScroll(radioButton);
                radioGroup.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (radioButton != null) {
                            radioButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    leadStatus.clearCheck();
                                }
                            });
                        }
                    }
                }, 100);
                // status = CommonUtils.getLeadStatus(status);
                break;
        }
    }

    public void autoSmoothScroll(final RadioButton radiobutton) {
        statusscrollview.postDelayed(new Runnable() {
            @Override
            public void run() {
                statusscrollview.setSmoothScrollingEnabled(true);
                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int width = displaymetrics.widthPixels;
                if (radiobutton.getRight() > width)
                    statusscrollview.smoothScrollTo(radiobutton.getScrollX(), 0);
            }
        }, 100);
    }

    private void setLeadStatus(String status) {

        RadioButton statusHot = (RadioButton) findViewById(R.id.statusHot);
        RadioButton statusWarm = (RadioButton) findViewById(R.id.statusWarm);
        RadioButton statusCold = (RadioButton) findViewById(R.id.statusCold);
        RadioButton statusClosed = (RadioButton) findViewById(R.id.statusClosed);
        RadioButton statusWalkin = (RadioButton) findViewById(R.id.statusWalkin);
        // RadioButton statusBooked = (RadioButton) findViewById(R.id.statusBooked);
        RadioButton statusConverted = (RadioButton) findViewById(R.id.statusConverted);
        // RadioButton statusWalkinScheduled = (RadioButton) findViewById(R.id.statusWalkinScheduled);

        if (status.equalsIgnoreCase("Closed")) {
            statusClosed.setChecked(true);
        } else if (status.equalsIgnoreCase("Cold")) {
            statusCold.setChecked(true);
        } else if (status.equalsIgnoreCase("Warm")) {
            statusWarm.setChecked(true);
        } else if (status.equalsIgnoreCase("Hot")) {
            statusHot.setChecked(true);
        } else if (status.equalsIgnoreCase("Walked-in")) {
            statusWalkin.setChecked(true);
        }/* else if (status.equalsIgnoreCase("Booked")) {
            statusBooked.setChecked(true);
        }*/ else if (status.equalsIgnoreCase("Converted")) {
            statusConverted.setChecked(true);
        }/* else if (status.equalsIgnoreCase("WalkInScheduled")) {
            statusWalkinScheduled.setChecked(true);
        }*/

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.footerLayout:
                if (!ApplicationController.checkInternetConnectivity()) {
                    CommonUtils.showToast(this,
                            getString(R.string.no_internet_msg), Toast.LENGTH_LONG);
                    return;
                }
                mValidator.validate();
                break;


            case R.id.followupDate:
                if (!datePickerDialog.isVisible()) {
                    showDatePickerDialog();
                }
                break;

            case R.id.followupTime:
                if (!timePickerDialog.isVisible()) {
                    showTimePickerDialog();
                }
                break;

            case R.id.btn_callNow:
                callItem = view;
                callItem.setEnabled(false);
                callLead();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_SELLER_LEAD_DETAILS);
        if (callItem != null)
            callItem.setEnabled(true);

    }

    private void callLead() {
        String mobileNumber = tv_contactNum.getText().toString().trim();
        logCallMade(mobileNumber, leadDetailModel.getLeadId());

        if ((mobileNumber != null) && !mobileNumber.isEmpty() && !"null".equalsIgnoreCase(mobileNumber)) {
            selectedNumberToCall = mobileNumber;

            if (CommonUtils.getIntSharedPreference(this, Constants.IS_LMS, 0) == 1) {
                CommonUtils.activateLeadCallStateListener(this,
                        LeadFollowUpActivity.class,
                        "+91" + /**/mobileNumber /*/"9560619309"/**/,
                        Constants.VALUE_VIEWLEAD,
                        Constants.NOT_YET_CALLED_FRAG_NO,
                        new LeadData(leadDetailModel));
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !CommonUtils.checkForPermission(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    Constants.REQUEST_PERMISSION_CALL, "Phone")) {
                selectedNumberToCall = mobileNumber;
                return;
            }

            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:+91" + /**/mobileNumber /*/"9560619309"/**/));

            startActivity(intent);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_SELLER_LEAD_DETAILS);

    }

    // function to log all calls made from manage leads page.
    private void logCallMade(String number, String leadId) {

        final HashMap<String, String> params = new HashMap<>();

        params.put(Constants.SHARE_SOURCE, Constants.CATEGORY_SELLER_LEAD_DETAILS);

        params.put(Constants.SHARE_TYPE, ShareType.CALL.name());
        params.put(Constants.MOBILE_NUM, number);
        // params.put(Constants.CAR_ID, carId);
        params.put(Constants.LEAD_ID_KEY, leadId);
        params.put(Constants.METHOD_LABEL, Constants.SENT_CARS_METHOD);

        RetrofitRequest.shareCarsRequest(getApplicationContext(), params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, retrofit.client.Response response) {
                GCLog.e("Dipanshu",params.toString());
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof UnknownHostException) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_SELLER_LEADS,
                            Constants.CATEGORY_SELLER_LEADS,
                            Constants.ACTION_TAP,
                            Constants.LABEL_NO_INTERNET,
                            0);

                } else {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_SELLER_LEADS,
                            Constants.CATEGORY_SELLER_LEADS,
                            Constants.CATEGORY_IO_ERROR,
                            error.getMessage(),
                            0);
                }


            }
        });
        /*ShareCarsRequest shareCarsRequest = new ShareCarsRequest(SellerLeadsDetailPageActivity.this,
                Request.Method.POST,
                Constants.getWebServiceURL(SellerLeadsDetailPageActivity.this),
                params,
                new Response.Listener<GeneralResponse>() {
                    @Override
                    public void onResponse(GeneralResponse response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getCause() instanceof UnknownHostException) {
                            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                    Constants.CATEGORY_SELLER_LEADS,
                                    Constants.CATEGORY_SELLER_LEADS,
                                    Constants.ACTION_TAP,
                                    Constants.LABEL_NO_INTERNET,
                                    0);

                        } else {
                            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                    Constants.CATEGORY_SELLER_LEADS,
                                    Constants.CATEGORY_SELLER_LEADS,
                                    Constants.CATEGORY_IO_ERROR,
                                    error.getMessage(),
                                    0);
                        }

                    }
                });

        ApplicationController.getInstance().addToRequestQueue(shareCarsRequest);*/

    }

    private void updateLeadRequest() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }

        final HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.DEALER_USERNAME, CommonUtils
                .getStringSharedPreference(this, Constants.UC_DEALER_USERNAME,
                        ""));
        params.put(Constants.DEALER_PASSWORD, CommonUtils
                .getStringSharedPreference(this, Constants.UC_DEALER_PASSWORD,
                        ""));
        params.put(Constants.UCDID, String.valueOf(CommonUtils
                .getIntSharedPreference(this, Constants.UC_DEALER_ID, -1)));
        if (isLeadEdit) {
            params.put(Constants.LEAD_NAME, et_fullName.getText().toString().trim());
            params.put(Constants.LEAD_MOBILE, tv_mobilenum.getText().toString()
                    .trim());
            params.put(Constants.EMAIL, et_emailId.getText().toString()
                    .trim());

        } else {
            params.put(Constants.LEAD_MOBILE, tv_contactNum.getText().toString()
                    .trim());
            params.put(Constants.EMAIL, tv_email.getText().toString()
                    .trim());

        }
        params.put(Constants.METHOD_LABEL, Constants.UPDATE_SELLER_LEAD_API);
        params.put(Constants.NEXT_FOLLOW, getFormattedDate(tv_followUpDate, tv_followUpTime));
        params.put(Constants.SOURCE, CommonUtils.isValidField(leadDetailModel.getSource()) ? leadDetailModel.getSource() : "");
        params.put(Constants.LEAD_ID_KEY, CommonUtils.isValidField(leadDetailModel.getLeadId())
                ? leadDetailModel.getLeadId() : "");
        params.put(Constants.SL_DID,
                String.valueOf(CommonUtils.getIntSharedPreference(ApplicationController.getInstance(), Constants.DEALER_ID, -1)));
        params.put(Constants.LEAD_STATUS, status);
        params.put(Constants.COMMENT, et_comments.getText().toString().trim());

        RetrofitRequest.updateSellerLeadsDetails(params, new Callback<AddLeadModel>() {
            @Override
            public void success(AddLeadModel response, retrofit.client.Response retrofitResponse) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                GCLog.e("response model: " + response.toString());
                if ("T".equalsIgnoreCase(response.getStatus())) {

                    CommonUtils.showToast(SellerLeadsDetailPageActivity.this,
                            "Lead Updated Successfully.",
                            Toast.LENGTH_LONG);
                    finish();
                    Intent intent = new Intent(SellerLeadsDetailPageActivity.this,
                            SellerLeadsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // intent.putExtra(Constants.SELECTED_TAB, selectedTab);
                    // intent.putExtra(Constants.MODEL_DATA, response);
                    // intent.putExtra("hashMapKey", params);
                    startActivity(intent);

                } else {
                    CommonUtils.showToast(SellerLeadsDetailPageActivity.this,
                            response.getError(), Toast.LENGTH_SHORT);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (error.getCause() instanceof UnknownHostException) {
                    //  showNetworkConnectionErrorLayout(showFullPageError);

                } else {
                    // showServerErrorLayout(showFullPageError);

                }
            }
        });
    }

    private void showTimePickerDialog() {

        if (hour != 0 && min != 0) {

            timePickerDialog = TimePickerDialog.newInstance(this,
                    hour,
                    min, false, false);
        }/* else if (!editedDate.equals("")) {
            int hour = Integer.parseInt(editedDate.split("\\s+")[1].split(":")[0]);
            int min = Integer.parseInt(editedDate.split("\\s+")[1].split(":")[1]);

            timePickerDialog = TimePickerDialog.newInstance(this,
                    hour,
                    min, false, false);
        } */ else {
            if ((Calendar.getInstance().get(Calendar.MINUTE) + 5) < 60) {
                timePickerDialog = TimePickerDialog.newInstance(this,
                        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                        Calendar.getInstance().get(Calendar.MINUTE) + 5, false, false);
            } else {
                timePickerDialog = TimePickerDialog.newInstance(this,
                        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                        Calendar.getInstance().get(Calendar.MINUTE), false, false);
            }
        }

        timePickerDialog.setCloseOnSingleTapMinute(false);
        timePickerDialog.setOnTimeSetListener(this);
        timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);

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
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        FollowupDate followupDate = new FollowupDate(year, month, day);
        tv_followUpDate.setText(day + "/" + followupDate.getMonthName()
                + "/" + year);
        String formatdate = "";
        formatdate = Integer.toString(year) + "-" + Integer.toString(month + 1)
                + "-" + Integer.toString(day);
        if (status.equalsIgnoreCase("Cold") || status.equalsIgnoreCase("Hot") || status.equalsIgnoreCase("Warm"))
            if (compareDate(formatdate)) {
                tv_followUpTime.performClick();
            }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day, DateType dateType) {

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        if (minute >= 60) {
            minute = 59;
        }
        hour = hourOfDay;
        min = minute;
        StringBuilder timeStringBuilder = new StringBuilder();
        timeStringBuilder.append((minute < 10) ? ("0" + minute) : minute);
        tv_followUpTime.setText(hourOfDay + "-" + timeStringBuilder);
        String date = "";
        if (!tv_followUpDate.getText().toString().trim().equals("")) {
            String fDate = tv_followUpDate.getText().toString().trim();
            String[] formatdatearr = fDate.split("/");
            date = formatdatearr[2].trim()
                    + "-"
                    + ApplicationController.monthNameMap
                    .get(formatdatearr[1].trim()) + "-"
                    + formatdatearr[0];

            if (status.equalsIgnoreCase("Cold") || status.equalsIgnoreCase("Hot") || status.equalsIgnoreCase("Warm")) {
                compareDateAndTime(date + " " + Integer.toString(hourOfDay) + ":"
                        + timeStringBuilder.toString());
            }
        } else {

            tv_followUpTime.setText("");

        }

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, TimeType timeType) {

    }

    private String getFormattedDate(TextView dateView, TextView timeView) {
        String datetime = "";

        if (!dateView.getText().toString().trim().equals("")) {
            String fDate = dateView.getText().toString().trim();
            String[] formatdate = fDate.split("/");
            fDate = formatdate[2].trim()
                    + "-"
                    + ApplicationController.monthNameMap.get(formatdate[1]
                    .trim()) + "-" + formatdate[0];
            String fTime = timeView.getText().toString().trim();
            String formatTime = fTime.replace("-", ":");
            datetime = fDate + " " + formatTime + ":00";

        } else {
            datetime = "";
        }
        return datetime;
    }

    private boolean compareDate(String selectedDateTime) {
        try {
            final Calendar calendar = Calendar.getInstance();
            String currentDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
            DateTime currentFormattedDate = dtf.parseDateTime(currentDate);
            DateTime selectedDate = dtf.parseDateTime(selectedDateTime);
            if (selectedDate.isBefore(currentFormattedDate)) {
                tv_followUpDate.setText("");
                tv_followUpTime.setText("");
                selectedDateTime = "";
                //editedDate = "";
                CommonUtils.showToast(SellerLeadsDetailPageActivity.this,
                        "Selected Date is before Current Date", Toast.LENGTH_SHORT);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void compareDateAndTime(String selectedDateTime) {
        try {
            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
            DateTime selectedDate = dtf.parseDateTime(selectedDateTime);
            if (selectedDate.isBeforeNow()) {
                tv_followUpDate.setText("");
                tv_followUpTime.setText("");
                selectedDateTime = "";
                // editedDate = "";
                CommonUtils.showToast(SellerLeadsDetailPageActivity.this,
                        "Selected Date is before Current Date", Toast.LENGTH_SHORT);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onValidationSucceeded() {
        if (!tv_followUpDate.getText().toString().isEmpty() && tv_followUpTime.getText().toString().isEmpty()) {
            CommonUtils.showToast(this, "Please select Follow Time", Toast.LENGTH_SHORT);
            return;
        }
        updateLeadRequest();
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
    public void onBackPressed() {

        if (isLeadEdit) {
            findViewById(R.id.nestedScrollVw).scrollTo(0, 0);
            Animation viewHide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.view_hide);
            Animation viewShow = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.activity_in_right);
            viewShow.setDuration(1000);

            findViewById(R.id.editLeadLayout).setAnimation(viewHide);
            findViewById(R.id.addLeadLayout).setAnimation(viewShow);
            findViewById(R.id.editLeadLayout).setVisibility(View.GONE);
            findViewById(R.id.addLeadLayout).setVisibility(View.VISIBLE);

            isLeadEdit = false;
            invalidateOptionsMenu();
            initializeViews(leadDetailModel);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION_CALL) {
            Intent intent;
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Intent intent = adapter.selectedNumberToCall();
                intent = new Intent(Intent.ACTION_CALL);
            } else {
                intent = new Intent(Intent.ACTION_DIAL);
            }
            intent.setData(Uri.parse("tel:+91" + selectedNumberToCall));
            startActivity(intent);
        }
    }
}
