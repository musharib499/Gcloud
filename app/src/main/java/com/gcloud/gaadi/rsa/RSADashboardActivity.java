package com.gcloud.gaadi.rsa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.rsa.RSAModel.RSADashboardResponseModel;
import com.gcloud.gaadi.rsa.RSAModel.RSAPackage;
import com.gcloud.gaadi.ui.BaseActivity;
import com.gcloud.gaadi.ui.GCProgressDialog;
import com.gcloud.gaadi.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by shivani on 7/3/16.
 */
public class RSADashboardActivity extends BaseActivity implements View.OnClickListener {
    LinearLayout carsInStock, carsNotInStock;
    TextView tvAmount, tvToalCases;
    ArrayList<RSAPackage> rsaPackageList;
    private GCProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_gaadi_rsa_dashboard);
        getLayoutInflater().inflate(R.layout.activity_gaadi_rsa_dashboard, frameLayout);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        carsInStock = (LinearLayout) findViewById(R.id.carsInStock);
        carsNotInStock = (LinearLayout) findViewById(R.id.carsNotInStock);
        tvAmount = (TextView) findViewById(R.id.totalAmount);
        tvToalCases = (TextView) findViewById(R.id.totalCasesCount);
        CommonUtils.insertCommaIntoNumber(tvAmount, "0", "##,##,###");
        progressDialog = new GCProgressDialog(this, this);
        progressDialog.setCancelable(false);
        carsInStock.setEnabled(false);
        carsNotInStock.setEnabled(false);
        carsInStock.setOnClickListener(this);
        carsNotInStock.setOnClickListener(this);
        progressDialog.show();
        fetchRSADashBoardData();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void fetchRSADashBoardData() {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.METHOD_LABEL, Constants.GET_RSA_STATS_METHOD);


        RetrofitRequest.getRSAStats(params, new Callback<RSADashboardResponseModel>() {
            @Override
            public void success(RSADashboardResponseModel rsaDashboardResponseModel, Response response) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if ("T".equalsIgnoreCase(rsaDashboardResponseModel.getStatus())) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    carsInStock.setEnabled(true);
                    carsNotInStock.setEnabled(true);
                    rsaPackageList = rsaDashboardResponseModel.getRsaPackages();
                    CommonUtils.insertCommaIntoNumber(tvAmount, String.valueOf(rsaDashboardResponseModel.getBalance()), "##,##,###");
                    if (rsaDashboardResponseModel.getTotalCases() > 1) {
                        tvToalCases.setText(rsaDashboardResponseModel.getTotalCases() + " RSAs Issued");
                    } else {
                        tvToalCases.setText(rsaDashboardResponseModel.getTotalCases() + " RSA Issued");
                    }

                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                CommonUtils.showErrorToast(RSADashboardActivity.this, error, Toast.LENGTH_SHORT);

            }

        });
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.carsInStock:
                intent = new Intent(this, RSAAvailableCarsInStockActivity.class);
                intent.putExtra(Constants.RSA_PACKAGE, rsaPackageList);
                startActivityForResult(intent, Constants.RSA_CAR_SELECTED_REQUEST_CODE);
                break;
            case R.id.carsNotInStock:
                intent = new Intent(this, RSACustomerInfoActivity.class);
                intent.putExtra(Constants.SOURCE, Constants.RSA_TAB);
                intent.putExtra(Constants.RSA_PACKAGE, rsaPackageList);
                startActivityForResult(intent, Constants.RSA_CAR_SELECTED_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.RSA_CAR_SELECTED_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    progressDialog.show();
                    fetchRSADashBoardData();

                }
                break;
        }

    }
}
