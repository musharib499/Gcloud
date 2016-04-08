package com.gcloud.gaadi.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.BuildConfig;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.chat.DealerChatListActivity;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.insurance.NewInsuranceDashboard;
import com.gcloud.gaadi.model.FinanceCompany;
import com.gcloud.gaadi.model.InsuranceAvailabilityModel;
import com.gcloud.gaadi.model.PreviousPolicyInsurer;
import com.gcloud.gaadi.model.RSAAvailabilityModel;
import com.gcloud.gaadi.model.SFAUserRight;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.rsa.RSADashboardActivity;
import com.gcloud.gaadi.service.KnowlarityContactService;
import com.gcloud.gaadi.sfa.SfaCampaignTrackingReceiver;
import com.gcloud.gaadi.ui.Finance.GaadiFinanceActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.gcloud.gaadi.utils.ObjectSerializer;
import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ankit on 19/11/14.
 */
public class MainFragment extends Fragment implements View.OnClickListener {

    private GCProgressDialog progressDialog;
    private RelativeLayout
            viewStocks,
            addStock,
            viewLeads,
            addLead,
            dealerPlatform,
            chatLogs,
            gaadiwarranty,
            issueFinance,
            insurancePolicy,
            roadSideAssistance,usedCarValuationLayout;

    private LinearLayout manageLeadSubMenu;

    //    private GAHelper gaHelper;
    private Activity mActivity;
    private View rootView;
    private String agentId = "";
    private ArrayList<String> insuranceCities;
    private ArrayList<FinanceCompany> financeCompanyList;

    private ArrayList<InsuranceAvailabilityModel.KnowlarityNumberModel> knowlarityNumbers;
    private String mUserRights = "";
    private HashMap<String, SFAUserRight> sfaUserRightsMap = new HashMap<String, SFAUserRight>();

    public MainFragment() {

    }

    public MainFragment setProgressDialog(GCProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
        return this;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(Constants.INSURANCE_CITIES, insuranceCities);
        outState.putSerializable(Constants.FINANCE_COMPANY, financeCompanyList);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();

        /*try {
            // only to dealers.
            //if (!CommonUtils.getBooleanSharedPreference(getFragmentActivity(), Constants.SERVICE_EXECUTIVE_LOGIN, false)) {
                checkRSAAvailability();
            //}
        } catch (Exception e) {
            GCLog.e("exception: " + e.getMessage());
        }*/

//        FinanceUtils.getBanksData();
//        FinanceUtils.getCompaniesData();
//        FinanceUtils.getEmploymentData();

        if ("GAADI".equalsIgnoreCase(BuildConfig.CLOUD_OWNER)) { //allowing only for gaadi dealers
            try {
                GCLog.e("checking rsa availability");
                //if (!CommonUtils.getBooleanSharedPreference(getFragmentActivity(), Constants.SERVICE_EXECUTIVE_LOGIN, false)) {
                checkInsuranceFinanceAvailability();
                //}
            } catch (Exception e) {
                GCLog.e("exception: " + e.getMessage());
            }
        /*MakeModelVersionDB makeModelVersionDB = ApplicationController.getMakeModelVersionDB();
        GCLog.e(Constants.TAG, "mmv data : " + makeModelVersionDB.getRecords().toString());*/


        }

        // This could be improved. Maybe we can do away with SFAUserRight map.
        mUserRights = CommonUtils.getStringSharedPreference(getFragmentActivity(), SfaCampaignTrackingReceiver.EXTRA_ACCESS_RIGHTS, "");
        SFAUserRight[] sfaUserRights = new Gson().fromJson(mUserRights, SFAUserRight[].class);
        if (sfaUserRights != null) {
            for (int i = 0; i < sfaUserRights.length; ++i) {
                sfaUserRightsMap.put(sfaUserRights[i].getUserRightId(), sfaUserRights[i]);
            }
            GCLog.e("sfa user rights: " + mUserRights);
        }

    }

