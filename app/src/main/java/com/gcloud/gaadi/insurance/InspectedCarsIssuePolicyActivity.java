package com.gcloud.gaadi.insurance;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.annotations.Required;
import com.gcloud.gaadi.annotations.ValueChecker;
import com.gcloud.gaadi.annotations.rules.Rule;
import com.gcloud.gaadi.annotations.rules.Validator;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.FinanceCompany;
import com.gcloud.gaadi.model.InsuranceInspectedCarData;
import com.gcloud.gaadi.ui.BaseActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

public class InspectedCarsIssuePolicyActivity extends BaseActivity implements Validator.ValidationListener {
    private ActionBar mActionBar;
    private ArrayList<String> mInsuranceCities;
    private ArrayList<FinanceCompany> financeCompanyList;
    private String mAgentId = "";
    InsuranceInspectedCarData insuranceInspectedCarData;
    TextView tv_makeModel, tv_regNo, tv_regYear, tv_fuelType, tv_Price, tv_kmsDriven, tv_Next;

    @Required(order = 1, messageResId = R.string.kit_value_required)
    @ValueChecker(order = 2, maxValue = 50000, replaceChars = ",.", messageResId = R.string.max_value_50000)
    MaterialEditText et_cngLpgLitValue;
    Validator mValidator;
    ImageView inspectedCarImage, iv_color, iv_makeLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.inspected_cars_issue_policy, frameLayout);
        mActionBar = getSupportActionBar();
        mActionBar.hide();
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
        // mActionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        mInsuranceCities = intent.getStringArrayListExtra(Constants.INSURANCE_CITIES);
        financeCompanyList = (ArrayList<FinanceCompany>) intent.getSerializableExtra(Constants.FINANCE_COMPANY);
        mAgentId = intent.getStringExtra(Constants.AGENT_ID);
        insuranceInspectedCarData = (InsuranceInspectedCarData) intent.getSerializableExtra(Constants.INSURANCE_INSPECTED_CAR_DATA);

        tv_makeModel = (TextView) findViewById(R.id.tv_makeModel);
        iv_makeLogo = (ImageView) findViewById(R.id.makeLogo);
        tv_regNo = (TextView) findViewById(R.id.tv_regNo);
        tv_regYear = (TextView) findViewById(R.id.tv_regYear);
        inspectedCarImage = (ImageView) findViewById(R.id.stockImage);
        tv_fuelType = (TextView) findViewById(R.id.tv_fuelType);
        tv_Price = (TextView) findViewById(R.id.tv_Price);
        tv_kmsDriven = (TextView) findViewById(R.id.tv_kmsDriven);
        iv_color = (ImageView) findViewById(R.id.iv_color);
        et_cngLpgLitValue = (MaterialEditText) findViewById(R.id.et_CngLpgKitValue);
        tv_Next = (TextView) findViewById(R.id.tv_Next);
        et_cngLpgLitValue.addTextChangedListener(new GenericTextWatcher(et_cngLpgLitValue));
        iv_makeLogo.setImageResource(ApplicationController.makeLogoMap.get(insuranceInspectedCarData.getMakeId()));
        findViewById(R.id.iv_actionBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tv_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mValidator.validate();
            }
        });

        tv_makeModel.setText(insuranceInspectedCarData.getModel() + " " + insuranceInspectedCarData.getVersion());
        tv_regNo.setText("Reg. No. " + insuranceInspectedCarData.getRegNo());
        tv_regYear.setText(insuranceInspectedCarData.getRegYear());
        tv_fuelType.setText(insuranceInspectedCarData.getFuelType());
        tv_Price.setText(insuranceInspectedCarData.getPrice());
        tv_kmsDriven.setText(insuranceInspectedCarData.getKmsDriven() + " kms");
        if ((insuranceInspectedCarData.getImage() != null) && !insuranceInspectedCarData.getImage().isEmpty()) {
            Glide.with(InspectedCarsIssuePolicyActivity.this)
                    .load(insuranceInspectedCarData.getImage())
                    .placeholder(R.drawable.image_load_default_small)
                    .crossFade()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(inspectedCarImage);

        } else {

            Glide.with(InspectedCarsIssuePolicyActivity.this)
                    .load(R.drawable.no_image_default_small)
                    .centerCrop()
                    .into(inspectedCarImage);

        }
        if (insuranceInspectedCarData.getColour() != null && !insuranceInspectedCarData.getColour().equals("")) {
            GradientDrawable bgShape = (GradientDrawable) iv_color.getBackground();
            bgShape.setColor(Color.parseColor(insuranceInspectedCarData.getColour()));
        } else {
            GradientDrawable bgShape = (GradientDrawable) iv_color.getBackground();
            bgShape.setColor(Color.parseColor("#FFFFFF"));
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.INSURANCE_CASE_FOR_INSPECTED_CAR:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CommonUtils.startActivityTransition(InspectedCarsIssuePolicyActivity.this, Constants.TRANSITION_RIIGHT);
    }

    @Override
    public void onValidationSucceeded() {
        Intent intent = new Intent(InspectedCarsIssuePolicyActivity.this, DealerQuoteScreen.class);

        intent.putExtra(Constants.INSURANCE_CITIES, mInsuranceCities);
        intent.putExtra(Constants.FINANCE_COMPANY, financeCompanyList);
        intent.putExtra(Constants.INSURANCE_INSPECTED_CAR_DATA, insuranceInspectedCarData);
        intent.putExtra(Constants.SELECTED_CASE, Constants.INSPECTED_CARS);
        intent.putExtra(Constants.AGENT_ID, mAgentId);
        intent.putExtra(Constants.CNG_PNG_VALUE, et_cngLpgLitValue.getText().toString().trim().replace(",", ""));
        startActivityForResult(intent, Constants.INSURANCE_CASE_FOR_INSPECTED_CAR);
        CommonUtils.startActivityTransition(InspectedCarsIssuePolicyActivity.this, Constants.TRANSITION_LEFT);
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        if (failedView instanceof EditText) {
            failedView.requestFocus();
            ((EditText) failedView).setError(failedRule.getFailureMessage());
            CommonUtils.shakeView(this, failedView);
            return;
        }
    }

    @Override
    public void onViewValidationSucceeded(View succeededView) {

    }
}
