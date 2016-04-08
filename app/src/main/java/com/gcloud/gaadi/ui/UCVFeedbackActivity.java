package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.UsedCarDetailsModel;
import com.gcloud.gaadi.model.UsedCarValuationModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;

import java.net.UnknownHostException;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UCVFeedbackActivity extends BaseActivity {

    TextView tv_excellentValue, tv_goodValue, tv_fairValue,added_success,tv_carmodel,tv_carowner , tv_know_more , tv_price_range;
    Button btnSubmit,btn_back_to_previus;
    RelativeLayout btnBack;
    private GCProgressDialog progressDialog;
  //  RatingBar ratingBar;
    UsedCarValuationModel.UsedCarData usedCarData;
    RadioGroup priceQuotedRgrp;
    LinearLayout  layout_feedback,submit_layout;
    CardView card_condition;

    private String excellent,good,fair,model,owner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.ucvfeedback_activity, frameLayout);
        progressDialog = new GCProgressDialog(this, (Activity)this);
        //tv_excellentValue = (TextView)findViewById(R.id.excellentValue);
        tv_goodValue = (TextView)findViewById(R.id.goodValue);
        //tv_fairValue = (TextView) findViewById(R.id.fairValue);
        tv_carmodel =(TextView)findViewById(R.id.carmodel);
        tv_know_more = (TextView) findViewById(R.id.know_more);
        tv_carowner =(TextView)findViewById(R.id.carowner);
        tv_price_range =(TextView)findViewById(R.id.car_valuation_price_range);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btn_back_to_previus=(Button)findViewById(R.id.btn_back_to_previus);
       // ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        priceQuotedRgrp = (RadioGroup)findViewById(R.id.priceRgrp);
        added_success=(TextView)findViewById(R.id.added_success);
        btnBack=(RelativeLayout)findViewById(R.id.btnBack);
        layout_feedback=(LinearLayout)findViewById(R.id.layout_feedback);
        submit_layout=(LinearLayout)findViewById(R.id.submit_layout);
     //   card_condition=(CardView)findViewById(R.id.card_condition);

        btn_back_to_previus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UCVFeedbackActivity.this, UsedCarValuationActivity.class);
                startActivity(intent);
                finish();

            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if(ratingBar.getRating()!=0 && ratingBar.getRating()<=2 && priceQuotedRgrp.getCheckedRadioButtonId() == -1)
                {
                    CommonUtils.showToast(UCVFeedbackActivity.this, "Please select a value", Toast.LENGTH_LONG);
                    return;
                }*/
                sendFeedbackRequest();

            }
        });

        tv_know_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogsInsuranceMessage(getResources().getString(R.string.know_more_title),getResources().getString(R.string.know_more_descripsion));
            }
        });

        Bundle extras = getIntent().getExtras();
        UsedCarDetailsModel used =(UsedCarDetailsModel)extras.getSerializable("value");
        if(used!=null){
            tv_carmodel.setText(new StringBuilder().append(used.getMake())
                    .append(" ").append(used.getVersion()).append(" ").append(used.getRegyear()).append(" Model"));

            tv_carowner.setText(new StringBuilder().append(used.getCarkm()).append(" KMs | ")
                    .append(used.getOwner()).append(" Owner | ")
                    .append(used.getBuyer()).append(" | ")
                    .append(used.getCity()));

        }
        /*GCLog.e("ashish " + used.getMake()+" "+used.getVersion()+" "+used.getRegyear());

        tv_carowner.setText(""+used.getCarkm()+"KMs|"+used.getOwner()+"|"+used.getBuyer()+"|"+used.getCity());*/

        if(getIntent().getExtras().getSerializable(Constants.USED_CAR_VALUATION_MODEL)!=null)
        {
            if(!UsedCarValuationActivity.mRadioGroup) {
                usedCarData = (UsedCarValuationModel.UsedCarData) getIntent().getExtras().getSerializable(Constants.USED_CAR_VALUATION_MODEL);
                excellent = usedCarData.getI2DExcellent().replace(",", "").trim();
                good = usedCarData.getI2DGood().replace(",", "").trim();
                fair = usedCarData.getI2DFair().replace(",", "").trim();

                //CommonUtils.insertCommaIntoNumber(holder.tvEMI, loanCase.getApprovedEmi(), usedCar, "##,##,###");

            }else {
                usedCarData = (UsedCarValuationModel.UsedCarData) getIntent().getExtras().getSerializable(Constants.USED_CAR_VALUATION_MODEL);
                excellent = usedCarData.getD2IExcellent().replace(",", "").trim();
                good = usedCarData.getD2IGood().replace(",", "").trim();
                fair = usedCarData.getD2IFair().replace(",", "").trim();
            }
            //CommonUtils.insertCommaIntoNumber(tv_excellentValue, excellent, "##,##,###");
            CommonUtils.insertCommaIntoNumber(tv_goodValue, good, "##,##,###");
            tv_price_range.setText(String.format(getString(R.string.used_car_valuation_price_range),CommonUtils.convertCommaIntoNumber(fair, "##,##,###"), CommonUtils.convertCommaIntoNumber(excellent, "##,##,###")));
            //CommonUtils.insertCommaIntoNumber(tv_fairValue, fair, "##,##,###");
            // CommonUtils.insertCommaIntoNumber((tv_excellentValue, usedCar, "##,##,###"));
            // tv_goodValue.setText(Constants.RUPEES_SYMBOL + usedCarData.getD2IGood());
            //tv_fairValue.setText(Constants.RUPEES_SYMBOL + usedCarData.getD2IFair());
        }
      /*  ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating != 0 && rating<=2)
                {
                    priceQuotedRgrp.setVisibility(View.VISIBLE);
                }
                else
                {
                    priceQuotedRgrp.setVisibility(View.GONE);
                }
            }
        });*/



    }

    public void dialogsInsuranceMessage(String st_title,String st_message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before

        //setting custom layout to dialog
        dialog.setContentView(R.layout.insurance_filter_dialogs);
        dialog.setTitle("Custom Dialog");

        //adding text dynamically
        TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
        tv_title.setText(st_title);
        TextView tv_message = (TextView) dialog.findViewById(R.id.tv_message);
        tv_message.setText(st_message);


        //adding button click event
        LinearLayout dismissLayout = (LinearLayout) dialog.findViewById(R.id.lay_dismiss);
        dismissLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_UCV_FEEDBACK);

    }

    public void sendFeedbackRequest() {
        progressDialog.setCancelable(false);
        progressDialog.show();
        HashMap<String, String> params = new HashMap<>();
        params.put("rating", "");
        params.put("dataID", usedCarData.getDataID());
        params.put(Constants.METHOD_LABEL, Constants.SEND_TRUPRICE_FEEDBACK);
        params.put("rating_comment", priceQuotedRgrp.getCheckedRadioButtonId() != -1 ? ((RadioButton) findViewById(priceQuotedRgrp.getCheckedRadioButtonId())).getText().toString() : "");
        RetrofitRequest.sendTruPriceFeedBack(params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse response, Response res) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                if ("T".equalsIgnoreCase(response.getStatus())) {
                    // CommonUtils.showToast(UCVFeedbackActivity.this, response.getMessage(), Toast.LENGTH_LONG);
                    //submit_layout.setVisibility(View.GONE);
                    //btnBack.setVisibility(View.VISIBLE);
                    layout_feedback.setVisibility(View.GONE);
                    CommonUtils.showToast(UCVFeedbackActivity.this, "Thank You for Feedback", Toast.LENGTH_LONG);
                    btnSubmit.setText("Back");
                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           finish();
                        }
                    });


                } else {
                    CommonUtils.showToast(UCVFeedbackActivity.this, response.getError(), Toast.LENGTH_LONG);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(UCVFeedbackActivity.this, getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                } else {
                    CommonUtils.showToast(UCVFeedbackActivity.this, getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                Constants.CATEGORY_USED_CAR_VALUATION,
                Constants.CATEGORY_USED_CAR_VALUATION,
                Constants.ACTION_TAP,
                Constants.LABEL_ADD_PHOTO,
                0);
    }

}
