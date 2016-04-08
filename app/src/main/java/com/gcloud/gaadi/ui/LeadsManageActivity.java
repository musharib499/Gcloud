package com.gcloud.gaadi.ui;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.LeadsAdapter;
import com.gcloud.gaadi.adapter.LeadsManagePagerAdapter;
import com.gcloud.gaadi.adapter.NumOwnerAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.FilterEvent;
import com.gcloud.gaadi.events.SearchEvent;
import com.gcloud.gaadi.model.BasicListItemModel;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.LeadData;
import com.gcloud.gaadi.model.LeadFilterModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.slidingmenu.SlidingMenu;
import com.gcloud.gaadi.ui.fourmob.datetimepicker.date.DatePickerDialog;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ankit on 24/11/14.
 */
// Do not change this class name... else log notification will not work. - Comment by Ankit Garg
public class LeadsManageActivity extends BaseActivity implements
        View.OnClickListener {

    public static final String DATEPICKER_TAG = "datepicker";
    private static final int BUYER_FILTER = 101;
    final private Context mContext = this;
    SlidingMenu menu;
    HashMap<String, String> filterParams;
    TextView textViewBudgetFrom, textViewBudgetTo, dateView;
    EditText editText;
    RadioGroup radioGroupSource;
    RadioGroup radioGroupStatus;
    String formatdate = "";
    // private ActionBar mActionBar;
    private GAHelper mGAHelper;
    private NumOwnerAdapter budgetFromAdapter, budgetToAdapter;
    private ArrayList<BasicListItemModel> budgetFromList = new ArrayList<BasicListItemModel>();
    private ArrayList<BasicListItemModel> budgetToList = new ArrayList<>();
    private BasicListItemModel selectedBudget;
    private String budgetFromID = "";
    private String priceFrom = "";
    private String priceTo = "";
    private String leadStatus = "";
    private String leadSource = "";
    private String leadSearch = "";
    private String budgetToID = "";
    //  private RelativeLayout notYetCalledTab, todayTab, pastTab, upcomingTab;
    private TextView notYetCalled, today, past, upcoming;
    private DatePickerDialog datePickerDialog;
    private LeadsAdapter adapter = null;
    private Intent callIntent = null;
    private boolean onResumeCalled = false, ottoRegistered = false;
    private LeadFilterModel leadFilterModel = new LeadFilterModel();
    private int count = 0;
    //private int FILTER



    @Override
    public void onBackPressed() {
      /*  if (!menu.isMenuShowing())*/
        super.onBackPressed();
      /*  else
            menu.toggle();*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GCLog.e("Call button was pressed");
       // getLayoutInflater().inflate(R.layout.fragment_leads_manage, frameLayout);
        frameLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);

        StocksActivity.filterOptionMap = new HashMap<>();
        StocksActivity.filterParams = new HashMap<>();
        StocksActivity.filterOptionCount = new HashMap<>();

        makeFilterRequest();

        tabLayout.addTab(tabLayout.newTab().setText("No Action Taken"));
        tabLayout.addTab(tabLayout.newTab().setText("Today's"));
        tabLayout.addTab(tabLayout.newTab().setText("Past"));
        tabLayout.addTab(tabLayout.newTab().setText("Upcoming"));
        filterParams = new HashMap<>();


        //   setContentView(R.layout.fragment_leads_manage);
        // mActionBar = getSupportActionBar();
//        mGAHelper = new GAHelper(this);

       /* mActionBar.setCustomView(R.layout.custom_actionbar_leads);

        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        *//*ImageView search = (ImageView) mActionBar.getCustomView().findViewById(R.id.search);
        search.setOnClickListener(this);*//*

        ImageView addLead = (ImageView) mActionBar.getCustomView().findViewById(R.id.addLead);
        addLead.setOnClickListener(this);
        ImageView filter = (ImageView) mActionBar.getCustomView().findViewById(R.id.filter);
        filter.setOnClickListener(this);
        ImageView search = (ImageView) mActionBar.getCustomView().findViewById(R.id.search);
        search.setOnClickListener(this);
        mActionBar.setDisplayHomeAsUpEnabled(true);*/

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            GCLog.e("extras are not null");
            if (extras.containsKey("aID")) {

                // send this to server to track.
                GCLog.e("notification opened: " + extras.toString());

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.GCM_RESPONSE_CODE, "0"); // notification was opened and was destined for this dealer.
                params.put(Constants.SCREEN_NAME, getClass().getSimpleName());
                params.put(Constants.GCM_MESSAGE, extras.toString());
                params.put(Constants.METHOD_LABEL, Constants.LOG_NOTIFICATION_METHOD);

                RetrofitRequest.makeLogNotificationRequest(this, params, new Callback<GeneralResponse>() {
                    @Override
                    public void success(GeneralResponse generalResponse, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

                /*LogNotificationRequest logNotificationRequest = new LogNotificationRequest(
                        this,
                        Request.Method.POST,
                        Constants.getWebServiceURL(this),
                        params,
                        new Response.Listener<GeneralResponse>() {
                            @Override
                            public void onResponse(GeneralResponse response) {

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }
                );

                ApplicationController.getInstance().addToRequestQueue(logNotificationRequest);*/
            }
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);


       /* notYetCalledTab = (RelativeLayout) findViewById(R.id.notYetCalledTab);
        notYetCalledTab.setOnClickListener(this);
        notYetCalled = (TextView) findViewById(R.id.notYetCalled);
        todayTab = (RelativeLayout) findViewById(R.id.todayTab);
        todayTab.setOnClickListener(this);
        today = (TextView) findViewById(R.id.today);
        pastTab = (RelativeLayout) findViewById(R.id.pastTab);
        pastTab.setOnClickListener(this);
        past = (TextView) findViewById(R.id.past);
        upcomingTab = (RelativeLayout) findViewById(R.id.upcomingTab);
        upcomingTab.setOnClickListener(this);
        upcoming = (TextView) findViewById(R.id.upcoming);*/

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new LeadsManagePagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //  selectTab(0);


        /*indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        indicator.setOnPageChangeListener(this);
        indicator.setViewPager(viewPager);*/

      /*  Intent intent = getIntent();
        if (intent != null) {
            int currentTab = intent.getIntExtra(Constants.SELECTED_TAB, 0);

            viewPager.setCurrentItem(currentTab);
            selectTab(currentTab);
        }*/
//        indicator.setTextColor(getResources().getColor(R.color.textColor));
//        indicator.setSelectedColor(getResources().getColor(R.color.textColor));

        /*ImageView addLeadMain = (ImageView) findViewById(R.id.addLeadMain);
        addLeadMain.setOnClickListener(this);*/

     /*    = new SlidingMenu(this);
        menu.setMode(SlidingMenu.RIGHT);
        //menu.setAboveOffset(R.layout.filter_layout);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        //menu.setShadowDrawable(R.drawable.shadow);

        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(R.layout.buyer_lead_filter_activity);
        RelativeLayout toolbarRelaytive = (RelativeLayout) findViewById(R.id.toolbar_relativeLayout);
        toolbarRelaytive.setVisibility(View.VISIBLE);


        //bSearchFilter = (ImageView) findViewById(R.id.ivFilter);
        //  bSearchFilter.setOnClickListener(this);

        findViewById(R.id.resetFilter).setOnClickListener(this);

        textViewBudgetFrom = (TextView) findViewById(R.id.tvBudgetFromFilter);
        textViewBudgetFrom.setOnClickListener(this);

        textViewBudgetTo = (TextView) findViewById(R.id.tvBudgetToFilter);
        textViewBudgetTo.setOnClickListener(this);

        dateView = (TextView) findViewById(R.id.tvEnquiryDateFilter);
        dateView.setOnClickListener(this);

        TextView buttonFilterLeads = (TextView) findViewById(R.id.apply_filter);
        buttonFilterLeads.setOnClickListener(this);

        radioGroupSource = (RadioGroup) findViewById(R.id.leadSource);
        radioGroupSource.setOnCheckedChangeListener(this);

        radioGroupStatus = (RadioGroup) findViewById(R.id.leadStatus);
        radioGroupStatus.setOnCheckedChangeListener(this);

        formLists();
        final Calendar calendar = Calendar.getInstance();
        filterParams = new HashMap<String, String>();
        datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), false);*/
    }

    private void showSearchLeadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LeadsManageActivity.this);
        final View view = this.getLayoutInflater().inflate(R.layout.layout_search_lead_dialog, null, false);
        editText = (EditText) view.findViewById(R.id.editText);
        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.leadSearch);
        leadSearch = "Name";
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rbSearchLead = (RadioButton) view.findViewById(checkedId);
                if (rbSearchLead != null) {
                    editText.setText("");
                    leadSearch = rbSearchLead.getText().toString().trim();
                    if (leadSearch.equals("Mobile No.")) {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                    } else if (leadSearch.equals("Name")) {
                        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                    }
                }
            }
        });

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leadSearch.equals("Mobile No.")) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                } else if (leadSearch.equals("Name")) {
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                }
            }
        });

        builder.setView(view);

        AlertDialog dialog = builder.setTitle(R.string.searchlead)
                .setPositiveButton(R.string.done_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if (editText.getText().toString().trim().length() == 0) {
                            CommonUtils.showToast(LeadsManageActivity.this, "Please enter Lead Name or Mobile Number", Toast.LENGTH_SHORT);
                        } else if (leadSearch.equals("Mobile No.") && editText.getText().toString().trim().length() < 10) {
                            CommonUtils.showToast(LeadsManageActivity.this, "Please enter valid Mobile Number", Toast.LENGTH_SHORT);
                        } else {
                            searchLead(editText.getText().toString().trim(), leadSearch);
                        }

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        if (!this.isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }

    }

  /*  private void selectTab(int i) {
        switch (i) {
            case 0:
                notYetCalledTab.setSelected(true);
                notYetCalled.setTextAppearance(this, R.style.text_normal);

                todayTab.setSelected(false);
                today.setTextAppearance(this, R.style.text_normal);

                pastTab.setSelected(false);
                past.setTextAppearance(this, R.style.text_normal);

                upcomingTab.setSelected(false);
                upcoming.setTextAppearance(this, R.style.text_normal);

                break;

            case 1:
                notYetCalledTab.setSelected(false);
                notYetCalled.setTextAppearance(this, R.style.text_normal);

                todayTab.setSelected(true);
                today.setTextAppearance(this, R.style.text_normal);

                pastTab.setSelected(false);
                past.setTextAppearance(this, R.style.text_normal);

                upcomingTab.setSelected(false);
                upcoming.setTextAppearance(this, R.style.text_normal);
                break;

            case 2:
                notYetCalledTab.setSelected(false);
                notYetCalled.setTextAppearance(this, R.style.text_normal);

                todayTab.setSelected(false);
                today.setTextAppearance(this, R.style.text_normal);

                pastTab.setSelected(true);
                past.setTextAppearance(this, R.style.text_normal);

                upcomingTab.setSelected(false);
                upcoming.setTextAppearance(this, R.style.text_normal);
                break;

            case 3:
                notYetCalledTab.setSelected(false);
                notYetCalled.setTextAppearance(this, R.style.text_normal);

                todayTab.setSelected(false);
                today.setTextAppearance(this, R.style.text_normal);

                pastTab.setSelected(false);
                past.setTextAppearance(this, R.style.text_normal);

                upcomingTab.setSelected(true);
                upcoming.setTextAppearance(this, R.style.text_normal);
                break;
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_BUYER_LEADS);
        GCLog.e("onResume called " + onResumeCalled);
        onResumeCalled = true;
        GCLog.e("onResume called");
        ApplicationController.getEventBus().register(this);
        ottoRegistered = true;
        if (adapter != null)
            adapter.resetCallInitiated();
        if (callIntent != null) {    // Sony Xperia: startActivity can not be called unless context is in Resumed state
            GCLog.e("call intent initiated");
            startActivity(callIntent);
            callIntent = null;
        }
      //  adjustLayoutIfNavigationBarExist();
      /*  boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        boolean hasMenuKey = ViewConfiguration.get(LeadsManageActivity.this).hasPermanentMenuKey();
        if (hasBackKey && hasHomeKey) {
            Log.e("check run", "true");
           *//* Resources resources = getApplicationContext().getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                parentLayout.setPadding(0, 0, 0, getNavigationBarHeight(LeadsManageActivity.this, getApplicationContext().getResources().getConfiguration().orientation));

            }*//*
            if(hasSoftKeys(getWindowManager()))
            {
                parentLayout.setPadding(0, 0, 0, getNavigationBarHeight(LeadsManageActivity.this, getApplicationContext().getResources().getConfiguration().orientation));

            }

            // no navigation bar, unless it is enabled in the settings
        } else {
            // 99% sure there's a navigation bar
            Log.e("check run", "false");

            parentLayout.setPadding(0, 0, 0, getNavigationBarHeight(LeadsManageActivity.this, getApplicationContext().getResources().getConfiguration().orientation));
        }*/
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (!ContentResolver.getMasterSyncAutomatically()) {
            ContentResolver.setMasterSyncAutomatically(true);
            /*Snackbar snackbar = Snackbar.make(findViewById(R.id.parent_layout),
                    "Sync is disabled",
                    Snackbar.LENGTH_LONG)
                    .setAction("Enable", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ContentResolver.setMasterSyncAutomatically(true);
                        }
                    })
                    .setActionTextColor(Color.parseColor("#40FF00"));
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(Color.parseColor("#FACC2E"));
            snackbar.show();*/
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        ApplicationController.getEventBus().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.leads_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*@Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.leads_menu, menu);
    MenuItem searchItem = menu.findItem(R.id.action_search);
    SearchManager searchManager =
        (SearchManager) getSystemService(Context.SEARCH_SERVICE);

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
      searchView.setSearchableInfo(searchManager
          .getSearchableInfo(getComponentName()));
      searchView.setIconifiedByDefault(true);
      searchView.setSubmitButtonEnabled(true);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      setupNewSearchView(searchItem, searchManager);
    }

    return true;

  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private void setupNewSearchView(MenuItem searchItem,
                                  SearchManager searchManager) {
    SearchView searchView =
        (SearchView) MenuItemCompat.getActionView(searchItem);
    searchView.setSearchableInfo(searchManager
        .getSearchableInfo(new ComponentName(getApplicationContext(), LeadsManageActivity.class)));
    searchView.setIconifiedByDefault(true);
    searchView.setSubmitButtonEnabled(true);
  }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_BUYER_LEADS,
                        Constants.CATEGORY_BUYER_LEADS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_BACK_BUTTON,
                        0);
                onBackPressed();
                return true;
            case R.id.action_add_lead:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_BUYER_LEADS,
                        Constants.CATEGORY_BUYER_LEADS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_ADD_LEAD_MANAGE_LEADS,
                        0);
                Intent intent = new Intent(this, LeadAddOptionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.CALL_SOURCE, "ML");
                intent.putExtras(bundle);
                startActivityForResult(intent, Constants.LEAD_ADD_FROM_MANAGE);
                return true;

            case R.id.action_search:
                showSearchLeadDialog();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GCLog.e("onStop called " + onResumeCalled);
        onResumeCalled = false;
    }

  /*  @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        selectTab(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }*/

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {

          /*  case R.id.notYetCalledTab:
                selectTab(0);
                viewPager.setCurrentItem(0);
                break;

            case R.id.todayTab:
                selectTab(1);
                viewPager.setCurrentItem(1);
                break;

            case R.id.pastTab:
                selectTab(2);
                viewPager.setCurrentItem(2);
                break;

            case R.id.upcomingTab:
                selectTab(3);
                viewPager.setCurrentItem(3);
                break;*/
            case R.id.fab:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_BUYER_LEADS,
                        Constants.CATEGORY_BUYER_LEADS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_FILTER,
                        0);
                startActivityForResult(new Intent(this, BuyerFilterActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT),
                        BUYER_FILTER);
                //menu.toggle();
                break;
            /*case R.id.addLeadMain:
                intent = new Intent(this, LeadAddOptionActivity.class);
                startActivityForResult(intent, Constants.LEAD_ADD_FROM_MANAGE);
                break;*/
            /*case R.id.search:
                break;*/

         /*   case R.id.addLead:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_BUYER_LEADS,
                        Constants.CATEGORY_BUYER_LEADS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_ADD_LEAD_MANAGE_LEADS,
                        0);
                intent = new Intent(this, LeadAddOptionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.CALL_SOURCE, "ML");
                intent.putExtras(bundle);
                startActivityForResult(intent, Constants.LEAD_ADD_FROM_MANAGE);
                break;

            case R.id.filter:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_BUYER_LEADS,
                        Constants.CATEGORY_BUYER_LEADS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_FILTER,
                        0);
                menu.toggle();
                break;
            case R.id.search:
                showSearchLeadDialog();
            break;*/
           /* case R.id.tvBudgetFromFilter:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_BUYER_LEADS,
                        Constants.CATEGORY_BUYER_LEADS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_FILTER_BUDGET,
                        0);
                final ListPopupWindow budgetWindow = new ListPopupWindow(this);
                budgetWindow.setAdapter(budgetFromAdapter);
                budgetWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
                budgetWindow.setModal(true);
                budgetWindow.setAnchorView(findViewById(R.id.tvBudgetFromFilter));
                budgetWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
                textViewBudgetFrom.post(new Runnable() {
                    @Override
                    public void run() {
                        budgetWindow.show();
                    }
                });
                budgetWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedBudget = (BasicListItemModel) parent.getAdapter().getItem(position);
                        textViewBudgetFrom.setText(selectedBudget.getValue());
                        budgetFromID = selectedBudget.getId();
                        priceFrom = budgetFromID;
                        GCLog.e(priceFrom);
                        budgetWindow.dismiss();
                    }
                });
                break;*/

            /*case R.id.tvBudgetToFilter:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_BUYER_LEADS,
                        Constants.CATEGORY_BUYER_LEADS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_FILTER_BUDGET,
                        0);
                final ListPopupWindow budgetWindow1 = new ListPopupWindow(this);
                budgetWindow1.setAdapter(budgetToAdapter);
                budgetWindow1.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
                budgetWindow1.setModal(true);
                budgetWindow1.setAnchorView(findViewById(R.id.tvBudgetToFilter));
                budgetWindow1.setWidth(ListPopupWindow.WRAP_CONTENT);
                textViewBudgetTo.post(new Runnable() {
                    @Override
                    public void run() {
                        budgetWindow1.show();
                    }
                });
                budgetWindow1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedBudget = (BasicListItemModel) parent.getAdapter().getItem(position);
                        textViewBudgetTo.setText(selectedBudget.getValue());
                        budgetToID = selectedBudget.getId();
                        priceTo = budgetToID;
                        GCLog.e(priceTo);
                        budgetWindow1.dismiss();
                    }
                });
                break;*/

          /*  case R.id.apply_filter:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_BUYER_LEADS,
                        Constants.CATEGORY_BUYER_LEADS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_FILTER_BUTTON,
                        0);

               // hideKeyboard();
                //  filterLeads();
                break;*/

            case R.id.tvEnquiryDateFilter:
                if (!datePickerDialog.isVisible()) {
                    showDatePickerDialog();
                }
                break;

           /* case R.id.resetFilter:
               // resetFilters();
                break;*/
        }
    }

    private void showDatePickerDialog() {
        datePickerDialog.setYearRange(
                Calendar.getInstance().get(Calendar.YEAR), Calendar
                        .getInstance().get(Calendar.YEAR) + 1);
        datePickerDialog.setCloseOnSingleTapDay(false);
        // datePickerDialog.setOnDateSetListener(this);
        datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);

    }

    private void searchLead(String searchvalue, String searchkey) {
        HashMap<String, String> searchParams = new HashMap<String, String>();
        searchParams.put(Constants.LEAD_SEARCH_KEY, searchkey);
        if (searchkey.equals("Name")) {
            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                    Constants.CATEGORY_BUYER_LEADS,
                    Constants.CATEGORY_BUYER_LEADS,
                    Constants.ACTION_TAP,
                    Constants.LABEL_LEAD_SEARCH_NAME,
                    0);

            searchParams.put(Constants.LEAD_NAME, searchvalue);
        } else if (searchkey.equals("Mobile No.")) {
            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                    Constants.CATEGORY_BUYER_LEADS,
                    Constants.CATEGORY_BUYER_LEADS,
                    Constants.ACTION_TAP,
                    Constants.LABEL_LEAD_SEARCH_NUMBER,
                    0);

            searchParams.put(Constants.LEAD_MOBILE, searchvalue);
        }
        GCLog.e(searchvalue + "," + searchkey);

        ApplicationController.getEventBus().post(new SearchEvent(
                searchParams, viewPager.getCurrentItem()));

    }

    private void makeFilterRequest() {

        //TODO The static filters need to be changed, as they get GCed and this results in a crash.
        if (StocksActivity.filterOptionMap == null) {
            return;
        }

        LinkedHashMap<String, String> budgets = new LinkedHashMap<String, String>() {
            {
                put("Below 1 Lac", "0-100000");
                put("1 - 2 Lacs", "100000-200000");
                put("2 - 3 Lacs", "200000-300000");
                put("3 - 4 Lacs", "300000-400000");
                put("4 - 5 Lacs", "400000-500000");
                put("5 - 8 Lacs", "500000-800000");
                put("8 - 12 Lacs", "800000-1200000");
                put("12 - 15 Lacs", "1200000-1500000");
                put("15 Lacs and Above", "1500000-500000000");
            }
        };
        String[] leadStatus = {"Hot", "Warm", "Cold", "Walk-in Scheduled", "Walked-in", "Booked", "Converted", "Closed"};
        String[] leadSource = {"Walk-in", "Website", "Gaadi", "CarDekho", "Zigwheels", "Cartrade", "Carwale", "OLX", "Quikr"};
        LinkedHashMap<String, String> verified = new LinkedHashMap<String, String>() {
            {
                put("Verified", "1");
            }
        };

        if (!StocksActivity.filterOptionMap.containsKey("filters")
                || StocksActivity.filterOptionMap.get("filters") == null) {
            //GCLog.e("filters created");
            StocksActivity.filterOptionMap.put("filters", FilterFragment.getInstance(true,
                    BuyerFilterActivity.list));

            StocksActivity.filterOptionMap.put("verified", FilterFragment.getInstance(false,
                    verified, true));

            StocksActivity.filterOptionMap.put("budget", FilterFragment.getInstance(false, budgets, true));

            StocksActivity.filterOptionMap.put("status", FilterFragment.getInstance(false,
                    new ArrayList<>(Arrays.asList(leadStatus))));

            StocksActivity.filterOptionMap.put("source", FilterFragment.getInstance(false,
                    new ArrayList<>(Arrays.asList(leadSource))));

            fab.setVisibility(View.VISIBLE);
        }
    }

    private Boolean filterLeads(boolean applyFilter) {
        count = 0;

        if(!filterParams.isEmpty()) {
            filterParams.clear();
        }

        for (Map.Entry entry : StocksActivity.filterParams.entrySet()) {
            if (((ArrayList<String>) entry.getValue()).size() > 0) {
                count++;
                StringBuilder values = new StringBuilder();
                for (String value : (ArrayList<String>) entry.getValue()) {
                    if (values.length() > 0) {
                        values.append(",");
                    }
                    values.append(value);
                }
                filterParams.put((String) entry.getKey(), values.toString());
            }
        }

        /*if (leadFilterModel.getBudgetfrom().trim().length() > 0) {
            filterParams.put(Constants.FILTER_PRICE, leadFilterModel.getBudgetfrom());
        }


        if (leadFilterModel.getBudgetto().trim().length() > 0) {
            filterParams.put(Constants.FILTER_PRICE_TO, leadFilterModel.getBudgetto());
        }

        filterParams.put(Constants.FILTER_SOURCE, leadFilterModel.getSource());
        filterParams.put(Constants.FILTER_STAUS, leadFilterModel.getLeadStatus());*/

        getBudgetFilters(filterParams.get("budget"));

        count = filterParams.size();
        if (filterParams.containsKey(Constants.FILTER_PRICE)
                && filterParams.containsKey(Constants.FILTER_PRICE_TO)
                && count > 0) {
            count--;
        }
        if ("".equals(filterParams.get(Constants.FILTER_SOURCE)) && count > 0) {
            count--;
        }
        if ("".equals(filterParams.get(Constants.FILTER_STAUS)) && count > 0) {
            count--;
        }

        if (applyFilter) {
            ApplicationController.getEventBus().post(new FilterEvent(filterParams, 1));
        }

        setFabCounter(count);
        return true;
    }

    private void getBudgetFilters(String budget) {
        if (filterParams.containsKey("budget")) {
            filterParams.remove("budget");
        }
        if (filterParams.containsKey(Constants.FILTER_PRICE)) {
            filterParams.remove(Constants.FILTER_PRICE);
        }
        if (filterParams.containsKey(Constants.FILTER_PRICE_TO)) {
            filterParams.remove(Constants.FILTER_PRICE_TO);
        }
        if (budget == null || budget.isEmpty()) {
            return;
        }
        String[] budgets = budget.split(",");
        ArrayList<Float> minList = new ArrayList<>(), maxList = new ArrayList<>();
        for (String string : budgets) {
            String[] values = string.split("-");
            minList.add(Float.parseFloat(values[0]));
            maxList.add(Float.parseFloat(values[1]));
        }
        Collections.sort(minList);
        filterParams.put(Constants.FILTER_PRICE, String.valueOf(minList.get(0).intValue()));
        if (maxList.size() > 0) {
            Collections.sort(maxList);
            filterParams.put(Constants.FILTER_PRICE_TO, String.valueOf(maxList.get(maxList.size() - 1).intValue()));
        }
    }

    @Subscribe
    public void onCallInitiated(LeadsAdapter adapter) {
        this.adapter = adapter;
    }

    // This code segment has been implemented to prevent Sony startActivity issue
    @Subscribe
    public void startLMS(Intent intent) {
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

    @Subscribe
    public void unregisterOtto(String unregister) {
        if (ottoRegistered) {
            ApplicationController.getEventBus().unregister(this);
            ottoRegistered = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        StocksActivity.filterOptionMap = null;
        StocksActivity.filterParams = null;
        StocksActivity.filterOptionCount = null;

        if (ottoRegistered) {
            ApplicationController.getEventBus().unregister(this);
            ottoRegistered = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION_CALL) {
            Intent intent = adapter.selectedNumberToCall();
            if (grantResults.length > 1
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Uri uri = intent.getData();
                String uriString = uri.toString();
                String phoneNumber = uriString.substring(7);
                if (CommonUtils.getIntSharedPreference(mContext, Constants.IS_LMS, 0) == 1) {
                    CommonUtils.activateLeadCallStateListener(mContext,
                            LeadFollowUpActivity.class,
                            "+91" + /**/phoneNumber /*/"9560619309"/**/,
                            Constants.VALUE_VIEWLEAD,
                            Constants.NOT_YET_CALLED_FRAG_NO,
                            (LeadData) intent.getExtras().getSerializable("lead_data"));
                }
            }
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                intent.removeExtra("lead_data");
                startActivity(intent);
            } else {
                Intent intent1 = new Intent(Intent.ACTION_DIAL);
                intent1.setData(intent.getData());
                startActivity(intent1);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == BUYER_FILTER) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    boolean applyFilter = extras.getBoolean("applyFilter");
                    filterLeads(applyFilter);
                }
            }
        }

    }
}