    /*@Override
    public void onResume() {
        super.onResume();
        ApplicationController.getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        ApplicationController.getEventBus().unregister(this);
    }*/

    public void startKnowlarityService(Intent intent) {
        if (MainActivity.START_KNOWLARITY_SERVICE.equalsIgnoreCase(intent.getAction())) {
            Intent knowlarityContactIntent = new Intent(getFragmentActivity(), KnowlarityContactService.class);
            knowlarityContactIntent.putExtra("contacts", knowlarityNumbers);
            if (getFragmentActivity().startService(knowlarityContactIntent) == null) {
                GCLog.e("Knowlarity Service has not been started");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION_CONTACTS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startKnowlarityService(new Intent(MainActivity.START_KNOWLARITY_SERVICE));
            }
        }
    }

    private void checkInsuranceFinanceAvailability() throws Exception {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.METHOD_LABEL, Constants.DEALER_INSURANCE_VALIDITY);

        RetrofitRequest.getInsuranceAvailability(params, new Callback<InsuranceAvailabilityModel>() {

            @Override
            public void success(InsuranceAvailabilityModel insuranceAvailabilityModel, Response response) {
                //GCLog.e(insuranceAvailabilityModel.toString());
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }

                if ("T".equalsIgnoreCase(insuranceAvailabilityModel.getStatus())) {
                    CommonUtils.setIntSharedPreference(getFragmentActivity(), Constants.IS_LMS, insuranceAvailabilityModel.getIsLMS());
                    CommonUtils.setIntSharedPreference(getFragmentActivity(), Constants.IS_SELLER, insuranceAvailabilityModel.getIsSeller());

                    if (!"0".equals(insuranceAvailabilityModel.getInsuranceAvailability())) {
                        //  if (!"0".equals(insuranceAvailabilityModel.getAgentId())) {
                        insurancePolicy.setVisibility(View.VISIBLE);
                        rootView.findViewById(R.id.insuranceSeparator).setVisibility(View.VISIBLE);
                        agentId = insuranceAvailabilityModel.getAgentId();
                        insuranceCities = insuranceAvailabilityModel.getCities();
                        financeCompanyList = insuranceAvailabilityModel.getFinanceCompany();
                        ArrayList<PreviousPolicyInsurer> previousPolicyInsurersList = insuranceAvailabilityModel.getPrevPolicyInsurer();
                        Gson gson = new Gson();
                        String prevPolicyInsurerStr = gson.toJson(previousPolicyInsurersList);
                        CommonUtils.setStringSharedPreference(getFragmentActivity(), Constants.PREV_POLICY_INSURER_LIST, prevPolicyInsurerStr);

                        ArrayList<String> bankList = insuranceAvailabilityModel.getBankList();
                        String bankListStr = gson.toJson(bankList);
                        CommonUtils.setStringSharedPreference(getFragmentActivity(), Constants.BANK_LIST, bankListStr);

                        CommonUtils.setBooleanSharedPreference(getFragmentActivity(), Constants.INSURANCE_DEALER, true);
                        CommonUtils.setIntSharedPreference(getFragmentActivity(), Constants.IS_CASH, insuranceAvailabilityModel.getIsCash());
                       /* CommonUtils.setIntSharedPreference(getFragmentActivity(), Constants.IS_LMS, insuranceAvailabilityModel.getIsLMS());
                        CommonUtils.setIntSharedPreference(getFragmentActivity(), Constants.IS_SELLER, insuranceAvailabilityModel.getIsSeller());
                       */
                        try {
                            CommonUtils.setStringSharedPreference(getFragmentActivity(), Constants.INSURANCE_CITIES,
                                    ObjectSerializer.serialize(insuranceCities));

                        } catch (IOException e) {
                            GCLog.e("serialization exception: " + e.getMessage());

                        }
                        GCLog.e("insurance cities: " + insuranceCities);

                    }
                } else {
                    insurancePolicy.setVisibility(View.GONE);
                    rootView.findViewById(R.id.insuranceSeparator).setVisibility(View.GONE);
//                        issueFinance.setVisibility(View.GONE);
//                        rootView.findViewById(R.id.financeFooter).setVisibility(View.GONE);
                }

                //  }
                if (insuranceAvailabilityModel.isRsa()) {
                    CommonUtils.setBooleanSharedPreference(getFragmentActivity(), Constants.RSA_DEALER, true);
                    roadSideAssistance.setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.rsaSeparator).setVisibility(View.VISIBLE);
                    ///rootView.findViewById(R.id.rsaSeparator).setVisibility(View.GONE);

                } else {
                    roadSideAssistance.setVisibility(View.GONE);
                    rootView.findViewById(R.id.rsaSeparator).setVisibility(View.GONE);

                }


                if ("1".equals(insuranceAvailabilityModel.getFinanceAvailability())) {
                    CommonUtils.setBooleanSharedPreference(getFragmentActivity(), Constants.FINANCE_DEALER, true);
                    GCLog.e("Loan eligible dealer");
                    issueFinance.setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.financeFooter).setVisibility(View.VISIBLE);
                } else {
                    issueFinance.setVisibility(View.GONE);
                    rootView.findViewById(R.id.financeFooter).setVisibility(View.GONE);
                    //rootView.findViewById(R.id.rsaSeparator).setVisibility(View.GONE);

                }

