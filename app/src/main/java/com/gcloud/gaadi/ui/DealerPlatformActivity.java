package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.answers.CustomEvent;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.adapter.DealerPlatformAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.FilterEvent;
import com.gcloud.gaadi.events.SetTabTextEvent;
import com.gcloud.gaadi.model.StockDetailModel;
import com.gcloud.gaadi.model.StocksModel;
import com.gcloud.gaadi.ui.viewpagerindicator.BaseActivityCollapsingToolbar;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.gcloud.gaadi.utils.GShareToUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by ankit on 24/11/14.
 */
public class DealerPlatformActivity extends BaseActivityCollapsingToolbar {


    public static final String INITIATE_FILTERS = "initiateFilters";
    public static final int FROM_DEALER_PLATFORM = 101;
    private static final int DEALER_FILTER = 101;
    CustomEvent customEvent;
    private boolean isFilterReady = false;
    //    private GAHelper mGAHelper;
    private NoSwipeViewPager mPager;
    private RelativeLayout searchTab, activeTab, inactiveTab;
    private TextView search, active, inactive;
    private GShareToUtil mGShareToUtil;
    private DealerPlatformAdapter adapter;
    private int count = 0;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.STOCKS_CONTACT_LIST) {
            if (resultCode == Activity.RESULT_OK) {
                GCLog.e("gaurav onactivityresult");
                adapter.getRegisteredFragment(1).onActivityResult(requestCode, resultCode, data);
                /*Uri contactUri = data.getData();
                Cursor cursor = getContentResolver().query(contactUri,
                        new String[]{ContactsContract.Contacts._ID},
                        null, null, null);
                cursor.moveToFirst();

                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                if (phones.getCount() >= 1) {
                    while (phones.moveToNext()) {

                        String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        try {
                            this.mGShareToUtil.sendSMSHelp(number);
                        } catch (Exception e) {
                            CommonUtils.showToast(this, "Could not send SMS. Invalid number.", Toast.LENGTH_SHORT);
                        }
                    }
                } else {
                    CommonUtils.showToast(this, "Could not send SMS. Invalid number", Toast.LENGTH_SHORT);
                }
                phones.close();
                cursor.close();*/
            }
        } else if (requestCode == DEALER_FILTER) {
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
        HashMap<String, String> params = new HashMap<>();
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
                params.put((String) entry.getKey(), values.toString());
            }
        }

        if ((StocksActivity.filterParams.get("reg no") != null && StocksActivity.filterParams.get("reg no").get(0).isEmpty()) && count > 0) {
            count--;
        }
        if (applyFilter) {
            ApplicationController.getEventBus().post(new FilterEvent(params, FROM_DEALER_PLATFORM));
        }
        setFabCounter(count);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        ApplicationController.getEventBus().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationController.getEventBus().unregister(this);

        StocksActivity.filterOptionMap = null;
        StocksActivity.makeHashMap = null;
        StocksActivity.filterParams = null;
        StocksActivity.filterOptionCount = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // this.getLayoutInflater().inflate(R.layout.activity_dealer_platform,frameLayout);
        //this.mGAHelper = new GAHelper(this);
        //this.mActionBar = this.getSupportActionBar();
       /* this.mGShareToUtil = new GShareToUtil(this);
        this.searchTab = (RelativeLayout) this.findViewById(R.id.searchTab);
        this.searchTab.setOnClickListener(this);
        this.search = (TextView) this.findViewById(R.id.search);
        customEvent = new CustomEvent("D2D Platform launch");

        Answers.getInstance().logCustom(customEvent);
        this.activeTab = (RelativeLayout) this.findViewById(R.id.activeTab);
        this.activeTab.setOnClickListener(this);
        this.active = (TextView) this.findViewById(R.id.active);

        this.inactiveTab = (RelativeLayout) this.findViewById(R.id.inactiveTab);
        this.inactiveTab.setOnClickListener(this);
        this.inactive = (TextView) this.findViewById(R.id.inactive);

        this.mPager = (NoSwipeViewPager) this.findViewById(R.id.viewPager);
        adapter = new DealerPlatformAdapter(this.getSupportFragmentManager());
        this.mPager.setAdapter(adapter);
        this.mPager.setCurrentItem(1);
        this.mPager.setOnPageChangeListener(this);
        this.mPager.setOffscreenPageLimit(2);
        this.setCurrentTab(1);*/

        StocksActivity.filterOptionMap = new HashMap<>();
        StocksActivity.makeHashMap = new LinkedHashMap<>();
        StocksActivity.filterParams = new HashMap<>();
        StocksActivity.filterOptionCount = new HashMap<>();

        setTabLayout();
    }

    public void setTabLayout() {
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addTab(tabLayout.newTab().setText("Search Cars"));
        tabLayout.addTab(tabLayout.newTab().setText("Active"));
        tabLayout.addTab(tabLayout.newTab().setText("Inactive"));
        adapter = new DealerPlatformAdapter(this.getSupportFragmentManager());
        // stocksPagerAdapter = new StocksPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(DealerPlatformActivity.this, DealerPlatformFilterActivity.class),
                        DEALER_FILTER);
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    fab.setVisibility(View.GONE);
                    setFabCounter(0);
                } else {
                    if (isFilterReady) {
                        fab.setVisibility(View.VISIBLE);
                        setFabCounter(count);
                    }
                }
                viewPager.setCurrentItem(tab.getPosition());
                CommonUtils.hideKeyboard(DealerPlatformActivity.this, viewPager);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    /*private void setCurrentTab(int i) {
        switch (i) {
            case 0:
                customEvent.putCustomAttribute("Search Tab", "Tapped");
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.ACTION_TAP,
                        Constants.LABEL_DEALER_SEARCH,
                        0);
                searchTab.setSelected(true);
                search.setTextAppearance(this, R.style.text_bold);

                activeTab.setSelected(false);
                active.setTextAppearance(this, R.style.text_normal);

                inactiveTab.setSelected(false);
                inactive.setTextAppearance(this, R.style.text_normal);
                break;

            case 1:
                customEvent.putCustomAttribute("Active Inventories Tab", "Tapped");
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.ACTION_TAP,
                        Constants.LABEL_DEALER_ACTIVE,
                        0);
                searchTab.setSelected(false);
                search.setTextAppearance(this, R.style.text_normal);

                activeTab.setSelected(true);
                active.setTextAppearance(this, R.style.text_bold);

                inactiveTab.setSelected(false);
                inactive.setTextAppearance(this, R.style.text_normal);
                break;

            case 2:
                customEvent.putCustomAttribute("Inactive Inventories Tab", "Tapped");
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.ACTION_TAP,
                        Constants.LABEL_DEALER_INACTIVE,
                        0);
                searchTab.setSelected(false);
                search.setTextAppearance(this, R.style.text_normal);

                activeTab.setSelected(false);
                active.setTextAppearance(this, R.style.text_normal);

                inactiveTab.setSelected(true);
                inactive.setTextAppearance(this, R.style.text_bold);
                break;
        }
    }
*/
    @Override
    protected void onPause() {
        super.onPause();
        //ApplicationController.getEventBus().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.ACTION_TAP,
                        Constants.LABEL_BACK_BUTTON,
                        0);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ApplicationController.getEventBus().register(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_DEALER_PLATFORM);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Subscribe
    public void setTabText(SetTabTextEvent event) {
        if (event.getCurrentItem() == Constants.ACTIVE_DEALER_PLATFORM)
            tabLayout.getTabAt(1).setText(event.getText());
        else if (event.getCurrentItem() == Constants.INACTIVE_DEALER_PLATFORM)
            tabLayout.getTabAt(2).setText(event.getText());
    }

    @Subscribe
    public void filterAction(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null && extras.containsKey(Constants.CALL_SOURCE) 
                && !"dealerPlatform".equalsIgnoreCase(extras.getString(Constants.CALL_SOURCE))) {
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
                    DealerPlatformFilterActivity.list));
            StocksActivity.filterOptionMap.put("reg no", FilterFragment.getInstance(true, "Search car by Reg no"));
            for (StockDetailModel model : filterResponse.getMake()) {
                StocksActivity.makeHashMap.put(model.getMake(), String.valueOf(model.getMakeid()));
            }
            StocksActivity.filterOptionMap.put("make", FilterFragment.getInstance(false, StocksActivity.makeHashMap, false));
            LinkedHashMap<String, String> modelMap = new LinkedHashMap<>();
            for (StockDetailModel model : filterResponse.getModel()) {
                modelMap.put(model.getModel(), String.valueOf(model.getMakeid()));
            }
            StocksActivity.filterOptionMap.put("model", FilterFragment.getInstance(false, modelMap, false));

            StocksActivity.filterOptionMap.put("fuel type", FilterFragment.getInstance(false, filterResponse.getFuelType()));
            StocksActivity.filterOptionMap.put("year", FilterFragment.getInstance(false, filterResponse.getYear()));

            LinkedHashMap<String, String> priceMap = new LinkedHashMap<>();
            for (StocksModel.Filters.KeyValueModel model : filterResponse.getPrice()) {
                priceMap.put(model.getValue(), model.getKey());
            }
            StocksActivity.filterOptionMap.put("price", FilterFragment.getInstance(false, priceMap, true));
            LinkedHashMap<String, String> kmMap = new LinkedHashMap<>();
            for (StocksModel.Filters.KeyValueModel model : filterResponse.getKm()) {
                kmMap.put(model.getValue(), model.getKey());
            }
            StocksActivity.filterOptionMap.put("km", FilterFragment.getInstance(false, kmMap, true));

            if (viewPager.getCurrentItem() != 0) {
                fab.setVisibility(View.VISIBLE);
            }
            isFilterReady = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Fragment fragment = adapter.getRegisteredFragment(0);
        if (fragment != null && fragment instanceof DealerActiveInventoryFragment) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

   /* @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchTab:
                customEvent.putCustomAttribute("Search Tab", "Tapped");
                setCurrentTab(0);
                mPager.setCurrentItem(0);
                break;

            case R.id.activeTab:
                setCurrentTab(1);
                mPager.setCurrentItem(1);
                break;

            case R.id.inactiveTab:
                setCurrentTab(2);
                mPager.setCurrentItem(2);
                break;
        }
    }*/


/*
    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        setCurrentTab(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Subscribe
    public void onItemCount(Intent intent) {
        if (intent.getExtras() != null) {
            String view =  intent.getExtras().getString("view");
            int count = intent.getExtras().getInt("count");

            switch (view) {
                case "inactive":
                    inactive.setText("Inactive ("+ count +")");
                    break;

                case "active":
                    active.setText("Active ("+ count +")");
                    break;
            }
        }
    }*/
}
