package com.gcloud.gaadi.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.AddLeadsPagerAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.interfaces.CallLogsItemClickInterface;
import com.gcloud.gaadi.interfaces.CallTrackClickInterface;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.CallLogItem;
import com.gcloud.gaadi.model.CustomerModel;
import com.gcloud.gaadi.ui.viewpagerindicator.BaseActivityCollapsingToolbar;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


/**
 * Created by ankit on 21/11/14.
 */
public class LeadAddOptionActivity extends BaseActivityCollapsingToolbar implements
        OnClickListener, CallTrackClickInterface, CallLogsItemClickInterface,
        OnNoInternetConnectionListener {

    RelativeLayout tvFromCallTracker, tvFromRecentCallLogs, rl_addLead;
    //    LinearLayout llCarTrade, llOlx, llCarwale, llQuicker;
//    TextView cartradeCount, olxCount, quikerCount, CarwaleCount;
//
    //private ActionBar mActionBar;
    GCProgressDialog progressBar;
    ArrayList<String> smsList;
    private AddLeadsPagerAdapter adapter;
//    private GAHelper mGAHelper;
    private CallLogsDialogFragment callLogsDialogFragment;
    private CallTrackFragment calltrackDialogFragment;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getLayoutInflater().inflate(R.layout.activity_leadadd_option,frameLayout);
        //setContentView(R.layout.activity_leadadd_option);

//        mGAHelper = new GAHelper(this);
        //progressBar = new GCProgressDialog(this, this);
        //mActionBar = getSupportActionBar();
        //mActionBar.setDisplayHomeAsUpEnabled(true);

        //initializeViews();
        //setOnClicks();
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAddLeads);
        //setSupportActionBar(toolbar);

        //TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        //tabLayout.setVisibility(View.VISIBLE);

        /*tabLayout.addTab(tabLayout.newTab().setText("From Recent Call"));
        tabLayout.addTab(tabLayout.newTab().setText("From Call Tracker"));
        tabLayout.addTab(tabLayout.newTab().setText("Add New Lead"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new AddLeadsPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(CallLogsFragment.getInstance(this), "From Recent Call");
        adapter.addFrag(new CallTrackFragment(), "From Call Tracker");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent = null;

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_add_lead) {

            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                    Constants.CATEGORY_ADD_LEAD,
                    Constants.CATEGORY_ADD_LEAD,
                    Constants.ACTION_TAP,
                    Constants.LABEL_ADD_LEAD_BUTTON,
                    0);
            intent = new Intent(this, LeadAddActivity.class);
            Bundle args = new Bundle();
            args.putString(Constants.CALL_SOURCE, "NL");
            intent.putExtras(args);
            startActivity(intent);
            return true;

        } else if (id == android.R.id.home) {

            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                    Constants.CATEGORY_ADD_LEAD,
                    Constants.CATEGORY_ADD_LEAD,
                    Constants.ACTION_TAP,
                    Constants.LABEL_BACK_BUTTON,
                    0);
            onBackPressed();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void setOnClicks() {

        tvFromCallTracker.setOnClickListener(this);
        tvFromRecentCallLogs.setOnClickListener(this);
        rl_addLead.setOnClickListener(this);
       /* llCarTrade.setOnClickListener(this);
        llOlx.setOnClickListener(this);
        llCarwale.setOnClickListener(this);
        llQuicker.setOnClickListener(this);*/

        // tvFromRecentCallLogs

    }

    /*private void initializeViews() {

        tvFromCallTracker = (RelativeLayout) findViewById(R.id.fromCallTracker);
        tvFromRecentCallLogs = (RelativeLayout) findViewById(R.id.fromRecentcalls);
        rl_addLead = (RelativeLayout) findViewById(R.id.rl_addLead);
      *//*  llCarTrade = (LinearLayout) findViewById(R.id.cartrade);
        llCarwale = (LinearLayout) findViewById(R.id.Carwale);
        llOlx = (LinearLayout) findViewById(R.id.OLX);
        llQuicker = (LinearLayout) findViewById(R.id.Quiker);
        llCarwale.setOnClickListener(this);
        llOlx.setOnClickListener(this);
        llQuicker.setOnClickListener(this);
        llCarTrade.setOnClickListener(this);
        cartradeCount = (TextView) findViewById(R.id.cartradeCount);
        olxCount = (TextView) findViewById(R.id.olxCount);
        quikerCount = (TextView) findViewById(R.id.quikerCount);
        CarwaleCount = (TextView) findViewById(R.id.CarwaleCount);*//*

    }*/

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_ADD_LEAD);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        //switch (v.getId()) {
            /* case R.id.cartrade:
                mGAHelper.sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_ADD_LEAD,
                        Constants.CATEGORY_ADD_LEAD,
                        Constants.ACTION_TAP,
                        Constants.LABEL_CARTRADE,
                        0);
                getSMS(Constants.CARTRADE);
                smsApiCall(Constants.CARTRADE, cartradeCount);
                break;

            case R.id.Quiker:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_ADD_LEAD,
                        Constants.CATEGORY_ADD_LEAD,
                        Constants.ACTION_TAP,
                        Constants.LABEL_QUIKR,
                        0);
                getSMS(Constants.QUIKR);
                smsApiCall(Constants.QUIKR, quikerCount);
                break;

            case R.id.Carwale:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_ADD_LEAD,
                        Constants.CATEGORY_ADD_LEAD,
                        Constants.ACTION_TAP,
                        Constants.LABEL_CARWALE,
                        0);
                getSMS(Constants.CARWALE);
                smsApiCall(Constants.CARWALE, CarwaleCount);
                break;

            case R.id.OLX:
                mGAHelper.sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_ADD_LEAD,
                        Constants.CATEGORY_ADD_LEAD,
                        Constants.ACTION_TAP,
                        Constants.LABEL_OLX,
                        0);
                getSMS(Constants.OLX);
                smsApiCall(Constants.OLX, olxCount);
                break;
*/
            /*case R.id.fromCallTracker:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_ADD_LEAD,
                        Constants.CATEGORY_ADD_LEAD,
                        Constants.ACTION_TAP,
                        Constants.LABEL_CALL_TRACKER_LEADS,
                        0);
                makeCallTrackApiCall();
                // showCallTrackerDialog();
                break;

            case R.id.fromRecentcalls:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_ADD_LEAD,
                        Constants.CATEGORY_ADD_LEAD,
                        Constants.ACTION_TAP,
                        Constants.LABEL_RECENT_CALLS,
                        0);
                showCallLogsDialog();

                break;

            case R.id.rl_addLead:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_ADD_LEAD,
                        Constants.CATEGORY_ADD_LEAD,
                        Constants.ACTION_TAP,
                        Constants.LABEL_ADD_LEAD_BUTTON,
                        0);
               Intent intent = new Intent(this, LeadAddActivity.class);
                Bundle args = new Bundle();
                args.putString(Constants.CALL_SOURCE, "NL");
                intent.putExtras(args);
                startActivity(intent);
                break;

            default:
                break;
        }*/

    }

   /* private void makeCallTrackApiCall() {
        progressBar.show();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.DEALER_USERNAME, CommonUtils
                .getStringSharedPreference(this, Constants.UC_DEALER_USERNAME,
                        ""));
        params.put(Constants.DEALER_PASSWORD, CommonUtils
                .getStringSharedPreference(this, Constants.UC_DEALER_PASSWORD,
                        ""));
        params.put(Constants.UCDID, String.valueOf(CommonUtils
                .getIntSharedPreference(this, Constants.UC_DEALER_ID, -1)));

        CallTrackRequest leadAddRequest = new CallTrackRequest(
                this,
                Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<CallTrackModel>() {
                    @Override
                    public void onResponse(CallTrackModel response) {
                        if (progressBar != null) {
                            progressBar.dismiss();
                        }

                        if (response.getStatus().equals("T")) {
                            if (response.getCustomers() != null) {
                                if (response.getCustomers().size() > 0) {
                                    //showCallTrackerDialog(response);
                                }
                            } else {
                                CommonUtils.showToast(LeadAddOptionActivity.this,
                                        response.getMessage(),
                                        Toast.LENGTH_SHORT);
                            }

                        } else {
                            CommonUtils.showToast(LeadAddOptionActivity.this,
                                    response.getErrorMessage(),
                                    Toast.LENGTH_SHORT);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (progressBar != null) {
                    progressBar.dismiss();
                }

                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(LeadAddOptionActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                } else {
                    CommonUtils.showToast(LeadAddOptionActivity.this,
                            getString(R.string.server_error_message), Toast.LENGTH_LONG);
                }
            }
        });

        ApplicationController.getInstance().addToRequestQueue(leadAddRequest,
                Constants.TAG_STOCKS_LIST, false, this);
    }*/

    /*private void showCallTrackerDialog(CallTrackModel callTrackModel) {

        if (calltrackDialogFragment == null) {

            calltrackDialogFragment = CallTrackFragment.getInstance();
            Bundle args = new Bundle();

            args.putSerializable(Constants.MODEL_DATA, callTrackModel);
            args.putString(Constants.CALL_SOURCE, "CT");
            calltrackDialogFragment.setArguments(args);

        }

        calltrackDialogFragment.show(getSupportFragmentManager(), "call-logs-dialog");

    }*/

    private void showCallLogsDialog() {

        if ((callLogsDialogFragment == null)) {
            callLogsDialogFragment = CallLogsDialogFragment.getInstance();


            Bundle args = new Bundle();
            args.putString(Constants.CALL_SOURCE, "RC");
            callLogsDialogFragment.setArguments(args);


        }

        callLogsDialogFragment.show(getSupportFragmentManager(), "call-logs-dialog");

    }

    @Override
    public void onCallLogSelected(CallLogItem callLogItem, ShareType shareType,
                                  String shareText, String carId, String imageUrl) {

        Intent intent = new Intent(LeadAddOptionActivity.this,
                LeadAddActivity.class);
        intent.putExtra(Constants.LEAD_MOBILE, callLogItem.getGaadiFormatNumber());
        intent.putExtra(Constants.LEAD_NAME, callLogItem.getName());
        intent.putExtra(Constants.CALL_SOURCE, callLogItem.getCallSource());
        startActivity(intent);

    }

    public ArrayList<String> getSMS(String sendername) {
        smsList = new ArrayList<String>();
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = getContentResolver().query(uriSMSURI, null, null, null,
                null);

        while (cur.moveToNext()) {
            String address = cur.getString(cur.getColumnIndex("address"));
            String body = cur.getString(cur.getColumnIndexOrThrow("body"));

            address = address.toLowerCase();
            sendername = sendername.toLowerCase();
            if (address.contains(sendername)) {

                byte[] data = null;

                try {
                    data = body.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String base64 = Base64.encodeToString(data, Base64.DEFAULT);
                smsList.add(base64);
            }

        }
        return smsList;

    }

   /* private void smsApiCall(String source, final TextView textView) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.DEALER_USERNAME, CommonUtils
                .getStringSharedPreference(this, Constants.UC_DEALER_USERNAME,
                        ""));
        params.put(Constants.DEALER_PASSWORD, CommonUtils
                .getStringSharedPreference(this, Constants.UC_DEALER_PASSWORD,
                        ""));
        params.put(Constants.SOURCE, source);
        params.put(Constants.LEADMSG, smsList.toString());
        progressBar.show();
        SmsRequest smsRequest = new SmsRequest(
                this,
                Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<SmsModel>() {
                    @Override
                    public void onResponse(SmsModel response) {
                        if (progressBar != null) {
                            progressBar.dismiss();
                        }

                        if (response.getStatus().equals("T")) {
                            textView.setText(response.getCount());
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (progressBar != null) {
                    progressBar.dismiss();
                }
                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(LeadAddOptionActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                } else {
                    CommonUtils.showToast(LeadAddOptionActivity.this,
                            getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                }
            }
        });

        ApplicationController.getInstance().addToRequestQueue(smsRequest,
                Constants.TAG_STOCKS_LIST, false, this);

    }*/

    @Override
    public void onNoInternetConnection(NetworkEvent networkEvent) {
        if (progressBar != null) {
            progressBar.dismiss();
        }
        CommonUtils.showToast(LeadAddOptionActivity.this, getString(R.string.network_error), Toast.LENGTH_LONG);
    }

    @Override
    public void onCallTrackkSelected(CustomerModel callLogItem) {

        Intent intent = new Intent(LeadAddOptionActivity.this,
                LeadAddActivity.class);
        intent.putExtra(Constants.LEAD_MOBILE, callLogItem.getMobile());
        intent.putExtra(Constants.LEAD_NAME, callLogItem.getName());
        intent.putExtra(Constants.CALL_SOURCE, callLogItem.getCallSource());
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION_READ_CALL_LOG) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Fragment fragment = adapter.getItem(0);
                if (fragment != null
                        && fragment instanceof CallLogsFragment) {
                    fragment.onActivityResult(Constants.REQUEST_PERMISSION_READ_CALL_LOG, RESULT_OK, null);
                }
            }
        }
    }
}
