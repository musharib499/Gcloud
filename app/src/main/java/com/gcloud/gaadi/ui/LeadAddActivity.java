package com.gcloud.gaadi.ui;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.BasicListItemAdapter;
import com.gcloud.gaadi.adapter.MakeModelVersionAdapter;
import com.gcloud.gaadi.adapter.SellerLeadsRecyclerAdapter;
import com.gcloud.gaadi.adapter.ViewCommentsAdapter;
import com.gcloud.gaadi.annotations.Email;
import com.gcloud.gaadi.annotations.FirstCharValidation;
import com.gcloud.gaadi.annotations.MobileNumber;
import com.gcloud.gaadi.annotations.NotAllowedChars;
import com.gcloud.gaadi.annotations.RadioGrp;
import com.gcloud.gaadi.annotations.Required;
import com.gcloud.gaadi.annotations.TextRule;
import com.gcloud.gaadi.annotations.rules.Rule;
import com.gcloud.gaadi.annotations.rules.Validator;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.DateType;
import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.constants.TimeType;
import com.gcloud.gaadi.db.LeadsOfflineDB;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.events.CancelMakeEvent;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.AddLeadModel;
import com.gcloud.gaadi.model.BasicListItemModel;
import com.gcloud.gaadi.model.Comments;
import com.gcloud.gaadi.model.CommentsModel;
import com.gcloud.gaadi.model.FollowupDate;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.LeadData;
import com.gcloud.gaadi.model.LeadDetailModel;
import com.gcloud.gaadi.model.LeadsModel;
import com.gcloud.gaadi.model.StockItemModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.service.DatabaseInsertionService;
import com.gcloud.gaadi.syncadapter.GenericAccountService;
import com.gcloud.gaadi.syncadapter.SyncUtils;
import com.gcloud.gaadi.ui.fourmob.datetimepicker.date.DatePickerDialog;
import com.gcloud.gaadi.ui.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.gcloud.gaadi.ui.sleepbot.datetimepicker.time.TimePickerDialog;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.MaterialTextView;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ankit on 21/11/14.
 */
