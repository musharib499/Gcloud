package com.gcloud.gaadi.insurance;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.EndlessScroll.RecyclerViewEndlessScrollListener;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.DealerQuoteAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.InsuranceInspectedCarSelectedEvent;
import com.gcloud.gaadi.model.FinanceCompany;
import com.gcloud.gaadi.model.InsuranceInspectedCarData;
import com.gcloud.gaadi.model.InsuranceQuotesModel;
import com.gcloud.gaadi.model.QuoteDetails;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.BaseActivity;
import com.gcloud.gaadi.ui.GCProgressDialog;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;


public class DealerQuoteScreen extends BaseActivity implements View.OnClickListener {

    private static final int FILTER_REQUEST = Constants.INSURANCE_CASE_FOR_INSPECTED_CAR+1;
    public static int isZeroDep = 0, deductible_premiums = 0, isZeroDep_C = 0, isZeroDep_H = 0;
    protected static String selectedIdv = "";
    protected static int minValue = 0;
    protected static int maxValue = 0;
    protected static int idvSteps = 0;
    protected static int steps = 0;
    protected static int checkItemPosition = 0;
    protected static boolean checkStaps = false, checkZeroDp = false;
    int pageNo = 1;
    int selectedIndex = 0;
    String cngLpgKitValue = "", insuranceCaseId = "";
    String carId = "";
    TextView tv_makeModel, tv_regNo, tv_regYear, tv_fuelType, tv_Price, tv_month, tv_Next;
    ImageView inspectedCarImage, iv_color, iv_makeLogo;
    private RecyclerView recyclerView;
    private LayoutInflater mInflater;
    private InsuranceInspectedCarData insuranceInspectedCarData;
    private View dummyView;
    private Button retry;
    private ProgressBar progressBar, progressBarList;
    //    private GAHelper mGAHelper;
    private TextView errorMessage;
    private FrameLayout layoutContainer, alternativeLayout;
    private ArrayList<QuoteDetails> insuranceQuotes;
    private ArrayList<String> mInsuranceCities;
    private ArrayList<FinanceCompany>financeCompanyList;
    private String mProcessId = "";
    private DealerQuoteAdapter dealerQuoteAdapter;
    private RecyclerView recList;
    private FrameLayout quouteFrameError;
    private LinearLayoutManager llm;
 //   private ActionBar mActionBar;
    private boolean mIsNewCar, stopRequest = false;
    private String mCaseSelected = "";
    private String mAgentId;
    private HashMap<String, String> params;
    private   GCProgressDialog gcProgressDialog;
    private TextView refineFilter;
    private ImageView img;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        {

            getLayoutInflater().inflate(R.layout.activity_place_holder, frameLayout);
            layoutContainer = (FrameLayout) findViewById(R.id.layoutContainer);
           // gcProgressDialog=new GCProgressDialog(this,this, "Please wait while we are fetching lowest quotes");
            fab.setOnClickListener(this);
            mInflater = getLayoutInflater();
          /*  mActionBar = getSupportActionBar();
            mActionBar.setDisplayHomeAsUpEnabled(true);
*/
            params = new HashMap<>();
            insuranceInspectedCarData = (InsuranceInspectedCarData) getIntent().getSerializableExtra(Constants.INSURANCE_INSPECTED_CAR_DATA);
            Bundle args = getIntent().getExtras();
            if (args == null) {
                if (savedInstanceState != null) {
                    mInsuranceCities = savedInstanceState.getStringArrayList(Constants.INSURANCE_CITIES);
                    financeCompanyList = (ArrayList<FinanceCompany>) savedInstanceState.getSerializable(Constants.FINANCE_COMPANY);
                    mProcessId = savedInstanceState.getString(Constants.PROCESS_ID);
                    mIsNewCar = savedInstanceState.getBoolean(Constants.INSURANCE_IS_NEW_CAR);
                    mCaseSelected = savedInstanceState.getString(Constants.SELECTED_CASE);
                    mAgentId = savedInstanceState.getString(Constants.AGENT_ID);
                    cngLpgKitValue=savedInstanceState.getString(Constants.CNG_PNG_VALUE);
                }
            } else {
                mInsuranceCities = args.getStringArrayList(Constants.INSURANCE_CITIES);
                financeCompanyList = (ArrayList<FinanceCompany>) args.getSerializable(Constants.FINANCE_COMPANY);
                mProcessId = args.getString(Constants.PROCESS_ID);
                mIsNewCar = args.getBoolean(Constants.INSURANCE_IS_NEW_CAR);
                mCaseSelected = args.getString(Constants.SELECTED_CASE);
                mAgentId = args.getString(Constants.AGENT_ID);
                cngLpgKitValue=args.getString(Constants.CNG_PNG_VALUE);

            }
          /*  if (steps==0) {*/
              //  setInitialView();
           // }
            initializeList(null);
            inSideProgressView();
            makeQuoteListRequest(true, 1);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();
        minValue = 0;
        maxValue = 0;
        idvSteps = 0;
        steps = 0;
        isZeroDep =0;
        deductible_premiums =0;
        checkItemPosition =0;
        checkZeroDp = false;
        checkStaps=false;
        params.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        params.clear();
        CommonUtils.startActivityTransition(DealerQuoteScreen.this, Constants.TRANSITION_RIIGHT);

    }


