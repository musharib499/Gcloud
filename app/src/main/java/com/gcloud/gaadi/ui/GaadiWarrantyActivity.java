package com.gcloud.gaadi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.adapter.WarrantyAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.FilterEvent;
import com.gcloud.gaadi.events.SetTabTextEvent;
import com.gcloud.gaadi.model.StockDetailModel;
import com.gcloud.gaadi.model.StocksModel;
import com.gcloud.gaadi.ui.viewpagerindicator.BaseActivityCollapsingToolbar;
import com.gcloud.gaadi.utils.GAHelper;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by Seema Pandit on 14-01-2015.
 */
public class GaadiWarrantyActivity extends BaseActivityCollapsingToolbar {
    public static final String INITIATE_FILTERS = "initiateFilters";
    public static final int FROM_DEALER_PLATFORM = 102;
    private static final int FILTER_APPLIED = 101;
    private HashMap<String, String> requestParamsMap = new HashMap<String, String>(){{
        put("warranty status", "warrantyStatus");
        put("warranty type", "warrantyType");
        put("reg no", "searchKey");
        put("make", "make");
        put("model", "model");
    }};
    //  private ActionBar mActionBar;
//    private GAHelper mGAHelper;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_APPLIED) {
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
        HashMap<String, String> params = new HashMap<>();
        int count = 0;
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
                params.put(requestParamsMap.get(entry.getKey()), values.toString());
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setContentView(R.layout.fragment_warranty);

//        mGAHelper = new GAHelper(this);


       /* certifiedCarsLayout = (RelativeLayout) findViewById(R.id.certifiedCarsTab);
        certifiedCarsLayout.setOnClickListener(this);
        issuedWarrantyLayout = (RelativeLayout) findViewById(R.id.issuedWarrantyTab);
        issuedWarrantyLayout.setOnClickListener(this);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new WarrantyAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(1);
        viewPager.setOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
        certifiedCarsLayout.setSelected(true);*/

        StocksActivity.filterOptionMap = new HashMap<>();
        StocksActivity.filterOptionCount = new HashMap<>();
        StocksActivity.filterParams = new HashMap<>();
        StocksActivity.makeHashMap = new LinkedHashMap<>();

        setTabLayout();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GaadiWarrantyActivity.this, WarrantyFilter.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, FILTER_APPLIED);
            }
        });
    }


    public void setTabLayout() {
        setTitleMsg("Warranty");


        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addTab(tabLayout.newTab().setText("Certified Cars"));
        tabLayout.addTab(tabLayout.newTab().setText("Issued Warranty"));

        // stocksPagerAdapter = new StocksPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(new WarrantyAdapter(getSupportFragmentManager()));

        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        fab.setVisibility(View.GONE);

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER,
                Constants.CATEGORY_WARRANTY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_WARRANTY,
                        Constants.CATEGORY_WARRANTY,
                        Constants.ACTION_TAP,
                        Constants.LABEL_BACK_BUTTON,
                        0);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Subscribe
    public void setTabText(SetTabTextEvent event) {
        if (event.getCurrentItem() == Constants.CERTIFIED_CARS)
            tabLayout.getTabAt(0).setText(event.getText());
        else if (event.getCurrentItem() == Constants.ISSUED_WARRANTY)
            tabLayout.getTabAt(1).setText(event.getText());
    }

