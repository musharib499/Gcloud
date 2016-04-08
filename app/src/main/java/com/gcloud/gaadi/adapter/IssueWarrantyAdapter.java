package com.gcloud.gaadi.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.model.CertifiedCarData;
import com.gcloud.gaadi.model.CertifiedCarViewHolder;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.GCProgressDialog;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by ankit on 9/1/15.
 */
public class IssueWarrantyAdapter extends ArrayAdapter<CertifiedCarData>
        implements OnClickListener {

    private Context mContext;
    private GCProgressDialog progressDialog;
    //    private GAHelper mGAHelper;
    private ArrayList<CertifiedCarData> mItems;
    private LayoutInflater mInflater;
    private CertifiedCarViewHolder mHolder;
    private String numberToCall = "";

    public IssueWarrantyAdapter(Context context, Activity activity,
                                ArrayList<CertifiedCarData> items) {
        super(context, 0);
        mContext = context;
//        mGAHelper = new GAHelper(mContext);
        progressDialog = new GCProgressDialog(mContext, activity);
        mItems = items;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public String getNumberToCall() {
        return numberToCall;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public CertifiedCarData getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.issued_warranty_layout,
                    parent, false);
            mHolder = new CertifiedCarViewHolder();
            mHolder.make = (ImageView) convertView.findViewById(R.id.makeLogo);
            mHolder.modelYear = (TextView) convertView
                    .findViewById(R.id.modelYear);
            mHolder.modelVersion = (TextView) convertView
                    .findViewById(R.id.stockModelVersion);
            mHolder.moreOptions = (ImageView) convertView
                    .findViewById(R.id.moreOptions);
            mHolder.stockImageView = (ImageView) convertView.findViewById(R.id.stockImage);

            mHolder.moreOptions.setImageResource(R.drawable.call);

            mHolder.kmsDriven = (TextView) convertView
                    .findViewById(R.id.kmsDriven);
            mHolder.stockPrice = (TextView) convertView
                    .findViewById(R.id.stockPrice);
            mHolder.colorValue = (TextView) convertView
                    .findViewById(R.id.colorValue);
            mHolder.color = (ImageView) convertView.findViewById(R.id.color);
            mHolder.issue = (Button) convertView.findViewById(R.id.issue);
            mHolder.sold = (Button) convertView.findViewById(R.id.sold);
            mHolder.customerName = (TextView) convertView.findViewById(R.id.customerName);
            mHolder.modelYear = (TextView) convertView
                    .findViewById(R.id.modelYear);
            mHolder.fuel_type = (TextView) convertView.findViewById(R.id.fuelType);
            mHolder.regNo = (TextView) convertView.findViewById(R.id.regnum);
            mHolder.tv_warrantyStartDate = (TextView) convertView.findViewById(R.id.warrantyStartDate);
            mHolder.tv_warrantyEndDate = (TextView) convertView.findViewById(R.id.warrantyEndDate);
            mHolder.tv_warrantyType = (TextView) convertView.findViewById(R.id.warrantyType);
            mHolder.tv_warrantyStatus = (TextView) convertView.findViewById(R.id.warrantyStatus);
            mHolder.tv_warrantyId = (TextView) convertView.findViewById(R.id.warrantyId);
            mHolder.callButton = (Button) convertView.findViewById(R.id.btn_callNow);


            convertView.setTag(mHolder);

        } else {
            mHolder = (CertifiedCarViewHolder) convertView.getTag();

        }

        mHolder.moreOptions.setTag(position);
        mHolder.moreOptions.setOnClickListener(this);
        mHolder.callButton.setTag(position);
        mHolder.callButton.setOnClickListener(this);

        if ((mItems.get(position).getImageIcon() != null) && !mItems.get(position).getImageIcon().trim().isEmpty()) {
            Glide.with(mContext)
                    .load(mItems.get(position).getImageIcon())
                    .placeholder(R.drawable.gcloud_placeholder)
                    .crossFade()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mHolder.stockImageView);
            /*mHolder.stockImage.setDefaultImageResId(R.drawable.image_load_default_small);
            mHolder.stockImage.setErrorImageResId(R.drawable.no_image_default_small);
            mHolder.stockImage.setImageUrl(mItems.get(position).getImageIcon(), mImageLoader);*/

        } else {
            Glide.with(mContext)
                    .load(R.drawable.no_image_default_small)
                    .centerCrop()
                    .into(mHolder.stockImageView);
            /*mHolder.stockImage.setDefaultImageResId(R.drawable.no_image_default_small);*/

        }
        mHolder.kmsDriven.setText(mItems.get(position).getKm());
        mHolder.regNo.setText(mItems.get(position).getRegno());
        mHolder.modelYear.setText(mItems.get(position).getMyear());
        mHolder.customerName.setText(CommonUtils.camelCase(mItems.get(position).getCustName()));
        mHolder.fuel_type.setText(mItems.get(position).getFuel_type());
        mHolder.modelVersion.setText(mItems.get(position).getModel() + " " + mItems.get(position).getCarversion());
        mHolder.make.setImageResource(ApplicationController.makeLogoMap.get(mItems.get(position).getMake_id()));
        mHolder.colorValue.setText(mItems.get(position).getColor());

        mHolder.color.setBackgroundColor(mItems.get(position).getHexcode());
        mHolder.stockPrice.setText(mItems.get(position).getPricefrom());
        mHolder.tv_warrantyStartDate.setText(mItems.get(position).getWarrantyStartDate());
        mHolder.tv_warrantyEndDate.setText(mItems.get(position).getWarrantyEndDate());
        mHolder.tv_warrantyType.setText(mItems.get(position).getWarrantyTypeName());
        mHolder.tv_warrantyStatus.setText(mItems.get(position).getWarrantyStatus());
        mHolder.tv_warrantyId.setText(mItems.get(position).getWarranty_id());
        return convertView;
    }


    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_callNow:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_ISSUED_WARRANTY,
                        Constants.CATEGORY_ISSUED_WARRANTY,
                        Constants.ACTION_TAP,
                        Constants.LABEL_ISSUED_WARRANTY_CALL_BUTTON,
                        0);
                String number = mItems.get(position).getCustMobile();

                logUserEvent(mItems.get(position).getUsedCarID(), mItems.get(position).getCustMobile(), ShareType.CALL);
                GCLog.e("Dipanshu","Log event Called".toString());
                if ((number != null) && !number.isEmpty() && !"null".equalsIgnoreCase(number)) {
                    if (CommonUtils.checkForPermission(mContext,
                            new String[]{android.Manifest.permission.CALL_PHONE},
                            Constants.REQUEST_PERMISSION_CALL, "Phone")) {
                        intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:+91" + number));

                        mContext.startActivity(intent);
                    } else {
                        numberToCall = number;
                    }

                }
                break;


        }
    }

    private void logUserEvent(String stockId, String customerMobile, ShareType shareType) {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.SHARE_SOURCE, Constants.CATEGORY_ISSUED_WARRANTY);
        params.put(Constants.MOBILE_NUM, customerMobile);
        params.put(Constants.SHARE_TYPE, shareType.name());
        params.put(Constants.CAR_ID, stockId);
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.METHOD_LABEL, Constants.SENT_CARS_METHOD);
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(mContext, Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.DEALER_PASSWORD, CommonUtils.getStringSharedPreference(mContext, Constants.UC_DEALER_PASSWORD, ""));

        RetrofitRequest.shareCarsRequest(getContext(), params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, retrofit.client.Response response) {

            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof UnknownHostException) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_ISSUED_WARRANTY,
                            Constants.CATEGORY_ISSUED_WARRANTY,
                            Constants.ACTION_TAP,
                            Constants.LABEL_NO_INTERNET,
                            0);

                } else {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_ISSUED_WARRANTY,
                            Constants.CATEGORY_ISSUED_WARRANTY,
                            Constants.CATEGORY_IO_ERROR,
                            error.getMessage(),
                            0);
                }
            }
        });

       /* ShareCarsRequest shareCarsRequest = new ShareCarsRequest(mContext,
                Request.Method.POST,
                Constants.getWebServiceURL(mContext),
                params,
                new Response.Listener<GeneralResponse>() {
                    @Override
                    public void onResponse(GeneralResponse response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getCause() instanceof UnknownHostException) {
                            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                    Constants.CATEGORY_ISSUED_WARRANTY,
                                    Constants.CATEGORY_ISSUED_WARRANTY,
                                    Constants.ACTION_TAP,
                                    Constants.LABEL_NO_INTERNET,
                                    0);

                        } else {
                            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                    Constants.CATEGORY_ISSUED_WARRANTY,
                                    Constants.CATEGORY_ISSUED_WARRANTY,
                                    Constants.CATEGORY_IO_ERROR,
                                    error.getMessage(),
                                    0);
                        }

                    }
                });

        ApplicationController.getInstance().addToRequestQueue(shareCarsRequest);*/
    }
}
