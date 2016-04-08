package com.gcloud.gaadi.insurance;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.ui.FilterFragment;
import com.gcloud.gaadi.ui.NoSwipeViewPager;
import com.gcloud.gaadi.ui.StocksActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;

public class InsuranceAllCasesFilterActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String CHANGE_LIST_ITEM = "changeListItem";
    public static final String FILTER_OPTION_CLICKED = "filterOptionClicked";
    public static final String MAP_KEY = "key";
    public static final String UPDATE_COUNT = "updateCount";
    public static final String RESET_COMMAND = "resetCommand";
    public static final String DATA_KEY = "dataKey";
    final public static ArrayList<String> list = new ArrayList<>(
            Arrays.asList(new String[]{"Req No", "Reg No", "Make", "Model", "Status", "Insurance Type", "Insurer"}));
    public static final String REFRESH_MODEL_COUNT = "refreshModelCount";
    private boolean applyFilterClicked = false;
    private String currentKey = "";

    private NoSwipeViewPager filterOptionViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_layout);

        findViewById(R.id.filter_actionbar_back).setOnClickListener(this);
        findViewById(R.id.filter_cancel).setOnClickListener(this);
        findViewById(R.id.apply_filter).setOnClickListener(this);
        findViewById(R.id.resetFilter).setOnClickListener(this);

        createFilters();
    }

    private void createFilters() {

        filterOptionViewPager = (NoSwipeViewPager) findViewById(R.id.filter_option_viewpager);
        filterOptionViewPager.setAdapter(new FilterOptionPagerAdapter(getSupportFragmentManager()));

        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.filter_option_contatiner,
                    StocksActivity.filterOptionMap.get("filters")).commit();
        } catch (NullPointerException e) {
            Crashlytics.logException(e);
            finish();
            return;
        }
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
                updateRegNoInParams("Reg No");
                updateRegNoInParams("Req No");
            case R.id.filter_cancel:
            case R.id.filter_actionbar_back:
                onBackPressed();
                break;

            case R.id.resetFilter:
                StocksActivity.filterParams.clear();
                ((FilterFragment)
                        ((FilterOptionPagerAdapter)
                                filterOptionViewPager.getAdapter()).getItem(list.indexOf("Model"))).refreshList(null);
                ApplicationController.getEventBus().post(new Intent(RESET_COMMAND));
                for (int i=0; i<list.size(); i++) {
                    ((FilterFragment)
                            ((FilterOptionPagerAdapter)
                                    filterOptionViewPager.getAdapter()).getItem(i)).getResetFragment().updateOnReset();
                }
                break;
        }
    }

    private void updateRegNoInParams(String key) {
        try {
            String regNo = ((EditText) ((FilterFragment) ((FilterOptionPagerAdapter) filterOptionViewPager.getAdapter())
                    .getItem(list.indexOf(key))).getListView().getChildAt(0).findViewById(R.id.reg_no))
                    .getText().toString().trim();
            Intent updateRegNo = new Intent(UPDATE_COUNT);
            updateRegNo.putExtra("itemPosition", list.indexOf(key));
            if (regNo.length() > 0) {
                updateRegNo.putExtra("count", 1);
                StocksActivity.filterOptionCount.put(list.indexOf(key), 1);
            } else {
                updateRegNo.putExtra("count", 0);
                StocksActivity.filterOptionCount.put(list.indexOf(key), 0);
            }
            StocksActivity.filterParams.put(key.toLowerCase(), new ArrayList<>(Arrays.asList(new String[]{regNo})));
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

    @Subscribe
    public void onCommandsReceived(Intent intent) {
        Bundle extras = intent.getExtras();
        String key;
        String dataKey;

        switch (intent.getAction()) {
            case FILTER_OPTION_CLICKED:
                key = extras.getString(MAP_KEY);
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
                dataKey = extras.getString(DATA_KEY);
                Intent updateCount = new Intent(UPDATE_COUNT);
                updateCount.putExtra("itemPosition", list.indexOf(CommonUtils.camelCase(key)));
                if ("make".equalsIgnoreCase(key)) {
                    if (CommonUtils.isValidField(StocksActivity.filterParams.get("model"))) {
                        StocksActivity.filterParams.get("model").clear();
                    }
                    ((FilterFragment)
                            ((FilterOptionPagerAdapter)
                                    filterOptionViewPager.getAdapter()).getItem(list.indexOf("Model"))).clearSelection();
                    ApplicationController.getEventBus().post(new Intent(REFRESH_MODEL_COUNT));
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