public class LeadAddActivity extends BaseActivity implements
        OnClickListener, DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, Validator.ValidationListener,
        OnCheckedChangeListener, OnNoInternetConnectionListener,
        OnItemClickListener {

    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    boolean isLeadEdit = false;
    //    private GAHelper mGAHelper;
    CommentsModel commentsModel;
    int hour = 0;
    int min = 0;
    String formatdate = "", editedDate = "";
    RadioButton radioButton;
    String crosschecked = "crossed", selectedNumberToCall = "";
    String dealerID = "";
    RadioButton sourceWalkin, sourceGaadi, sourceCartrade, sourceCarwale,
            sourceOlx, sourceQuickr, sourceWebsite, sourceCardekho;
    String makeId = "", modelId = "", makeModelName = "", makea, maString, modelName;

    @TextRule(order = 3, minLength = 10, messageResId = R.string.error_lead_mobile_number)
    @MobileNumber(order = 4, minLength = 10, maxLength = 10, messageResId = R.string.error_mobile_number_invalid)
    @FirstCharValidation(order = 5, notAllowedFirstChars = "0,1,2,3,4,5,6", messageResId = R.string.error_mobile_number_invalid)
    MaterialEditText et_mobilenum;

    String budgetFromID = "", budgetToID = "";
    GCProgressDialog progressBar;
    TextView sourceValueEdit;
    TextView totalBudget;
    //  private RelativeLayout stockLayout;
    StockItemModel model;
    LeadDetailModel leadDetailModel;
    // TextView viewComments;tal
    RecyclerView leadsRecyclerVw;
    TextView viewStocks;
    ImageView moreOptions, make;
    String name = "", mobilenum = "", email = "", source = "", status = "",
            date = "", time = "";
    ScrollView scrollView;
    Button btn_callNow;
    private MaterialTextView budget, budgetTo;
    private int selectedTab = 0;
    // private ActionBar mActionBar;
    private boolean onResumeCalled = false;
    private Intent callIntent = null;
    private MenuItem callItem = null;
    //   private CustomAutoCompleteTextView mMakeModel;
    private Validator mValidator;
    private MakeModelVersionAdapter makeModelVersionAdapter;

    @Required(order = 1, messageResId = R.string.enter_lead_name)
    @NotAllowedChars(order = 2, notAllowedChars = Constants.NOT_ALLOWED_NAME_CHARS, messageResId = R.string.error_lead_name_invalid_chars)
    private MaterialEditText et_fullName;

    private ArrayList<BasicListItemModel> budgetList = new ArrayList<BasicListItemModel>();
    private ArrayList<BasicListItemModel> budgetToList = new ArrayList<BasicListItemModel>();
    private TextView tv_contactNum;
    private BasicListItemAdapter budgetFromAdapter, budgetToAdapter;

    @Email(order = 6, messageResId = R.string.error_lead_email_address)
    private MaterialEditText et_emailId;

    private TextView tv_email;
    private ListView listView;
    private BasicListItemModel selectedFromBudget, selectedToBudget;

    @RadioGrp(order = 6, messageResId = R.string.error_provide_lead_source)
    private RadioGroup leadSource;

    private RadioGroup leadStatus;
    private TextView dateView;
    private TextView timeView;
    private EditText comments;
    private TextView leadAdd, commentsHeader;
    private HorizontalScrollView statusscrollview;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private LinearLayout linearLayoutViewSource;
    private String callSource = "";
    private boolean showCallButton;
    private Menu menu;
    private  MenuItem iv_edit;
    private boolean checkDeeplink = false, checkChangeListenerCalled = false;
    private FilterQueryProvider mmFilterQueryProvider = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getCursor(constraint);
        }
    };
    private boolean performCall = false;

    public void EditView() {


        //  scrollView.scrollTo(0, 0);

        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                Constants.CATEGORY_BUYER_LEAD_DETAIL,
                Constants.CATEGORY_BUYER_LEAD_DETAIL,
                Constants.ACTION_TAP,
                Constants.LABEL_CALL_BUTTON,
                0);

        findViewById(R.id.editLeadLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.addLeadLayout).setVisibility(View.GONE);

        // invalidateOptionsMenu();
        //  ((ScrollView)findViewById(R.id.scrollView)).smoothScrollTo(0,0);
        // scrollView.scrollTo(0, 0);
        Animation viewHide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.activity_out_left);
        Animation viewShow = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.view_show);
        findViewById(R.id.editLeadLayout).setAnimation(viewShow);
        findViewById(R.id.addLeadLayout).setAnimation(viewHide);


        //
    }

    public void OnMenuChange() {
        if (isLeadEdit) {
            isLeadEdit = false;
            initializeViews(leadDetailModel);
            Animation viewHide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.view_hide);
            Animation viewShow = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.activity_in_right);
            viewShow.setDuration(1000);

            findViewById(R.id.editLeadLayout).setAnimation(viewHide);
            findViewById(R.id.addLeadLayout).setAnimation(viewShow);
            findViewById(R.id.editLeadLayout).setVisibility(View.GONE);
            findViewById(R.id.addLeadLayout).setVisibility(View.VISIBLE);
            invalidateOptionsMenu();
            // ((ScrollView)findViewById(R.id.scrollView)).smoothScrollTo(0, 0);

        } else {
            isLeadEdit = true;
            EditView();
            et_mobilenum.setEnabled(false);
            et_mobilenum.setHideUnderline(true);


            invalidateOptionsMenu();
            if (leadDetailModel != null) {
                et_fullName.setText(leadDetailModel.getName().trim());
                et_emailId.setText(leadDetailModel.getEmailID());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_add_lead, frameLayout);

        setTitleMsg("Add Lead");

        progressBar = new GCProgressDialog(this, this, getString(R.string.please_wait));

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        final RelativeLayout rootView = (RelativeLayout) findViewById(R.id.rootView);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        // stockLayout = (RelativeLayout) findViewById(R.id.stockLayout);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
                if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
            }
        });

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

        dateView = (TextView) findViewById(R.id.followupDate);
        dateView.setOnClickListener(this);

        timeView = (TextView) findViewById(R.id.followupTime);
        timeView.setOnClickListener(this);

        leadAdd = (TextView) findViewById(R.id.addLead);
        leadAdd.setOnClickListener(this);

        linearLayoutViewSource= (LinearLayout) findViewById(R.id.sourceLayout);
        leadSource = (RadioGroup) findViewById(R.id.leadSource);
        leadSource.setOnCheckedChangeListener(this);

        sourceValueEdit = (TextView) findViewById(R.id.sourceValueEdit);
        leadsRecyclerVw = (RecyclerView) findViewById(R.id.leadsRecyclerView);
        leadsRecyclerVw.setLayoutManager(new LinearLayoutManager(LeadAddActivity.this, LinearLayoutManager.HORIZONTAL, false));
        statusscrollview = (HorizontalScrollView) findViewById(R.id.statusscrollview);

        leadStatus = (RadioGroup) findViewById(R.id.leadStatus);
        leadStatus.setOnCheckedChangeListener(this);
        btn_callNow = (Button) findViewById(R.id.btn_callNow);
        btn_callNow.setOnClickListener(this);
        if (!ApplicationController.checkInternetConnectivity()) {
            btn_callNow.setVisibility(View.INVISIBLE);
        }

        //  leadName = (EditText) findViewById(R.id.leadName);


        sourceCardekho = (RadioButton) findViewById(R.id.sourceCardekho);
        sourceWalkin = (RadioButton) findViewById(R.id.sourceWalkin);
        sourceGaadi = (RadioButton) findViewById(R.id.sourceGaadi);
        sourceCartrade = (RadioButton) findViewById(R.id.sourceCartrade);
        sourceCarwale = (RadioButton) findViewById(R.id.sourceCarwale);
        sourceOlx = (RadioButton) findViewById(R.id.sourceOlx);
        sourceQuickr = (RadioButton) findViewById(R.id.sourceQuickr);
        sourceWebsite = (RadioButton) findViewById(R.id.sourceWebsite);


        // viewComments = (TextView) findViewById(R.id.viewcommnts);
        viewStocks = (TextView) findViewById(R.id.viewstocks);
        et_fullName = (MaterialEditText) findViewById(R.id.et_fullName);
        et_emailId = (MaterialEditText) findViewById(R.id.et_email);
        et_mobilenum = (MaterialEditText) findViewById(R.id.et_mobileNum);

        tv_contactNum = (TextView) findViewById(R.id.tv_contactNum);
        tv_email = (TextView) findViewById(R.id.tv_email);
        listView = (ListView) findViewById(R.id.previousCommentsList);
        commentsHeader = (TextView) findViewById(R.id.comments_header);
        listView.setOnTouchListener(new View.OnTouchListener() {

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


        budget = (MaterialTextView) findViewById(R.id.budget);
        totalBudget = (TextView) findViewById(R.id.budgetview);

        budget.setOnClickListener(this);
        budgetTo = (MaterialTextView) findViewById(R.id.budgetto);
        budgetTo.setOnClickListener(this);
        formLists();
        //formBudgetToLists();
        makeModelVersionAdapter = new MakeModelVersionAdapter(this, null);
        /*mMakeModel = (CustomAutoCompleteTextView) findViewById(R.id.makeModel);
       // mMakeModel.setType(MakeModelType.MAKE);
        mMakeModel.setThreshold(1);
        mMakeModel.setAdapter(makeModelVersionAdapter);
        makeModelVersionAdapter.setFilterQueryProvider(mmFilterQueryProvider);
        mMakeModel.setOnItemClickListener(this);*/


        Intent intent = getIntent();
        String leadId = "";
        if ("NL".equals(intent.getStringExtra(Constants.CALL_SOURCE))
                || "RC".equals(intent.getStringExtra(Constants.CALL_SOURCE))
                || "CT".equals(intent.getStringExtra(Constants.CALL_SOURCE)))
        {

            linearLayoutViewSource.setVisibility(View.VISIBLE);

        }
        String token = "";

        showCallButton = false;
        //invalidateOptionsMenu();

        if (intent.hasExtra(Constants.FROM_DEEPLINK)) { // DEEPLINK CASE
            if (intent.getBooleanExtra(Constants.FROM_DEEPLINK, false)) {

                token = intent.getStringExtra(Constants.TOKEN);

                GCLog.e("Lead id: " + token);
                fetchLeadData(token, true);
                checkDeeplink = true;

            }

        } else if (intent.hasExtra(Constants.FROM_NOTIFICATION) && intent.getBooleanExtra(Constants.FROM_NOTIFICATION, false)) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(Integer.parseInt(intent.getStringExtra("nID")));

            if (intent.hasExtra("rowId")) {
                ContentValues values = new ContentValues();
                values.put(MakeModelVersionDB.COLUMN_TYPE, 2);

                startService(new Intent(this, DatabaseInsertionService.class)
                        .putExtra(Constants.ACTION, "update")
                        .putExtra(Constants.PROVIDER_URI, Uri.parse("content://"
                                + Constants.NOTIFICATION_CONTENT_AUTHORITY + "/" + MakeModelVersionDB.TABLE_NOTIFICATION))
                        .putExtra(Constants.CONTENT_VALUES, values)
                        .putExtra(Constants.SELECTION, MakeModelVersionDB.COLUMN_ID + " = ?")
                        .putExtra(Constants.SELECTION_ARGS, new String[]{String.valueOf(intent.getLongExtra("rowId", 0))}));
            }

            if (!intent.hasExtra("read")) {
                HashMap<String, String> logParams = new HashMap<>();
                logParams.put(Constants.GCM_RESPONSE_CODE, "0"); // notification was opened and was destined for this dealer.
                logParams.put(Constants.SCREEN_NAME, getClass().getSimpleName());
                logParams.put(Constants.GCM_MESSAGE, intent.getExtras().toString());
                logParams.put(Constants.METHOD_LABEL, Constants.LOG_NOTIFICATION_METHOD);

                RetrofitRequest.makeLogNotificationRequest(this, logParams, new Callback<GeneralResponse>() {
                    @Override
                    public void success(GeneralResponse generalResponse, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }

            callSource = "ML";
            if (intent.hasExtra(Constants.PERFORM_CALL) && intent.getBooleanExtra(Constants.PERFORM_CALL, false)) {
                performCall = true;
            }
            fetchLeadData(intent.getStringExtra("leadId"), false);
        } else {

            if (intent.getStringExtra(Constants.LEAD_MOBILE) != null) {
                tv_contactNum.setText(intent.getStringExtra(
                        Constants.LEAD_MOBILE));
                if ((intent.getStringExtra(Constants.CALL_SOURCE) != null) &&
                        (getIntent().getStringExtra(Constants.CALL_SOURCE).equalsIgnoreCase("CT")
                                || getIntent().getStringExtra(Constants.CALL_SOURCE).equalsIgnoreCase("RC"))) {
                    et_mobilenum.setText(intent.getStringExtra(
                            Constants.LEAD_MOBILE));
                    et_mobilenum.setEnabled(false);
                }
            }
            if (intent.getStringExtra(Constants.CALL_SOURCE) != null
                    && intent.getStringExtra(Constants.CALL_SOURCE).equalsIgnoreCase("CT")) {
                setTitleMsg("Add Lead");
            }
            if ((intent.getStringExtra(Constants.LEAD_NAME) != null)
                    && (!intent.getStringExtra(Constants.LEAD_NAME)
                    .equals("<Unknown>"))) {

                setTitleMsg(intent.getStringExtra(
                        Constants.LEAD_NAME));
                et_fullName.setText(intent.getStringExtra(Constants.LEAD_NAME));
                //  leadName.setText();
            }
            if ((intent.getStringExtra(Constants.CALL_SOURCE) != null)) {
                callSource = getIntent().getStringExtra(Constants.CALL_SOURCE);

                GCLog.e("callSource: " + callSource);
            }



            setprefilledData(null, false);
          /*  fetchLeadData(((LeadDetailModel) this.getIntent()
                    .getSerializableExtra(Constants.MODEL_DATA)).getLeadId());*/

        }

        // shifting code up to handle deeplinking scenarios
        handleDateTimeValues();

        /*if (!callSource.equalsIgnoreCase("RC")
                && !callSource.equalsIgnoreCase("CT")
                && !callSource.equalsIgnoreCase("NL")
                && !callSource.isEmpty()) {
            // comments will only be shown for already added lead.
            makeViewCommentsAPiCall();
            if (!checkDeeplink)
            {
                fetchLeadData(((LeadDetailModel) this.getIntent()
                        .getSerializableExtra(Constants.MODEL_DATA)).getLeadId(),
                        false);
            }

        }*/
    }

    private void fetchCarList(String leadId) {
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.METHOD_LABEL, Constants.LEADS_METHOD);
        params.put(Constants.LEAD_ID_KEY, leadId);
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(ApplicationController.getInstance(), Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.DEALER_PASSWORD, CommonUtils.getStringSharedPreference(ApplicationController.getInstance(), Constants.UC_DEALER_PASSWORD, ""));

        RetrofitRequest.getLeadData(params, new Callback<LeadsModel>() {
            @Override
            public void success(LeadsModel leadsModel, Response response) {
                if ("T".equalsIgnoreCase(leadsModel.getStatus())
                        && leadsModel.getLeads() != null
                        && leadsModel.getLeads().size() > 0
                        && leadsModel.getLeads().get(0) != null
                        && leadsModel.getLeads().get(0).getCarsList() != null
                        && leadsModel.getLeads().get(0).getCarsList().size() > 0) {
                    leadsRecyclerVw.setVisibility(View.VISIBLE);
                    leadsRecyclerVw.setAdapter(new SellerLeadsRecyclerAdapter(LeadAddActivity.this,
                            leadsModel.getLeads().get(0).getCarsList()));
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void fetchLeadData(String leadId, boolean fromDeepLink) {

        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.METHOD_LABEL, Constants.LEADS_METHOD);
        if (fromDeepLink) {
            params.put(Constants.TOKEN, leadId);
        } else {
            params.put(Constants.LEAD_ID_KEY, leadId);
        }
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(ApplicationController.getInstance(), Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.DEALER_PASSWORD, CommonUtils.getStringSharedPreference(ApplicationController.getInstance(), Constants.UC_DEALER_PASSWORD, ""));

        progressBar.setCancelable(false);
        progressBar.show();

        RetrofitRequest.getLeadData(params, new Callback<LeadsModel>() {
            @Override
            public void success(LeadsModel leadsModel, retrofit.client.Response response) {

                if (progressBar != null) {
                    progressBar.dismiss();
                }
                if ("T".equalsIgnoreCase(leadsModel.getStatus())) {
                    if (leadsModel.getLeads() != null) {
                        setprefilledData(leadsModel.getLeads(), true);
                        if (performCall) {
                            btn_callNow.performClick();
                            performCall = false;
                        }
                        makeViewCommentsAPiCall();
                        GCLog.e(leadsModel.getLeads().toString());
                    } else {
                        CommonUtils.showToast(LeadAddActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT);
                    }
                } else {
                    if (checkDeeplink) {
                        new AlertDialog.Builder(LeadAddActivity.this)
                                .setTitle(getString(R.string.information))
                                .setMessage(leadsModel.getMessage())
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
                        CommonUtils.showToast(LeadAddActivity.this, leadsModel.getError(), Toast.LENGTH_SHORT);
                    }
                }


            }

            @Override
            public void failure(RetrofitError error) {
                if (progressBar != null) {
                    progressBar.dismiss();
                }
                CommonUtils.showErrorToast(ApplicationController.getInstance(), error, Toast.LENGTH_SHORT);
            }
        });

    }

    private void initializeViews(LeadDetailModel leadDetailsObj) {
        leadDetailModel = leadDetailsObj;
        tv_email.setText(leadDetailsObj.getEmailID());
        tv_contactNum.setText(leadDetailsObj.getNumber());
        et_mobilenum.setText(leadDetailsObj.getNumber());
        et_fullName.setText(leadDetailsObj.getName());
        et_emailId.setText(leadDetailsObj.getEmailID());
        //et_mobilenum.sta();
        if (CommonUtils.isValidField(leadDetailsObj.getLeadStatus())) {
            setLeadStatus(leadDetailsObj.getLeadStatus());
        }
        if (!leadDetailsObj.getformattedfollowDate().trim().equals("")) {
            String formattedFollowDate = leadDetailsObj.getformattedfollowDate();
            String[] formattedDate = formattedFollowDate.split("\\s+");
            dateView.setText("" + formattedDate[0]);
            timeView.setText("" + formattedDate[1]);


        } else {
            dateView.setText("");
            timeView.setText("");

        }

        if (CommonUtils.isValidField(leadDetailsObj.getCarsList())) {
            /// tv_totalCarsSold.setVisibility(View.VISIBLE);
            leadsRecyclerVw.setVisibility(View.VISIBLE);
            leadsRecyclerVw.setAdapter(new SellerLeadsRecyclerAdapter(LeadAddActivity.this, leadDetailsObj.getCarsList()));
            //tv_totalCarsSold.setText(((LeadDetailModel) getIntent().getSerializableExtra(Constants.MODEL_DATA)).getName() + " is selling " + leadDetailsObj.getCarsList().size() + " cars");
        } else if (ApplicationController.checkInternetConnectivity()) {
            fetchCarList(leadDetailsObj.getLeadId());
        }

        if (CommonUtils.isValidField(leadDetailsObj.getCommentsArrayList())) {
            // showViewCommentsFragment(response);
            listView.setVisibility(View.VISIBLE);
            commentsHeader.setVisibility(View.VISIBLE);
            commentsHeader.setText("Comments (" + leadDetailsObj.getCommentsArrayList().size() + ")");
            ArrayList<Comments> inReverse = leadDetailsObj.getCommentsArrayList();
            //Collections.reverse(inReverse);
            ViewCommentsAdapter commentsAdapter = new ViewCommentsAdapter(LeadAddActivity.this, inReverse);
            listView.setAdapter(commentsAdapter);
        }

    }

    private void handleDateTimeValues() {

        final Calendar calendar = Calendar.getInstance();
        if (!editedDate.equals("")) {
            int year = Integer.parseInt(editedDate.split("\\s+")[0].split("-")[0]);
            int month = Integer.parseInt(editedDate.split("\\s+")[0].split("-")[1]) - 1;
            int day = Integer.parseInt(editedDate.split("\\s+")[0].split("-")[2]);
            datePickerDialog = DatePickerDialog.newInstance(this,
                    year, month,
                    day, false);
        } else {
            datePickerDialog = DatePickerDialog.newInstance(this,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH), false);
        }

        if (!editedDate.equals("")) {
            int hour = Integer.parseInt(editedDate.split("\\s+")[1].split(":")[0]);
            int min = Integer.parseInt(editedDate.split("\\s+")[1].split(":")[1]);

            timePickerDialog = TimePickerDialog.newInstance(this,
                    hour,
                    min, false, false);
        } else {
            if ((calendar.get(Calendar.MINUTE) + 5) < 60) {
                timePickerDialog = TimePickerDialog.newInstance(this,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE) + 5, false, false);

            } else {
                timePickerDialog = TimePickerDialog.newInstance(this,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), false, false);

            }
        }


    }

    private void setprefilledData(ArrayList<LeadDetailModel> leads, boolean fromDeeplink) {

        String viewLead = "";


        // handling done for deeplinking.
        if (leads == null) {

            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                if (extras.getString(Constants.VIEW_LEAD) != null) {

                    leadDetailModel = (LeadDetailModel) this.getIntent()
                            .getSerializableExtra(Constants.MODEL_DATA);
                    initializeViews(leadDetailModel);
                    viewLead = extras.getString(Constants.VIEW_LEAD);

                    selectedTab = extras.getInt(Constants.SELECTED_TAB);
                }
            }
        } else {
            viewLead = Constants.VALUE_VIEWLEAD;
            if (!leads.isEmpty()) {
                leadDetailModel = leads.get(0);
                initializeViews(leadDetailModel);
            } else {
                showCallButton = false;
                invalidateOptionsMenu();
                return;
            }
        }

        //toolbar.setTitle("View Lead");


        if (viewLead.equals(Constants.VALUE_VIEWLEAD)) {
            // viewComments.setVisibility(View.VISIBLE);
            viewStocks.setVisibility(View.VISIBLE);
            //viewComments.setOnClickListener(this);
            viewStocks.setOnClickListener(this);
            dealerID = CommonUtils.isValidField(leadDetailModel.getDealerID()) ? leadDetailModel.getDealerID() : "";
            setTitleMsg(leadDetailModel.getName().trim());
            // tv_email.setText(leadDetailModel.ge);
            tv_contactNum.setText(leadDetailModel.getNumber());
            if (CommonUtils.isValidField(leadDetailModel.getBudgetto())
                    && CommonUtils.isValidField(leadDetailModel.getBudgetfrom())) {
                totalBudget.setText(Constants.RUPEES_SYMBOL + " " +
                        ApplicationController.budgetFrom.get(leadDetailModel.getBudgetfrom()) + "-" +
                        ApplicationController.budgetTo.get(leadDetailModel.getBudgetto()));
            }
            // marking lead as viewed
            markLeadAsViewed(leadDetailModel.getNumber(), fromDeeplink);
            if (!leadDetailModel.getBudgetfrom().trim()
                    .equals("N/A")
                    || !leadDetailModel.getBudgetfrom().trim()
                    .equals("")) {

                String fromBudget = ApplicationController.budgetFrom.get(leadDetailModel.getBudgetfrom().trim());
                if (fromBudget != null) {
                    selectedFromBudget = new BasicListItemModel(leadDetailModel.getBudgetfrom().trim(), fromBudget);
                    budget.setText(selectedFromBudget.getValue());
                    budget.startLabelAnimation();
                }
            }

            if (!leadDetailModel.getBudgetto().trim()
                    .equals("N/A")
                    || !leadDetailModel.getBudgetto().trim()
                    .equals("")) {

                String toBudget = ApplicationController.budgetTo.get(leadDetailModel.getBudgetto().trim());
                if (toBudget != null) {
                    if (selectedFromBudget != null) {
                        if (selectedFromBudget.getValue().equalsIgnoreCase(ApplicationController.budgetFrom.get("1500000"))) {
                            selectedToBudget = null;
                            budgetTo.setEnabled(false);
                            budgetTo.setText("");

                        } else {
                            selectedToBudget = new BasicListItemModel(leadDetailModel.getBudgetto().trim(), toBudget);
                            budgetTo.setEnabled(true);
                            budgetTo.setText(selectedToBudget.getValue());
                            budgetTo.startLabelAnimation();
                        }
                    } else {
                        budgetTo.setEnabled(true);
                        selectedToBudget = new BasicListItemModel(leadDetailModel.getBudgetto().trim(), toBudget);
                        budgetTo.setText(selectedToBudget.getValue());
                        budgetTo.startLabelAnimation();
                    }
                } else {
                    if (selectedFromBudget != null) {
                        if (selectedFromBudget.getId().equals("1500000")) {
                            budgetTo.setEnabled(false);
                            budgetTo.setText("");
                        }
                    }
                }


            }


            if (!leadDetailModel.getModel().trim()
                    .equals("N/A")
                    || !leadDetailModel.getModel().trim()
                    .equals("")) {
                //   mMakeModel.setText(leadDetailModel.getModel().trim());
            }

            if (!"N/A".equalsIgnoreCase(leadDetailModel.getEmailID().trim())
                    || !leadDetailModel.getEmailID().trim()
                    .equals("")) {
                tv_email.setText(leadDetailModel.getEmailID().trim());
            }

            if (leadDetailModel.getMakeId() != 0) {
//                mMakeModel.setCompoundDrawablesWithIntrinsicBounds(
//                        ApplicationController.makeLogoMap
//                                .get(leadDetailModel.getMakeId()), 0,
//                        R.drawable.close_layer_dark, 0);
            }

            setSource(leadDetailModel.getSource());
            setLeadStatus(leadDetailModel.getLeadStatus());
            if (!leadDetailModel.getformattedfollowDate().trim().equals("")) {
                String formattedFollowDate = leadDetailModel.getformattedfollowDate();
                String[] formattedDate = formattedFollowDate.split("\\s+");
                dateView.setText(formattedDate[0]);
                timeView.setText(formattedDate[1]);
                editedDate = leadDetailModel.getformattedfollowUPDate();
            } else {
                dateView.setText("");
                timeView.setText("");
                editedDate = "";
            }
        }

        if (leadDetailModel != null) {

           /* if (leadDetailModel.getCarsList().size() > 0) {
                // tv_totalCarsSold.setVisibility(View.VISIBLE);
                leadsRecyclerVw.setVisibility(View.VISIBLE);
                leadsRecyclerVw.setAdapter(new SellerLeadsRecyclerAdapter(LeadAddActivity.this, leadDetailModel.getCarsList()));
                // tv_totalCarsSold.setText(((LeadDetailModel) getIntent().getSerializableExtra(Constants.MODEL_DATA)).getName() + " is selling " + leadDetailModel.getCarsList().size() + " cars");
            }*/
            if (!leadDetailModel.getCarID().trim().equals("0") && !leadDetailModel.getCarID().isEmpty()) {

                // mMakeModel.setVisibility(View.GONE);
                //   leadsRecyclerVw.setVisibility(View.VISIBLE);
                //  leadsRecyclerVw.setAdapter(new SellerLeadsRecyclerAdapter(LeadAddActivity.this,leadDetailModel.getCarsList()));

            /*    stockLayout.setVisibility(View.VISIBLE);
                ImageView makeLogo = (ImageView) findViewById(R.id.makeLogo);
                ImageView stockImage = (ImageView) findViewById(R.id.stockImage);
                TextView stockPrice = (TextView) findViewById(R.id.stockPrice);
                TextView modelVersion = (TextView) findViewById(R.id.stockModelVersion);
                TextView modelYear = (TextView) findViewById(R.id.modelYear);
                TextView colorValue = (TextView) findViewById(R.id.colorValue);
                TextView kmsDriven = (TextView) findViewById(R.id.kmsDriven);

                makeLogo.setImageResource(ApplicationController.makeLogoMap.get(leadDetailModel.getMakeId()));
                stockPrice.setText(leadDetailModel.getPrice());
                modelVersion.setText(leadDetailModel.getModel());
                modelYear.setText(leadDetailModel.getYear());
                colorValue.setText(leadDetailModel.getColor());
                kmsDriven.setText(leadDetailModel.getKm());
                leadDetailModel.getCarsList();

                String imageUrl = leadDetailModel.getImageIcon();
                if ((imageUrl != null) && !imageUrl.isEmpty()) {
                    Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.image_load_default_small)
                            .error(R.drawable.no_image_default_small)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(stockImage);
                } else {
                    stockImage.setImageResource(R.drawable.no_image_default_small);
                }*/

            }
            //tv_contactNum.setEnabled(false);
           /* for (int i = 0; i < leadSource.getChildCount(); i++) {
                leadSource.getChildAt(i).setEnabled(false);
            }*/
            TextView addLead = (TextView) findViewById(R.id.addLead);
            addLead.setText("Update Lead");
            // toolbar.setTitle("View Lead");
            if (!fromDeeplink) {
                setTitleMsg("View Lead");
            }

            showCallButton = true;
            invalidateOptionsMenu();
        }

        if (leadDetailModel!=  null && leadDetailModel.getVerified() != null
                && !leadDetailModel.getVerified().isEmpty()
                && leadDetailModel.getVerified().equalsIgnoreCase("1")) {
            findViewById(R.id.verified_sign).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.verified_sign).setVisibility(View.GONE);
        }


    }

    private void markLeadAsViewed(String number, boolean fromDeeplink) {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.MOBILE_NUM, number);
        params.put(Constants.FROM_DEEPLINK, fromDeeplink ? "1" : "0");
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.UC_DEALER_USERNAME, CommonUtils.getStringSharedPreference(getApplicationContext(), Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.METHOD_LABEL, Constants.LEAD_VIEW_METHOD);

        RetrofitRequest.leadViewRequest(getApplicationContext(), params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, retrofit.client.Response response) {
                GCLog.e("lead view api response: " + response.toString());
                if ("T".equalsIgnoreCase(generalResponse.getStatus())) {
                    GCLog.e("Successfully marked as viewed lead.");
                } else {
                    GCLog.e("Unable to mark lead as viewed.");
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof UnknownHostException) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_BUYER_LEAD_DETAIL,
                            Constants.CATEGORY_BUYER_LEAD_DETAIL,
                            Constants.ACTION_TAP,
                            Constants.LABEL_NO_INTERNET,
                            0);

                } else {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_BUYER_LEAD_DETAIL,
                            Constants.CATEGORY_BUYER_LEAD_DETAIL,
                            Constants.CATEGORY_IO_ERROR,
                            error.getMessage(),
                            0);
                }

            }
        });