   /* public static int getMaxValue() {
        return maxValue;
    }

    public static void setMaxValue(int maxValue) {
        DealerQuoteScreen.maxValue = maxValue;
    }

    public static int getIdvSteps() {
        return idvSteps;
    }

    public static void setIdvSteps(int idvSteps) {
        DealerQuoteScreen.idvSteps = idvSteps;
    }

    public static int getMinValue() {
        return minValue;
    }

    public static void setMinValue(int minValue) {
        DealerQuoteScreen.minValue = minValue;
    }*/

    private void inSideProgressView()
    {


            View view = mInflater.inflate(R.layout.dealer_progress_dialog, null, false);


                ((TextView) view.findViewById(R.id.progressMessage)).setText("Please wait while we are fetching lowest quotes");

            ImageView imageView= (ImageView) view.findViewById(R.id.img_pro_bg);
            if (quouteFrameError.getVisibility() != View.VISIBLE)
            {
                quouteFrameError.setVisibility(View.VISIBLE);
            }
           fab.setVisibility(View.GONE);
        fab_lay_count.setVisibility(View.GONE);
            quouteFrameError.removeAllViews();
            quouteFrameError.addView(view);


    }
    private void inSideCheckErrorView(View view)
    {
        if (quouteFrameError.getVisibility() != View.VISIBLE)
        {
            quouteFrameError.setVisibility(View.VISIBLE);
        }

        quouteFrameError.removeAllViews();
        quouteFrameError.addView(view);
    }

    private void setInitialView() {
        layoutContainer.removeAllViews();
        dummyView = mInflater.inflate(R.layout.layout_loading_full_page, null, false);
        layoutContainer.addView(dummyView);
        alternativeLayout = (FrameLayout) dummyView.findViewById(R.id.alternativeLayout);
       /* TextView textView = new TextView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        textView.setText(" Please wait while we are fetching lowest quotes");
        textView.setPadding(0, 100, 0, 0);
        textView.setGravity(Gravity.CENTER);*/
       // alternativeLayout.addView(textView, params);
/*
        progressBar = (ProgressBar) dummyView.findViewById(R.id.progressBar);
        if (progressBar.getVisibility() != View.VISIBLE)
            progressBar.setVisibility(View.VISIBLE);*/
        if (!gcProgressDialog.isShowing()) {
            gcProgressDialog.show();
        }

    }


