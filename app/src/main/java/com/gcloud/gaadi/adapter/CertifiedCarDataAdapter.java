package com.gcloud.gaadi.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.events.OpenListItemEvent;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.CertifiedCarData;
import com.gcloud.gaadi.model.CertifiedCarViewHolder;
import com.gcloud.gaadi.model.ViewCertificationCarsWarrantyInput;
import com.gcloud.gaadi.model.ViewCertifiedCarModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.rsa.RSACustomerInfoActivity;
import com.gcloud.gaadi.ui.GCProgressDialog;
import com.gcloud.gaadi.ui.IssueWarrantyFormActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

public class CertifiedCarDataAdapter extends ArrayAdapter<CertifiedCarData>
        implements View.OnClickListener, OnNoInternetConnectionListener {

    Activity mActivity;
    AlertDialog alertDialog;
    StringBuilder stringBuilder1, stringBuilder2;
    private Context mContext;
    private GCProgressDialog progressDialog;
    //    private GAHelper mGAHelper;
    private ArrayList<CertifiedCarData> mItems;
    private LayoutInflater mInflater;
    // private CertifiedCarViewHolder mHolder;


    public CertifiedCarDataAdapter(Context context, Activity activity,
                                   ArrayList<CertifiedCarData> items) {
        super(context, 0);
        mContext = context;
//        mGAHelper = new GAHelper(mContext);
        progressDialog = new GCProgressDialog(mContext, activity);
        mItems = items;
        mActivity = activity;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final CertifiedCarViewHolder mHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_cardatalistitem,
                    parent, false);
            mHolder = new CertifiedCarViewHolder();
            mHolder.make = (ImageView) convertView.findViewById(R.id.makeLogo);
            mHolder.modelYear = (TextView) convertView
                    .findViewById(R.id.modelYear);
            mHolder.modelVersion = (TextView) convertView
                    .findViewById(R.id.stockModelVersion);
            mHolder.moreOptions = (ImageView) convertView
                    .findViewById(R.id.moreOptions);
            mHolder.moreOptions.setVisibility(View.GONE);
            mHolder.kmsDriven = (TextView) convertView
                    .findViewById(R.id.kmsDriven);
            mHolder.stockPrice = (TextView) convertView
                    .findViewById(R.id.stockPrice);
            mHolder.colorValue = (TextView) convertView
                    .findViewById(R.id.colorValue);
            mHolder.color = (ImageView) convertView.findViewById(R.id.color);
            /*mHolder.certifieddate = (TextView) convertView
                    .findViewById(R.id.certifieddate);*/
            mHolder.fuel_type = (TextView) convertView.findViewById(R.id.fuelType);
            // mHolder.regnum = (TextView) convertView.findViewById(R.id.regnum);
            // mHolder.issue = (Button) convertView.findViewById(R.id.issue);
            //  mHolder.sold = (Button) convertView.findViewById(R.id.sold);
            mHolder.CertifieldImage = (ImageView) convertView.findViewById(R.id.stockImage);
            mHolder.modelYear = (TextView) convertView
                    .findViewById(R.id.modelYear);


            mHolder.fstpckgtv = (TextView) convertView
                    .findViewById(R.id.fstpckgtv);
            mHolder.scndpckgtv = (TextView) convertView
                    .findViewById(R.id.scndpckgtv);
            mHolder.thirdpckgtv = (TextView) convertView
                    .findViewById(R.id.thirdpckgtv);
            mHolder.fourthpckgtv = (TextView) convertView
                    .findViewById(R.id.fourthpckgtv);

            convertView.setTag(mHolder);

        } else {
            mHolder = (CertifiedCarViewHolder) convertView.getTag();
        }
        // ((ListView) parent).recycle(convertView, position);

        mHolder.thirdpckgtv.setVisibility(View.GONE);
        mHolder.fourthpckgtv.setVisibility(View.GONE);

