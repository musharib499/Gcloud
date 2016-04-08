package com.gcloud.gaadi.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.events.OpenListItemEvent;
import com.gcloud.gaadi.events.ShareTypeEvent;
import com.gcloud.gaadi.model.ActiveInventoriesViewHolder;
import com.gcloud.gaadi.model.InventoryModel;
import com.gcloud.gaadi.ui.swipelistview.SwipeListView;

import java.util.ArrayList;

/**
 * Created by ankit on 9/1/15.
 */
public class SimilarCarsAdapter extends ArrayAdapter<InventoryModel> implements View.OnClickListener {

    // private ImageLoader mImageLoader;
    String mLeadMobileNum, mLeadName;
    String carId = "";
    AlertDialog alertDialog;
    private int ottoCallBack;
    private Context mContext;
    private ArrayList<InventoryModel> mItems;
    private LayoutInflater mInflater;
    private ActiveInventoriesViewHolder mHolder;
    private boolean smsReceiverRegistered = false;
//    GAHelper mGAHelper;


    public SimilarCarsAdapter(Context context, ArrayList<InventoryModel> items, String leadMobileNum, String leadName) {
        super(context, 0);
        mContext = context;
        mItems = items;
        mLeadMobileNum = leadMobileNum;
        mLeadName = leadName;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      //  mImageLoader = ApplicationController.getInstance().getImageLoader();

    }

    public SimilarCarsAdapter(Context context, ArrayList<InventoryModel> items,
                              String leadMobileNum, String leadName, int ottoCallBack) {
        this(context, items, leadMobileNum, leadName);
        this.ottoCallBack = ottoCallBack;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public InventoryModel getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_active_inventories, parent, false);

            convertView.findViewById(R.id.leadsLayout).setVisibility(View.GONE);
            mHolder = new ActiveInventoriesViewHolder();
            mHolder.edit = (RelativeLayout) convertView.findViewById(R.id.edit);
            mHolder.make = (ImageView) convertView.findViewById(R.id.makeLogo);
            mHolder.modelYear = (TextView) convertView.findViewById(R.id.modelYear);
            mHolder.modelVersion = (TextView) convertView.findViewById(R.id.stockModelVersion);
            mHolder.moreOptions = (ImageView) convertView.findViewById(R.id.moreOptions);
            mHolder.kmsDriven = (TextView) convertView.findViewById(R.id.kmsDriven);
            mHolder.stockPrice = (TextView) convertView.findViewById(R.id.stockPrice);
            mHolder.stockImage = (ImageView) convertView.findViewById(R.id.stockImage);
            mHolder.color = (ImageView) convertView.findViewById(R.id.color);
            mHolder.colorValue = (TextView) convertView.findViewById(R.id.colorValue);
            mHolder.remove = (RelativeLayout) convertView.findViewById(R.id.remove);
            mHolder.whatsapp = (RelativeLayout) convertView.findViewById(R.id.whatsapp);
            mHolder.sms = (RelativeLayout) convertView.findViewById(R.id.sms);
            mHolder.modelYear = (TextView) convertView.findViewById(R.id.modelYear);
            mHolder.trustMark = (ImageView) convertView.findViewById(R.id.trustmark);

