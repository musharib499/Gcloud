package com.gcloud.gaadi.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FilterQueryProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.CustomEvent;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.MakeAdapter;
import com.gcloud.gaadi.adapter.ModelAdapter;
import com.gcloud.gaadi.adapter.NumOwnerAdapter;
import com.gcloud.gaadi.adapter.StocksAdapter;
import com.gcloud.gaadi.adapter.StocksPagerAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.DateType;
import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.constants.SortType;
import com.gcloud.gaadi.db.DBFunction;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.events.CancelMakeEvent;
import com.gcloud.gaadi.events.CancelModelEvent;
import com.gcloud.gaadi.events.FilterEvent;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.events.SetTabTextEvent;
import com.gcloud.gaadi.events.ShareTypeEvent;
import com.gcloud.gaadi.interfaces.CallLogsItemClickInterface;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.interfaces.OnSortTypeListener;
import com.gcloud.gaadi.model.BasicListItemModel;
import com.gcloud.gaadi.model.CallLogItem;
import com.gcloud.gaadi.model.StockDetailModel;
import com.gcloud.gaadi.model.StockItemModel;
import com.gcloud.gaadi.model.StocksModel;
import com.gcloud.gaadi.service.SyncStocksService;
import com.gcloud.gaadi.slidingmenu.SlidingMenu;
import com.gcloud.gaadi.ui.fourmob.datetimepicker.date.DatePickerDialog;
import com.gcloud.gaadi.ui.swipelistview.SwipeListView;
import com.gcloud.gaadi.ui.viewpagerindicator.BaseActivityCollapsingToolbar;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.gcloud.gaadi.utils.GShareToUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


//import com.edmodo.rangebar.RangeBar;

/**
 * Created by ankit on 29/12/14.
 */