/*    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        GCLog.e("current selected tab: " + i);
        handleIndicatorVisibility(i);
    }*/

  /*  private void handleIndicatorVisibility(int currentItem) {
        switch (currentItem) {
            case 0:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_WARRANTY,
                        Constants.CATEGORY_WARRANTY,
                        Constants.ACTION_TAP,
                        Constants.LABEL_CERTIFIED_CARS,
                        0);
                certifiedCarsLayout.setSelected(true);
                ((TextView) findViewById(R.id.certifedCars)).setTextAppearance(this, R.style.text_bold);
                issuedWarrantyLayout.setSelected(false);
                ((TextView) findViewById(R.id.issuedWarranty)).setTextAppearance(this, R.style.text_normal);
                break;

            case 1:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_WARRANTY,
                        Constants.CATEGORY_WARRANTY,
                        Constants.ACTION_TAP,
                        Constants.LABEL_ISSUED_WARRANTY,
                        0);
                certifiedCarsLayout.setSelected(false);
                ((TextView) findViewById(R.id.certifedCars)).setTextAppearance(this, R.style.text_normal);
                issuedWarrantyLayout.setSelected(true);
                ((TextView) findViewById(R.id.issuedWarranty)).setTextAppearance(this, R.style.text_bold);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.certifiedCarsTab:
                handleIndicatorVisibility(0);
                viewPager.setCurrentItem(0);
                break;

            case R.id.issuedWarrantyTab:
                handleIndicatorVisibility(1);
                viewPager.setCurrentItem(1);
                break;
        }
    }*/

    @Subscribe
    public void filterAction(Intent intent) {
        Bundle extras = intent.getExtras();
        int count = 0;
        if (extras != null && extras.containsKey(Constants.CALL_SOURCE)
                && !"warrantyFilters".equalsIgnoreCase(extras.getString(Constants.CALL_SOURCE))) {
            return;
        }
        switch (intent.getAction()) {
            case INITIATE_FILTERS:
                makeFilterRequest((StocksModel.Filters) extras.getSerializable("filters"));
                break;
        }
    }

    private void makeFilterRequest(StocksModel.Filters filterResponse) {

        //TODO The static filters need to be changed, as they get GCed and this results in a crash.
        if (StocksActivity.filterOptionMap == null) {
            return;
        }

        if (!StocksActivity.filterOptionMap.containsKey("filters")
                || StocksActivity.filterOptionMap.get("filters") == null) {
            //GCLog.e("filters created");
            StocksActivity.filterOptionMap.put("filters", FilterFragment.getInstance(true,
                    WarrantyFilter.list));
            StocksActivity.filterOptionMap.put("reg no", FilterFragment.getInstance(true, "By Reg No/ Warranty ID"));
            for (StockDetailModel model : filterResponse.getMake()) {
                StocksActivity.makeHashMap.put(model.getMake(), String.valueOf(model.getMakeid()));
            }
            StocksActivity.filterOptionMap.put("make", FilterFragment.getInstance(false, StocksActivity.makeHashMap, false));

            LinkedHashMap<String, String> modelMap = new LinkedHashMap<>();
            for (StockDetailModel model : filterResponse.getModel()) {
                modelMap.put(model.getModel(), String.valueOf(model.getMake()));
            }
            StocksActivity.filterOptionMap.put("model", FilterFragment.getInstance(false, modelMap, false));

            LinkedHashMap<String, String> warrantyStatusMap = new LinkedHashMap<>();
            for (StocksModel.Filters.KeyValueModel model : filterResponse.getWarrantyStatus()) {
                warrantyStatusMap.put(model.getValue(), model.getKey());
            }
            StocksActivity.filterOptionMap.put("warranty status", FilterFragment.getInstance(false, warrantyStatusMap, true));

            LinkedHashMap<String, String> warrantyTypeMap = new LinkedHashMap<>();
            for (StocksModel.WarrantyType model : filterResponse.getWarrantyType()) {
                warrantyTypeMap.put(model.getPackName(), model.getPackID());
            }
            StocksActivity.filterOptionMap.put("warranty type", FilterFragment.getInstance(false, warrantyTypeMap, true));

            fab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationController.getEventBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ApplicationController.getEventBus().unregister(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Fragment fragment = ((WarrantyAdapter) viewPager.getAdapter()).getRegisteredFragment(1);
        if (fragment != null && fragment instanceof IssuedWarrantyFragment) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        StocksActivity.filterOptionMap = null;
        StocksActivity.filterOptionCount = null;
        StocksActivity.filterParams = null;
        StocksActivity.makeHashMap = null;
    }
}
