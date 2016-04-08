package com.gcloud.gaadi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;

public class DealerPlatformFilterActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String CHANGE_LIST_ITEM = "changeListItem";
    final public static ArrayList<String> list = new ArrayList<>(
            Arrays.asList(new String[]{"Reg No", "Make", "Model", "Fuel Type", "Year", "Price", "Km"}));
    private NoSwipeViewPager filterOptionViewPager;
    private boolean applyFilterClicked = false;
    private String currentKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_layout);

        findViewById(R.id.filter_actionbar_back).setOnClickListener(this);
        findViewById(R.id.filter_cancel).setOnClickListener(this);
        findViewById(R.id.apply_filter).setOnClickListener(this);
        findViewById(R.id.resetFilter).setOnClickListener(this);

        filterOptionViewPager = (NoSwipeViewPager) findViewById(R.id.filter_option_viewpager);
        filterOptionViewPager.setAdapter(new FilterOptionPagerAdapter(getSupportFragmentManager()));

        getSupportFragmentManager().beginTransaction().replace(R.id.filter_option_contatiner,
                StocksActivity.filterOptionMap.get("filters")).commit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                StocksActivity.filterOptionMap.get("filters").updateView();
                Intent intent = new Intent(CHANGE_LIST_ITEM);
                intent.putExtra("itemPosition", 0);
                ApplicationController.getEventBus().post(intent);
            }
        }, 50);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.apply_filter:
                applyFilterClicked = true;
                updateRegNoInParams();
            case R.id.filter_cancel:
            case R.id.filter_actionbar_back:
                onBackPressed();
                break;

            case R.id.resetFilter:
                StocksActivity.filterParams.clear();
                ((FilterFragment)
                        ((FilterOptionPagerAdapter)
                                filterOptionViewPager.getAdapter()).getItem(list.indexOf("Model"))).refreshList(null);
                ApplicationController.getEventBus().post(new Intent(FilterActivity.RESET_COMMAND));
                for (int i=0; i<list.size(); i++) {
                    ((FilterFragment)
                            ((FilterOptionPagerAdapter)
                                    filterOptionViewPager.getAdapter()).getItem(i)).getResetFragment().updateOnReset();
                }
                break;
        }
    }

    private void updateRegNoInParams() {
        try {
            String regNo = ((EditText) ((FilterFragment) ((FilterOptionPagerAdapter) filterOptionViewPager.getAdapter())
                    .getItem(list.indexOf("Reg No"))).getListView().getChildAt(0).findViewById(R.id.reg_no))
                    .getText().toString().trim();
            Intent updateRegNo = new Intent(FilterActivity.UPDATE_COUNT);
            updateRegNo.putExtra("itemPosition", list.indexOf("Reg No"));
            if (regNo.length() > 0) {
                updateRegNo.putExtra("count", 1);
                StocksActivity.filterOptionCount.put(list.indexOf("Reg No"), 1);
            } else {
                updateRegNo.putExtra("count", 0);
                StocksActivity.filterOptionCount.put(list.indexOf("Reg No"), 0);
            }
            StocksActivity.filterParams.put("reg no", new ArrayList<>(Arrays.asList(new String[]{regNo})));
        } catch (IllegalStateException e) {
            if (CommonUtils.canLog()) {
                GCLog.e("Method is returning null pointer");
            }
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

    @Subscribe
    public void onCommandsReceived(Intent intent) {
        Bundle extras = intent.getExtras();
        String key;
        String dataKey;

        switch (intent.getAction()) {
            case FilterActivity.FILTER_OPTION_CLICKED:
                key = extras.getString(FilterActivity.MAP_KEY);
                ArrayList<String> makeIds = null;
                if ("model".equalsIgnoreCase(key)) {
                    if (StocksActivity.filterParams.get("make") != null
                            && StocksActivity.filterParams.get("make").size() > 0) {
                        makeIds = new ArrayList<>();
                        for (String mapKey : StocksActivity.filterParams.get("make")) {
                            makeIds.add(StocksActivity.makeHashMap.get(mapKey));
                        }
                    }
                }
                if (StocksActivity.filterOptionMap.get(key) != null) {
                    currentKey = key;
                    filterOptionViewPager.setCurrentItem(list.indexOf(CommonUtils.camelCase(key)));
                    if ("model".equalsIgnoreCase(key)) {
                        ((FilterFragment)
                                ((FilterOptionPagerAdapter)
                                        filterOptionViewPager.getAdapter()).getItem(list.indexOf("Model"))).refreshList(makeIds);
                    }
                }
                break;

            case "":
                key = currentKey;
                int index = list.indexOf(CommonUtils.camelCase(key));
                dataKey = extras.getString(FilterActivity.DATA_KEY);
                Intent updateCount = new Intent(FilterActivity.UPDATE_COUNT);
                updateCount.putExtra("itemPosition", list.indexOf(CommonUtils.camelCase(key)));
                if ("make".equalsIgnoreCase(key)) {
                    if (CommonUtils.isValidField(StocksActivity.filterParams.get("model"))) {
                        StocksActivity.filterParams.get("model").clear();
                    }
                    ((FilterFragment)
                            ((FilterOptionPagerAdapter)
                                    filterOptionViewPager.getAdapter()).getItem(list.indexOf("Model"))).clearSelection();
                    ApplicationController.getEventBus().post(new Intent(FilterActivity.REFRESH_MODEL_COUNT));
                }
                if (!currentKey.isEmpty()
                        && StocksActivity.filterParams.containsKey(key)
                        && StocksActivity.filterParams.get(key) != null) {
                    if (StocksActivity.filterParams.get(key).contains(dataKey)) {
                        StocksActivity.filterParams.get(key).remove(dataKey);
                    } else {
                        StocksActivity.filterParams.get(key).add(dataKey);
                    }
                } else {
                    StocksActivity.filterParams.put(key, new ArrayList<String>());
                    StocksActivity.filterParams.get(key).add(dataKey);
                }
                StocksActivity.filterOptionCount.put(index, StocksActivity.filterParams.get(key).size());
                updateCount.putExtra("count", StocksActivity.filterParams.get(key).size());
                ApplicationController.getEventBus().post(updateCount);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra("applyFilter", applyFilterClicked);
        /*StocksActivity.filterSelectedPositions.clear();
        for (int i=0; i<list.size(); i++) {
            StocksActivity.filterSelectedPositions.put(i,
                    ((FilterFragment) ((FilterOptionPagerAdapter) filterOptionViewPager.getAdapter())
                            .getItem(i)).getSelectedPositions());
        }*/
        setResult(RESULT_OK, data);
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_BUYER_LEAD_DETAIL);
    }

    private class FilterOptionPagerAdapter extends FragmentPagerAdapter {

        public FilterOptionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String key = list.get(position).toLowerCase();
            return StocksActivity.filterOptionMap.get(key);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }
}
