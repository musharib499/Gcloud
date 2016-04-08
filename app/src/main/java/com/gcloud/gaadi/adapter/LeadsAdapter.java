package com.gcloud.gaadi.adapter;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.AdapterType;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.db.LeadsOfflineDB;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.LeadData;
import com.gcloud.gaadi.model.LeadDetailModel;
import com.gcloud.gaadi.model.LeadViewHolder;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.LeadFollowUpActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;

import org.joda.time.DateTime;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;


/**
 * Created by ankit on 21/11/14.
 */
public class LeadsAdapter extends ArrayAdapter<LeadDetailModel> implements View.OnClickListener {

    private Context mContext;
    private LayoutInflater mInflater;
    private final Uri LEADS_CALL_LOG_URI = Uri.parse(new StringBuilder().append("content://")
            .append(Constants.LEADS_CONTENT_AUTHORITY).append("/").append(LeadsOfflineDB.CALL_LOG_TABLE_NAME).toString());

    private ArrayList<LeadDetailModel> mItems;
    private LeadViewHolder mHolder;
    //    private GAHelper mGAHelper;
    private AdapterType mAdapterType;
    private boolean callInitiated;
    private Intent selectedIntent = null;
    private String currentKey = "";


    public LeadsAdapter(Context context, ArrayList<LeadDetailModel> items, AdapterType adapterType) {
        super(context, 0);
        mContext = context;
//        mGAHelper = new GAHelper(mContext);
        mItems = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAdapterType = adapterType;
        callInitiated = false;
    }


    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public LeadDetailModel getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.view_lead_list_item, parent, false);
            mHolder = new LeadViewHolder();
            mHolder.callButton = (ImageView) convertView.findViewById(R.id.callButton);
            mHolder.budget = (TextView) convertView.findViewById(R.id.budget);
            mHolder.leadDetails = (TextView) convertView.findViewById(R.id.leadDetails);
            mHolder.makeLogo = (ImageView) convertView.findViewById(R.id.makeLogo);
            mHolder.stockColor = (ImageView) convertView.findViewById(R.id.stockColor);
            mHolder.stockModelVersion = (TextView) convertView.findViewById(R.id.stockModelVersion);
            mHolder.leadDate = (TextView) convertView.findViewById(R.id.leadDate);
            mHolder.leadmakemodel = (RelativeLayout) convertView.findViewById(R.id.leadmakemodel);
            mHolder.iv_certifiedLogo = (ImageView) convertView.findViewById(R.id.iv_certifiedLogo);
            convertView.setTag(mHolder);

        } else {
            mHolder = (LeadViewHolder) convertView.getTag();

        }

        if (mAdapterType == AdapterType.CAR_LEADS) {
            mHolder.leadmakemodel.setVisibility(View.GONE);
        } else {
            mHolder.leadmakemodel.setVisibility(View.VISIBLE);
        }

        int stockMakeId = mItems.get(position).getMakeId();
        if (stockMakeId != 0) {
            mHolder.stockModelVersion.setVisibility(View.VISIBLE);
            mHolder.makeLogo.setVisibility(View.VISIBLE);
            mHolder.stockModelVersion.setText(mItems.get(position).getModel());
            mHolder.makeLogo.setImageResource(ApplicationController.makeLogoMap.get(mItems.get(position).getMakeId()));
        } else {
            mHolder.stockModelVersion.setVisibility(View.GONE);
            mHolder.makeLogo.setVisibility(View.GONE);
        }
        mHolder.callButton.setTag(position);
        mHolder.callButton.setOnClickListener(this);

        String budgetfrom = mItems.get(position).getBudgetfrom();
        String budgetto = mItems.get(position).getBudgetto();

        if ((budgetfrom != null) && !budgetfrom.isEmpty() && (budgetto != null) && !budgetto.isEmpty()) {
            if (budgetfrom.equals("0") && budgetto.equals("0")) {
                mHolder.budget.setVisibility(View.INVISIBLE);

            } else {
                mHolder.budget.setVisibility(View.VISIBLE);
                if (budgetfrom.equals("1500000")) {
                    mHolder.budget.setText("15 Lacs and above");

                } else {
                    mHolder.budget.setText(new StringBuilder().append(Constants.RUPEES_SYMBOL).append(" ")
                            .append(ApplicationController.budgetFrom.get(budgetfrom))
                            .append(" - ")
                            .append(ApplicationController.budgetTo.get(budgetto)).toString());

                }
            }

        } else {
            mHolder.budget.setVisibility(View.INVISIBLE);
        }
        //String stockModel = mItems.get(position).getModel();
        if (!mItems.get(position).getVerified().equals("") && !mItems.get(position).getVerified().equals("0")) {
            mHolder.iv_certifiedLogo.setVisibility(View.VISIBLE);


        } else {
            mHolder.iv_certifiedLogo.setVisibility(View.GONE);
        }

        String leadStatus = mItems.get(position).getLeadStatus();
        String source = mItems.get(position).getSource();

        /*mHolder.leadDetails.setText(
        mItems.get(position).getName()
                + (((leadStatus != null && !leadStatus.isEmpty())) ? (" | " + mItems.get(position).getLeadStatus()) : "")
                + (((source != null) && !source.isEmpty()) ? (" | " + key.charAt(0)+key.substring(1,key.length()).toLowerCase()) : ""));*/
        mHolder.leadDetails.setText(
                 mItems.get(position).getName()
                + (((leadStatus != null && !leadStatus.isEmpty())) ? (" | " + mItems.get(position).getLeadStatus()) : "")
                + (((source != null) && !source.isEmpty()) ? (" | " + CommonUtils.camelCase(mItems.get(position).getSource())) : ""));


        mHolder.leadDate.setText(mItems.get(position).getDateTime());

        return convertView;
    }


    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Intent intent;
        switch (v.getId()) {
            case R.id.callButton:

                String number = mItems.get(position).getNumber();
                String carId = mItems.get(position).getCarID();

                // make server call to log call made.
                logCallMade(number, carId);

                if (!callInitiated) {
                    callInitiated = true;
                    ApplicationController.getEventBus().post(this);
                    if (mAdapterType == AdapterType.CAR_LEADS) {
                        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                Constants.CATEGORY_LEADS_AGAINST_STOCK,
                                Constants.CATEGORY_LEADS_AGAINST_STOCK,
                                Constants.ACTION_TAP,
                                Constants.LABEL_CALL_BUTTON + "-" + mAdapterType.name(),
                                0);
                    } else {
                        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                Constants.CATEGORY_BUYER_LEADS,
                                Constants.CATEGORY_BUYER_LEADS,
                                Constants.ACTION_TAP,
                                Constants.LABEL_CALL_BUTTON + "-" + mAdapterType.name(),
                                0);
                    }

                    if ((number != null) && !number.isEmpty() && !"null".equalsIgnoreCase(number)) {
                        ArrayList<String> permissions = new ArrayList<>();
                        permissions.add(Manifest.permission.CALL_PHONE);
                        if (CommonUtils.getIntSharedPreference(mContext, Constants.IS_LMS, 0) == 1) {
                            permissions.add(Manifest.permission.READ_CALL_LOG);
                        }

                        intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:+91" + /**/number /*/"9560619309"/**/));

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                && !CommonUtils.checkForPermission(mContext,
                                permissions.toArray(new String[permissions.size()]),
                                Constants.REQUEST_PERMISSION_CALL, "Phone")) {
                            intent.putExtra("lead_data", new LeadData(getItem(position)));
                            selectedIntent = intent;
                            return;
                        }
                        if (CommonUtils.getIntSharedPreference(mContext, Constants.IS_LMS, 0) == 1) {
                            CommonUtils.activateLeadCallStateListener(mContext,
                                    LeadFollowUpActivity.class,
                                    "+91" + /**/number /*/"9560619309"/**/,
                                    Constants.VALUE_VIEWLEAD,
                                    Constants.NOT_YET_CALLED_FRAG_NO,
                                    new LeadData(getItem(position)));
                        }

                        callInitiated = true;
                        mContext.startActivity(intent);
                    }

                }


                break;
        }
    }


    // function to log all calls made from manage leads page.
    private void logCallMade(String number, String carId) {

        final HashMap<String, String> params = new HashMap<>();
        if (mAdapterType == AdapterType.CAR_LEADS) {
            params.put(Constants.SHARE_SOURCE, Constants.CATEGORY_LEADS_AGAINST_STOCK);
        } else {
            params.put(Constants.SHARE_SOURCE, Constants.CATEGORY_BUYER_LEADS);
        }
        params.put(Constants.SHARE_TYPE, ShareType.CALL.name());
        params.put(Constants.MOBILE_NUM, number);
        params.put(Constants.CAR_ID, carId);

        if (!ApplicationController.checkInternetConnectivity()) {
            insertInCallLog(params.get(Constants.SHARE_SOURCE),
                    params.get(Constants.SHARE_TYPE),
                    params.get(Constants.MOBILE_NUM),
                    params.get(Constants.CAR_ID));
        }
        params.put(Constants.METHOD_LABEL, Constants.SENT_CARS_METHOD);
        RetrofitRequest.shareCarsRequest(getContext(), params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, retrofit.client.Response response) {
                GCLog.e("Dipanshu Leads Adapter",params.toString());
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof UnknownHostException) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_BUYER_LEADS,
                            Constants.CATEGORY_BUYER_LEADS,
                            Constants.ACTION_TAP,
                            Constants.LABEL_NO_INTERNET,
                            0);

                } else {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_BUYER_LEADS,
                            Constants.CATEGORY_BUYER_LEADS,
                            Constants.CATEGORY_IO_ERROR,
                            error.getMessage(),
                            0);
                }

            }
        });

        /*ShareCarsRequest shareCarsRequest = new ShareCarsRequest(mContext,
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
                                    Constants.CATEGORY_BUYER_LEADS,
                                    Constants.CATEGORY_BUYER_LEADS,
                                    Constants.ACTION_TAP,
                                    Constants.LABEL_NO_INTERNET,
                                    0);

                        } else {
                            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                    Constants.CATEGORY_BUYER_LEADS,
                                    Constants.CATEGORY_BUYER_LEADS,
                                    Constants.CATEGORY_IO_ERROR,
                                    error.getMessage(),
                                    0);
                        }

                    }
                });

        ApplicationController.getInstance().addToRequestQueue(shareCarsRequest);*/

    }

    private void insertInCallLog(String shareSource, String shareType, String mobile, String carId) {
        ContentValues values = new ContentValues();
        values.put(Constants.SHARE_SOURCE, shareSource);
        values.put(Constants.SHARE_TYPE, shareType);
        values.put(Constants.MOBILE_NUM, mobile);
        values.put(Constants.CAR_ID, carId);
        values.put(LeadsOfflineDB.CALL_LOG_TIME, CommonUtils.MillisToSQLTime(DateTime.now().getMillis()));

        mContext.getContentResolver().insert(LEADS_CALL_LOG_URI, values);
    }

    public void resetCallInitiated() {
        callInitiated = false;
    }

    public Intent selectedNumberToCall() {
        return this.selectedIntent;
    }
}