    private void makeQuoteListRequest(final boolean showFullPageError, final int pageNo) {
        final DateTime requestDateTime = new DateTime();

        if(mCaseSelected.equalsIgnoreCase(Constants.INSPECTED_CARS)) {
            params.put(Constants.METHOD_LABEL, Constants.DEALER_QUOTE_FOR_INSURANCE);
            params.put(Constants.CAR_ID, insuranceInspectedCarData.getCarId());
            params.put(Constants.CNG_LPG_KIT_VALUE, cngLpgKitValue);
        }
        else
        {
            params.put(Constants.METHOD_LABEL, Constants.GET_QUOTES_FOR_OTHER_CARS);
            params.put(Constants.PROCESS_ID, mProcessId);
        }

        params.put(Constants.PAGE, String.valueOf(pageNo));
        params.put(Constants.RPP, "10");
        Log.d("MakeQuotes List", params.toString());
        RetrofitRequest.getInsuranceQuotes(
                params, new Callback<InsuranceQuotesModel>() {
                    @Override
                    public void success(final InsuranceQuotesModel insuranceQuoteModel, retrofit.client.Response response) {
                      /*  if (quouteFrameError.getVisibility()==View.VISIBLE)
                        {
                            quouteFrameError.setVisibility(View.GONE);
                        }*/
                       /* if (progressBar != null) {


                            progressBar.setVisibility(View.GONE);


                        }*/

                      /*  if (gcProgressDialog != null) {
                            gcProgressDialog.dismiss();
                        }*/
                        proggressBarHiding();

                        if (stopRequest) {
                            return;
                        }

                        if ("T".equalsIgnoreCase(insuranceQuoteModel.getStatus())) {
                            if (insuranceQuoteModel.getQuoteList().size() > 0) {

                                GCLog.e("response list: " + insuranceQuoteModel.getQuoteList());

                                if (pageNo == 1) {
                                    initializeList(insuranceQuoteModel);
                                    if (fab.getVisibility() == View.GONE) {
                                        fab.setVisibility(View.VISIBLE);
                                        setFabCounter(count);

                                    }

                                } else {
                                    insuranceQuotes.addAll(insuranceQuoteModel.getQuoteList());

                                    dealerQuoteAdapter.notifyDataSetChanged();
                                }

                                if (!insuranceCaseId.equals(insuranceQuoteModel.getQuoteList().get(0).getInsurance_case_id())) {
                                    insuranceCaseId = insuranceQuoteModel.getQuoteList().get(0).getInsurance_case_id();
                                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                            Constants.LABEL_INSURANCE_POLICY,
                                            Constants.LABEL_INSURANCE_POLICY,
                                            Constants.CATEGORY_INSURANCE_QUOTE_SCREEN,
                                            insuranceCaseId + "-" +
                                                    String.valueOf((new DateTime().getMillis() - requestDateTime.getMillis()) / 1000), 0);
                                }

                                recList.setOnScrollListener(new RecyclerViewEndlessScrollListener(
                                        llm,
                                        insuranceQuoteModel.getTotalRecords()) {

                                    @Override
                                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                                        if (newState == 0) {

                                            if (fab.getVisibility() == View.GONE) {
                                                Animation fab_show = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_show);
                                                fab.setAnimation(fab_show);
                                                fab.setVisibility(View.VISIBLE);
                                                fab_show.setAnimationListener(new Animation.AnimationListener() {
                                                    @Override
                                                    public void onAnimationStart(Animation animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationEnd(Animation animation) {
                                                        if (fab_lay_count.getVisibility() == View.GONE) {
                                                            setFabCounter(count);
                                                        }

                                                    }

                                                    @Override
                                                    public void onAnimationRepeat(Animation animation) {

                                                    }
                                                });

                                            }
                                        } else {
                                            if (fab.getVisibility() == View.VISIBLE) {
                                                Animation fab_hide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_hide);
                                                fab.setAnimation(fab_hide);
                                                fab.setVisibility(View.GONE);

                                                fab_hide.setAnimationListener(new Animation.AnimationListener() {
                                                    @Override
                                                    public void onAnimationStart(Animation animation) {
                                                        if (fab_lay_count.getVisibility() == View.VISIBLE) {
                                                            fab_lay_count.setVisibility(View.GONE);
                                                        }
                                                    }

                                                    @Override
                                                    public void onAnimationEnd(Animation animation) {


                                                    }

                                                    @Override
                                                    public void onAnimationRepeat(Animation animation) {

                                                    }
                                                });

                                            }
                                        }
                                        super.onScrollStateChanged(recyclerView, newState);
                                    }

                                    @Override
                                    public void onLoadMore(int nextPageNo) {
                                        if (insuranceQuoteModel.getHasNext() == 1) {
                                            makeQuoteListRequest(false, nextPageNo);
                                        }
                                    }
                                });
                            } else {
                                // count mismatch for total records
                                showNoRecordsMessage(showFullPageError, insuranceQuoteModel.getError());
                            }
                        } else {
                            showNoRecordsMessage(showFullPageError, insuranceQuoteModel.getError());

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                       /* if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }*/
                        proggressBarHiding();
                        if (stopRequest) {
                            return;
                        }
                        switch (error.getKind()) {
                            case NETWORK:
                                showNetworkConnectionErrorLayout(showFullPageError);
                                break;

                            case UNEXPECTED:
                                showServerErrorLayout(showFullPageError);
                                break;

                            case HTTP:
                                showServerErrorLayout(showFullPageError);
                                break;

                            case CONVERSION:
                                showNoRecordsMessage(showFullPageError, "Application Error.");
                                break;
                        }
                        //CommonUtils.showErrorToast(getFragmentActivity(), error, Toast.LENGTH_SHORT);
                    }
                });

    }