                // To maintain server time difference to prevent time difference errors
                CommonUtils.setStringSharedPreference(getFragmentActivity(),
                        Constants.SERVER_TIME, insuranceAvailabilityModel.getServerTime());
                CommonUtils.setLongSharedPreference(getFragmentActivity(), Constants.SERVER_TIME_DIFFERENCE,
                        CommonUtils.SQLTimeToMillis(insuranceAvailabilityModel.getServerTime()) - new DateTime().getMillis());

                // Add/Update Knowlarity contact details
                knowlarityNumbers = insuranceAvailabilityModel.getKnowlarityNumbers();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !CommonUtils.checkForPermission(getFragmentActivity(),
                        new String[]{Manifest.permission.WRITE_CONTACTS},
                        Constants.REQUEST_PERMISSION_CONTACTS, "Contacts")) {
                    return;
                }
                startKnowlarityService(new Intent(MainActivity.START_KNOWLARITY_SERVICE));
            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }

                if (error.getCause() instanceof UnknownHostException) {
                    //showNetworkConnectionErrorDialog();

                } else {

                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.ACTION_LAUNCH,
                            error.getUrl() + " " + error.getMessage(),
                            0);

                    //showServerErrorDialog();

                }
                GCLog.e("error: " + error.getMessage());
            }
        });
    }

    private void checkRSAAvailability() throws Exception {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.METHOD_LABEL, Constants.VERIFY_DEALER_RSA);

        RetrofitRequest.getRSAAvailability(jsonObject, new Callback<RSAAvailabilityModel>() {

            @Override
            public void success(RSAAvailabilityModel rsaAvailabilityModel, Response response) {
                //GCLog.e(rsaAvailabilityModel.toString());
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }

                if ("T".equalsIgnoreCase(rsaAvailabilityModel.getStatus())) {
                    CommonUtils.setBooleanSharedPreference(getFragmentActivity(), Constants.RSA_DEALER, true);
                    roadSideAssistance.setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.rsaSeparator).setVisibility(View.VISIBLE);

                } else {
                    roadSideAssistance.setVisibility(View.GONE);
                    rootView.findViewById(R.id.rsaSeparator).setVisibility(View.GONE);

                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }

                if (error.getCause() instanceof UnknownHostException) {
                    //showNetworkConnectionErrorDialog();

                } else {

                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.ACTION_LAUNCH,
                            error.getUrl() + " " + error.getMessage(),
                            0);

                    //showServerErrorDialog();

                }
                GCLog.e("error: " + error.getMessage());
            }
        });
    }

    private void showProgressDialog() {
        if (!getFragmentActivity().isFinishing()
                && progressDialog != null
                && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private Activity getFragmentActivity() {
        return mActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setRetainInstance(true);

        manageLeadSubMenu = (LinearLayout) rootView.findViewById(R.id.manage_lead_sub_menu);

        viewStocks = (RelativeLayout) rootView.findViewById(R.id.viewStock);
        viewStocks.setOnClickListener(this);

        addStock = (RelativeLayout) rootView.findViewById(R.id.addStock);
        addStock.setOnClickListener(this);

        viewLeads = (RelativeLayout) rootView.findViewById(R.id.viewLeads);
        //viewLeads.setOnClickListener(this);
        rootView.findViewById(R.id.manage_lead_linear_layout).setOnClickListener(this);
        rootView.findViewById(R.id.buyer_lead).setOnClickListener(this);
        rootView.findViewById(R.id.seller_leads).setOnClickListener(this);

        addLead = (RelativeLayout) rootView.findViewById(R.id.addLead);
        addLead.setOnClickListener(this);

        dealerPlatform = (RelativeLayout) rootView.findViewById(R.id.dealerPlatform);
        dealerPlatform.setOnClickListener(this);

        gaadiwarranty = (RelativeLayout) rootView.findViewById(R.id.gaadiwarranty);
        gaadiwarranty.setOnClickListener(this);
        if (!"GAADI".equalsIgnoreCase(BuildConfig.CLOUD_OWNER)) {
            gaadiwarranty.setVisibility(View.GONE);
        }

        issueFinance = (RelativeLayout) rootView.findViewById(R.id.gaadiFinance);
        issueFinance.setOnClickListener(this);

        roadSideAssistance = (RelativeLayout) rootView.findViewById(R.id.gaadiRSA);
        roadSideAssistance.setOnClickListener(this);

        chatLogs = (RelativeLayout) rootView.findViewById(R.id.chatLogs);
        chatLogs.setOnClickListener(this);

        insurancePolicy = (RelativeLayout) rootView.findViewById(R.id.insurancePolicy);
        insurancePolicy.setOnClickListener(this);

        usedCarValuationLayout = (RelativeLayout) rootView.findViewById(R.id.usedCarValuationLayout);
        usedCarValuationLayout.setOnClickListener(this);
        if (!"GAADI".equalsIgnoreCase(BuildConfig.CLOUD_OWNER)) {
            usedCarValuationLayout.setVisibility(View.GONE);
        }

        if (CommonUtils.getBooleanSharedPreference(getFragmentActivity(), Constants.SERVICE_EXECUTIVE_LOGIN, false)) {
            chatLogs.setVisibility(View.GONE);
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Constants.INSURANCE_CITIES)) {
                insuranceCities = savedInstanceState.getStringArrayList(Constants.INSURANCE_CITIES);
                financeCompanyList = (ArrayList<FinanceCompany>) savedInstanceState.getSerializable(Constants.FINANCE_COMPANY);
            }
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        gaHelper = new GAHelper(getFragmentActivity());
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        String warrantyOnlyDealer = CommonUtils.getStringSharedPreference(getFragmentActivity(), Constants.UC_WARRANTY_ONLY_DEALER, "0");
        switch (v.getId()) {
            case R.id.viewStock:

                if ((sfaUserRightsMap.get("3") == null) || sfaUserRightsMap.get("3").getIsUserRightEnabled().equalsIgnoreCase("1")) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(
                            GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.ACTION_TAP,
                            Constants.LABEL_VIEW_STOCK,
                            0);
                    intent = new Intent(getFragmentActivity(), StocksActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    CommonUtils.startActivityTransition(getFragmentActivity(), Constants.TRANSITION_LEFT);
                } else {
                    CommonUtils.showToast(getFragmentActivity(), getString(R.string.sfa_no_permission), Toast.LENGTH_SHORT);
                }
                break;

            case R.id.addStock:

                if ((sfaUserRightsMap.get("4") == null) || sfaUserRightsMap.get("4").getIsUserRightEnabled().equalsIgnoreCase("1")) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(
                            GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.ACTION_TAP,
                            Constants.LABEL_ADD_STOCK,
                            0);
                    intent = new Intent(getFragmentActivity(), StockAddActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    CommonUtils.startActivityTransition(getFragmentActivity(), Constants.TRANSITION_LEFT);
                } else {

                    // setting
                    CommonUtils.setBooleanSharedPreference(getFragmentActivity(), Constants.CAN_EDIT, false);
                    CommonUtils.showToast(getFragmentActivity(), getString(R.string.sfa_no_permission), Toast.LENGTH_SHORT);
                }
                break;


            case R.id.gaadiwarranty:

                if ((sfaUserRightsMap.get("12") == null) || sfaUserRightsMap.get("12").getIsUserRightEnabled().equalsIgnoreCase("1")) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(
                            GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.ACTION_TAP,
                            Constants.LABEL_WARRANTY,
                            0);
                    intent = new Intent(getFragmentActivity(), GaadiWarrantyActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    CommonUtils.startActivityTransition(getFragmentActivity(), Constants.TRANSITION_LEFT);
                } else {
                    CommonUtils.showToast(getFragmentActivity(), getString(R.string.sfa_no_permission), Toast.LENGTH_SHORT);
                }
                break;

            //case R.id.viewLeads:
            case R.id.manage_lead_linear_layout:
                ApplicationController.getInstance().getGAHelper().sendEvent(
                        GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_HOME_PAGE,
                        Constants.CATEGORY_HOME_PAGE,
                        Constants.ACTION_TAP,
                        Constants.LABEL_MANAGE_LEAD,
                        0);


               /* if ("0".equals(warrantyOnlyDealer)) {
                    intent = new Intent(getFragmentActivity(), SellerLeadsActivity.class);
                    startActivity(intent);
                    CommonUtils.startActivityTransition(getFragmentActivity(), Constants.TRANSITION_LEFT);
                } else {
                    CommonUtils.showToast(getFragmentActivity(), getFragmentActivity().getString(R.string.warranty_only_dealer_message), Toast.LENGTH_SHORT);
                }*/
                TransitionDrawable transition = (TransitionDrawable) viewLeads.getBackground();
                if (CommonUtils.getIntSharedPreference(getFragmentActivity(), Constants.IS_SELLER, 0) == 1) {
                    if (manageLeadSubMenu.getVisibility() == View.VISIBLE) {
                        transition.reverseTransition(0);
                        /*TranslateAnimation collapse = new TranslateAnimation(
                                Animation.RELATIVE_TO_SELF, 0f,
                                Animation.RELATIVE_TO_SELF, -1f,
                                Animation.RELATIVE_TO_PARENT, 0f,
                                Animation.RELATIVE_TO_PARENT, 0f
                        );
                        collapse.setDuration(200);
                        collapse.setFillAfter(true);
                        collapse.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {*/
                        manageLeadSubMenu.setVisibility(View.GONE);
                        //viewLeads.setBackgroundColor(ContextCompat.getColor(getFragmentActivity(), android.R.color.white));
                            /*}

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        manageLeadSubMenu.startAnimation(collapse);*/

                    } else {
                        manageLeadSubMenu.setVisibility(View.VISIBLE);
                        transition.startTransition(0);
                        //viewLeads.setBackgroundColor(ContextCompat.getColor(getFragmentActivity(), R.color.insurance_light_gray));
                        /*TranslateAnimation expand = new TranslateAnimation(
                                Animation.RELATIVE_TO_SELF, -1f,
                                Animation.RELATIVE_TO_SELF, 0f,
                                Animation.RELATIVE_TO_PARENT, 0f,
                                Animation.RELATIVE_TO_PARENT, 0f
                        );
                        expand.setDuration(700);
                        //expand.setFillAfter(true);
                        manageLeadSubMenu.startAnimation(expand);*/
                    }
                } else {
                    // buyer lead access
                    intent = new Intent(getActivity(), LeadsManageActivity.class);
                } /*else if (CommonUtils.getIntSharedPreference(getActivity(), Constants.IS_SELLER, 0) == 1) {
                    // seller leads access
                    intent = new Intent(getActivity(), SellerLeadsActivity.class);
                }*/
                if (intent != null) {
                    startActivity(intent);
                    CommonUtils.startActivityTransition(getFragmentActivity(), Constants.TRANSITION_LEFT);
                }
                break;

            case R.id.buyer_lead:
                intent = new Intent(getActivity(), LeadsManageActivity.class);
                onClick(rootView.findViewById(R.id.manage_lead_linear_layout));
                final Intent finalIntent = intent;
                viewLeads.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(finalIntent);
                        CommonUtils.startActivityTransition(getFragmentActivity(), Constants.TRANSITION_LEFT);
                    }
                }, 200);
                break;

            case R.id.seller_leads:
                intent = new Intent(getActivity(), SellerLeadsActivity.class);
                onClick(rootView.findViewById(R.id.manage_lead_linear_layout));
                final Intent finalIntent1 = intent;
                viewLeads.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(finalIntent1);
                        CommonUtils.startActivityTransition(getFragmentActivity(), Constants.TRANSITION_LEFT);
                    }
                }, 200);
                break;

            case R.id.addLead:

                if ((sfaUserRightsMap.get("9") == null) || sfaUserRightsMap.get("9").getIsUserRightEnabled().equalsIgnoreCase("1")) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(
                            GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.ACTION_TAP,
                            Constants.LABEL_STOCK_DETAIL_ADD_LEAD,
                            0);
                /*if ("0".equals(warrantyOnlyDealer)) {*/
                    intent = new Intent(getFragmentActivity(), LeadAddOptionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    CommonUtils.startActivityTransition(getFragmentActivity(), Constants.TRANSITION_LEFT);
                /*} else {
                    CommonUtils.showToast(getFragmentActivity(), getFragmentActivity().getString(R.string.warranty_only_dealer_message), Toast.LENGTH_SHORT);
                }*/
                } else {
                    CommonUtils.showToast(getFragmentActivity(), getString(R.string.sfa_no_permission), Toast.LENGTH_SHORT);
                }
                break;

            case R.id.dealerPlatform:

                if ((sfaUserRightsMap.get("25") == null) || sfaUserRightsMap.get("25").getIsUserRightEnabled().equalsIgnoreCase("1")) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(
                            GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.ACTION_TAP,
                            Constants.LABEL_DEALER_PLATFORM,
                            0);
                    intent = new Intent(getFragmentActivity(), DealerPlatformActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    CommonUtils.startActivityTransition(getFragmentActivity(), Constants.TRANSITION_LEFT);
                } else {
                    CommonUtils.showToast(getFragmentActivity(), getString(R.string.sfa_no_permission), Toast.LENGTH_SHORT);
                }
                break;

            case R.id.gaadiFinance:

                if ((sfaUserRightsMap.get("20") == null) || sfaUserRightsMap.get("20").getIsUserRightEnabled().equalsIgnoreCase("1")) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(
                            GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.ACTION_TAP,
                            Constants.LABEL_ISSUE_FINANCE,
                            0
                    );
