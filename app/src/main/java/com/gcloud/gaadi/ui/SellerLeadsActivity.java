package com.gcloud.gaadi.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.NumOwnerAdapter;
import com.gcloud.gaadi.adapter.SellerLeadsAdapter;
import com.gcloud.gaadi.adapter.SellerLeadsManagePagerAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.DateType;
import com.gcloud.gaadi.events.FilterEvent;
import com.gcloud.gaadi.events.SearchEvent;
import com.gcloud.gaadi.model.BasicListItemModel;
import com.gcloud.gaadi.model.FollowupDate;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.StockDetailModel;
import com.gcloud.gaadi.model.StocksModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.fourmob.datetimepicker.date.DatePickerDialog;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by ankitgarg on 14/08/15.
 */
public class SellerLeadsActivity extends BaseActivity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, DatePickerDialog.OnDateSetListener {

    //  private ActionBar mActionBar;

    public static final String DATEPICKER_TAG = "datepicker";
    private static final int FILTER_REQUEST_CODE = 101;
    private final String INITIATE_FILTERS = "Initiate Filters";
    TextView textViewBudgetFrom, textViewBudgetTo, dateView;
    EditText editText;
    RadioGroup radioGroupSource;
    RadioGroup radioGroupStatus;
    String formatdate = "";
    private HashMap<String, String> filterParams;
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
    private RelativeLayout notYetCalledTab, todayTab, pastTab, upcomingTab;
    private TextView notYetCalled, today, past, upcoming;
    private DatePickerDialog datePickerDialog;
    private SellerLeadsAdapter adapter = null;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StocksActivity.filterOptionMap = new HashMap<>();
        StocksActivity.filterOptionCount = new HashMap<>();
        StocksActivity.filterParams = new HashMap<>();
        StocksActivity.makeHashMap = new LinkedHashMap<>();

        //getLayoutInflater().inflate(R.layout.activity_seller_leads, frameLayout);
        frameLayout.setVisibility(View.GONE);
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.addTab(tabLayout.newTab().setText("No Action Taken"));
        tabLayout.addTab(tabLayout.newTab().setText("Today's"));
        tabLayout.addTab(tabLayout.newTab().setText("Past"));
        tabLayout.addTab(tabLayout.newTab().setText("Upcoming"));

      /*  mActionBar = getSupportActionBar();
//        mGAHelper = new GAHelper(this);

        mActionBar.setCustomView(R.layout.custom_actionbar_leads);

        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setTitle("Seller Leads");
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
                params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
                params.put(Constants.METHOD_LABEL, Constants.LOG_NOTIFICATION_METHOD);
                params.put(Constants.UC_DEALER_USERNAME, CommonUtils.getStringSharedPreference(getApplicationContext(), Constants.UC_DEALER_USERNAME, ""));


                RetrofitRequest.logNotificationRequest(getApplicationContext(), params, new Callback<GeneralResponse>() {
                    @Override
                    public void success(GeneralResponse generalResponse, retrofit.client.Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
              /*  LogNotificationRequest logNotificationRequest = new LogNotificationRequest(
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

      /*  notYetCalledTab = (RelativeLayout) findViewById(R.id.notYetCalledTab);
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

        viewPager.setVisibility(View.VISIBLE);
        viewPager.setAdapter(new SellerLeadsManagePagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(0);
        //viewPager.setOnPageChangeListener(this);
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
        //   selectTab(0);


        /*indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        indicator.setOnPageChangeListener(this);
        indicator.setViewPager(viewPager);*/

       /* Intent intent = getIntent();
        if (intent != null) {
            int currentTab = intent.getIntExtra(Constants.SELECTED_TAB, 0);

            viewPager.setCurrentItem(currentTab);
            selectTab(currentTab);
        }*/
//        indicator.setTextColor(getResources().getColor(R.color.textColor));
//        indicator.setSelectedColor(getResources().getColor(R.color.textColor));

