package com.gcloud.gaadi.ui;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.BuildConfig;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.annotations.Email;
import com.gcloud.gaadi.annotations.Password;
import com.gcloud.gaadi.annotations.Required;
import com.gcloud.gaadi.annotations.rules.Rule;
import com.gcloud.gaadi.annotations.rules.Validator;
import com.gcloud.gaadi.chat.GCMIntentService;
import com.gcloud.gaadi.chat.PreferenceSettings;
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
import com.gcloud.gaadi.model.DealerData;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.LoginUserModel;
import com.gcloud.gaadi.model.Make;
import com.gcloud.gaadi.model.Model;
import com.gcloud.gaadi.model.SFAUserRight;
import com.gcloud.gaadi.model.UpgradeCheckModel;
import com.gcloud.gaadi.model.UserDetail;
import com.gcloud.gaadi.model.Version;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.service.SyncStocksService;
import com.gcloud.gaadi.syncadapter.GenericAccountService;
import com.gcloud.gaadi.syncadapter.SyncUtils;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by ankit on 17/11/14.
 */
public class SplashActivity extends AppCompatActivity implements Validator.ValidationListener,
        View.OnClickListener, DealerPickerFragment.OnDealerSelectedListener,
        OnNoInternetConnectionListener {

    public String token = "";
    public String mUserRights = "";

    @Required(order = 1, messageResId = R.string.invalid_email_id)
    @Email(order = 2, minLength = 8, messageResId = R.string.invalid_email_id)
    EditText userEmail;

    @Password(order = 3, messageResId = R.string.invalid_password)
    EditText userPassword;

    Button loginButton;
    ProgressBar progressBar;
    TextView signUpText;
    TextInputLayout tlUserEmail, tlUserPassword;
    LinearLayout progressLayout, formContainer;
    int screenHeight, screenWidth, logoTop;
    private Validator mValidator;
    private DisplayMetrics displayMetrics;
    private ImageView logo;
    private GCProgressDialog progressDialog;
    //private Account mAccount;
    //private AccountManager mAccountManager;
    private String mAccessToken;
    // sync interval one day
    /*public static final long SYNC_INTERVAL = 60*60*24;
    public static final String AUTHORITY = "com.gcloud.gaadi.syncadapter";
    public static final String ACCOUNT = "GCloud";*/
    private int[] viewCoordinates = new int[2];
    // Account type and auth token type
    //public static final String ACCOUNT_TYPE = "com.gcloud.gaadi.syncadapter";
    private int requestPerformedCounter = 0;
    //    private GAHelper gaHelper;
    private int networkCallsCounter = 0;
    private int selectedIndex = -1;
    private String TAG = SplashActivity.class.getSimpleName();
    private boolean timeout = false;
    private CheckBox checkBox;
    private ArrayList<SFAUserRight> mSFAUserRights;


    public void onBackPressed() {
        super.onBackPressed();
    }

    @Subscribe
    public void onNoNetworkConnectivity(NetworkEvent networkEvent) {
        GCLog.e("There is no network connection");
        if (!this.isFinishing() && progressDialog != null) {
            progressDialog.dismiss();
        }
        if (networkEvent.getNetworkError().equals(NetworkEvent.NetworkError.NO_INTERNET_CONNECTION)) {
            GCLog.e("Full page error: " + networkEvent.isShowFullPageError());
            CommonUtils.showToast(this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, "Login Screen");
        CommonUtils.uploadPendingImages(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*mUserRights = CommonUtils.getStringSharedPreference(getApplicationContext(), SfaCampaignTrackingReceiver.EXTRA_ACCESS_RIGHTS, "");
        SFAUserRight[] sfaUserRights = new Gson().fromJson(mUserRights, SFAUserRight[].class);
        if (sfaUserRights != null) {
            mSFAUserRights = new ArrayList<SFAUserRight>(Arrays.asList(sfaUserRights));
            GCLog.e("sfa user rights: " + mSFAUserRights.toString());
        }*/

        SyncUtils.CreateSyncAccount(this);
        ContentResolver.requestSync(GenericAccountService.GetAccount(SyncUtils.ACCOUNT_TYPE),
                Constants.SPLASH_CONTENT_AUTHORITY,
                new Bundle());

        /*AccountManager manager = AccountManager.get(this);
        Account account = new Account(ACCOUNT, ACCOUNT_TYPE);

        Account[] accounts = manager.getAccountsByType(ACCOUNT_TYPE);
        if (accounts.length >= 1) {
            account = accounts[0];
            GCLog.e("Sync Account Already exist");
            GCLog.e("Sync Account User name = " + account.name);
        } else {
            GCLog.e("Need to add Sync Account");
            manager.addAccountExplicitly(account, null, null);
        }
        ContentResolver.setIsSyncable(account, AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
        ContentResolver.addPeriodicSync(account, AUTHORITY, new Bundle(), SYNC_INTERVAL);*/


        CommonUtils.setStringSharedPreference(this, Constants.REST_HOST, BuildConfig.REST_HOST);
        //CommonUtils.setStringSharedPreference(this, Constants.INSURANCE_REST_HOST, BuildConfig.INSURANCE_REST_HOST);

        Answers.getInstance().logCustom(new CustomEvent("App Launch"));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            GCLog.e("extras are not null");
            if (extras.containsKey("aID")) {

                // send this to server to track.
                GCLog.e("notification opened: " + extras.toString());

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.METHOD_LABEL, Constants.LOG_NOTIFICATION_METHOD);
                params.put(Constants.GAADI_GCM_ID, CommonUtils.getStringSharedPreference(this, Constants.GAADI_GCM_ID, ""));
                params.put(Constants.GCM_RESPONSE_CODE, "0");
                params.put(Constants.SCREEN_NAME, getClass().getSimpleName());
                params.put(Constants.GCM_MESSAGE, extras.toString());

                /*/LogNotificationRequest logNotificationRequest = new LogNotificationRequest(
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

                ApplicationController.getInstance().addToRequestQueue(logNotificationRequest);/*/
                RetrofitRequest.makeLogNotificationRequest(this, params, new Callback<GeneralResponse>() {
                    @Override
                    public void success(GeneralResponse generalResponse, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
                /**/
            } else if (extras.containsKey(Constants.FROM_DEEPLINK)) {
                token = extras.getString(Constants.TOKEN);
                if (token == null) {
                    token = "";
                }
            }
        }

        try {
            int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            String packageName = getPackageManager().getPackageInfo(getPackageName(), 0).packageName;
            CommonUtils.setStringSharedPreference(this, Constants.APP_VERSION_CODE, String.valueOf(versionCode));
            CommonUtils.setStringSharedPreference(this, Constants.APP_VERSION_NAME, versionName);
            CommonUtils.setStringSharedPreference(this, Constants.APP_PACKAGE_NAME, packageName);
            String android_id = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            GCLog.e("Android id: " + android_id);
            CommonUtils.setStringSharedPreference(this, Constants.ANDROID_ID, android_id);

        } catch (PackageManager.NameNotFoundException e) {
            GCLog.e("Package name not found " + e.getMessage());
        }

//        gaHelper = new GAHelper(this);


        logo = (ImageView) findViewById(R.id.splash_logo);
        progressLayout = (LinearLayout) findViewById(R.id.progressLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        signUpText = (TextView) findViewById(R.id.progressText);
        progressDialog = new GCProgressDialog(this, this);
        tlUserPassword = (TextInputLayout) findViewById(R.id.tlUserPassword);
        tlUserEmail = (TextInputLayout) findViewById(R.id.tlUserEmail);
        formContainer = (LinearLayout) findViewById(R.id.formContainer);
        userEmail = (EditText) findViewById(R.id.userEmail);
        userPassword = (EditText) findViewById(R.id.userPassword);
        loginButton = (Button) findViewById(R.id.login);
        checkBox = (CheckBox) findViewById(R.id.show_password);
        loginButton.setOnClickListener(this);
        userEmail.setHint(getResources().getString(R.string.username_hint));
        userPassword.setHint(getResources().getString(R.string.password_hint));

        /*userEmail.setText("saroj.sahoo@gaadi.com");
        userPassword.setText("sarojsahoo");*/
        userEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                tlUserEmail.setHint("Email");
                userEmail.setHint("");
                //userEmail.setText("");
            }
        });
        userPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override

            public void onFocusChange(View v, boolean hasFocus) {
                tlUserPassword.setHint("Password");
                // userPassword.setText("");
                userPassword.setHint("");
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userPassword.setInputType(isChecked ?
                        InputType.TYPE_TEXT_VARIATION_PASSWORD :
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                userPassword.setSelection(userPassword.getText().length());
            }
        });
       /* userEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                tlUserEmail.setHint("Email");
                userEmail.setHint("");
                return false;

            }
        });
        userPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tlUserPassword.setHint("Password");
                userPassword.setHint("");
                return false;

            }
        });
*/

        if ("HDFC".equalsIgnoreCase(BuildConfig.CLOUD_OWNER)) {
            userEmail.setTextColor(ContextCompat.getColor(this, R.color.textColor));
            userPassword.setTextColor(ContextCompat.getColor(this, R.color.textColor));
            findViewById(R.id.separator).setBackgroundColor(ContextCompat.getColor(this, R.color.hintColor));
            findViewById(R.id.secondSeparator).setBackgroundColor(ContextCompat.getColor(this, R.color.hintColor));
        }

        userPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    loginButton.performClick();
                }
                return false;
            }
        });


        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        displayMetrics = CommonUtils.getDisplayMetrics(this);

        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        performAppInitialization();

        GCLog.i("oncreate");

    }

    @Override
    protected void onResume() {
        super.onResume();
        GCLog.i("onresume");
        ApplicationController.getEventBus().register(this);
    }


    public void performAppInitialization() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!timeout) {
                    timeout = true;
                    doNext();
                }
            }
        }, 5000);
        HashMap<String, String> mParams = new HashMap<>();
        mParams.put(Constants.METHOD_LABEL, Constants.UPGRADE_CHECK_METHOD);
        mParams.put("v", CommonUtils.getStringSharedPreference(this, Constants.APP_VERSION_CODE, ""));
        RetrofitRequest.makeUpgradeRequest(this, mParams, new Callback<UpgradeCheckModel>() {
            @Override
            public void success(UpgradeCheckModel response, retrofit.client.Response res) {

                //GCLog.e("Upgrade check response: " + response);

                try {

                    if ("F".equalsIgnoreCase(response.getStatus())) {
                        if (!timeout) {
                            timeout = true;
                            doNext();
                        }
                    } else {
                        timeout = true;
                        if (response.isForceUpgrade()) {
                            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                    Constants.CATEGORY_SPLASH_SCREEN,
                                    Constants.CATEGORY_SPLASH_SCREEN,
                                    Constants.ACTION_LAUNCH,
                                    Constants.LABEL_FORCE_UPGRADE,
                                    0);
                            showForceUpgradeDialog();

                        } else {
                            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                    Constants.CATEGORY_SPLASH_SCREEN,
                                    Constants.CATEGORY_SPLASH_SCREEN,
                                    Constants.ACTION_LAUNCH,
                                    Constants.LABEL_SOFT_UPGRADE,
                                    0);
                            showSoftUpgradeDialog();

                        }
                    }
                } catch (Exception e) {
                    doNext();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_SPLASH_SCREEN,
                        Constants.CATEGORY_SPLASH_SCREEN,
                        Constants.ACTION_LAUNCH,
                        Constants.LABEL_SERVER_ERROR,
                        0);

                if (error.getCause() instanceof UnknownHostException) {
                    timeout = true;
                    showNetworkConnectionErrorDialog();
                } else {
                    if (!timeout) {
                        timeout = true;
                        doNext();
                        return;
                    }
                    timeout = true;
                    showServerErrorDialog();
                }
                GCLog.e("error: " + error.getMessage());
            }
        });
        /**/

    }

    private void doNext() {
        if (CommonUtils.getBooleanSharedPreference(SplashActivity.this, Constants.APP_INITIALIZATION, false)) {
            showLoginForm();
        } else {
            makeRequests();
        }
    }

    private void showNetworkConnectionErrorDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.network_connection_error);
        builder.setMessage("It seems your network connection is not working fine. Please reconnect and try again.");
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performAppInitialization();
            }
        });
        builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.create().dismiss();
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        if (!this.isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }
    }

    private void makeRequests() {

        performMakeRequest();
        performModelRequest();
        performVersionRequest();
        performCityRequest();

    }

    private void performCityRequest() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.METHOD_LABEL, Constants.CITY_METHOD);

        RetrofitRequest.makeCityRequest(this, params, new Callback<CityModel>() {
            @Override
            public void success(CityModel response, retrofit.client.Response res) {
                if (response.getStatus().equalsIgnoreCase("T")) {
                    GCLog.e("cities: " + response.toString());
                    MakeModelVersionDB db = ApplicationController.getMakeModelVersionDB();
                    db.removeCities();
                    db.insertCities(response.getCityList());

                    ApplicationController.getEventBus().post(new InitialRequestCompleteEvent(4, InitialRequestCompleteEvent.RequestType.CITY_REQUEST));

                } else {
                    if (CommonUtils.getBooleanSharedPreference(SplashActivity.this, Constants.APP_INITIALIZATION, false)) {
                        showLoginForm();
                    } else {
                        showServerErrorDialog();
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                GCLog.e("error: " + error.getMessage());
                if (CommonUtils.getBooleanSharedPreference(SplashActivity.this, Constants.APP_INITIALIZATION, false)) {
                    showLoginForm();
                } else {
                    if (error.getCause() instanceof UnknownHostException) {
                        showNetworkConnectionErrorDialog();
                    } else {
                        showServerErrorDialog();
                    }
                }
            }
        });
        /**/
    }

    private void showSoftUpgradeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_upgrade_title);
        builder.setCancelable(false);
        builder.setMessage(R.string.app_upgrade_message);
        builder.setPositiveButton(R.string.upgrade_now, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_SPLASH_SCREEN,
                        Constants.CATEGORY_SPLASH_SCREEN,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SOFT_UPGRADE,
                        0);
                navigateToPlayStore(true);
            }
        });
        builder.setNegativeButton(R.string.upgrade_later, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_SPLASH_SCREEN,
                        Constants.CATEGORY_SPLASH_SCREEN,
                        Constants.ACTION_TAP,
                        Constants.LABEL_LATER,
                        0);
                doNext();
            }
        });

        AlertDialog dialog = builder.create();

        if (!this.isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }
    }

    private void navigateToPlayStore(boolean softUpgrade) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
                    + CommonUtils.getStringSharedPreference(this, Constants.APP_PACKAGE_NAME, ""))));
            finish();

        } catch (ActivityNotFoundException anfe) {
            Crashlytics.logException(anfe);
            CommonUtils.showToast(this, "Play store not found. Please retry.", Toast.LENGTH_LONG);
            if (softUpgrade) {
                showSoftUpgradeDialog();
            } else {
                showForceUpgradeDialog();
            }
        }
    }

    private void showForceUpgradeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_upgrade_title);
        builder.setMessage(R.string.app_upgrade_message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.upgrade_now, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_SPLASH_SCREEN,
                        Constants.CATEGORY_SPLASH_SCREEN,
                        Constants.ACTION_TAP,
                        Constants.LABEL_FORCE_UPGRADE,
                        0);
                navigateToPlayStore(false);
            }
        });
        builder.setNegativeButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_SPLASH_SCREEN,
                        Constants.CATEGORY_SPLASH_SCREEN,
                        Constants.ACTION_TAP,
                        Constants.LABEL_FORCE_UPGRADE,
                        0);
                finish();
            }
        });

        AlertDialog dialog = builder.create();

        if (!this.isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }
    }

    @Subscribe
    public void onInitialRequestCompleted(InitialRequestCompleteEvent requestCompleteEvent) {
        GCLog.e("Request complete " + requestCompleteEvent.toString());
        ++requestPerformedCounter;
        if (requestPerformedCounter >= 4) {
            CommonUtils.setBooleanSharedPreference(SplashActivity.this, Constants.APP_INITIALIZATION, true);
            ApplicationController.getEventBus().post(new AppInitializationEvent("App Initialized", logoTop));

        } /*else if (requestCompleteEvent.getRequestType() == InitialRequestCompleteEvent.RequestType.MODEL_SYNC) {
            CommonUtils.setBooleanSharedPreference(SplashActivity.this, Constants.RE_SYNCED, true);

            if (CommonUtils.getBooleanSharedPreference(this, Constants.APP_INITIALIZATION, false)) {
                ApplicationController.getEventBus().post(new AppInitializationEvent("Model Synced", logoTop));
            }
        }*/

    }


    @Subscribe
    public void onAppInitialized(AppInitializationEvent initializationEvent) {
        MakeModelVersionDB makeModelVersionDB = ApplicationController.getMakeModelVersionDB();
        DateTime dateTime = DateTime.now();
        long millis = dateTime.getMillis();
        CommonUtils.setLongSharedPreference(this, Constants.SYNC_DATE, millis);
        //GCLog.e( "MMV = " + makeModelVersionDB.getRecords());
        /*if ("Model Synced".equalsIgnoreCase(initializationEvent.getMessage())) {
            loginUser(CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_EMAIL, ""),
                    CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_PASSWORD, ""), false);
        } else {*/
            showLoginForm();
        //}
    }

    @Override
    protected void onPause() {
        super.onPause();
        ApplicationController.getEventBus().unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GCLog.i("onStop");
    }

    private void performMakeRequest() {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.METHOD_LABEL, Constants.MAKE_ALL_METHOD);

        RetrofitRequest.makeMakeRequest(this, params, new Callback<Make>() {
            @Override
            public void success(Make make, retrofit.client.Response response) {
                MakeModelVersionDB makeDB = ApplicationController.getMakeModelVersionDB();
                makeDB.insertMakes(make.getMakeObjects());
                //ArrayList<MakeObject> makes = makeDB.getMakes();

                ApplicationController.getEventBus().post(new InitialRequestCompleteEvent(1, InitialRequestCompleteEvent.RequestType.MAKE_REQUEST));
            }

            @Override
            public void failure(RetrofitError error) {
                GCLog.e("error: " + error.getMessage());
                if (error.getCause() instanceof UnknownHostException) {
                    showNetworkConnectionErrorDialog();
                } else {
                    showServerErrorDialog();
                }
            }
        });
        /**/

    }

    private void performModelRequest() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.METHOD_LABEL, Constants.MODEL_ALL_METHOD);


        RetrofitRequest.makeModelRequest(this, params, new Callback<Model>() {
            @Override
            public void success(Model model, retrofit.client.Response response) {
                GCLog.e(model.toString());
                MakeModelVersionDB modelDB = ApplicationController.getMakeModelVersionDB();
                modelDB.insertModels(model.getModels());

                if (CommonUtils.getBooleanSharedPreference(SplashActivity.this, Constants.APP_INITIALIZATION, false)) {
                    if (!CommonUtils.getBooleanSharedPreference(SplashActivity.this, Constants.RE_SYNCED, false)) {
                        //CommonUtils.setBooleanSharedPreference(SplashActivity.this, Constants.RE_SYNCED, true);
                        ApplicationController.getEventBus().post(new InitialRequestCompleteEvent(0, InitialRequestCompleteEvent.RequestType.MODEL_SYNC));
                    }
                }

                ApplicationController.getEventBus().post(new InitialRequestCompleteEvent(2, InitialRequestCompleteEvent.RequestType.MODEL_REQUEST));
            }

            @Override
            public void failure(RetrofitError error) {
                GCLog.e("error: " + error.getMessage());
                if (error.getCause() instanceof UnknownHostException) {
                    showNetworkConnectionErrorDialog();
                } else {
                    showServerErrorDialog();
                }
            }
        });
        /**/
    }

    private void showServerErrorDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
       // builder.setTitle("Server Error");
        builder.setMessage(getString(R.string.no_internet_connection));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performAppInitialization();
            }
        });
        builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.create().dismiss();
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        if (!this.isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }
    }

    private void performVersionRequest() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.METHOD_LABEL, Constants.VERSION_ALL_METHOD);

        RetrofitRequest.makeVersionRequest(this, params, new Callback<Version>() {
            @Override
            public void success(Version version, retrofit.client.Response response) {
                GCLog.e(version.toString());

                MakeModelVersionDB versionDB = ApplicationController.getMakeModelVersionDB();
                versionDB.insertVersions(version.getVersions());

                ApplicationController.getEventBus().post(new InitialRequestCompleteEvent(3, InitialRequestCompleteEvent.RequestType.VERSION_REQUEST));
            }

            @Override
            public void failure(RetrofitError error) {
                GCLog.e("error: " + error.getMessage());
                if (error.getCause() instanceof UnknownHostException) {
                    showNetworkConnectionErrorDialog();
                } else {
                    showServerErrorDialog();
                }
            }
        });
        /**/

    }


    protected void showLoginForm() {
        //GCLog.e("mishra","token : = "+token);
        if (token.isEmpty()) {
            progressLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.INVISIBLE);
            Animation animHide = AnimationUtils.loadAnimation(this, R.anim.view_hide_with_bounce_effect);
            final Animation animSowh = AnimationUtils.loadAnimation(this, R.anim.view_show_wd_bounce_effect);
            animHide.setFillAfter(true);
            progressLayout.setVisibility(View.GONE);


            if (CommonUtils.getBooleanSharedPreference(this, Constants.USER_LOGGEDIN, false)) {
                startMainActivity();
            } else {
                logo.setAnimation(animHide);
                animHide.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        formContainer.setVisibility(View.VISIBLE);
                        RelativeLayout loginForm = (RelativeLayout) findViewById(R.id.loginFormLayout);
                   /* progressLayout.setVisibility(View.VISIBLE);
                      String signSting=getResources().getString(R.string.sign_up);
                    Spanned result = Html.fromHtml(signSting);
                    signUpText.setText(result);
                    progressLayout.setAnimation(animSowh);*/

                        loginForm.setVisibility(View.VISIBLE);


                        loginForm.setAnimation(animSowh);

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            /*Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            formContainer.startAnimation(animation);*/


                //findViewById(R.id.splash_sec_logo).setVisibility(View.VISIBLE);
            }
        } else {
            loginUser("", "", true);
        }
    }

    @Override
    public void onValidationSucceeded() {
        clearErrors();

        progressDialog.setCancelable(false);
        progressDialog.show();

        loginUser(userEmail.getText().toString(), userPassword.getText().toString(), false);
    }

    protected void loginUser(String username, String password, boolean fromDeepLink) {
        MakeModelVersionDB db = ApplicationController.getMakeModelVersionDB();
        db.removeShowrooms();
        HashMap<String, String> params = new HashMap<String, String>();
        if (fromDeepLink) {
            params.put(Constants.TOKEN, token);
        } else {
            params.put("username", username);
            params.put("password", password);
        }

        GCLog.e("user rights: " + mUserRights);

        params.put(Constants.METHOD_LABEL, Constants.LOGIN_METHOD);
        RetrofitRequest.makeLoginUserRequest(this, params, new Callback<LoginUserModel>() {
            @Override
            public void success(LoginUserModel response, retrofit.client.Response res) {
                GCLog.e("status: " + response.getStatus());
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }

                if (response.getDealers() == null) {
                    if ("T".equalsIgnoreCase(response.getStatus())) {
                        CommonUtils.showToast(SplashActivity.this, "Logged in as: " + response.getUserDetail().getEmail(), Toast.LENGTH_LONG);
                        UserDetail userDetail = response.getUserDetail();
                        CommonUtils.setIntSharedPreference(SplashActivity.this, Constants.UC_DEALER_ID, Integer.parseInt(userDetail.getUcdealerId()));
                        CommonUtils.setIntSharedPreference(SplashActivity.this, Constants.DEALER_ID, Integer.parseInt(userDetail.getId()));

                        CommonUtils.setBooleanSharedPreference(SplashActivity.this, Constants.SERVICE_EXECUTIVE_LOGIN, false);
                        CommonUtils.setStringSharedPreference(SplashActivity.this, Constants.UC_DEALER_MOBILE, userDetail.getMobile());
                        CommonUtils.setBooleanSharedPreference(SplashActivity.this, Constants.USER_LOGGEDIN, true);
                        CommonUtils.setStringSharedPreference(SplashActivity.this, Constants.UC_DEALER_EMAIL, userDetail.getEmail());
                        CommonUtils.setStringSharedPreference(SplashActivity.this, Constants.UC_DEALER_USERNAME, userDetail.getUsername());
                        CommonUtils.setStringSharedPreference(SplashActivity.this, Constants.UC_DEALER_PASSWORD, userDetail.getDealerPassword());
                        CommonUtils.setStringSharedPreference(SplashActivity.this, Constants.UC_DEALER_NAME, userDetail.getName());
                        CommonUtils.setStringSharedPreference(SplashActivity.this, Constants.UC_DEALER_CITY, userDetail.getDealerCity());
                        CommonUtils.setIntSharedPreference(SplashActivity.this, Constants.UC_DEALER_NUM_INVENTORIES_TO_LIST, Integer.parseInt(userDetail.getNumInventoriesList()));
                        CommonUtils.setStringSharedPreference(SplashActivity.this, Constants.UC_WARRANTY_ONLY_DEALER, userDetail.getWarrantyOnlyDealer());
                        CommonUtils.setStringSharedPreference(SplashActivity.this, Constants.UC_CARDEKHO_INVENTORY, userDetail.getCardekhoInventory());
                        ApplicationController.getMakeModelVersionDB().insertShowrooms(response.getShowrooms());
                        // code for registering user with pushwoosh server
                        PreferenceSettings.openDataBase(SplashActivity.this);
                        PreferenceSettings.setDealerId(userDetail.getId());
                        PreferenceSettings.setDealerEmailId(userDetail.getEmail());
                        GCLog.e("GCM id, " + PreferenceSettings.getGCM());
                        syncStockService();
                        if (PreferenceSettings.getGCM() != null) {
                            GCMIntentService.sendDataToServerFromLogin(SplashActivity.this);
                        }
                        // 0 for Active Dealer and 1 for deactive Dealer for Chat Notification
                        //  GCMIntentService.startOrStopChatNotification(SplashActivity.this,0);

                        if (token.isEmpty()) {
                            startMainActivity();
                        } else {
                            LinkedList<Intent> stack = new LinkedList<Intent>();
                            String leadType = token.substring(0, 1);
                            Intent intent = null;
                            stack.add(new Intent(SplashActivity.this, MainActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                            switch (leadType) {
                                case "b":
                                    stack.add(new Intent(SplashActivity.this, LeadsManageActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                                    intent = new Intent(SplashActivity.this, LeadAddActivity.class);
                                    break;
                                case "s":
                                    if (response.getUserDetail().getIsSeller() == 1) {
                                        stack.add(new Intent(SplashActivity.this, SellerLeadsActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                                        intent = new Intent(SplashActivity.this, SellerLeadsDetailPageActivity.class);
                                    } else {
                                        CommonUtils.showToast(SplashActivity.this, getString(R.string.warranty_only_dealer_message), Toast.LENGTH_LONG);
                                    }
                                    break;
                                case "f":
                                    CommonUtils.setIntSharedPreference(SplashActivity.this, Constants.SFA_USER_ID, response.getUserDetail().getSfaUserId());
                                    break;
                            }
                            if (intent != null) {
                                intent.putExtra(Constants.TOKEN, token);
                                intent.putExtra(Constants.FROM_DEEPLINK, true);
                                stack.add(intent);
                            }
                            startActivities(stack.toArray(new Intent[stack.size()]));
                            SplashActivity.this.finish();
                        }
                    } else {
                        CommonUtils.showToast(SplashActivity.this, response.getMessage(), Toast.LENGTH_SHORT);
                        if (!token.isEmpty()) {
                            token = "";
                            showLoginForm();
                        }
                    }
                } else {
                    CommonUtils.setStringSharedPreference(SplashActivity.this, Constants.SERVICE_EXECUTIVE_ID, response.getExecutiveData().getExecutiveId());
                    CommonUtils.setBooleanSharedPreference(SplashActivity.this, Constants.SERVICE_EXECUTIVE_LOGIN, true);

                    MakeModelVersionDB db = ApplicationController.getMakeModelVersionDB();

                    for (int i = 0; i < response.getDealers().size(); i++) {
                        db.insertShowrooms(response.getDealers().get(i).getShowrooms());
                    }

                    showDealerChooserDialog(response.getDealers());
                }
                GCLog.e("User logged in, " + response.toString());
            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                CommonUtils.showErrorToast(SplashActivity.this, error, Toast.LENGTH_SHORT);
                finish();

            }
        });
        /**/
    }

    private void syncStockService() {
        Intent intent = new Intent(getBaseContext(), SyncStocksService.class);
        getBaseContext().stopService(intent);
        getBaseContext().startService(intent);
    }

    private void showDealerChooserDialog(ArrayList<DealerData> dealers) {
        if ((dealers.size() > 0) && !dealers.isEmpty()) {
            if(!SplashActivity.this.isFinishing()) {
                MakeModelVersionDB db = ApplicationController.getMakeModelVersionDB();
                db.removeDealers();
                long insertid = db.insertDealers(dealers);

                DealerPickerFragment fragment = DealerPickerFragment.newInstance(getString(R.string.select_dealer), selectedIndex);

                fragment.show(getSupportFragmentManager(), "dealer-pick-fragment");
            }
        } else {
            CommonUtils.showToast(this, "No dealers present. Please try after sometime.", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {

        clearErrors();

        if (failedView instanceof EditText) {
            ((EditText) failedView).setError(failedRule.getFailureMessage());
            failedView.requestFocus();
        }


        CommonUtils.shakeView(this, failedView);
    }

    private void clearErrors() {
        userEmail.clearFocus();
        userPassword.clearFocus();
    }

    @Override
    public void onViewValidationSucceeded(View succeededView) {
        clearErrors();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                GCLog.e("Login button clicked");
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER, "Login Screen", "Login", "Perform login", "xyz", 1);
                mValidator.validate();
                break;
        }
    }


    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        GCLog.e("UCDID: " + CommonUtils.getIntSharedPreference(this, Constants.UC_DEALER_ID, -1));
        startActivity(intent);
        finish();

    }

    @Override
    public void onDealerSelected(DealerPickerFragment fragment, DealerData dealer, int index) {
        selectedIndex = index;
        CommonUtils.setIntSharedPreference(this, Constants.DEALER_ID, Integer.parseInt(dealer.getDealerId()));
        CommonUtils.setBooleanSharedPreference(this, Constants.USER_LOGGEDIN, true);
        CommonUtils.setIntSharedPreference(this, Constants.UC_DEALER_ID, Integer.parseInt(dealer.getUCdid()));
        CommonUtils.setStringSharedPreference(this, Constants.UC_DEALER_EMAIL, dealer.getmEmail());
        CommonUtils.setStringSharedPreference(this, Constants.UC_DEALER_MOBILE, dealer.getMobileSms());
        CommonUtils.setStringSharedPreference(this, Constants.UC_DEALER_USERNAME, dealer.getmUsername());
        CommonUtils.setStringSharedPreference(this, Constants.UC_DEALER_PASSWORD, dealer.getDealerPassword());
        startMainActivity();
    }

    @Override
    public void onNoInternetConnection(NetworkEvent networkEvent) {
        if (networkEvent.isShowFullPageError()) {
            showNetworkErrorDialog();
        } else {
            CommonUtils.showToast(this, networkEvent.getNetworkError().name(), Toast.LENGTH_SHORT);
        }
    }

    private void showNetworkErrorDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.network_error);
        builder.setMessage("Internet connection is required.");
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performAppInitialization();
            }
        });
        builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.create().dismiss();
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        if (!this.isFinishing() && !dialog.isShowing()) {
            dialog.show();
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
        GCLog.e("Versions inserted: any error? " + event.isError());
        if (event.isError()) {
            showDBErrorDialog();
        } else {

        }
    }

    private void showDBErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.application_error);
        builder.setMessage(R.string.application_error_message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_SPLASH_SCREEN,
                        Constants.CATEGORY_SPLASH_SCREEN,
                        Constants.ACTION_TAP,
                        Constants.LABEL_DBWRITE_ERROR,
                        0);
                makeRequests();

            }
        });

        AlertDialog dialog = builder.create();

        if (!this.isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }
    }

}