//                intent = new Intent(getFragmentActivity(), FinanceReviewDocumentsActivity.class);
//                intent = new Intent(getFragmentActivity(), FinanceCollectImagesActivity.class);
                    intent = new Intent(getFragmentActivity(), GaadiFinanceActivity.class);
//                intent = new Intent(getFragmentActivity(), FinanceFormsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    CommonUtils.startActivityTransition(getFragmentActivity(), Constants.TRANSITION_LEFT);
                } else {
                    CommonUtils.showToast(getFragmentActivity(), getString(R.string.sfa_no_permission), Toast.LENGTH_SHORT);
                }
                break;

            case R.id.gaadiRSA:

                if ((sfaUserRightsMap.get("11") == null) || sfaUserRightsMap.get("11").getIsUserRightEnabled().equalsIgnoreCase("1")) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(
                            GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.ACTION_TAP,
                            Constants.LABEL_ISSUE_RSA,
                            0
                    );
                    intent = new Intent(getFragmentActivity(), RSADashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    CommonUtils.startActivityTransition(getFragmentActivity(), Constants.TRANSITION_LEFT);
                } else {
                    CommonUtils.showToast(getFragmentActivity(), getString(R.string.sfa_no_permission), Toast.LENGTH_SHORT);
                }
                break;

            case R.id.insurancePolicy:

                if ((sfaUserRightsMap.get("21") == null) || sfaUserRightsMap.get("21").getIsUserRightEnabled().equalsIgnoreCase("1")) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(
                            GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.ACTION_TAP,
                            Constants.LABEL_INSURANCE_POLICY,
                            0
                    );

                    intent = new Intent(getFragmentActivity(), NewInsuranceDashboard.class);
                    intent.putExtra(Constants.AGENT_ID, agentId);
                    intent.putStringArrayListExtra(Constants.INSURANCE_CITIES, insuranceCities);
                    intent.putExtra(Constants.FINANCE_COMPANY, financeCompanyList);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    startActivity(intent);
                    CommonUtils.startActivityTransition(getFragmentActivity(), Constants.TRANSITION_LEFT);
                } else {
                    CommonUtils.showToast(getFragmentActivity(), getString(R.string.sfa_no_permission), Toast.LENGTH_SHORT);
                }
                break;

            case R.id.chatLogs:
                /**/
                ApplicationController.getInstance().getGAHelper().sendEvent(
                        GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_HOME_PAGE,
                        Constants.CATEGORY_HOME_PAGE,
                        Constants.ACTION_TAP,
                        Constants.LABEL_CHAT_LOGS,
                        0);
                intent = new Intent(getFragmentActivity(), DealerChatListActivity.class);
                startActivity(intent);
                CommonUtils.startActivityTransition(getFragmentActivity(), Constants.TRANSITION_LEFT);/*/
                startActivity(new Intent(getFragmentActivity(), PaymentModeScreen.class));
                /**/
                break;

            case R.id.usedCarValuationLayout:
                /**/

                if ((sfaUserRightsMap.get("14") == null) || sfaUserRightsMap.get("14").getIsUserRightEnabled().equalsIgnoreCase("1")) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(
                            GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.CATEGORY_HOME_PAGE,
                            Constants.ACTION_TAP,
                            Constants.LABEL_USED_CAR_VALUATION,
                            0);
                    intent = new Intent(getFragmentActivity(), UsedCarValuationActivity.class);
                    startActivity(intent);
                    CommonUtils.startActivityTransition(getFragmentActivity(), Constants.TRANSITION_LEFT);/*/

                startActivity(new Intent(getFragmentActivity(), PaymentModeScreen.class));
                /**/
                } else {
                    CommonUtils.showToast(getFragmentActivity(), getString(R.string.sfa_no_permission), Toast.LENGTH_SHORT);
                }
                break;
        }

        switch (v.getId()) {
            case R.id.manage_lead_linear_layout:
            case R.id.seller_leads:
            case R.id.buyer_lead:
                break;

            default:
                if (manageLeadSubMenu.getVisibility() == View.VISIBLE) {
                    onClick(rootView.findViewById(R.id.manage_lead_linear_layout));
                }
                break;
        }
    }

}