/*
        LeadViewRequest leadViewRequest = new LeadViewRequest(
                LeadAddActivity.this,
                Request.Method.POST,
                Constants.getWebServiceURL(LeadAddActivity.this),
                params,
                new Response.Listener<GeneralResponse>() {
                    @Override
                    public void onResponse(GeneralResponse response) {
                        GCLog.e("lead view api response: " + response.toString());
                        if ("T".equalsIgnoreCase(response.getStatus())) {
                            GCLog.e("Successfully marked as viewed lead.");
                        } else {
                            GCLog.e("Unable to mark lead as viewed.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getCause() instanceof UnknownHostException) {
                            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                    Constants.CATEGORY_BUYER_LEAD_DETAIL,
                                    Constants.CATEGORY_BUYER_LEAD_DETAIL,
                                    Constants.ACTION_TAP,
                                    Constants.LABEL_NO_INTERNET,
                                    0);

                        } else {
                            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                    Constants.CATEGORY_BUYER_LEAD_DETAIL,
                                    Constants.CATEGORY_BUYER_LEAD_DETAIL,
                                    Constants.CATEGORY_IO_ERROR,
                                    error.getMessage(),
                                    0);
                        }
                    }
                }
        );

        ApplicationController.getInstance().addToRequestQueue(leadViewRequest);*/
    }

    private void setSource(String selectedSource) {

        // leadSource.setVisibility(View.GONE);
       // selectedSource=selectedSource.charAt(0)+selectedSource.substring(1,selectedSource.length()).toLowerCase();
        selectedSource=CommonUtils.camelCase(selectedSource);
        sourceValueEdit.setText(selectedSource);
        source = selectedSource;

    }

    private void setLeadStatus(String status) {

        RadioButton statusHot = (RadioButton) findViewById(R.id.statusHot);
        RadioButton statusWarm = (RadioButton) findViewById(R.id.statusWarm);
        RadioButton statusCold = (RadioButton) findViewById(R.id.statusCold);
        RadioButton statusClosed = (RadioButton) findViewById(R.id.statusClosed);
        RadioButton statusWalkin = (RadioButton) findViewById(R.id.statusWalkin);
        RadioButton statusBooked = (RadioButton) findViewById(R.id.statusBooked);
        RadioButton statusConverted = (RadioButton) findViewById(R.id.statusConverted);
        RadioButton statusWalkinScheduled = (RadioButton) findViewById(R.id.statusWalkinScheduled);

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
        } else if (status.equalsIgnoreCase("Booked")) {
            statusBooked.setChecked(true);
        } else if (status.equalsIgnoreCase("Converted")) {
            statusConverted.setChecked(true);
        } else if (status.equalsIgnoreCase("WalkInScheduled")) {
            statusWalkinScheduled.setChecked(true);
        }
        checkChangeListenerCalled = false;

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

    private void formLists() {

        for (Map.Entry<String, String> entry : ApplicationController.budgetFrom
                .entrySet()) {
            BasicListItemModel listItem = new BasicListItemModel(
                    entry.getKey(), entry.getValue());
            budgetList.add(listItem);
        }
        budgetList.trimToSize();
        budgetFromAdapter = new BasicListItemAdapter(this, budgetList);


        for (Map.Entry<String, String> entry : ApplicationController.budgetTo
                .entrySet()) {
            BasicListItemModel listItem = new BasicListItemModel(
                    entry.getKey(), entry.getValue());
            budgetToList.add(listItem);
        }

        budgetToList.trimToSize();
        budgetToAdapter = new BasicListItemAdapter(this, budgetToList);

    }

    private Cursor getCursor(CharSequence constraint) {
        Cursor cursor;
        MakeModelVersionDB db = ApplicationController.getMakeModelVersionDB();
        cursor = db.getModelRecords(constraint, false);
        return cursor;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (ApplicationController.checkInternetConnectivity()) {
            getMenuInflater().inflate(R.menu.lead_detail_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!ApplicationController.checkInternetConnectivity()) {
            return true;
        }
        iv_edit = menu.findItem(R.id.iv_edit);
        if (isLeadEdit) {
            iv_edit.setIcon(R.drawable.toolbar_close);
            getSupportActionBar().setTitle("Edit Lead");
            ((ScrollView) findViewById(R.id.scrollView)).smoothScrollTo(0, 0);
        } else {

            iv_edit.setIcon(R.drawable.toolbar_edit_white);
            if ("ML".equalsIgnoreCase(callSource)
                    || "CL".equalsIgnoreCase(callSource)) {
                try {
                    getSupportActionBar().setTitle(((LeadDetailModel) getIntent().getSerializableExtra(Constants.MODEL_DATA)).getName().trim());
                } catch (Exception ex) {
                    if (leadDetailModel != null && leadDetailModel.getName() != null) {
                        getSupportActionBar().setTitle(leadDetailModel.getName());
                    }
                }
            } else if (checkDeeplink) {
                if (leadDetailModel != null
                        && leadDetailModel.getName() != null
                        && !leadDetailModel.getName().isEmpty()) {
                    getSupportActionBar().setTitle(leadDetailModel.getName().trim());
                }
            } else {
                iv_edit.setVisible(false);
                // EditView();
                if ("CT".equalsIgnoreCase(callSource)) {
                    getSupportActionBar().setTitle("Add Lead");
                }
                findViewById(R.id.editLeadLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.addLeadLayout).setVisibility(View.GONE);
                leadAdd.setText("Add Lead");
            }
            ((ScrollView) findViewById(R.id.scrollView)).smoothScrollTo(0, 0);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = null;

       /* switch (item.getItemId())
        {
            case android.R.id.home:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_ADD_LEAD,
                        Constants.CATEGORY_ADD_LEAD,
                        Constants.ACTION_TAP,
                        Constants.LABEL_BACK_BUTTON,
                        0);
                onBackPressed();
               break;



            case R.id.iv_edit:
                isLeadEdit = true;

                findViewById(R.id.editLeadLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.addLeadLayout).setVisibility(View.GONE);
                setTitleMsg("Edit Lead");

                ((ScrollView)findViewById(R.id.scrollView)).scrollTo(0, 0);
                Animation viewHide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.activity_out_left);
                Animation viewShow = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.view_show);
                findViewById(R.id.editLeadLayout).setAnimation(viewShow);
                findViewById(R.id.addLeadLayout).setAnimation(viewHide);
                if (leadDetailModel.getName().trim() !=null) {
                    et_fullName.setText(leadDetailModel.getName().trim());
                    et_emailId.setText(leadDetailModel.getEmailID());
                }
                break;
        }
*/


        // noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                    Constants.CATEGORY_ADD_LEAD,
                    Constants.CATEGORY_ADD_LEAD,
                    Constants.ACTION_TAP,
                    Constants.LABEL_BACK_BUTTON,
                    0);
            onBackPressed();
            return true;
        } else if (id == R.id.iv_edit) {

            OnMenuChange();

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (iv_edit != null && iv_edit.isVisible()) {   // Fabric #837 by gaurav.kumar@gaadi.com
            if (isLeadEdit) {
                isLeadEdit = false;
                initializeViews(leadDetailModel);
                Animation viewHide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.view_hide);
                Animation viewShow = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.activity_in_right);
                viewShow.setDuration(1000);

                findViewById(R.id.editLeadLayout).setAnimation(viewHide);
                findViewById(R.id.addLeadLayout).setAnimation(viewShow);
                findViewById(R.id.editLeadLayout).setVisibility(View.GONE);
                findViewById(R.id.addLeadLayout).setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
            } else {
                super.onBackPressed();
            }
        }

       /* if (isLeadEdit) {
            ((NestedScrollView) findViewById(R.id.nestedScrollVw)).scrollTo(0, 0);
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
        }*/ else {
            super.onBackPressed();
        }
    }

    private void callLead() {
        String mobileNumber = tv_contactNum.getText().toString().trim();
        // make server call to log call made.
        logCallMade(mobileNumber, leadDetailModel.getCarID());

        if ((mobileNumber != null) && !mobileNumber.isEmpty() && !"null".equalsIgnoreCase(mobileNumber)) {
            ArrayList<String> permissions = new ArrayList<>();
            permissions.add(Manifest.permission.CALL_PHONE);
            if (CommonUtils.getIntSharedPreference(this, Constants.IS_LMS, 0) == 1) {
                permissions.add(Manifest.permission.READ_CALL_LOG);
                CommonUtils.activateLeadCallStateListener(this,
                        LeadFollowUpActivity.class,
                        "+91" + /**/mobileNumber /*/"9560619309"/**/,
                        Constants.VALUE_VIEWLEAD,
                        Constants.NOT_YET_CALLED_FRAG_NO,
                        new LeadData(leadDetailModel));
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !CommonUtils.checkForPermission(this,
                    permissions.toArray(new String[permissions.size()]),
                    Constants.REQUEST_PERMISSION_CALL, "Phone")) {
                selectedNumberToCall = mobileNumber;
                return;
            }
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:+91" + /**/mobileNumber /*/"9560619309"/**/));
            startActivity(intent);
        }
    }

    // function to log all calls made from manage leads page.
    private void logCallMade(String number, String carId) {
        final HashMap<String, String> params = new HashMap<>();
        //  if (mAdapterType == AdapterType.CAR_LEADS) {
        // params.put(Constants.SHARE_SOURCE, Constants.CATEGORY_LEADS_AGAINST_STOCK);
        //     } else {
        params.put(Constants.SHARE_SOURCE, Constants.CATEGORY_BUYER_LEAD_DETAIL);
        // }
        params.put(Constants.SHARE_TYPE, ShareType.CALL.name());
        params.put(Constants.MOBILE_NUM, number);
        params.put(Constants.CAR_ID, carId);
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.METHOD_LABEL, Constants.SENT_CARS_METHOD);
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(getApplicationContext(), Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.DEALER_PASSWORD, CommonUtils.getStringSharedPreference(getApplicationContext(), Constants.UC_DEALER_PASSWORD, ""));


        RetrofitRequest.shareCarsRequest(getApplicationContext(), params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, retrofit.client.Response response) {

            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof UnknownHostException) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_BUYER_LEAD_DETAIL,
                            Constants.CATEGORY_BUYER_LEAD_DETAIL,
                            Constants.ACTION_TAP,
                            Constants.LABEL_NO_INTERNET,
                            0);

                } else {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_BUYER_LEAD_DETAIL,
                            Constants.CATEGORY_BUYER_LEAD_DETAIL,
                            Constants.CATEGORY_IO_ERROR,
                            error.getMessage(),
                            0);
                }


            }
        });

       /* ShareCarsRequest shareCarsRequest = new ShareCarsRequest(LeadAddActivity.this,
                Request.Method.POST,
                Constants.getWebServiceURL(LeadAddActivity.this),
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
                                    Constants.CATEGORY_BUYER_LEAD_DETAIL,
                                    Constants.CATEGORY_BUYER_LEAD_DETAIL,
                                    Constants.ACTION_TAP,
                                    Constants.LABEL_NO_INTERNET,
                                    0);

                        } else {
                            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                    Constants.CATEGORY_BUYER_LEAD_DETAIL,
                                    Constants.CATEGORY_BUYER_LEAD_DETAIL,
                                    Constants.CATEGORY_IO_ERROR,
                                    error.getMessage(),
                                    0);
                        }

                    }
                });

        ApplicationController.getInstance().addToRequestQueue(shareCarsRequest);*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        onResumeCalled = true;
        ApplicationController.getEventBus().register(this);
        if (callItem != null)
            callItem.setEnabled(true);

        if (callIntent != null) {    // Sony Xperia: startActivity can not be called unless context is in Resumed state
            GCLog.e("call intent initiated");
            startActivity(callIntent);
            callIntent = null;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_BUYER_LEAD_DETAIL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //ApplicationController.getEventBus().unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        onResumeCalled = false;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.followupDate:
                findViewById(R.id.addLead).setEnabled(true);
                if (!datePickerDialog.isVisible()) {
                    showDatePickerDialog();
                }
                break;

            case R.id.followupTime:
                findViewById(R.id.addLead).setEnabled(true);
                if (!timePickerDialog.isVisible()) {
                    showTimePickerDialog();
                }
                break;

           /* case R.id.viewcommnts:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_BUYER_LEAD_DETAIL,
                        Constants.CATEGORY_BUYER_LEAD_DETAIL,
                        Constants.ACTION_TAP,
                        Constants.LABEL_VIEW_LEADS,
                        0);
                makeViewCommentsAPiCall();
                break;*/

            case R.id.viewstocks:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_BUYER_LEAD_DETAIL,
                        Constants.CATEGORY_BUYER_LEAD_DETAIL,
                        Constants.ACTION_TAP,
                        Constants.LABEL_VIEW_SIMILAR_STOCKS,
                        0);
                viewStock();

                // showTimePickerDialog();
                break;

            case R.id.addLead:
                v.setEnabled(false);
                clearErrors();
                mValidator.validate();
                break;

            case R.id.budget:
                final ListPopupWindow budgetPopupWindow = new ListPopupWindow(
                        this);
                budgetPopupWindow.setAdapter(budgetFromAdapter);
                budgetPopupWindow.setModal(true);
                budgetPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
                budgetPopupWindow.setAnchorView(findViewById(R.id.budget));
                budgetPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
                budgetPopupWindow
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent,
                                                    View view, int position, long id) {
                                selectedToBudget = null;
                                budgetTo.setText("");

                                selectedFromBudget = (BasicListItemModel) parent
                                        .getAdapter().getItem(position);

                                int fromBudget = 0, toBudget = 0;
                                if (selectedFromBudget.getValue().equals(ApplicationController.budgetFrom.get("1500000"))) {
                                    budgetTo.setText("");
                                    budget.setText(selectedFromBudget.getValue());
                                    selectedToBudget = null;
                                    budgetTo.setEnabled(false);
                                    budgetPopupWindow.dismiss();

                                } else {
                                    budgetTo.setEnabled(true);
                                    fromBudget = Integer.parseInt(selectedFromBudget.getId());

                                    if (selectedToBudget != null) {
                                        toBudget = Integer.parseInt(selectedToBudget.getId());
                                    }

                                    if ((fromBudget > toBudget) && (toBudget != 0)) {
                                        CommonUtils.showToast(LeadAddActivity.this, "Budget from cannot be greater than budget to.", Toast.LENGTH_SHORT);
                                    } else {
                                        budget.setText(selectedFromBudget.getValue());
                                        budgetPopupWindow.dismiss();
                                    }

                                }


                            }
                        });
                budgetPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (budget.getText().toString().isEmpty()) {
                            budget.removeLabelAnimation();
                        }
                        budget.removeLabelFocusAnimation();
                    }
                });
                budget.post(new Runnable() {
                    @Override
                    public void run() {
                        budgetPopupWindow.show();
                    }
                });
                break;
            case R.id.btn_callNow:
                callLead();
                break;


            case R.id.budgetto:
                final ListPopupWindow budgetToPopupWindow = new ListPopupWindow(
                        this);
                budgetToPopupWindow.setAdapter(budgetToAdapter);
                budgetToPopupWindow.setModal(true);
                budgetToPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
                budgetToPopupWindow.setAnchorView(findViewById(R.id.budgetto));
                budgetToPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
                budgetToPopupWindow
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent,
                                                    View view, int position, long id) {
                                selectedToBudget = (BasicListItemModel) parent
                                        .getAdapter().getItem(position);

                                int fromBudget = 0, toBudget = 0;
                                if (selectedFromBudget != null) {
                                    fromBudget = Integer.parseInt(selectedFromBudget.getId());
                                    toBudget = Integer.parseInt(selectedToBudget.getId());
                                    if ((fromBudget > toBudget)) {
                                        CommonUtils.showToast(LeadAddActivity.this, "Budget from cannot be greater than budget to.", Toast.LENGTH_SHORT);
                                    } else if (toBudget == 0) {
                                        CommonUtils.showToast(LeadAddActivity.this, "BudgetTo can not be equal to 0", Toast.LENGTH_SHORT);
                                    } else {
                                        budgetTo.setText(selectedToBudget.getValue());
                                        budgetToPopupWindow.dismiss();
                                    }

                                } else {

                                }


                            }
                        });
                budgetToPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (budgetTo.getText().toString().isEmpty()) {
                            budgetTo.removeLabelAnimation();
                        }
                        budgetTo.removeLabelFocusAnimation();
                    }
                });
                budgetTo.post(new Runnable() {
                    @Override
                    public void run() {
                        budgetToPopupWindow.show();
                    }
                });
                break;

        }
    }

    private void clearErrors() {
        //leadName.setError(null);

        tv_email.setError(null);
        tv_contactNum.setError(null);
    }

    private void viewStock() {

        final HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.DEALER_USERNAME, CommonUtils
                .getStringSharedPreference(this, Constants.UC_DEALER_USERNAME,
                        ""));
        params.put(Constants.DEALER_PASSWORD, CommonUtils
                .getStringSharedPreference(this, Constants.UC_DEALER_PASSWORD,
                        ""));
        params.put(Constants.UCDID, String.valueOf(CommonUtils
                .getIntSharedPreference(this, Constants.UC_DEALER_ID, -1)));
        params.put(Constants.LEAD_NAME, toolbar.getTitle().toString().trim());
        params.put(Constants.LEAD_MOBILE, et_mobilenum.getText().toString()
                .trim());
        params.put(Constants.EMAIL, tv_email.getText().toString()
                .trim());
        params.put(Constants.LEAD_SOURCE, source);

        if (formatdate.equals("")) {
            if (editedDate.equals("")) {

                params.put(Constants.NEXT_FOLLOW,
                        getFormattedDate(dateView, timeView));

            } else {
                params.put(Constants.NEXT_FOLLOW, editedDate);
            }
        } else {
            params.put(Constants.NEXT_FOLLOW, formatdate);
        }

        params.put(Constants.LEAD_STATUS, status);
        if (isLeadEdit) {
            if (selectedFromBudget != null) {
                params.put(Constants.BUDGETFROM, selectedFromBudget.getId());
            } else {
                params.put(Constants.BUDGETFROM, "");
            }
            if (selectedToBudget != null) {
                params.put(Constants.BUDGETTO, selectedToBudget.getId());
            } else {
                params.put(Constants.BUDGETTO, "");
            }
        }
        if (makeModelName.equals("")) {
            if (!crosschecked.equals("")) {
                params.put(Constants.ADDMAKEMODEL, leadDetailModel.getMakename() + " "
                        + leadDetailModel.getModelName());
            } else {
                params.put(Constants.ADDMAKEMODEL, "");
            }
        } else {
            params.put(Constants.ADDMAKEMODEL, makeModelName);
        }
        params.put(Constants.COMMENT, comments.getText().toString().trim());

        Intent intent = new Intent(LeadAddActivity.this, LeadsActivity.class);
        intent.putExtra(Constants.LEAD_MOBILE, tv_contactNum.getText().toString()
                .trim());
        intent.putExtra(Constants.LEAD_NAME, toolbar.getTitle().toString()
                .trim());
        intent.putExtra("hashMapKey", params);
        startActivity(intent);

    }

    private void makeViewCommentsAPiCall() {
        // progressBar.show();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("mobile", tv_contactNum.getText().toString().trim());
        params.put(Constants.DEALERID, dealerID);
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.METHOD_LABEL, Constants.VIEW_COMMENT_API);
        GCLog.e("Connents", params.toString());
        // params.put("mobile","9873269130");
        // params.put(Constants.DEALERID,"1217");

        RetrofitRequest.ViewCommentsRequest(params, new Callback<CommentsModel>() {
            @Override
            public void success(CommentsModel commentsModel, retrofit.client.Response response) {
                if (progressBar != null) {
                    progressBar.dismiss();
                }

                if (commentsModel.getStatus().equals("T")) {
                    if (commentsModel.getComments() != null) {
                        if (commentsModel.getComments().size() > 0) {
                            // showViewCommentsFragment(response);
                            listView.setVisibility(View.VISIBLE);
                            commentsHeader.setVisibility(View.VISIBLE);
                            commentsHeader.setText("Comments (" + commentsModel.getComments().size() + ")");
                            ViewCommentsAdapter commentsAdapter = new ViewCommentsAdapter(LeadAddActivity.this, commentsModel.getComments());
                            listView.setAdapter(commentsAdapter);
                        }
                    } else {
                             /*   CommonUtils.showToast(LeadAddActivity.this,
                                        response.getMessage(),
                                        Toast.LENGTH_SHORT);*/
                    }

                } else {
                           /* CommonUtils.showToast(LeadAddActivity.this,
                                    response.getErrorMessage(),
                                    Toast.LENGTH_SHORT);*/
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (progressBar != null) {
                    progressBar.dismiss();
                }

                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(LeadAddActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                } else {
                    CommonUtils.showToast(LeadAddActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                }


            }
        });

       /* ViewCommentsRequest commentRequest = new ViewCommentsRequest(this,
                Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<CommentsModel>() {
                    @Override
                    public void onResponse(CommentsModel response) {
                        if (progressBar != null) {
                            progressBar.dismiss();
                        }

                        if (response.getStatus().equals("T")) {
                            if (response.getComments() != null) {
                                if (response.getComments().size() > 0) {
                                    // showViewCommentsFragment(response);
                                    listView.setVisibility(View.VISIBLE);
                                    commentsHeader.setVisibility(View.VISIBLE);
                                    commentsHeader.setText("Comments (" + response.getComments().size() + ")");
                                    ViewCommentsAdapter commentsAdapter = new ViewCommentsAdapter(LeadAddActivity.this, response.getComments());
                                    listView.setAdapter(commentsAdapter);
                                }
                            } else {
                             *//*   CommonUtils.showToast(LeadAddActivity.this,
                                        response.getMessage(),
                                        Toast.LENGTH_SHORT);*//*
                            }

                        } else {
                           *//* CommonUtils.showToast(LeadAddActivity.this,
                                    response.getErrorMessage(),
                                    Toast.LENGTH_SHORT);*//*
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (progressBar != null) {
                    progressBar.dismiss();
                }

                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(LeadAddActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                } else {
                    CommonUtils.showToast(LeadAddActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                }

            }
        });

        ApplicationController.getInstance().addToRequestQueue(commentRequest,
                Constants.TAG_STOCKS_LIST, false, this);*/
    }

    private void showViewCommentsFragment(CommentsModel model) {

        ViewCommentsFragment commentDialogFragment = ViewCommentsFragment
                .getInstance();
        Bundle args = new Bundle();

        args.putSerializable(Constants.MODEL_DATA, model);
        commentDialogFragment.setArguments(args);
        commentDialogFragment.show(getSupportFragmentManager(),
                "call-logs-dialog");

    }

    private void showTimePickerDialog() {

        if (hour != 0 && min != 0) {

            timePickerDialog = TimePickerDialog.newInstance(this,
                    hour,
                    min, false, false);
        } else if (!editedDate.equals("")) {
            int hour = Integer.parseInt(editedDate.split("\\s+")[1].split(":")[0]);
            int min = Integer.parseInt(editedDate.split("\\s+")[1].split(":")[1]);

            timePickerDialog = TimePickerDialog.newInstance(this,
                    hour,
                    min, false, false);
        } else {
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
    public void onDateSet(DatePickerDialog datePickerDialog, int year,
                          int month, int day) {
        // Toast.makeText(this, "Date set to " + day + ", " + month + ", " +
        // year, Toast.LENGTH_SHORT).show();


        if ("ML".equalsIgnoreCase(callSource)) {
            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                    Constants.CATEGORY_BUYER_LEAD_DETAIL,
                    Constants.CATEGORY_BUYER_LEAD_DETAIL,
                    Constants.ACTION_TAP,
                    Constants.LABEL_LEAD_FOLLOW_UP_DATE,
                    0);
        } else {
            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                    Constants.CATEGORY_BUYER_LEAD_DETAIL,
                    Constants.CATEGORY_BUYER_LEAD_DETAIL,
                    Constants.ACTION_TAP,
                    Constants.LABEL_LEAD_FOLLOW_UP_DATE,
                    0);
        }

        // TODO apply logic to check whether date chosen is greater than today's
        // date.
        FollowupDate followupDate = new FollowupDate(year, month, day);
        dateView.setText(day + "/" + followupDate.getMonthName()
                + "/" + year);
        formatdate = Integer.toString(year) + "-" + Integer.toString(month + 1)
                + "-" + Integer.toString(day);

        if (!(status.equalsIgnoreCase("") || status.equalsIgnoreCase("Closed") || status.equalsIgnoreCase("Booked") || status.equalsIgnoreCase("Walked-in") || status.equalsIgnoreCase("Converted")))
            if (compareDate(formatdate)) {
                timeView.performClick();
            }

    }

    private String getFormattedDate(TextView dateView, TextView timeView) {
        String datetime = "";

        if (!dateView.getText().toString().trim().equals("")) {
            String fDate = dateView.getText().toString().trim();
            String[] formatdate = fDate.split("/");
            fDate = formatdate[0].trim()
                    + "-"
                    + ApplicationController.monthNameMap.get(formatdate[1]
                    .trim()) + "-" + formatdate[2];
            String fTime = timeView.getText().toString().trim();
            String formatTime = fTime.replace("-", ":");
            datetime = fDate + " " + formatTime;

        } else {
            datetime = "";
        }
        return datetime;
    }

    private boolean compareDateAndTime(String selectedDateTime) {
        if (!(status.equalsIgnoreCase("")
                || status.equalsIgnoreCase("Closed")
                || status.equalsIgnoreCase("Booked")
                || status.equalsIgnoreCase("Walked-in")
                || status.equalsIgnoreCase("Converted"))) {

            try {
                DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
                DateTime selectedDate = dtf.parseDateTime(formatdate);
                if (selectedDate.isBeforeNow()) {
                   /* dateView.setText("");
                    timeView.setText("");*/
                    formatdate = "";
                    editedDate = "";
                    CommonUtils.showToast(LeadAddActivity.this,
                            "Selected Date is before Current Date", Toast.LENGTH_SHORT);
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return true;
    }

    private boolean compareDate(String selectedDateTime) {

        if ((!(status.equalsIgnoreCase("")
                || status.equalsIgnoreCase("Closed")
                || status.equalsIgnoreCase("Booked")
                || status.equalsIgnoreCase("Walked-in")
                || status.equalsIgnoreCase("Converted")))) {
            return true;
        } else {
            try {
                final Calendar calendar = Calendar.getInstance();
                String currentDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
                DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
                DateTime currentFormattedDate = dtf.parseDateTime(currentDate);
                DateTime selectedDate = dtf.parseDateTime(formatdate);
                if (selectedDate.isBefore(currentFormattedDate)) {
                    /*dateView.setText("");
                    timeView.setText("");*/
                    formatdate = "";
                    editedDate = "";
                    CommonUtils.showToast(LeadAddActivity.this,
                            "Selected Date is before Current Date", Toast.LENGTH_SHORT);
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year,
                          int month, int day, DateType dateType) {

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        // Toast.makeText(this, "Time set to " + hourOfDay + ", " + minute,
        // Toast.LENGTH_SHORT).show();
        if (minute >= 60) {
            minute = 59;
        }
        hour = hourOfDay;
        min = minute;
        StringBuilder timeStringBuilder = new StringBuilder();
        timeStringBuilder.append((minute < 10) ? ("0" + minute) : minute);
        timeView.setText(hourOfDay + "-" + timeStringBuilder);
        String date = "";
        if (formatdate.equals("")) {
            if (!dateView.getText().toString().trim().equals("")) {
                String fDate = dateView.getText().toString().trim();
                String[] formatdatearr = fDate.split("/");
                date = formatdatearr[2].trim()
                        + "-"
                        + ApplicationController.monthNameMap
                        .get(formatdatearr[1].trim()) + "-"
                        + formatdatearr[0];
                formatdate = date + " " + Integer.toString(hourOfDay) + ":"
                        + timeStringBuilder.toString();
                if (!(status.equalsIgnoreCase("") || status.equalsIgnoreCase("Closed") || status.equalsIgnoreCase("Booked") || status.equalsIgnoreCase("Walked-in") || status.equalsIgnoreCase("Converted")))
                    compareDateAndTime(formatdate);
            } else {
                formatdate = "";
                timeView.setText("");

            }

        } else {

            formatdate = formatdate.split("\\s+")[0] + " "
                    + Integer.toString(hourOfDay) + ":" + min;
            if (!(status.equalsIgnoreCase("") || status.equalsIgnoreCase("Closed") || status.equalsIgnoreCase("Booked") || status.equalsIgnoreCase("Walked-in") || status.equalsIgnoreCase("Converted")))
                compareDateAndTime(formatdate);
        }


    }

    @Override
    public void onValidationSucceeded() {
        if (!dateView.getText().toString().trim().isEmpty()) {
            String dateStr = dateView.getText().toString();
            formatdate = (dateStr.split("/")[2]) + "-" + ApplicationController.monthNameMap.get(dateStr.split("/")[1])
                    + "-" + dateStr.split("/")[0];
            if (!timeView.getText().toString().trim().isEmpty()) {
                String timeStr = timeView.getText().toString();
                if (timeStr.contains("-"))
                    formatdate = formatdate + " " + timeStr.split("-")[0] + ":"
                            + timeStr.split("-")[1];
                else
                    formatdate = formatdate + " " + timeStr;
            }
        }
        if (((selectedFromBudget != null)
                && !selectedFromBudget.getValue().equals(ApplicationController.budgetFrom.get("1500000")))
                && (selectedToBudget == null)) {
            CommonUtils.showToast(LeadAddActivity.this, "Please select budget To", Toast.LENGTH_SHORT);
            findViewById(R.id.addLead).setEnabled(true);
        } else if (!dateView.getText().toString().isEmpty()
                && timeView.getText().toString().isEmpty()) {
            CommonUtils.showToast(LeadAddActivity.this, "Please Select Follow up Time", Toast.LENGTH_SHORT);
            findViewById(R.id.addLead).setEnabled(true);
        } else if (!timeView.getText().toString().isEmpty()
                && dateView.getText().toString().isEmpty()) {
            CommonUtils.showToast(LeadAddActivity.this, "Please Select Follow up Date", Toast.LENGTH_SHORT);
            findViewById(R.id.addLead).setEnabled(true);
        } else if (!formatdate.equals("") && !compareDateAndTime(formatdate)) {

            findViewById(R.id.addLead).setEnabled(true);
        } else {
            //progressBar.show();
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                if (extras.getString(Constants.VIEW_LEAD) != null) {
                    String viewLead = extras.getString(Constants.VIEW_LEAD);
                    if (viewLead.equals(Constants.VALUE_VIEWLEAD)) {

                        if (progressBar != null) {
                            progressBar.show();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                editLead(true);
                            }
                        }, 500);
                    } else {
                        findViewById(R.id.addLead).setEnabled(true);
                    }
                } else {
                    addLead(true);
                }
            } else {
                addLead(true);
            }
        }
    }

    private void editLead(boolean showFullPageError) {

        boolean shouldMakeHit = false;

        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                Constants.CATEGORY_BUYER_LEAD_DETAIL,
                Constants.CATEGORY_BUYER_LEAD_DETAIL,
                Constants.ACTION_TAP,
                Constants.LABEL_UPDATE_LEAD_DATA,
                0);

        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.DEALER_USERNAME, CommonUtils
                .getStringSharedPreference(this, Constants.UC_DEALER_USERNAME,
                        ""));
        params.put(Constants.DEALER_PASSWORD, CommonUtils
                .getStringSharedPreference(this, Constants.UC_DEALER_PASSWORD,
                        ""));
        params.put(Constants.UCDID, String.valueOf(CommonUtils
                .getIntSharedPreference(this, Constants.UC_DEALER_ID, -1)));

        if (!et_fullName.getText().toString().trim().equals(leadDetailModel.getName())) {
            shouldMakeHit = true;
            params.put(Constants.LEAD_NAME, et_fullName.getText().toString().trim());
            leadDetailModel.setName(params.get(Constants.LEAD_NAME));
        }

        params.put(Constants.LEAD_MOBILE, et_mobilenum.getText().toString().trim());

        if (!et_emailId.getText().toString().equals(leadDetailModel.getEmailID())) {
            shouldMakeHit = true;
            params.put(Constants.EMAIL, et_emailId.getText().toString().trim());
            leadDetailModel.setEmailID(params.get(Constants.EMAIL));
        }
        params.put(Constants.LEAD_SOURCE, source);
        params.put(Constants.CAR_ID, leadDetailModel.getCarID());

        if (formatdate.equals("")) {
            if (editedDate.equals("")) {

                params.put(Constants.NEXT_FOLLOW,
                        getFormattedDate(dateView, timeView));

            } else {
                params.put(Constants.NEXT_FOLLOW, editedDate);
            }
        } else {
            params.put(Constants.NEXT_FOLLOW, formatdate);
        }
        if (params.get(Constants.NEXT_FOLLOW).equals(leadDetailModel.getFollowDate())
                || params.get(Constants.NEXT_FOLLOW).isEmpty()) {
            params.remove(Constants.NEXT_FOLLOW);
        } else {
            shouldMakeHit = true;
            leadDetailModel.setFollowDate(params.get(Constants.NEXT_FOLLOW));
        }

        if (!status.equals(leadDetailModel.getLeadStatus())) {
            shouldMakeHit = true;
            params.put(Constants.LEAD_STATUS, status);
            leadDetailModel.setLeadStatus(params.get(Constants.LEAD_STATUS));
        }

        if (selectedFromBudget != null) {
            params.put(Constants.BUDGETFROM, selectedFromBudget.getId());
        } else {
            params.put(Constants.BUDGETFROM, "");
        }
        if (params.get(Constants.BUDGETFROM).equals(leadDetailModel.getBudgetfrom())) {
            params.remove(Constants.BUDGETFROM);
        } else {
            shouldMakeHit = true;
            leadDetailModel.setBudgetfrom(params.get(Constants.BUDGETFROM));
        }

        if (selectedToBudget != null) {
            params.put(Constants.BUDGETTO, selectedToBudget.getId());
        } else {
            params.put(Constants.BUDGETTO, "");
        }
        if (params.get(Constants.BUDGETTO).equals(leadDetailModel.getBudgetto())) {
            params.remove(Constants.BUDGETTO);
        } else {
            shouldMakeHit = true;
            leadDetailModel.setBudgetto(params.get(Constants.BUDGETTO));
        }

        if (makeModelName.equals("")) {
            if (!crosschecked.equals("")) {
                params.put(Constants.ADDMAKEMODEL, leadDetailModel.getMakename() + "##"
                        + leadDetailModel.getModel());
            } else {
                params.put(Constants.ADDMAKEMODEL, "");
            }
        } else {

            params.put(Constants.ADDMAKEMODEL, makeModelName);
        }

        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
        params.put(Constants.METHOD_LABEL, Constants.EDIT_LEAD_API);

        params.put("offline_datetime", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(new DateTime(
                new DateTime().getMillis() + CommonUtils.getLongSharedPreference(this, Constants.SERVER_TIME_DIFFERENCE, 0)
        )));

        if (!comments.getText().toString().isEmpty()) {
            shouldMakeHit = true;
            params.put(Constants.COMMENT, comments.getText().toString().trim());
            if (leadDetailModel.getCommentsArrayList() == null) {
                leadDetailModel.setCommentsArrayList(new ArrayList<Comments>());
            }
            leadDetailModel.getCommentsArrayList().add(new Comments(params.get(Constants.COMMENT), params.get("offline_datetime")));
        }

        if (shouldMakeHit) {

            /*getContentResolver().update(Uri.parse("content://"
                            + Constants.LEADS_CONTENT_AUTHORITY + "/"
                            + LeadsOfflineDB.TABLE_NAME),
                    getContentValues(leadDetailModel, params.get("offline_datetime")),
                    LeadsOfflineDB.NUMBER + " = ?",
                    new String[]{leadDetailModel.getNumber()});*/

            Intent updateIntent = new Intent(this, DatabaseInsertionService.class);
            updateIntent.putExtra(Constants.ACTION, "update");
            updateIntent.putExtra(Constants.PROVIDER_URI, Uri.parse("content://"
                    + Constants.LEADS_CONTENT_AUTHORITY + "/"
                    + LeadsOfflineDB.TABLE_NAME));
            updateIntent.putExtra(Constants.SELECTION, LeadsOfflineDB.NUMBER + " = ?");
            updateIntent.putExtra(Constants.SELECTION_ARGS, new String[]{leadDetailModel.getNumber()});
            updateIntent.putExtra(Constants.CONTENT_VALUES, getContentValues(leadDetailModel, params.get("offline_datetime")));
            startService(updateIntent);

            if (ApplicationController.checkInternetConnectivity()) {
                RetrofitRequest.leadEditRequest(params, new Callback<AddLeadModel>() {
                    @Override
                    public void success(AddLeadModel addLeadModel, retrofit.client.Response response) {
                        if (progressBar != null) {
                            progressBar.dismiss();
                        }

                        if (addLeadModel.getStatus().equals("T")) {
                            CommonUtils.showToast(LeadAddActivity.this,
                                    "Lead Updated Successfully.",
                                    Toast.LENGTH_LONG);

                            if ("CL".equalsIgnoreCase(callSource)) {
                                setResult(100);
                                finish();
                            } else {
                                finish();
                                Intent intent = new Intent(LeadAddActivity.this,
                                        LeadsManageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra(Constants.SELECTED_TAB, selectedTab);
                                startActivity(intent);
                            }

                            ContentResolver.requestSync(GenericAccountService.GetAccount(SyncUtils.ACCOUNT_TYPE),
                                    Constants.LMS_CONTENT_AUTHORITY,
                                    new Bundle());

                            ContentResolver.requestSync(GenericAccountService.GetAccount(SyncUtils.ACCOUNT_TYPE),
                                    Constants.LEADS_CONTENT_AUTHORITY,
                                    new Bundle());
                        } else {
                            findViewById(R.id.addLead).setEnabled(true);

                            CommonUtils.showToast(LeadAddActivity.this,
                                    addLeadModel.getError(), Toast.LENGTH_SHORT);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        findViewById(R.id.addLead).setEnabled(true);
                        if (progressBar != null) {
                            progressBar.dismiss();
                        }

                        if (error.getCause() instanceof UnknownHostException) {
                            CommonUtils.showToast(LeadAddActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                        } else {
                            CommonUtils.showToast(LeadAddActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                        }
                    }
                });

            } else {
                ContentValues updateTableValues = new ContentValues();
                updateTableValues.put(LeadsOfflineDB.COLUMN_JSON, new Gson().toJson(params));

                /*getContentResolver().insert(Uri.parse("content://"
                        + Constants.LEADS_CONTENT_AUTHORITY + "/"
                        + LeadsOfflineDB.TABLE_OFFLINE_UPDATE), updateTableValues);*/

                Intent intent = new Intent(this, DatabaseInsertionService.class);
                intent.putExtra(Constants.ACTION, "insert");
                intent.putExtra(Constants.PROVIDER_URI, Uri.parse("content://"
                        + Constants.LEADS_CONTENT_AUTHORITY + "/"
                        + LeadsOfflineDB.TABLE_OFFLINE_UPDATE));
                intent.putExtra(Constants.CONTENT_VALUES, updateTableValues);
                startService(intent);

                /*intent.putExtra(Constants.ACTION, "update");
                intent.putExtra(Constants.PROVIDER_URI, Uri.parse("content://"
                        + Constants.LEADS_CONTENT_AUTHORITY + "/"
                        + LeadsOfflineDB.TABLE_NAME));
                intent.putExtra(Constants.CONTENT_VALUES,
                        getContentValues(leadDetailModel, params.get("offline_datetime")));
                intent.putExtra(Constants.SELECTION, LeadsOfflineDB.NUMBER + " = ?");
                intent.putExtra(Constants.SELECTION_ARGS, new String[]{leadDetailModel.getNumber()});
                startService(intent);*/

                if ("CL".equalsIgnoreCase(callSource)) {
                    setResult(100);
                } else {
                    intent = new Intent(LeadAddActivity.this,
                            LeadsManageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(Constants.SELECTED_TAB, selectedTab);
                    // intent.putExtra(Constants.MODEL_DATA, response);
                    // intent.putExtra("hashMapKey", params);

                    CommonUtils.showToast(this,
                            "Update Saved. Will be updated once internet is available.", Toast.LENGTH_SHORT);

                    startActivity(intent);
                }
                finish();
            }
        } else {
            CommonUtils.showToast(LeadAddActivity.this,
                    "Lead Updated Successfully.",
                    Toast.LENGTH_LONG);
            finish();
        }

        if (progressBar != null) {
            progressBar.dismiss();
        }
        ///--------------------------------

      /*  EditLeadRequest leadEditRequest = new EditLeadRequest(this,
                Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<AddLeadModel>() {
                    @Override
                    public void onResponse(AddLeadModel response) {
                        if (progressBar != null) {
                            progressBar.dismiss();
                        }

                        if (response.getStatus().equals("T")) {
                            CommonUtils.showToast(LeadAddActivity.this,
                                    "Lead Updated Successfully.",
                                    Toast.LENGTH_LONG);

                            if ("CL".equalsIgnoreCase(callSource)) {
                                setResult(100);
                                finish();
                            } else {
                                finish();
                                Intent intent = new Intent(LeadAddActivity.this,
                                        LeadsManageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra(Constants.SELECTED_TAB, selectedTab);
                                // intent.putExtra(Constants.MODEL_DATA, response);
                                // intent.putExtra("hashMapKey", params);
                                startActivity(intent);
                            }

                            ContentResolver.requestSync(GenericAccountService.GetAccount(SyncUtils.ACCOUNT_TYPE),
                                    Constants.LMS_CONTENT_AUTHORITY,
                                    new Bundle());
                        } else {

                            CommonUtils.showToast(LeadAddActivity.this,
                                    response.getError(), Toast.LENGTH_SHORT);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressBar != null) {
                    progressBar.dismiss();
                }

                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(LeadAddActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                } else {
                    CommonUtils.showToast(LeadAddActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                }

            }
        });

        ApplicationController.getInstance().addToRequestQueue(leadEditRequest,
                Constants.TAG_LEADS_LIST, showFullPageError, this);*/
    }

    private ContentValues getContentValues(LeadDetailModel model, String offline_datetime) {
        ContentValues values = new ContentValues();

        model.setChangeTime(offline_datetime);

        values.put(LeadsOfflineDB.LEAD_TYPE, "Buyer");
        values.put(LeadsOfflineDB.FOLLOW_DATE_ANDROID, String.valueOf(CommonUtils.SQLTimeToMillis(model.getFollowDate())));
        values.put(LeadsOfflineDB.CHANGE_TIME, String.valueOf(CommonUtils.SQLTimeToMillis(model.getChangeTime())));
        values.put(LeadsOfflineDB.NAME, model.getName());
        //values.put(LeadsOfflineDB.NUMBER, model.getNumber());
        values.put(LeadsOfflineDB.BUDGET_FROM, model.getBudgetfrom());
        values.put(LeadsOfflineDB.BUDGET_TO, model.getBudgetto());
        values.put(LeadsOfflineDB.SOURCE, model.getSource());
        values.put(LeadsOfflineDB.LEAD_STATUS, model.getLeadStatus());
        values.put(LeadsOfflineDB.JSON_FORMAT, new Gson().toJson(model));

        return values;
    }

    private void addLead(final boolean showFullPageError) {

        if (progressBar != null) {
            progressBar.show();
        }

        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                Constants.CATEGORY_BUYER_LEAD_DETAIL,
                Constants.CATEGORY_BUYER_LEAD_DETAIL,
                Constants.ACTION_TAP,
                callSource,
                0);

        final HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.DEALER_USERNAME, CommonUtils
                .getStringSharedPreference(this, Constants.UC_DEALER_USERNAME,
                        ""));
        params.put(Constants.DEALER_PASSWORD, CommonUtils
                .getStringSharedPreference(this, Constants.UC_DEALER_PASSWORD,
                        ""));
        params.put(Constants.UCDID, String.valueOf(CommonUtils
                .getIntSharedPreference(this, Constants.UC_DEALER_ID, -1)));
        params.put(Constants.LEAD_NAME, et_fullName.getText().toString().trim());
        params.put(Constants.LEAD_MOBILE, et_mobilenum.getText().toString()
                .trim());
        params.put(Constants.EMAIL, et_emailId.getText().toString()
                .trim());
        params.put(Constants.LEAD_SOURCE, source);

        params.put(Constants.NEXT_FOLLOW, formatdate);

        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.METHOD_LABEL, Constants.ADD_LEAD_API);

        params.put(Constants.LEAD_STATUS, status);
        if (selectedToBudget != null) {
            params.put(Constants.BUDGETTO, selectedToBudget.getId());
        } else {
            params.put(Constants.BUDGETTO, "");
        }

        if (selectedFromBudget != null) {
            params.put(Constants.BUDGETFROM, selectedFromBudget.getId());
        } else {
            params.put(Constants.BUDGETFROM, "");
        }

        params.put(Constants.ADDMAKEMODEL, makeModelName);
        params.put("makeID", makeId);
        params.put("modelID", modelId);

        params.put(Constants.COMMENT, comments.getText().toString().trim());
        params.put(Constants.CALL_SOURCE, callSource);

       RetrofitRequest.leadAddRequest(params, new Callback<AddLeadModel>() {
           @Override
           public void success(AddLeadModel addLeadModel, retrofit.client.Response response) {
               if (progressBar != null) {
                   progressBar.dismiss();
               }

               if (addLeadModel.getStatus().equals("T")) {
                   CommonUtils.showToast(LeadAddActivity.this,
                           "Lead Added Successfully.",
                           Toast.LENGTH_SHORT);

                   ContentResolver.requestSync(GenericAccountService.GetAccount(SyncUtils.ACCOUNT_TYPE),
                           Constants.LEADS_CONTENT_AUTHORITY,
                           new Bundle());

                   Intent intent = new Intent(LeadAddActivity.this,
                           LeadsActivity.class);
                   intent.putExtra(Constants.MODEL_DATA, addLeadModel);
                   intent.putExtra("hashMapKey", params);
                   intent.putExtra(Constants.LEAD_MOBILE, et_mobilenum.getText().toString()
                           .trim());
                   intent.putExtra(Constants.LEAD_NAME, et_fullName.getText().toString()
                           .trim());
                   startActivity(intent);
                   finish();
               } else {
                   findViewById(R.id.addLead).setEnabled(true);

                   CommonUtils.showToast(LeadAddActivity.this,
                           addLeadModel.getError(), Toast.LENGTH_SHORT);
               }
           }

           @Override
           public void failure(RetrofitError error) {
               findViewById(R.id.addLead).setEnabled(true);
               if (progressBar != null) {
                   progressBar.dismiss();
               }

               if (error.getCause() instanceof UnknownHostException) {
                   CommonUtils.showToast(LeadAddActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
               } else {
                   CommonUtils.showToast(LeadAddActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
               }
           }

       });


     /*   LeadAddRequest leadAddRequest = new LeadAddRequest(this, Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<AddLeadModel>() {
                    @Override
                    public void onResponse(AddLeadModel response) {
                        if (progressBar != null) {
                            progressBar.dismiss();
                        }

                        if (response.getStatus().equals("T")) {
                            CommonUtils.showToast(LeadAddActivity.this,
                                    "Lead Added Successfully.",
                                    Toast.LENGTH_SHORT);

                            Intent intent = new Intent(LeadAddActivity.this,
                                    LeadsActivity.class);
                            intent.putExtra(Constants.MODEL_DATA, response);
                            intent.putExtra("hashMapKey", params);
                            intent.putExtra(Constants.LEAD_MOBILE, et_mobilenum.getText().toString()
                                    .trim());
                            intent.putExtra(Constants.LEAD_NAME, et_fullName.getText().toString()
                                    .trim());
                            startActivity(intent);
                            finish();
                        } else {

                            CommonUtils.showToast(LeadAddActivity.this,
                                    response.getError(), Toast.LENGTH_SHORT);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressBar != null) {
                    progressBar.dismiss();
                }

                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(LeadAddActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                } else {
                    CommonUtils.showToast(LeadAddActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                }
            }
        });

        ApplicationController.getInstance().addToRequestQueue(leadAddRequest,
                Constants.TAG_LEADS_LIST, showFullPageError, this);*/
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        if (failedView instanceof EditText) {
            findViewById(R.id.addLead).setEnabled(true);
            ((EditText) failedView).setError(failedRule.getFailureMessage());
            failedView.requestFocus();
            return;
        }

        CommonUtils.showToast(this, failedRule.getFailureMessage(), Toast.LENGTH_SHORT);

        findViewById(R.id.addLead).setEnabled(true);

    }

    @Override
    public void onViewValidationSucceeded(View succeededView) {

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute,
                          TimeType timeType) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCheckedChanged(RadioGroup radiogroup, int checkedId) {

        switch (radiogroup.getId()) {
            case R.id.leadSource:
                if ("ML".equalsIgnoreCase(callSource)) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_BUYER_LEAD_DETAIL,
                            Constants.CATEGORY_BUYER_LEAD_DETAIL,
                            Constants.ACTION_TAP,
                            Constants.LABEL_LEAD_SOURCE,
                            0);
                } else {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_BUYER_LEAD_DETAIL,
                            Constants.CATEGORY_BUYER_LEAD_DETAIL,
                            Constants.ACTION_TAP,
                            Constants.LABEL_LEAD_SOURCE,
                            0);
                }
                radioButton = (RadioButton) findViewById(checkedId);
                source = radioButton.getText().toString().trim();
                break;

            case R.id.leadStatus:
                if (checkedId < 0) {
                    status = "";
                    if (radioButton != null) {
                        radioButton.setOnClickListener(null);
                    }
                    break;
                }
                checkChangeListenerCalled = true;
                radioButton = (RadioButton) findViewById(checkedId);
                if (checkedId == R.id.statusWalkinScheduled) {
                    status = "WalkInScheduled";
                } else {
                    status = radioButton.getText().toString().trim();
                }
                autoSmoothScroll(radioButton);
                radioButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!checkChangeListenerCalled) {
                            status = "";
                            leadStatus.clearCheck();
                        } else {
                            checkChangeListenerCalled = false;
                        }
                    }
                });
                // status = CommonUtils.getLeadStatus(status);
                break;
        }
    }

    @Override
    public void onNoInternetConnection(NetworkEvent networkEvent) {
        if (networkEvent.getNetworkError() == NetworkEvent.NetworkError.NO_INTERNET_CONNECTION) {
            if (progressBar != null) {
                progressBar.dismiss();
            }
            CommonUtils.showToast(LeadAddActivity.this, getString(R.string.network_error),
                    Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (parent.getAdapter() instanceof MakeModelVersionAdapter) {
         /*   Cursor cursor = (Cursor) mMakeModel.getAdapter().getItem(position);

            int rowid = cursor.getInt(cursor
                    .getColumnIndex(MakeModelVersionDB.ID));
            makeId = cursor.getString(cursor
                    .getColumnIndex(MakeModelVersionDB.MAKEID));
            makea = cursor.getString(cursor
                    .getColumnIndex(MakeModelVersionDB.MAKENAME));
            modelId = cursor.getString(cursor
                    .getColumnIndex(MakeModelVersionDB.MODELID));
            modelName = cursor.getString(cursor
                    .getColumnIndex(MakeModelVersionDB.MODELNAME));
            makeModelName = makea + "##" + modelName;
            String makeModel = cursor.getString(cursor
                    .getColumnIndex(MakeModelVersionDB.MMV_COLUMN_MAKE_MODEL));

            // model = new VersionObject();
            // model.setId(rowid);
            // model.setMakeId(makeId);
            // model.setMake(make);
            // model.setModelId(modelId);
            // model.setModel(modelName);
            // model.setMakeModel(makeModel);

            GCLog.e("makeid = " + makeId);

            mMakeModel.setText(modelName);
            mMakeModel.setSelection(mMakeModel.getText().length());
            mMakeModel.setCompoundDrawablesWithIntrinsicBounds(
                    ApplicationController.makeLogoMap.get(Integer
                            .parseInt(makeId)), 0, R.drawable.close_layer_dark,
                    0);*/

            // ApplicationController.getEventBus().post(new
            // MakeModelSelectedEvent(model));

        }
    }

    @Subscribe
    public void onCancelEvent(CancelMakeEvent event) {
        makeModelName = "";
        crosschecked = "";


    }

    // This code segment has been implemented to prevent Sony startActivity issue
    @Subscribe
    public void startLMS(Intent intent) {
        //GCLog.e("startLMS Called " + onResumeCalled);
        if (intent.getExtras() == null
                || !intent.getExtras().containsKey(Constants.CALL_SUCCESS)) {
            return;
        }
        if (onResumeCalled) {
            startActivity(intent);
        } else {
            callIntent = intent;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationController.getEventBus().unregister(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION_CALL) {
            Intent intent;
            if (grantResults.length > 1
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //Uri uri = intent.getData();
                //String uriString = uri.toString();
                //String phoneNumber = uriString.substring(7);
                if (CommonUtils.getIntSharedPreference(this, Constants.IS_LMS, 0) == 1) {
                    CommonUtils.activateLeadCallStateListener(this,
                            LeadFollowUpActivity.class,
                            "+91" + /**/selectedNumberToCall /*/"9560619309"/**/,
                            Constants.VALUE_VIEWLEAD,
                            Constants.NOT_YET_CALLED_FRAG_NO,
                            new LeadData(leadDetailModel));
                }
            }
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //intent.removeExtra("lead_data");
               intent = new Intent(Intent.ACTION_CALL);
            } else {
                intent = new Intent(Intent.ACTION_DIAL);
            }
            intent.setData(Uri.parse("tel:+91" + selectedNumberToCall));
            startActivity(intent);
        }
    }
}
