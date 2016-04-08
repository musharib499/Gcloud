package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.BuildConfig;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.events.AppInitializationEvent;
import com.gcloud.gaadi.events.InitialRequestCompleteEvent;
import com.gcloud.gaadi.events.MakeInsertEvent;
import com.gcloud.gaadi.events.ModelsInsertEvent;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.events.VersionsInsertEvent;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.CityModel;
import com.gcloud.gaadi.model.DealerToDealerNoChangeModel;
import com.gcloud.gaadi.model.Make;
import com.gcloud.gaadi.model.Model;
import com.gcloud.gaadi.model.PasswordChangeModel;
import com.gcloud.gaadi.model.Version;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.squareup.otto.Subscribe;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends BaseActivity implements View.OnClickListener, OnNoInternetConnectionListener {

    ActionBar mActionBar;
    private Button syncNow, dealerToDealerNoDone, changePass, mChangePasswordExpand, mDealerToDealerExpand;
    private GAHelper mGAHelper;
    private int requestPerformedCounter = 0;
    private GCProgressDialog progressDialog;
    private TextView tvLoggedInLabel, dealerName;
    private LinearLayout mChangePasswordContainer, mDealerToDealerChangeContainer;
    private CheckBox showPass;
    private EditText newPass, oldPass, dealerToDealerNo;
    private String oldPasswd, newPasswd, mDToDNo;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_settings);
        getLayoutInflater().inflate(R.layout.activity_settings, frameLayout);
        mGAHelper = new GAHelper(this);
        mActionBar = getSupportActionBar();
        progressDialog = new GCProgressDialog(this, (Activity) this);
        setupActionBar();
        mChangePasswordContainer = (LinearLayout) findViewById(R.id.changePasswordContainer);
        mChangePasswordContainer.setVisibility(View.GONE);

        mDealerToDealerChangeContainer = (LinearLayout) findViewById(R.id.dealerToDealerChangeContainer);
        mDealerToDealerChangeContainer.setVisibility(View.GONE);
        tvLoggedInLabel = (TextView) findViewById(R.id.logedInAs);
        showPass = (CheckBox) findViewById(R.id.showPass);
        newPass = (EditText) findViewById(R.id.newPass);
        oldPass = (EditText) findViewById(R.id.oldPass);
        dealerToDealerNo = (EditText) findViewById(R.id.dealerToDealerNo);
        dealerName = (TextView) findViewById(R.id.dealership_name);
        dealerName.setText(CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_USERNAME, ""));
        dealerToDealerNo.setHint(CommonUtils.getStringSharedPreference(SettingsActivity.this, Constants.UC_DEALER_MOBILE, ""));
        showPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    newPass.setTransformationMethod(null);
                else
                    newPass.setTransformationMethod(new PasswordTransformationMethod());
                if (newPass.getText().length() > 0)
                    newPass.setSelection(newPass.getText().length());

            }
        });
        Boolean isServiceExecutive = CommonUtils.getBooleanSharedPreference(SettingsActivity.this, Constants.SERVICE_EXECUTIVE_LOGIN, false);
        String dealerName = CommonUtils.getStringSharedPreference(SettingsActivity.this, Constants.UC_DEALER_EMAIL, "abc@gaadi.com");

        setSyncDate();
        if (isServiceExecutive) {
            tvLoggedInLabel.setText("Current Dealer: " + dealerName);
        } else {
            tvLoggedInLabel.setText("Logged in as: " + dealerName);
        }
        syncNow = (Button) findViewById(R.id.syncNow);
        dealerToDealerNoDone = (Button) findViewById(R.id.dealerToDealerNoDone);
        mChangePasswordExpand = (Button) findViewById(R.id.changePasswordExpand);
        mDealerToDealerExpand = (Button) findViewById(R.id.dealerToDealerExpand);
        if (isServiceExecutive) {
            mChangePasswordExpand.setVisibility(View.GONE);
            mDealerToDealerExpand.setVisibility(View.GONE);
        }
        syncNow.setOnClickListener(this);
        dealerToDealerNoDone.setOnClickListener(this);
        changePass = (Button) findViewById(R.id.changePass);
        changePass.setOnClickListener(this);

        ((TextView) findViewById(R.id.versionName)).setText(CommonUtils.getReplacementString(getApplicationContext(), R.string.version_name, BuildConfig.VERSION_NAME));
    }

    public void updatePassword() {
        oldPasswd = oldPass.getText().toString();
        newPasswd = newPass.getText().toString();
        performPasswordChangeRequest();
        newPass.getText().clear();
        oldPass.getText().clear();
    }

    public void updateDealerToDealerNumber() {
        mDToDNo = dealerToDealerNo.getText().toString();
        performDealerToDealerNumberChangeRequest();
    }

    public void onExpandClicked(View v) {
        if (v.getId() == R.id.changePasswordExpand) {
            mDealerToDealerExpand.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_gray, 0);
            if (mChangePasswordContainer.getVisibility() == View.VISIBLE) {
                mChangePasswordContainer.setVisibility(View.GONE);
                mChangePasswordExpand.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_gray, 0);
                mChangePasswordExpand.setBackgroundColor(Color.parseColor("#F1F1F1"));
                mChangePasswordExpand.setTextColor(Color.parseColor("#263242"));
            } else {
                mChangePasswordContainer.setVisibility(View.VISIBLE);
                mChangePasswordExpand.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.close_small, 0);
                mChangePasswordExpand.setBackgroundColor(getResources().getColor(R.color.primary));
                mChangePasswordExpand.setTextColor(Color.parseColor("#FFFFFF"));
            }
            mDealerToDealerChangeContainer.setVisibility(View.GONE);
            mDealerToDealerExpand.setBackgroundColor(Color.parseColor("#F1F1F1"));
            mDealerToDealerExpand.setTextColor(Color.parseColor("#263242"));
        }
        if (v.getId() == R.id.dealerToDealerExpand) {
            mChangePasswordExpand.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_gray, 0);
            if (mDealerToDealerChangeContainer.getVisibility() == View.VISIBLE) {
                mDealerToDealerChangeContainer.setVisibility(View.GONE);
                mDealerToDealerExpand.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_gray, 0);

                mDealerToDealerExpand.setBackgroundColor(Color.parseColor("#F1F1F1"));
                mDealerToDealerExpand.setTextColor(Color.parseColor("#263242"));
            } else {
                mDealerToDealerChangeContainer.setVisibility(View.VISIBLE);
                mDealerToDealerExpand.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.close_small, 0);
                mDealerToDealerExpand.setBackgroundColor(getResources().getColor(R.color.primary));
                mDealerToDealerExpand.setTextColor(Color.parseColor("#FFFFFF"));
            }
            mChangePasswordContainer.setVisibility(View.GONE);
            mChangePasswordExpand.setBackgroundColor(Color.parseColor("#F1F1F1"));
            mChangePasswordExpand.setTextColor(Color.parseColor("#263242"));
        }
    }

    private void setSyncDate() {
        TextView tvSyncDate = (TextView) findViewById(R.id.syncDate);
        Long syncDate = CommonUtils.getLongSharedPreference(SettingsActivity.this, Constants.SYNC_DATE, 0);
        String syncDateFormat = getFormatDate(syncDate);
        tvSyncDate.setText("Sync Date: " + syncDateFormat);
    }

    private String getAppVersion() {
        String versionName = "Version ";
        try {

            versionName += getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private String getFormatDate(Long syncDate) {
        Date formatDate = new Date(syncDate);
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss");
        String finalDate = format.format(formatDate);
        return finalDate;
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_SETTINGS);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu, menu);
        return true;
    }

    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Download Gcloud App");
        share.putExtra(Intent.EXTRA_TEXT, "Easiest way to manage your used car dealership:- " +
                "https://play.google.com/store/apps/details?id=com.gcloud.gaadi");

        startActivity(Intent.createChooser(share, "Share Gcloud with friends !"));
    }

    private void performPasswordChangeRequest() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.METHOD_LABEL, Constants.PASSWORD_CHANGE_METHOD);
        params.put(Constants.OLD_PASS, oldPasswd);
        params.put(Constants.NEW_PASS, newPasswd);

        RetrofitRequest.makePasswordChangeRequest(this, params, new Callback<PasswordChangeModel>() {
            @Override
            public void success(PasswordChangeModel response, retrofit.client.Response res) {
                if (response.getStatus().equalsIgnoreCase("T")) {
                    CommonUtils.showToast(SettingsActivity.this, "Dealer Password Changed Successfully", Toast.LENGTH_SHORT);
                    ApplicationController.getEventBus().post(new InitialRequestCompleteEvent(4, InitialRequestCompleteEvent.RequestType.CITY_REQUEST));

                } else {
                    CommonUtils.showToast(SettingsActivity.this, response.getMessage(), Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                GCLog.e("error: " + error.getMessage());
                CommonUtils.showToast(SettingsActivity.this, "Network error, Please try again later.", Toast.LENGTH_SHORT);
            }
        });
    }

    private void performDealerToDealerNumberChangeRequest() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.METHOD_LABEL, Constants.DEALER_NO_CHANGE_METHOD);
        params.put(Constants.MOBILE_NUM, mDToDNo);
        RetrofitRequest.makeDealerToDealerNoChangeRequest(this, params, new Callback<DealerToDealerNoChangeModel>() {
            @Override
            public void success(DealerToDealerNoChangeModel response, retrofit.client.Response res) {
                if (response.getStatus().equalsIgnoreCase("T")) {
                    dealerToDealerNo.getText().clear();
                    dealerToDealerNo.setHint(mDToDNo);
                    CommonUtils.setStringSharedPreference(SettingsActivity.this, Constants.UC_DEALER_MOBILE, mDToDNo);
                    CommonUtils.showToast(SettingsActivity.this, "Dealer to Dealer Number Updated", Toast.LENGTH_SHORT);
                    ApplicationController.getEventBus().post(new InitialRequestCompleteEvent(4, InitialRequestCompleteEvent.RequestType.CITY_REQUEST));
                } else {
                    CommonUtils.showToast(SettingsActivity.this, response.getMessage(), Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                GCLog.e("error: " + error.getMessage());
                CommonUtils.showToast(SettingsActivity.this, "Network error, please try again later.", Toast.LENGTH_SHORT);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.share_button) {
            shareTextUrl();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.syncNow:
                progressDialog.setCancelable(false);
                progressDialog.show();
                performMakeModelSync();
                break;
            case R.id.dealerToDealerNoDone:
                updateDealerToDealerNumber();
                break;
            case R.id.changePass:
                updatePassword();
                break;
        }
    }

    private void performMakeModelSync() {
        performMakeRequest();
        performModelRequest();
        performVersionRequest();
        performCityRequest();
    }

    private void performMakeRequest() {

        final HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.METHOD_LABEL, Constants.MAKE_ALL_METHOD);
        RetrofitRequest.makeRequest(params, new Callback<Make>() {
            @Override
            public void success(Make make, retrofit.client.Response response) {
                GCLog.e("Dipanshu",params+"");
                MakeModelVersionDB makeDB = ApplicationController.getMakeModelVersionDB();
                makeDB.insertMakes(make.getMakeObjects());
                ApplicationController.getEventBus().post(new InitialRequestCompleteEvent(1, InitialRequestCompleteEvent.RequestType.MAKE_REQUEST));

            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(SettingsActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);

                } else {
                    CommonUtils.showToast(SettingsActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);

                }
            }
        });

       /* MakeRequest makeRequest = new MakeRequest(this,
                Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<Make>() {
                    @Override
                    public void onResponse(Make response) {
                        MakeModelVersionDB makeDB = ApplicationController.getMakeModelVersionDB();
                        makeDB.insertMakes(response.getMakeObjects());
                        ApplicationController.getEventBus().post(new InitialRequestCompleteEvent(1, InitialRequestCompleteEvent.RequestType.MAKE_REQUEST));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        if (error.getCause() instanceof UnknownHostException) {
                            CommonUtils.showToast(SettingsActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);

                        } else {
                            CommonUtils.showToast(SettingsActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);

                        }
                    }
                });

        ApplicationController.getInstance().addToRequestQueue(makeRequest, Constants.TAG_MAKE_ALL, false, this);*/

    }

    private void performModelRequest() {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.METHOD_LABEL, Constants.MODEL_ALL_METHOD);
        RetrofitRequest.modelRequest(params, new Callback<Model>() {
            @Override
            public void success(Model model, retrofit.client.Response response) {
                GCLog.e("Dipanshu2",params.toString());
                MakeModelVersionDB modelDB = ApplicationController.getMakeModelVersionDB();
                modelDB.insertModels(model.getModels());
                ApplicationController.getEventBus().post(new InitialRequestCompleteEvent(2, InitialRequestCompleteEvent.RequestType.MODEL_REQUEST));

            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(SettingsActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);

                } else {
                    CommonUtils.showToast(SettingsActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);

                }
            }
        });


       /* ModelRequest modelRequest = new ModelRequest(
                this,
                Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<Model>() {
                    @Override
                    public void onResponse(Model response) {
                        MakeModelVersionDB modelDB = ApplicationController.getMakeModelVersionDB();
                        modelDB.insertModels(response.getModels());
                        ApplicationController.getEventBus().post(new InitialRequestCompleteEvent(2, InitialRequestCompleteEvent.RequestType.MODEL_REQUEST));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        if (error.getCause() instanceof UnknownHostException) {
                            CommonUtils.showToast(SettingsActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);

                        } else {
                            CommonUtils.showToast(SettingsActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);

                        }
                    }
                }
        );

        ApplicationController.getInstance().addToRequestQueue(modelRequest, Constants.TAG_MODEL_ALL, false, this);*/
    }

    private void performVersionRequest() {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.METHOD_LABEL, Constants.VERSION_ALL_METHOD);

        RetrofitRequest.versionRequest(params, new Callback<Version>() {
            @Override
            public void success(Version version, retrofit.client.Response response) {
                GCLog.e("Dipanshu3",params.toString());
                MakeModelVersionDB versionDB = ApplicationController.getMakeModelVersionDB();
                versionDB.insertVersions(version.getVersions());
                ApplicationController.getEventBus().post(new InitialRequestCompleteEvent(3, InitialRequestCompleteEvent.RequestType.VERSION_REQUEST));

            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(SettingsActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);

                } else {
                    CommonUtils.showToast(SettingsActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);

                }

            }
        });

       /* VersionRequest versionRequest = new VersionRequest(
                this,
                Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<Version>() {
                    @Override
                    public void onResponse(Version response) {
                        MakeModelVersionDB versionDB = ApplicationController.getMakeModelVersionDB();
                        versionDB.insertVersions(response.getVersions());
                        ApplicationController.getEventBus().post(new InitialRequestCompleteEvent(3, InitialRequestCompleteEvent.RequestType.VERSION_REQUEST));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        if (error.getCause() instanceof UnknownHostException) {
                            CommonUtils.showToast(SettingsActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);

                        } else {
                            CommonUtils.showToast(SettingsActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);

                        }
                    }
                }
        );

        ApplicationController.getInstance().addToRequestQueue(versionRequest, Constants.TAG_VERSION_ALL, false, this);*/

    }

    private void performCityRequest() {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.METHOD_LABEL, Constants.CITY_METHOD);
        RetrofitRequest.cityRequest(params, new Callback<CityModel>() {
            @Override
            public void success(CityModel cityModel, retrofit.client.Response response) {
                GCLog.e("Dipanshu4", params.toString());
                if (cityModel.getStatus().equalsIgnoreCase("T")) {
                    MakeModelVersionDB db = ApplicationController.getMakeModelVersionDB();
                    db.removeCities();
                    db.insertCities(cityModel.getCityList());
                    ApplicationController.getEventBus().post(new InitialRequestCompleteEvent(4, InitialRequestCompleteEvent.RequestType.VERSION_REQUEST));

                } else {
                    CommonUtils.showToast(SettingsActivity.this, "Server Error - Please try again later", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(SettingsActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);

                } else {
                    CommonUtils.showToast(SettingsActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                }
            }
        });

     /*   CityRequest cityRequest = new CityRequest(
                this,
                Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<CityModel>() {
                    @Override
                    public void onResponse(CityModel response) {
                        if (response.getStatus().equalsIgnoreCase("T")) {
                            MakeModelVersionDB db = ApplicationController.getMakeModelVersionDB();
                            db.removeCities();
                            db.insertCities(response.getCityList());
                            ApplicationController.getEventBus().post(new InitialRequestCompleteEvent(4, InitialRequestCompleteEvent.RequestType.VERSION_REQUEST));

                        } else {
                            CommonUtils.showToast(SettingsActivity.this, "Server Error - Please try again later", Toast.LENGTH_SHORT);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        if (error.getCause() instanceof UnknownHostException) {
                            CommonUtils.showToast(SettingsActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);

                        } else {
                            CommonUtils.showToast(SettingsActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);

                        }

                    }
                });

        ApplicationController.getInstance().addToRequestQueue(cityRequest, Constants.TAG_CITY_REQUEST, true, this);*/
    }

    @Subscribe
    public void onInitialRequestCompleted(InitialRequestCompleteEvent requestCompleteEvent) {
        GCLog.e("Request complete " + requestCompleteEvent.toString());
        ++requestPerformedCounter;
        if (requestPerformedCounter >= 4) {
            CommonUtils.setBooleanSharedPreference(this, Constants.APP_INITIALIZATION, true);
            ApplicationController.getEventBus().post(new AppInitializationEvent("App Initialized", -1));
        }
    }

    @Subscribe
    public void onAppInitialized(AppInitializationEvent initializationEvent) {
        updateLastSyncDate();
    }

    private void updateLastSyncDate() {
        Date syncDate = new Date(System.currentTimeMillis());
        long millis = syncDate.getTime();
        CommonUtils.setLongSharedPreference(this, Constants.SYNC_DATE, millis);

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        setSyncDate();
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
    public void onNoInternetConnection(NetworkEvent networkEvent) {

        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        if (networkEvent.isShowFullPageError()) {

        } else {
            CommonUtils.showToast(this, "Internet connection is required.", Toast.LENGTH_SHORT);
        }
    }

    @Subscribe
    public void onMakesInsertEvent(MakeInsertEvent event) {
        if (event.isError()) {
            showDBErrorDialog();
        } else {

        }
    }

    @Subscribe
    public void onModelsInsertEvent(ModelsInsertEvent event) {
        if (event.isError()) {
            showDBErrorDialog();
        } else {

        }
    }

    @Subscribe
    public void onVersionsInsertEvent(VersionsInsertEvent event) {
        GCLog.e("Versions inserted: " + event.isError());
        if (event.isError()) {
            showDBErrorDialog();
        } else {

        }
    }

    private void showDBErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.application_error);
        builder.setMessage(R.string.application_error_message);
        builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mGAHelper.sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_SETTINGS,
                        Constants.CATEGORY_SETTINGS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_DBWRITE_ERROR,
                        0);
                performMakeModelSync();

            }
        });

        AlertDialog dialog = builder.create();

        if (!this.isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }
    }
}