/*
        mHolder.issue.setTag(position);
        mHolder.issue.setOnClickListener(this);
*/
        mHolder.moreOptions.setTag(position);
        // mHolder.moreOptions.setOnClickListener(this);
        mHolder.kmsDriven.setText(mItems.get(position).getKm());
        //mHolder.regnum.setText(mItems.get(position).getRegno());
     /*   mHolder.certifieddate.setText(mItems.get(position)
                .getCertification_date());*/
        mHolder.modelYear.setText(mItems.get(position).getMyear());
        mHolder.modelVersion.setText(mItems.get(position).getModel() + " "
                + mItems.get(position).getCarversion());
        mHolder.make.setImageResource(ApplicationController.makeLogoMap
                .get(mItems.get(position).getMake_id()));
        mHolder.colorValue.setText(mItems.get(position).getColor());
        mHolder.color.setBackgroundColor(mItems.get(position).getHexcode());
      /*  mHolder.sold.setTag(position);
        mHolder.sold.setOnClickListener(this);
      */
        mHolder.stockPrice.setText(Constants.RUPEES_SYMBOL+" "+mItems.get(position).getPricefrom());

        mHolder.fuel_type.setText(mItems.get(position).getFuel_type());
        if ((mItems.get(position).getImageIcon() != null) && !mItems.get(position).getImageIcon().trim().isEmpty()) {
            Glide.with(mContext)
                    .load(mItems.get(position).getImageIcon())
                    .placeholder(R.drawable.gcloud_placeholder)
                    .crossFade()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mHolder.CertifieldImage);
            /*mHolder.stockImage.setDefaultImageResId(R.drawable.image_load_default_small);
            mHolder.stockImage.setErrorImageResId(R.drawable.no_image_default_small);
            mHolder.stockImage.setImageUrl(mItems.get(position).getImageIcon(), mImageLoader);*/

        } else {
            Glide.with(mContext)
                    .load(R.drawable.no_image_default_small)
                    .centerCrop()
                    .into(mHolder.CertifieldImage);
            /*mHolder.stockImage.setDefaultImageResId(R.drawable.no_image_default_small);*/

        }

        ArrayList<String> recommendedPackg = mItems.get(position)
                .getRecommendedPackageNames();
        setRecommendedPackages(recommendedPackg, mHolder);
        mHolder.moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPopupMenu(mHolder.moreOptions, position);
            }
        });
        return convertView;
    }

    private void setRecommendedPackages(ArrayList<String> recommendedPackg, CertifiedCarViewHolder mHolder) {
        stringBuilder1 = new StringBuilder();
        stringBuilder2 = new StringBuilder();


            for (int i = 0; i < recommendedPackg.size(); i++) {

                if (i % 2 == 0) {
                    if (stringBuilder1.length() > 0) {
                        stringBuilder1.append("\n");
                    }
                    stringBuilder1.append(recommendedPackg.get(i).trim());
                } else {
                    if (stringBuilder2.length()>0){
                        stringBuilder2.append("\n");
                    }
                    stringBuilder2.append(recommendedPackg.get(i).trim());
                }
            }

        mHolder.fstpckgtv.setText(stringBuilder1.toString());
        mHolder.scndpckgtv.setText(stringBuilder2);


      /*  if (recommendedPackg.size() >= 1) {
            mHolder.fstpckgtv.setVisibility(View.VISIBLE);
            mHolder.fstpckgtv.setText(recommendedPackg.get(0));
        }
        if (recommendedPackg.size() >= 2) {
            mHolder.scndpckgtv.setVisibility(View.VISIBLE);
            mHolder.scndpckgtv.setText(recommendedPackg.get(1));
        }
        if (recommendedPackg.size() >= 3) {
            mHolder.thirdpckgtv.setVisibility(View.VISIBLE);
            mHolder.thirdpckgtv.setText(recommendedPackg.get(2));
        }
        if (recommendedPackg.size() >= 4) {
            mHolder.fourthpckgtv.setVisibility(View.VISIBLE);
            mHolder.fourthpckgtv.setText(recommendedPackg.get(3));
        }
*/


      /*
        if (recommendedPackg.size() == 4) {

            mHolder.fstpckg.setVisibility(View.VISIBLE);
            mHolder.fstpckgtv.setText(recommendedPackg.get(0));
            mHolder.scndpckg.setVisibility(View.VISIBLE);
            mHolder.scndpckgtv.setText(recommendedPackg.get(1));
            mHolder.thirdpckg.setVisibility(View.VISIBLE);
            mHolder.thirdpckgtv.setText(recommendedPackg.get(2));
            mHolder.fourthpckg.setVisibility(View.VISIBLE);
            mHolder.fourthpckgtv.setText(recommendedPackg.get(3));
        } else if (recommendedPackg.size() == 3) {
            mHolder.fstpckg.setVisibility(View.VISIBLE);
            mHolder.fstpckgtv.setText(recommendedPackg.get(0));
            mHolder.scndpckg.setVisibility(View.VISIBLE);
            mHolder.scndpckgtv.setText(recommendedPackg.get(1));
            mHolder.thirdpckg.setVisibility(View.VISIBLE);
            mHolder.thirdpckgtv.setText(recommendedPackg.get(2));
        } else if (recommendedPackg.size() == 2) {
            mHolder.fstpckg.setVisibility(View.VISIBLE);
            mHolder.fstpckgtv.setText(recommendedPackg.get(0));
            mHolder.scndpckg.setVisibility(View.VISIBLE);
            mHolder.scndpckgtv.setText(recommendedPackg.get(1));
        } else if (recommendedPackg.size() == 1) {
            mHolder.fstpckg.setVisibility(View.VISIBLE);
            mHolder.fstpckgtv.setText(recommendedPackg.get(0));
        }*/

    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        switch (v.getId()) {
            // case R.id.recertification:
            //
            // updateCertificationStatus(mItems.get(position).getCertificationID());
            // break;

        /*    case R.id.issue:

                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_CERTIFIED_CARS,
                        Constants.CATEGORY_CERTIFIED_CARS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_CERTIFIED_CARS_ISSUE_WARRANTY,
                        0);
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.CLOSELIST_WARRANTY));
                Intent intent = new Intent(mContext, IssueWarrantyFormActivity.class);

                Bundle args = new Bundle();

                args.putSerializable(Constants.MODEL_DATA, mItems.get(position));
                intent.putExtras(args);
                mContext.startActivity(intent);

                break;

            case R.id.sold:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_CERTIFIED_CARS,
                        Constants.CATEGORY_CERTIFIED_CARS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_CERTIFIED_CARS_SOLD_WITHOUT_WARRANTY,
                        0);
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.CLOSELIST_WARRANTY));
                showRSADialog(mItems.get(position));
                //showAlertDialog(mItems.get(position).getCertificationID());
                break;*/

            case R.id.moreOptions:
               /* ApplicationController.getEventBus().post(
                        new OpenListItemEvent(position, Constants.OPENLIST_CERTIFIED));*/
                //CertifiedCarViewHolder holder = (CertifiedCarViewHolder) v.getTag();
                //  setPopupMenu(mH.moreOptions);
                break;
        }
    }

    protected void setPopupMenu(View view, int position) {

        final int int_position = position;
        PopupMenu popupMenu = new PopupMenu(mContext, view);

        popupMenu.inflate(R.menu.menu_gaadi_warranty);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    // case R.id.recertification:
                    //
                    // updateCertificationStatus(mItems.get(position).getCertificationID());
                    // break;

                    case R.id.issue:

                        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                Constants.CATEGORY_CERTIFIED_CARS,
                                Constants.CATEGORY_CERTIFIED_CARS,
                                Constants.ACTION_TAP,
                                Constants.LABEL_CERTIFIED_CARS_ISSUE_WARRANTY,
                                0);
                        ApplicationController.getEventBus().post(new OpenListItemEvent(int_position, Constants.CLOSELIST_WARRANTY));
                        Intent intent = new Intent(mContext, IssueWarrantyFormActivity.class);

                        Bundle args = new Bundle();

                        args.putSerializable(Constants.MODEL_DATA, mItems.get(int_position));
                        intent.putExtras(args);
                        mContext.startActivity(intent);

                        return true;

                    case R.id.sold:
                        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                Constants.CATEGORY_CERTIFIED_CARS,
                                Constants.CATEGORY_CERTIFIED_CARS,
                                Constants.ACTION_TAP,
                                Constants.LABEL_CERTIFIED_CARS_SOLD_WITHOUT_WARRANTY,
                                0);
                        ApplicationController.getEventBus().post(new OpenListItemEvent(int_position, Constants.CLOSELIST_WARRANTY));
                        showRSADialog(mItems.get(int_position));
                        //showAlertDialog(mItems.get(position).getCertificationID());
                        return true;

                    case R.id.moreOptions:
               /* ApplicationController.getEventBus().post(
                        new OpenListItemEvent(position, Constants.OPENLIST_CERTIFIED));*/
                        //CertifiedCarViewHolder holder = (CertifiedCarViewHolder) v.getTag();
                        //  setPopupMenu(mH.moreOptions);
                        return true;
                }
                return false;
            }
        });

      /*  // Force icons to show
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popupMenu);
            argTypes = new Class[] { boolean.class };
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {


            popupMenu.show();
            return;
        }*/

        popupMenu.show();

    }

    private void showRSADialog(final CertifiedCarData certifiedCarData) {
        if (CommonUtils.getBooleanSharedPreference(mActivity, Constants.RSA_DEALER, false)) {
            final Dialog dialog = new Dialog(mActivity);
            dialog.setContentView(R.layout.layout_rsa_view);
            dialog.setTitle(R.string.issue_rsa);

            Button yes = (Button) dialog.findViewById(R.id.yes);
            Button no = (Button) dialog.findViewById(R.id.no);

            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    ApplicationController.getInstance().getGAHelper().sendEvent(
                            GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_WARRANTY,
                            Constants.CATEGORY_WARRANTY,
                            Constants.ACTION_TAP,
                            Constants.LABEL_ISSUE_RSA_STARTED + " - " + Constants.WARRANTY_TAB,
                            0
                    );

                    try {
                        CommonUtils.logRSAEvent(
                                certifiedCarData.getUsedCarID(),
                                Constants.WARRANTY_TAB,
                                Constants.LABEL_ISSUE_RSA_STARTED);
                    } catch (Exception e) {
                        GCLog.e("exception: " + e.getMessage());
                    }

                    Intent intent = new Intent(mActivity, RSACustomerInfoActivity.class);
                    intent.putExtra(Constants.SOURCE, Constants.WARRANTY_TAB);
                    intent.putExtra(Constants.MODEL_DATA, certifiedCarData);
                    mActivity.startActivityForResult(intent, Constants.RSA_CAR_SELECTED_REQUEST_CODE);
                }
            });

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    showAlertDialog(certifiedCarData.getCertificationID());
                }
            });

            if (!dialog.isShowing() && !mActivity.isFinishing()) {
                dialog.show();
            }
        } else {
            showAlertDialog(certifiedCarData.getCertificationID());
        }
    }

    private void soldWithoutWarranty(String id) {
        HashMap<String, String> params = new HashMap<String, String>();
        ViewCertificationCarsWarrantyInput certcarsWarrantyInput = new ViewCertificationCarsWarrantyInput();
        certcarsWarrantyInput.setApikey(Constants.API_KEY);
        certcarsWarrantyInput
                .setMethod(Constants.WARRANTY_UPDATECAR_STOCKSTATUS_METHOD);
        certcarsWarrantyInput.setOutput(Constants.API_RESPONSE_FORMAT);
        certcarsWarrantyInput.setUsername(CommonUtils.getStringSharedPreference(mContext,
                Constants.UC_DEALER_USERNAME, ""));
        certcarsWarrantyInput.setNormal_password(CommonUtils.getStringSharedPreference(mContext,
                Constants.UC_DEALER_PASSWORD, ""));

        certcarsWarrantyInput.setCertificateID(id);
        certcarsWarrantyInput.setCarStatus("2");
        certcarsWarrantyInput.setRemarks("");
        Gson gson = new Gson();
        String request_string = gson.toJson(certcarsWarrantyInput,
                ViewCertificationCarsWarrantyInput.class);

        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject(request_string);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put(Constants.EVALUATIONDATA, request_string);
        progressDialog.show();
        RetrofitRequest.viewCertifiedCarRequest(jsonObject, new Callback<ViewCertifiedCarModel>() {
            @Override
            public void success(ViewCertifiedCarModel viewCertifiedCarModel, retrofit.client.Response response) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (viewCertifiedCarModel.getStatus().equals("T")) {
                    CommonUtils.showToast(mContext, viewCertifiedCarModel.getMsg(),
                            Toast.LENGTH_SHORT);
                } else {
                    CommonUtils.showToast(mContext,
                            viewCertifiedCarModel.getError(), Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                CommonUtils.showToast(mContext, "Server Error. Please try again later.",
                        Toast.LENGTH_SHORT);
            }
        });
       /* ViewCertifiedCarRequest stocksRequest = new ViewCertifiedCarRequest(
                mContext,
                Request.Method.POST,
                Constants.getWarrantyWebServiceURL(mContext),
                params,
                new Response.Listener<ViewCertifiedCarModel>() {
                    @Override
                    public void onResponse(ViewCertifiedCarModel response) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        if (response.getStatus().equals("T")) {
                            CommonUtils.showToast(mContext, response.getMsg(),
                                    Toast.LENGTH_SHORT);
                        } else {
                            CommonUtils.showToast(mContext,
                                    response.getError(), Toast.LENGTH_SHORT);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                CommonUtils.showToast(mContext, "Server Error. Please try again later.",
                        Toast.LENGTH_SHORT);

            }
        });

        ApplicationController.getInstance().addToRequestQueue(stocksRequest,
                Constants.TAG_STOCKS_LIST, true, this);
*/
    }


    @Override
    public void onNoInternetConnection(NetworkEvent networkEvent) {
/*
        ApplicationController.getInstance().cancelPendingRequests(
                Constants.TAG_STOCK_DETAIL);*/

        NetworkEvent.NetworkError networkError = networkEvent.getNetworkError();
        if (networkError == NetworkEvent.NetworkError.NO_INTERNET_CONNECTION) {
            CommonUtils.showToast(mContext,
                    mContext.getResources().getString(R.string.network_error),
                    Toast.LENGTH_SHORT);
        }

    }

    private void showAlertDialog(final String certificationID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        alertDialog = builder
                .setTitle(R.string.alert)
                .setMessage(R.string.soldcar_without_warranty)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                soldWithoutWarranty(certificationID);
                            }
                        })
                .setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                alertDialog.dismiss();

                            }
                        }).setCancelable(false).create();
        if (!alertDialog.isShowing() && !mActivity.isFinishing()) {
            alertDialog.show();
        }

    }
}
