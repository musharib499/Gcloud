package com.gcloud.gaadi.ui;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ListPopupWindow;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.annotations.Required;
import com.gcloud.gaadi.annotations.rules.Rule;
import com.gcloud.gaadi.annotations.rules.Validator;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.InsuranceDB;
import com.gcloud.gaadi.insurance.InsuranceBackgroundImageUploadService;
import com.gcloud.gaadi.insurance.InsuranceDashboardModel;
import com.gcloud.gaadi.model.InsuranceInspectedCarData;
import com.gcloud.gaadi.model.PaymentDetailsModel;
import com.gcloud.gaadi.model.QuoteDetails;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.service.DatabaseInsertionService;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imageuploadlib.Activity.CameraPriorityActivity;
import com.imageuploadlib.Fragments.CameraItemsFragment;
import com.imageuploadlib.PhotosLibrary;
import com.imageuploadlib.Utils.PhotoParams;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.MaterialTextView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class PaymentModeScreen extends BaseActivity implements View.OnClickListener, Validator.ValidationListener {

    private static final int REQUEST_CODE_IMAGE = 101;
    private final Uri INSURANCE_IMAGE_URI = Uri.parse("content://"
            + Constants.INSURANCE_CONTENT_AUTHORITY + "/" + InsuranceDB.TABLE_IMAGES);
    ArrayList<String> bankList;
    String selectedIssuingBankName = "";
    private ActionBar mActionBar;
    private Validator mValidator;
    private String imageUri = "";
    private int mPolicyID;
    private String mInsuranceCaseID;
    private String mInsurerID, processId, carId;

    @Required(order = 1, messageResId = R.string.error_cheque_number_required)
    private MaterialEditText mChequeNumber;

    @Required(order = 2, messageResId = R.string.error_issuing_bank_name_required)
    private MaterialAutoCompleteTextView mIssuingBank;
    private ScrollView scrollView;
    private QuoteDetails mQuoteDetails;
    private String mAgentID;
    private boolean mIsDataUploaded;
    private boolean mIsImageUploaded;
    private boolean mIsPayingThroughCheque = true;
    private LinearLayout chequeInfoView;
    private RelativeLayout chequeLayout;
    private ImageView chequeDelete, chequeImage/*, chequeTick*/;
    private TextView chequeRetry;
    private MaterialTextView chequeInFavour;
    private ProgressBar progressBar, fullScreenProgressBar;
    private GCProgressDialog progressDialog;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.QUOTE_DETAILS, mQuoteDetails);
        outState.putStringArrayList(Constants.BANK_LIST, bankList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mQuoteDetails = (QuoteDetails) savedInstanceState.getSerializable(Constants.QUOTE_DETAILS);
        bankList = savedInstanceState.getStringArrayList(Constants.BANK_LIST);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.layout_payment_mode_screen, frameLayout);
        mActionBar = getSupportActionBar();
        mActionBar.hide();
//        mActionBar.setDisplayHomeAsUpEnabled(true);

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        chequeInFavour = (MaterialTextView) findViewById(R.id.cheque_in_favour);
        chequeInfoView = (LinearLayout) findViewById(R.id.chequeInfo);
        chequeLayout = (RelativeLayout) findViewById(R.id.cheque_layout);
        chequeLayout.setOnClickListener(this);
        chequeDelete = (ImageView) findViewById(R.id.cheque_delete);
        chequeDelete.setOnClickListener(this);
        chequeRetry = (TextView) findViewById(R.id.cheque_retry);
        chequeImage = (ImageView) findViewById(R.id.cheque_image);
        chequeRetry = (TextView) findViewById(R.id.cheque_retry);