        /*ImageView addLeadMain = (ImageView) findViewById(R.id.addLeadMain);
        addLeadMain.setOnClickListener(this);*/

    }

    @Subscribe
    public void filterAction(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null && extras.containsKey(Constants.CALL_SOURCE)
                && !"seller".equalsIgnoreCase(extras.getString(Constants.CALL_SOURCE))) {
            return;
        }
        switch (intent.getAction()) {
            case INITIATE_FILTERS:
                makeFilterRequest((StocksModel.Filters) extras.getSerializable("filterResponse"));
                break;
        }
    }

    private void makeFilterRequest(StocksModel.Filters filterResponse) {
        if (StocksActivity.filterOptionMap == null) {
            return;
        }
        if (!StocksActivity.filterOptionMap.containsKey("filters")
                || StocksActivity.filterOptionMap.get("filters") == null) {
            //GCLog.e("filters created");
            StocksActivity.filterOptionMap.put("filters", FilterFragment.getInstance(true,
                    SellerFilterActivity.list));

            LinkedHashMap<String, String> verified = new LinkedHashMap<String, String>() {{
                put("Verified", "1");
            }};
            StocksActivity.filterOptionMap.put("verified", FilterFragment.getInstance(false, verified, true));
            for (StockDetailModel model : filterResponse.getMake()) {
                StocksActivity.makeHashMap.put(model.getMake(), String.valueOf(model.getMakeid()));
            }
            StocksActivity.filterOptionMap.put("make", FilterFragment.getInstance(false, StocksActivity.makeHashMap, false));
            LinkedHashMap<String, String> modelMap = new LinkedHashMap<>();
            for (StockDetailModel model : filterResponse.getModel()) {
                modelMap.put(model.getModel(), String.valueOf(model.getMakeid()));
            }
            StocksActivity.filterOptionMap.put("model", FilterFragment.getInstance(false, modelMap, false));

            LinkedHashMap<String, String> priceMap = new LinkedHashMap<>();
            for (StocksModel.Filters.KeyValueModel model : filterResponse.getPrice()) {
                priceMap.put(model.getValue(), model.getKey());
            }
            StocksActivity.filterOptionMap.put("price", FilterFragment.getInstance(false, priceMap, true));

            StocksActivity.filterOptionMap.put("lead source", FilterFragment.getInstance(false, filterResponse.getLeadSource()));
            StocksActivity.filterOptionMap.put("status", FilterFragment.getInstance(false, filterResponse.getStatus()));

            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(SellerLeadsActivity.this, SellerFilterActivity.class), FILTER_REQUEST_CODE);
                }
            });
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

 /*   @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selectTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }*/


    private void formLists() {
        for (Map.Entry<String, String> entry : ApplicationController.budgetFrom.entrySet()) {
            BasicListItemModel listItem = new BasicListItemModel(entry.getKey(), entry.getValue());
            budgetFromList.add(listItem);
        }
        budgetFromList.trimToSize();
        budgetFromAdapter = new NumOwnerAdapter(this, budgetFromList);


        for (Map.Entry<String, String> entry : ApplicationController.budgetFrom.entrySet()) {
            BasicListItemModel listItem = new BasicListItemModel(entry.getKey(), entry.getValue());
            budgetToList.add(listItem);
        }
        budgetToList.trimToSize();
        budgetToAdapter = new NumOwnerAdapter(this, budgetToList);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.leadSource:
                RadioButton rbSource = (RadioButton) findViewById(checkedId);
                if (rbSource != null) {
                    leadSource = rbSource.getText().toString().trim();
                    filterParams.put(Constants.FILTER_SOURCE, leadSource);
                }
                break;


            case R.id.leadStatus:
                RadioButton rbStatus = (RadioButton) findViewById(checkedId);
                if (rbStatus != null) {
                    leadStatus = rbStatus.getText().toString().trim();
                    filterParams.put(Constants.FILTER_STAUS, leadStatus);
                }
                break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        FollowupDate followupDate = new FollowupDate(year, month, day);
        dateView.setText("Date: " + day + "/" + followupDate.getMonthName()
                + "/" + year);
        formatdate = Integer.toString(year) + "-" + Integer.toString(month + 1)
                + "-" + Integer.toString(day);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day, DateType dateType) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_SELLER_LEADS);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.seller_leads_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                showSearchLeadDialog();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    boolean applyFilter = extras.getBoolean("applyFilter");
                    filterStockList(applyFilter);
                }
            }
        }
    }

    private void filterStockList(boolean applyFilter) {
        count = 0;
        if (filterParams != null) {
            filterParams.clear();
        } else {
            filterParams = new HashMap<>();
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

        if (applyFilter) {
            ApplicationController.getEventBus().post(new FilterEvent(filterParams, 501));
        }
        setFabCounter(count);
    }

    private void showSearchLeadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SellerLeadsActivity.this);
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
                            CommonUtils.showToast(SellerLeadsActivity.this, "Please enter Lead Name or Mobile Number", Toast.LENGTH_SHORT);
                        } else if (leadSearch.equals("Mobile No.") && editText.getText().toString().trim().length() < 10) {
                            CommonUtils.showToast(SellerLeadsActivity.this, "Please enter valid Mobile Number", Toast.LENGTH_SHORT);
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

    private void searchLead(String searchvalue, String searchkey) {
        HashMap<String, String> searchParams = new HashMap<String, String>();
        searchParams.put(Constants.LEAD_SEARCH_KEY, searchkey);
        if (searchkey.equals("Name")) {
          /*  ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                    Constants.CATEGORY_MANAGE_LEAD,
                    Constants.CATEGORY_MANAGE_LEAD,
                    Constants.ACTION_TAP,
                    Constants.LABEL_LEAD_SEARCH_NAME,
                    0);
*/
            searchParams.put(Constants.LEAD_NAME, searchvalue);
        } else if (searchkey.equals("Mobile No.")) {
           /* ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                    Constants.CATEGORY_MANAGE_LEAD,
                    Constants.CATEGORY_MANAGE_LEAD,
                    Constants.ACTION_TAP,
                    Constants.LABEL_LEAD_SEARCH_NUMBER,
                    0);
*/
            searchParams.put(Constants.LEAD_MOBILE, searchvalue);
        }
        GCLog.e(searchvalue + "," + searchkey);

        ApplicationController.getEventBus().post(new SearchEvent(
                searchParams, viewPager.getCurrentItem()));

    }

    @Subscribe
    public void onCallInitiated(SellerLeadsAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_SELLER_LEADS);
        ApplicationController.getEventBus().register(this);
        if (adapter != null)
            adapter.resetCallInitiated();
        adjustLayoutIfNavigationBarExist();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationController.getEventBus().unregister(this);

        StocksActivity.filterOptionMap = null;
        StocksActivity.filterOptionCount = null;
        StocksActivity.filterParams = null;
        StocksActivity.makeHashMap = null;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION_CALL) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = adapter.selectedNumberToCall();
                if (intent != null) {
                    startActivity(intent);
                }
            } else {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(adapter.selectedNumberToCall().getData());
                startActivity(intent);
            }
        }
    }
}
