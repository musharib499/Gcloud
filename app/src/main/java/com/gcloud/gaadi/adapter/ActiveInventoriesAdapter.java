package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.events.EditActiveInventoryPriceEvent;
import com.gcloud.gaadi.events.OpenListItemEvent;
import com.gcloud.gaadi.events.RemoveFromD2DEvent;
import com.gcloud.gaadi.events.ShareTypeEvent;
import com.gcloud.gaadi.model.ActiveInventoriesViewHolder;
import com.gcloud.gaadi.model.InventoryModel;
import com.gcloud.gaadi.ui.swipelistview.SwipeListView;
import com.gcloud.gaadi.utils.GAHelper;

import java.util.ArrayList;

/**
 * Created by ankit on 9/1/15.
 */
public class ActiveInventoriesAdapter extends ArrayAdapter<InventoryModel> implements View.OnClickListener {

    private Context mContext;
    private ArrayList<InventoryModel> mItems;
    private LayoutInflater mInflater;
    private ActiveInventoriesViewHolder mHolder;

    public ActiveInventoriesAdapter(Context context, ArrayList<InventoryModel> items) {
        super(context, 0);
        mContext = context;
        mItems = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
            mHolder = new ActiveInventoriesViewHolder();
            mHolder.edit = (RelativeLayout) convertView.findViewById(R.id.edit);
            mHolder.make = (ImageView) convertView.findViewById(R.id.makeLogo);
            mHolder.modelYear = (TextView) convertView.findViewById(R.id.modelYear);
            mHolder.leadsLayout = (LinearLayout) convertView.findViewById(R.id.leadsLayout);
            mHolder.color = (ImageView) convertView.findViewById(R.id.color);
            mHolder.modelVersion = (TextView) convertView.findViewById(R.id.stockModelVersion);
            mHolder.moreOptions = (ImageView) convertView.findViewById(R.id.moreOptions);
            mHolder.kmsDriven = (TextView) convertView.findViewById(R.id.kmsDriven);
            mHolder.stockPrice = (TextView) convertView.findViewById(R.id.stockPrice);
            mHolder.stockImage = (ImageView) convertView.findViewById(R.id.stockImage);
            mHolder.colorValue = (TextView) convertView.findViewById(R.id.colorValue);
            mHolder.remove = (RelativeLayout) convertView.findViewById(R.id.remove);
            mHolder.whatsapp = (RelativeLayout) convertView.findViewById(R.id.whatsapp);
            mHolder.sms = (RelativeLayout) convertView.findViewById(R.id.sms);
            mHolder.modelYear = (TextView) convertView.findViewById(R.id.modelYear);
            mHolder.trustMark = (ImageView) convertView.findViewById(R.id.trustmark);
            mHolder.fuelType = (TextView) convertView.findViewById(R.id.fuelType);

            convertView.setTag(mHolder);

        } else {
            mHolder = (ActiveInventoriesViewHolder) convertView.getTag();

        }

        mHolder.leadsLayout.setVisibility(View.GONE);//Till further modification

        ((SwipeListView) parent).recycle(convertView, position);
        mHolder.kmsDriven.setText(mItems.get(position).getKmsDriven());
        if ("1".equals(mItems.get(position).getTrusmarkCertified())) {
            mHolder.trustMark.setVisibility(View.VISIBLE);
        } else {
            mHolder.trustMark.setVisibility(View.GONE);
        }

        mHolder.modelYear.setText(mItems.get(position).getYear());
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
        mHolder.edit.setTag(position);
        mHolder.edit.setOnClickListener(this);
        mHolder.whatsapp.setTag(position);
        mHolder.whatsapp.setOnClickListener(this);
        mHolder.remove.setTag(position);
        mHolder.remove.setOnClickListener(this);
        //mHolder.stockLeads.setText(mItems.get(position).getTotalLeads());
        mHolder.stockPrice.setText(Constants.RUPEES_SYMBOL + " " + mItems.get(position).getDealerPrice());
        mHolder.fuelType.setText(mItems.get(position).getFuel_type());

        return convertView;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        switch (v.getId()) {
            case R.id.edit:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.ACTION_TAP,
                        Constants.LABEL_D2D_ACTIVE_EDIT,
                        0);
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.CLOSELIST_DEALERACTIVE));
                ApplicationController.getEventBus().post(new EditActiveInventoryPriceEvent(mItems.get(position).getId(), mItems.get(position).getDealerPrice()));
                break;

            case R.id.remove:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.ACTION_TAP,
                        Constants.LABEL_REMOVE_D2D,
                        0);
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.CLOSELIST_DEALERACTIVE));
                ApplicationController.getEventBus().post(new RemoveFromD2DEvent(mItems.get(position).getId()));
                break;

            case R.id.whatsapp:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.ACTION_TAP,
                        Constants.LABEL_D2D_SEND_WHATSAPP,
                        0);
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.CLOSELIST_DEALERACTIVE));
                ApplicationController.getEventBus().post(new ShareTypeEvent(ShareType.WHATSAPP, mItems.get(position).getShareText(), mItems.get(position).getId(), Constants.OPENLIST_ACTIVE_INVENTORY));
                break;

            case R.id.sms:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.ACTION_TAP,
                        Constants.LABEL_D2D_SEND_SMS,
                        0);
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.OPENLIST_ACTIVE_INVENTORY));
                ApplicationController.getEventBus().post(new ShareTypeEvent(ShareType.SMS, mItems.get(position).getShareText(), mItems.get(position).getId(), Constants.OPENLIST_ACTIVE_INVENTORY));
                break;

            case R.id.moreOptions:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.ACTION_TAP,
                        Constants.LABEL_THREE_DOTS,
                        0);
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.OPENLIST_ACTIVE_INVENTORY));
                break;
        }
    }
}