//        chequeTick = (ImageView) findViewById(R.id.chequeTick);
        progressBar = (ProgressBar) findViewById(R.id.cheque_progressBar);
        getBankList();
        fullScreenProgressBar = (ProgressBar) findViewById(R.id.fullScreenProgressBar);
        progressDialog = new GCProgressDialog(this, this, null);
        progressDialog.setCancelable(false);

        Bundle intentData = getIntent().getExtras();
        if (intentData != null) {
            mQuoteDetails = (QuoteDetails) intentData.get(Constants.QUOTE_DETAILS);
            processId = intentData.getString(Constants.PROCESS_ID);
            carId = ((InsuranceInspectedCarData) intentData.get(Constants.INSURANCE_INSPECTED_CAR_DATA)).getCarId();
            /*mInsuranceCaseID = intentData.getString(Constants.INSURANCE_CASE_ID);
            mInsurerID = intentData.getString(Constants.INSURER_ID);
            mAgentID = intentData.getString(Constants.AGENT_ID);*/

            ((TextView) findViewById(R.id.tv_premiumAmount)).setText(mQuoteDetails.getNetPremium());
            chequeInFavour.setText(mQuoteDetails.getChequeInFavour());

        } else if (savedInstanceState != null) {
            mQuoteDetails = (QuoteDetails) savedInstanceState.getSerializable(Constants.QUOTE_DETAILS);
            bankList = savedInstanceState.getStringArrayList(Constants.BANK_LIST);
            mInsuranceCaseID = savedInstanceState.getString(Constants.INSURANCE_CASE_ID);
            mInsurerID = savedInstanceState.getString(Constants.INSURER_ID);
            mAgentID = savedInstanceState.getString(Constants.AGENT_ID);

            ((TextView) findViewById(R.id.tv_premiumAmount)).setText(mQuoteDetails.getNetPremium());
            chequeInFavour.setText(mQuoteDetails.getChequeInFavour());
        }

        chequeInFavour.startLabelAnimation();
        mChequeNumber = (MaterialEditText) findViewById(R.id.etChequeNumber);

        mIssuingBank = (MaterialAutoCompleteTextView) findViewById(R.id.tvIssuingBank);
        mIssuingBank.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bankList));
        mIssuingBank.setThreshold(1);

        mIssuingBank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedIssuingBankName = mIssuingBank.getText().toString();
            }
        });