public class StocksActivity extends BaseActivityCollapsingToolbar
        implements OnNoInternetConnectionListener, View.OnClickListener,
        OnSortTypeListener,
        AdapterView.OnItemClickListener,
        DatePickerDialog.OnDateSetListener,
        CallLogsItemClickInterface {

    public static final String FILTER_OPTION_CLICKED = "filterOptionClicked";
    public static final String MAP_KEY = "key";
    public static final String DATA_KEY = "dataKey";
    public static final String RESET_COMMAND = "resetCommmand";
    public static final String CHANGE_LIST_ITEM = "changeListItem";
    public static final String UPDATE_COUNT = "updateCount";
    private static final String DATEPICKER_TAG = "DATEPICKER_TAG";
    private static final int FILTER_REQUEST_CODE = 99;
    public static HashMap<String, FilterFragment> filterOptionMap;
    public static HashMap<String, ArrayList<String>> filterParams;
    public static LinkedHashMap<String, String> makeHashMap;
    //public static HashMap<Integer, ArrayList<Integer>> filterSelectedPositions;
    public static HashMap<Integer, Integer> filterOptionCount;
    protected static int check_id_item = 0, previousCheckedId = -1;
    protected static boolean checkChangeListenerCalled = false;
    private final String TAG = "StocksActivity";
    public Fragment currentFragment;
    TextView label;
    SlidingMenu menu;
    HashMap<String, String> params = new HashMap<String, String>();
    ImageView bSearchFilter;
    private ActionBar mActionBar;
    private String makeId = "";
    private Bundle args;
    private FrameLayout layoutContainer, alternativeLayout;
    private View dummyView;
    private Button retry;
    private ProgressBar progressBar;
    private CustomAutoCompleteTextView makeAutoCompleteTextView;
    private CustomAutoCompleteTextView modelAutoCompleteTextView;
    private TextView errorMessage;
    private ImageView sort, filter;
    private int pageNo = 1;
    //    private GAHelper mGaHelper;
    private int selectedIndex = 0;
    private Boolean nextPossible, makeDataChanged = false;
    private TextView tvMin, tvMax, kmDriven, budgetFromTextView, budgetToTextView, yearTo, yearFrom;
    private PopupMenu popupMenu;
    private String mobileNumber = "", carId = "", budgetFromID = "", budgetToID = "";
    private ShareTypeEvent shareTypeEvent;
    private ArrayList<StockItemModel> dataList;
    private LayoutInflater mInflater;
    private StocksAdapter stocksAdapter;
    private String trustMark = "";
    private SwipeListView stocksList;
    private ArrayList<BasicListItemModel> budgetFromList = new ArrayList<>(),
            budgetToList = new ArrayList<>(), kmList = new ArrayList<>();
    private NumOwnerAdapter budgetFromAdapter, budgetToAdapter, kmAdapter;
    private BasicListItemModel selectedBudget;
    private CheckBox trustMarkCertified;
    private DatePickerDialog datePickerDialog;
    private boolean smsReceiverRegistered = false;
    private StocksPagerAdapter stocksPagerAdapter;
    private FilterQueryProvider mFilterQueryAdapter = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getModelCursor(constraint);
        }
    };
    private boolean clickedOnTo = false;
    private String makeFilter = "";
    private String modelFilter = "";
    private int yearToFilter;
    private int yearFromFilter;
    private Integer[] yearList;
    private BasicListItemModel selectedKm;
    private String kmID = "", currentKey = "";
    private String kmFrom = "";
    private String kmTo = "";
    private String priceFrom = "";
    private String priceTo = "";
    private String budgetValue = "";
    private String kmValue = "";
    private ImageView ivBack;
    private CallLogsDialogFragment callLogsDialogFragment;
    private Intent whatsappIntent = null;
    private String imageUrl;
    private GShareToUtil mGShareToUtil;
    private DrawerLayout Drawer;                                  // Declaring DrawerLayout
    private ActionBarDrawerToggle mDrawerToggle;
    private FilterQueryProvider mmFilterQueryProvider = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getCursor(constraint);
        }
    };
    private CustomEvent customEvent;
    private TextView refineFilter;
    private ImageView img;
    private int count = 0;
    private final OverFlowMenuListener listener = new OverFlowMenuListener() {
        @Override
        public void onOpen() {
            fabHide();
        }

        @Override
        public void onClose() {
            fabShow();
        }
    };

    private Cursor getModelCursor(CharSequence constraint) {
        Cursor cursor;
        if (makeId.trim().length() == 0) {
            cursor = ApplicationController.getMakeModelVersionDB().getModelRecords(constraint, true);
        } else {
            cursor = ApplicationController.getMakeModelVersionDB().getModelRecords(constraint, makeId, true);
        }
        return cursor;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        check_id_item = 0;
        finish();
        if (!menu.isMenuShowing()) {
            /*if (smsReceiverRegistered) {
                unregisterReceiver(sendSMSReceiver);
            }*/

            try {
                filterOptionCount.clear();
                filterParams.clear();
                //filterSelectedPositions.clear();
                filterOptionMap.clear();
                makeHashMap.clear();
            } catch (NullPointerException ex) {
                Crashlytics.logException(ex);
            }

        } else {
//            yearFrom.setEnabled(false);
//            yearTo.setEnabled(false);
            menu.toggle();
        }
    }

    private void initData() {
        Intent intent = new Intent(getBaseContext(), SyncStocksService.class);
        getBaseContext().stopService(intent);
        getBaseContext().startService(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        //mActionBar = getSupportActionBar();
        //  mActionBar.setDisplayHomeAsUpEnabled(true);
        // getLayoutInflater().inflate(R.layout.activity_place_holder,frameLayout);

        filterOptionMap = new HashMap<>();
        filterParams = new HashMap<>();
        makeHashMap = new LinkedHashMap<>();
        filterOptionCount = new HashMap<>();
        //filterSelectedPositions = new HashMap<>();
        initData();
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addTab(tabLayout.newTab().setText("Available Cars"));
        tabLayout.addTab(tabLayout.newTab().setText("Removed Cars"));
        stocksPagerAdapter = new StocksPagerAdapter(getSupportFragmentManager(), listener);
        viewPager.setAdapter(stocksPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        fab.setOnClickListener(this);

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
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setDisplayShowHomeEnabled(true);
        mGShareToUtil = new GShareToUtil(this);
        //mGaHelper = new GAHelper(this);
       /* mInflater = getLayoutInflater();
       // setContentView(R.layout.activity_place_holder);

        layoutContainer = (FrameLayout) findViewById(R.id.layoutContainer);

        layoutContainer.setFocusable(true);
        layoutContainer.setFocusableInTouchMode(true);
*/
        /*simpleSideDrawer = new SimpleSideDrawer(this);
        simpleSideDrawer.setRightBehindContentView(R.layout.filter_layout);
        simpleSideDrawer.setEnabled(false);
        simpleSideDrawer.offsetLeftAndRight(40);

*/


        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.RIGHT);
        //menu.setAboveOffset(R.layout.filter_layout);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        //menu.setShadowDrawable(R.drawable.shadow);

        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(R.layout.filter_layout);
        // Toolbar toolbarMenu= (Toolbar) findViewById(R.id.toolbar_menu);
        // toolbarMenu.setNavigationIcon(R.drawable.refresh);
        // toolbarMenu.inflateMenu(R.menu.menu_slide_filter);
        RelativeLayout toolbarRelaytive = (RelativeLayout) findViewById(R.id.toolbar_relativeLayout);
        toolbarRelaytive.setVisibility(View.GONE);
        setInitialView();

        StocksModel.Filters filters = DBFunction.getFilterData();
        if (filters != null && filters.getMake() != null) {
            makeFilterRequest(filters);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_VIEW_STOCK);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ApplicationController.getEventBus().register(this);
    }

    public void fabHide() {
        //if (fab.getVisibility() == View.VISIBLE) {
        setFabCounter(0);
        fab.setVisibility(View.GONE);
        //}
    }

    public void fabShow() {
        //if (fab.getVisibility() == View.GONE) {
        setFabCounter(count);
        fab.setVisibility(View.VISIBLE);
        //}
    }

    @Override
    protected void onPause() {
        super.onPause();
        ApplicationController.getEventBus().unregister(this);
    }


    private void makeFilterRequest(StocksModel.Filters response) {
        if (response == null || filterOptionMap == null) {
            return;
        }
        if (!filterOptionMap.containsKey("filters") || filterOptionMap.get("filters") == null) {
            if (response.getMake() == null) {
                return;
            }
            filterOptionMap.put("filters", FilterFragment.getInstance(true,
                    FilterActivity.list));
            filterOptionMap.put("reg no", FilterFragment.getInstance(true, "Search car by Reg no"));
            filterOptionMap.put("certified", FilterFragment.getInstance(false,
                    new ArrayList<>(Arrays.asList(new String[]{"TrustMark"}))));
            for (StockDetailModel model : response.getMake()) {
                makeHashMap.put(model.getMake(), String.valueOf(model.getMakeid()));
            }
            filterOptionMap.put("make", FilterFragment.getInstance(false, makeHashMap, false));
            LinkedHashMap<String, String> modelMap = new LinkedHashMap<>();
            for (StockDetailModel model : response.getModel()) {
                modelMap.put(model.getModel(), String.valueOf(model.getMakeid()));
            }
            filterOptionMap.put("model", FilterFragment.getInstance(false, modelMap, false));

            filterOptionMap.put("fuel type", FilterFragment.getInstance(false, response.getFuelType()));
            filterOptionMap.put("year", FilterFragment.getInstance(false, response.getYear()));

            LinkedHashMap<String, String> priceMap = new LinkedHashMap<>();
            for (StocksModel.Filters.KeyValueModel model : response.getPrice()) {
                priceMap.put(model.getValue(), model.getKey());
            }
            filterOptionMap.put("price", FilterFragment.getInstance(false, priceMap, true));
            LinkedHashMap<String, String> kmMap = new LinkedHashMap<>();
            for (StocksModel.Filters.KeyValueModel model : response.getKm()) {
                kmMap.put(model.getValue(), model.getKey());
            }
            filterOptionMap.put("km", FilterFragment.getInstance(false, kmMap, true));

            fab.setVisibility(View.VISIBLE);

        }
    }

    private void showNetworkConnectionErrorLayout(boolean fullPageError) {
        setInitialView();
        hideProgressBar();
        /*alternativeLayout.removeAllViews();*/
        if (fullPageError) {
            View view = mInflater.inflate(R.layout.layout_error, null, false);

            alternativeLayout.addView(view);

            errorMessage = (TextView) view.findViewById(R.id.errorMessage);
            errorMessage.setText(R.string.network_connection_error_message);

            retry = (Button) view.findViewById(R.id.retry);
            retry.setOnClickListener(this);

        } else {
            CommonUtils.showToast(this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
        }
    }

    private void showNoRecordsMessage(String message) {
        setInitialView();
        hideProgressBar();

        View view = mInflater.inflate(R.layout.layout_error, null, false);

        alternativeLayout.addView(view);

        refineFilter = (TextView) view.findViewById(R.id.checkconnection);
        //  refineFilter.setText(R.string.refine_filter);
        refineFilter.setVisibility(View.INVISIBLE);
        img = (ImageView) view.findViewById(R.id.no_internet_img);
        img.setImageResource(R.drawable.no_result_icons);
        errorMessage = (TextView) view.findViewById(R.id.errorMessage);
        errorMessage.setText(message);

        retry = (Button) view.findViewById(R.id.retry);
        retry.setTag("ADD STOCK");
        retry.setText(R.string.add_stock);
        retry.setOnClickListener(this);

    }

    private void showServerErrorLayout(boolean showFullPageError) {
        setInitialView();
        hideProgressBar();

        if (showFullPageError) {
            View view = mInflater.inflate(R.layout.layout_error, null, false);
            /*alternativeLayout.removeAllViews();*/
            alternativeLayout.addView(view);

            errorMessage = (TextView) view.findViewById(R.id.errorMessage);
            errorMessage.setText(R.string.no_internet_connection);

            retry = (Button) view.findViewById(R.id.retry);
            retry.setOnClickListener(this);

        } else {
            CommonUtils.showToast(this, getString(R.string.network_error), Toast.LENGTH_SHORT);
        }
    }

    private Cursor getCursor(CharSequence constraint) {
        Cursor cursor;
        MakeModelVersionDB db = ApplicationController.getMakeModelVersionDB();
        cursor = db.getMakeRecords(constraint);
        return cursor;
    }


  /*  public void openAnimate(int position) {
        Log.e("OpenANimate", " StocksActivity");
        stocksList.openAnimate(position);
    }

    public void closeAnimate(int position) {
        stocksList.closeAnimate(position);
    }*/

    private void setInitialView() {
      /*  layoutContainer.removeAllViews();
        dummyView = mInflater.inflate(R.layout.layout_loading_full_page, null, false);
        layoutContainer.addView(dummyView);
        alternativeLayout = (FrameLayout) dummyView.findViewById(R.id.alternativeLayout);
        progressBar = (ProgressBar) dummyView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);*/

        menu.measure(getResources().getDisplayMetrics().widthPixels - 40, getResources().getDisplayMetrics().heightPixels);

        /*kmDriven = (TextView) findViewById(R.id.tvKmDriven);
        kmDriven.setOnClickListener(this);

        if (kmValue.trim().length() > 0)
            kmDriven.setText(kmValue);*/

        MakeAdapter makeAdapter = new MakeAdapter(this, null);
        /*makeAutoCompleteTextView = (CustomAutoCompleteTextView) findViewById(R.id.makeSuggestor);
        makeAutoCompleteTextView.setOnItemClickListener(this);
        makeAutoCompleteTextView.setType(MakeModelType.MAKE);
        makeAutoCompleteTextView.setThreshold(1);
        if (makeFilter.trim().length() > 0)
            makeAutoCompleteTextView.setText(makeFilter);
        makeAdapter.setFilterQueryProvider(mmFilterQueryProvider);
        makeAutoCompleteTextView.setAdapter(makeAdapter);

        modelAutoCompleteTextView = (CustomAutoCompleteTextView) findViewById(R.id.modelSuggestor);
        ModelAdapter modelAdapter = new ModelAdapter(this, null);
        if (modelFilter.trim().length() > 0)
        modelAutoCompleteTextView.setText(modelFilter);
        modelAutoCompleteTextView.setOnItemClickListener(this);
        modelAutoCompleteTextView.setType(MakeModelType.MODEL);
        modelAutoCompleteTextView.setAdapter(modelAdapter);
        modelAutoCompleteTextView.setThreshold(1);
        modelAdapter.setFilterQueryProvider(mFilterQueryAdapter);*/

        /*yearFrom = (TextView) findViewById(R.id.yearFrom);
        yearFrom.setOnClickListener(this);*/

        if (yearFromFilter > 0)
            yearFrom.setText("From Year : " + yearFromFilter + "");

        /*yearTo = (TextView) findViewById(R.id.yearTo);
        yearTo.setOnClickListener(this);*/

        if (yearToFilter > 0)
            yearTo.setText("To Year : " + yearToFilter);

        /*budgetFromTextView = (TextView) findViewById(R.id.tvBudgetFromFilter);
        budgetFromTextView.setOnClickListener(this);

        budgetToTextView = (TextView) findViewById(R.id.tvBudgetToFilter);
        budgetToTextView.setOnClickListener(this);*/

        /*trustMarkCertified = (CheckBox) findViewById(R.id.trustmarkCertified);
        trustMarkCertified.setOnClickListener(this);*/

//        if (budgetValue.trim().length() > 0)
//            budgetTextView.setText(budgetValue);

    }

    @Override
    public void onNoInternetConnection(NetworkEvent networkEvent) {
        //ApplicationController.getInstance().cancelPendingRequests(Constants.TAG_STOCK_DETAIL);

        NetworkEvent.NetworkError networkError = networkEvent.getNetworkError();
        if (networkError == NetworkEvent.NetworkError.NO_INTERNET_CONNECTION) {
            showNetworkErrorLayout(networkEvent.isShowFullPageError());
        } else if (networkError == NetworkEvent.NetworkError.SLOW_CONNECTION) {

        }
    }

    private void showNetworkErrorLayout(boolean showFullPageError) {
        setInitialView();
        hideProgressBar();
        /*alternativeLayout.removeAllViews();*/
        if (showFullPageError) {
            View view = mInflater.inflate(R.layout.layout_error, null, false);
            alternativeLayout.addView(view);
            errorMessage = (TextView) view.findViewById(R.id.errorMessage);
            errorMessage.setText(R.string.network_error);
            retry = (Button) view.findViewById(R.id.retry);
            retry.setOnClickListener(this);

        } else {
            CommonUtils.showToast(this, getString(R.string.network_error), Toast.LENGTH_SHORT);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance();
        final ListPopupWindow listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.abc_popup_background_mtrl_mult, null));
        listPopupWindow.setModal(true);
        listPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
        switch (v.getId()) {

            case R.id.fab:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.ACTION_TAP,
                        Constants.LABEL_FILTER,
                        0);
                if (filterOptionMap.containsKey("filters")) {
                    startActivityForResult(new Intent(this, FilterActivity.class), FILTER_REQUEST_CODE);
                } else {
                    CommonUtils.showToast(this, "Wait while data loading completes", Toast.LENGTH_SHORT);
                }
                break;


        }
    }

    /*  private void showSortingPopup(View anchorView) {

          PopupMenu popupMenu = new PopupMenu(this, anchorView);
          popupMenu.setOnMenuItemClickListener(this);
          popupMenu.inflate(R.menu.stock_sorting_menu);
          popupMenu.show();
      }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stock_sorting_menu, menu);

        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                Constants.CATEGORY_VIEW_STOCK,
                Constants.CATEGORY_VIEW_STOCK,
                Constants.ACTION_TAP,
                Constants.LABEL_SORT,
                0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.ACTION_TAP,
                        Constants.LABEL_BACK_BUTTON,
                        0);
                onBackPressed();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void filterStockList(boolean applyFilter) {

        hideKeyboard();

        params.clear();
        count = 0;
        for (Map.Entry entry : filterParams.entrySet()) {
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
        if ((filterParams.get("reg no") != null && filterParams.get("reg no").get(0).isEmpty()) && count > 0) {
            count--;
        }

        if (applyFilter) {
            ApplicationController.getEventBus().post(new FilterEvent(params, viewPager.getCurrentItem()));
        }
        setFabCounter(count);
    }

    private void openAddStockActivity() {
        Intent intent = new Intent(this, StockAddActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSortType(SortType sortType) {
        pageNo = 1;
        switch (sortType) {
            case PRICE_ASC:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SORT_BY_PRICE,
                        0);
                params.put(Constants.SORT_BY_FIELD, Constants.LABEL_SORT_BY_PRICE);
                params.put(Constants.SORT_BY_VALUE, Constants.LABEL_ASCENDING);
                params.put(Constants.PAGE_NO, String.valueOf(pageNo));
                break;

            case PRICE_DESC:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SORT_BY_PRICE,
                        0);
                params.put(Constants.SORT_BY_FIELD, Constants.LABEL_SORT_BY_PRICE);
                params.put(Constants.SORT_BY_VALUE, Constants.LABEL_DESCENDING);
                params.put(Constants.PAGE_NO, String.valueOf(pageNo));
                break;

            case KM_ASC:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SORT_BY_KM,
                        0);
                params.put(Constants.SORT_BY_FIELD, Constants.LABEL_SORT_BY_KM);
                params.put(Constants.SORT_BY_VALUE, Constants.LABEL_ASCENDING);
                params.put(Constants.PAGE_NO, String.valueOf(pageNo));
                break;

            case KM_DESC:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SORT_BY_KM,
                        0);
                params.put(Constants.SORT_BY_FIELD, Constants.LABEL_SORT_BY_KM);
                params.put(Constants.SORT_BY_VALUE, Constants.LABEL_DESCENDING);
                params.put(Constants.PAGE_NO, String.valueOf(pageNo));
                break;

            case YEAR_ASC:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SORT_BY_YEAR,
                        0);
                params.put(Constants.SORT_BY_FIELD, Constants.LABEL_SORT_BY_YEAR);
                params.put(Constants.SORT_BY_VALUE, Constants.LABEL_ASCENDING);
                params.put(Constants.PAGE_NO, String.valueOf(pageNo));
                break;

            case YEAR_DESC:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SORT_BY_YEAR,
                        0);
                params.put(Constants.SORT_BY_FIELD, Constants.LABEL_SORT_BY_YEAR);
                params.put(Constants.SORT_BY_VALUE, Constants.LABEL_DESCENDING);
                params.put(Constants.PAGE_NO, String.valueOf(pageNo));
                break;
        }

//        setInitialView();
//        makeStockListRequest(true, 1);
    }

    @Override
    public void onCallLogSelected(CallLogItem callLogItem, ShareType shareType, String shareText, String carId, String imageUrl) {
        mobileNumber = callLogItem.getGaadiFormatNumber();
        GCLog.e("call log item: " + callLogItem.toString() + ", sharetype = " + shareType.name() + ", sharetext = " + shareText);
        if (shareType == ShareType.WHATSAPP) {
            if ("null".equalsIgnoreCase(callLogItem.getName()) || (callLogItem.getName() == null)) {
                mGShareToUtil.showAddNameToContactDialog(callLogItem.getNumber(), shareText, carId, imageUrl);

            } else {
                mGShareToUtil.sendMessageInWhatsApp(shareText, carId, imageUrl);
            }

        } else if (shareType == ShareType.SMS) {
            try {
                GCLog.e("share text: " + shareText + ", carid : " + carId + ", number : " + callLogItem.getNumber());
                mGShareToUtil.sendSMSHelp(callLogItem.getNumber(), shareText, carId);
            } catch (Exception e) {
                CommonUtils.showToast(this, "Could not send SMS. Invalid number.", Toast.LENGTH_SHORT);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        currentFragment = stocksPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
        getSupportFragmentManager().putFragment(outState, "currentFragment", currentFragment);
    }


    @Override
    public void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        currentFragment = getSupportFragmentManager().getFragment(inState, "currentFragment");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.STOCK_VIEW_ACTIVITY) {
            if (resultCode != RESULT_CANCELED) {
                // makeStockListRequest(false, 1);
                initData();
                if (stocksPagerAdapter == null) {
                    stocksPagerAdapter = new StocksPagerAdapter(getSupportFragmentManager(), listener);
                }
                viewPager.setAdapter(stocksPagerAdapter);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ApplicationController.getEventBus().post("refresh");
                    }
                }, 500);
                /*stocksPagerAdapter.getRegisteredFragment(0).onActivityResult(requestCode, resultCode, data);
                stocksPagerAdapter.getRegisteredFragment(1).onActivityResult(requestCode, resultCode, data);*/
            }
        } else if (requestCode == Constants.STOCKS_CONTACT_LIST) {
            if (resultCode == RESULT_OK) {
                if (stocksPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem()) == null)
                    currentFragment.onActivityResult(requestCode, resultCode, data);
                else
                    stocksPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem()).onActivityResult(requestCode, resultCode, data);
            }
        } else if (requestCode == FILTER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    boolean applyFilter = extras.getBoolean("applyFilter");
                    filterStockList(applyFilter);
                }
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        GCLog.e("without dateType");
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day, DateType dateType) {
        GCLog.e(" With Date");
        if (clickedOnTo) {
            if (year >= yearFromFilter) {
                yearTo.setText("To Year : " + year);
                yearToFilter = year;
            } else {
                yearTo.setText("");
                CommonUtils.showToast(this, "Please enter a valid year", Toast.LENGTH_SHORT);
            }
        } else {
            if (yearToFilter != 0 && year > yearToFilter) {
                yearFrom.setText("");
                CommonUtils.showToast(this, "Please enter a valid year", Toast.LENGTH_SHORT);
            } else {
                yearFrom.setText("From Year : " + year);
                yearFromFilter = year;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getAdapter() instanceof MakeAdapter) {
            Cursor cursor = (Cursor) makeAutoCompleteTextView.getAdapter().getItem(position);
            String makeName = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MAKENAME));
            makeId = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MAKEID));
            makeAutoCompleteTextView.setText(makeName);
            makeFilter = makeName;
        } else if (parent.getAdapter() instanceof ModelAdapter) {
            GCLog.e("true");
            Cursor cursor = (Cursor) modelAutoCompleteTextView.getAdapter().getItem(position);
            String modelName = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MODELNAME));
            GCLog.e(modelName);
            modelAutoCompleteTextView.setText(modelName);
            modelFilter = modelName;
        }
    }

    @Subscribe
    public void clearMake(CancelMakeEvent cancelMakeEvent) {
        makeFilter = "";
        makeId = "";
        params.remove(Constants.FILTER_MAKE);
        GCLog.e(" CLEAR MAKE");
    }

    @Subscribe
    public void clearModel(CancelModelEvent cancelModelEvent) {
        modelFilter = "";
        params.remove(Constants.FILTER_MODEL);
        GCLog.e(" CLEAR MODEL");
    }

    @Subscribe
    public void addToStockCalled(String order) {
        if ("refresh".equals(order)) {
            viewPager.setCurrentItem(0);
        }
    }

    @Subscribe
    public void setTabText(SetTabTextEvent event) {
        if (event.getCurrentItem() == Constants.AVAILABLE_STOCKS)
            tabLayout.getTabAt(0).setText(event.getText());
        else if (event.getCurrentItem() == Constants.REMOVED_STOCKS)
            tabLayout.getTabAt(1).setText(event.getText());

    }

    @Subscribe
    public void filterOptionClicked(Intent intent) {
        try {
            switch (intent.getAction()) {
                case "Initiate Filters":
                    StocksModel.Filters filters = DBFunction.getFilterData();
                    if (filters != null) {
                        makeFilterRequest(filters);
                    }
                    break;
            }
        } catch (NullPointerException ex) {
            Crashlytics.logException(ex);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        check_id_item = 0;
        checkChangeListenerCalled = false;
        previousCheckedId = -1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Fragment fragment = stocksPagerAdapter.getRegisteredFragment(0);
        if (fragment != null && fragment instanceof AvailableStocksFragment) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public interface OverFlowMenuListener {
        void onOpen();

        void onClose();
    }
}