private void proggressBarHiding()
{
    if (quouteFrameError != null) {
        quouteFrameError.removeAllViews();
        if (quouteFrameError.getVisibility() == View.VISIBLE)
        {
            quouteFrameError.setVisibility(View.GONE);
        }

    }
}

    private void showNetworkConnectionErrorLayout(boolean fullPageError) {

        /*alternativeLayout.removeAllViews();*/
        if (fullPageError) {
            proggressBarHiding();

           /* if (gcProgressDialog != null)
            {
                gcProgressDialog.dismiss();
            }*/

            View view = mInflater.inflate(R.layout.layout_error, null, false);
           /* if (steps != 0)
            {
                quouteFrameError.setVisibility(View.VISIBLE);
                quouteFrameError.addView(view);

            }else {*/
           /* if (checkStaps)
            {
*/
                inSideCheckErrorView(view);
           /* }else {

                setInitialView();
                hideProgressBar();
                alternativeLayout.addView(view);
            }*/


           // }


            errorMessage = (TextView) view.findViewById(R.id.errorMessage);
            errorMessage.setText(R.string.network_connection_error_message);

            retry = (Button) view.findViewById(R.id.retry);
            retry.setOnClickListener(this);

        } else {
            CommonUtils.showToast(DealerQuoteScreen.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
        }
    }


    private void initializeList(InsuranceQuotesModel response) {
        if (response != null) {
            insuranceQuotes = response.getQuoteList();
            mProcessId = response.getProcessId();
            carId = response.getCarId();

            String string = response.getIdvRange();
            String[] parts = string.split("-");
            String part1 = parts[0]; // 004
            String part2 = parts[1];
            if (steps == 0) {
                minValue = Integer.parseInt(part1);
                maxValue = Integer.parseInt(part2);
                idvSteps = Integer.parseInt(response.getSliderSteps());
                fab.setVisibility(View.VISIBLE);
                setFabCounter(count);
            }
        }
        layoutContainer.removeAllViews();
        View layoutView = mInflater.inflate(R.layout.layout_dealer_quoute, null, false);
        FrameLayout quotesLayoutContainer = (FrameLayout) layoutView.findViewById(R.id.quotesFrameLayout);

        /*setMaxValue();
            setMinValue(Integer.parseInt(part2));
           setIdvSteps(Integer.parseInt(response.getSliderSteps()));
*/
        if(mCaseSelected.equalsIgnoreCase(Constants.INSPECTED_CARS))
        {
            View view = mInflater.inflate(R.layout.layout_dealer_quote_inspected_cars, null, false);
            quotesLayoutContainer.addView(view);
            tv_makeModel = (TextView)view.findViewById(R.id.tv_makeModel);
            tv_regNo = (TextView)view.findViewById(R.id.tv_regNo);
            tv_regYear = (TextView)view.findViewById(R.id.tv_regYear);
            inspectedCarImage = (ImageView)view.findViewById(R.id.stockImage);
            tv_fuelType = (TextView)view.findViewById(R.id.tv_fuelType);
            tv_Price = (TextView)view.findViewById(R.id.tv_Price);
            iv_makeLogo = (ImageView)view.findViewById(R.id.makeLogo);
            tv_month = (TextView)view.findViewById(R.id.tv_kmsDriven);
            iv_color = (ImageView)view.findViewById(R.id.iv_color);

            tv_makeModel.setText(insuranceInspectedCarData.getModel() +" "+insuranceInspectedCarData.getVersion());
            tv_regNo.setText("Reg. No. " + insuranceInspectedCarData.getRegNo());
            tv_regYear.setText(insuranceInspectedCarData.getRegYear());
            tv_fuelType.setText(insuranceInspectedCarData.getFuelType());
            iv_makeLogo.setImageResource(ApplicationController.makeLogoMap.get(insuranceInspectedCarData.getMakeId()));



            if(mCaseSelected.equalsIgnoreCase(Constants.INSPECTED_CARS))
            {
               // tv_Price.setVisibility(View.VISIBLE);
                tv_Price.setText(insuranceInspectedCarData.getPrice());
                tv_month.setVisibility(View.VISIBLE);
                iv_color.setVisibility(View.VISIBLE);
                tv_month.setText(ApplicationController.monthShortMap.get(insuranceInspectedCarData.getRegMonth()) );
                if(insuranceInspectedCarData.getColour()!=null && !insuranceInspectedCarData.getColour().equals("")) {
                    GradientDrawable bgShape = (GradientDrawable) iv_color.getBackground();
                    bgShape.setColor(Color.parseColor(insuranceInspectedCarData.getColour()));
                }
                else{
                    GradientDrawable bgShape = (GradientDrawable) iv_color.getBackground();
                    bgShape.setColor(Color.parseColor("#FFFFFF"));
                }
                inspectedCarImage.setVisibility(View.VISIBLE);
                if (( insuranceInspectedCarData.getImage() != null) && !insuranceInspectedCarData.getImage().isEmpty()) {
                    Glide.with(ApplicationController.getInstance())
                            .load(insuranceInspectedCarData.getImage())
                            .placeholder(R.drawable.image_load_default_small)
                            .crossFade()
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(inspectedCarImage);

                } else {

                    Glide.with(ApplicationController.getInstance())
                            .load(R.drawable.no_image_default_small)
                            .centerCrop()
                            .into(inspectedCarImage);

                }

            }
        }


        else
        {

            View view = mInflater.inflate(R.layout.layout_dealer_quote_for_other_cars, null, false);
            quotesLayoutContainer.addView(view);
            tv_makeModel = (TextView)view.findViewById(R.id.tv_makeModel);
            tv_regNo = (TextView)view.findViewById(R.id.tv_regNo);
            tv_regYear = (TextView)view.findViewById(R.id.tv_regYear);
            tv_fuelType = (TextView)view.findViewById(R.id.tv_fuelType);
            iv_makeLogo = (ImageView)view.findViewById(R.id.makeLogo);
            tv_makeModel.setText(insuranceInspectedCarData.getModel() +" "+insuranceInspectedCarData.getVersion());
            tv_regNo.setText("Reg. No. " + insuranceInspectedCarData.getRegNo());
            tv_regYear.setText(insuranceInspectedCarData.getRegYear());
            tv_fuelType.setText(insuranceInspectedCarData.getFuelType());
            iv_makeLogo.setImageResource(ApplicationController.makeLogoMap.get(insuranceInspectedCarData.getMakeId()));
        }




        layoutContainer.addView(layoutView);

        quouteFrameError= (FrameLayout) layoutContainer.findViewById(R.id.quotesFrameLayoutError);

        if (response != null) {
            recList = (RecyclerView) layoutContainer.findViewById(R.id.rv);
            if (recList.getVisibility() == View.GONE) {
                recList.setVisibility(View.VISIBLE);
            }
            recList.setHasFixedSize(true);
            llm = new LinearLayoutManager(DealerQuoteScreen.this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recList.setLayoutManager(llm);
            dealerQuoteAdapter = new DealerQuoteAdapter(DealerQuoteScreen.this,
                    insuranceQuotes, mProcessId, mCaseSelected,
                    insuranceInspectedCarData, mAgentId);

            recList.setAdapter(dealerQuoteAdapter);
        }
    }


    private void showNoRecordsMessage(boolean showFullPageError, String message) {
        if (showFullPageError) {


          /*  if (gcProgressDialog != null)
            {
                gcProgressDialog.dismiss();
            }
*/
            proggressBarHiding();
            View view = mInflater.inflate(R.layout.layout_error, null, false);
          /*  if (steps != 0)
            {
                quouteFrameError.setVisibility(View.VISIBLE);
                quouteFrameError.addView(view);

            }else {*/
           /* if (checkStaps)
            {*/
                inSideCheckErrorView(view);
           /* }else {
                setInitialView();
                hideProgressBar();

                alternativeLayout.addView(view);
            }*/
           // }
            refineFilter = (TextView) view.findViewById(R.id.checkconnection);
           // refineFilter.setText(R.string.refine_filter);
            refineFilter.setVisibility(View.INVISIBLE);
            img = (ImageView) view.findViewById(R.id.no_internet_img);

            img.setImageResource(R.drawable.no_result_icons);
            errorMessage = (TextView) view.findViewById(R.id.errorMessage);
            errorMessage.setText(message);
            if (steps !=0 || checkItemPosition !=0 || isZeroDep !=0 ) {
                if (fab.getVisibility() !=View.VISIBLE) {
                    fab.setVisibility(View.VISIBLE);
                    setFabCounter(count);
                }
            }

            retry = (Button) view.findViewById(R.id.retry);
            retry.setVisibility(View.GONE);

        } else {
            CommonUtils.showToast(DealerQuoteScreen.this, "No more records found", Toast.LENGTH_SHORT);

        }

    }


    private void showServerErrorLayout(boolean showFullPageError) {
       /* if (steps == 0)
        {*/

        //}


        if (showFullPageError) {
            View view = mInflater.inflate(R.layout.layout_error, null, false);
            /*alternativeLayout.removeAllViews();*/
           /* if (steps != 0)
            {
                quouteFrameError.setVisibility(View.VISIBLE);
                quouteFrameError.addView(view);

            }else {*/
          /*  if (checkStaps) {
                inSideCheckErrorView(view);
            }else {

                setInitialView();
                hideProgressBar();
                alternativeLayout.addView(view);
            }*/
           // }
            inSideCheckErrorView(view);


            errorMessage = (TextView) view.findViewById(R.id.errorMessage);
            errorMessage.setText(R.string.no_internet_connection);

            retry = (Button) view.findViewById(R.id.retry);
            retry.setOnClickListener(this);

        } else {
            CommonUtils.showToast(DealerQuoteScreen.this, DealerQuoteScreen.this.getString(R.string.server_error), Toast.LENGTH_SHORT);
        }
    }

    private void hideProgressBar() {
        if (gcProgressDialog != null) {
           // progressBar.setVisibility(View.GONE);
            gcProgressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.retry:
               /* if (steps==0)
                {*/

               // }
             /*   if (checkStaps)
                {*/
                    inSideProgressView();
                    makeQuoteListRequest(true, 1);
                /*}else {
                    setInitialView();
                    makeQuoteListRequest(true, 1);
                }*/



                break;
            case R.id.fab:
                if (idvSteps == 0) {
                    idvSteps = 40;
                }
                Intent intent = new Intent(DealerQuoteScreen.this, InsuranceFilter.class);
                startActivityForResult(intent,FILTER_REQUEST);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case Constants.INSURANCE_CASE_FOR_INSPECTED_CAR:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                    finishActivity(Constants.INSURANCE_CASE_FOR_INSPECTED_CAR);
                }
                break;

            case FILTER_REQUEST:
                if (resultCode == RESULT_OK) {
                    count = 0;
                    if (data.getStringExtra("selectedIdv") != null) {
                        String selectedIdvFromFilter = data.getStringExtra("selectedIdv");
                        //steps = Integer.parseInt(data.getStringExtra("steps"));
                       // checkStaps = data.getBooleanExtra("checkStaps",false);
                     /*   if (!selectedIdvFromFilter.equalsIgnoreCase(selectedIdv)) {
                            params.clear();*/

                        selectedIdv = selectedIdvFromFilter;


                        params.put("selected_idv", selectedIdvFromFilter);
                        params.put("insurance_case_id", DealerQuoteAdapter.insurance_case_id);
                        params.put("isZeroDep", String.valueOf(isZeroDep));

                        if (isZeroDep == 1) {
                            params.put("isZeroDep_C", String.valueOf(isZeroDep_C));
                            params.put("isZeroDep_H", String.valueOf(isZeroDep_H));
                        }
                        params.put("voluntaryDedAmt", "" + deductible_premiums);
                        params.put(Constants.PROCESS_ID, mProcessId);
                        inSideProgressView();

                        stopRequest = false;
                        makeQuoteListRequest(true, 1);

                        if (isZeroDep > 0) {
                            count++;
                        }
                        if (deductible_premiums >= 2500) {
                            count++;
                        }
                        if (steps > 0) {
                            count++;
                        }

                       /* }*/
                    }

                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onResume() {
        super.onResume();
        ApplicationController.getEventBus().register(this);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (checkStaps)
            {
                quouteFrameError.removeAllViews();
                quouteFrameError.setClickable(false);
                if (fab.getVisibility()!= View.VISIBLE) {
                    fab.setVisibility(View.VISIBLE);
                    setFabCounter(count);
                }
                else {
                    onBackPressed();
                }
                return true;
            }
            else {
                onBackPressed();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ApplicationController.getEventBus().unregister(this);
    }

    @Subscribe
    public void onInsuranceInspectedCarSelectedEvent(InsuranceInspectedCarSelectedEvent event) {
        GCLog.e("insurance inspected car: " + event.toString());
        if(event.getQuoteDetails()!=null) {
            stopRequest = true;
            Intent intent = new Intent(DealerQuoteScreen.this, InsuranceCustomerDetailScreen.class);

            intent.putStringArrayListExtra(Constants.INSURANCE_CITIES, mInsuranceCities);
            intent.putExtra(Constants.INSURANCE_IS_NEW_CAR, mIsNewCar);
            intent.putExtra(Constants.FINANCE_COMPANY, financeCompanyList);

            intent.putExtra(Constants.QUOTE_DETAILS, event.getQuoteDetails());
            intent.putExtra(Constants.PREMIUM_AMOUNT, event.getQuoteDetails().getNetPremium());
            intent.putExtra(Constants.INSURER_ID, "" + event.getQuoteDetails().getInsurerID());
            intent.putExtra(Constants.SELECTED_CASE, mCaseSelected);
            intent.putExtra(Constants.INSURANCE_INSPECTED_CAR_DATA, insuranceInspectedCarData);
            intent.putExtra(Constants.AGENT_ID, mAgentId);
            intent.putExtra(Constants.PROCESS_ID, mProcessId);
            intent.putExtra(Constants.CAR_ID, carId);
            startActivityForResult(intent, Constants.INSURANCE_CASE_FOR_INSPECTED_CAR);
            CommonUtils.startActivityTransition(DealerQuoteScreen.this, Constants.TRANSITION_LEFT);
        }
    }
}