//        mIssuingBank.setOnClickListener(this);

        /*findViewById(R.id.imageButton).setOnClickListener(this);
        findViewById(R.id.retry).setOnClickListener(this);*/

        findViewById(R.id.btnSubmit).setOnClickListener(this);
        if (CommonUtils.getIntSharedPreference(this, Constants.IS_CASH, 0) == 1) {
            RadioButton cash = (RadioButton) findViewById(R.id.radioCash);
            RadioButton cheque = (RadioButton) findViewById(R.id.radioCheque);

            cash.setVisibility(View.VISIBLE);

            ((RadioGroup) findViewById(R.id.radioGroupPaymentMode)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {/*
                if (checkedId == R.id.radioCheque && chequeInfoView.getVisibility() != View.VISIBLE) {
                    mIsPayingThroughCheque = true;
                    chequeInfoView.setVisibility(View.VISIBLE);
                    return;
                }
                mIsPayingThroughCheque = false;
                if (chequeInfoView.getVisibility() == View.VISIBLE) {
                    chequeInfoView.setVisibility(View.GONE);
                }*/
                    if (checkedId == R.id.radioCheque) {
                        chequeInfoView.setVisibility(View.VISIBLE);
                    /*cash.setBackgroundColor(getResources().getColor(R.color.insurance_dashboard_upper_bg));
                    cheque.setBackgroundColor(getResources().getColor(R.color.insurance_inspected_bg));*/
                        mIsPayingThroughCheque = true;
                    } else {
                        chequeInfoView.setVisibility(View.GONE);
                    /*cheque.setBackgroundColor(getResources().getColor(R.color.insurance_dashboard_upper_bg));
                    cash.setBackgroundColor(getResources().getColor(R.color.insurance_inspected_bg));*/
                        mIsPayingThroughCheque = false;
                    }
                }

            });
        }
    }

    private void getBankList() {
        String str = CommonUtils.getStringSharedPreference(PaymentModeScreen.this, Constants.BANK_LIST, "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        bankList = gson.fromJson(str, type);
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
        CommonUtils.startActivityTransition(PaymentModeScreen.this, Constants.TRANSITION_RIIGHT);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            /*case R.id.tvIssuingBank:
                if(bankList!=null)
                showListPopupWindow(mIssuingBank);
                break;*/

            case R.id.iv_actionBack:
                onBackPressed();
                break;

            case R.id.btnSubmit:
                findViewById(R.id.btnSubmit).setEnabled(false);
                mValidator.validate();
                break;

            /*case R.id.imageButton:
                launchImageSelector();
                break;*/

            case R.id.cheque_layout:
                /*ArrayList<String> photoName = new ArrayList<>();
                photoName.add("Cheque Copy");*/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !CommonUtils.checkForPermission(this,
                        new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.REQUEST_PERMISSION_CAMERA, "Camera and Storage")) {
                    return;
                }
                PhotoParams params = new PhotoParams("Cheque Copy");
                params.setMode(PhotoParams.MODE.CAMERA_PRIORITY);
                params.setOrientation(PhotoParams.CameraOrientation.LANDSCAPE);
                params.setRequestCode(REQUEST_CODE_IMAGE);
                params.setNoOfPhotos(1);
                PhotosLibrary.collectPhotos(this, params);
                break;

            case R.id.cheque_delete:
//                chequeImage.setImageBitmap(null);
                Glide.with(ApplicationController.getInstance())
                        .load(android.R.color.transparent)
                        .into(chequeImage);
                chequeDelete.setVisibility(View.GONE);
//                chequeTick.setImageResource(R.drawable.tick);
                imageUri = "";
                if (chequeRetry.getVisibility() == View.VISIBLE)
                    chequeRetry.setVisibility(View.GONE);
                break;
        }

    }


    private void launchImageSelector() {
        PhotoParams params = new PhotoParams();
        params.setImageName("One");
        Intent intent = new Intent(this, CameraPriorityActivity.class);
        intent.putExtra(CameraItemsFragment.PHOTO_PARAMS, params);
        startActivityForResult(intent, REQUEST_CODE_IMAGE);
    }


    private void showListPopupWindow(final MaterialTextView textView) {
        final ListPopupWindow listPopupWindow = new ListPopupWindow(
                this);
        listPopupWindow.setAdapter(new ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line, bankList));
        listPopupWindow.setAnchorView(textView);
        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);

        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPopupWindow.dismiss();
                textView.setText(bankList.get(position));
            }
        });
        listPopupWindow.show();
    }

    @Override
    public void onValidationSucceeded() {
        if(!selectedIssuingBankName.equals(mIssuingBank.getText().toString())) {
            onValidationFailed(mIssuingBank, new Rule<View>(getString(R.string.error_issuing_bank_name_required)) {
                @Override
                public boolean isValid(View view) {
                    return false;
                }
            });
            return;
        }
            if (mIsPayingThroughCheque) {
                if (imageUri.isEmpty()) {
                    //Toast.makeText(this, "Cheque Copy is required", Toast.LENGTH_LONG).show();
                    findViewById(R.id.btnSubmit).setEnabled(true);
                    CommonUtils.showToast(this, "Cheque Copy is required", Toast.LENGTH_LONG);
                    return;
                }
            }
            putPaymentMode();

    }

    private void putPaymentMode() {
//        fullScreenProgressBar.setVisibility(View.VISIBLE);
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
        HashMap<String, String> mParams = new HashMap<>();
        if (mIsPayingThroughCheque) {

            if (!mIsImageUploaded) {
                if (imageUri != null) {
                    uploadChequeImage();
                } else {
                    CommonUtils.showToast(this, "Please upload cheque image", Toast.LENGTH_SHORT);
                }
                return;
            }
            mParams.put(Constants.PAYMEMT_MODE, Constants.PAYMEMT_CHEQUE);
            mParams.put(Constants.CHEQUE_NUMBER, mChequeNumber.getText().toString());
            mParams.put(Constants.ISSUING_BANK, mIssuingBank.getText().toString());
        } else {
            mParams.put(Constants.PAYMEMT_MODE, Constants.PAYMEMT_CASH);
            mIsImageUploaded = true;
        }

        mParams.put(Constants.METHOD_LABEL, "selectInsurancePaymentMode");
        mParams.put("background_upload", "1");
        /*mParams.put(Constants.ACTION, Constants.PUT_PAYMENT_DATA);
        mParams.put(Constants.INSURANCE_CASE_ID, mInsuranceCaseID);
        mParams.put(Constants.INSURER_ID, mInsurerID);
        mParams.put(Constants.AGENT_ID_API, mAgentID);*/
        mParams.put("car_id", carId);
        mParams.put("process_id", processId);
        RetrofitRequest.putPaymentDetails(mParams, new Callback<PaymentDetailsModel>() {
            @Override
            public void success(PaymentDetailsModel paymentDetailsModel, Response response) {
//                fullScreenProgressBar.setVisibility(View.GONE);
                if (paymentDetailsModel.getStatus().equals("T")) {
                    mIsDataUploaded = true;
                    mPolicyID = paymentDetailsModel.getPolicyId();
//                    if (mIsImageUploaded) {
//                        launchThankYouScreen(mPolicyID);
//                    }

                    InsuranceDashboardModel model = new Gson()
                            .fromJson(CommonUtils
                                            .getStringSharedPreference(PaymentModeScreen.this,
                                                    Constants.INSURANCE_DASHBOARD_OFFLINE_DATA, ""),
                                    InsuranceDashboardModel.class);
                    if (model != null) {
                        try {
                            model.setData(paymentDetailsModel.getDashboard().getData());
                            model.getMonthwise().set(model.getMonthwise().size() - 1,
                                    paymentDetailsModel.getDashboard().getMonthwise().get(0));
                            CommonUtils.setStringSharedPreference(PaymentModeScreen.this,
                                    Constants.INSURANCE_DASHBOARD_OFFLINE_DATA,
                                    new Gson().toJson(model));
                        } catch (Exception e) {

                        }
                    }
                    launchThankYouScreen(mPolicyID);
                } else {
                    findViewById(R.id.btnSubmit).setEnabled(true);
                    CommonUtils.showToast(PaymentModeScreen.this,
                            "Error: " + paymentDetailsModel.getError(), Toast.LENGTH_SHORT);
                }
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                findViewById(R.id.btnSubmit).setEnabled(true);
//                fullScreenProgressBar.setVisibility(View.GONE);
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                CommonUtils.showErrorToast(PaymentModeScreen.this, error, Toast.LENGTH_SHORT);
            }
        });

    }

    private void launchThankYouScreen(int policyID) {
        Intent databaseIntent = new Intent(this, DatabaseInsertionService.class);
        databaseIntent.putExtra(Constants.ACTION, "update");
        databaseIntent.putExtra(Constants.PROVIDER_URI, INSURANCE_IMAGE_URI);
        databaseIntent.putExtra(Constants.SELECTION, InsuranceDB.COLUMN_PROCESS_ID + " = ?");
        databaseIntent.putExtra(Constants.SELECTION_ARGS, new String[]{processId});

        ContentValues values = new ContentValues();
        values.put(InsuranceDB.COLUMN_POLICY_ID, policyID);

        databaseIntent.putExtra(Constants.CONTENT_VALUES, values);
        startService(databaseIntent);

        Intent intent = new Intent(PaymentModeScreen.this, ThankYouInsurance.class);
        intent.putExtra(Constants.INSURANCE_POLICY_ID, policyID);
        startActivity(intent);
        CommonUtils.startActivityTransition(PaymentModeScreen.this, Constants.TRANSITION_LEFT);
        setResult(RESULT_OK);
        finish();
        finishActivity(Constants.INSURANCE_CASE_FOR_INSPECTED_CAR);

    }

    private void uploadChequeImage() {
        /*Intent intent = new Intent(this, DatabaseInsertionService.class);
        intent.putExtra(Constants.ACTION, "insert");
        intent.putExtra(Constants.PROVIDER_URI, INSURANCE_IMAGE_URI);*/

        ContentValues values = new ContentValues();
        values.put(InsuranceDB.COLUMN_CAR_ID, (carId != null && !carId.isEmpty()) ? Integer.parseInt(carId) : 0);
        values.put(InsuranceDB.COLUMN_PROCESS_ID, Integer.parseInt(processId));
        values.put(InsuranceDB.COLUMN_DOC_NAME, "doc_cheque_copy");
        values.put(InsuranceDB.COLUMN_IMAGE_PATH, imageUri);

        /*intent.putExtra(Constants.CONTENT_VALUES, values);
        startService(intent);*/

        getContentResolver().insert(INSURANCE_IMAGE_URI, values);

        startService(new Intent(this, InsuranceBackgroundImageUploadService.class));

        mIsImageUploaded = true;
        putPaymentMode();
        /*progressBar.setVisibility(View.VISIBLE);
        JSONObject params = new JSONObject();
        try {
            params.put(Constants.METHOD_LABEL, "uploadInsuranceDocuments");
            params.put("process_id", processId);
            params.put("document_name", "doc_cheque_copy");
            params.put("car_id", carId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitRequest.uploadInsuranceDocument(imageUri,
                params,
                new Callback<GeneralResponse>() {
                    @Override
                    public void success(GeneralResponse response, Response res) {
                        progressBar.setVisibility(View.GONE);
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        if (response.getStatus().equals("T")) {
                            mIsImageUploaded = true;
//                            if (mIsDataUploaded) {
//                                launchThankYouScreen(mPolicyID);
//                            }
                            CommonUtils.showToast(PaymentModeScreen.this, "All Images Uploaded Successfully", Toast.LENGTH_SHORT);
                            putPaymentMode();
                        } else {
                            findViewById(R.id.btnSubmit).setEnabled(true);
                            CommonUtils.showToast(PaymentModeScreen.this,
                                    "Error: " + response.getMessage(), Toast.LENGTH_SHORT);
                            chequeRetry.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            chequeRetry.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    chequeRetry.setVisibility(View.GONE);
                                    putPaymentMode();
                                }
                            });
                        }


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        findViewById(R.id.btnSubmit).setEnabled(true);
                        CommonUtils.showErrorToast(PaymentModeScreen.this, error, Toast.LENGTH_SHORT);
                        chequeRetry.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        chequeRetry.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                progressBar.setVisibility(View.VISIBLE);
                                chequeRetry.setVisibility(View.GONE);
                                uploadChequeImage();
                            }
                        });
                    }
                });*/

    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        findViewById(R.id.btnSubmit).setEnabled(true);

        if (failedView instanceof MaterialEditText) {
            ((MaterialEditText) failedView).setError(failedRule.getFailureMessage());
            failedView.requestFocus();
        } else {
            CommonUtils.showToast(this, failedRule.getFailureMessage(), Toast.LENGTH_SHORT);
        }

        CommonUtils.scrollTo(scrollView, failedView);
        CommonUtils.shakeView(this, failedView);

    }

    @Override
    public void onViewValidationSucceeded(View succeededView) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_IMAGE:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> list = null;
                    if (data != null) {
                        list = data.getStringArrayListExtra(Constants.RESULT_IMAGES);

                        imageUri = list.get(0);
                        if (imageUri != null) {
                            GCLog.i("on Bind View Position");
                            Glide.with(ApplicationController.getInstance())
                                    .load("file://" + imageUri)
                                    .into(chequeImage);
//                            chequeImage.setImageURI(Uri.fromFile(new File(imageUri)));
//                            chequeTick.setImageResource(R.drawable.tick_active);
                            chequeDelete.setVisibility(View.VISIBLE);
                        }
                    }

                }
                break;

            case Constants.INSURANCE_CASE_FOR_INSPECTED_CAR:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                    finishActivity(Constants.INSURANCE_CASE_FOR_INSPECTED_CAR);
                }
                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onClick(findViewById(R.id.cheque_layout));
            }
        }
    }
}