            convertView.setTag(mHolder);

        } else {
            mHolder = (ActiveInventoriesViewHolder) convertView.getTag();

        }
        ((SwipeListView) parent).recycle(convertView, position);
        mHolder.edit.setVisibility(View.GONE);
        mHolder.remove.setVisibility(View.GONE);
        mHolder.kmsDriven.setText(mItems.get(position).getKmsDriven());
        mHolder.modelYear.setText(mItems.get(position).getYear());

        if ("1".equals(mItems.get(position).getTrusmarkCertified())) {
            mHolder.trustMark.setVisibility(View.VISIBLE);
        } else {
            mHolder.trustMark.setVisibility(View.GONE);
        }
        if ((mItems.get(position).getImageIcon() != null) && !mItems.get(position).getImageIcon().isEmpty()) {
            Glide.with(mContext)
                    .load(mItems.get(position).getImageIcon())
                    .placeholder(R.drawable.image_load_default_small)
                    .crossFade()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mHolder.stockImage);

        } else {
            Glide.with(mContext)
                    .load(R.drawable.no_image_default_small)
                    .centerCrop()
                    .into(mHolder.stockImage);

        }
        if (mItems.get(position).getHexCode() != null && !mItems.get(position).getHexCode().equals("")) {
            GradientDrawable bgShape = (GradientDrawable) mHolder.color.getBackground();
            bgShape.setColor(Color.parseColor(mItems.get(position).getHexCode()));
        } else {
            GradientDrawable bgShape = (GradientDrawable) mHolder.color.getBackground();
            bgShape.setColor(Color.parseColor("#FFFFFF"));
        }
        mHolder.modelVersion.setText(mItems.get(position).getModelVersion());
        mHolder.make.setImageResource(ApplicationController.makeLogoMap.get(mItems.get(position).getMake()));
        mHolder.moreOptions.setTag(position);
        mHolder.moreOptions.setOnClickListener(this);
        mHolder.colorValue.setText(mItems.get(position).getColor());
        mHolder.sms.setTag(position);
        mHolder.sms.setOnClickListener(this);


        mHolder.whatsapp.setTag(position);
        mHolder.whatsapp.setOnClickListener(this);
        //mHolder.stockLeads.setText(mItems.get(position).getTotalLeads());
        mHolder.stockPrice.setText(mContext.getString(R.string.inr,
                new String[]{mItems.get(position).getPricefrom()}));

        return convertView;
    }


    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        switch (v.getId()) {
            case R.id.whatsapp:
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, ottoCallBack));
                ApplicationController.getEventBus().post(new ShareTypeEvent(
                        ShareType.WHATSAPP,
                        mItems.get(position).getShareText(),
                        mItems.get(position).getCarId(),
                        Constants.OPENLIST_SIMILAR,
                        mLeadMobileNum,
                        mLeadName));

                break;

            case R.id.sms:
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, ottoCallBack));
                ApplicationController.getEventBus().post(new ShareTypeEvent(
                        ShareType.SMS,
                        mItems.get(position).getShareText(),
                        mItems.get(position).getCarId(),
                        mLeadMobileNum,
                        mLeadName));
                // showAlertDialog(mLeadMobileNum, mItems.get(position).getShareText(), mItems.get(position).getCarId());
                break;

            case R.id.moreOptions:
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, ottoCallBack));
                break;
        }
    }
   /* private void showAlertDialog(final String number,final String shareText,final String carId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        alertDialog = builder
                .setTitle(mLeadName+"-"+mLeadMobileNum)
                .setMessage(R.string.send_sms)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                sendSMS(number, shareText, carId);
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
        Activity mActivity = (Activity)mContext;
        if (!alertDialog.isShowing() && !(mActivity.isFinishing())) {
            alertDialog.show();
        }

    }
    private void sendSMS(String number, String shareText, String carId) {
        SmsManager smsManager = SmsManager.getDefault();
        String SENT = number;
        this.carId = carId;
        PendingIntent sentPI = PendingIntent.getBroadcast(mContext, 0,
                new Intent(SENT), 0);

        //---when the SMS has been sent---
        mContext.registerReceiver(sendSMSReceiver, new IntentFilter(SENT));
        smsReceiverRegistered = true;
        smsManager.sendTextMessage(number, null, shareText, sentPI, null);
    }
    private BroadcastReceiver sendSMSReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    mGAHelper.sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_ALL_CARS,
                            Constants.CATEGORY_ALL_CARS,
                            Constants.LABEL_SMS_SENT,
                            mLeadMobileNum,
                            0);

                    makeServerCallForSharedItem();

                    CommonUtils.showToast(mContext, "SMS sent",
                            Toast.LENGTH_SHORT);
                    break;

                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    CommonUtils.showToast(mContext, "Could not send SMS.",
                            Toast.LENGTH_SHORT);
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    CommonUtils.showToast(mContext, "No service",
                            Toast.LENGTH_SHORT);
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    CommonUtils.showToast(mContext, "PDU is empty",
                            Toast.LENGTH_SHORT);
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    CommonUtils.showToast(mContext, "Radio off",
                            Toast.LENGTH_SHORT);
                    break;
            }
        }
    };
    private void makeServerCallForSharedItem() {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.CAR_ID, carId);
        params.put(Constants.MOBILE_NUM, mLeadMobileNum);

        ShareCarsRequest shareCarsRequest = new ShareCarsRequest(mContext,
                Request.Method.POST,
                Constants.getWebServiceURL(),
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
                });

        ApplicationController.getInstance().addToRequestQueue(shareCarsRequest);
    }*/
}
