package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
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
import com.gcloud.gaadi.events.MakeOfferEvent;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.StockItemModel;
import com.gcloud.gaadi.model.StockItemViewHolder;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.SearchCarsListActivity;
import com.gcloud.gaadi.ui.swipelistview.SwipeListView;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by ankit on 21/11/14.
 */
public class SearchedCarsListAdapter extends ArrayAdapter<StockItemModel> implements View.OnClickListener {

    private ArrayList<StockItemModel> mItems;
    private LayoutInflater mInflater;
    private Context mContext;
    private StockItemViewHolder mHolder;
//    private GAHelper mGAHelper;

    public SearchedCarsListAdapter(Context context, ArrayList<StockItemModel> items) {
        super(context, 0);
        mContext = context;
//        mGAHelper = new GAHelper(mContext);
        mItems = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ApplicationController.getEventBus().register(this);

    }

    @Override
    public int getCount() {
        if (mItems != null)
            return mItems.size();
        else
            return 0;
    }

    @Override
    public StockItemModel getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_searchedcarlist_adapter, parent, false);
            mHolder = new StockItemViewHolder();

            mHolder.viewStockListItem = (RelativeLayout) convertView.findViewById(R.id.view_stock_list_item);
            mHolder.stockImage = (ImageView) convertView.findViewById(R.id.stockImage);
            mHolder.stockPrice = (TextView) convertView.findViewById(R.id.stockPrice);
            mHolder.make = (ImageView) convertView.findViewById(R.id.makeLogo);
            mHolder.modelVersion = (TextView) convertView.findViewById(R.id.stockModelVersion);
            mHolder.moreOptions = (ImageView) convertView.findViewById(R.id.moreOptions);
            mHolder.modelYear = (TextView) convertView.findViewById(R.id.modelYear);
            mHolder.colorValue = (TextView) convertView.findViewById(R.id.colorValue);
            mHolder.color = (ImageView) convertView.findViewById(R.id.color);
            mHolder.call = (RelativeLayout) convertView.findViewById(R.id.call);
            mHolder.makeOffer = (RelativeLayout) convertView.findViewById(R.id.makeOffer);
            mHolder.fuelType = (TextView) convertView.findViewById(R.id.fuelType);
            //mHolder.stockLeads = (TextView) convertView.findViewById(R.id.stockLeads);

            mHolder.kmsDriven = (TextView) convertView.findViewById(R.id.kmsDriven);
            mHolder.trustMark = (ImageView) convertView.findViewById(R.id.trustmark);

            convertView.setTag(mHolder);


        } else {

            mHolder = (StockItemViewHolder) convertView.getTag();

        }

        ((SwipeListView) parent).recycle(convertView, position);

        if ("1".equals(mItems.get(position).getTrusmarkCertified())) {
            mHolder.trustMark.setVisibility(View.VISIBLE);
        } else {
            mHolder.trustMark.setVisibility(View.GONE);
        }


        //mHolder.viewStockListItem.setTag(position);
        if ((mItems.get(position).getImageIcon() != null) && !mItems.get(position).getImageIcon().trim().isEmpty()) {
            Glide.with(mContext)
                    .load(mItems.get(position).getImageIcon())
                    .placeholder(R.drawable.image_load_default_small)
                    .crossFade()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mHolder.stockImage);
            /*mHolder.stockImage.setDefaultImageResId(R.drawable.image_load_default_small);
            mHolder.stockImage.setErrorImageResId(R.drawable.no_image_default_small);
            mHolder.stockImage.setImageUrl(mItems.get(position).getImageIcon(), mImageLoader);*/

        } else {
            Glide.with(mContext)
                    .load(R.drawable.no_image_default_small)
                    .centerCrop()
                    .into(mHolder.stockImage);
            /*mHolder.stockImage.setDefaultImageResId(R.drawable.image_load_default_small);*/

        }
        if (mItems.get(position).getHexCode() != null && !mItems.get(position).getHexCode().equals("")) {
            GradientDrawable bgShape = (GradientDrawable) mHolder.color.getBackground();
            bgShape.setColor(Color.parseColor(mItems.get(position).getHexCode()));
        } else {
            GradientDrawable bgShape = (GradientDrawable) mHolder.color.getBackground();
            bgShape.setColor(Color.parseColor("#FFFFFF"));
        }
        mHolder.kmsDriven.setText(mItems.get(position).getKms());
        mHolder.modelYear.setText(mItems.get(position).getModelYear());
        mHolder.modelVersion.setText(mItems.get(position).getModelVersion());
        mHolder.fuelType.setText(mItems.get(position).getFuelType());
        mHolder.make.setImageResource(ApplicationController.makeLogoMap.get(mItems.get(position).getMake()));
        mHolder.moreOptions.setTag(position);
        mHolder.moreOptions.setOnClickListener(this);
        mHolder.colorValue.setText(mItems.get(position).getColor());
        mHolder.call.setTag(position);
        mHolder.call.setOnClickListener(this);
        mHolder.makeOffer.setTag(position);
        mHolder.makeOffer.setOnClickListener(this);

        //mHolder.stockLeads.setText(mItems.get(position).getTotalLeads());
        mHolder.stockPrice.setText(mItems.get(position).getStockPrice());


        return convertView;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Intent intent;

        switch (v.getId()) {
            case R.id.moreOptions:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.ACTION_TAP,
                        Constants.LABEL_THREE_DOTS,
                        0);
                ((SearchCarsListActivity) mContext).openAnimate(position);

                GCLog.e("position clicked: " + position);
                break;

            case R.id.call:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_DEALER_PLATFORM_SEARCH,
                        Constants.CATEGORY_DEALER_PLATFORM_SEARCH,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SEARCH_TAB_CALL_BUTTON,
                        0);

                logUserEvent(mItems.get(position).getStockId(), mItems.get(position).getD2dMobile(), ShareType.CALL);
                String number = mItems.get(position).getD2dMobile();

                if ((number != null) && !number.isEmpty() && !"null".equalsIgnoreCase(number)) {
                    intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:+91" + number));

                    mContext.startActivity(intent);

                }

                break;

            case R.id.makeOffer:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_DEALER_PLATFORM_SEARCH,
                        Constants.CATEGORY_DEALER_PLATFORM_SEARCH,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SEARCH_TAB_MAKE_OFFER,
                        0);

                logUserEvent(mItems.get(position).getStockId(), mItems.get(position).getD2dMobile(), ShareType.SMS);
                ApplicationController.getEventBus().post(new MakeOfferEvent(
                        mItems.get(position).getD2dMobile(),
                        mItems.get(position).getD2dEmail(),
                        CommonUtils.getStringSharedPreference(mContext, Constants.UC_DEALER_EMAIL, ""),
                        CommonUtils.getStringSharedPreference(mContext, Constants.UC_DEALER_MOBILE, "")));

                break;


        }
    }

    private void logUserEvent(String stockId, String otherDealerMobile, ShareType shareType) {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.SHARE_SOURCE, Constants.CATEGORY_DEALER_PLATFORM_SEARCH);
        params.put(Constants.MOBILE_NUM, otherDealerMobile);
        params.put(Constants.SHARE_TYPE, shareType.name());
        params.put(Constants.CAR_ID, stockId);
        params.put(Constants.METHOD_LABEL, Constants.SENT_CARS_METHOD);

        RetrofitRequest.shareCarsRequest(getContext(), params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, retrofit.client.Response response) {

            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof UnknownHostException) {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_DEALER_PLATFORM_SEARCH,
                            Constants.CATEGORY_DEALER_PLATFORM_SEARCH,
                            Constants.ACTION_TAP,
                            Constants.LABEL_NO_INTERNET,
                            0);

                } else {
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_DEALER_PLATFORM_SEARCH,
                            Constants.CATEGORY_DEALER_PLATFORM_SEARCH,
                            Constants.CATEGORY_IO_ERROR,
                            error.getMessage(),
                            0);
                }

            }
        });
/*
        ShareCarsRequest shareCarsRequest = new ShareCarsRequest(mContext,
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
                                    Constants.CATEGORY_DEALER_PLATFORM_SEARCH,
                                    Constants.CATEGORY_DEALER_PLATFORM_SEARCH,
                                    Constants.ACTION_TAP,
                                    Constants.LABEL_NO_INTERNET,
                                    0);

                        } else {
                            ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                                    Constants.CATEGORY_DEALER_PLATFORM_SEARCH,
                                    Constants.CATEGORY_DEALER_PLATFORM_SEARCH,
                                    Constants.CATEGORY_IO_ERROR,
                                    error.getMessage(),
                                    0);
                        }

                    }
                });

        ApplicationController.getInstance().addToRequestQueue(shareCarsRequest);*/

    }
}
